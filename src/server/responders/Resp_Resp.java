package server.responders;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import mainApl.Apl;
import server.HttpResp;
import server.HttpRespPat;


@SuppressWarnings("deprecation")
public class Resp_Resp extends Responder {
    private static final String PAT = ".*responders.*";
    private static String dir = "";

    public Resp_Resp() {
        super(new HttpRespPat(PAT));
    }

    @SuppressWarnings("unchecked")
	@Override
    public HttpResp getResp(String[] request, HttpResp response) {
    	JSONObject jsonRetObj = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        
		for (Responder r : Apl.downloadResponders) {
			 JSONObject o = new JSONObject();
			 o.put("Name: ", r.getName());
			 o.put("Pattern: ", r.cpat);
			 jsonArray.add(o);
		}
		
		for (Responder r : Apl.serviceResponders) {
			 JSONObject o = new JSONObject();
			 o.put("Name: ", r.getName());
			 o.put("Pattern: ", r.cpat);
			 jsonArray.add(o);
		}
				
		jsonRetObj.put("Responders", jsonArray); 
        response.type = HttpResp.JSON;
        response.resp = jsonRetObj.toJSONString();       
        return response;
    }

    @Override
    public String getName() {return "Responders";};    
    
    @Override
    public String getScreenInfo() { return "responders - returns supported responders information"; };
    
}
