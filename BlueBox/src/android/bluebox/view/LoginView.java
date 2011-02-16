package android.bluebox.view;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.bluebox.R;
import android.bluebox.model.PasswordCrypto;
import android.bluebox.model.StaticValue;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginView extends Activity {

	EditText edtPassword;
	Button btnLogin;
	Button btnClear;
	
	PasswordCrypto passwordCrypto;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		edtPassword = (EditText) findViewById(R.id.edtLoginPassword);
		
		btnLogin = (Button) findViewById(R.id.btnLoginLogin);
		btnLogin.setOnClickListener(login);
		
		btnClear = (Button) findViewById(R.id.btnLoginClear);
		btnClear.setOnClickListener(clear);
	}
	
	private OnClickListener login = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			try {
				passwordCrypto = new PasswordCrypto();
				passwordCrypto.readFile(openFileInput(StaticValue.PWD_MD5));
				String pwd = edtPassword.getText().toString();
				String strNotice = (passwordCrypto.checkPassword(pwd))? "Correct" : "Incorrect";
				Toast.makeText(getBaseContext(), strNotice, Toast.LENGTH_LONG).show();
			} catch (FileNotFoundException e) {
				Toast.makeText(getBaseContext(), "File not found", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (Exception e) {
				Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			
		}
	};
	
	private OnClickListener clear = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			edtPassword.setText("");
		}
	};
}
