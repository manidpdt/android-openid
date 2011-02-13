package android.bluebox.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class InitConfiguration extends Activity {

	private PasswordCrypto passwordCrypto;
	private KeyCrypto keyCrypto;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = getIntent().getExtras();
		String pwd = b.getString("pwd");
		String remind = b.getString("remind");
		initConfig(pwd, remind);
		Toast.makeText(this, "Congratilation", Toast.LENGTH_LONG).show();
	}

	public void initConfig(String pwd, String remind) {

		passwordCrypto = new PasswordCrypto();
		keyCrypto = new KeyCrypto();
		
		createPwdMD5(pwd);
		createRemindFile(remind);
		createKeyFile(pwd);
		createPointerFile();
		createDataFile();
	}

	public void createPwdMD5(String pwd) {
		try {
			FileOutputStream fos = openFileOutput(StaticValue.PWD_MD5, Context.MODE_PRIVATE);
			passwordCrypto.initFile(fos, pwd);
			fos.close();
		} catch (FileNotFoundException e) {
			System.err.println("Can not create pwd_md5");
		} catch (IOException e) {
			System.err.println("Can not modify pwd_md5");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createRemindFile(String remind) {
		try {
			FileOutputStream fos = openFileOutput(StaticValue.PROPERTIES_FILE, Context.MODE_PRIVATE);
			OutputStreamWriter out = new OutputStreamWriter(fos);
			out.write(remind);
			out.close();
			fos.close();
		} catch (FileNotFoundException e) {
			System.err.println("Can not create remind");
		} catch (IOException e) {
			System.err.println("Can not modify remind");
		}
	}

	public void createKeyFile(String pwd) {
		try {
			FileOutputStream fos = openFileOutput(StaticValue.KEY_FILE, Context.MODE_PRIVATE);
			keyCrypto.initFile(fos, passwordCrypto);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
	}

	public void createPointerFile() {
		try {
			FileOutputStream fos = openFileOutput(StaticValue.POINTER_FILE, Context.MODE_PRIVATE);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public void createDataFile() {
		try {
			FileOutputStream fos = openFileOutput(StaticValue.DATA_FILE, Context.MODE_PRIVATE);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
}