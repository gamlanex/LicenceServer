package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

import mainApl.Apl;
import server.responders.Resp_File;
import server.responders.Responder;
import utils.dir.DirLib;


public class HttpFileSystemSupprt {	
	private static final int MAX_DIR_NEXT_LEVEL = 5;	
	private	static String[][] httpFileTypes = {
			// extension, HTTP type
			
			// Image
			{ "gif",   			"image/gif"}, 
			{ "jpeg",   		"image/jpeg"},
			{ "png",   			"image/png"},
			{ "tiff",   		"image/tiff"},
			{ "icon",   		"image/vnd.microsoft.icon"},
			{ "x-icon",   		"image/x-icon"},
			{ "djvu",   		"image/vnd.djvu"},
			{ "svg+xml",   		"image/svg+xml"},
			
			// Multipart	                                                                         
			{ "mixed",   		"multipart/mixed"},
			{ "alternative",	"multipart/alternative"},
			{ "related",   		"multipart/related"}, 		// using by MHTML (HTML mail)                            
			{ "form-data",  	"multipart/form-data"},
			
			// Text	                                                                       
			{ "css",   			"text/css"},
			{ "csv",   			"text/csv"},
			{ "html",   		"text/html"},
			{ "javascript",   	"text/javascript"}, 		// obsolete                                              
			{ "plain",   		"text/plain"},
			{ "xml",   			"text/xml"},
			
			// Video	                                                                            
			{ "mpeg",  			"video/mpeg"},
			{ "mp4",   			"video/mp4"},
			{ "quicktime",  	"video/quicktime"},
			{ "x-ms-wmv",   	"video/x-ms-wmv"},
			{ "x-msvideo",  	"video/x-msvideo"},
			{ "x-flv",   		"video/x-flv"},
			{ "webm",   		"video/webm"},
			
			// VND	                                                                                 
			{ "text",   		"application/vnd.oasis.opendocument.text"},
			{ "spreadsheet",	"application/vnd.oasis.opendocument.spreadsheet"},
			{ "presentation",	"application/vnd.oasis.opendocument.presentation"},
			{ "graphics",   	"application/vnd.oasis.opendocument.graphics"},
			{ "ms-excel",   	"application/vnd.ms-excel"},
			{ "sheet",   		"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
			{ "ms-powerpoint",  "application/vnd.ms-powerpoint"},
			{ "presentation",   "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
			{ "msword",   		"application/msword"},
			{ "document",   	"application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
			{ "xul+xml",   		"application/vnd.mozilla.xul+xml"}
	};     

		
	public static void parse(String path, LinkedList<Responder> responders) {
//		LinkedList<String> ls = new LinkedList<String>();
				
		if (Apl.httpDownloadServer != null) 
			Apl.httpDownloadServer.disable(true);
		
//		for (Responder r : responders)
//			if (r instanceof Resp_File)
//				responders.remove(r);
						
//		for (String s : (new DirLib(MAX_DIR_NEXT_LEVEL)).getDir(path, ls)) {
//			String sMat = getMatchingPattern(s);					
//			if (sMat != null) {
//				HttpRespPat rp = new HttpRespPat(sMat, s, HttpResp.FILE, getFileType(s));
//				if (sMat.contains(Apl.fileNameToPreload))
//					rp.preloadedFileData = readFileData(s, -1, -1);	
//				responders.add(new Resp_File(rp));
//			}
//		}
//		
//		if (Apl.httpDownloadServer != null) 
//			Apl.httpDownloadServer.disable(false);
	}
		
//	private static String getMatchingPattern(String fName) {	
//		
//		fName = fName.replaceAll("\\\\", "/");
//		fName = fName.replace(".", ".");
//		fName = fName.replace("(", ".");
//		fName = fName.replace(")", ".");
//		fName = fName.replace("-", ".");
//				
//		int i = fName.lastIndexOf("/");				
//		if (i <= 0)
//			return null;				
//		fName = fName.substring(i+1);
//		
//		String ret = ".*" + fName + ".*";
//		
//		return ret;
//	}
	
//	private static String getRespFileNameWithPath(String fName) {
//		return fName;
//	}

	public static String getFileType(String fName) {				
		int i = fName.lastIndexOf('.');		// get file name extension
		
		if (i <= 0)
			return null;
		
		String fileExt = fName.substring(i+1);
		
		// find the file type to put in the HTTP header
		for (i=0; i< httpFileTypes.length; i++)
			if (httpFileTypes[i][0].equals(fileExt)) 
				return httpFileTypes[i][1];
		
		return null;
	}
	
//	public static String readFileText(HttpResp resp, String fName) {
//		if (fName == null || fName.equals(""))
//			return null;		
//		String line = null;
//		String respString = new String();
//		File f = new File(fName);
//		try {
//			BufferedReader input = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8));			
//			while ((line = input.readLine()) != null)		
//				respString += line;			
//			input.close();
//		}
//		catch (IOException e1) {
//			System.out.println("File not found: " + fName);
//			return null;
//		}		
//		return respString;
//	}
		
	public static byte[] readFileData(String fName, int rangeFrom, int rangeTo) {				
		File f = new File(fName);
		int fileLength = (int) f.length();		
		byte[] fileData = new byte[fileLength];
		
		try {			
			if (fileLength == 0) {
				System.out.println("Error reading download file - file length is 0");
				return null;
			}
			
			System.out.println("Sending out download file: "+ fName +", length: " + fileLength);
			
			FileInputStream fileIn = new FileInputStream(f);
			fileIn.read(fileData);
			fileIn.close();
		}
		catch (IOException e) {
			System.out.println("Error reading download file: " + e);
			return null;
		}
		
		return fileData;
	}

}
