package com.lectek.android.lereader.widgets;

import java.util.ArrayList;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.utils.ClientInfoUtil;
import com.lectek.android.lereader.net.response.SubjectResultInfo;
import com.lectek.android.lereader.widgets.ReaderGallery.OnReStartListener;
import com.lectek.android.lereader.widgets.adapter.CoverSubjectActivityAdapter;
import com.lectek.android.lereader.widgets.adapter.SubjectActivityAdapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class SlidingViewCoverSwitcher extends FrameLayout {

	private Context mContext = null;
	private ArrayList<SubjectResultInfo> specialSubjectInfoList;
	private ReaderGallery subjectActivityRG;
	// private DotProgressBar subjectActivityNumTV;
	protected ImageButton cover_finish_ib;
	protected ImageButton cover_share_ib;
	private CoverSubjectActivityAdapter subjectActivityAdapter;
	private int mSubjectActivityTotalNum = -1;
	private Handler mHandler;
	public CoverOnClick mCoverOnClick;

	public SlidingViewCoverSwitcher(Context context, CoverOnClick mCoverOnClick) {
		super(context);
		mContext = context;
		this.mCoverOnClick = mCoverOnClick;
		specialSubjectInfoList = new ArrayList<SubjectResultInfo>();
		init();
	}

	private void init() {
		mHandler = new Handler(Looper.getMainLooper());
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.slidingview_cover_layout, null);
		addView(view);
		subjectActivityRG = (ReaderGallery) view
				.findViewById(R.id.cover_subject_rg);
		// subjectActivityNumTV = (DotProgressBar)
		// view.findViewById(R.id.subject_activity_num_tv);
		cover_finish_ib = (ImageButton) view.findViewById(R.id.cover_finish_ib);
		cover_finish_ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCoverOnClick.finishClick();
			}
		});
		cover_share_ib = (ImageButton) view.findViewById(R.id.cover_share_ib);
		cover_share_ib.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCoverOnClick.shareClick();
			}
		});
		subjectActivityAdapter = new CoverSubjectActivityAdapter(mContext,
				specialSubjectInfoList);
		subjectActivityRG.setAdapter(subjectActivityAdapter);
		subjectActivityRG
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> adapterView,
							View view, int position, long id) {
						ClientInfoUtil.readerGalleryIndex = position;
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

		subjectActivityRG.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View v,
					int position, long id) {// 专题

				if (position >= specialSubjectInfoList.size()) {
					return;
				}

				SubjectResultInfo info = specialSubjectInfoList.get(position);
				// SpecialSubjectActivity.openSpecialSubjectActivity(mContext,
				// info, true);
			}
		});
		subjectActivityRG.setOnReStartListener(new OnReStartListener() {

			@Override
			public void reStart() {
				mHandler.removeCallbacks(mGalleryChangeRunnable);
				mHandler.postDelayed(mGalleryChangeRunnable, 3000);
			}

		});

		// subjectActivityNumTV.setDotNum(mSubjectActivityTotalNum);
		if (subjectActivityAdapter != null) {
			subjectActivityAdapter.notifyDataSetChanged();
		}
		subjectActivityRG.setSelection(ClientInfoUtil.readerGalleryIndex);

		// subjectActivityNumTV.setCurIndex(ClientInfoUtil.readerGalleryIndex);
		mHandler.postDelayed(mGalleryChangeRunnable, 3000);

	}

	private Runnable mGalleryChangeRunnable = new Runnable() {

		@Override
		public void run() {
			if (mSubjectActivityTotalNum > 0) {
				if (!subjectActivityRG.isTouchNow()) {
					++ClientInfoUtil.readerGalleryIndex;
					if (ClientInfoUtil.readerGalleryIndex >= mSubjectActivityTotalNum) {
						ClientInfoUtil.readerGalleryIndex = 0;
					}
					subjectActivityRG.moveNext();// add by chends 2012-11-13
													// banner循环滚动
				} else {
					subjectActivityRG.setTouchNow(false);
				}
				mHandler.removeCallbacks(mGalleryChangeRunnable);
				mHandler.postDelayed(mGalleryChangeRunnable, 3000);
			}
		}

	};

	public void setData(ArrayList<SubjectResultInfo> data) {
		if (specialSubjectInfoList == null) {
			specialSubjectInfoList = data;
		} else {
			specialSubjectInfoList.clear();
			specialSubjectInfoList.addAll(data);
		}
		if (specialSubjectInfoList != null && specialSubjectInfoList.size() > 0) {
			mSubjectActivityTotalNum = specialSubjectInfoList.size();
			// subjectActivityNumTV.setDotNum(mSubjectActivityTotalNum);
			if (subjectActivityAdapter != null) {
				subjectActivityAdapter.notifyDataSetChanged();
			}
			subjectActivityRG.setSelection(ClientInfoUtil.readerGalleryIndex);

			// subjectActivityNumTV.setCurIndex(ClientInfoUtil.readerGalleryIndex);
			// changeSubjectNum(subjectActivityTotalNum);
			mHandler.postDelayed(mGalleryChangeRunnable, 3000);
		}
	}

	protected void finalize() throws Throwable {
		mHandler.removeCallbacks(mGalleryChangeRunnable);
	}

	public interface CoverOnClick {
		public void shareClick();

		public void finishClick();
	}
}
