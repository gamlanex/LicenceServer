package loopbackServer;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import mainApl.Apl;
import utils.Pause;
import utils.TransArr;
import utils.net.Nif;



public class LoopbackServer {

	private static final int 	MAX_BUFFER_SIZE 			= 60 * 1024;
	private static final int 	DEFAULT_BUFFER_SIZE 		= 6 * 1024;  
	private static final int 	DEFAULT_RESPONSE_DELAY 		= 1000; 		// 1 sek.
	private static final int 	DEFAULT_RECEIVE_TIMEOUT 	= 10000; 		// 10 sek.
	
	private static int			ourPort						= Apl.loopbackPort;
	
	private Statistics          stat; 
	private int 				seq_Num						= 0;
	private int 				wrongResp					= 0;

	private DatagramSocket		socket					    = null;
	private Sread				sRead						= null;
	private Swrite				sWrite						= null;
	
	private InetAddress 		outAddress;	
	
	private int					outPort;		
	private String  			name						= "Not defined";
		
	private AtomicInteger		bufferSize					= new AtomicInteger(DEFAULT_BUFFER_SIZE);
	private AtomicInteger 		receiveTimeout				= new AtomicInteger(DEFAULT_RECEIVE_TIMEOUT);
	private AtomicInteger		responseDelay 				= new AtomicInteger(DEFAULT_RESPONSE_DELAY);
	private AtomicBoolean       loopbackOn					= new AtomicBoolean(false);
	
	private volatile boolean 	gotResponse					= true; 	
	
		
	public LoopbackServer(String name) {
		this(name, ourPort);
	}
	
	public LoopbackServer(String name, int ourPort) {
		this(name, ourPort, DEFAULT_RECEIVE_TIMEOUT);
	}
	
	public LoopbackServer(String name, int ourPort, int receiveTimeout) {		
		this(name, ourPort, receiveTimeout, DEFAULT_BUFFER_SIZE);
	}
	
	public LoopbackServer(String name, int ourPort, int receiveTimeout, int bufferSize) {		
		this(name, ourPort, receiveTimeout, bufferSize, DEFAULT_RESPONSE_DELAY);
	}

	public LoopbackServer(String name, int ourPort, int receiveTimeout, int bufferSize, int responseDelay) {
		
		LoopbackServer.ourPort = ourPort;
		
		this.name = name; 		
		this.receiveTimeout.set(receiveTimeout);
		this.bufferSize.set(bufferSize);		
		this.responseDelay.set(responseDelay);
		this.loopbackOn.set(false);		
		this.stat = new Statistics();		
		System.out.println("\n- Creating " + name + " server -");
	}
	
	public void setBufferSize(int bufferSize) {
		this.bufferSize.set(bufferSize);
	}

	public int getBufferSize() {
		return this.bufferSize.get();
	}
	
	public void setResponseDelay(int responseDelay) {
		this.responseDelay.set(responseDelay);
	}
	
	public void setMaxAvgCnt(int maxCnt) {
		this.stat.setMaxCnt(maxCnt);
	}
	
	public void setReceiveTimeout(int receiveTimeout) {
		this.receiveTimeout.set(receiveTimeout);
	}
	
	public long getAverageResponseTime() {
		return stat.getLastRespTime();
	}
	
	public int getSeqNum() {
		return seq_Num;
	}

	public int getTimouts() {
		return stat.timeoutResp;
	}
	
	public int getWrongResp() {
		return wrongResp;
	}
	
	public void start() {		
		if (sWrite != null || sRead != null) {
			stop();
			return;
		}

		try {
			socket = new DatagramSocket(null);
			socket.bind(new InetSocketAddress(Nif.getPrefferedIPAddress(), ourPort));
		}		
		catch (BindException e) {
			System.out.println("Socket alredy in use, Loopback server disabled");
			return;
		}
		catch (SocketException e) {
			System.out.println("Excetion, Loopback server disabled, exception: " + e);
		}

		this.stat.timeoutResp = 0;
		
		System.err.println("- Starting server: " + name + ", address: " + socket.getLocalSocketAddress().toString().substring(1));
		
		sRead = new Sread();
		sRead.start();		
		sWrite = new Swrite();
		sWrite.start();		
		gotResponse = true;
	}
	
	public void stop() {	
		
		loopbackOn.set(false);
		
		if (sWrite != null) {
			sWrite.cancel();
			sWrite = null;
		}		
		if (sRead != null) {
			sRead.cancel();
			sRead = null;
		}		
		if (socket != null) {
			socket.close();
			socket.disconnect();
		}		
	}

	public void stopLoopback() {
		loopbackOn.set(false);
	}
	
	public void startLoopback(InetAddress outAddress, int port, int bufferSize) {
		this.outPort 	= port;
		this.outAddress = outAddress;
		this.bufferSize.set(bufferSize);
		
		this.seq_Num 	= 0;
		this.wrongResp	= 0;
		System.out.println("=== Starting loopback ");
		loopbackOn.set(true);
	}

	private void writeLoopback() {		
		int bufferSize = this.bufferSize.get();
		
		DatagramPacket pkt = null;
		byte[] buf = new byte[bufferSize];

		try {
			TransArr req = new TransArr();
			
			// encode request
			req.add_4(this.seq_Num);
			req.add_4(this.responseDelay.get());
			req.add_4(bufferSize);			
			byte[] buff = new byte[bufferSize];			
			// this should have different wariants of filling buffer
			for (int i=0; i<bufferSize; i++)
				 buff[i] = (byte) i;			
			req.add(buf);
			
			// set outgoing packet
			pkt = new DatagramPacket(buf, buf.length);
			pkt.setAddress(outAddress);
			pkt.setPort(outPort);
			pkt.setData(req.getBytes());
					
			System.out.println("=== Loopback packet out, address: " +  pkt.getAddress().getHostAddress() + ":" + outPort + " size: " + String.format("%8d", bufferSize) + " SEQ NUM: " + seq_Num);			
			socket.send(pkt);
			
			stat.setLastReqTime(System.currentTimeMillis(), bufferSize);
			
			this.seq_Num++;
		}
		catch (SocketException e) {
			System.out.println("Loppback socket closed");
		}
		catch(IOException e) {
			System.out.println("Escp socket IOException: " + e.toString() );
        }
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error while handling IN terminal request!");
		}
	}
	
	private void readLoopback() {		
		int receiveTimeout = this.receiveTimeout.get();
		byte[] buf = new byte[MAX_BUFFER_SIZE];
		
		DatagramPacket pkt = new DatagramPacket(buf, buf.length);

		try {
			socket.setSoTimeout(receiveTimeout);
			socket.receive(pkt);			
			gotResponse = true;
			
			InetAddress tAdr = pkt.getAddress();
			int tPort = pkt.getPort();
			
			// decode response
			TransArr resp = new TransArr();
			resp.add(pkt.getData());
			int respSeqNum 		= (int) resp.read_4();
			int respSleepTime 	= (int) resp.read_4();
			int bytesOfData 	= (int) resp.read_4();
			
			stat.setLastRespTime(System.currentTimeMillis(), bytesOfData);
			
			System.out.println("=== Loopback packet in,  address: " + tAdr.getHostAddress() + ":" + tPort + " size: " + String.format("%8d", bytesOfData) + " SEQ NUM: " + respSeqNum);

		} catch (SocketTimeoutException e) {
			stat.timeoutResp++;
		}
		catch (SocketException e) {
			System.out.println("Loppback socket closed");
		}
		catch(IOException e) {
			System.out.println("In Loopback socket IOException: " + e.toString() );
        }
		catch (Exception e) {
			System.out.println("Error while handling Loopback In");
		}
	}

	private class Swrite extends Thread {
		public volatile boolean run;

		public Swrite() {
			super("LoopbackServerOut");
			run = true;
		}

		public void run() {
									
			while (run) {		
				
				if (!loopbackOn.get()) {
					Pause.p(1000);
					continue;
				}
				
				if (!gotResponse) {
					Pause.p(100);
					continue;					
				}
			
				try {							
					writeLoopback();														
					gotResponse = false;
					Pause.p(responseDelay.get());
				}
				catch (InternalError e) {
					System.out.println("Internal error in Loopback Out Server: " + e);
				}
				catch (Exception e) {
					System.out.println("Loopback Out Server Interrupted Exc. 2");
				}
				
			}
		}

		public void cancel() {
			run = false;
		}

	}

	private class Sread extends Thread {
		public volatile boolean run;

		public Sread() {
			super("LoopbackServerIn");
			run = true;
		}

		public void run() {
									
			while (run) {
							
				try {		
					readLoopback();  
				}
				catch (InternalError e) {
					System.out.println("Internal error in Loopback In Server: " + e);
				}
				catch (Exception e) {
					System.out.println("Loopback In Server Interrupted Exc. 2");
				}
				
			}
		}

		public void cancel() {
			run = false;
		}

	}
	
	private class Statistics {
		
		private int maxCnt = 10;
		private int cnt = 0;
		private long timeSum = 0L;
		
		public long lastReqT;
		public long lastReqSeq;
		
		public long lastRespT;
		public long lastRespSeq;
		
		public long lastRespTime;
		public long avgRespTime;
		
		public int timeoutResp	= 0;
		
		public void setLastReqTime(long time, long seq) {
			this.lastReqT = time;
			this.lastReqSeq = seq;
		}
		
		public void setMaxCnt(int maxCnt) {
			this.maxCnt = maxCnt;
		}
		
		public void setLastRespTime(long time, long seq) {			
			this.lastRespSeq = seq;
			if (seq != lastReqSeq) {
				wrongResp++;
				return;							
			}
			this.avgRespTime = this.lastRespTime = time - lastReqT;
			if (cnt < maxCnt) cnt++;			
			avgRespTime = (avgRespTime*(cnt-1) + lastRespTime) / cnt;			
		}
		
		public long getLastRespTime() { return avgRespTime; }		
	}
	
	
}

