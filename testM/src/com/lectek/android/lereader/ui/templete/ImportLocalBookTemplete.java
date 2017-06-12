package com.lectek.android.lereader.ui.templete;

import android.content.Context;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.ui.model.dataDefine.ImportLocalBookData;
import com.lectek.android.lereader.ui.model.dataProvider.AbsUiDataProvider;
import com.lectek.android.lereader.ui.model.dataProvider.ImportLocalBookDataProvider;

public class ImportLocalBookTemplete extends AbsTemplete<ImportLocalBookData>{

	public ImportLocalBookTemplete(Context context) {
		super(context, R.layout.import_local_activity_lay);
	}

	@Override
	protected AbsUiDataProvider<ImportLocalBookData> onGetDataProvider() {
		return new ImportLocalBookDataProvider();
	}

}
