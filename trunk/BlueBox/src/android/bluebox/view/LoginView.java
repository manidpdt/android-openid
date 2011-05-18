package android.bluebox.view;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluebox.R;
import android.bluebox.model.KeyCrypto;
import android.bluebox.model.NetworkBox;
import android.bluebox.model.PasswordCrypto;
import android.bluebox.model.StaticBox;
import android.content.Intent;
import android.os.AsyncTask;
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
	
	Intent intent;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		edtPassword = (EditText) findViewById(R.id.edtLoginPassword);
		
		btnLogin = (Button) findViewById(R.id.btnLoginLogin);
		btnLogin.setOnClickListener(login);
		
		btnClear = (Button) findViewById(R.id.btnLoginClear);
		btnClear.setOnClickListener(clear);
		
		intent = new Intent(LoginView.this, WorkspaceListView.class);
	}
	
	private OnClickListener login = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			new Login().execute();	
		}
	};
	
	private OnClickListener clear = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			edtPassword.setText("");
		}
	};
	
	public void toastShow(String str) {
		Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
	}
	
	public class Login extends AsyncTask<Void, Void, Void> {

		private final ProgressDialog dialog = new ProgressDialog(LoginView.this);
		boolean foundHost = false;
		
		protected void onPreExecute() {
			this.dialog.setTitle("Searching server");
			this.dialog.setMessage("Please wait...");
			this.dialog.show();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				StaticBox.passwordCrypto = new PasswordCrypto();
				StaticBox.passwordCrypto.readFile(openFileInput(StaticBox.PWD_MD5));
				String pwd = edtPassword.getText().toString();
				boolean login = StaticBox.passwordCrypto.checkPassword(pwd);
				
				if (login) {
					
					StaticBox.passwordCrypto.setPassword(pwd);
					
					StaticBox.keyCrypto = new KeyCrypto();
					StaticBox.keyCrypto.init(openFileInput(StaticBox.KEY_FILE), StaticBox.passwordCrypto);
					
					toastShow("Successful");
					
					startActivity(intent);
					finish();
				} else {
					toastShow("Unsuccessful");
				}
				
			} catch (FileNotFoundException e) {
				toastShow("File not found");
				e.printStackTrace();
			} catch (Exception e) {
				toastShow("Error");
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onProgressUpdate(final Void unused) {
		}
		
		protected void onPostExecute(final Void unused) {
			if (this.dialog.isShowing())
				this.dialog.dismiss();
		}
	}
}
