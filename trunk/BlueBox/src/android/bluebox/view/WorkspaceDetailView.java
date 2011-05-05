package android.bluebox.view;

import java.util.List;

import android.app.Activity;
import android.bluebox.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WorkspaceDetailView extends Activity {

	WifiManager wifi;
	
	LocationManager mlocManager = null;
	LocationListener mlocListener = null;
	
	String strLattitude = null;
	String strLongitude = null;
	
	EditText txtName;
	
	Button btnGetNetwork;
	TextView txtNetwork;
	
	Button btnGetLocation;
	TextView txtLocation;
	
	
	Button btnSave;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workspacedetail);
		
		txtName = (EditText) findViewById(R.id.wsd_edt_name);
		
		initGPS();
		btnGetLocation = (Button) findViewById(R.id.wsd_btn_location);
		btnGetLocation.setOnClickListener(getGPS);
		
		txtLocation = (TextView) findViewById(R.id.wsd_txt_location);
		
		initWifi();
		btnGetNetwork = (Button) findViewById(R.id.wsd_btn_network);
		btnGetNetwork.setOnClickListener(getWifi);
		
		txtNetwork = (TextView) findViewById(R.id.wsd_txt_network);
		
		btnSave = (Button) findViewById(R.id.wsd_btn_save);
		btnSave.setOnClickListener(saveWorkspace);
		
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
	
	private void initWifi() {
		WifiReceiver wifiReceiver = new WifiReceiver();
		registerReceiver(wifiReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}
	
//	public void startScanning() {
//		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//		wifi.startScan();
//	}
	
	class WifiReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			List<ScanResult> results = wifi.getScanResults();
			
			int n = results.size();
			txtNetwork.setText(n);
		}
		
	}
	
	private OnClickListener getWifi = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			wifi.startScan();
		}
	};
	
	private OnClickListener saveWorkspace = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if (txtName.equals("")) {
				Toast.makeText(getBaseContext(), "Enter name of workspace", Toast.LENGTH_LONG).show();
				return;
			}
			
		}
	};
}
