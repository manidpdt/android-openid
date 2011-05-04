/*
 * 
 */

package android.bluebox.view;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import android.app.Activity;
import android.bluebox.R;
import android.bluebox.model.StaticValue;
import android.bluebox.model.WorkspaceItem;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

public class WorkspaceListView extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workspacelist);

		/*
		 * Load workspace list
		 */
		ArrayList<WorkspaceItem> workspaceList = loadWorkspace();

		final ListView lvl = (ListView) findViewById(R.id.ListOfWorkspace);
		CustomBaseAdapter cba = new CustomBaseAdapter(this, workspaceList);
		lvl.setAdapter(cba);
	}
	
	/*
	 * Create Option Menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.workspacelist_optionmenu, menu);
		return true;
	}
	
	/*
	 * Create event for Option Menu
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.wsl_new_ws:
	        	Intent iWorkspaceDetail = new Intent(WorkspaceListView.this, WorkspaceDetailView.class);
	        	startActivity(iWorkspaceDetail);
	        	break;
	        case R.id.wsl_setting:     Toast.makeText(this, "You pressed Setting!", Toast.LENGTH_LONG).show();
	                            break;
	        case R.id.wsl_exit: Toast.makeText(this, "You pressed the Exit!", Toast.LENGTH_LONG).show();
	                            break;
	    }
	    return true;
	}


	// load and decrypt workspace from file WORKSPACE_FILE

	public ArrayList<WorkspaceItem> loadWorkspace() {

		ArrayList<WorkspaceItem> list = new ArrayList<WorkspaceItem>();

		try {
			FileInputStream fis = openFileInput(StaticValue.WORKSPACE_FILE);

			Properties properties = new Properties();
			properties.load(fis);

			int n = Integer.parseInt(properties.getProperty("n"));

			for (int i = 1; i <= n; i++) {
				String ws = properties.getProperty("w" + i);
				if (ws != null) {
					ws = StaticValue.keyCrypto.decrypt(ws);
					WorkspaceItem wi = new WorkspaceItem();
					wi.setName(ws);
					wi.setAccuracy(String.valueOf(i));
					wi.setLastVisit("today");
					list.add(wi);
				}
			}

			return list;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
