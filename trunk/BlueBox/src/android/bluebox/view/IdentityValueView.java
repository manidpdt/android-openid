package android.bluebox.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import android.app.Activity;
import android.bluebox.R;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class IdentityValueView extends Activity {

	EditText edtName;
	ListView lvWorkspace;
	ListView lvTag;
	
	String[] workspaceList = {"ab", "bc", "cd"};
	
	String encryptedIdName = null;
	int idIdentity = 0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.identityvalue);
		
		edtName = (EditText) findViewById(R.id.idv_name);
		lvWorkspace = (ListView) findViewById(R.id.idv_list_workspace);
		lvTag = (ListView) findViewById(R.id.idv_list_tag);
		
		lvWorkspace.setAdapter(new ArrayAdapter<String>(this, R.layout.listview, workspaceList));
		
		Bundle b = getIntent().getExtras();
		encryptedIdName = b.getString("ecryptName");
		idIdentity = Integer.parseInt(b.getString("id"));
		
	}
	
	public void loadIdentityValue() {
		
		String[] workspaceList;
		String[] tagList;
		
		try {
			FileInputStream fis = openFileInput("i" + encryptedIdName);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();
			
			String strWorkspace = properties.getProperty("w" + idIdentity);
			String strTag = properties.getProperty("t" + idIdentity);
			
			workspaceList = strWorkspace.split(":");
			tagList = strTag.split(":");
			
			lvWorkspace.setAdapter(new ArrayAdapter<String>(this, R.layout.identityvalue, workspaceList));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
