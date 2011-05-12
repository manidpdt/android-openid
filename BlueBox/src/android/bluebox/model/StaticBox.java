package android.bluebox.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;

public class StaticBox extends Activity {
	public static final String PWD_MD5 = "bluebox.pwdmd5";
	public static final String PROPERTIES_FILE = "bluebox.properties";
	public static final String KEY_FILE = "bluebox.key";

	public static final String TAG_FILE = "bluebox.tag";
	public static final String SEMANTIC_FILE = "bluebox.semantic";
	public static final String WORKSPACE_FILE = "bluebox.workspace";

	public static final String SYNONYMS_FILE = "bluebox.synonyms";

	public static final String LOG_FILE = "bluebox.log";

	public static PasswordCrypto passwordCrypto;
	public static KeyCrypto keyCrypto;

	public static String currentWorkspace = null;
	public static String currentLocation = null;
	public static String currentNetwork = null;

	/*
	 * Trash value
	 */
	public static final String POINTER_FILE = "bluebox.pointer";
	public static final String DATA_FILE = "bluebox.data";
	
	public static String connectToHost(String hostIP, int hostPort) {
		String message = null;
		try {
			InetAddress host = InetAddress.getByName(hostIP);
			Socket socket = new Socket(host, hostPort);

			ObjectOutputStream oos = new ObjectOutputStream(
					socket.getOutputStream());
			oos.writeObject("IamBluebox");

			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());
			message = (String) ois.readObject();

			ois.close();
			oos.close();

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return message;
	}
}
