package android.bluebox.view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import android.app.Activity;
import android.bluebox.R;
import android.bluebox.model.InitConfiguration;
import android.bluebox.model.StaticValue;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class InitQuestionView extends Activity {

	public static final String AES = "AES";

	EditText edtRemind;

	Button btnDone;
	Button btnBack;

	String strKey;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.initquestion);

		edtRemind = (EditText) findViewById(R.id.edtRemind);

		btnDone = (Button) findViewById(R.id.btnDone);
		btnDone.setOnClickListener(done);

		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(back);
	}

	private OnClickListener done = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			/**Get data from InitQuestionView
			 * pwd: password
			 * remind: Hint to remind password
			 */
			Bundle b = getIntent().getExtras();
			String pwd = b.getString("pwd");
			String remind = edtRemind.getText().toString();

			Intent iInitConfiguration = new Intent(InitQuestionView.this, InitConfiguration.class);

			/**Push password and remind question to InitConfiguration 
			 */
			//			b = new Bundle();
			//			b.putString("pwd", pwd);
			//			b.putString("remind", remind);
			//			iInitConfiguration.putExtras(b);
			//			
			//			startActivity(iInitConfiguration); // 1 means create file
			initConfig(pwd, remind);
		}

		public void initConfig(String pwd, String remind) {
			File keyFile = new File(StaticValue.KEY_FILE);
			if (!keyFile.exists()) {
				try {
					KeyGenerator keyGen = KeyGenerator.getInstance(AES);
					keyGen.init(128);
					SecretKey sk = keyGen.generateKey();
					FileWriter fw = new FileWriter(keyFile);
					fw.write(byteArrayToHexString(sk.getEncoded()));
					fw.flush();
					fw.close();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		private String byteArrayToHexString(byte[] b){
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
	};

	private OnClickListener back = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			finish();
		}
	};
}
