package com.lectek.android.lereader.widgets;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lectek.android.LYReader.R;

/**
 * 自定义AlertDialog
 * 
 * @author mingkg21
 * @date 2010-5-4
 * @email mingkg21@gmail.com
 */
public class CustomAlertPagerDialog extends Dialog {

	private Builder builder;
	private Button preBtn;
	private Button nextBtn;

	private CustomAlertPagerDialog(Builder builder, int styleId) {
		super(builder.context, styleId);
		this.builder = builder;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (builder.isShort) {
			setContentView(R.layout.dialog_alert_short);
			if (builder.view instanceof ListView) {
				ListAdapter mListAdapter = ((ListView) builder.view)
						.getAdapter();
				if (mListAdapter != null && mListAdapter.getCount() > 0) {
					findViewById(R.id.dialog_line_gone).setVisibility(
							View.VISIBLE);
				}
			}
		} else {
			setContentView(R.layout.dialog_alert_long_pager);
		}
		builder.dialog = this;

		TextView titleTV = (TextView) findViewById(R.id.dialog_title);
		Button negativeBtn = (Button) findViewById(R.id.negative_btn);
		preBtn = (Button) findViewById(R.id.pre_btn);
		nextBtn = (Button) findViewById(R.id.next_btn);

		FrameLayout content = (FrameLayout) findViewById(R.id.dialog_content);

		if (TextUtils.isEmpty(builder.title)) {
			titleTV.setVisibility(View.GONE);
			// findViewById(R.id.dialog_divider).setVisibility(View.GONE);
		} else {
			titleTV.setText(builder.title);
		}
		if (builder.negativeListener != null) {
			negativeBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					builder.negativeListener.onClick(v, 0);
					dismiss();
				}
			});
		} else {
			negativeBtn.setVisibility(View.INVISIBLE);
		}

		if (builder.totalPage > 1) {
			if (builder.curPageIndex <= 1) {
				setPreButtonEnabled(false);
				setNexButtonEnabled(true);
			} else if ((builder.curPageIndex >= builder.totalPage)) {
				setNexButtonEnabled(false);
				setPreButtonEnabled(true);
			} else {
				setNexButtonEnabled(true);
				setPreButtonEnabled(true);
			}
		} else {
			setPreButtonEnabled(false);
			setNexButtonEnabled(false);
		}

		preBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.preListener.onClick(v, 0);
				if (builder.curPageIndex <= 1) {
					setPreButtonEnabled(false);
				}
				setNexButtonEnabled(true);
			}
		});

		nextBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				builder.nextListener.onClick(v, 0);
				if (builder.curPageIndex >= builder.totalPage) {
					setNexButtonEnabled(false);
				}
				setPreButtonEnabled(true);
			}
		});

		if (builder.view != null) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			lp.bottomMargin = 10;
			content.addView(builder.view, lp);
		}
	}

	public void setPreButtonEnabled(boolean enabled) {
		if (preBtn != null) {
			preBtn.setEnabled(enabled);
		}
	}

	public void setNexButtonEnabled(boolean enabled) {
		if (nextBtn != null) {
			nextBtn.setEnabled(enabled);
		}
	}

	public static class Builder {
		protected boolean isShort = true;// 标示是长Dialog，还是短的,将匹配不同xml
		protected Context context;
		protected CharSequence title = "";
		protected View view;
		protected CustomDialogInterface.OnClickListener negativeListener;
		protected CustomDialogInterface.OnClickListener preListener;
		protected CustomDialogInterface.OnClickListener nextListener;
		protected ArrayList<String> itemsList;
		protected int textId = -1;
		protected CustomAlertPagerDialog dialog;
		protected BaseAdapter mArrayAdapter;
		protected int curPageIndex = 1;
		protected int pageDelta = 10;
		protected int totalPage;

		public Builder(Context context) {
			this.context = context;
		}

		public Builder setTitle(CharSequence title) {
			this.title = title;
			return this;
		}

		public Builder setTitle(int resId) {
			this.title = context.getString(resId);
			return this;
		}

		public Builder setView(View view) {
			this.view = view;
			return this;
		}

		public Builder setNegativeButton(
				CustomDialogInterface.OnClickListener listener) {
			this.negativeListener = listener;
			return this;
		}

		public Builder setPreButton(
				CustomDialogInterface.OnClickListener listener) {
			this.preListener = listener;
			return this;
		}

		public Builder setNextButton(
				CustomDialogInterface.OnClickListener listener) {
			this.nextListener = listener;
			return this;
		}

		public CustomAlertPagerDialog create() {
			return new CustomAlertPagerDialog(this, R.style.CustomDialog);
		}

		private ArrayList<String> getPageList(ArrayList<String> list,
				int startIndex, int pageDelta, String[] items) {
			list.clear();
			int totalLength = items.length;
			int length = startIndex + pageDelta;
			if (length > totalLength) {
				length = totalLength;
			}
			for (int i = startIndex; i < length; i++) {
				list.add(items[i]);
			}
			return list;
		}

		private int getPageIndexByItemPosition(int itemPosition, int pageDelta) {
			int index = (int) Math.ceil(((float) itemPosition) / pageDelta);
			if (index < 1) {
				index = 1;
			}
			return index;
		}

		private int getStartIndex(int pageIndex, int pageDelta) {
			return (pageIndex - 1) * pageDelta;
		}

		public Builder setItems(final String[] items,
				final int selectedItemPosition, final int pageDelta,
				final CustomDialogInterface.OnClickListener listener) {
			isShort = false;
			itemsList = new ArrayList<String>(pageDelta);
			totalPage = (int) Math.ceil(((float) items.length) / pageDelta);
			final ArrayList<String> tempPageList = new ArrayList<String>(
					pageDelta);

			curPageIndex = getPageIndexByItemPosition(selectedItemPosition,
					pageDelta);
			itemsList.addAll(getPageList(tempPageList, getStartIndex(
					curPageIndex, pageDelta), pageDelta, items));

			mArrayAdapter = new ArrayAdapter<String>(context,
					R.layout.dialog_alert_list_item, itemsList);
			final ListView mListView = new ListView(context);
			mListView.setAdapter(mArrayAdapter);
			mListView.setDivider(context.getResources().getDrawable(
					R.drawable.line));
			mListView.setCacheColorHint(Color.TRANSPARENT);
			setView(mListView);
			mListView.setAdapter(mArrayAdapter);
			mListView.setFocusable(true);
			mListView.setFocusableInTouchMode(true);
			mListView.setSelection((selectedItemPosition + pageDelta)
					% pageDelta);
			mListView.requestFocus();
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long id) {
					listener.onClick(view, (curPageIndex - 1) * pageDelta
							+ position);
					if (dialog != null) {
						dialog.dismiss();
					}
				}
			});
			setPreButton(new CustomDialogInterface.OnClickListener() {

				@Override
				public void onClick(View view, int which) {
					curPageIndex--;
					if (curPageIndex < 1) {
						curPageIndex = 1;
					}
					itemsList.clear();
					itemsList.addAll(getPageList(tempPageList, getStartIndex(
							curPageIndex, pageDelta), pageDelta, items));
					mArrayAdapter.notifyDataSetChanged();
					mListView.setSelection(0);// 将滚动条拉到最上端
				}
			});
			setNextButton(new CustomDialogInterface.OnClickListener() {

				@Override
				public void onClick(View view, int which) {
					curPageIndex++;
					if (curPageIndex > totalPage) {
						curPageIndex = totalPage;
					}
					itemsList.clear();
					itemsList.addAll(getPageList(tempPageList, getStartIndex(
							curPageIndex, pageDelta), pageDelta, items));
					mArrayAdapter.notifyDataSetChanged();
					mListView.setSelection(0);
				}
			});

			return this;
		}
	}
}
