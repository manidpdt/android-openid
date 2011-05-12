package android.bluebox.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluebox.R;
import android.bluebox.model.StaticBox;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class IdentityWorkspaceListView extends ListActivity {
	ListView lvWorkspace;

	ArrayList<String> workspaceList;

	int numberOfWorkspace;

	String encryptedIdName;
	int idIdentity;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		encryptedIdName = this.getIntent().getExtras().getString("encryptedIdName");
		idIdentity = Integer.parseInt(this.getIntent().getExtras().getString("id"));

		loadIdentityWorkspaceList();
		lvWorkspace = this.getListView();
		lvWorkspace.setTextFilterEnabled(true);
		lvWorkspace.setOnItemClickListener(clickItem);
	}

	public void loadIdentityWorkspaceList() {

		try {
			FileInputStream fis = openFileInput("s" + encryptedIdName);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			String rawWorkspaceList = StaticBox.keyCrypto.decrypt(properties.getProperty("w" + idIdentity));
			String[] rawWorkspaceLists = rawWorkspaceList.split(", ");
			workspaceList = new ArrayList<String>(Arrays.asList(rawWorkspaceLists));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setListAdapter(new ArrayAdapter<String>(this, R.layout.identityworkspacelist, workspaceList));
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.identityworkspacelist_optionmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		/*
		 * Add new workspace to identity
		 */
		case R.id.itl_add_workspace:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Choose workspace");

			final String[] list = loadWorkspaceList();
			builder.setItems(list, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int item) {
					// TODO Auto-generated method stub
					addWorkspace(list[item]);
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();

			break;
			/*
			 * Go to activity manage workspace
			 */
		case R.id.itl_manage_workspace:
			Intent intent = new Intent(IdentityWorkspaceListView.this, WorkspaceListView.class);
			startActivity(intent);
			break;
		}

		return true;
	}

	public String[] loadWorkspaceList() {

		ArrayList<String> list = new ArrayList<String>();

		try {
			FileInputStream fis = openFileInput(StaticBox.WORKSPACE_FILE);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			int numberOfWorkspace = Integer.parseInt(properties.getProperty("n"));

			for (int i = 1; i <= numberOfWorkspace; i++) {
				String workspaceName = properties.getProperty("w" + i);
				if (workspaceName != null) {
					workspaceName = StaticBox.keyCrypto.decrypt(workspaceName);
					list.add(workspaceName);
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] result = list.toArray(new String[list.size()]);
		return result;
	}

	public void addWorkspace(String newWorkspaceName) {

		if (workspaceList.contains(newWorkspaceName)) {
			return;
		}
		try {
			FileInputStream fis = openFileInput("s" + encryptedIdName);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			String strWorkspace = properties.getProperty("w" + idIdentity);
			strWorkspace = StaticBox.keyCrypto.decrypt(strWorkspace);
			if (strWorkspace.trim().equals("")) {
				strWorkspace = newWorkspaceName;
			} else {
				strWorkspace = strWorkspace + ", " + newWorkspaceName;
			}
			strWorkspace = StaticBox.keyCrypto.encrypt(strWorkspace);
			properties.setProperty("w" + idIdentity, strWorkspace);

			FileOutputStream fos = openFileOutput("s" + encryptedIdName, Context.MODE_PRIVATE);
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

		loadIdentityWorkspaceList();
	}

	OnItemClickListener clickItem = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			final String strName = ((TextView) view).getText().toString();
			removeWorkspace(strName);
		}
	};

	public void removeWorkspace(String strName) {

		for (int i = 0; i < workspaceList.size(); i++) {
			if (workspaceList.get(i).equals(strName)) {
				workspaceList.remove(i);

				try {
					FileInputStream fis = openFileInput("s" + encryptedIdName);
					Properties properties = new Properties();
					properties.load(fis);
					fis.close();

					String strWs = workspaceList.get(0);

					for (int j = 1; j <= workspaceList.size(); j++) {
						strWs = strWs + ", " + workspaceList.get(j);
					}
					strWs = StaticBox.keyCrypto.encrypt(strWs);
					
					properties.setProperty("w" + idIdentity, strWs);
					
					FileOutputStream fos = openFileOutput("s" + encryptedIdName, Context.MODE_PRIVATE);
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
				
				loadIdentityWorkspaceList();
				return;
			}
		}
	}
}
