package server.responders;



import java.util.regex.Matcher;

import mainApl.Apl;
import server.HttpFileSystemSupprt;
import server.HttpResp;
import server.HttpRespPat;


public class Resp_Load extends Responder {
    private static final String PAT = ".*load +dir=([^ ]+).*";
    private static String dir = "";

    public Resp_Load() {
        super(new HttpRespPat(PAT));
    }

    @Override
    public HttpResp getResp(String[] request, HttpResp response) {
        response.type = HttpResp.STRING;
        
        Matcher mat = cpat.matcher(request[0]);
        
        if (mat.matches())
            dir = mat.group(1);
        
        if (checkLoad(request[0], response))
            response.resp = "OK - loading files from dir: " + dir;
        else
            response.resp = "FAIL to downlaod files from dir: " + dir;
        return response;
    }

    private boolean checkLoad(String request, HttpResp response) {
    	HttpFileSystemSupprt.parse(dir, Apl.downloadResponders);
        return true;
    }

    @Override
    public String getName() {return "Download";};    
    
    @Override
    public String getScreenInfo() { return "load dir \"directory\" - creates file responders for files in a specified directory"; };
    
}
