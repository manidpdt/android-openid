package android.bluebox.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class InitConfiguration extends Activity {

	private String strKey;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle b = getIntent().getExtras();
		String pwd = b.getString("pwd");
		String remind = b.getString("remind");
		initConfig(pwd, remind);
		Toast.makeText(this, "Congratilation", Toast.LENGTH_LONG).show();
	}
	
	public void initConfig(String pwd, String remind) {
		createPwdMD5(pwd);
		createRemindFile(remind);
		createKeyFile(pwd);
		createPointerFile();
		createDataFile();
	}

	public void createPwdMD5(String pwd) {
		try {
			FileOutputStream fos = openFileOutput(StaticValue.PWD_MD5, Context.MODE_PRIVATE);
			MessageDigest md5 = MessageDigest.getInstance("md5");
			md5.update(pwd.getBytes(), 0, pwd.length());
			String sig = new BigInteger(1, md5.digest()).toString();

			OutputStreamWriter out = new OutputStreamWriter(fos);
			out.write(sig);
			out.close();
			fos.close();
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Can not use md5 algorithm");
		} catch (FileNotFoundException e) {
			System.err.println("Can not create pwd_md5");
		} catch (IOException e) {
			System.err.println("Can not modify pwd_md5");
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
			SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
			strKey = new Integer(prng.nextInt()).toString();

			String encryptedKey = Crypto2.encrypt(strKey, new File(StaticValue.KEY_FILE));
			
			FileOutputStream fos = openFileOutput(StaticValue.KEY_FILE, Context.MODE_PRIVATE);
			OutputStreamWriter out = new OutputStreamWriter(fos);
			out.write(encryptedKey);
			out.close();
			fos.close();
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Can not use SHA1PRNG algorithm");
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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