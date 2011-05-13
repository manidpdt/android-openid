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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SemanticListView extends ListActivity {

	int numberOfIdentity = 0;

	ListView lvIdentity;

	String newIdentityName = "";
	String newIdentityValue = "";

	AlertDialog.Builder alert;
	EditText edtName;

	int index = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadIdentity();

		lvIdentity = this.getListView();
		lvIdentity.setTextFilterEnabled(true);
		lvIdentity.setOnItemClickListener(clickItem);
	}

	public void loadIdentity() {
		ArrayList<String> list = new ArrayList<String>();

		try {
			FileInputStream fis = openFileInput(StaticBox.SEMANTIC_FILE);

			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			numberOfIdentity = Integer.parseInt(properties.getProperty("n"));

			for (int i = 1; i <= numberOfIdentity; i++) {
				String se = properties.getProperty("s" + i);
				if (se != null) {
					se = StaticBox.keyCrypto.decrypt(se);
					list.add(se);
				}
			}		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setListAdapter(new ArrayAdapter<String>(this, R.layout.identitylist, list));
	}

	/*
	 * Create Option Menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.identitylist_optionmenu, menu);
		return true;
	}

	/*
	 * Create event for Option Menu
	 */
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent;

		switch (item.getItemId()) {
		case R.id.idl_new_id:

			alert = new AlertDialog.Builder(this);
			alert.setTitle("New Identity");
			alert.setMessage("Name");

			// Set an EditText view to get user Input
			edtName = new EditText(this);
			alert.setView(edtName);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int button) {
					// TODO Auto-generated method stub
					newIdentityName = edtName.getText().toString().trim(); 
					if (newIdentityName.length() > 0) {
						createNewIdentity(newIdentityName);
						loadIdentity();
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

		case R.id.idl_change_ws:
			intent = new Intent(SemanticListView.this, WorkspaceListView.class);
			startActivity(intent);
			finish();
			break;

		case R.id.idl_change_tag:
			intent = new Intent(SemanticListView.this, TagListView.class);
			startActivity(intent);
			finish();
			break;
		}	
		return true;
	}

	public void createNewIdentity(String newIdentityName) {

		String encryptedName =  StaticBox.keyCrypto.encrypt(newIdentityName);

		try {

			/*
			 * Read info of identity from StaticBox.SEMANTIC_FILE
			 */
			FileInputStream fis = openFileInput(StaticBox.SEMANTIC_FILE);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			/*
			 * Add new identity info
			 */
			int n = Integer.valueOf(properties.getProperty("n"));
			properties.setProperty("n", String.valueOf(++n));
			properties.setProperty("s" + n, encryptedName);

			/*
			 * Store to file again
			 */

			FileOutputStream fos = openFileOutput(StaticBox.SEMANTIC_FILE, Context.MODE_PRIVATE);
			properties.store(fos, null);
			fos.flush();
			fos.close();

			/*
			 * Create new file of identity
			 */
			fos = openFileOutput("s" + encryptedName, Context.MODE_PRIVATE);
			properties = new Properties();
			properties.setProperty("n", "0");
			properties.store(fos, null);
			fos.flush();
			fos.close();

			/*
			 * Add data to synonyms file
			 */
			fos = openFileOutput(StaticBox.SYNONYMS_FILE, Context.MODE_PRIVATE);
			properties = new Properties();
			properties.setProperty("n", String.valueOf(n));
			properties.setProperty(String.valueOf(n), encryptedName);
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
			final CharSequence[] semanticOption = {"Open", "Edit", "Synonyms", "Delete"};

			/*
			 * Create dialog
			 */
			AlertDialog.Builder builder = new AlertDialog.Builder(SemanticListView.this);
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
						Intent intent = new Intent(SemanticListView.this, IdentityListView.class);
						intent.putExtra("idName", strName);	// Send name of identity you want to open
						startActivity(intent);
						break;

						/*
						 * Edit
						 */
					case 1:
						editIdentityName(strName);
						break;

					case 2:
						editSynonyms(strName);
						break;
						/*
						 * Delete
						 */
					case 3:
						deleleIdentity(strName);
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

	private void editSynonyms(String strName) {
		// TODO Auto-generated method stub
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Edit Synonyms");
		alert.setMessage("Synonyms list");

		// Set an EditText view to get user Input
		final EditText edtName = new EditText(this);

		/*
		 * find and load synonyms of identity
		 */
		try {
			FileInputStream fis = openFileInput(StaticBox.SEMANTIC_FILE);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();


			int n = Integer.parseInt(properties.getProperty("n"));
			for (int i = 1; i <= n; i++) {
				String s = properties.getProperty("s" + i);
				if (s != null) {
					s = StaticBox.keyCrypto.decrypt(s); 
					if (s.equals(strName)) {
						index = i;
						break;
					}
				}
			}

			fis = openFileInput(StaticBox.SYNONYMS_FILE);
			properties = new Properties();
			properties.load(fis);
			fis.close();

			edtName.setText(StaticBox.keyCrypto.decrypt(properties.getProperty("s" + index)));
//			edtName.setText(properties.getProperty("s" + index));

		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		alert.setView(edtName);

		alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int button) {
				// TODO Auto-generated method stub
				try {
					FileInputStream fis = openFileInput(StaticBox.SYNONYMS_FILE);
					Properties properties = new Properties();
					properties.load(fis);
					fis.close();

					properties.setProperty("s" + index, StaticBox.keyCrypto.encrypt(edtName.getText().toString()));

					FileOutputStream fos = openFileOutput(StaticBox.SYNONYMS_FILE, Context.MODE_PRIVATE);
					properties.store(fos, null);
					fos.flush();
					fos.close();

				}catch (FileNotFoundException e) {
				} catch (IOException e) {
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

	public void changeIdentityName(String oldName, String newName) {

		String encryptOldName = StaticBox.keyCrypto.encrypt(oldName);
		String encryptNewName = StaticBox.keyCrypto.encrypt(newName);

		try {
			FileInputStream fis = openFileInput(StaticBox.SEMANTIC_FILE);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			int n = Integer.parseInt(properties.getProperty("n"));

			/*
			 * Find and change name of identity in semantic file
			 */
			for (int i = 1; i <=n; i++) {
				String id = properties.getProperty("s" + i);
				if (id.equals(encryptOldName)) {
					properties.setProperty("s" + i, encryptNewName);
					break;
				}
			}

			FileOutputStream fos = openFileOutput(StaticBox.SEMANTIC_FILE, Context.MODE_PRIVATE);
			properties.store(fos, null);
			fos.close();

			/*
			 * Change file name
			 */

			fis = openFileInput("s" + encryptOldName);
			properties.clear();
			properties.load(fis);
			fis.close();

			fos = openFileOutput("s" + encryptNewName, Context.MODE_PRIVATE);
			properties.store(fos, null);
			fos.close();

			deleteFile("s" + encryptOldName);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadIdentity();
	}

	public void deleleIdentity(String identityName) {

		try {
			FileInputStream fis = openFileInput(StaticBox.SEMANTIC_FILE);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			int n = Integer.parseInt(properties.getProperty("n"));

			/*
			 * Delete record
			 */

			int id = 0;

			for (int i = 1; i <=n; i++) {
				String idName = properties.getProperty("s" + i);
				if (idName.equals(StaticBox.keyCrypto.encrypt(identityName))) {
					properties.remove("s" + i);	
					id = i;
					break;
				}
			}

			/*
			 * Update number of identities
			 */
			//			properties.setProperty("n", String.valueOf(--n));

			FileOutputStream fos = openFileOutput(StaticBox.SEMANTIC_FILE, Context.MODE_PRIVATE);
			properties.store(fos, null);
			fos.close();

			/*
			 * Delete record in SYNONYMS_FILE
			 */

			fis = openFileInput(StaticBox.SYNONYMS_FILE);
			properties = new Properties();
			properties.load(fis);
			fis.close();

			if (id > 0)
				properties.remove(String.valueOf(id));

			fos = openFileOutput(StaticBox.SYNONYMS_FILE, Context.MODE_PRIVATE);
			properties.store(fos, null);
			fos.flush();
			fos.close();

			/*
			 * Delete file
			 */
			deleteFile("s" + StaticBox.keyCrypto.encrypt(identityName));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadIdentity();
	}

	public void editIdentityName(final String strName) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Edit Identity");
		alert.setMessage("Name");

		// Set an EditText view to get user Input
		final EditText edtName = new EditText(this);
		edtName.setText("");
		alert.setView(edtName);
		//						alert.setView(edtName);

		alert.setPositiveButton("Change", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int button) {
				// TODO Auto-generated method stub
				newIdentityName = edtName.getText().toString().trim(); 
				if (newIdentityName.length() > 0) {
					changeIdentityName(strName, newIdentityName);
					loadIdentity();
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
