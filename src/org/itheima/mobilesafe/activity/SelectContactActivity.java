package org.itheima.mobilesafe.activity;

import java.util.List;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.bean.ContactBean;
import org.itheima.mobilesafe.utils.ContactUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SelectContactActivity extends Activity {
	public static final String KEY_NUMBER = "number";
	private ListView mListView;
	private ProgressBar mProgressBar;
	private List<ContactBean> mDatas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_select_contact);

		initView();
		initData();
		initEvent();
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.sc_listview);
		mProgressBar = (ProgressBar) findViewById(R.id.sc_progress);
	}

	private void initData() {

		mProgressBar.setVisibility(View.VISIBLE);
		// 读数据库可能会耗时很长
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 数据库中读出联系人信息，赋给mDatas
				mDatas = ContactUtils.findAll(SelectContactActivity.this);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// UI操作
						mProgressBar.setVisibility(View.GONE);
						// 更新显示，UI操作
						mListView.setAdapter(new ContactAdapter());
					}
				});
			}
		}).start();
	}

	private class ContactAdapter extends BaseAdapter {
		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			if (mDatas != null) {
				return mDatas.get(position);
			}
			return null;
		}

		@Override
		public int getCount() {
			if (mDatas != null) {
				return mDatas.size();
			}
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// if (convertView == null) {// 没有复用
			// convertView = View.inflate(SelectContactActivity.this,
			// R.layout.item_contact, null);
			// }
			//
			// ImageView ivIcon = (ImageView) convertView
			// .findViewById(R.id.item_contact_iv_icon);
			// TextView tvName = (TextView) convertView
			// .findViewById(R.id.item_contact_tv_name);
			// TextView tvNumber = (TextView) convertView
			// .findViewById(R.id.item_contact_tv_number);
			//
			// ContactBean bean = mDatas.get(position);
			//
			// // 设置数据
			// tvName.setText(bean.name);
			// tvNumber.setText(bean.number);
			//
			// return convertView;

			ViewHolder holder;

			if (convertView == null) {
				// 没有复用
				convertView = View.inflate(SelectContactActivity.this,
						R.layout.item_contact, null);
				// 新建holder
				holder = new ViewHolder();

				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_contact_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_contact_tv_name);
				holder.tvNumber = (TextView) convertView
						.findViewById(R.id.item_contact_tv_number);

				// 设置标记 相当于把对象存储起来了进行回收利用，省去了多次findViewById操作的耗时
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ContactBean bean = mDatas.get(position);
			// 设置显示
			holder.tvName.setText(bean.name);
			holder.tvNumber.setText(bean.number);

			Bitmap bitmap = ContactUtils.getContactBitmap(
					SelectContactActivity.this, bean.contactId);
			if (bitmap != null) {
				holder.ivIcon.setImageBitmap(bitmap);
			} else {
				holder.ivIcon.setImageResource(R.drawable.ic_contact);
			}

			return convertView;
		}
	}

	// 存储记录的是item条目中的view
	private class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvNumber;
	}

	private void initEvent() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				ContactBean bean = mDatas.get(position);
				Intent intent = getIntent();
				intent.putExtra(KEY_NUMBER, bean.number);
				setResult(Activity.RESULT_OK, intent);
				
				finish();
			}
		});
	}
}
