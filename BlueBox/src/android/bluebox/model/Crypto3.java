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
			/* 
			 * tmp dung de luu tam data bi ma hoa, so luong toi
			 * da la 1024. Sau khi ma hoa, so luong byte thua se
			 * bi cat di va luu vao result. 
			 */
			byte[] tmp = new byte[1024];
			int pos = 0;
			
			/*
			 * Convert chuoi str sang dang byte
			 */
			byte[] input = str.getBytes();
			
			/*
			 * Ma hoa input thanh output
			 */
			byte[] output = enCipher.update(input, 0, input.length);
			
			/*
			 * Neu output != null thi copy vao tmp
			 */
			if (output != null) {
				pos = output.length;
				for (int i = 0; i < pos; i++) {
					tmp[i] = output[i];
				}
			}
			
			/*
			 * Ket thuc ma hoa
			 */
			output = enCipher.doFinal();
			
			/*
			 * Neu output != null thi copy vao tmp
			 */ 
			if (output != null) {
				for (int i = 0; i < output.length; i++) {
					tmp[i+pos] = output[i];
				}
				pos+=output.length;
			}
			
			/*
			 * Copy du lieu tu tmp sang result, xoa het
			 * nhung byte thua.
			 */
			byte[] result = new byte[pos];
			for (int i = 0; i < pos; i++) {
				result[i] = tmp[i];
			}
			
			/*
			 * Ma hoa sang Base64 de co the luu xuong file
			 */
			String strBase64 = Base64.encodeToString(result, Base64.DEFAULT);
			return strBase64;
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

		String result = "";
		try {
			/*
			 * Giai ma base64 chuoi str, luu vao input
			 */
			byte[] input = Base64.decode(str, Base64.DEFAULT);
			
			/*
			 * Giai ma input thanh output
			 */
			byte[] output = deCipher.update(input, 0, input.length);
			
			/*
			 * Neu output != null thi convert sang dang chuoi
			 */
			if (output != null) {
				result += new String(output);
			}
			
			/*
			 * Ket thuc giai ma
			 */
			output = deCipher.doFinal();
			/*
			 * Neu output != null thi convert sang dang chuoi
			 */
			if (output != null) {
				result += new String(output);
			}
			return result;
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