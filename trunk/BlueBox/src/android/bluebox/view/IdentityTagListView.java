package android.bluebox.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

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
		lvTag.setOnItemClickListener(clickItem);

	}

	public void loadIdentityTagList() {

		try {
			FileInputStream fis = openFileInput("s" + encryptedIdName);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			String rawTagList = StaticBox.keyCrypto.decrypt(properties.getProperty("t" + idIdentity));
			String[] rawTagLists = rawTagList.split(", ");
			tagList = new ArrayList<String>(Arrays.asList(rawTagLists));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setListAdapter(new ArrayAdapter<String>(this, R.layout.identitytaglist, tagList));
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.identitytaglist_optionmenu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		/*
		 * Add new tag to identity
		 */
		case R.id.itl_add_tag:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Choose tag");

			final String[] list = loadTagList();
			builder.setItems(list, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int item) {
					// TODO Auto-generated method stub
					addTag(list[item]);
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();

			break;
			/*
			 * Go to activity manage tag
			 */
		case R.id.itl_manage_tag:
			Intent intent = new Intent(IdentityTagListView.this, TagListView.class);
			startActivity(intent);
			break;
		}

		return true;
	}

	public String[] loadTagList() {

		ArrayList<String> list = new ArrayList<String>();

		try {
			FileInputStream fis = openFileInput(StaticBox.TAG_FILE);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			int numberOfTag = Integer.parseInt(properties.getProperty("n"));

			for (int i = 1; i <= numberOfTag; i++) {
				String tagName = properties.getProperty("t" + i);
				if (tagName != null) {
					tagName = StaticBox.keyCrypto.decrypt(tagName);
					list.add(tagName);
				}
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String[] result = list.toArray(new String[list.size()]);
		return result;
	}

	public void addTag(String newTagName) {

		if (tagList.contains(newTagName)) {
			return;
		}
		try {
			FileInputStream fis = openFileInput("s" + encryptedIdName);
			Properties properties = new Properties();
			properties.load(fis);
			fis.close();

			String strTag = properties.getProperty("t" + idIdentity);
			strTag = StaticBox.keyCrypto.decrypt(strTag);
			if (strTag.trim().equals("")) {
				strTag = newTagName;
			} else {
				strTag = strTag + ", " + newTagName;
			}
			strTag = StaticBox.keyCrypto.encrypt(strTag);
			properties.setProperty("t" + idIdentity, strTag);

			FileOutputStream fos = openFileOutput("s" + encryptedIdName, Context.MODE_PRIVATE);
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

		loadIdentityTagList();
	}

	OnItemClickListener clickItem = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			final String strName = ((TextView) view).getText().toString();

			final CharSequence[] tagListOption = {"Remove tag"};

			AlertDialog.Builder builder = new AlertDialog.Builder(IdentityTagListView.this);
			builder.setTitle(strName + " Options");
			builder.setItems(tagListOption, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int item) {
					// TODO Auto-generated method stub

					/*
					 * Remove
					 */
					switch(item) {
					case 0:
						removeTag(strName);
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

	public void removeTag(String strName) {

		for (int i = 0; i < tagList.size(); i++) {
			if (tagList.get(i).equals(strName)) {
				tagList.remove(i);

				try {
					FileInputStream fis = openFileInput("s" + encryptedIdName);
					Properties properties = new Properties();
					properties.load(fis);
					fis.close();

					String strTag = tagList.get(0);

					for (int j = 1; j < tagList.size(); j++) {
						strTag = strTag + ", " + tagList.get(j);
					}
					strTag = StaticBox.keyCrypto.encrypt(strTag);

					properties.setProperty("t" + idIdentity, strTag);

					FileOutputStream fos = openFileOutput("s" + encryptedIdName, Context.MODE_PRIVATE);
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

				loadIdentityTagList();
				return;
			}
		}
	}
}
