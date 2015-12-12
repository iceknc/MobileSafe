package org.itheima.mobilesafe.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.bean.ContactBean;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactUtils {

	/**
	 * 查询所有联系人信息
	 * 
	 * @param context
	 * @return 联系人对象集合
	 */
	public static List<ContactBean> findAll(Context context) {
		List<ContactBean> list = new ArrayList<ContactBean>();

		// 获得内容解析者
		ContentResolver resolver = context.getContentResolver();
		// 通过号码查询
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		String[] projection = new String[] {
				// 联系人ID
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
				// 联系人名称
				ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				// 联系人电话
				ContactsContract.CommonDataKinds.Phone.NUMBER };

		String sortOrder = null;
		String[] selectionArgs = null;
		String selection = null;

		Cursor cursor = resolver.query(uri, projection, selection,
				selectionArgs, sortOrder);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				long id = cursor.getLong(0);
				String name = cursor.getString(1);
				String number = cursor.getString(2);

				ContactBean bean = new ContactBean();
				bean.contactId = id;
				bean.name = name;
				bean.number = number;

				list.add(bean);
			}
		}

		cursor.close();
		return list;
	}

	/**
	 * 获取联系人头像
	 * 
	 * @param context
	 * @param id
	 * @return
	 */
	public static Bitmap getContactBitmap(Context context, long id) {
		ContentResolver resolver = context.getContentResolver();

		// contont://contact/ 所有的联系人
		// contont://contact/102 查询_id是102的联系人
		Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,
				String.valueOf(id));
		InputStream stream = ContactsContract.Contacts
				.openContactPhotoInputStream(resolver, uri);

		return BitmapFactory.decodeStream(stream);
	}
}
