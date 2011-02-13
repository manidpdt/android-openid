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
		blueCrypto = new Crypto3(this.password, salt, iterationCount);
	}
	
	public void readFile(FileInputStream fis) throws Exception {
		DataInputStream dis = new DataInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		
		strMD5 = br.readLine();
		salt = br.readLine().getBytes();
		iterationCount = Integer.parseInt(br.readLine());
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
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
		 out.write(iterationCount);
		 
		 out.flush();
		 out.close();
	}
	
	public static String createMD5(String str) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			md5.update(str.getBytes(), 0, str.length());
			String sig = new BigInteger(1, md5.digest()).toString();
			return sig;
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Can not use md5 algorithm");
		}
		return null;
	}
	
	public boolean checkPassword(String password) {
		return (createMD5(password) == strMD5);
	}
	
	public String encrypt(String str) {
		return blueCrypto.encrypt(str);
	}
	
	public String decrypt(String str) {
		return blueCrypto.decript(str);
	}
}
