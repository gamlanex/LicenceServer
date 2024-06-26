package mainApl;

import java.util.LinkedList;

import loopbackServer.LoopbackServer;
import server.HttpPicoServer;
import server.responders.Resp_Dir;
import server.responders.Resp_DownloadDisable;
import server.responders.Resp_DownloadEnable;
import server.responders.Resp_Licence;
import server.responders.Resp_Load;
import server.responders.Resp_Loopback;
import server.responders.Resp_Memory;
import server.responders.Resp_Performance;
import server.responders.Resp_ReqAvgTime;
import server.responders.Resp_ReqNum;
import server.responders.Resp_ReqTime;
import server.responders.Resp_Resp;
import server.responders.Resp_Stop;
import server.responders.Resp_Version;
import server.responders.Responder;
import utils.Os;
import utils.dir.DirLib;
import utils.file.ConfigFile;
import utils.net.Nif;



public class Apl {
	
	public static final String  				VERSION 				= "1.1";
	
	public static byte[] 						secret 					= new byte[] { 0x37, 0x12, 0x7F, 0x54, 0x01 };
	
	public static String						fileNameToPreload 		= "RSP";
	public static String						curPath;
	public static String						historyFileName;	
	
	public static int 							servicePort 			= 8085;
	public static int 							downloadPort 			= servicePort + 1;
	public static int 							loopbackPort 			= servicePort + 2;	
	
	public static String 						desiredProtocol 		= "TLSv1.3";
	
	public static HttpPicoServer				httpServiceServer		= null;
	public static HttpPicoServer				httpDownloadServer		= null;
	public static LoopbackServer				loopbackServer			= null;	
	public static LinkedList<Responder> 		serviceResponders		= new LinkedList<>();
	public static LinkedList<Responder> 		downloadResponders		= new LinkedList<>();		
	public static LinkedList<HttpPicoServer>	servers					= new LinkedList<>();
	
	// Socket creation fields
	public static String 						keystorePath  			= null;
	public static String 						keystorePassword		= null;
	public static String 						truststorePath          = null;
	public static String 						truststorePassword      = null;
	public static boolean 						useSecureSocket			= false;
	
	public static int 							maxNumberOfTerminals	= 0;
	public static ConfigFile  					historyFile				= null;

	
	public static void addServiceResponders() {
		Apl.serviceResponders.add(new Resp_Version());
		Apl.serviceResponders.add(new Resp_Licence());
		Apl.serviceResponders.add(new Resp_Dir());
		Apl.serviceResponders.add(new Resp_ReqNum());
		Apl.serviceResponders.add(new Resp_Stop());
//		Apl.serviceResponders.add(new Resp_Load());
		Apl.serviceResponders.add(new Resp_ReqTime());
		Apl.serviceResponders.add(new Resp_ReqAvgTime());
		Apl.serviceResponders.add(new Resp_Memory());
		Apl.serviceResponders.add(new Resp_Resp());
//		Apl.serviceResponders.add(new Resp_DownloadDisable());
//		Apl.serviceResponders.add(new Resp_DownloadEnable());
//		Apl.serviceResponders.add(new Resp_Performance());
//		Apl.serviceResponders.add(new Resp_Loopback());
	}
	
	public static boolean parseCommandLineAndSetValues(String[] args) {		
		boolean retVal = true;
		int i = 0;
		String arg;
		
		curPath = DirLib.getCurrentPath();
		System.out.println("Current path=" + curPath);
		

		while (i < args.length) {
			
			arg = args[i++].toLowerCase();
			
			if (arg.startsWith("-"))
		        arg = arg.substring(1);
						
			if (arg.equals("?") || arg.equals("h")) {
				displayHelpInformation();
				return false;
			}
			
			else if (arg.equals("n") || arg.equals("net")) {
				if (i < args.length)
					Nif.setPrefferedInetAddress(Nif.getInetAddressOfNic(args[i++]));
				else 
					System.err.println("Error: - n requires a network interface name");
			}
			
			else if (arg.equals("keystore_path")) {
				if (i < args.length)
					keystorePath = args[i++];
				else 
					System.err.println("Error: - keystore_path requires a keystore name");
			}
			
			else if (arg.equals("keystore_password")) {
				if (i < args.length)
					keystorePassword = args[i++];
				else 
					System.err.println("Error: - keystore_password requires a keystore paswword");
			}
			
			else if (arg.equals("truststore_path")) {
				if (i < args.length)
					truststorePath = args[i++];
				else 
					System.err.println("Error: - truststore_path requires a truststore name");
			}
			
			else if (arg.equals("truststore_password")) {
				if (i < args.length)
					truststorePassword = args[i++];
				else 
					System.err.println("Error: - truststore_password requires a truststore paswword");
			}
									
			else if (arg.equals("p_s") || arg.equals("service_port")) {
				if (i < args.length)
					try {
						servicePort	= Integer.parseInt(args[i++]);	
					}
					catch(Exception e) {
						System.err.println("Error: can not parse service port number!");	
					}				
			}
			
//			else if (arg.equals("p_d") || arg.equals("download_port")) {
//				if (i < args.length)
//					try {
//						downloadPort = Integer.parseInt(args[i++]);	
//					}
//					catch(Exception e) {
//						System.err.println("Error: can not parse download port number!");	
//					}				
//			}
//
//			else if (arg.equals("p_l") || arg.equals("loopback_port")) {
//				if (i < args.length)
//					try {
//						loopbackPort = Integer.parseInt(args[i++]);	
//					}
//					catch(Exception e) {
//						System.err.println("Error: can not parse loopback port number!");	
//					}				
//			}

			else if (arg.equals("n_t") || arg.equals("number_terminals")) {
				if (i < args.length)
					try {
						Apl.maxNumberOfTerminals = Integer.parseInt(args[i++]);	
						
					}
					catch(Exception e) {
						System.err.println("Error: can not parse download port number!");	
					}				
			}
						
			else if (arg.equals("path_fs")) {
				if (i < args.length)
					try {
						curPath = args[i++];
						System.out.println("Curr path from command line:" + curPath);	
					}
					catch(Exception e) {
						System.err.println("Error: can not parse file path!");	
					}				
			}

			else if (arg.equals("path_h")) {
				if (i < args.length)
					try {
						historyFileName = args[i++];
						System.out.println("History file name from command line: " + historyFileName);	
					}
					catch(Exception e) {
						System.err.println("Error: can not parse history file path!");	
					}				
			}
			
			
			else if (arg.equals("preload")) {
				if (i < args.length)
					try {
						fileNameToPreload = args[i++];
					}
					catch(Exception e) {
						System.err.println("Error: can not parsethe preload file name");	
					}				
			}

			
		}
		
		// Curr path correction
		if (curPath.charAt(curPath.length() - 1) != '/' && curPath.charAt(curPath.length() - 1) != '\\')
			curPath += Os.getOsPathSeparator();

		useSecureSocket = keystorePassword != null && keystorePath != null && truststorePassword != null && truststorePath != null;
		return retVal;
	}
		
	public static void displayHead() {
		System.out.println("");
		System.out.println("  +----------------------------------------------+");
		System.out.println("  |                                              |");
		System.out.println("  |         MDM Licence Server ver. " + Apl.VERSION + "          |");
		System.out.println("  |                                              |");
		System.out.println("  +----------------------------------------------+\n");		
		
		Apl.displayNetworkInformation();
	}
	
	public static void displayNetworkInformation() {
		System.out.println("Available network intefaces:\n" +  Nif.check());
	}
	
	public static void displayHelpInformation() {
		System.out.println("Command line options: ");
		System.out.println("\t-service_port PORT NUMBER 	- TCP port number the server wil be listening on (the default is: 8085)" );
		System.out.println("\t-download_port PORT NUMBER 	- TCP port number the download server wil be listening on (the default is: 8086)" );
		System.out.println("\t-loopback_port PORT NUMBER 	- UDP port number for loopback (the default is: 8087)" );		
		System.out.println("\t-path PATH                	- the full path where response files are located (the default is server current directory)" );
		System.out.println("\t-n NETWORK INTERFACE NAME 	- the name of the network interface name which will be used (listed below)" );
		System.out.println("\t-preload NAME             	- the name of the files to be stored for fast respone (the default is \"MDM_\")" );
		System.out.println("\t-keystore_path NAME       	- the name of the keystrore file" );
		System.out.println("\t-keystore_password PASS   	- the password for the keystrore file" );
		System.out.println("\t-truststore_path NAME     	- the name of the truststore file" );
		System.out.println("\t-trusstore_password PASS  	- the password for the truststore file" );
		System.out.println("");
		
		System.out.println("Supported commands: ");
		
		for (Responder r : downloadResponders) 
			System.out.println("\t" + r.getScreenInfo() );	
		
		for (Responder r : serviceResponders) 
			System.out.println("\t" + r.getScreenInfo() );		
	}
	
	public static void displayMatchersInfo(LinkedList<Responder> responders) {
		String s = "\n";
		
		for (Responder r : responders) {  
//			if (r.respPat.respFile != null)
//				s += "\n";
			s += "\t" + r.getScreenInfo();
			s += "  (regExp:\t" 	+ r.respPat.regPat + ")\n";
//			s += r.respPat.respFile != null ? "\tFile:\t" + r.respPat.respFile : "";
//			if (r.respPat.respFile != null && r.respPat.preloadedFileData != null)
//				s += " (preloaded)";
		}
		
		System.out.println("\n- Installed request matchers: " + s + "\n");
	}
			
}
