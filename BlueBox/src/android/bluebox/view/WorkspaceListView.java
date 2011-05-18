/*
 * 
 */

package android.bluebox.view;


import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluebox.R;
import android.bluebox.model.CustomBaseAdapter;
import android.bluebox.model.NetworkBox;
import android.bluebox.model.StaticBox;
import android.bluebox.model.WorkspaceItem;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class WorkspaceListView extends Activity {

	final boolean IS_NEW_WORKSPACE = true;

	ListView lvWorkspace;
	ArrayList<WorkspaceItem> workspaceList;
	CustomBaseAdapter cba;
	
	Intent intent;

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

		intent = new Intent (WorkspaceListView.this, SemanticListView.class);
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
//			Toast.makeText(WorkspaceListView.this, "You have chosen: " + " " + wsItem.getName(), Toast.LENGTH_SHORT).show();

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
//					Toast.makeText(getBaseContext(), workspaceOption[item], Toast.LENGTH_SHORT).show();
					switch (item) {

					/*
					 * Connect
					 */
					case 0:

						try {
							FileInputStream fis  = openFileInput("w" + wsItem.getEncryptedName());
							Properties properties = new Properties();
							properties.load(fis);
							fis.close();

							String hostIP = properties.getProperty("network");
//							Toast.makeText(getBaseContext(), hostIP, Toast.LENGTH_SHORT).show();
							if (hostIP != null) {
								hostIP = StaticBox.keyCrypto.decrypt(hostIP);
//								Toast.makeText(getBaseContext(), hostIP, Toast.LENGTH_SHORT).show();
							}
							if (NetworkBox.connectToHost(getBaseContext(), hostIP)) {
								
//								boolean b = NetworkBox.sendMessage("2973<EOF>");
//								Toast.makeText(getBaseContext(), String.valueOf(b) + NetworkBox.RESPONEPAIRCODE  + "<EOF>", Toast.LENGTH_SHORT).show();
								connectServer(wsItem.getName());	
							}
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 

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
						deleteWorkspace(wsItem);
						break;
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

					wi.setAccuracy(StaticBox.keyCrypto.decrypt(properties2.getProperty("network")));
					wi.setLastVisit(StaticBox.keyCrypto.decrypt(properties2.getProperty("gps")));
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

	public void deleteWorkspace(WorkspaceItem wsItem) {

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

			FileOutputStream fos = openFileOutput(StaticBox.WORKSPACE_FILE, Context.MODE_PRIVATE);
			properties.store(fos, null);
			fos.flush();
			fos.close();

			refreshWorkspaceList();

			/*
			 * Update tag in semantic
			 */

			String workspaceName = wsItem.getName();

			fis = openFileInput(StaticBox.SEMANTIC_FILE);
			properties = new Properties();
			properties.load(fis);
			fis.close();

			int n = Integer.parseInt(properties.getProperty("n"));

			for (int i = 1; i <= n; i++) {
				String s = properties.getProperty("s" + i);
				if (s != null) {
					fis = openFileInput("s" + s);
					Properties properties2 = new Properties();
					properties2.load(fis);
					fis.close();

					int m = Integer.parseInt(properties2.getProperty("n"));

					for (int j = 1; j <= m; j++) {
						String s2 = properties2.getProperty("w" + j);
						if (s2 != null) {
							s2 = StaticBox.keyCrypto.decrypt(s2);
							if (s2.contains(workspaceName)) {
								s2 = s2.replace(workspaceName + ", ", "");
								s2 = s2.replace(", " + workspaceName, "");
								s2 = s2.replace(workspaceName, "");
								properties2.setProperty("w" + j, StaticBox.keyCrypto.encrypt(s2));
							}
						}
					}

					fos = openFileOutput("s" + s, Context.MODE_PRIVATE);
					properties2.store(fos, null);
					fos.flush();
					fos.close();

				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void connectServer(String wsName) {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Pair code");

		// Set an EditText view to get user Input
		final EditText edtName = new EditText(this);
		edtName.setText("");
		alert.setView(edtName);
		//						alert.setView(edtName);

		alert.setPositiveButton("Connect", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int button) {
				// TODO Auto-generated method stub
				String code = edtName.getText().toString().trim(); 
				if (code.length() > 0) {
					boolean b = NetworkBox.sendPairCode(edtName.getText().toString());
					if (b)
						new ListeningMessage().execute();
					Toast.makeText(getBaseContext(), String.valueOf(b), Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getBaseContext(), "Please input pair code", Toast.LENGTH_SHORT).show();
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
	}
	
	public class ListeningMessage extends AsyncTask<Void, Void, Void> {

		private final String REQUESTFILL = "1234";
		
		ServerSocket server;
		
		public DataInputStream dis;
		
		protected void onPreExecute() {
			try {
				server = new ServerSocket(NetworkBox.hostPort + 10);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			while (true) {
				try {
					Socket socket = server.accept();
					dis = new DataInputStream(socket.getInputStream());
					String message = this.recieveMessage();
					
					if (message != null) {
						
						startActivity(intent);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		protected void onPostExecute(final Void unused) {
			try {
				server.close();
				dis.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		protected String recieveMessage() {
			try {
				byte[] b = new byte[1024];
				dis.read(b);
				String s = new String (b);
				s = s.substring(NetworkBox.getPairCode().length() + 1);
				return s;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	}
	
}
