package android.bluebox.model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class PasswordCrypto {
	
	private String password;
	private String strMD5;
	private byte[] salt = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte) 0xe9, (byte) 0xe0, (byte) 0xae };
	private int iterationCount = 1010;
	
	private Crypto3 blueCrypto; 
	
	public void init(FileInputStream fis, String password) throws Exception {
		
		readFile(fis);
		setPassword(password);
	}
	
	public void readFile(FileInputStream fis) throws Exception {
		DataInputStream dis = new DataInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		
		strMD5 = br.readLine();
//		salt = br.readLine().getBytes();
//		String str = br.readLine();
//		iterationCount = Integer.parseInt(str);
		br.close();
		dis.close();
		fis.close();
	}
	
	// Set new password for PasswordCrypto and re-init.
	
	public void setPassword(String password) throws Exception {
		this.password = password;
		blueCrypto = new Crypto3(this.password, salt, iterationCount); 
	}

	// Create new file PWDMD5
	
	public void initFile(FileOutputStream fos, String password) throws Exception {
		OutputStreamWriter out = new OutputStreamWriter(fos);
		String pwdMD5 = Crypto3.createMD5(password);
		this.password = password;
		
		// write MD5 encription of password to file. This string is used to verify correct password later.
		
		out.write(pwdMD5);
		
		// generate salt and iterationCount for crypto
		
//		 salt = new byte[8];
//		 Random rnd = new Random();
//		 rnd.nextBytes(salt);
//		 iterationCount = 1010;
//		 
//		 out.write(salt + "\n");
//		 out.write(Integer.toString(iterationCount));
		 
		 out.flush();
		 out.close();
		 
		 blueCrypto = new Crypto3(this.password, salt, iterationCount); 
	}
	
	
	public boolean checkPassword(String password) {
		return (Crypto3.createMD5(password).equals(strMD5));
	}
	
	public String encrypt(String str) {
		return blueCrypto.encrypt(str);
	}
	
	public String decrypt(String str) {
		return blueCrypto.decrypt(str);
	}
}
