package android.bluebox.view;

import android.app.Activity;
import android.bluebox.R;
import android.bluebox.model.InitConfiguration;
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
			b = new Bundle();
			b.putString("pwd", pwd);
			b.putString("remind", remind);
			iInitConfiguration.putExtras(b);

			startActivityForResult(iInitConfiguration, RESULT_OK);
		}
	};

	private OnClickListener back = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			setResult(RESULT_OK);
			finish();
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == RESULT_OK) //all done
		{
			this.setResult(RESULT_OK);
			this.finish();
		}
	}
}
