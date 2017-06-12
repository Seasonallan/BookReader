package com.lectek.android.lereader.binding.model.imports;

import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.IntegerObservable;
import gueei.binding.observables.StringObservable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.binding.command.OnClickCommand;
import com.lectek.android.binding.command.OnItemClickCommand;
import com.lectek.android.lereader.binding.model.BaseLoadDataViewModel;
import com.lectek.android.lereader.binding.model.customertitle.CustomerTitleViewModel;
import com.lectek.android.lereader.binding.model.customertitle.CustomerTitleViewModel.OnClickCallback;
import com.lectek.android.lereader.binding.model.imports.ImportLocalModel.OnScanListener;
import com.lectek.android.lereader.data.FileData;
import com.lectek.android.lereader.lib.utils.FileUtil;
import com.lectek.android.lereader.permanent.AppBroadcast;
import com.lectek.android.lereader.ui.ILoadView;
import com.lectek.android.lereader.ui.basereader_leyue.BaseReaderActivityLeyue;
import com.lectek.android.lereader.utils.Constants;
import com.lectek.android.lereader.utils.DialogUtil;
import com.lectek.android.lereader.utils.DialogUtil.CancelListener;
import com.lectek.android.lereader.utils.DialogUtil.ConfirmListener;
import com.lectek.android.lereader.utils.ToastUtil;

public class ImportLocalViewModel extends BaseLoadDataViewModel implements OnScanListener{
	
	private static final String Tag = ImportLocalViewModel.class.getSimpleName();
	
//	private ImportLocalCallback mImportLocalCallback;
	private Dialog mScanDialog;
	
	private static final String SDCARD_PARENT_DIR = FileUtil.getExternalStorageDirectory().getParent();
	
	private ImportLocalModel mImportLocalModel;
	private ImportBookModel mImportBookModel;
	private ArrayList<FileData> mDataSource;
	public final StringObservable bCurrDirectory = new StringObservable();
	public final BooleanObservable bDirectoryVisibility = new BooleanObservable(true);
	public final BooleanObservable bDirectoryLineVisibility = new BooleanObservable(true);
	public final ArrayListObservable<ItemViewModel> bItems = new ArrayListObservable<ImportLocalViewModel.ItemViewModel>(ItemViewModel.class);
	public final BooleanObservable bBottomBtnClickable = new BooleanObservable(true);
	public final IntegerObservable bBackTipVisibility = new IntegerObservable(View.GONE);
	public final IntegerObservable bNoBookTipVisibility = new IntegerObservable(View.GONE);
	public final StringObservable bBottomBtnText = new StringObservable();
	public final BooleanObservable bPathLayVisibility = new BooleanObservable(true);
	public StringObservable bScanButtonText = new StringObservable();
	/**
	 * 是否是扫描界面
	 */
	private boolean isScan = false;
	
	private boolean isSelectAll = false;
	
	private static String mLastDir = Constants.bookStoredDiretory;
	private boolean isNeedNotice;
	
//	public ImportLocalViewModel(Context context, ILoadView loadView, ImportLocalCallback importLocalCallback) {
	public ImportLocalViewModel(Context context, ILoadView loadView) {
		super(context, loadView);
		mDataSource = new ArrayList<FileData>();
		mImportLocalModel = new ImportLocalModel();
		mImportLocalModel.addCallBack(this);
		mImportLocalModel.setOnScanListener(this);
		mImportBookModel = new ImportBookModel();
		mImportBookModel.addCallBack(this);
//		mImportLocalCallback = importLocalCallback;
	}
	
	
	public final OnItemClickCommand bOnItemClickCommand = new OnItemClickCommand() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ItemViewModel itemViewModel = bItems.get(position);
			if(itemViewModel == null) {
				return;
			}
			
			if(isScan) {	//扫描结果
				itemViewModel.bItemChecked.set(!itemViewModel.bItemChecked.get());
				updateCheckedState();
				return;
			}
			
			if(!FileUtil.isSDcardExist()) {
				DialogUtil.oneConfirmBtnDialog((Activity)getContext(), null, getString(R.string.import_local_no_sdcard), -1, null);
				return;
			}
			File temp = new File(itemViewModel.fileData.mAbsolutePath);
			if(temp.isDirectory()) {	//文件夹
				showDirectoryView(temp.getPath());
			} else {	//文件，打开书籍并导入到书架
				mImportBookModel.addBook(itemViewModel.fileData);
			}
		}
	};
	
	public final OnClickCommand bOnScanClickCommand = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			
			if(getContext().getString(R.string.import_local_scan_text).equals(bScanButtonText.get())) {	//扫描书籍
				startScanFiles(mLastDir);
			}else if(getContext().getString(R.string.btn_text_check_in).equals(bScanButtonText.get())) {	//导入书架
				if(bItems != null && bItems.size() > 0) {
					ArrayList<FileData> selectItems = new ArrayList<FileData>();
					for(ItemViewModel item : bItems) {
						if(item.bItemChecked.get()) {
							selectItems.add(item.fileData);
						}
					}
					if(selectItems.size() == 0){
                        ToastUtil.showToast(getContext(), R.string.import_local_unselected);
						return;
					}
					mImportBookModel.addBooks(selectItems);
					isNeedNotice = true;
				}
			}
		}
	};

	public OnClickCallback getTitleLeftButtonClick() {
		return new CustomerTitleViewModel.OnClickCallback() {
			
			@Override
			public void onClick(View v) {
				if(getContext().getString(R.string.import_local_scan_text).equals(bScanButtonText.get())
						&& (getContext() instanceof Activity)) {	
					
					((Activity)getContext()).finish();
					
				}else if(getContext().getString(R.string.btn_text_check_in).equals(bScanButtonText.get())) {	//导入书架
					showDirectoryView(mLastDir);
				}
			}
		};
	}
	
	private void updateCheckedState() {
		if(!isScan || bItems == null || bItems.size() <= 0) {
			return;
		}
		isSelectAll = true;
		for(ItemViewModel item : bItems) {
			if(!item.bItemChecked.get()) {
				isSelectAll = false;
				break;
			}
		}
//		if(mImportLocalCallback != null) {
//			if(isSelectAll) {
//				mImportLocalCallback.setTitleRightBtnText(getString(R.string.btn_text_anti_check));
//			} else {
//				mImportLocalCallback.setTitleRightBtnText(getString(R.string.btn_text_checkall));
//			}
//		}
	}
	
	public final OnClickCommand bBottomBtnOnClick = new OnClickCommand() {
		@Override
		public void onClick(View v) {
			if(!isScan) {	//浏览本机文件，返回上一级
				File currFile = new File(mLastDir);
				File file = currFile.getParentFile();
				if(file == null || file.getPath().equals(SDCARD_PARENT_DIR)) {
					mLastDir = Constants.bookStoredDiretory;
					finish();
				}else {
					showDirectoryView(file.getPath());
				}
			} 
//			else {	//扫描结果，导入到书架
//				if(bItems != null && bItems.size() > 0) {
//					ArrayList<FileData> selectItems = new ArrayList<FileData>();
//					for(ItemViewModel item : bItems) {
//						if(item.bItemChecked.get()) {
//							selectItems.add(item.fileData);
//						}
//					}
//					if(selectItems.size() == 0){
//                        ToastUtil.showToast(getContext(), R.string.import_local_unselected);
//						return;
//					}
//					mImportBookModel.addBooks(selectItems);
//					isNeedNotice = true;
//				}
//				
//				
//			}
		}
	};
	
	/**
	 * 显示当前目录下的文件列表
	 * @param filePath
	 */
	private void showDirectoryView(String filePath) {
		if(TextUtils.isEmpty(filePath)) {
			mLastDir = Constants.bookStoredDiretory;
			finish();
			return;
		}

		mLastDir = filePath;
		bCurrDirectory.set(mLastDir);
//		if(mImportLocalCallback != null) {
//			mImportLocalCallback.setActivityTitleContent(getString(R.string.import_local_title));
//		}
		resetBtn(ImportLocalModel.LOAD_CURRENT_DIRECTORY);
		mImportLocalModel.start(ImportLocalModel.LOAD_CURRENT_DIRECTORY, mLastDir);
		isScan = false;
	}

	/**
	 * 扫描当前目录下的文件
	 * @param filePath
	 */
	private void startScanFiles(String filePath) {
		
		bPathLayVisibility.set(false);
		
		mImportLocalModel.start(ImportLocalModel.LOAD_SCAN_FILES, filePath);
		isScan = true;
	}
	
	/**
	 * 重置头部右边按钮和底部按钮
	 * @param loadType
	 */
	private void resetBtn(int loadType) {
		if(loadType == ImportLocalModel.LOAD_CURRENT_DIRECTORY) {
			bBackTipVisibility.set(View.VISIBLE);
			bBottomBtnText.set(getString(R.string.import_local_back_to_super));
			if(new File(mLastDir).getParent().equals(SDCARD_PARENT_DIR)) {
				bBottomBtnClickable.set(false);
			} else {
				bBottomBtnClickable.set(true);
			}
			
			bPathLayVisibility.set(true);
			bScanButtonText.set(getContext().getString(R.string.import_local_scan_text));
			
		} else if (loadType == ImportLocalModel.LOAD_SCAN_FILES) {
			bBottomBtnClickable.set(true);
			bBackTipVisibility.set(View.GONE);
			bBottomBtnText.set(getString(R.string.import_local_to_bookshelf));
			isSelectAll = false;
			setAllItemChecked(isSelectAll);
			bScanButtonText.set(getContext().getString(R.string.btn_text_check_in));
		}
	}
	
	/**
	 * 全选或反选
	 * @param isChecked
	 */
	private void setAllItemChecked(boolean isChecked) {
		if(bItems == null || bItems.size() <= 0) {
			return;
		}
		isSelectAll = isChecked;
		for(ItemViewModel item : bItems) {
			item.bItemChecked.set(isChecked);
		}
//		if(mImportLocalCallback != null) {
//			if(isSelectAll) {
//				mImportLocalCallback.setTitleRightBtnText(getString(R.string.btn_text_anti_check));
//			} else {
//				mImportLocalCallback.setTitleRightBtnText(getString(R.string.btn_text_checkall));
//			}
//		}
	}
	
	/**
	 * 显示扫描中的对话框
	 */
	private void showScanDialog() {
		mScanDialog = DialogUtil.oneCancelBtnDialog((Activity)getContext(), 
				getString(R.string.import_local_scan_result, 0), 
				getString(R.string.import_local_dialog_scan_result_2, 0, 0, 0), 
				-1, 
				new CancelListener() {
			@Override
			public void onClick(View v) {
				mImportLocalModel.cancel();
				dismissScanDialog();
				resetBtn(ImportLocalModel.LOAD_CURRENT_DIRECTORY);
				isScan = false;
			}
		});
	}
	/**
	 * 隐藏扫描中的对话框
	 */
	private void dismissScanDialog() {
		if(mScanDialog != null) {
			mScanDialog.dismiss();
			mScanDialog = null;
		}
	}
	
	public boolean onBackPressed() {
		if(isScan) {
			showDirectoryView(mLastDir);
			return true;
		}
		return false;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		showDirectoryView(mLastDir);
	};
	
	@Override
	public void onRelease() {
		super.onRelease();
	}

	@Override
	public boolean onPreLoad(String tag, Object... params) {
		if(mImportLocalModel.getTag().equals(tag)) {
			if(params != null && params.length > 0) {
				int loadType = (Integer) params[0];
				if(loadType == ImportLocalModel.LOAD_CURRENT_DIRECTORY) {
//					showLoadView();
				} else if (loadType == ImportLocalModel.LOAD_SCAN_FILES) {
					showScanDialog();
				}
			}
		}else if(mImportBookModel.getTag().equals(tag)) {
//			showLoadView();
		}
		return false;
	}

	@Override
	public boolean onFail(Exception e, String tag, Object... params) {
		if(mImportLocalModel.getTag().equals(tag)) {
			hideLoadView();
			dismissScanDialog();
			if(e != null) {
				if(ImportLocalModel.EXCEPTION_MSG_SDCARD_NOT_EXIST.equals(e.getMessage())) {
					DialogUtil.oneConfirmBtnDialog((Activity)getContext(), null, getString(R.string.import_local_no_sdcard), -1,
							new ConfirmListener(){
						@Override
						public void onClick(View v) {
							finish();
						}
					});
				} else {
					finish();
					ToastUtil.showToast(getContext(), e.getMessage());
				}
			}
		}
		return false;
	}

	@Override
	public boolean onPostLoad(Object result, String tag, boolean isSucceed,
			boolean isCancel, Object... params) {
		if(!isCancel && result == null) {
			bNoBookTipVisibility.set(View.VISIBLE);
		}
		if(!isCancel && result != null && params != null && params.length > 0) {
			if(mImportLocalModel.getTag().equals(tag)) {
				ArrayList<FileData> fileDatas = (ArrayList<FileData>) result;
				if(fileDatas.size() > 0) {
					bNoBookTipVisibility.set(View.GONE);
				} else {
					bNoBookTipVisibility.set(View.VISIBLE);
				}
				if(fileDatas != null) {
					mDataSource.clear();
					mDataSource.addAll(fileDatas);
				}
				int loadType = (Integer) params[0];
				ArrayListObservable<ItemViewModel> mTempItems = new ArrayListObservable<ImportLocalViewModel.ItemViewModel>(ItemViewModel.class);
				for(int i = 0; i < mDataSource.size(); i++) {
					FileData fileData = mDataSource.get(i);
					ItemViewModel itemViewModel = newItemViewModel(loadType, fileData);
					itemViewModel.bListBg.set(i % 2 == 0 ? R.color.transparent : R.color.transparent_black);
					mTempItems.add(itemViewModel);
				}
				bItems.setAll(mTempItems);
				if(loadType == ImportLocalModel.LOAD_CURRENT_DIRECTORY) {
					hideLoadView();
				} else if (loadType == ImportLocalModel.LOAD_SCAN_FILES) {
					dismissScanDialog();
					isSelectAll = false;
					resetBtn(loadType);
//					if(mImportLocalCallback != null) {
//						mImportLocalCallback.setActivityTitleContent(getString(R.string.import_local_scan_result, bItems.size()));
//					}
				}
			}else if(mImportBookModel.getTag().equals(tag)) {
				hideLoadView();
				if((Boolean) result){
					ToastUtil.showToast(getContext(), R.string.import_local_to_bookshelf_succeed);
				}else{
					ToastUtil.showToast(getContext(), R.string.import_local_to_bookshelf_fail);
				}
//                if (!isScan){
//                    isNeedNotice = true;
//                    BaseReaderActivityLeyue.openActivity(getContext(), mImportBookModel.getLatestBook() , false);
//                }
				if((Activity)getContext() instanceof Activity) {
					isNeedNotice = true;
					((Activity)getContext()).finish();
				}
			}
		}
		return false;
	}
	
	private ItemViewModel newItemViewModel(int loadType, FileData fileData) {
		ItemViewModel itemViewModel = new ItemViewModel();
		itemViewModel.fileData = fileData;
		if(loadType == ImportLocalModel.LOAD_CURRENT_DIRECTORY) {
			itemViewModel.bFileDesVisibility.set(View.VISIBLE);
			itemViewModel.bCheckBoxVisibility.set(View.GONE);
		} else if (loadType == ImportLocalModel.LOAD_SCAN_FILES) {
			itemViewModel.bFileDesVisibility.set(View.GONE);
			itemViewModel.bCheckBoxVisibility.set(View.VISIBLE);
			itemViewModel.bArrowVisibility.set(View.GONE);
			itemViewModel.bItemChecked.set(false);
		}
		File file = new File(fileData.mAbsolutePath);
		if(file.isDirectory()) {
			itemViewModel.bFileSizeVisibility.set(View.GONE);
			itemViewModel.bArrowVisibility.set(View.VISIBLE);
			itemViewModel.bIconSource.set(R.drawable.ic_folder);
			itemViewModel.bFileDes.set(fileData.mInfo);
		} else {
			itemViewModel.bFileSizeVisibility.set(View.VISIBLE);
			itemViewModel.bArrowVisibility.set(View.GONE);
			itemViewModel.bFileSize.set(fileData.mInfo);
			updateFileInfo(fileData.mAbsolutePath, itemViewModel);
		}
		itemViewModel.bFileName.set(fileData.mName);
		return itemViewModel;
	}
	
	private void updateFileInfo(String filePath, ItemViewModel itemViewModel) {
		String extension = FileUtil.getExtension(filePath).toLowerCase();
		if("epub".equals(extension)) {
			itemViewModel.bIconSource.set(R.drawable.ic_epub_file);
			itemViewModel.bFileDes.set(getString(R.string.import_local_file_format, "epub"));
		} else if ("txt".equals(extension)) {
			itemViewModel.bIconSource.set(R.drawable.ic_txt_file);
			itemViewModel.bFileDes.set(getString(R.string.import_local_file_format, "txt"));
		} else if ("pdf".equals(extension)) {
			itemViewModel.bIconSource.set(R.drawable.ic_pdf_file);
			itemViewModel.bFileDes.set(getString(R.string.import_local_file_format, "pdf"));
		}
	}
	
	public class ItemViewModel {
		FileData fileData;
		public IntegerObservable bIconSource = new IntegerObservable(R.drawable.ic_folder);
		public IntegerObservable bListBg = new IntegerObservable(R.color.transparent);
		public StringObservable bFileName = new StringObservable();
		public StringObservable bFileDes = new StringObservable();
		public StringObservable bFileSize = new StringObservable();
		public IntegerObservable bFileSizeVisibility = new IntegerObservable(View.VISIBLE);
		public IntegerObservable bArrowVisibility = new IntegerObservable(View.VISIBLE);
		public IntegerObservable bCheckBoxVisibility = new IntegerObservable(View.GONE);
		public BooleanObservable bItemChecked = new BooleanObservable(false);
		public IntegerObservable bFileDesVisibility = new IntegerObservable(View.GONE);
	}
	
//	public interface ImportLocalCallback {
//		public void setTitleLeftBtn(OnClickCallback onClickCallback);
//		public void setActivityTitleContent(String title);
//	}

	@Override
	public void onScan(int scanNum) {
		if(mScanDialog != null) {
			((TextView)mScanDialog.findViewById(R.id.dialog_title))
			.setText(getString(R.string.import_local_dialog_scan_result, scanNum));
		}
	}

	@Override
	public void onFindFile(int epubNum, int txtNum, int pdfNum) {
		if(mScanDialog != null) {
			((TextView)mScanDialog.findViewById(R.id.dialog_content))
			.setText(getString(R.string.import_local_dialog_scan_result_2, epubNum, txtNum, pdfNum));
		}
	}

	@Override
	public void onCancelScan(List<FileData> fileDatas) {
	}

	public void noticeDataChange() {
		if(isNeedNotice){
			getContext().sendBroadcast(new Intent(AppBroadcast.ACTION_UPDATE_BOOKSHELF));
		}
	}
}
