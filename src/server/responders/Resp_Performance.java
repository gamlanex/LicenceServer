package server.responders;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.json.simple.JSONObject;

import server.HttpReqestHandler;
import server.HttpResp;
import server.HttpRespPat;



@SuppressWarnings("deprecation")
public class Resp_Performance extends Responder {
    private static final String PAT =  ".*performance.*";

    public Resp_Performance() {
        super(new HttpRespPat(PAT));
    }

	@SuppressWarnings({ "unchecked" })
	@Override
	public HttpResp getResp(String[] request, HttpResp response) {

//        System.out.println("Dir getRequest=" + request);

		JSONObject jsonRetObj = new JSONObject();
		response.type = HttpResp.JSON;

		try {
			JSONObject o =  new JSONObject();
			o.put("concurrentRequests: ", HttpReqestHandler.concurrentRequestsCounter.getRequestNum());
	        o.put("lastResponseTime", HttpReqestHandler.concurrentRequestsCounter.getLastRequestTime());         
	        o.put("averageTime", HttpReqestHandler.concurrentRequestsCounter.getAverageRequestTime());	   
	        
	        Runtime runtime 	= Runtime.getRuntime();
	        
	        long totalMemory 	= runtime.totalMemory();
	        long freeMemory 	= runtime.freeMemory();
	        long usedMemory 	= totalMemory - freeMemory;
	        long maxMemory 		= runtime.maxMemory();	        
	        o.put("totalMemory", totalMemory);
	        o.put("freeMemory", freeMemory);
	        o.put("usedMemory", usedMemory); 
	        o.put("maxMemory", maxMemory);        

	        long p = runtime.availableProcessors();	        
	        o.put("availableProcessors", p);
	        
	        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	        int threadCount = threadMXBean.getThreadCount();
	        
	        o.put("threadsCount", threadCount);
	        
			response.resp = o.toJSONString();

		} catch (Exception e) {
			response.resp = "Interenal responder error, responder name: " + getName() + "\nException: " + e;
		}

		return response;
	}

    @Override
    public String getName() {return "Performance";};
    
    @Override
    public String getScreenInfo() { return "performance - returns the program health data"; };
}
