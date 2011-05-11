package android.bluebox.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import android.app.Activity;
import android.bluebox.R;
import android.bluebox.model.StaticBox;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WorkspaceDetailView extends Activity {

	static int NEW_WORKSPACE = 0;
	static int EDIT_WORKSPACE = 1;
	
	String workspaceName = "null";

	LocationManager mlocManager = null;
	LocationListener mlocListener = null;

	String strLattitude = null;
	String strLongitude = null;

	String hostIP = "10.0.2.2";
	int hostPort = 7777;

	EditText edtName;

	Button btnGetNetwork;
	TextView txtNetwork;

	Button btnGetLocation;
	TextView txtLocation;


	Button btnSave;
	Button btnCancel;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workspacedetail);

		/*
		 * Check if we create a new workspace or edit one
		 */
		
		edtName = (EditText) findViewById(R.id.wsd_edt_name);
		edtName.setText("");

		initGPS();
		btnGetLocation = (Button) findViewById(R.id.wsd_btn_location);
		btnGetLocation.setOnClickListener(getGPS);

		txtLocation = (TextView) findViewById(R.id.wsd_txt_location);

		btnGetNetwork = (Button) findViewById(R.id.wsd_btn_network);
		btnGetNetwork.setOnClickListener(getServer);

		txtNetwork = (TextView) findViewById(R.id.wsd_txt_network);

		btnSave = (Button) findViewById(R.id.wsd_btn_save);
		btnSave.setOnClickListener(saveWorkspace);

		btnCancel = (Button) findViewById(R.id.wsd_btn_canel);
		btnCancel.setOnClickListener(cancel);
		
		/*
		 * Load data if editing
		 */
		workspaceName = this.getIntent().getExtras().getString("WorkspaceName");
		
		if (workspaceName != "null") {
			
			try {
				FileInputStream fis = openFileInput("w" + workspaceName);
				Properties properties = new Properties();
				properties.load(fis);
				fis.close();
				
				edtName.setText(StaticBox.keyCrypto.decrypt(workspaceName));
				txtNetwork.setText(StaticBox.keyCrypto.decrypt(properties.getProperty("network")));
				txtLocation.setText(StaticBox.keyCrypto.decrypt(properties.getProperty("gps")));
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void initGPS() {

		mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mlocListener = new LocationListener() {

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLocationChanged(Location loc) {
				// TODO Auto-generated method stub
				strLattitude = String.valueOf(loc.getLatitude());
				strLongitude = String.valueOf(loc.getLongitude());
			}
		};
	}

	private OnClickListener getGPS = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
			txtLocation.setText("Lattitude = " + strLattitude + "\n"
					+ "Longitude = " + strLongitude);
		}
	};

	private OnClickListener getServer = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			try {
				InetAddress host = InetAddress.getByName(hostIP);
				Socket socket = new Socket(host, hostPort);

				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				oos.writeObject("IamBluebox");

				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				String message = (String) ois.readObject();

				txtNetwork.setText(hostIP + ":" + hostPort);
				Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();

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
		}
	};

	private OnClickListener saveWorkspace = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (edtName.equals("")) {
				Toast.makeText(getBaseContext(), "Enter name of workspace", Toast.LENGTH_LONG).show();
				return;
			}
			String newWorkspaceName = StaticBox.keyCrypto.encrypt(edtName.getText().toString());
			
			/*
			 * Neu tao workspace moi ma da co roi thi bao loi va ngung lai
			 */
			if (workspaceName.equals("null")) {
				try {
					FileInputStream fis = openFileInput("w" + newWorkspaceName);
					if (fis != null) fis.close();
					Toast.makeText(getBaseContext(), "This workspace name existed", Toast.LENGTH_SHORT).show();
					return;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			/*
			 * Tao file moi
			 */
			try {
				FileInputStream fis = openFileInput(StaticBox.WORKSPACE_FILE);
				Properties properties = new Properties();
				properties.load(fis);
				fis.close();

				FileOutputStream fos = openFileOutput(StaticBox.WORKSPACE_FILE, Context.MODE_PRIVATE);

				int n = Integer.valueOf(properties.getProperty("n"));
				properties.setProperty("n", String.valueOf(++n));
				properties.setProperty("w" + n, newWorkspaceName);
				properties.store(fos, null);

				fos.flush();
				fos.close();

				fos = openFileOutput("w" + newWorkspaceName.trim(), Context.MODE_PRIVATE);
				
				properties = new Properties();
				properties.setProperty("header", StaticBox.keyCrypto.getMD5Key());
				properties.setProperty("network", StaticBox.keyCrypto.encrypt(hostIP + ":" + hostPort));
				properties.setProperty("gps", StaticBox.keyCrypto.encrypt(strLattitude + ":" + strLongitude));
				properties.store(fos, null);

				fos.flush();
				fos.close();

				Toast.makeText(getBaseContext(), "Workspace Saved", Toast.LENGTH_LONG).show();
				finish();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				Toast.makeText(getBaseContext(), "Can not create file " + newWorkspaceName, Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private OnClickListener cancel = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			finish();
		}
	};
}
