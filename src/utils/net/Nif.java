package utils.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Nif {				
	private static InetAddress prefferedInetAddress = null;
	
	public Nif() {
		try {
			prefferedInetAddress = InetAddress.getLocalHost();			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
		
	public static String check() {		
        Enumeration<NetworkInterface> nifs = null;
        String ns = "";
		
		try {
			nifs = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		
        if(nifs == null)
           return "Error getting the network interface!\n";
       
        final String GOOD_IP = "\\b\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\b";
		Pattern goodIpPattern = Pattern.compile(GOOD_IP);      
       
        for (NetworkInterface nif : Collections.list(nifs)) {
	        Enumeration<InetAddress> nifAddresses = nif.getInetAddresses();	        
	        for (InetAddress iA : Collections.list(nifAddresses)) {	        
	    	    Matcher goodIpMatcher = goodIpPattern.matcher(iA.getHostAddress());		      
	    	    if (goodIpMatcher.matches())	    	    	
	    	    	ns += "\t" + nif.getName() + "	host: " + iA.getHostName() + " - addr: "+ iA.getHostAddress() + "\n";
	        }
        }
        
		return ns;
	}
	
	public static void setPrefferedInetAddress(InetAddress inetAddress) {
		Nif.prefferedInetAddress = inetAddress;
	}

	public static InetAddress getPrefferedIPAddress() {
		try{
			if (Nif.prefferedInetAddress == null) 
				Nif.prefferedInetAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e){
			e.printStackTrace();
		}	
		return Nif.prefferedInetAddress;
	}
	
	public static InetAddress getInetAddress() {
		return Nif.prefferedInetAddress;
	}
	
	public static InetAddress getInetAddressOfNic(String nicName) {		
        Enumeration<NetworkInterface> nifs = null;
		
		try {
			nifs = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		
        if(nifs == null)
           return null;
        
        InetAddress inetAddr;
        
        for (NetworkInterface nif : Collections.list(nifs)) {
	        Enumeration<InetAddress> nifAddresses = nif.getInetAddresses();	        
	        for (InetAddress iA : Collections.list(nifAddresses)) {	        
	        	if(nif.getName().equals(nicName))
	        		return iA;
	        }
        }
        
		return null;
	}
	
	
	
}
