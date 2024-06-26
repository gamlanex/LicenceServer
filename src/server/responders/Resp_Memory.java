package server.responders;

import org.json.simple.JSONObject;

import server.HttpResp;
import server.HttpRespPat;

@SuppressWarnings("deprecation")
public class Resp_Memory extends Responder {
    private static final String PAT =  ".*memory.*";;

    public Resp_Memory() {
        super(new HttpRespPat(PAT));
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public HttpResp getResp(String[] request, HttpResp response) {
    	
        Runtime runtime 	= Runtime.getRuntime();
        
        long totalMemory 	= runtime.totalMemory();
        long freeMemory 	= runtime.freeMemory();
        long usedMemory 	= totalMemory - freeMemory;
        long maxMemory 		= runtime.maxMemory();
        
//        System.out.println("Total Memory: " + totalMemory);
//        System.out.println("Free Memory: " + freeMemory);
//        System.out.println("Used Memory: " + usedMemory);
//        System.out.println("Max Memory: " + maxMemory);
        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalMemory", totalMemory);
        jsonObject.put("freeMemory", freeMemory);
        jsonObject.put("usedMemory", usedMemory); 
        jsonObject.put("maxMemory", maxMemory);        
        response.type = HttpResp.JSON;
        response.resp = jsonObject.toJSONString();       
        return response;
    }

    @Override
    public String getName() {return "Memory Usage";};
        
    @Override
    public String getScreenInfo() { return "memory - returns JVM memory usage information"; };
    
}
