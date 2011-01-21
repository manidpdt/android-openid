package android.bluebox.view;

import android.bluebox.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Main extends Activity{
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		Intent iInitPassword = new Intent(Main.this, InitPasswordView.class);
		startActivityForResult(iInitPassword, 0);
	}

	// get result from InitPasswordView
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//
//		if(resultCode == 0) //all done
//		{
//			this.finish();
//		}
//	}
//	
//	public boolean firstTime() {
//		File file = new File ("pwd_md5");
//		return !file.exists();
//	}
}
