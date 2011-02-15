package android.bluebox.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.bluebox.R;
import android.bluebox.model.StaticValue;
import android.content.Intent;
import android.os.Bundle;

public class Main extends Activity{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Intent intent;
		if (checkExistedFile()) {
			intent = new Intent(Main.this, LoginView.class);
		} else {
			intent = new Intent(Main.this, InitPasswordView.class);
		}
		startActivityForResult(intent, 0);
	}

	public boolean checkExistedFile() {
		FileInputStream fis;
		try {
			fis = openFileInput(StaticValue.PWD_MD5);
			fis.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
