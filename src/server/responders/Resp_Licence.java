package server.responders;

import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.zip.CRC32;

import org.json.simple.JSONObject;

import mainApl.Apl;
import server.HttpResp;
import server.HttpRespPat;
import utils.ByteArray;
import utils.DateTime;
import utils.dir.JSon;

@SuppressWarnings("deprecation")
public class Resp_Licence extends Responder {
	private static final String PAT = ".*totalTerminalCount.*";

	public Resp_Licence() {
		super(new HttpRespPat(PAT));
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public HttpResp getResp(String[] request, HttpResp response) {
		String resp = "OK";
		response.type = HttpResp.STRING;

		Matcher mat = cpat.matcher(request[0]);

		if (mat.matches()) {
			// Save request to file 
			Apl.historyFile.writeFile("\n--- " + DateTime.getFormattedDateTime() + " --------------------------------------");
			String s = JSon.getJsonOnly(request[0]);
			Apl.historyFile.writeFile("REQ: " + s);
			
			// --- CRC ---			
			byte[] combined = ByteArray.sum(s.getBytes(), Apl.secret);
			CRC32 crc = new CRC32();
			crc.update(combined);
			long checksum = crc.getValue();
			
			// Response
			JSONObject json = new JSONObject();
			json.put("approved", resp);
			json.put("maxTerminals", Apl.maxNumberOfTerminals);
			json.put("crc", String.format("%08X", checksum));

			response.type = HttpResp.JSON;
			response.resp = json.toJSONString();
			
			Apl.historyFile.writeFile("RSP: " + response.resp);	
		}

		return response;
	}

	@Override
	public String getName() {
		return "Licence";
	};

	@Override
	public String getScreenInfo() {
		return "licence for: " + Apl.maxNumberOfTerminals + " terminals";
	}
	
	
}
