/* http://www.java2s.com/Code/Java/Security/ExampleofusingPBEwithaPBEParameterSpec.htm
 * 
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class PBECrypto {
	private static String filename;
	private static String password;
	private static FileInputStream inFile;
	private static FileOutputStream outFile;

	public static void main (String args[]) throws Exception {
		filename = "clear.txt";
		String password = "helloworld";

		inFile = new FileInputStream(filename);
		outFile = new FileOutputStream(filename + ".des");

		// Use PBEKeySpec to create a key based on a password.
		// The password is passed as a character array

		PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		SecretKey passwordKey = keyFactory.generateSecret(keySpec);

		byte[] salt = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte) 0xe9, (byte) 0xe0, (byte) 0xae };
		int iterationCount = 2048;

		Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
		cipher.init(Cipher.ENCRYPT_MODE, passwordKey, new PBEParameterSpec(salt, iterationCount));

		byte[] input = new byte[64];
		int bytesRead;
		while ((bytesRead = inFile.read(input)) != -1)
		{
			byte[] output = cipher.update(input, 0, bytesRead);
			if (output != null) outFile.write(output);
		}

		byte[] output = cipher.doFinal();
		if (output != null) outFile.write(output);

		inFile.close();
		outFile.flush();
		outFile.close();
	}
}
