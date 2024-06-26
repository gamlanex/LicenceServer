package server.responders;

import mainApl.Apl;
import server.HttpResp;
import server.HttpRespPat;

public class Resp_DownloadDisable extends Responder {
    private static final String PAT =  ".*download_disable.*";;

    public Resp_DownloadDisable() {
        super(new HttpRespPat(PAT));
    }

    @Override
    public HttpResp getResp(String[] request, HttpResp response) {
        response.resp = "OK - disabling download server";
        response.type = HttpResp.STRING;
        
        Apl.httpDownloadServer.disable(true);
        
        return response;
    }

    @Override
    public String getName() {return "Disable download server";};
    
    @Override
    public String getScreenInfo() { return "download_disable - disables the downalod Server thread, all pending requests should be served"; };
    
}
