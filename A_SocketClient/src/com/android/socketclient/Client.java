package com.android.socketclient;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Client extends Activity {

	private EditText txtMessage;
	private Button connectPhone;
	private Button sendMessage;
	private String serverIpAddress = "10.0.2.2";

	InetAddress serverAddr;
	Socket socket;
//	PrintWriter out;
	
	private boolean connected = false;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		txtMessage = (EditText) findViewById(R.id.message);
		
		connectPhone = (Button) findViewById(R.id.connect_phone);
		connectPhone.setOnClickListener(connectListener);
		
		sendMessage = (Button) findViewById(R.id.send_message);
		sendMessage.setOnClickListener(sendListener);
	}

	private OnClickListener connectListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			try {
				serverAddr = InetAddress.getByName(serverIpAddress);
				socket = new Socket(serverAddr, 5580);
				connected = true;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private OnClickListener sendListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			if (connected) {
				Thread thread = new Thread(new ClientThread());
				thread.start();
			}
		}
	};
	
	public class ClientThread implements Runnable {
		
		public void run() {
			try {
				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
				out.println(txtMessage.getText().toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected void onStop() {
		super.onStop();
		try {
			socket.close();
//			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}