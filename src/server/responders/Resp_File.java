package server.responders;

import java.util.regex.Pattern;

import server.HttpFileSystemSupprt;
import server.HttpResp;
import server.HttpRespPat;

public class Resp_File extends Responder {
	
	private static final String PAT_HOST 	= ".*Host: +((\\d{1,3}\\.){3}\\d{1,3}) *(:\\d+).*";
	private static final String PAT_RANGE 	= ".*Range: *byte=(\\d+)-(\\d+)";
	
	private Pattern cpatHost = null;
	private Pattern cpatRange = null;
	
	public String fileName = "";

	public Resp_File(HttpRespPat respPat) {
		super(respPat);		
		cpatHost	= Pattern.compile(PAT_HOST);
		cpatRange 	= Pattern.compile(PAT_RANGE);
	}

	@Override
	public HttpResp getResp(String[] request, HttpResp resp) {
		int rangeFrom 	= -1;
		int rangeTo		= -1;
		
		resp.data = respPat.preloadedFileData != null ? respPat.preloadedFileData : HttpFileSystemSupprt.readFileData(respPat.respFile, rangeFrom, rangeTo);
		resp.type = HttpResp.FILE;
		resp.fileName = respPat.respFile;
		System.out.println("Sending out from responder " + getName());		
	    return resp;
	}

	@Override
	public String getName() {return "File: " + respPat.respFile;};
    
    @Override
    public String getScreenInfo() { return "FILE NAME - downloading file, returns specified file"; };
	
}
