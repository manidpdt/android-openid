package android.bluebox.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Crypto2 {

	public static final String AES = "AES";

	public static String encrypt(String value, File keyFile) throws GeneralSecurityException, IOException{
		
		SecretKeySpec sks = getSecretKeySpec(keyFile);
		Cipher cipher = Cipher.getInstance(AES);
		cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
		byte[] encrypted = cipher.doFinal(value.getBytes());
		return byteArrayToHexString(encrypted);
	}

	public static String decrypted(String message, File keyFile) throws GeneralSecurityException, IOException {
		SecretKeySpec sks = getSecretKeySpec(keyFile);
		Cipher cipher = Cipher.getInstance(AES);
		cipher.init(Cipher.DECRYPT_MODE, sks);
		byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
		return new String(decrypted);
	}

	private static SecretKeySpec getSecretKeySpec(File keyFile) throws GeneralSecurityException, IOException {
		byte[] key = readKeyFile(keyFile);
		SecretKeySpec sks = new SecretKeySpec(key, AES);
		return sks;
	}

	private static byte [] readKeyFile(File keyFile) throws FileNotFoundException {
		Scanner scanner = 
			new Scanner(keyFile).useDelimiter("\\Z");
		String keyValue = scanner.next();
		scanner.close();
		return hexStringToByteArray(keyValue);
	}

	private static String byteArrayToHexString(byte[] b){
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++){
			int v = b[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	private static byte[] hexStringToByteArray(String s) {
		byte[] b = new byte[s.length() / 2];
		for (int i = 0; i < b.length; i++){
			int index = i * 2;
			int v = Integer.parseInt(s.substring(index, index + 2), 16);
			b[i] = (byte)v;
		}
		return b;
	}
}
