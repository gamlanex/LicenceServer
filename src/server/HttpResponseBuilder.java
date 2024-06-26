package server;

import java.util.LinkedList;

import server.responders.Responder;

public class HttpResponseBuilder {

    public LinkedList<Responder> responders = new LinkedList<>();
    
    public HttpResponseBuilder(String filesPath, LinkedList<Responder> responders) {
    	this.responders = responders;    	
    }
    
    public HttpResp getResponse(String[] req) {    	
        System.out.println("MY REQ: " + req[0]);        
        HttpResp response = new HttpResp();

        for (Responder r : responders)
            if (r.checkPat(req[0])) {       
            	
            	System.out.println( "--- OK - Match for responder: " + r.getName());
            	
            	Responder responder = getResponderInstance(r);
            	
            	if (responder != null)
            		response = responder.getResp(req, response);
            	else {
            		response.type = HttpResp.STRING;
            		response.resp = "Error creating responder";
            	}    
       	
                return response;
            }
//            else 
//            	System.out.println( "--- NO - match for responder: " + r.getName());            
        
        return null;
    }

    private Responder getResponderInstance(Responder r) {
        try { return (Responder) r.clone(); } catch (Exception e) { System.out.println( "Error creating responder: " + r.getName()); }        
        return null;
    }

}
