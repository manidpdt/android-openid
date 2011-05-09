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
import android.app.AlertDialog;
import android.bluebox.R;
import android.bluebox.model.StaticValue;
import android.bluebox.model.WorkspaceItem;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class WorkspaceListView extends Activity {

	ListView lvWorkspace;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workspacelist);

		/*
		 * Load workspace list
		 */
		ArrayList<WorkspaceItem> workspaceList = loadWorkspace();

		lvWorkspace = (ListView) findViewById(R.id.ListOfWorkspace);
		CustomBaseAdapter cba = new CustomBaseAdapter(this, workspaceList);
		lvWorkspace.setAdapter(cba);
		lvWorkspace.setOnItemClickListener(chooseWorkspace);
	}
	
	/*
	 * Catch choosing workspace event
	 */
	OnItemClickListener chooseWorkspace = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> a, View v, int position,
				long id) {
			// TODO Auto-generated method stub
			WorkspaceItem wsItem = (WorkspaceItem) lvWorkspace.getItemAtPosition(position);
			Toast.makeText(WorkspaceListView.this, "You have chosen: " + " " + wsItem.getName(), Toast.LENGTH_SHORT).show();
			
			/*
			 * Create list of option: Connect, Edit, Delete
			 */
			final CharSequence[] workspaceOption = {"Connect", "Edit", "Delete"};
			
			/*
			 * Create dialog
			 */
			AlertDialog.Builder builder = new AlertDialog.Builder(WorkspaceListView.this);
			builder.setTitle(wsItem.getName() + " Options");
			builder.setItems(workspaceOption, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int item) {
					// TODO Auto-generated method stub
					Toast.makeText(getBaseContext(), workspaceOption[item], Toast.LENGTH_SHORT).show();
				}
			});
			AlertDialog dialog = builder.create();
			
			/*
			 * Show dialog
			 */
			dialog.show();
		}
	};
	
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
	        	
	        	/*
	        	 * 0:	new Workspace
	        	 * 1:	load existed workspace
	        	 */
	        	startActivityForResult(iWorkspaceDetail, 0);
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
			
			fis.close();

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
