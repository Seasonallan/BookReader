package com.lectek.lereader.core.pdf;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lectek.lereader.core.pdf.PageView.PageViewCallback;
import com.lectek.lereader.core.pdf.jni.MuPDFCore;

public class PdfPageAdapter extends BaseAdapter {
	
	private final Context mContext;
	private final MuPDFCore mCore;
	private final SparseArray<PointF> mPageSizes = new SparseArray<PointF>();
	private PageViewCallback mPageViewCallback;
	
	public PdfPageAdapter(Context c, MuPDFCore core, PageViewCallback pageViewCallback) {
		mContext = c;
		mCore = core;
		mPageViewCallback = pageViewCallback;
	}

	@Override
	public int getCount() {
		if(mCore != null) {
			return mCore.countPages();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final PdfPageView pageView;
		if(convertView == null) {
			pageView = new PdfPageView(mContext, mCore, new Point(parent.getWidth(), parent.getHeight()), mPageViewCallback);
		} else {
			pageView = (PdfPageView) convertView;
		}
		PointF pageSize = mPageSizes.get(position);
		if (pageSize != null) {
			// We already know the page size. Set it up
			// immediately
			pageView.setPage(position, pageSize);
		} else {
			// Page size as yet unknown. Blank it for now, and
			// start a background task to find the size
			pageView.blank(position);
			AsyncTask<Void,Void,PointF> sizingTask = new AsyncTask<Void,Void,PointF>() {
				@Override
				protected PointF doInBackground(Void... arg0) {
					return mCore.getPageSize(position);
				}

				@Override
				protected void onPostExecute(PointF result) {
					super.onPostExecute(result);
					PdfLog.d("onPostExecute__result.x: " + result.x + " result.y: " + result.y);
					
					if(result.x <= 0 || result.y <= 0) {
						return;
					}
					
					// We now know the page size
					mPageSizes.put(position, result);
					
					// Check that this view hasn't been reused for
					// another page since we started
					if (pageView.getPage() == position)
						pageView.setPage(position, result);
				}
			};
			try
			{
				sizingTask.execute((Void)null);
			}
			catch (java.util.concurrent.RejectedExecutionException e)
			{
				// If we can't do it in the background, just
				// do it in the foreground.
				PointF result = mCore.getPageSize(position);
				PdfLog.d("RejectedExecutionException__result.x: " + result.x + " result.y: " + result.y);
				mPageSizes.put(position, result);
				// Check that this view hasn't been reused for
				// another page since we started
				if (pageView.getPage() == position)
					pageView.setPage(position, result);
			}
		}
		return pageView;
	}

}
