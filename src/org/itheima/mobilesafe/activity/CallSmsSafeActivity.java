package org.itheima.mobilesafe.activity;

import java.util.List;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.bean.BlackBean;
import org.itheima.mobilesafe.db.BlackDao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CallSmsSafeActivity extends Activity implements
		OnItemClickListener, OnScrollListener {
	private static final int REQUEST_UPDATE = 0x0002;
	private static final int REQUEST_ADD = 0x0001;

	private ListView mListView;
	private ImageView mIvEmpty;
	private LinearLayout mLlprogress;

	private List<BlackBean> mDatas;
	private BlackDao mDao;
	private CallSmsAdapter mAdapter;

	private int pageSize = 20;
	/**
	 * 用来标记是否加载更多
	 */
	private boolean isLoadMore = false;
	/**
	 * 用来标记是否有更多的数据
	 */
	private boolean hasMoreData = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_callsms_safe);

		mDao = new BlackDao(this);

		initView();
		initEvent();
		initData();
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.css_listview);
		mIvEmpty = (ImageView) findViewById(R.id.css_iv_empty);
		mLlprogress = (LinearLayout) findViewById(R.id.css_ll_progress);
	}

	private void initEvent() {
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
	}

	private void initData() {
		// 显示加载进度
		mLlprogress.setVisibility(View.VISIBLE);
		// 数据库读取放子线程操作
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// 查询数据
				// mDatas = mDao.queryAll();
				// 分页查询
				mDatas = mDao.queryPart(pageSize, 0);

				// UI操作
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// 隐藏加载进度
						mLlprogress.setVisibility(View.GONE);
						// 设置数据
						mAdapter = new CallSmsAdapter();
						mListView.setAdapter(mAdapter);
						// 设置无数据时显示的图片
						mListView.setEmptyView(mIvEmpty);
					}
				});
			}
		}).start();
	}

	public void clickAdd(View view) {
		// 页面跳转，返回添加的信息
		Intent intent = new Intent(this, BlackEditActivity.class);
		// 设置跳转的页面
		intent.setAction(BlackEditActivity.ACTION_ADD);
		startActivityForResult(intent, REQUEST_ADD);
	}

	private class CallSmsAdapter extends BaseAdapter {

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
				convertView = View.inflate(CallSmsSafeActivity.this,
						R.layout.item_callsms, null);
				// 新建holder
				holder = new ViewHolder();
				// 给holder赋值
				holder.tvNumber = (TextView) convertView
						.findViewById(R.id.item_callsms_tv_number);
				holder.tvType = (TextView) convertView
						.findViewById(R.id.item_callsms_tv_type);
				holder.ivDelete = (ImageView) convertView
						.findViewById(R.id.item_callsms_iv_delete);
				// 设置标记
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			// 加载数据
			final BlackBean bean = mDatas.get(position);
			// 设置显示
			holder.tvNumber.setText(bean.number);

			switch (bean.type) {
			case BlackBean.TYPE_CALL:
				holder.tvType.setText("电话拦截");
				break;
			case BlackBean.TYPE_SMS:
				holder.tvType.setText("短信拦截");
				break;
			case BlackBean.TYPE_ALL:
				holder.tvType.setText("电话+短信拦截");
				break;
			default:
				break;
			}

			holder.ivDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					boolean delete = mDao.delete(bean.number);
					if (delete) {
						// 内存删除
						mDatas.remove(bean);
						// 更新UI
						mAdapter.notifyDataSetChanged();

						Toast.makeText(CallSmsSafeActivity.this, "删除成功",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(CallSmsSafeActivity.this, "删除失败",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			return convertView;
		}
	}

	private class ViewHolder {
		TextView tvNumber;
		TextView tvType;
		ImageView ivDelete;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUEST_ADD == requestCode) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				// 返回数据成功
				String number = data
						.getStringExtra(BlackEditActivity.KEY_NUMBER);
				int type = data.getIntExtra(BlackEditActivity.KEY_TYPE,
						BlackBean.TYPE_NONE);

				// 添加数据到集合中
				BlackBean bean = new BlackBean();
				bean.number = number;
				bean.type = type;
				mDatas.add(bean);

				// 更新UI
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		} else if (REQUEST_UPDATE == requestCode) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				// 返回数据成功
				int type = data.getIntExtra(BlackEditActivity.KEY_TYPE,
						BlackBean.TYPE_NONE);
				int position = data.getIntExtra(BlackEditActivity.KEY_POSITION,
						-1);
				// 修改集合里的数据
				mDatas.get(position).type = type;
				// 更新UI
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * ListView的条目的点击事件
	 * 
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 页面带BlackBean的信息跳转，返回修改后的信息
		BlackBean bean = mDatas.get(position);

		Intent intent = new Intent(this, BlackEditActivity.class);
		// 指定跳转更新
		intent.setAction(BlackEditActivity.ACITON_UPDATE);

		intent.putExtra(BlackEditActivity.KEY_NUMBER, bean.number);
		intent.putExtra(BlackEditActivity.KEY_TYPE, bean.type);
		intent.putExtra(BlackEditActivity.KEY_POSITION, position);
		startActivityForResult(intent, REQUEST_UPDATE);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 滚动状态改变时的回调
		// public static int SCROLL_STATE_IDLE = 0; 闲置空闲状态
		// public static int SCROLL_STATE_TOUCH_SCROLL = 1; 触摸滚动状态
		// public static int SCROLL_STATE_FLING = 2; 惯性滑动
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		loadMore();
	}

	private void loadMore() {
		if (mAdapter == null) {
			return;
		}
		// 当前正在加载更多
		if (isLoadMore == true) {
			return;
		}
		// 没有更多的数量
		if (!hasMoreData) {
			return;
		}

		// 最后一个可见时
		int lastVisiblePosition = mListView.getLastVisiblePosition();
		if (lastVisiblePosition == mAdapter.getCount() - 1) {
			isLoadMore = true;

			// 需要加载更多的数据
			mLlprogress.setVisibility(View.VISIBLE);
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					final List<BlackBean> part = mDao.queryPart(pageSize,
							mDatas.size());

					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (part.size() < pageSize) {
								hasMoreData = false;
								Toast.makeText(CallSmsSafeActivity.this,
										"没有更多数据", Toast.LENGTH_SHORT).show();
							} else {
								hasMoreData = true;
							}

							// 添加查询出来的数据
							mDatas.addAll(part);

							mLlprogress.setVisibility(View.GONE);

							mAdapter.notifyDataSetChanged();
							isLoadMore = false;
						}
					});
				}
			}).start();
		}
	}
}
