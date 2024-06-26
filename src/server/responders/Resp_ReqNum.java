package server.responders;

import java.util.LinkedList;

import org.json.simple.JSONObject;

import server.HttpReqestHandler;
import server.HttpResp;
import server.HttpRespPat;

@SuppressWarnings("deprecation")
public class Resp_ReqNum extends Responder {
    private static final String PAT =  ".*req_num.*";;

    public Resp_ReqNum() {
        super(new HttpRespPat(PAT));
    }

    @SuppressWarnings({ "unchecked" })
	@Override
    public HttpResp getResp(String[] request, HttpResp response) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("concurrentRequests", HttpReqestHandler.concurrentRequestsCounter.getRequestNum());
        response.type = HttpResp.JSON;
        response.resp = jsonObject.toJSONString();    
        
        return response;
    }

    @Override
    public String getName() {return "Number of pending requestes";};
        
    @Override
    public String getScreenInfo() { return "req_num - returns the number of the pending requests"; };
    
}
