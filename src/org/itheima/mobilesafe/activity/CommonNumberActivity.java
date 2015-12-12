package org.itheima.mobilesafe.activity;

import java.util.List;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.bean.ChildDataBean;
import org.itheima.mobilesafe.bean.GroupDataBean;
import org.itheima.mobilesafe.db.CommonNumberDao;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.nineoldandroids.animation.ObjectAnimator;

public class CommonNumberActivity extends Activity {
	private ExpandableListView mListView;
	private List<GroupDataBean> mDatas;

	private int mOpenedPosition = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);

		initView();
		initEvent();
		initData();

	}

	private void initView() {
		mListView = (ExpandableListView) findViewById(R.id.cn_listview);
	}

	private void initData() {
		mDatas = CommonNumberDao.getDatas(this);
		mListView.setAdapter(new CommonNumberAdapter());
	}

	private class CommonNumberAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			if (mDatas != null) {
				return mDatas.size();
			}
			return 0;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (mDatas != null) {
				GroupDataBean groupDataBean = mDatas.get(groupPosition);
				List<ChildDataBean> list = groupDataBean.list;

				if (list != null) {
					return list.size();
				}
			}
			return 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			if (mDatas != null) {
				return mDatas.get(groupPosition);
			}
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			if (mDatas != null) {
				GroupDataBean groupDataBean = mDatas.get(groupPosition);
				List<ChildDataBean> list = groupDataBean.list;

				if (list != null) {
					return list.get(childPosition);
				}
			}
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return groupPosition * childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			GroupViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(CommonNumberActivity.this,
						R.layout.item_group, null);

				holder = new GroupViewHolder();
				convertView.setTag(holder);

				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.item_group_tv_title);

			} else {
				holder = (GroupViewHolder) convertView.getTag();
			}

			GroupDataBean bean = mDatas.get(groupPosition);
			holder.tvTitle.setText(bean.title);

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ChildViewHolder holder = null;

			if (convertView == null) {
				convertView = View.inflate(CommonNumberActivity.this,
						R.layout.item_child, null);
				holder = new ChildViewHolder();
				convertView.setTag(holder);

				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_child_tv_name);
				holder.tvNumber = (TextView) convertView
						.findViewById(R.id.item_child_tv_number);

			} else {
				holder = (ChildViewHolder) convertView.getTag();
			}
			GroupDataBean groupDataBean = mDatas.get(groupPosition);
			ChildDataBean childDataBean = groupDataBean.list.get(childPosition);

			holder.tvName.setText(childDataBean.name);
			holder.tvNumber.setText(childDataBean.number);

			// 定义动画
			ObjectAnimator obj = ObjectAnimator.ofFloat(convertView,
					"translationX", 720, 0);
			obj.setDuration(500);
			obj.setInterpolator(new BounceInterpolator());
			obj.start();

			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

	private class GroupViewHolder {
		TextView tvTitle;
	}

	private class ChildViewHolder {
		TextView tvName;
		TextView tvNumber;
	}

	private void initEvent() {
		mListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				if (mOpenedPosition == -1) {
					mListView.expandGroup(groupPosition);
					// 选中group
					mListView.setSelectedGroup(groupPosition);
					// 标记
					mOpenedPosition = groupPosition;
				} else {
					if (mOpenedPosition == groupPosition) {
						mListView.collapseGroup(groupPosition);
						mOpenedPosition = -1;
					} else {
						mListView.collapseGroup(mOpenedPosition);
						mListView.expandGroup(groupPosition);
						mListView.setSelectedGroup(groupPosition);
						mOpenedPosition = groupPosition;
					}
				}
				return true;
			}
		});

		mListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// 获得电话号码
				String number = mDatas.get(groupPosition).list
						.get(childPosition).number;

				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + number));

				startActivity(intent);
				return true;
			}
		});
	}
}
