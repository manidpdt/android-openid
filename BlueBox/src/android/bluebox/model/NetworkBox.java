package android.bluebox.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;

public class NetworkBox extends Activity {

	public static final String REQUESTWELCOME = "2973";
	public static final String WELCOMECONNECTING = "6593";
	public static final String REQUESTCONNECTING = "2872";
	public static final String RESPONECONNECTING = "1103";

	public static int hostPort = 7777;
	
	public static InetAddress host;
	public static Socket socket;
	public static DataInputStream dis;
	public static DataOutputStream dos;
	
	public static boolean hostIsFound = false;
	public static boolean isConnecting = false;

	public static boolean findHost(String hostIP) {
		
//		String message = null;
		hostIsFound = false;
		try {
			
			host = InetAddress.getByName(hostIP);
			socket = new Socket(host, hostPort);

			socket.setSoTimeout(5000);
			
			dos = new DataOutputStream(
					socket.getOutputStream());
			dos.writeBytes(REQUESTWELCOME + "<EOF>");

			dis = new DataInputStream(
					socket.getInputStream());
			String message = dis.readLine();
			
			if (message.equals(WELCOMECONNECTING)) {
				hostIsFound = true;
				return true;
			}
		} catch (UnknownHostException e) {
//			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public static boolean connectToHost(String hostIP) {
		if (!hostIsFound)
			findHost(hostIP);
		
		if (!hostIsFound) return false;
		
		String message = null;
		
		try {
			sendMessage(REQUESTCONNECTING + "<EOF>");
			message = recieveMessage();

			if (message.equals(RESPONECONNECTING)) {
				return true;
			}

			dis.close();
			dos.close();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
	
	public static boolean sendMessage(String str) {
		if (!isConnecting) return false;
		
		try {
			dos.writeBytes(str);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static String recieveMessage() {
		if (!isConnecting) return null;
		
		try {
			byte[] b = new byte[1024];
			dis.read(b);
			
			return new String(b);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
}
