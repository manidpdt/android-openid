package android.bluebox.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class NetworkBox extends Activity {

	public static final String REQUESTWELCOME = "2973";
	public static final String WELCOMECONNECTING = "6593";
	public static final String REQUESTCONNECTING = "2872";
	public static final String RESPONECONNECTING = "1103";
	public static final String REQUESTPAIRCODE = "3943";
	public static final String RESPONEPAIRCODE = "5292";
	private static final String RESPONECONTENT = "1155";
	
	private static final String FILLFIELD = "FIELD";
	
	public static String PAIRCODE = "";
	
	public static final String SEPERATECHAR = ",";
	
	public static int hostPort = 7777;
	public static String hostIP = "192.168.0.100";

	public static InetAddress host;
	public static Socket socket;
	public static DataInputStream dis;
	public static DataOutputStream dos;

	public static boolean hostIsFound = false;
	public static boolean isConnecting = false;

	public static void setHostIP(String IP) {
		hostIP = IP;
	}

	public static boolean connect(String IP) {
			setHostIP(IP);
			return connect();
	}
	
	public static boolean connect() {
		
		try {
			host = InetAddress.getByName(hostIP);
			
			socket = new Socket(host, hostPort);
			
			if (!socket.isConnected())
				return false;

			socket.setSoTimeout(5000);

			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());

			isConnecting = true;
			return true;
		} catch (UnknownHostException e) {
			isConnecting = false;
			return false;
		} catch (IOException e) {
			isConnecting = false;
			return false;
		}
	}

	public static void disconect() {
		try {
			isConnecting = false;
			if (dos!= null) dos.close();
			if (dis!= null) dis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			isConnecting = false;
			e.printStackTrace();
		}
	}

	public static boolean findHost(Context context, String hostIP) {

		connect(hostIP);

		boolean b = sendMessage(REQUESTWELCOME);
		
		Toast.makeText(context, String.valueOf(b), Toast.LENGTH_SHORT).show();
		
		String message = recieveMessage();

		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

		if (message != null && message.trim().equals(WELCOMECONNECTING)) {
			hostIsFound = true;
			return true;
		}
		return false;
	}
	public static boolean connectToHost(Context context, String hostIP) {
		
		if (!connect())
			return false;
		
		sendMessage(REQUESTCONNECTING);

		String message = recieveMessage();
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		if (message != null && message.trim().equals(RESPONECONNECTING)) {
			return true;
		}
		return false;
	}

	public static boolean sendMessage(String str) {

//		if (!connect())
//			return false;
		
		try {
			dos.writeBytes(PAIRCODE + SEPERATECHAR + str + "<EOF>");
			dos.flush();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	public static String recieveMessage() {

		try {
			byte[] b = new byte[1024];
			dis.read(b);
			String s = new String (b);
			s = s.substring(PAIRCODE.length() + 1);
			return s;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean sendPairCode(String str) {
		if (!connect())
			return false;
		
		sendMessage(REQUESTPAIRCODE + SEPERATECHAR + str);
		
		String message = recieveMessage();
		
		if (message != null && message.trim().equals(RESPONEPAIRCODE)) {
			PAIRCODE = str;
//			connect();
//			Thread listenThread = new Thread(new ListenThread());
//			listenThread.start();
			return true;
		}
		
		return false;
	}
	
	public static boolean sendToHost(String str) {
		if (!connect())
			return false;
		
		return sendMessage(RESPONECONTENT + SEPERATECHAR + FILLFIELD + SEPERATECHAR + str);
	}
}
