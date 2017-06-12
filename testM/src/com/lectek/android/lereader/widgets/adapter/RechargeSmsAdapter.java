package com.lectek.android.lereader.widgets.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.ui.pay.RechargeSmsActivity.RechargeSms;
import com.lectek.android.lereader.utils.FontUtil;
import com.lectek.android.lereader.widgets.ReadPointCoupons;



public class RechargeSmsAdapter extends BaseAdapter{
	private LayoutInflater mInflater;
	private ArrayList<RechargeSms> mRechargeSmsList;
	private Context mContext;
	
	public RechargeSmsAdapter(Context context, ArrayList<RechargeSms> mRechargeSmsList) {
		super();
		this.mContext = context;
		this.mRechargeSmsList = mRechargeSmsList;
		this.mInflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public int getCount() {
		if (null == mRechargeSmsList) {
			return 0;
		}
		return mRechargeSmsList.size();
	}

	@Override
	public Object getItem(int position) {
		if (null != mRechargeSmsList && position < mRechargeSmsList.size()) {
			return mRechargeSmsList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = newView();
			viewHolder = new ViewHolder();
//			viewHolder.readPointTV = (TextView) convertView.findViewById(R.id.readticket_count_tv);
			viewHolder.readPointIV = (ReadPointCoupons) convertView.findViewById(R.id.recharge_sms_item_img);
			viewHolder.priceTV = (TextView) convertView.findViewById(R.id.sms_price_tv);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		RechargeSms item = mRechargeSmsList.get(position);
//		viewHolder.readPointTV.setText(mContext.getString(R.string.account_convent_point, item.mReadticketCount));
		String pointPrice = mContext.getString(R.string.book_content_price, item.mSmsPrice);
		SpannableStringBuilder ssBuilder = FontUtil.textSpannableStringBuilder(false, pointPrice, mContext.getResources().getColor(R.color.red), 3, pointPrice.length() - 1);
		viewHolder.priceTV.setText(ssBuilder);
		int resId = item.mReadtickBg;
		viewHolder.readPointIV.setBackgroundResource(R.drawable.read_point_default_bg);
		viewHolder.readPointIV.setPadding(10, 15, 10, 15);
		viewHolder.readPointIV.setReadPoint(Integer.valueOf(item.mReadticketCount));
		return convertView;
	}
	
	private View newView() {
		return mInflater.inflate(R.layout.recharge_sms_item, null);
	}

	private class ViewHolder {
		// 阅点
//		TextView readPointTV;
		// 价格
		ReadPointCoupons readPointIV;
		TextView priceTV;
	}
}
