/*
 * Same as IdentityList
 */

package android.bluebox.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import android.app.Activity;
import android.bluebox.R;
import android.bluebox.model.MatchingCustomBaseAdapter;
import android.bluebox.model.MatchingItem;
import android.bluebox.model.StaticBox;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

public class MatchingListView extends Activity {

	ListView lvMatching;
	ArrayList<MatchingItem> matchingList;
	MatchingCustomBaseAdapter cba;

	String message;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.matchinglist);

		//		message = this.getIntent().getExtras().getString("request");

		lvMatching = (ListView) findViewById(R.id.ListOfMatching);
		//		lvMatching.setOnItemClickListener(clickItem);

		matchingList = analysis("01,1,username");
		cba = new MatchingCustomBaseAdapter(this, matchingList);
		lvMatching.setAdapter(cba);

	}

	/*
	 * Load data to list view
	 */
	public ArrayList<MatchingItem> loadMatchingDetail() {
		ArrayList<MatchingItem> list = new ArrayList<MatchingItem>();

		return list;
	}

	public ArrayList<MatchingItem> analysis(String message) {

		ArrayList<MatchingItem> list = new ArrayList<MatchingItem>();

		String[] request = message.split(",");

		int n = Integer.parseInt(request[1]);
		for (int i = 0; i < n; i++) {
			MatchingItem mi = new MatchingItem();

			String name = request[i + 2]; 
			mi.setName(name);
			int id = findSynonyms(name);

			/*
			 * If find name in synonyms file
			 */
			if (id > 0) {

				/*
				 * Load recommend identity
				 */
				Properties properties = new Properties();
				try {
					FileInputStream fis = openFileInput(StaticBox.SEMANTIC_FILE);
					properties.load(fis);
					fis.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				mi.setIdentity(StaticBox.keyCrypto.decrypt(properties.getProperty("s" + id)));
			} else {
				mi.setIdentity("null");
			}
			mi.setValue("null");
			list.add(mi);
		}

		return list;
	}

	public int findSynonyms(String name) {

		try {
			FileInputStream fis = openFileInput(StaticBox.SYNONYMS_FILE);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			int n = Integer.parseInt(properties.getProperty("n"));
			for (int i = 1; i <= n; i++) {
				String syn = properties.getProperty(String.valueOf("s" + i));
				syn = StaticBox.keyCrypto.decrypt(syn);
				if (syn.contains(name))
					return i;
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.matchinglist_optionmenu, menu);
		return true;
	}

	/*
	 * Create event for Option Menu
	 */
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent;

		switch (item.getItemId()) {
		
		/*
		 * Send data to PC
		 */
		case R.id.ml_send:
			break;
			
		case R.id.ml_change_id:
			intent = new Intent(MatchingListView.this, SemanticListView.class);
			startActivity(intent);
			finish();
			break;
			
		case R.id.ml_change_ws:
			intent = new Intent(MatchingListView.this, WorkspaceListView.class);
			startActivity(intent);
			finish();
			break;
			
		case R.id.ml_change_tag:
			intent = new Intent(MatchingListView.this, TagListView.class);
			startActivity(intent);
			finish();
			break;
		}
		
		return true;
	}

}
