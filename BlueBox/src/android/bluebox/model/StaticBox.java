package android.bluebox.model;

import android.app.Activity;

public class StaticBox extends Activity {
	public static final String PWD_MD5 = "bluebox.pwdmd5";
	public static final String PROPERTIES_FILE = "bluebox.properties";
	public static final String KEY_FILE = "bluebox.key";

	public static final String TAG_FILE = "bluebox.tag";
	public static final String SEMANTIC_FILE = "bluebox.semantic";
	public static final String WORKSPACE_FILE = "bluebox.workspace";

	public static final String SYNONYMS_FILE = "bluebox.synonyms";

	public static final String LOG_FILE = "bluebox.log";

	public static PasswordCrypto passwordCrypto;
	public static KeyCrypto keyCrypto;

	public static String currentWorkspace = null;
	public static String currentLocation = null;
	public static String currentNetwork = null;

	/*
	 * Trash value
	 */
	public static final String POINTER_FILE = "bluebox.pointer";
	public static final String DATA_FILE = "bluebox.data";
}
