package org.itheima.mobilesafe.activity;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.business.ProgressProvider;
import org.itheima.mobilesafe.view.ProgressStateView;

import android.app.Activity;
import android.os.Bundle;

public class ProgressManagerActivity extends Activity {
	private ProgressStateView mPsvProgress;
	private ProgressStateView mPsvMemory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress_manager);

		initView();
		initData();
	}

	private void initView() {
		mPsvProgress = (ProgressStateView) findViewById(R.id.pm_psv_progress);
		mPsvMemory = (ProgressStateView) findViewById(R.id.pm_psv_memory);

	}

	private void initData() {
		mPsvProgress.setTextLeft("正在运行"
				+ ProgressProvider.getRunningProgressCount(this) + "个");
	}
}
