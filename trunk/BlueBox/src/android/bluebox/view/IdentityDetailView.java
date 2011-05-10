package android.bluebox.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluebox.R;
import android.bluebox.model.IdCustomBaseAdapter;
import android.bluebox.model.IdentityItem;
import android.bluebox.model.StaticBox;
import android.bluebox.model.IdentityItem;
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
import android.widget.ListView;
import android.widget.Toast;

public class IdentityDetailView extends Activity{

	ListView lvIdentityDetail;
	ArrayList<IdentityItem> idList;
	IdCustomBaseAdapter cba;

	String encryptedIdName = null;
	int numberOfId;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.identitydetail);

		Bundle b = getIntent().getExtras();
		String id = b.getString("id");
		encryptedIdName = StaticBox.keyCrypto.encrypt(id);

		lvIdentityDetail = (ListView) findViewById(R.id.ListOfIdentity);
		lvIdentityDetail.setOnItemClickListener(clickItem);

		idList = loadIdentityDetail();
		cba = new IdCustomBaseAdapter(this, idList);
		lvIdentityDetail.setAdapter(cba);
	}

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
					idItem.setId(i);
				}

				idString = properties.getProperty("t" + i);
				if (idString != null) {
					idString = StaticBox.keyCrypto.decrypt(idString);
					idItem.setTagList(idString);
				}

				idString = properties.getProperty("w" + i);
				if (idString != null) {
					idString = StaticBox.keyCrypto.decrypt(idString);
					idItem.setWorkspaceList(idString);
				}

				list.add(idItem);
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

	OnItemClickListener clickItem = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> a, View v, int position,
				long id) {
			// TODO Auto-generated method stub
			final IdentityItem idItem = (IdentityItem) lvIdentityDetail.getItemAtPosition(position);
			Toast.makeText(IdentityDetailView.this, "You have chosen: " + " " + idItem.getName(), Toast.LENGTH_SHORT).show();

			/*
			 * Create list of option: Connect, Edit, Delete
			 */
			final CharSequence[] idOption = {"Connect", "Edit", "Delete"};

			/*
			 * Create dialog
			 */
			AlertDialog.Builder builder = new AlertDialog.Builder(IdentityDetailView.this);
			builder.setTitle(idItem.getName() + " Options");
			builder.setItems(idOption, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int item) {
					// TODO Auto-generated method stub
					Toast.makeText(getBaseContext(), idOption[item], Toast.LENGTH_SHORT).show();
					switch (item) {
					/*
					 * Connect
					 */
					case 0:
						break;
					/*
					 * Edit
					 */
					case 1:
						Intent intent = new Intent(IdentityDetailView.this, IdentityValueView.class);
						Bundle b = new Bundle();
						b.putString("ecryptName", encryptedIdName);
						b.putString("id", String.valueOf(idItem.getId()));
						intent.putExtras(b);
						startActivityForResult(intent, 1);
						break;
					
					/*
					 * Delete
					 */
					case 2:
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
				switch (item.getItemId()) {
				case R.id.idd_new_id:
					try {
						FileInputStream fis = openFileInput("s" + encryptedIdName);
						Properties properties = new Properties();
						properties.load(fis);
						fis.close();

						numberOfId = Integer.parseInt(properties.getProperty("n"));
						properties.setProperty("n", String.valueOf(++numberOfId));
						properties.setProperty("i" + numberOfId, StaticBox.keyCrypto.encrypt("x"));
						properties.setProperty("t" + numberOfId, StaticBox.keyCrypto.encrypt("y"));
						properties.setProperty("w" + numberOfId, StaticBox.keyCrypto.encrypt("z"));

						FileOutputStream fos = openFileOutput("s" + encryptedIdName, Context.MODE_PRIVATE);
						properties.store(fos, null);
						fos.flush();
						fos.close();

						idList = loadIdentityDetail();
						cba = new IdCustomBaseAdapter(this, idList);
						lvIdentityDetail.setAdapter(cba);

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						Toast.makeText(getBaseContext(), "Can not create file", Toast.LENGTH_SHORT).show();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				return true;
			}
		}
