package server;

import java.util.regex.Pattern;

public class HttpRespPat {
	
	public String	regPat;						// RegExp to find proper answer for the GET request
	public String	respFile;					// file name for file containing the answer
	public Pattern	pat;   						// space for compiled RegExp (to recompile just once)
	public int      type;				   	    // type of the answer: HTML, XML, FILE 
	public String   typeString; 				// string with a type string to put into the HTTP header
	public byte[]   preloadedFileData; 			// cash file data, not to red it ale over again

	public HttpRespPat(String regPat, String respFile, String typeString) {
		this(regPat, respFile, HttpResp.XML, typeString);
	}
	
	public HttpRespPat(String regPat, String respFile) {
		this(regPat, respFile, HttpResp.XML, "");
	}
	
	public HttpRespPat(String regPat) {
		this(regPat, null, HttpResp.XML, "");
	}

	public HttpRespPat(String regPat, String respFile, int type, String typeString) {			
		this.regPat 	= regPat;
		this.respFile 	= respFile;
		this.type 		= type;
		this.typeString = typeString;		
		this.pat 		= Pattern.compile(regPat);			
	}
}

