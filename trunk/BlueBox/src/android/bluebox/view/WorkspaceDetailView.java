package android.bluebox.view;

import android.app.Activity;
import android.bluebox.R;
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

public class WorkspaceDetailView extends Activity {

	LocationManager mlocManager = null;
	LocationListener mlocListener = null;
	
	String strLattitude = null;
	String strLongitude = null;
	
	Button btnGetLocation;
	TextView edtLocation;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workspacedetail);
		
		btnGetLocation = (Button) findViewById(R.id.wsd_btn_location);
		btnGetLocation.setOnClickListener(getLocation);
		getGPS();
		
		edtLocation = (TextView) findViewById(R.id.wsd_txt_location);
	}
	
	public void getGPS() {
		
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
	
	private OnClickListener getLocation = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
			edtLocation.setText("Lattitude = " + strLattitude + "\n"
					+ "Longitude = " + strLongitude);
		}
	};
	
	private void getNetwork() {
		
	}
}
