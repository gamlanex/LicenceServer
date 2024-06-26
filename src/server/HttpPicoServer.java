package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import mainApl.Apl;
import server.responders.Responder;
import utils.Pause;


public class HttpPicoServer extends Thread {
	
	private String 					name = "";
	private HttpResponseBuilder		responderBuilder;
	private ServerSocket			serverSocket;
	private volatile boolean		stop;
	private volatile boolean		disabled = false;

	public HttpPicoServer(String name, boolean secureConnection, String historyFileName, InetAddress inetAddress, int port, LinkedList<Responder> responders) {
		
		this.name  = name;		
		
		responderBuilder = new HttpResponseBuilder(null, responders);
		
		System.out.println("\n- Creating " + name + " server -");

		try {						
			
			serverSocket = createServerSocket(inetAddress, port, secureConnection);
			
			if (serverSocket == null) {
				System.err.println("Error: Socket creation error!");
				return;
			}			
			
//			System.out.println("- Binding " + name + "\tserver socket to: " + inetAddress + " Port: " + port);			
//			serverSocket.bind(new InetSocketAddress(inetAddress, port));
			
		}
		catch (IOException e) {
			System.err.println("Error: Not able to start " + name + " Server: " + e.getMessage());
			System.out.println("\n- Terminating -");
			return;
		}
		catch (SecurityException e) {
			System.err.println("Error: Not able to bind socket: " + e.getMessage());
			System.out.println("\n- Terminating -");
			return;
		}
		catch (IllegalArgumentException e) {
			System.err.println("Error: Not able to bind socket: " + e.getMessage());
			System.out.println("\n- Terminating -");
			return;
		}
		
	}

	@Override
	public void run() {		
		
		try {
			
			System.err.println("- Starting server: " + name);			
			
			stop = false;
			
			while (!stop) {
				
				if (disabled) {
					Pause.p(300);
					continue;
				}
								
				System.out.println("- Server: " + name + "\twaiting for connection, concurrent requests: " + HttpReqestHandler.concurrentRequestsCounter.getRequestNum());
				
				(new HttpReqestHandler(responderBuilder)).handleRequest(serverSocket.accept());				
			}
			
		}
		catch (IOException e) {
			System.err.println("Error: Server " + name + "Connection error : " + e.getMessage());
		}	
		finally {
			
			System.err.println("- Stopping " + name + " server (pending requests will be finished)");	
			
			try {
				if (serverSocket != null) {
					System.err.println("- Closing " + name + " server socket");
					serverSocket.close();
				}
			}
			catch (IOException e) {
				System.err.println("Error closing server socket");
			}
			
		}
		
	}
	
	public void stopServer() {
		stop = true;
	}

	public void disable(boolean b) {
		disabled = b;
	}
	
	 private ServerSocket createServerSocket(InetAddress inetAddress, int serverPort, boolean useSecureSocket) throws IOException {
		 		 
		 	System.out.println("\tCreating socket, address: " + inetAddress.getHostAddress() + " port " + serverPort + " secure: " + useSecureSocket);
		 
	        if (useSecureSocket) {

//			 	// --------------------  testing  ------------------------------
//			 	Apl.keystorePath 		= "Koncept.p12"; //"keystore.pfx";
//			 	Apl.keystorePassword 	= "pass1234";
//			 	Apl.truststorePath 		= "Koncept.p12"; //"keystore.pfx";
//			 	Apl.truststorePassword 	= "pass1234";
//			 	//---------------------------------------------------------------
	        	
	        	System.setProperty("javax.net.ssl.keyStoreType", 		"PKCS12");
				System.setProperty("javax.net.ssl.keyStore", 			Apl.keystorePath);
				System.setProperty("javax.net.ssl.keyStorePassword", 	Apl.keystorePassword);
				
				System.setProperty("javax.net.ssl.trustStoreType", 		"PKCS12");
	            System.setProperty("javax.net.ssl.trustStore", 			Apl.truststorePath);
	            System.setProperty("javax.net.ssl.trustStorePassword", 	Apl.truststorePassword);

	            try {
					SSLContext sslContext = SSLContext.getInstance(Apl.desiredProtocol);
				} catch (NoSuchAlgorithmException e) {
					System.err.println("Error, can not set desired SSL protocol: " + Apl.desiredProtocol + "\nException: " + e);
					return null;
				}
				
				InetAddress serverAddress = InetAddress.getByName(inetAddress.getHostAddress());				
		        SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		        SSLServerSocket socket = null;
		        
		        try {
		        	socket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(serverPort, 0, serverAddress);
		        	System.out.println("\tConnected to " + inetAddress.getHostAddress() + " port " + serverPort);
		        }
		        catch(IOException | SecurityException | IllegalArgumentException e) {
		        	System.err.println("Error creating secure socket: " + e);
	        		System.out.println("- Exit -");		        	
		        	System.exit(1);
		        }
	            	            	            
	            return socket;	            
	        } 
	        else {
	        	ServerSocket socket = null;
	        	
	        	try {
	        		socket = new ServerSocket();
	        		socket.bind(new InetSocketAddress(inetAddress, serverPort));
	        	}
	        	catch(IOException | SecurityException | IllegalArgumentException e) {
	        		System.err.println("Error creating plain socket: " + e);
	        		System.out.println("- Exit -");
	        		System.exit(1);
	        	}
	            
	            return socket;
	        }
	 }
	 
	
}
