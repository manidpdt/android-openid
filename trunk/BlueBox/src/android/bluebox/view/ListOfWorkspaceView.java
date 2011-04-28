/*
 * 
 */

package android.bluebox.view;

import java.util.ArrayList;

import android.app.Activity;
import android.bluebox.R;
import android.bluebox.model.WorkspaceItem;
import android.os.Bundle;
import android.widget.ListView;

public class ListOfWorkspaceView extends Activity {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		ArrayList<WorkspaceItem> workspaceList = loadWorkspace();
		
		final ListView lvl = (ListView) findViewById(R.id.ListOfWorkspace);
		lvl.setAdapter(new CustomBaseAdapter(this, workspaceList));
	}
	
	// load and decrypt workspace from file WORKSPACE_FILE
	
	public ArrayList<WorkspaceItem> loadWorkspace() {
		return null;
	}
}
