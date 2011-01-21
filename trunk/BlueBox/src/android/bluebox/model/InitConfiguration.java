package android.bluebox.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

public class InitConfiguration extends Activity {

	private String strKey;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void initConfiguration(String pwd, String remind) {
		createPwdMD5(pwd, remind);
		createKeyFile(pwd);
		createPointerFile();
		createDataFile();
	}

	public void createPwdMD5(String pwd, String remind) {
		try {
			FileOutputStream fos = openFileOutput("pwd_md5", MODE_PRIVATE);
			MessageDigest md5 = MessageDigest.getInstance("md5");
			md5.update(pwd.getBytes(), 0, pwd.length());
			String sig = new BigInteger(1, md5.digest()).toString();

			OutputStreamWriter out = new OutputStreamWriter(fos);
			out.write(sig + "\n" + remind);
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

	public void createKeyFile(String pwd) {
		try {
			SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
			strKey = new Integer(prng.nextInt()).toString();

			FileOutputStream fos = openFileOutput("key", Context.MODE_PRIVATE);
			Crypto crypto = new Crypto(pwd);
			crypto.encryptStringToFile(strKey, fos);
			fos.close();
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Can not use SHA1PRNG algorithm");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createPointerFile() {
		try {
			FileOutputStream fos = openFileOutput("pointer", Context.MODE_PRIVATE);
			Crypto crypto = new Crypto(strKey);
			crypto.encryptStringToFile(null, fos);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public void createDataFile() {
		try {
			FileOutputStream fos = openFileOutput("data", Context.MODE_PRIVATE);
			Crypto crypto = new Crypto(strKey);
			crypto.encryptStringToFile(null, fos);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
}
