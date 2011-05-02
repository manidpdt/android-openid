package android.bluebox.model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.SecureRandom;
import java.util.Random;

public class KeyCrypto {
	
	private String secretKey;
	private byte[] salt = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte) 0xe9, (byte) 0xe0, (byte) 0xae };
	private int iterationCount = 2020;
	
	private Crypto3 blueCrypto;
	
	public void init(FileInputStream fis, PasswordCrypto passwordCrypto) throws Exception {
		DataInputStream dis = new DataInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));

		secretKey = passwordCrypto.decrypt(br.readLine());
//		String strSalt = br.readLine();
//		strSalt = passwordCrypto.decrypt(strSalt);
//		salt = strSalt.getBytes();
//		iterationCount = Integer.parseInt(passwordCrypto.decrypt(br.readLine()));

		blueCrypto = new Crypto3(secretKey, salt, iterationCount);
	}

	public void initFile(FileOutputStream fos, PasswordCrypto passwordCrypto) throws Exception {
		OutputStreamWriter out = new OutputStreamWriter(fos);

		// generate super secret key and encrypt it by using PasswordCrypto
		SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");
		secretKey = new Integer(prng.nextInt()).toString();
		String encryptedKey = passwordCrypto.encrypt("password");
		String tmp = passwordCrypto.decrypt(encryptedKey);
		
		out.write(encryptedKey + "\n");
		
		// generate salt and iterationCount for crypto

//		salt = new byte[8];
//		Random rnd = new Random();
//		rnd.nextBytes(salt);
//		iterationCount = 2020;
//		String strSalt = salt.toString();
//
//		String encryptedSalt = passwordCrypto.encrypt(strSalt);
//		String encryptedIterationCount = passwordCrypto.encrypt(Integer.toString(iterationCount));
//		out.write(encryptedSalt + "\n");
//		out.write(encryptedIterationCount);

		out.flush();
		out.close();
	}
	
	public String getMD5Key() {
		return Crypto3.createMD5(secretKey);
	}

	public String encrypt(String str) {
		return blueCrypto.encrypt(str);
	}

	public String decrypt(String str) {
		return blueCrypto.decrypt(str);
	}
}
