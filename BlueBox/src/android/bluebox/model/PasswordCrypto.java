package android.bluebox.model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordCrypto {
	
	private String password;
	private String strMD5;
	private byte[] salt = new byte[8];
	private int iterationCount;
	
	private Crypto3 blueCrypto; 
	
	public void init(FileInputStream fis, String password) throws Exception {
		
		readFile(fis);
		setPassword(password);
	}
	
	public void readFile(FileInputStream fis) throws Exception {
		DataInputStream dis = new DataInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		
		strMD5 = br.readLine();
		salt = br.readLine().getBytes();
		String str = br.readLine();
		iterationCount = Integer.parseInt(str);
	}
	
	// Set new password for PasswordCrypto and re-init.
	
	public void setPassword(String password) throws Exception {
		this.password = password;
		blueCrypto = new Crypto3(this.password, salt, iterationCount); 
	}

	// Create new file PWDMD5
	
	public void initFile(FileOutputStream fos, String password) throws Exception {
		OutputStreamWriter out = new OutputStreamWriter(fos);
		String pwdMD5 = createMD5(password);
		this.password = password;
		
		// write MD5 encription of password to file. This string is used to verify correct password later.
		
		out.write(pwdMD5 + "\n");
		
		// generate salt and iterationCount for crypto
		
		 salt = new byte[8];
		 Random rnd = new Random();
		 rnd.nextBytes(salt);
		 iterationCount = 1010;
		 
		 out.write(salt + "\n");
		 out.write(Integer.toString(iterationCount));
		 
		 out.flush();
		 out.close();
		 
		 blueCrypto = new Crypto3(this.password, salt, iterationCount); 
	}
	
	public String createMD5(String str) {
		String sig = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			md5.update(str.getBytes(), 0, str.length());
			BigInteger tmp = new BigInteger(1, md5.digest());
			sig = tmp.toString();
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Can not use md5 algorithm");
		}
		return sig;
	}
	
	public boolean checkPassword(String password) {
		return (createMD5(password).equals(strMD5));
	}
	
	public String encrypt(String str) {
		return blueCrypto.encrypt(str);
	}
	
	public String decrypt(String str) {
		return blueCrypto.decript(str);
	}
}
