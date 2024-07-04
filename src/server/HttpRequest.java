package server;

import java.net.InetAddress;

public class HttpRequest {

	public static final int XML 	= 1;
	public static final int JS 		= 2;
	public static final int STRING 	= 3;
	public static final int FILE 	= 4;
	public static final int JSON 	= 5;	
	
	public int type;
	public String[] lines;
	public byte[] data;	
		
	public boolean partial;
	public int fromRange;
	public int toRange;
	public int rangeSize;
	
	public InetAddress remoteSocketAddress;
	public int remotePort;
}
