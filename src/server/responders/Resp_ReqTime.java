package server.responders;

import org.json.simple.JSONObject;

import server.HttpReqestHandler;
import server.HttpResp;
import server.HttpRespPat;

@SuppressWarnings("deprecation")
public class Resp_ReqTime extends Responder {
    private static final String PAT =  ".*req_last_time.*";;

    public Resp_ReqTime() {
        super(new HttpRespPat(PAT));
    }

    @SuppressWarnings({ "unchecked" })
	@Override
    public HttpResp getResp(String[] request, HttpResp response) {
		JSONObject jsonObject = new JSONObject();
        jsonObject.put("lastResponseTime", HttpReqestHandler.concurrentRequestsCounter.getLastRequestTime());         
        response.type = HttpResp.JSON;
        response.resp = jsonObject.toJSONString();
        return response;
    }

    @Override
    public String getName() {return "Last time spent to respond request";};
    
    @Override
    public String getScreenInfo() { return "req_last_time - returns the response time for the last response"; };
     
}
