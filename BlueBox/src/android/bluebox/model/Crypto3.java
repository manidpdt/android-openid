package android.bluebox.model;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class Crypto3 {

	private Cipher enCipher;
	private Cipher deCipher;
	private static String algorithm = "PBEWithMD5AndDES";

	public Crypto3 (String str, byte[] salt, int iterationCount) throws Exception {
		PBEKeySpec keySpec = new PBEKeySpec(str.toCharArray());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
		SecretKey secretKey = keyFactory.generateSecret(keySpec);
		PBEParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

		enCipher = Cipher.getInstance(algorithm);
		enCipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);

		deCipher = Cipher.getInstance(algorithm);
		deCipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
	}

	public String encrypt(String str) {

		try {
			byte[] input = str.getBytes();
			byte[] output = enCipher.update(input, 0, input.length);
			output = enCipher.doFinal();
			return new String(output);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String decrypt(String str) {

		try {
			byte[] input = str.getBytes();
			byte[] output = deCipher.update(input, 0, input.length);
			output = deCipher.doFinal();
			return new String(output);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String createMD5(String str) {
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
}
