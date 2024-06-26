package server.responders;

import org.json.simple.JSONObject;

import mainApl.Apl;
import server.HttpResp;
import server.HttpRespPat;

@SuppressWarnings("deprecation")
public class Resp_Version extends Responder {
    private static final String PAT =  ".*version.*";;

    public Resp_Version() {
        super(new HttpRespPat(PAT));
    }

    @SuppressWarnings({ "unchecked" })
	@Override
    public HttpResp getResp(String[] request, HttpResp response) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("version", Apl.VERSION);
        response.type = HttpResp.JSON;
        response.resp = jsonObject.toJSONString();     
        return response;
    }

    @Override
    public String getName() {return "Version";};
    
    @Override
    public String getScreenInfo() { return "version - returns the version number"; };
    
}
