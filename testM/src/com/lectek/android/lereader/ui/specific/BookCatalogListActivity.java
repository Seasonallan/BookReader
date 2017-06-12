package com.lectek.android.lereader.ui.specific;

import java.util.ArrayList;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.binding.model.contentinfo.BookCatalogListViewModel;
import com.lectek.android.lereader.binding.model.contentinfo.BookCatalogListViewModel.UserActionListener;
import com.lectek.android.lereader.net.response.BookCatalog;
import com.lectek.android.lereader.ui.basereader_leyue.BaseReaderActivityLeyue;
import com.lectek.android.lereader.ui.basereader_leyue.Book;
import com.lectek.android.lereader.ui.common.BaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 书籍目录列表
 * @author Shizq
 *
 */
public class BookCatalogListActivity extends BaseActivity{
	
	public static final String EXTRA_BOOK_ID = "extra_book_id";
	public static final String EXTRA_BOOK_INFO = "extra_book_info";
	public static final String EXTRA_BOOK_ORDER = "extra_booK_order";
	public static final String EXTRA_BOOK_IS_SURFINGREADER = "extra_book_is_surfingreader";
	
	public static final String NEED_BUY_POINT = "1";

	private BookCatalogListViewModel mBookCatalogListViewModel;

	private ListView mCatalogListView;
	private ArrayList<BookCatalog> mList;

	private CatalogAdapter mCatalogAdapter;

	private Book mBookInfo;
	private boolean mHadOrder;
	private boolean mIsSurfingReader;
	
	public static void openActivity(Context context, String bookId, String key, Book book, boolean hadOrder){
		Intent intent = new Intent(context, BookCatalogListActivity.class);
		intent.putExtra(EXTRA_BOOK_ID, bookId);
		intent.putExtra(EXTRA_BOOK_INFO, book);
		intent.putExtra(EXTRA_BOOK_ORDER, hadOrder);
		((Activity)context).startActivityForResult(intent, ContentInfoActivityLeyue.REQUEST_CODE_GOTOCATALOGLIST);
	}
	
	public static void openActivity(Context context, String bookId, boolean isSurfingReader, Book book, boolean hadOrder){
		Intent intent = new Intent(context, BookCatalogListActivity.class);
		intent.putExtra(EXTRA_BOOK_ID, bookId);
		intent.putExtra(EXTRA_BOOK_INFO, book);
		intent.putExtra(EXTRA_BOOK_ORDER, hadOrder);
		intent.putExtra(EXTRA_BOOK_IS_SURFINGREADER, isSurfingReader);
		((Activity)context).startActivityForResult(intent, ContentInfoActivityLeyue.REQUEST_CODE_GOTOCATALOGLIST);
	}
    View footerView;
	@Override
	protected View newContentView(Bundle savedInstanceState) {
		String bookId = getIntent().getStringExtra(EXTRA_BOOK_ID);
		mBookInfo = (Book)getIntent().getSerializableExtra(EXTRA_BOOK_INFO);
		mHadOrder = getIntent().getBooleanExtra(EXTRA_BOOK_ORDER, false);
		mIsSurfingReader = getIntent().getBooleanExtra(EXTRA_BOOK_IS_SURFINGREADER, false);
		
		mBookCatalogListViewModel = new BookCatalogListViewModel(this, this, bookId);
//		mBookCatalogListViewModel.bFootLoadingVisibility.subscribe(this);
//		mFootLoadingView = getLayoutInflater().inflate(R.layout.loading_data_lay, null);
//		mFootLoadingLay = new FrameLayout(this);
		mBookCatalogListViewModel.setIsSurfingReader(mIsSurfingReader);
		mBookCatalogListViewModel.setUserActionListener(new UserActionListener() {
			
			@Override
			public void setCatalogListInfo(ArrayList<BookCatalog> list) {
				setCatalogList(list);
			}
		});
		
		View view = bindView(R.layout.reader_catalog_tab_item_lay, null, mBookCatalogListViewModel);
		
		mCatalogListView = (ListView)view.findViewById(R.id.reader_catalog_lv);
		mList = new ArrayList<BookCatalog>();

        if(mCatalogListView.getFooterViewsCount() == 0){
            footerView = LayoutInflater.from(this).inflate(R.layout.reader_catalog_item_leyue, null);
            TextView textView = (TextView) footerView.findViewById(R.id.catalog_title_index_tv);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            textView.setGravity(Gravity.CENTER);
            textView.setText(R.string.no_more);
            mCatalogListView.addFooterView(footerView);
        }
		mCatalogAdapter = new CatalogAdapter();
		mCatalogListView.setAdapter(mCatalogAdapter);

		mCatalogListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
                if(mList == null || mList.size() == 0 || position >= mList.size()){
                    return;
                }
				if(mBookInfo != null){
					if(mIsSurfingReader){
						gotoCEBBook(position);
					}else{
						gotoLeReaderBook(position);
					}
				}
			}
		});
		return view;
	}
	
	private void gotoCEBBook(int position){
        BookCatalog bookCatalog = mList.get(position);
		if(bookCatalog.getCalpoint().equals(NEED_BUY_POINT) && !mHadOrder){
			setResult(ContentInfoActivityLeyue.RESULT_CODE_CATALOG_NEED_BUY);
			finish();
		}else{
			BaseReaderActivityLeyue.openActivity(BookCatalogListActivity.this, mBookInfo, position,  0);
			finish();
		}
	}

	private void gotoLeReaderBook(int position){
        BookCatalog bookCatalog = mList.get(position);
		//如果是购买章节，直接关闭目录界面回到书籍详情，并且打开购买窗口
		if(bookCatalog.getCalpoint().equals(NEED_BUY_POINT) && !mHadOrder){
			setResult(ContentInfoActivityLeyue.RESULT_CODE_CATALOG_NEED_BUY);
			finish();
		}else{
			BaseReaderActivityLeyue.openActivity(BookCatalogListActivity.this, mBookInfo, position, 0);
			finish();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBookCatalogListViewModel.onStart();
		setTitleContent(getResources().getString(R.string.reader_menu_title_catalog));
	}
	
	private void setCatalogList(ArrayList<BookCatalog> list){
//		mList.clear();
		if(list == null)
			return;
		if (list.size() <= 20 && footerView.getParent() != null && mCatalogListView.getFooterViewsCount() > 0){
            mCatalogListView.removeFooterView(footerView);
        }
		mList.addAll(list);
		mCatalogAdapter.notifyDataSetChanged();
	}
	
	public class CatalogAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder = null;
			if(convertView == null){
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(BookCatalogListActivity.this).inflate(R.layout.reader_catalog_item_leyue, null);
				viewHolder.titileTV = (TextView)convertView.findViewById(R.id.catalog_title_tv);
				viewHolder.titileIndexTV = (TextView)convertView.findViewById(R.id.catalog_title_index_tv);
				viewHolder.lockIV = (ImageView)convertView.findViewById(R.id.catalog_pay_iv);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			BookCatalog bookCatalog = (BookCatalog)getItem(position);
//			if(bookCatalog.getPageIndex() != null){
//				viewHolder.titileIndexTV.setText(String.valueOf(catalog.getPageIndex()));
//			}else{
//				viewHolder.titileIndexTV.setText("");
//			}
//			if(catalog.getBgColor() != null){
//				viewHolder.titileTV.setBackgroundColor(catalog.getBgColor());
//				viewHolder.titileIndexTV.setBackgroundColor(catalog.getBgColor());
//			}else{
//				viewHolder.titileTV.setBackgroundDrawable(null);
//				viewHolder.titileIndexTV.setBackgroundDrawable(null);
//			}
//			if(catalog.getTextColor() != null){
//				viewHolder.titileTV.setTextColor(catalog.getTextColor());
//				viewHolder.titileIndexTV.setTextColor(catalog.getTextColor());
//			}else{
//				viewHolder.titileTV.setTextColor(getContext().getResources().getColor(R.color.common_black_6));
//				viewHolder.titileIndexTV.setTextColor(getContext().getResources().getColor(R.color.common_black_6));
//			}
//			float textSize = DimensionsUtil.DIPToPX(11);
//			for (int i = 1; i < catalog.getLayer(); i++) {
//				textSize *= 0.85f;
//			}
//			int paddingLeft = DimensionsUtil.DIPToPX(10) * catalog.getLayer();
//			viewHolder.titileTV.setPadding(paddingLeft, convertView.getPaddingTop()
//					, convertView.getPaddingRight(), convertView.getPaddingBottom());
			viewHolder.titileTV.setText(bookCatalog.getName());
			if(bookCatalog.getCalpoint().equals(NEED_BUY_POINT) && !mHadOrder){
				viewHolder.lockIV.setVisibility(View.VISIBLE);
			}else{
				viewHolder.lockIV.setVisibility(View.INVISIBLE);
			}
//			viewHolder.titileTV.setTextSize(textSize);
			return convertView;
		}
		
		private class ViewHolder{
			TextView titileTV;
			TextView titileIndexTV;
			ImageView lockIV;
		}
		
	}

//	@Override
//	public void onPropertyChanged(IObservable<?> prop, Collection<Object> initiators) {
//		if ((Boolean) prop.get()) {
//			if (mFootLoadingView.getParent() == null) {
//				mFootLoadingLay.addView(mFootLoadingView);
//			}
//		} else {
//			mFootLoadingLay.removeAllViews();
//		}
//	}
//
//	@Override
//	public void onPreBindView(View rootView, int layoutId) {
//		if (layoutId == R.layout.reader_catalog_tab_item_lay) {
//			ListView listView = (ListView) rootView.findViewById(R.id.reader_catalog_lv);
//			listView.addFooterView(mFootLoadingLay);
//		}
//	}

}
