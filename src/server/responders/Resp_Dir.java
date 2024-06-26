package server.responders;

import java.util.List;
import java.util.regex.Matcher;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import server.HttpResp;
import server.HttpRespPat;
import utils.dir.DirLib;



@SuppressWarnings("deprecation")
public class Resp_Dir extends Responder {
    private static final String PAT =  ".*dir +([0-9a-zA-Z_\\./\\:]+).*";

    public Resp_Dir() {
        super(new HttpRespPat(PAT));
    }

    @SuppressWarnings({ "unchecked" })
	@Override
    public HttpResp getResp(String[] request, HttpResp response) {
    	
//        System.out.println("Dir getRequest=" + request);
        
        JSONObject jsonRetObj = new JSONObject();
        response.type = HttpResp.JSON;
        
        String dName = null;
        Matcher mat = cpat.matcher(request[0]);
                
//        System.out.println("Dir -dir request:" + request);
   
        if (mat.matches())
            dName = mat.group(1);
        
//        System.out.println("Dir -dir name:" + mat.group(1));

        if (dName == null || dName.length() < 3) {
            jsonRetObj.put("Error", "Directory not recognized"); 
            response.resp = jsonRetObj.toJSONString();            		
            return response;
        }
                       
        try {        	
        	DirLib dl = new DirLib();
            List<String> ret = dl.getDir(dName, null);
                        
            if (ret == null) 
            	jsonRetObj.put("Error", "Directory not found");            
            else {
                JSONArray jsonArray = new JSONArray();                
            	for (String name : ret) {
       			 	JSONObject o = new JSONObject();
       			 	o.put("File: ", name);
       			 	jsonArray.add(o);
            	}            	
        		jsonRetObj.put("Dir", jsonArray);
        		response.resp = jsonRetObj.toJSONString();
            }

        } catch (Exception e) {
			response.resp = "Interenal responder error, responder name: " + getName() + "\nException: " + e;
        }

        return response;
    }

    @Override
    public String getName() {return "Directory";};
    
    @Override
    public String getScreenInfo() { return "dir  \"directory\" - returns lists of files if the specified directory"; };
}
