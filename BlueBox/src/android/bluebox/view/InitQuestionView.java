package android.bluebox.view;

import android.bluebox.R;
import android.app.Activity;
import android.bluebox.model.InitConfiguration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class InitQuestionView extends Activity {
	
	EditText edtRemind;
	
	Button btnDone;
	Button btnBack;
	
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
			Bundle b = getIntent().getExtras();
			String pwd = b.getString("pwd");
			String remind = edtRemind.getText().toString();
			
			InitConfiguration iConfig = new InitConfiguration();
			iConfig.initConfiguration(pwd, remind);
			setResult(1);
			finish();
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
