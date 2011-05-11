/*
 * Same of SemanticListView
 */

package android.bluebox.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.bluebox.R;
import android.bluebox.model.StaticBox;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TagListView extends ListActivity { 

	ListView lvTag;

//	ArrayList<String> tagList;
	
	int numberOfTag;
	
	String newTagName;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadTagList();

		lvTag = this.getListView();
		lvTag.setTextFilterEnabled(true);
		lvTag.setOnItemClickListener(clickItem);

	}

	public void loadTagList() {
		ArrayList<String> list = new ArrayList<String>();

		try {
			FileInputStream fis = openFileInput(StaticBox.TAG_FILE);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();
			
			numberOfTag = Integer.parseInt(properties.getProperty("n"));
			
			for (int i = 1; i <= numberOfTag; i++) {
				String tag = properties.getProperty("t" + i);
				if (tag!= null) {
					tag = StaticBox.keyCrypto.decrypt(tag);
					list.add(tag);
				}
			}		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setListAdapter(new ArrayAdapter<String>(this, R.layout.taglist, list));
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.taglist_optionmenu, menu);
		return true;
	}
	
	/*
	 * Create event for Option Menu
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Intent intent;
		
		switch (item.getItemId()) {
		case R.id.tl_new_tag:

			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("New Identity");
			alert.setMessage("Name");

			// Set an EditText view to get user Input
			final EditText edtName = new EditText(this);
			alert.setView(edtName);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int button) {
					// TODO Auto-generated method stub
					newTagName = edtName.getText().toString().trim(); 
					if (newTagName.length() > 0) {
						createNewTag(newTagName);
						loadTagList();
					} else {
						Toast.makeText(getBaseContext(), "Fail create identity", Toast.LENGTH_SHORT).show();
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
			break;
			
		case R.id.tl_change_ws:
			intent = new Intent(TagListView.this, WorkspaceListView.class);
			startActivity(intent);
			break;
		}	
		
		return true;
	}
	
	public void createNewTag(String newTagName) {

		String encryptedName =  StaticBox.keyCrypto.encrypt(newTagName);

		try {

			/*
			 * Read info of identity from StaticBox.SEMANTIC_FILE
			 */
			FileInputStream fis = openFileInput(StaticBox.TAG_FILE);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			/*
			 * Add new identity info
			 */
			int n = Integer.valueOf(properties.getProperty("n"));
			properties.setProperty("n", String.valueOf(++n));
			properties.setProperty("t" + n, encryptedName);

			/*
			 * Store to file again
			 */

			FileOutputStream fos = openFileOutput(StaticBox.TAG_FILE, Context.MODE_PRIVATE);
			properties.store(fos, null);
			fos.flush();
			fos.close();

			/*
			 * Create new file of identity
			 */
//			fos = openFileOutput("t" + encryptedName, Context.MODE_PRIVATE);
//			Properties properties2 = new Properties();
//			properties2.setProperty("n", "0");
//			properties2.store(fos, null);
//			fos.flush();
//			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	OnItemClickListener clickItem = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			final String strName = ((TextView) view).getText().toString();
			Toast.makeText(getApplicationContext(), strName, Toast.LENGTH_SHORT).show();

			/*
			 * Create list of option: Connect, Edit, Delete
			 */
			final CharSequence[] semanticOption = {"Open", "Edit", "Delete"};

			/*
			 * Create dialog
			 */
			AlertDialog.Builder builder = new AlertDialog.Builder(TagListView.this);
			builder.setTitle(strName + " Options");
			builder.setItems(semanticOption, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int item) {
					// TODO Auto-generated method stub

					/*
					 * Open
					 */
					switch(item) {
					case 0:
//						Intent intent = new Intent(TagListView.this, TagDetailView.class);
//						intent.putExtra("tagName", strName);	// Send name of identity you want to open
//						startActivity(intent);
						break;

						/*
						 * Edit
						 */
					case 1:
						editTagName(strName);
						break;

						/*
						 * Delete
						 */
					case 2:
						deleleTag(strName);
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
	
	public void changeTagName(String oldName, String newName) {

		String encryptOldName = StaticBox.keyCrypto.encrypt(oldName);
		String encryptNewName = StaticBox.keyCrypto.encrypt(newName);

		try {
			FileInputStream fis = openFileInput(StaticBox.TAG_FILE);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			int n = Integer.parseInt(properties.getProperty("n"));

			/*
			 * Find and change name of identity in semantic file
			 */
			for (int i = 1; i <=n; i++) {
				String id = properties.getProperty("t" + i);
				if (id.equals(encryptOldName)) {
					properties.setProperty("t" + i, encryptNewName);
					break;
				}
			}

			FileOutputStream fos = openFileOutput(StaticBox.TAG_FILE, Context.MODE_PRIVATE);
			properties.store(fos, null);
			fos.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadTagList();
	}
	
	public void deleleTag(String tagName) {

		try {
			FileInputStream fis = openFileInput(StaticBox.TAG_FILE);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			int n = Integer.parseInt(properties.getProperty("n"));

			/*
			 * Delete record
			 */
			for (int i = 1; i <=n; i++) {
				String id = properties.getProperty("t" + i);
				if (id.equals(StaticBox.keyCrypto.encrypt(tagName))) {
					properties.remove("t" + i);
					break;
				}
			}

			/*
			 * Update number of identities
			 */
			properties.setProperty("n", String.valueOf(--n));

			FileOutputStream fos = openFileOutput(StaticBox.TAG_FILE, Context.MODE_PRIVATE);
			properties.store(fos, null);
			fos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadTagList();
	}
	
	public void editTagName(final String strName) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Edit Tag");
		alert.setMessage("Tag Name");

		// Set an EditText view to get user Input
		final EditText edtName = new EditText(this);
		edtName.setText("");
		alert.setView(edtName);
		//						alert.setView(edtName);

		alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int button) {
				// TODO Auto-generated method stub
				newTagName = edtName.getText().toString().trim(); 
				if (newTagName.length() > 0) {
					changeTagName(strName, newTagName);
					loadTagList();
				} else {
					Toast.makeText(getBaseContext(), "Fail create identity", Toast.LENGTH_SHORT).show();
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
}
