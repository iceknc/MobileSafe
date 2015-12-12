package org.itheima.mobilesafe.view;

import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.db.AddressBean;
import org.itheima.mobilesafe.utils.Constants;
import org.itheima.mobilesafe.utils.PreferenceUtils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AddressDialog extends Dialog implements OnItemClickListener {
	private ListView mListView;
	private List<AddressBean> mDatas;

	private final static String[] TITLES = new String[] { "半透明", "活力橙", "卫士蓝",
			"金属灰", "苹果绿" };
	private final static int[] STYLES = new int[] { R.drawable.toast_normal,
			R.drawable.toast_orange, R.drawable.toast_blue,
			R.drawable.toast_grey, R.drawable.toast_green };

	public AddressDialog(Context context) {
		super(context, R.style.AddressDialogStyle);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_address);

		// 设置显示在底部
		LayoutParams attributes = getWindow().getAttributes();
		attributes.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		getWindow().setAttributes(attributes);

		// 初始化View
		mListView = (ListView) findViewById(R.id.dialog_address_lv);

		int style = PreferenceUtils.getInt(getContext(),
				Constants.STYLE_ADDRESS, STYLES[0]);

		mDatas = new ArrayList<AddressBean>();
		for (int i = 0; i < TITLES.length; i++) {
			AddressBean bean = new AddressBean();
			bean.style = STYLES[i];
			bean.title = TITLES[i];
			bean.selected = (style == bean.style);

			mDatas.add(bean);
		}
		mListView.setAdapter(new AddressAdapter());
		mListView.setOnItemClickListener(this);
	}

	private class AddressAdapter extends BaseAdapter {

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
				convertView = View.inflate(getContext(), R.layout.item_address,
						null);
				holder = new ViewHolder();
				convertView.setTag(holder);

				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.item_address_tv_title);
				holder.ivStyle = (ImageView) convertView
						.findViewById(R.id.item_address_iv_style);
				holder.ivSelected = (ImageView) convertView
						.findViewById(R.id.item_address_iv_selected);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			AddressBean bean = mDatas.get(position);
			holder.tvTitle.setText(bean.title);
			holder.ivStyle.setImageResource(bean.style);
			holder.ivSelected.setVisibility(bean.selected ? View.VISIBLE
					: View.GONE);

			return convertView;
		}
	}

	private class ViewHolder {
		TextView tvTitle;
		ImageView ivStyle;
		ImageView ivSelected;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		PreferenceUtils.putInt(getContext(), Constants.STYLE_ADDRESS,
				STYLES[position]);
		
		this.dismiss();
	}

}
