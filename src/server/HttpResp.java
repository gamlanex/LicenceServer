package server;

public class HttpResp {
	public static final int XML 	= 1;
	public static final int JS 		= 2;
	public static final int STRING 	= 3;
	public static final int FILE 	= 4;
	public static final int JSON 	= 5;	
	
	public int type;
	public String typeString;
	public String fileName;		
	public String resp;
	public byte[] data;	
}
