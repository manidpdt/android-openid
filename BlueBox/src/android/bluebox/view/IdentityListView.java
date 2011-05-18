package android.bluebox.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluebox.R;
import android.bluebox.model.IdCustomBaseAdapter;
import android.bluebox.model.IdentityItem;
import android.bluebox.model.NetworkBox;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class IdentityListView extends Activity {

	ListView lvIdentityDetail;
	ArrayList<IdentityItem> idList;
	IdCustomBaseAdapter cba;

	String encryptedIdName = null;
	int numberOfId;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.identitydetail);

		String name = this.getIntent().getExtras().getString("idName");
		encryptedIdName = StaticBox.keyCrypto.encrypt(name);

		lvIdentityDetail = (ListView) findViewById(R.id.ListOfIdentity);
		lvIdentityDetail.setOnItemClickListener(clickItem);

		idList = loadIdentityDetail();
		cba = new IdCustomBaseAdapter(this, idList);
		lvIdentityDetail.setAdapter(cba);

	}

	/*
	 * Load data to list view
	 */
	public ArrayList<IdentityItem> loadIdentityDetail() {
		ArrayList<IdentityItem> list = new ArrayList<IdentityItem>();

		try {
			FileInputStream fis = openFileInput("s" + encryptedIdName);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			numberOfId = Integer.parseInt(properties.getProperty("n"));

			for (int i = 1; i <= numberOfId; i++) {
				IdentityItem idItem = new IdentityItem();

				String idString = properties.getProperty("i" + i);
				if (idString != null) {
					idString = StaticBox.keyCrypto.decrypt(idString);
					idItem.setName(idString);
					idItem.setEncryptedName(StaticBox.keyCrypto.encrypt(idString));
					idItem.setId(i);

					idString = properties.getProperty("t" + i);
					if (idString != null) {
						idString = StaticBox.keyCrypto.decrypt(idString);
						idItem.setTagList("Tag: " + idString);
					}

					idString = properties.getProperty("w" + i);
					if (idString != null) {
						idString = StaticBox.keyCrypto.decrypt(idString);
						idItem.setWorkspaceList("Workspace: " + idString);
					}
					list.add(idItem);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	/*
	 * Click on item list view
	 */
	OnItemClickListener clickItem = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> a, View v, int position, long id) {
			// TODO Auto-generated method stub
			final IdentityItem idItem = (IdentityItem) lvIdentityDetail
			.getItemAtPosition(position);
			Toast.makeText(IdentityListView.this,
					"You have chosen: " + " " + idItem.getName(),
					Toast.LENGTH_SHORT).show();

			/*
			 * Create list of option: Connect, Edit Value, Edit Tag, Edit Workspace, Delete
			 */
			final CharSequence[] idOption = { "Connect", "Edit Value",
					"Edit Tag", "Edit Workspace", "Delete" };

			/*
			 * Create dialog
			 */
			AlertDialog.Builder builder = new AlertDialog.Builder(
					IdentityListView.this);
			builder.setTitle(idItem.getName() + " Options");
			builder.setItems(idOption, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int item) {
					// TODO Auto-generated method stub
					Toast.makeText(getBaseContext(), idOption[item],
							Toast.LENGTH_SHORT).show();

					Intent intent;

					switch (item) {
					/*
					 * Connect
					 */
					case 0:
						sendIdentity(idItem);
						break;

						/*
						 * Edit Value
						 */
					case 1:
						editIdentityValue(idItem.getId(), idItem.getName());
						break;
						/*
						 * Edit Tag
						 */
					case 2:
						intent = new Intent(IdentityListView.this,
								IdentityTagListView.class);

						intent.putExtra("encryptedIdName", encryptedIdName);
						intent.putExtra("id", String.valueOf(idItem.getId()));
						startActivityForResult(intent, 1);

						break;

						/*
						 * Edit Workspace
						 */
					case 3:
						intent = new Intent(IdentityListView.this,
								IdentityWorkspaceListView.class);

						intent.putExtra("encryptedIdName", encryptedIdName);
						intent.putExtra("id", String.valueOf(idItem.getId()));
						startActivityForResult(intent, 1);

						break;

						/*
						 * Delete
						 */
					case 4:
						deleteIdentityValue(idItem);
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
		inflater.inflate(R.layout.identitydetail_optionmenu, menu);
		return true;
	}

	/*
	 * Create event for Option Menu
	 */
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent;

		switch (item.getItemId()) {
		case R.id.idd_new_id:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("New Identity Value");
			alert.setMessage("Value");

			// Set an EditText view to get user Input
			final EditText edtName = new EditText(this);
			alert.setView(edtName);

			alert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int button) {
					// TODO Auto-generated method stub
					String newIdentityValue = edtName.getText()
					.toString().trim();
					if (newIdentityValue.length() > 0) {
						try {
							FileInputStream fis = openFileInput("s"	+ encryptedIdName);
							Properties properties = new Properties();
							properties.load(fis);
							fis.close();

							numberOfId = Integer.parseInt(properties.getProperty("n"));
							properties.setProperty("n", String.valueOf(++numberOfId));
							properties.setProperty("i" + numberOfId, StaticBox.keyCrypto.encrypt(newIdentityValue));
							properties.setProperty("t" + numberOfId, StaticBox.keyCrypto.encrypt(""));
							properties.setProperty("w" + numberOfId, StaticBox.keyCrypto.encrypt(""));

							FileOutputStream fos = openFileOutput("s"+ encryptedIdName, Context.MODE_PRIVATE);
							properties.store(fos, null);
							fos.flush();
							fos.close();
							Toast.makeText(getBaseContext(), "Identity created", Toast.LENGTH_SHORT).show();
							refreshIdentityList();
						} catch (FileNotFoundException e) {
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(getBaseContext(),
								"Fail create identity",
								Toast.LENGTH_SHORT).show();
					}
				}
			});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int button) {
					// TODO Auto-generated method stub
				}
			});
			alert.show();
			break;

			/*
			 * Go to Tag Activity
			 */
		case R.id.idd_change_tag:

			intent = new Intent(IdentityListView.this, TagListView.class);
			startActivity(intent);
			break;

			/*
			 * Go to Tag Activity
			 */		
		case R.id.idd_change_ws:
			intent = new Intent(IdentityListView.this, WorkspaceListView.class);
			startActivity(intent);
			break;
		}

		return true;
	}

	public void refreshIdentityList() {
		idList = loadIdentityDetail();
		cba.setArraylist(idList);
		cba.notifyDataSetChanged();
	}

	protected void onResume() {
		super.onResume();
		refreshIdentityList();
	}

	public void changeIdentityValue(int id, String newValue) {

		String encryptNewName = StaticBox.keyCrypto.encrypt(newValue);

		try {
			FileInputStream fis = openFileInput("s" + encryptedIdName);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			/*
			 * Find and change value of identity in identity file
			 */

			properties.setProperty("i" + id, encryptNewName);

			FileOutputStream fos = openFileOutput("s" + encryptedIdName,
					Context.MODE_PRIVATE);
			properties.store(fos, null);
			fos.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		refreshIdentityList();

	}

	public void editIdentityValue(final int id, final String strName) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Edit Identity Value");
		alert.setMessage("Change " + strName + " to");

		// Set an EditText view to get user Input
		final EditText edtName = new EditText(this);
		edtName.setText("");
		alert.setView(edtName);
		// alert.setView(edtName);

		alert.setPositiveButton("Change",
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int button) {
				// TODO Auto-generated method stub
				String newIdentityValue = edtName.getText().toString()
				.trim();
				if (newIdentityValue.length() > 0) {
					changeIdentityValue(id, newIdentityValue);
					refreshIdentityList();
				} else {
					Toast.makeText(getBaseContext(),
							"Fail change identity value", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int button) {
				// TODO Auto-generated method stub
			}
		});
		alert.show();
	}

	public void deleteIdentityValue(IdentityItem idItem) {

		try {
			FileInputStream fis = openFileInput("s" + encryptedIdName);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			properties.remove("i" + idItem.getId());
			properties.remove("t" + idItem.getId());
			properties.remove("w" + idItem.getId());

			FileOutputStream fos = openFileOutput("s" + encryptedIdName, Context.MODE_PRIVATE);
			properties.store(fos, null);
			fos.flush();
			fos.close();

			refreshIdentityList();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean sendIdentity(IdentityItem idItem) {

		try {
			
			/*
			 * Send data to server
			 */
			NetworkBox.sendToHost(idItem.getName());
			
			/*
			 * Write to log file
			 */
			Calendar cal = Calendar.getInstance();
			int hour24 = cal.get(Calendar.HOUR_OF_DAY);
			
			FileInputStream fis = openFileInput(StaticBox.LOG_FILE);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			int n = Integer.parseInt(properties.getProperty("n"));
			String record = hour24 + ":" + StaticBox.currentWorkspace
			+ ":" + StaticBox.currentLocation + ":" 
			+ StaticBox.currentNetwork + ":" + idItem.getId();
			properties.setProperty("n", String.valueOf(++n));
			properties.setProperty(String.valueOf(n), record);

			FileOutputStream fos = openFileOutput(StaticBox.LOG_FILE, Context.MODE_PRIVATE);
			properties.store(fos, null);
			fos.flush();
			fos.close();

			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}
}
