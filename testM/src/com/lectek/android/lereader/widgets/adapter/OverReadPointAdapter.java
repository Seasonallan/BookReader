package com.lectek.android.lereader.widgets.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.data.ReadPointRecord;

public class OverReadPointAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	ViewHolder holder = null;
	private ArrayList<ReadPointRecord> list;
	private Activity mContext;

	public OverReadPointAdapter(Activity context, ArrayList<ReadPointRecord> list2) {
		super();
		this.list = list2;
		inflater = LayoutInflater.from(context);
		this.mContext = context;
	}

	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (list != null && list.size() > position) {
			return list.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup arg2) {
		if (view == null) {
			holder = new ViewHolder();
			view = inflater.inflate(R.layout.adapter_page_over_read_point, null);
			holder.overReadPointTv = (TextView) view
					.findViewById(R.id.overReadPointTv);
			holder.overTimeTv = (TextView) view.findViewById(R.id.overTimeTv);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.overReadPointTv.setText(list.get(position).readPointValue + "");
		holder.overTimeTv.setText(list.get(position).deadline);
		return view;
	}

	class ViewHolder {
		private TextView overReadPointTv;
		private TextView overTimeTv;

	}
}
