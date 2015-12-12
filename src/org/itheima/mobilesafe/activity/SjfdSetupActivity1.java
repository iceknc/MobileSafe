package org.itheima.mobilesafe.activity;

import org.itheima.mobilesafe.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class SjfdSetupActivity1 extends SjfdBaseSetupActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sjfd_setup1);
	}

	@Override
	protected boolean performPre() {
		return false;
	}

	@Override
	protected boolean performNext() {
		Intent intent = new Intent(this, SjfdSetupActivity2.class);
		startActivity(intent);
		return false;
	}
}
