package android.bluebox.view;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.bluebox.model.NetworkBox;
import android.content.Intent;
import android.os.Bundle;

public class ListenThread extends Activity implements Runnable {

	private final String REQUESTFILL = "1234";
	
	ServerSocket serverSocket;
	
	Intent intent;

	public static DataInputStream dis;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = new Intent (ListenThread.this, MatchingListView.class);
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(NetworkBox.hostPort + 10);

			while (true) {			
				Socket socket = serverSocket.accept();
				dis = new DataInputStream(socket.getInputStream());
				String message = recieveMessage();
				
				if (message != null) {//&& message.equals(REQUESTFILL)) {
					startActivity(intent);
				}
				
			}
		} catch (IOException e) {
		}
	}

	public static String recieveMessage() {

		try {
			byte[] b = new byte[1024];
			dis.read(b);
			String s = new String (b);
			s = s.substring(NetworkBox.PAIRCODE.length() + 1);
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
	
}
