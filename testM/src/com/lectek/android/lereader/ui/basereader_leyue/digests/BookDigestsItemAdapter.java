package com.lectek.android.lereader.ui.basereader_leyue.digests;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.storage.dbase.digest.BookDigests;
import com.lectek.android.lereader.utils.CommonUtil;
import com.lectek.lereader.core.bookformats.Catalog;
import com.lectek.lereader.core.text.Constant;


public class BookDigestsItemAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private ArrayList<BookDigests> mBookDigests;
    private Activity mContext;

    public BookDigestsItemAdapter(Activity context){
        super();
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<BookDigests> bookDigests){
        Collections.sort(bookDigests,new SortByDate());
        this.mBookDigests = bookDigests;
    }

    /**
     * 获取某章节位置的章节信息
     */
    public String getCatalogByPosition(int position){
        if(mCatalogList != null && mCatalogList.size() > position && position >=0){
            Catalog catalog = mCatalogList.get(position);
            return catalog.getText();
        }
        return "第"+position +"章";
    }
    protected ArrayList<Catalog> mCatalogList;
    public void setCatalogMsg(ArrayList<Catalog> catalogList){
        this.mCatalogList = catalogList;
    }

    public BookDigests remove(int position) {
        return mBookDigests.remove(position);
    }

    @Override
    public int getCount() {
        if(mBookDigests != null){
            return mBookDigests.size();
        }
        return 0;
    }

    @Override
    public BookDigests getItem(int position) {
        if(position < getCount()){
            return mBookDigests.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = newView();
            viewHolder = new ViewHolder();
            viewHolder.titleTV = (TextView) convertView.findViewById(R.id.bookdigests_title_tv);
            viewHolder.msgTV = (TextView) convertView.findViewById(R.id.bookdigests_msg_tv);
            viewHolder.contentTV = (TextView) convertView.findViewById(R.id.bookdigests_name_tv);
            viewHolder.timeTV = (TextView) convertView.findViewById(R.id.bookdigests_time_tv);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final BookDigests item = getItem(position);
/*
        String catalog = item.getChaptersName();
        if(TextUtils.isEmpty(catalog)){
            catalog = getCatalogByPosition(item.getChaptersId());
        }
        viewHolder.titleTV.setText(catalog);*/
        String content = item.getContent();
        SpannableString spanStr = new SpannableString(content);
        int index = content.indexOf(Constant.REPLACEMENT_SPAN_CHAR);
        while (index >= 0){
            ImageSpan span = new ImageSpan(mContext, R.drawable.tp_icon);
            spanStr.setSpan(span, index, index + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            index = content.indexOf(Constant.REPLACEMENT_SPAN_CHAR, index +1);
        }
        viewHolder.contentTV.setText(spanStr);
        viewHolder.timeTV.setText(getTime(item.getDate()));
        String msg = item.getMsg();
        if(TextUtils.isEmpty(msg)){
            msg = mContext.getString(R.string.reader_bookdigest_msg_none);
        }
        viewHolder.msgTV.setText(mContext.getString(R.string.reader_bookdigest_msg,
                mContext.getString(R.string.reader_bookdigest_msg_part1),msg));

        return convertView;
    }

    private String getTime(Long datelong){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        date.setTime(datelong);
        return CommonUtil.getNowDay(sdf.format(date));
    }

    private View newView(){
        return mLayoutInflater.inflate(R.layout.my_digest_item, null);
    }

    private class ViewHolder {
        public TextView msgTV;
        public TextView contentTV;
        public TextView titleTV;
        public TextView timeTV;
    }

    class SortByDate implements Comparator<BookDigests>{
        @Override
        public int compare(BookDigests obj1,BookDigests obj2){
            BookDigests bookDigests1 = obj1;
            BookDigests bookDigests2 = obj2;
            if(bookDigests1.getDate() <= bookDigests2.getDate()) {
                return 1;
            }
            else{
                return -1;
            }
        }
    }
}

