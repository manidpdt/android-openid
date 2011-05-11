package android.bluebox.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import android.app.ListActivity;
import android.bluebox.R;
import android.bluebox.model.StaticBox;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class IdentityTagListView extends ListActivity {
	ListView lvTag;

	ArrayList<String> tagList;
	
	int numberOfTag;
	
	String encryptedIdName;
	int idIdentity;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		encryptedIdName = this.getIntent().getExtras().getString("encryptedIdName");
		idIdentity = Integer.parseInt(this.getIntent().getExtras().getString("id"));
		
		loadIdentityTagList();
		lvTag = this.getListView();
		lvTag.setTextFilterEnabled(true);
//		lvTag.setOnItemClickListener(clickItem);

	}
	
	public void loadIdentityTagList() {

		try {
			FileInputStream fis = openFileInput("s" + encryptedIdName);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();
			
			String rawTagList = StaticBox.keyCrypto.decrypt(properties.getProperty("t" + idIdentity));
			String[] rawTagLists = rawTagList.split(",");
			tagList = new ArrayList<String>(Arrays.asList(rawTagLists));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setListAdapter(new ArrayAdapter<String>(this, R.layout.taglist, tagList));
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.identitytaglist_optionmenu, menu);
		return true;
	}
}
