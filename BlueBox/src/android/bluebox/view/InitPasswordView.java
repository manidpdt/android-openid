package android.bluebox.view;

import android.app.Activity;
import android.bluebox.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class InitPasswordView extends Activity {

	EditText edtInitPwd;
	EditText edtConfirmPwd;

	Button btnNext;
	Button btnClear;

	Intent iQuesion;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.initpassword);

		edtInitPwd = (EditText) findViewById(R.id.edtInitPwd);
		edtConfirmPwd = (EditText) findViewById(R.id.edtConfirmPwd);

		btnNext = (Button) findViewById(R.id.btnNext);
		btnNext.setOnClickListener(initAllData);

		btnClear = (Button) findViewById(R.id.btnClear);
		btnClear.setOnClickListener(clearEditText);

		iQuesion = new Intent(InitPasswordView.this, InitQuestionView.class);

	}

	private OnClickListener initAllData = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// s1 is password
			String s1 = edtInitPwd.getText().toString();
			
			// if the password is blank
			if (s1.equals("")) {
				Toast.makeText(getBaseContext(), "You must enter new password", 
						Toast.LENGTH_LONG).show();
				return;
			}

			// get the confirmed password
			String s2 = edtConfirmPwd.getText().toString();
			
			// if they do not match together
			if (!s1.equals(s2)) {
				Toast.makeText(getBaseContext(), "Your passwords does not match together",
						Toast.LENGTH_LONG).show();
			} else {
				
				// pass the password to InitQuestionView
				Bundle b = new Bundle();
				b.putString("pwd", s1);
				iQuesion.putExtras(b);
				
				// start InitQuestionView
				startActivityForResult(iQuesion, 0);
			}
		}
	};

	private OnClickListener clearEditText = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			
			// set both EditView are blank
			edtInitPwd.setText("");
			edtConfirmPwd.setText("");
		}
	};

	// get result from InitQuestionView
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == 1) //all done
		{
			this.finish();
		}
	}
}