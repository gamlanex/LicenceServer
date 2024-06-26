package server.responders;



import java.net.InetAddress;
import java.util.regex.Matcher;

import org.json.simple.JSONObject;

import mainApl.Apl;
import server.HttpResp;
import server.HttpRespPat;


@SuppressWarnings("deprecation")
public class Resp_Loopback extends Responder {
    private static final String PAT = ".*loopback +(((on) +((\\d{1,3}\\.){3}\\d{1,3}) +(\\d+) +(\\d+))|"
    		+ "off|"
    		+ "(set +buffer +(\\d+))|"
    		+ "(set +delay +(\\d+))|"
    		+ "(set +timeout +(\\d+))|"
    		+ "(set +max_avg_cnt +(\\d+))|"
    		+ "(get +stat)).*";
    
    						  	      
    /* ---     
     - on -    
    full 		(1):on 12.14.16.17 8990 4656
     			(2):on 12.14.16.17 8990 4656
          		(3):on
    udp IP		(4):12.14.16.17
    junk 		(5):16.
    port 		(6):8990
    buff size	(7):4656
        
    - off -    
    group 		(1):off
	group 		(2):null
        
     --- */
        
    
    public Resp_Loopback() {
        super(new HttpRespPat(PAT));
    }

    @SuppressWarnings({ "unchecked" })
	@Override
    public HttpResp getResp(String[] request, HttpResp response) {
        response.type = HttpResp.STRING;
        
        Matcher mat = cpat.matcher(request[0]);
        
        if (mat.matches()) {
        	
//            for (int i=1; i<14; i++)
//            	System.out.println("---group (" + i + "):" + mat.group(i));
        	                			        	
            if (mat.group(1).toLowerCase().contains("on")) {            	
            	InetAddress ipAddress 	= null;
                int port 				= 0;
                int bufferSize 			= 0;
            	                
            	try {
            		
           		 	ipAddress = InetAddress.getByName(mat.group(4));
           		 	port = Integer.parseInt(mat.group(6));
           		 	bufferSize = Integer.parseInt(mat.group(7));
           		 	
            	}
            	catch(Exception e) {
            		response.resp = "Error parsing input data!!!";
            	}
            	
                response.resp = "Staring loopback server, address: " + ipAddress + ":" + port + ", buffer size :" + bufferSize;                
                Apl.loopbackServer.startLoopback(ipAddress, port, bufferSize);
            }
            
			else if (mat.group(1).toLowerCase().contains("set")) {
				
				try {
					
					if (mat.group(1).toLowerCase().contains("buffer")) {
						int val = Integer.parseInt(mat.group(9));
						response.resp = "Setting loopback server data, Buffer Size: " + val;
						Apl.loopbackServer.setBufferSize(val);
						
					} else if (mat.group(1).toLowerCase().contains("delay")) {
						int val = Integer.parseInt(mat.group(11));
						response.resp = "Setting loopback server data, Delay: " + val;
						Apl.loopbackServer.setResponseDelay(val);
						
					} else if (mat.group(1).toLowerCase().contains("timeout")) {
						int val = Integer.parseInt(mat.group(13));
						response.resp = "Setting loopback server data, Response timeout: " + val;
						Apl.loopbackServer.setReceiveTimeout(val);
						
					} else if (mat.group(1).toLowerCase().contains("max_avg_cnt")) {
						int val = Integer.parseInt(mat.group(15));
						response.resp = "Setting loopback server data, Maximum Count For Average: " + val;
						Apl.loopbackServer.setMaxAvgCnt(val);
				   }
					
				} catch (Exception e) {
					response.resp = "Error parsing loopabask set data!!!";
				}

			}
            
			else if (mat.group(1).toLowerCase().contains("get") && mat.group(1).toLowerCase().contains("stat")) {
				
				JSONObject jsonObject = new JSONObject();
				
		        jsonObject.put("averageTime", 	Apl.loopbackServer.getAverageResponseTime());
		        jsonObject.put("seqNum", 		Apl.loopbackServer.getSeqNum());
		        jsonObject.put("wrongResp",		Apl.loopbackServer.getWrongResp());
		        jsonObject.put("timeouts",		Apl.loopbackServer.getTimouts());
		        jsonObject.put("bufferSize",	Apl.loopbackServer.getBufferSize());
		        
		        response.type = HttpResp.JSON;
		        response.resp = jsonObject.toJSONString();
		        
			}

            else if (mat.group(1).toLowerCase().contains("off")){
                response.resp = "Stopping loppback server ";                
                Apl.loopbackServer.stopLoopback();
            }
        	
        }
        
                
        return response;
    }


    @Override
    public String getName() {return "Loopback";};    
    
    @Override
    public String getScreenInfo() { return 	"loopback \"on IP PORT BUFFER_SIZE\"   (MDM loopback port is 8099, max buffer size is 60KB)\n" +
    										"\t         \"off\"\n" +     
											"\t         \"set buffer BUFFER_SIZE\"\n" +
											"\t         \"set delay DELAY_TIME\"\n" +
											"\t         \"set timeout RESPONSE WAIT TIMEOUT\"\n" +
											"\t         \"set max_avg_cnt NUMBER OF REUESTS TO CALAC. AVERAGE\"\n" +
											"\t         \"get stat\"";
    }


}
