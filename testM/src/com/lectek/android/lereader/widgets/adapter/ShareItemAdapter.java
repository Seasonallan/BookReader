package com.lectek.android.lereader.widgets.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lectek.android.LYReader.R;
import com.lectek.android.lereader.lib.utils.LogUtil;

/**
 * @author KinsonWu
 * @date 2011-09-19
 * @email kinson.wu@gmail.com
 */

public class ShareItemAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<ResolveInfo> mResolveInfo;
	private Context mContext;
	private PackageManager packageManager;
	private final Intent mIntent;
	private final LayoutInflater mInflater;

	private List<DisplayResolveInfo> mList;
	private static final String TAG = ShareItemAdapter.class.getSimpleName();
	
	private boolean isSinaWeibo = false;
	private boolean isQqWeibo = false;

	private boolean hasSinaWeibo = false;
	private boolean haoQqWeibo = false;

	public final class DisplayResolveInfo {
		public ResolveInfo ri;
		public CharSequence displayLabel;
		Drawable displayIcon;
		CharSequence extendedInfo;
		Intent origIntent;

		DisplayResolveInfo(ResolveInfo pri, CharSequence pLabel, CharSequence pInfo, Intent pOrigIntent) {
			ri = pri;
			displayLabel = pLabel;
			extendedInfo = pInfo;
			origIntent = pOrigIntent;
		}
	}

	public ShareItemAdapter(Context context, Intent intent, Intent[] initialIntents) {
		this.mContext = context;
		mIntent = new Intent(intent);
		mIntent.setComponent(null);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		packageManager = context.getPackageManager();
		List<ResolveInfo> resolveList = packageManager.queryIntentActivities(intent, 0/* PackageManager.MATCH_DEFAULT_ONLY */);
		int num;
		if ((resolveList != null) && ((num = resolveList.size()) > 0)) {
			// Only display the first matches that are either of equal
			// priority or have asked to be default options.
			ResolveInfo resolveInfo0 = resolveList.get(0);
			for (int i = 1; i < num; i++) {
				ResolveInfo ri = resolveList.get(i);
				if (resolveInfo0.priority != ri.priority || resolveInfo0.isDefault != ri.isDefault) {
					while (i < num) {
						resolveList.remove(i);
						num--;
					}
				}
			}
			if (num > 1) {
				ResolveInfo.DisplayNameComparator rComparator = new ResolveInfo.DisplayNameComparator(packageManager);
				Collections.sort(resolveList, rComparator);
			}

			mList = new ArrayList<DisplayResolveInfo>();

			// First put the initial items at the top.
			if (initialIntents != null) {
				for (int i = 0; i < initialIntents.length; i++) {
					Intent iIntent = initialIntents[i];
					if (iIntent == null) {
						continue;
					}
					ActivityInfo ai = iIntent.resolveActivityInfo(context.getPackageManager(), 0);
					if (ai == null) {
						continue;
					}
					ResolveInfo ri = new ResolveInfo();
					ri.activityInfo = ai;
					if (iIntent instanceof LabeledIntent) {
						LabeledIntent li = (LabeledIntent) iIntent;
						ri.resolvePackageName = li.getSourcePackage();
						LogUtil.v(TAG, "ShareItemAdapter packageName: " + ri.resolvePackageName);
						ri.labelRes = li.getLabelResource();
						LogUtil.v(TAG, "ShareItemAdapter labelRes: " + ri.labelRes);
						ri.nonLocalizedLabel = li.getNonLocalizedLabel();
						LogUtil.v(TAG, "ShareItemAdapter nonLocalizedLabel: " + ri.nonLocalizedLabel);
						ri.icon = li.getIconResource();
					}
					if (resolveInfo0.activityInfo.packageName.contains("com.sina.weibo")) {
						mList.add(0, new DisplayResolveInfo(ri, ri.loadLabel(context.getPackageManager()), null, iIntent));
					} else if (resolveInfo0.activityInfo.packageName.equals("com.tencent.WBlog")) {
						mList.add(1, new DisplayResolveInfo(ri, ri.loadLabel(context.getPackageManager()), null, iIntent));
					} else if (resolveInfo0.activityInfo.packageName.equals("com.android.mms")) {
						mList.add(1, new DisplayResolveInfo(ri, ri.loadLabel(context.getPackageManager()), null, iIntent));
					} else {
						mList.add(new DisplayResolveInfo(ri, ri.loadLabel(context.getPackageManager()), null, iIntent));
					}
				}
			}

			// Check for applications with same name and use application name or
			// package name if necessary
			resolveInfo0 = resolveList.get(0);
			int start = 0;
			CharSequence r0Label = resolveInfo0.loadLabel(packageManager);
			for (int i = 1; i < num; i++) {
				if (r0Label == null) {
					r0Label = resolveInfo0.activityInfo.packageName;
				}
				ResolveInfo ri = resolveList.get(i);
				CharSequence riLabel = ri.loadLabel(packageManager);
				if (riLabel == null) {
					riLabel = ri.activityInfo.packageName;
				}
				if (ri.activityInfo.packageName.contains("com.android.bluetooth")
					|| ri.activityInfo.packageName.contains("com.qihoo360.kouxin")) {
					start = i;
					continue;
				}
				if (riLabel.equals(r0Label)) {
					continue;
				}
				if (resolveInfo0.activityInfo.packageName.equals("com.sina.weibo")
						|| ri.activityInfo.packageName.equals("com.sina.weibo")) {
					hasSinaWeibo = true;
				} else if (resolveInfo0.activityInfo.packageName.equals("com.tencent.WBlog")
						|| ri.activityInfo.packageName.contains("com.tencent.WBlog")
						|| r0Label.toString().contains("腾讯")) {
					haoQqWeibo = true;
				}
				LogUtil.v(TAG, "ShareItemAdapter r0Label: " + r0Label);
				LogUtil.v(TAG, "ShareItemAdapter package: " + resolveInfo0.activityInfo.packageName);
				processGroup(resolveList, start, (i - 1), resolveInfo0, r0Label);
				resolveInfo0 = ri;
				r0Label = riLabel;
				start = i;
			}
			// Process last group
			processGroup(resolveList, start, (num - 1), resolveInfo0, r0Label);

			if (!haoQqWeibo) {
				LogUtil.v(TAG, "添加分享到腾讯微博");
				ResolveInfo sinaRi = new ResolveInfo();
				ActivityInfo activityInfo = new ActivityInfo();
				activityInfo.name = "com.lectek.android.sfreader";
				sinaRi.resolvePackageName = "com.lectek.android.sfreader";
				sinaRi.activityInfo = activityInfo;
				ApplicationInfo appInfo = new ApplicationInfo();
				appInfo.packageName = "com.lectek.android.sfreader";
				sinaRi.activityInfo.applicationInfo = appInfo;
				LogUtil.v(TAG, "packageName: " + sinaRi.resolvePackageName);
				sinaRi.labelRes = R.string.share_to_sina_weibo;
				LogUtil.v(TAG, "labelRes: " + sinaRi.labelRes);
				sinaRi.icon = R.drawable.weibo_qq;
				if (hasSinaWeibo) {
					mList.add(1, new DisplayResolveInfo(sinaRi, "分享到腾讯微博", null, null));
				} else {
					mList.add(0, new DisplayResolveInfo(sinaRi, "分享到腾讯微博", null, null));
				}
			}
			// 判断是否有新浪微博
			if (!hasSinaWeibo) {
				LogUtil.v(TAG, "添加分享到新浪微博");
				ResolveInfo sinaRi = new ResolveInfo();
				ActivityInfo activityInfo = new ActivityInfo();
				activityInfo.name = "com.lectek.android.sfreader";
				sinaRi.resolvePackageName = "com.lectek.android.sfreader";
				sinaRi.activityInfo = activityInfo;
				ApplicationInfo appInfo = new ApplicationInfo();
				appInfo.packageName = "com.lectek.android.sfreader";
				sinaRi.activityInfo.applicationInfo = appInfo;
				LogUtil.v(TAG, "packageName: " + sinaRi.resolvePackageName);
				sinaRi.labelRes = R.string.share_to_sina_weibo;
				LogUtil.v(TAG, "labelRes: " + sinaRi.labelRes);
				sinaRi.icon = R.drawable.weibo_sina_icon;
				mList.add(0, new DisplayResolveInfo(sinaRi, "分享到微博", null, null));
			}
		}
	}
	
	public ShareItemAdapter(Context context, Intent intent) {
		this.mContext = context;
		mIntent = new Intent(intent);
		mIntent.setComponent(null);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		packageManager = context.getPackageManager();
		
		mList = new ArrayList<DisplayResolveInfo>();
		int i = 0;
		// 判断是否有新浪微博
		LogUtil.v(TAG, "添加分享到新浪微博");
		ResolveInfo sinaRi = new ResolveInfo();
		ActivityInfo activityInfo = new ActivityInfo();
		activityInfo.name = "com.lectek.android.sfreader";
		sinaRi.resolvePackageName = "com.lectek.android.sfreader";
		sinaRi.activityInfo = activityInfo;
		ApplicationInfo appInfo = new ApplicationInfo();
		appInfo.packageName = "com.lectek.android.sfreader";
		sinaRi.activityInfo.applicationInfo = appInfo;
		LogUtil.v(TAG, "packageName: " + sinaRi.resolvePackageName);
		sinaRi.labelRes = R.string.share_to_sina_weibo;
		LogUtil.v(TAG, "labelRes: " + sinaRi.labelRes);
		sinaRi.icon = R.drawable.weibo_sina_icon;
		mList.add(i++, new DisplayResolveInfo(sinaRi, "分享到微博", null, null));
		
		//添加分享到腾讯微博
		LogUtil.v(TAG, "添加分享到腾讯微博");
		ResolveInfo qqRi = new ResolveInfo();
//		ActivityInfo activityInfo = new ActivityInfo();
//		activityInfo.name = "com.lectek.android.sfreader";
		qqRi.resolvePackageName = "com.lectek.android.sfreader";
		qqRi.activityInfo = activityInfo;
//		ApplicationInfo appInfo = new ApplicationInfo();
//		appInfo.packageName = "com.lectek.android.sfreader";
		qqRi.activityInfo.applicationInfo = appInfo;
		LogUtil.v(TAG, "packageName: " + qqRi.resolvePackageName);
		qqRi.labelRes = R.string.share_to_sina_weibo;
		LogUtil.v(TAG, "labelRes: " + qqRi.labelRes);
		qqRi.icon = R.drawable.weibo_qq;
		mList.add(i++, new DisplayResolveInfo(qqRi, "分享到腾讯微博", null, null));
	}

	private void processGroup(List<ResolveInfo> rList, int start, int end, ResolveInfo ro, CharSequence roLabel) {
		// Process labels from start to i
		int num = end - start + 1;
		if (num == 1) {
			// No duplicate labels. Use label for entry at start
			if (ro.activityInfo.packageName.equals("com.sina.weibo")) {
				isSinaWeibo = true;
				mList.add(0, new DisplayResolveInfo(ro, roLabel, null, null));
			} else if (ro.activityInfo.packageName.equals("com.tencent.WBlog")) {
				isQqWeibo = true;
				if (isSinaWeibo) {
					mList.add(1, new DisplayResolveInfo(ro, roLabel, null, null));
				} else {
					mList.add(0, new DisplayResolveInfo(ro, roLabel, null, null));
				}
			} else if (ro.activityInfo.packageName.equals("com.android.mms")) {
				if (isSinaWeibo && isQqWeibo) {
					mList.add(2, new DisplayResolveInfo(ro, roLabel, null, null));
				} else if ((isSinaWeibo && !isQqWeibo) || (!isSinaWeibo && isQqWeibo)) {
					mList.add(1, new DisplayResolveInfo(ro, roLabel, null, null));
				} else {
					mList.add(0, new DisplayResolveInfo(ro, roLabel, null, null));
				}
			} else {
				mList.add(new DisplayResolveInfo(ro, roLabel, null, null));
			}
			
		} else {
			boolean usePkg = false;
			CharSequence startApp = ro.activityInfo.applicationInfo.loadLabel(packageManager);
			if (startApp == null) {
				usePkg = true;
			}
			if (!usePkg) {
				// Use HashSet to track duplicates
				HashSet<CharSequence> duplicates = new HashSet<CharSequence>();
				duplicates.add(startApp);
				for (int j = start + 1; j <= end; j++) {
					ResolveInfo jRi = rList.get(j);
					CharSequence jApp = jRi.activityInfo.applicationInfo.loadLabel(packageManager);
					if ((jApp == null) || (duplicates.contains(jApp))) {
						usePkg = true;
						break;
					} else {
						duplicates.add(jApp);
					}
				}
				// Clear HashSet for later use
				duplicates.clear();
			}
			for (int k = start; k <= end; k++) {
				ResolveInfo addResolveInfo = rList.get(k);
				if (usePkg) {
					// Use application name for all entries from start to end-1
					LogUtil.v(TAG, "r0Label: " + roLabel);
					LogUtil.v(TAG, "packageName: " + addResolveInfo.activityInfo.packageName);
					if (usePkg) {
						
					}
					mList.add(new DisplayResolveInfo(addResolveInfo, roLabel, addResolveInfo.activityInfo.packageName, null));
				} else {
					// Use package name for all entries from start to end-1
					LogUtil.v(TAG, "r0Label: " + roLabel);
					CharSequence charSequence = addResolveInfo.activityInfo.applicationInfo.loadLabel(packageManager);
					LogUtil.v(TAG, "packageName: " + charSequence);
					mList.add(new DisplayResolveInfo(addResolveInfo, roLabel,
							addResolveInfo.activityInfo.applicationInfo.loadLabel(packageManager), null));
				}
			}
		}
	}

	public ResolveInfo resolveInfoForPosition(int position) {
		if (mList == null) {
			return null;
		}

		return mList.get(position).ri;
	}

	public Intent intentForPosition(int position) {
		if (mList == null) {
			return null;
		}

		DisplayResolveInfo dri = mList.get(position);

		Intent intent = new Intent(dri.origIntent != null ? dri.origIntent : mIntent);
		intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT | Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
		ActivityInfo ai = dri.ri.activityInfo;
		intent.setComponent(new ComponentName(ai.applicationInfo.packageName, ai.name));
		return intent;
	}

	@Override
	public int getCount() {
		return mList != null ? mList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mList != null ? mList.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView == null) {
			view = mInflater.inflate(R.layout.share_item, parent, false);
		} else {
			view = convertView;
		}
		bindView(view, mList.get(position));
		return view;
	}

	private final void bindView(View view, DisplayResolveInfo info) {
		TextView text = (TextView) view.findViewById(R.id.share_name);
		ImageView icon = (ImageView) view.findViewById(R.id.share_logo);
		text.setText(info.displayLabel);
		if (info.displayIcon == null) {
			info.displayIcon = info.ri.loadIcon(packageManager);
		}
		icon.setImageDrawable(info.displayIcon);
	}
}
