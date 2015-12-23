package org.itheima.mobilesafe.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.bean.TrafficBean;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TrafficActivity extends Activity {
	private ListView mListView;
	private LinearLayout mLoading;

	private List<TrafficBean> mDatas;

	private PackageManager mPm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic);

		initView();
		initData();
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.traffic_listview);
		mLoading = (LinearLayout) findViewById(R.id.include_loading);
	}

	private void initData() {

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				mLoading.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.GONE);
			};

			@Override
			protected Void doInBackground(Void... params) {
				mDatas = new ArrayList<TrafficBean>();

				mPm = getPackageManager();

				List<ApplicationInfo> applications = mPm
						.getInstalledApplications(0);

				for (ApplicationInfo info : applications) {
					int uid = info.uid;
					long sndSize = getSndSize(uid);
					long rcvSize = getRcvSize(uid);

					// 剔除没有使用到网络的程序
					// if (sndSize == 0 && rcvSize == 0) {
					// continue;
					// }
					// TODO:/proc/uid_stat的手机适配问题

					TrafficBean bean = new TrafficBean();

					bean.icon = info.loadIcon(mPm);
					bean.name = info.loadLabel(mPm).toString();
					bean.receive = rcvSize;
					bean.send = sndSize;

					mDatas.add(bean);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mListView.setAdapter(new TrafficAdapter());

				mLoading.setVisibility(View.GONE);
				mListView.setVisibility(View.VISIBLE);
			};
		}.execute();
	}

	private class TrafficAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mDatas != null) {
				return mDatas.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mDatas != null) {
				return mDatas.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(TrafficActivity.this,
						R.layout.item_traffic, null);

				holder = new ViewHolder();
				convertView.setTag(holder);

				holder.mIvIcon = (ImageView) convertView
						.findViewById(R.id.item_traffic_iv_icon);
				holder.mTvName = (TextView) convertView
						.findViewById(R.id.item_traffic_tv_name);
				holder.mTvReceive = (TextView) convertView
						.findViewById(R.id.item_traffic_tv_receive);
				holder.mTvSend = (TextView) convertView
						.findViewById(R.id.item_traffic_tv_send);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			TrafficBean bean = (TrafficBean) getItem(position);

			holder.mIvIcon.setImageDrawable(bean.icon);
			holder.mTvName.setText(bean.name);
			holder.mTvReceive.setText("接收:"
					+ Formatter.formatFileSize(TrafficActivity.this,
							bean.receive));
			holder.mTvSend
					.setText("发送:"
							+ Formatter.formatFileSize(TrafficActivity.this,
									bean.send));
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView mIvIcon;
		TextView mTvName;
		TextView mTvSend;
		TextView mTvReceive;
	}

	private long getRcvSize(int uid) {
		File file = new File("/proc/uid_stat/" + uid + "/tcp_rcv");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			return Long.valueOf(br.readLine());
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
	}

	private long getSndSize(int uid) {
		File file = new File("/proc/uid_stat/" + uid + "/tcp_snd");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			return Long.valueOf(br.readLine());
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
	}
}
