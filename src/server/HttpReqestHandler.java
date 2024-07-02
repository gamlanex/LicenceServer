package server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

import mainApl.Apl;
import utils.ConcurrentRequestsCounter;



public class HttpReqestHandler extends Thread {

	static final String			HEADER_OK				= "HTTP/1.1 200 OK\nAccess-Control-Allow-Origin: *";
	static final String			HEADER_FILE_OK			= "HTTP/1.1 200 OK\nAccess-Control-Allow-Origin: *";
	static final String			HEADER_NO_DATA			= "HTTP/1.1 204 No data\nAccess-Control-Allow-Origin: *";
	static final String			HEADER_ERR				= "HTTP/1.1 500 ERR\nAccess-Control-Allow-Origin: *";
	static final String			HEADER_END				= "\r\n\r\n";
	static final String			FILE_NOT_FOUND			= "404.html";
	static final String			METHOD_NOT_SUPPORTED	= "not_supported.html";
	static final String			NO_DATA_BODY			= "<html><body><h1>No data!</h1></body></html>";
	static final String			NO_MATCH				= "<html><body><h1>No match!</h1></body></html>";
	static final String			JS_START				= "<html><script>";   	
	static final String			JS_END					= "</script></html>"; 
			
	public static ConcurrentRequestsCounter concurrentRequestsCounter = new ConcurrentRequestsCounter();
		
	private Socket socket;
	private HttpResponseBuilder	responseBuilder;

	public HttpReqestHandler(HttpResponseBuilder responseBilder) {
		this.responseBuilder = responseBilder;		
	}

	private class PrintWriterWrapper extends PrintWriter {		
		public PrintWriterWrapper(OutputStream stream) throws FileNotFoundException {
			super(stream, false, StandardCharsets.UTF_8);
		}		
		
		public void println(String s) {
            super.println(s);
	    }
	}
	
	public void handleRequest(Socket socket) {
		
		this.socket = socket;
		
		concurrentRequestsCounter.increment();
		
		start();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		
		BufferedReader in = null;
		PrintWriter out = null;
		BufferedOutputStream dataOut = null;
				
		try {	
						
			in 		= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out 	= new PrintWriterWrapper(socket.getOutputStream());
			dataOut = new BufferedOutputStream(socket.getOutputStream());
			
//			String[] input = new String[10]; 					
//			String l = in.readLine();
			
			socket.setSoTimeout(10);

			LinkedList<String> inList = new LinkedList<>();					
			String l = null;
			
			do {
				l = null;
				try {
					l = in.readLine();
					inList.add(URLDecoder.decode(l));
				} catch (SocketTimeoutException e) {
					
				}
				
			} while(l != null);
			
			
			HttpRequest req = new HttpRequest();
			
			req.lines = new String[inList.size()];
			int i = 0;
			
			System.out.println("--- Got request ---");
			
			for(String s : inList) {
				req.lines[i++] = s;
				System.out.println(s);
			}			
			
								
			if (req.lines[0] == null || req.lines[0].length() < 2) {
				System.err.println("Error processing request, " + req.lines[0] == null ? "no input!" : "input length= " + req.lines[0].length());
				return;
			}
						
			
			// get pure response
			System.out.println("\n Got request from: " + socket.getRemoteSocketAddress() + " > data: " + req.lines[0]);

			req.remoteSocketAddress = socket.getInetAddress();
			req.remotePort =  socket.getPort();
			
			HttpResp resp = responseBuilder.getResponse(req.lines);
			
			// "No data" response (checkbox selected or no response found - return error 204)
			if (resp == null || (resp.resp == null && resp.data == null)) {
				System.out.println("Sending \"No DATA \" out");				
				// HTTP Header
				out.println(HEADER_OK);
				out.println("Server: Java HTTP Zabka Server : ");
				out.println("Date: " + new Date());
				out.println("Content-type: " + "text/xml");
				out.println("Content-length: " + NO_DATA_BODY.length());
				out.println(); 	// blank line between headers and content, it's a must, very important !!!				
				out.println(NO_DATA_BODY);
				out.flush();
				return;
			}
			
			// XML response
			else if (resp.type == HttpResp.XML) {
				System.out.println("Sending XML out");
				// HTTP Header out
				out.println(HEADER_OK);
				out.println("Server: Java HTTP Zabka Server : " + Apl.VERSION);
				out.println("Date: " + new Date());				
				out.println("Content-type: " + "text/xml");
				int len = resp.resp.getBytes(StandardCharsets.UTF_8).length;	
				out.println("Content-length: " + len);
				out.println(); 	// blank line between header and content, it's a must, very important !!!				
				out.println(resp.resp);
				out.flush();				
				return;
			}
			
			// XML response
			else if (resp.type == HttpResp.JSON) {
				System.out.println("Sending XML out");
				// HTTP Header out
				out.println(HEADER_OK);
				out.println("Server: Java HTTP Zabka Server : " + Apl.VERSION);
				out.println("Date: " + new Date());				
				out.println("Content-type: " + "text/json");
				int len = resp.resp.getBytes(StandardCharsets.UTF_8).length;	
				out.println("Content-length: " + len);
				out.println(); 	// blank line between header and content, it's a must, very important !!!				
				out.println(resp.resp);
				out.flush();				
				return;
			}
			
            // STRING response
			else if (resp.type == HttpResp.STRING) {
                // HTTP Header out
                out.println(HEADER_OK);
                out.println("Server: Java HTTP Zabka Server : " + Apl.VERSION);
                out.println("Date: " + new Date());
                out.println("Content-type: " + "text/text");
                //out.println("Content-length: " + resp.resp.length());
                out.println("Content-length: " + ((String) resp.resp).getBytes(StandardCharsets.UTF_8).length);
                out.println(); 	// blank line between header and content, it's a must, very important !!!
                out.println(resp.resp);
                out.flush();
                return;
            }
			
			// FILE response
			else if (resp.type == HttpResp.FILE) {
				System.out.println("Sending data file out, file: " + resp.fileName + " length: " + resp.data.length);
				// HTTP Header out
				out.println(HEADER_FILE_OK);
				out.println("Server: Java HTTP Zabka Server : " + Apl.VERSION);
				out.println("Date: " + new Date());				
				out.println("Content-Type: " + resp.typeString);	  
				out.println("Content-length: " + resp.data.length );
				out.println(); 	// blank line between header and content, it's a must, very important !!!
				out.flush();												
				dataOut.write(resp.data);											
				dataOut.flush();						
				return;
			}
						
		}
		
		catch (Exception e) {
			System.out.println("Handle request server error : " + e);
			Arrays.stream(e.getStackTrace()).forEach(element -> System.out.println(	"Method: " + element.getMethodName() + 
																					" in " + element.getClassName() + 
																					":" + element.getLineNumber()));
		}
		
		finally {
			
			concurrentRequestsCounter.decrement();
			
			try {	
				if (in != null) in.close();
			}
			catch (Exception e) {
				System.err.println("Error closing in stream: " + e);
			}
			
			try {
				if (out != null) out.close();
			}
			catch (Exception e) {
				System.err.println("Error closing out stream: " + e);
			}
			
			try {
				if (socket != null) socket.close();
			}
			catch (Exception e) {
				System.err.println("Error closing stream - socket: " + e);
			}
			
			try {
				if (dataOut != null) dataOut.close();
			}
			catch (Exception e) {
				System.err.println("Error closing stream - dataOut: " + e);
			}
						
		}
		
	}

}
