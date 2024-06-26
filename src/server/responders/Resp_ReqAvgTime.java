package server.responders;

import org.json.simple.JSONObject;

import server.HttpReqestHandler;
import server.HttpResp;
import server.HttpRespPat;

@SuppressWarnings("deprecation")
public class Resp_ReqAvgTime extends Responder {
    private static final String PAT =  ".*req_avg_time.*";;

    public Resp_ReqAvgTime() {
        super(new HttpRespPat(PAT));
    }

    @SuppressWarnings({ "unchecked" })
	@Override
    public HttpResp getResp(String[] request, HttpResp response) {
		JSONObject jsonObject = new JSONObject();
        jsonObject.put("averageTime", HttpReqestHandler.concurrentRequestsCounter.getAverageRequestTime());
        response.type = HttpResp.JSON;
        response.resp = jsonObject.toJSONString();                
        return response;
    }

    @Override
    public String getName() {return "Average time for last 10 requests";};
        
    @Override
    public String getScreenInfo() { return "req_avg_time - returns the avarage response time for the last 10 responses"; };    
}
