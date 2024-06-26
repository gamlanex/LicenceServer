package server.responders;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import server.HttpResp;
import server.HttpRespPat;


public class Responder implements Cloneable {
	
    protected Pattern cpat = null;
    public HttpRespPat respPat = null;

    public Responder(HttpRespPat respPat) {
    	this.respPat = respPat;
        cpat = Pattern.compile(respPat.regPat);
    }

    public synchronized boolean checkPat(String req) {
        Matcher mat = cpat.matcher(req);
        return mat.matches();
    }

    public HttpResp getResp(String[] request, HttpResp response) {
        return null;
    }

    public String getName() { return "Not Defined"; };
    
    public String getScreenInfo() { return "responder prototype"; };
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
