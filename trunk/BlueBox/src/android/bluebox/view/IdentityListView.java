package android.bluebox.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluebox.R;
import android.bluebox.model.StaticBox;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class IdentityListView extends ListActivity {
	
	int numberOfIdentity = 0;
	
	ListView lvIdentity;
	
	String newIdentityName = "";
	String newIdentityValue = "";
	
	AlertDialog.Builder alert;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		loadIdentity();
		
		lvIdentity = getListView();
		lvIdentity.setTextFilterEnabled(true);
		lvIdentity.setOnItemClickListener(clickItem);
	}
	
	public void loadIdentity() {
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			FileInputStream fis = openFileInput(StaticBox.SEMANTIC_FILE);
			
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();
			
			list.add("Temp 1");
			list.add("Temp 2");
			list.add("Temp 3");
			numberOfIdentity = Integer.parseInt(properties.getProperty("n"));
			
			for (int i = 1; i <= numberOfIdentity; i++) {
				String se = properties.getProperty("s" + i);
				se = StaticBox.keyCrypto.decrypt(se);
				if (se != null) {
					list.add(se);
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setListAdapter(new ArrayAdapter<String>(this, R.layout.identitylist, list));
	}
	
	/*
	 * Create Option Menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.identitylist_optionmenu, menu);
		return true;
	}
	
	/*
	 * Create event for Option Menu
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.idl_new_id:
			
			alert = new AlertDialog.Builder(this);
			alert.setTitle("New Identity");
			alert.setMessage("Name");

			// Set an EditText view to get user Input
			final EditText edtName = new EditText(this);
			alert.setView(edtName);
			
			alert.setPositiveButton("Next", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int button) {
					// TODO Auto-generated method stub
					newIdentityName = edtName.getText().toString().trim(); 
					if (newIdentityName.length() > 0) {
						createNewIdentity(newIdentityName);
						loadIdentity();
					} else {
						Toast.makeText(getBaseContext(), "Fail create identity", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int button) {
					// TODO Auto-generated method stub
				}
			});
			alert.show();
			break;
		}	
		return true;
	}
	
	public void createNewIdentity(String newIdentityName) {
		
		String encryptedName =  StaticBox.keyCrypto.encrypt(newIdentityName);
		
		try {
			
			/*
			 * Read info of identity from StaticBox.SEMANTIC_FILE
			 */
			FileInputStream fis = openFileInput(StaticBox.SEMANTIC_FILE);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();
			
			/*
			 * Add new identity info
			 */
			int n = Integer.valueOf(properties.getProperty("n"));
			properties.setProperty("n", String.valueOf(++n));
			properties.setProperty("s" + n, encryptedName);
			
			/*
			 * Store to file again
			 */
			
			FileOutputStream fos = openFileOutput(StaticBox.SEMANTIC_FILE, Context.MODE_PRIVATE);
			properties.store(fos, null);
			fos.flush();
			fos.close();
			
			/*
			 * Create new file of identity
			 */
			fos = openFileOutput("s" + encryptedName, Context.MODE_PRIVATE);
			Properties properties2 = new Properties();
			properties2.setProperty("n", "0");
			properties.store(fos, null);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	OnItemClickListener clickItem = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			String strName = ((TextView) view).getText().toString();
			Toast.makeText(getApplicationContext(), strName, Toast.LENGTH_SHORT).show();
			
			Intent intent = new Intent(IdentityListView.this, IdentityDetailView.class);
			
			Bundle b = new Bundle();
			b.putString("id", strName);
			intent.putExtras(b);
			
			startActivity(intent);
		}
	};
}
