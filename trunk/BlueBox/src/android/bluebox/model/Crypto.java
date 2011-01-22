package android.bluebox.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Crypto {

	// We use cipher to encrypt and decrypt data
	private Cipher ecipher;
	private Cipher dcipher;
	// the algorithm to crypt
	private String transformation = "AES/CBC/PKCS5Padding";
	byte[] buf = new byte[1024];

	public Crypto(String password) {
		
		// Create an 8-byte initialization vector
		byte[] iv = new byte[] {
				0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09,0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f
		};

		AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
		try {
			
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(128);
			SecretKey key = keygen.generateKey();
			
			ecipher = Cipher.getInstance(transformation);
			dcipher = Cipher.getInstance(transformation);

			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// encrypt the string in and write to file
	public void encryptStringToFile(String in, OutputStream out) {
		try {
			out = new CipherOutputStream(out, ecipher);
			buf = in.getBytes();
			int numRead = in.length();
			if (numRead >= 0) {
				out.write(buf, 0, numRead);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// decrypt the data from file in and return correct string
	public String decryptFiletoString(InputStream in) {
		String out = null;
		try {
			in = new CipherInputStream(in, dcipher);
			in.read(buf);
			out = new String(buf);
			return out;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out;
	}
}
