package android.bluebox.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class InitConfiguration extends Activity {
//	public PasswordCrypto passwordCrypto;
//	public KeyCrypto keyCrypto;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle b = getIntent().getExtras();
		String pwd = b.getString("pwd");
		String remind = b.getString("remind");
		initConfig(pwd, remind);
		Toast.makeText(this, "Congratilation", Toast.LENGTH_LONG).show();
	}

	public void initConfig(String pwd, String remind) {

		StaticValue.passwordCrypto = new PasswordCrypto();
		StaticValue.keyCrypto = new KeyCrypto();
		
		createPwdMD5(pwd);
		createRemindFile(remind);
		createKeyFile(pwd);
		try {
			FileInputStream fis = openFileInput(StaticValue.KEY_FILE); 
			StaticValue.keyCrypto.init(fis, StaticValue.passwordCrypto);
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createTagFile();
		createSemanticFile();
		createWorkspaceFile();
	}

	// Ma hoa chuoi pwd theo md5
	public void createPwdMD5(String pwd) {
		try {
			FileOutputStream fos = openFileOutput(StaticValue.PWD_MD5, Context.MODE_PRIVATE);
			StaticValue.passwordCrypto.initFile(fos, pwd);
			fos.close();
		} catch (FileNotFoundException e) {
			System.err.println("Can not create pwd_md5");
		} catch (IOException e) {
			System.err.println("Can not modify pwd_md5");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// tao file goi y password cho nguoi dung
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

	// tao key
	public void createKeyFile(String pwd) {
		try {
			FileOutputStream fos = openFileOutput(StaticValue.KEY_FILE, Context.MODE_PRIVATE);
			StaticValue.keyCrypto.initFile(fos, StaticValue.passwordCrypto);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
	}

	/*
	 * Tao file luu nhung tag hien tai. Thong tin cac tag se duoc luu trong file,
	 * ten file dang: "T" + ten cac tag da duoc ma hoa.
	 */
	
	public void createTagFile() {
		try {
			FileOutputStream fos = openFileOutput(StaticValue.TAG_FILE, Context.MODE_PRIVATE);
			Properties properties = new Properties();
			
			// luu ma md5 cua key de tranh truong hop bi chep de tu may khac
			
			properties.setProperty("header", StaticValue.keyCrypto.getMD5Key());

			// So luong workspace hien tai = 2
			
			properties.setProperty("n", "0");
			
			properties.store(fos, null);
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	/*
	 * Tao file luu nhung semantic hien tai. Thong tin cac tag se duoc luu trong file,
	 * ten file dang: "S" + ten cac tag da duoc ma hoa.
	 */
	
	public void createSemanticFile() {
		try {
			FileOutputStream fos = openFileOutput(StaticValue.SEMANTIC_FILE, Context.MODE_PRIVATE);
			Properties properties = new Properties();
			
			//luu ma md5 cua key de tranh truong hop bi chep de tu may khac
			
			properties.setProperty("header", StaticValue.keyCrypto.getMD5Key());
			
			// So luong workspace hien tai = 2
			
			properties.setProperty("n", "0");
			
			
			properties.store(fos, "");
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	
	/*
	 * Tao file luu nhung workspace hien tai. Thong tin cac workspace se duoc luu trong file,
	 * ten file dang: "W" + ten cac tag da duoc ma hoa.
	 */
	
	public void createWorkspaceFile() {
		try {
			FileOutputStream fos = openFileOutput(StaticValue.WORKSPACE_FILE, Context.MODE_PRIVATE);
			Properties properties = new Properties();
			
			//luu ma md5 cua key de tranh truong hop bi chep de tu may khac
			
			properties.setProperty("header", StaticValue.keyCrypto.getMD5Key());
			
			// So luong workspace hien tai = 2
			
			properties.setProperty("n", "2");
			
			// Them 1 so default workspace: Home, Office
			
			String wp = StaticValue.keyCrypto.encrypt("Vo Minh Huy");
			properties.setProperty("w1", wp);
			wp = StaticValue.keyCrypto.encrypt("Office");
			properties.setProperty("w2", wp);
			
			properties.store(fos, null);
			
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
}
