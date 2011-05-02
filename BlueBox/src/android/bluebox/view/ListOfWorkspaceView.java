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
import android.bluebox.model.InitConfiguration;
import android.os.Bundle;
import android.widget.ListView;

public class ListOfWorkspaceView extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listofworkspace);
		
		ArrayList<WorkspaceItem> workspaceList = loadWorkspace();
		
		final ListView lvl = (ListView) findViewById(R.id.ListOfWorkspace);
		CustomBaseAdapter cba = new CustomBaseAdapter(this, workspaceList);
		lvl.setAdapter(cba);
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
//					ws = StaticValue.keyCrypto.decrypt(ws);
					WorkspaceItem wi = new WorkspaceItem();
					wi.setName(ws);
					wi.setAccuracy(String.valueOf(i));
					wi.setLastVisit("today");
					list.add(wi);
				}
			}
			
//			WorkspaceItem wi = new WorkspaceItem();
//			wi.setName("HCMUS");
//			wi.setAccuracy("90%");
//			wi.setLastVisit("today");
//			
//			list.add(wi);
			
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
