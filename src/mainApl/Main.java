package mainApl;

import server.HttpFileSystemSupprt;
import server.HttpPicoServer;
import utils.file.ConfigFile;
import utils.net.Nif;

public class Main {

	public static void main(String[] args) {
		
		Apl.displayHead();		
		Apl.addServiceResponders();
		
		if (!Apl.parseCommandLineAndSetValues(args))
			return;
		
//		HttpFileSystemSupprt.parse(Apl.curPath, Apl.downloadResponders);

		Apl.historyFile = new ConfigFile(Apl.historyFileName);
		Apl.httpServiceServer = new HttpPicoServer("Service", Apl.useSecureSocket, Apl.historyFileName, Nif.getPrefferedIPAddress(), Apl.servicePort,	Apl.serviceResponders);
		Apl.httpServiceServer.disable(false);
		Apl.displayMatchersInfo(Apl.serviceResponders);
		Apl.servers.add(Apl.httpServiceServer);
		
//		Apl.httpDownloadServer = new HttpPicoServer("Download", false,	Apl.curPath, Nif.getPrefferedIPAddress(), Apl.downloadPort,	Apl.downloadResponders);
//		Apl.httpDownloadServer.disable(false);
//    	Apl.displayMatchersInfo(Apl.downloadResponders);
//		Apl.servers.add(Apl.httpDownloadServer);	
		
		Apl.httpServiceServer.start();
//		Apl.httpDownloadServer.start();
		
//		Apl.loopbackServer = new LoopbackServer("Loop-back", Apl.loopbackPort);
//		Apl.loopbackServer.start();
	}

}
