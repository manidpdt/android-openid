/*
 * 
 */

package android.bluebox.view;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluebox.R;
import android.bluebox.model.CustomBaseAdapter;
import android.bluebox.model.StaticBox;
import android.bluebox.model.WorkspaceItem;
import android.content.Context;
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

	final boolean IS_NEW_WORKSPACE = true;

	ListView lvWorkspace;
	ArrayList<WorkspaceItem> workspaceList;
	CustomBaseAdapter cba;

	int numberOfWorkspaces;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workspacelist);

		/*
		 * Load workspace list
		 */

		lvWorkspace = (ListView) findViewById(R.id.ListOfWorkspace);
		lvWorkspace.setOnItemClickListener(chooseWorkspace);

		workspaceList = loadWorkspace();
		cba = new CustomBaseAdapter(this, workspaceList);
		lvWorkspace.setAdapter(cba);

	}

	/*
	 * Catch choosing workspace event
	 */
	OnItemClickListener chooseWorkspace = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> a, View v, int position,
				long id) {
			// TODO Auto-generated method stub

			/*
			 * Get clicked item
			 */
			final WorkspaceItem wsItem = (WorkspaceItem) lvWorkspace.getItemAtPosition(position);
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
					switch (item) {

					/*
					 * Connect
					 */
					case 0:
						String netInfos = StaticBox.keyCrypto.decrypt(wsItem.getAccuracy()).trim();
						String[] netInfo = netInfos.split(":");
						Toast.makeText(getBaseContext(), String.valueOf(StaticBox.connectToHost(netInfo[0], Integer.parseInt(netInfo[1]))), Toast.LENGTH_SHORT).show();
						StaticBox.currentNetwork = netInfo[0];
						break;

						/*
						 * Edit
						 */
					case 1:
						Intent intent = new Intent(WorkspaceListView.this, WorkspaceDetailView.class);
						intent.putExtra("WorkspaceName", wsItem.getEncryptedName());
						startActivityForResult(intent, 1);
						break;

						/*
						 * Delete
						 */
					case 2:

						try {
							/*
							 * Delete workspace file
							 */
							deleteFile("w" + wsItem.getEncryptedName().trim());

							/*
							 * Delete workspace info in StaticValue.WORKSPACE_FILE
							 */

							FileInputStream fis = openFileInput(StaticBox.WORKSPACE_FILE);
							Properties properties = new Properties();
							properties.load(fis);
							fis.close();

							int id = wsItem.getId();
							properties.remove("w" + id);
//							properties.setProperty("n", String.valueOf(--numberOfWorkspaces));

							FileOutputStream fos = openFileOutput(StaticBox.WORKSPACE_FILE, Context.MODE_PRIVATE);
							properties.store(fos, null);
							fos.flush();
							fos.close();

							refreshWorkspaceList();
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
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

		Intent intent; 

		switch (item.getItemId()) {

		/*
		 * Refresh workspace list
		 */
		case R.id.wsl_refresh:
			refreshWorkspaceList();
			break;

			/*
			 * Case change to identity list
			 */
		case R.id.wsl_change_id:
			intent = new Intent(WorkspaceListView.this, SemanticListView.class);
			startActivity(intent);
			finish();
			break;

		case R.id.wsl_change_tag:
			intent = new Intent(WorkspaceListView.this, TagListView.class);
			startActivity(intent);
			finish();
			break;
		
			/*
			 * Create new workspace
			 */
		case R.id.wsl_new_ws:
			Intent iWorkspaceDetail = new Intent(WorkspaceListView.this, WorkspaceDetailView.class);
			iWorkspaceDetail.putExtra("WorkspaceName", "null");
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
			FileInputStream fis = openFileInput(StaticBox.WORKSPACE_FILE);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			numberOfWorkspaces = Integer.parseInt(properties.getProperty("n"));

			for (int i = 1; i <= numberOfWorkspaces; i++) {
				String ws = properties.getProperty("w" + i);
				if (ws != null) {

					ws = ws.trim();
					FileInputStream fis2 = openFileInput("w" + ws);
					Properties properties2 = new Properties();
					properties2.load(fis2);
					fis2.close();

					WorkspaceItem wi = new WorkspaceItem();
					wi.setEncryptedName(ws.trim());
					ws = StaticBox.keyCrypto.decrypt(ws);
					wi.setId(i);
					wi.setName(ws);

					wi.setAccuracy(properties2.getProperty("network"));
					wi.setLastVisit(properties2.getProperty("gps"));
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
		return list;
	}

	public void refreshWorkspaceList() {
		workspaceList = loadWorkspace();
		cba.setArraylist(workspaceList);
		cba.notifyDataSetChanged();
		lvWorkspace.setAdapter(cba);
	}

	protected void onResume() {
		super.onResume();
		refreshWorkspaceList();
	}
}
