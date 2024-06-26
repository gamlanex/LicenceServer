package server.responders;

import mainApl.Apl;
import server.HttpResp;
import server.HttpRespPat;

public class Resp_DownloadEnable extends Responder {
    private static final String PAT =  ".*download_enable.*";;

    public Resp_DownloadEnable() {
        super(new HttpRespPat(PAT));
    }

    @Override
    public HttpResp getResp(String[] request, HttpResp response) {
        response.resp = "OK - enabling download server";
        response.type = HttpResp.STRING;
        
        Apl.httpDownloadServer.disable(false);
        
        return response;
    }

    @Override
    public String getName() {return "Disable download server";};
    
    @Override
    public String getScreenInfo() { return "download_enable - enables the downalod Server thread"; };
    
}
