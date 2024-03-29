package android.bluebox.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluebox.R;
import android.bluebox.model.NetworkBox;
import android.bluebox.model.StaticBox;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WorkspaceDetailView extends Activity {

	static int NEW_WORKSPACE = 0;
	static int EDIT_WORKSPACE = 1;
	
	Context thisContext;

	String workspaceName = "null";

	LocationManager mlocManager = null;
	LocationListener mlocListener = null;

	String strLattitude = null;
	String strLongitude = null;

	String hostIP = "";

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
		thisContext = this.getBaseContext();

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

		if (!workspaceName.equals("null")) {

			try {
				FileInputStream fis = openFileInput("w" + workspaceName);
				Properties properties = new Properties();
				properties.load(fis);
				fis.close();

				edtName.setText(StaticBox.keyCrypto.decrypt(workspaceName));
				txtNetwork.setText(StaticBox.keyCrypto.decrypt(properties.getProperty("network")));
				hostIP = txtNetwork.getText().toString();
				
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
			mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
					0, mlocListener);
			txtLocation.setText("Lattitude = " + strLattitude + "\n"
					+ "Longitude = " + strLongitude);
		}
	};

	private OnClickListener getServer = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			findServer();
		}
	};

	private OnClickListener saveWorkspace = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (edtName.getText().toString().equals("")) {
				Toast.makeText(getBaseContext(), "Enter name of workspace",
						Toast.LENGTH_SHORT).show();
				return;
			}
			String newWorkspaceName = StaticBox.keyCrypto.encrypt(edtName
					.getText().toString());

			/*
			 * Neu tao workspace moi ma da co roi thi bao loi va ngung lai
			 */
			if (workspaceName.equals("null")) {
				try {
					FileInputStream fis = openFileInput("w" + newWorkspaceName.trim());
					if (fis != null)
						fis.close();
					Toast.makeText(getBaseContext(),
							"This workspace name existed", Toast.LENGTH_SHORT)
							.show();
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
				
				if (workspaceName.equals("null")) {
					
					properties.setProperty("n", String.valueOf(++n));
					properties.setProperty("w" + n, newWorkspaceName);

				} else {
					for (int i = 1; i <= n; i++) {
						String s = properties.getProperty("w" + i);
						if (s != null && s.trim().equals(workspaceName)) {
							deleteFile("w" + workspaceName);
							properties.setProperty("w" + i, newWorkspaceName);
							break;
						}
					}
				}
				
				properties.store(fos, null);

				fos.flush();
				fos.close();
				
				fos = openFileOutput("w" + newWorkspaceName.trim(), Context.MODE_PRIVATE);

				properties = new Properties();
				properties.setProperty("header", StaticBox.keyCrypto
						.getMD5Key());
				properties.setProperty("network", StaticBox.keyCrypto
						.encrypt(hostIP));
				properties.setProperty("gps", StaticBox.keyCrypto
						.encrypt(strLattitude + ":" + strLongitude));
				properties.store(fos, null);

				fos.flush();
				fos.close();

				Toast.makeText(getBaseContext(), "Workspace Saved",
						Toast.LENGTH_LONG).show();
				finish();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				Toast.makeText(getBaseContext(),
						"Can not create file " + newWorkspaceName,
						Toast.LENGTH_LONG).show();
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

	public void findServer() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Find server");
		alert.setMessage("Server IP");

		// Set an EditText view to get user Input
		final EditText edtName = new EditText(this);
		edtName.setText(NetworkBox.hostIP);
		alert.setView(edtName);

		alert.setPositiveButton("Find", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int button) {
				// TODO Auto-generated method stub
				String IP = edtName.getText().toString().trim();
				if (IP.length() > 0) {
					findingServer(IP);
				} else {
					Toast.makeText(getBaseContext(), "Please input server IP",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int button) {
				// TODO Auto-generated method stub
			}
		});
		alert.show();
	}
	
	public void findingServer(String IP) {
		hostIP = IP;
		new FindingServer().execute();
	}

	public class FindingServer extends AsyncTask<Void, Void, Void> {

		private final ProgressDialog dialog = new ProgressDialog(WorkspaceDetailView.this);
		boolean foundHost = false;
		
		protected void onPreExecute() {
			this.dialog.setTitle("Searching server");
			this.dialog.setMessage("Please wait...");
			this.dialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			foundHost = NetworkBox.findHost(hostIP);
			
			return null;
		}
		
		protected void onProgressUpdate(final Void unused) {
		}
		
		protected void onPostExecute(final Void unused) {
			if (this.dialog.isShowing())
				this.dialog.dismiss();
			
			if (foundHost) {
				txtNetwork.setText(hostIP);
				Toast.makeText(thisContext, "Server found",
						Toast.LENGTH_SHORT).show();		
			} else {
				Toast.makeText(thisContext, "Server not found",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	
}
