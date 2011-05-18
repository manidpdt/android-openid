package android.bluebox.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.bluebox.R;
import android.bluebox.model.StaticBox;
import android.content.Intent;
import android.os.Bundle;

public class Main extends Activity {

	Intent intent;
	boolean isFirstTime = false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		intent = new Intent(Main.this, InitPasswordView.class);
		if (checkExistedFile()) {
			intent = new Intent(Main.this, LoginView.class);
		} else {
			intent = new Intent(Main.this, InitPasswordView.class);
			isFirstTime = true;
		}
		startActivityForResult(intent, 0);
	}

	public boolean checkExistedFile() {
		FileInputStream fis;
		try {
			fis = openFileInput(StaticBox.PWD_MD5);
			fis.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == RESULT_OK && isFirstTime) //all done
		{
			intent = new Intent(Main.this, LoginView.class);
			startActivity(intent);
		}
	}
}
