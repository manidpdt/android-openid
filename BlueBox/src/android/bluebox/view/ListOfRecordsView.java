package android.bluebox.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ListActivity;
import android.bluebox.R;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleAdapter;

public class ListOfRecordsView extends ListActivity {
	
	// declare an SimpleAdapter for containing list of data
	
	private SimpleAdapter notes;
	
	// Customize datalist of Android
	
	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	
	// declare IDs for menu
	
	private static final int ADD_ITEM_ID = 1;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listofrecords);
		
		notes = new SimpleAdapter(
				this,
				list,
				R.layout.item_list_two_lines,
				new String[] {"title", "content"},
				new int[] {R.id.itemlisttitle, R.id.itemlistcontent});
		
		this.setListAdapter(notes);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, ADD_ITEM_ID, Menu.NONE, "Add new record");
		return result;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ADD_ITEM_ID:
			addRecord();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/** add new record to data
	 * 1st byte is 0 or 1
	 * 	0 - show without password
	 * 	1 - show with password
	 * remain is content
	 */
	
	private void addRecord() {
		
	}
}
