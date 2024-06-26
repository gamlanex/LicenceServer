package server.responders;

import mainApl.Apl;
import server.HttpResp;
import server.HttpRespPat;

public class Resp_Stop extends Responder {
    private static final String PAT =  ".*stop.*";;

    public Resp_Stop() {
        super(new HttpRespPat(PAT));
    }

    @Override
    public HttpResp getResp(String[] request, HttpResp response) {
        response.resp = "OK - stopping server";
        response.type = HttpResp.STRING;
        
        Apl.httpServiceServer.stopServer();
        
        return response;
    }

    @Override
    public String getName() {return "Stop Server";};
    
    @Override
    public String getScreenInfo() { return "stop - stops the Server thread, all pending request should be served"; };
    
}
