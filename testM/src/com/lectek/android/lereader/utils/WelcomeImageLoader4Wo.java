package com.lectek.android.lereader.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.graphics.drawable.Drawable;

import com.lectek.android.lereader.application.MyAndroidApplication;
import com.lectek.android.lereader.lib.net.exception.GsonResultException;
import com.lectek.android.lereader.lib.thread.ThreadFactory;
import com.lectek.android.lereader.lib.thread.ThreadPoolFactory;
import com.lectek.android.lereader.lib.utils.DimensionsUtil;
import com.lectek.android.lereader.net.ApiProcess4Leyue;
import com.lectek.android.lereader.net.response.WelcomeImageInfo;

/**
 * 启动界面加载类
 * @author Shizq
 *
 */
public class WelcomeImageLoader4Wo {
	
	public static final String IMAGE_SUFFIX = "#welcomeimage";
	public static final String isOffline = "3";//下载
	public static final String isPause = "2";//暂停
	
	/**
	 * 获取启动界面图片
	 * @param needCheckFromNet 是否需要从网络中获取最新的启动图片
	 * @return 如果有符合条件的图片就返回，否则返回null
	 */
	public static Drawable getWelcomeImageDrawable(boolean needCheckFromNet){
		Drawable welcomeImageDrawable = null;
		try {
			welcomeImageDrawable = getCurrentAvailableImage();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(needCheckFromNet){
			updateWelcomeImageInfoFromNet();
		}
		return welcomeImageDrawable;
	}
	
	/**
	 * 获取当前可用的启动图片
	 * @return
	 * @throws java.io.FileNotFoundException
	 */
	private static Drawable getCurrentAvailableImage() throws FileNotFoundException{
		ArrayList<WelcomeImageInfo> imageInfos = getLocalWelcomeImageList();
		long currentTimeMillis = System.currentTimeMillis();
		if(imageInfos != null && imageInfos.size() > 0){
			for(int i = 0; i < imageInfos.size(); i++){
				WelcomeImageInfo imageInfo = imageInfos.get(i);
				long startTimeMillis = Long.parseLong(imageInfo.getStartTime());
				long endTimeMillis = Long.parseLong(imageInfo.getEndTime());
				//如果当前时间大于等于开始时间，并且小于等于结束时间，则为可用的图片
				if(currentTimeMillis >= startTimeMillis && currentTimeMillis <= endTimeMillis){
					//判断当前的图片状态不为下线状态
					if(!imageInfo.getStatus().equals(isOffline)){
						//这里的imageInfo.getPath()方法实际上获取的是图片的文件名而非网络图片地址
						File imageFile = getWelcomeImageFile(imageInfo.getPath());
						//图片存在，并且不为暂停状态
						if(imageFile.exists() && !imageInfo.getStatus().equals(isPause)){
							Drawable drawable = Drawable.createFromPath(imageFile.getAbsolutePath());
							return drawable;
						}
					}else{
						//如果图片状态为下线状态则删除图片
						getWelcomeImageFile(imageInfo.getPath()).delete();
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 通过文件名获取缓存目录中的图片文件对象
	 * @param fileName
	 * @return
	 */
	private static File getWelcomeImageFile(String fileName){
		File cacheDir = MyAndroidApplication.getInstance().getCacheDir();
		File imageFile = new File(cacheDir.getAbsolutePath()+ File.separator + fileName);
		return imageFile;
	}
	
	/**
	 * 获取本地缓存目录的图片列表
	 * @return
	 */
	private static ArrayList<WelcomeImageInfo> getLocalWelcomeImageList(){
		File cacheDir = MyAndroidApplication.getInstance().getCacheDir();
		//获取过滤文件名后的文件名数组
		String imageNames[] = cacheDir.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				if(filename.endsWith(IMAGE_SUFFIX)){
					return true;
				}
				return false;
			}
		});
		
		if(imageNames != null && imageNames.length > 0 ){
			ArrayList<WelcomeImageInfo> imageInfos = new ArrayList<WelcomeImageInfo>();
			//通过对文件名的处理获取WelcomeImageInfo对象信息
			for(int i = 0; i < imageNames.length; i++){
				//文件名格式为：id#name#startTime#endTime#status#welcomeimage
				//例如：1#image#12312312312#123123131232#1#62937885260a55885360e675745d4206#welcomeimage
				//通过符合“#”区分，分别获取信息填充到WelcomeImageInfo对象中
				String info[] = imageNames[i].split("#");
				if(info != null && info.length == 7 ){
					WelcomeImageInfo imageInfo = new WelcomeImageInfo();
					imageInfo.setId(info[0]);
					imageInfo.setName(info[1]);
					imageInfo.setStartTime(info[2]);
					imageInfo.setEndTime(info[3]);
					imageInfo.setStatus(info[4]);
					imageInfo.setPath(imageNames[i]);//本地文件信息的路径实际上存储的是图片的文件名
					imageInfo.setPicCode(info[5]);
					imageInfos.add(imageInfo);
				}
			}
			return imageInfos;
		}
		
		return null;
	}
	
	/**
	 * 从网络中请求最新的启动界面图片并保存到本地缓存目录中
	 */
	private static void updateWelcomeImageInfoFromNet(){
		MyAndroidApplication.addBackgroundTask(new Runnable() {

            @Override
            public void run() {
                ArrayList<WelcomeImageInfo> infos = null;

                //请求网络图片信息
                try {
                    infos = ApiProcess4Leyue.getInstance(MyAndroidApplication.getInstance()).getWelcomImageInfo(DimensionsUtil.getDeviceResolution(MyAndroidApplication.getInstance()), "1", CommonUtil.getApplicationVersionName(MyAndroidApplication.getInstance()));
                } catch (GsonResultException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (infos == null)
                    return;

                ArrayList<WelcomeImageInfo> localInfos = getLocalWelcomeImageList();

                long currentTimeMillis = System.currentTimeMillis();

                for (int i = 0; i < infos.size(); i++) {
                    WelcomeImageInfo newInfo = infos.get(i);
                    newInfo.setStartTime(convertTimeToLong(newInfo.getStartTime()));
                    newInfo.setEndTime(convertTimeToLong(newInfo.getEndTime()));
                    //这里的picCode为图片的唯一标识符，用户判断网络图片是否被替换过
                    newInfo.setPicCode(CommonUtil.makeMD5(newInfo.getPath()));
                    boolean isDownload = false;
                    for (int j = 0; localInfos != null && j < localInfos.size(); j++) {
                        WelcomeImageInfo localInfo = localInfos.get(j);
                        //如果网络图片已经存在了本地缓存中
                        if (newInfo.getId().equals(localInfo.getId())) {
                            isDownload = true;
                            updateWelcomeImageInfo(newInfo, localInfo);
                        }
                    }
                    //如果本地不存在该图片则直接添加
                    if (!isDownload) {
                        long endTimeMillis = Long.parseLong(newInfo.getEndTime());
                        //添加的条件必须是该图片的结束时间大于当前时间
                        if (currentTimeMillis < endTimeMillis) {
                            downloadWelcomeImage(newInfo);
                        }
                    }
                }
                clearExpiredImage();
            }
        }, true);
	}
	
	/**
	 * 清除已经过期的本地缓存图片
	 */
	private static void clearExpiredImage(){
		ArrayList<WelcomeImageInfo> localInfos = getLocalWelcomeImageList();
		long currentTimeMillis = System.currentTimeMillis();
		for(int i = 0; localInfos != null && i < localInfos.size(); i++){
			WelcomeImageInfo imageInfo = localInfos.get(i);
			long endTimeMillis = Long.parseLong(imageInfo.getEndTime());
			if(currentTimeMillis > endTimeMillis){
				getWelcomeImageFile(imageInfo.getPath()).delete();
			}
		}
	}
	
	/**
	 * 通过最新的图片信息更新本地的图片信息
	 * @param newInfo
	 * @param localInfo
	 */
	private static void updateWelcomeImageInfo(WelcomeImageInfo newInfo, WelcomeImageInfo localInfo){
		//判断服务器的图片MD5值是否与本地一致，如果不一致证明网络图片被替换过，直接删除本地图片，下载最新的图片
		if(!newInfo.getPicCode().equals(localInfo.getPicCode())){
			getWelcomeImageFile(localInfo.getPath()).delete();
			downloadWelcomeImage(newInfo);
		}else if(!newInfo.getName().equals(localInfo.getName())
				|| !newInfo.getStartTime().equals(localInfo.getStartTime())
				|| !newInfo.getEndTime().equals(localInfo.getEndTime())
				|| !newInfo.getStatus().equals(localInfo.getStatus())){
			//如果图片本事没被替换，只是更改了其他图片信息，则直接重命名本地图片名字用于存储图片信息
			
			File updateFile = getWelcomeImageFile(localInfo.getPath());
			String filePath = updateFile.getAbsolutePath().substring(0, updateFile.getAbsolutePath().lastIndexOf(File.separator));
			File renameFile = new File(filePath+File.separator+generateFileName(newInfo));
			updateFile.renameTo(renameFile);
		}
	}
	
	/**
	 * 把网络图片下载到本地的缓存目录
	 * 把下载的图片信息拼接作为图片的文件名，再获取图片时通过分析图片文件名获得图片信息
	 * @param newInfo
	 */
	private static void downloadWelcomeImage(WelcomeImageInfo newInfo){
		FileOutputStream fos = null;
		InputStream is = null;
		try {
			URL url = new URL(newInfo.getPath());
			is = url.openStream();
			byte datas[] = new byte[1024*1];
			File newPicFile = getWelcomeImageFile(generateFileName(newInfo));
			fos = new FileOutputStream(newPicFile);
			int byteRead = -1;
			while((byteRead = is.read(datas)) != -1){
				fos.write(datas, 0 ,byteRead);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fos != null){
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 通过网络图片信息拼接图片名称
	 * @param info
	 * @return
	 */
	private static String generateFileName(WelcomeImageInfo info){
		StringBuffer sb = new StringBuffer();
		sb.append(info.getId());
		sb.append("#");
		sb.append(info.getName());
		sb.append("#");
		sb.append(info.getStartTime());
		sb.append("#");
		sb.append(info.getEndTime());
		sb.append("#");
		sb.append(info.getStatus());
		sb.append("#");
		sb.append(info.getPicCode());
		sb.append(IMAGE_SUFFIX);
		return sb.toString();
	}
	
	private static String convertTimeToLong(String time){
		SimpleDateFormat tempsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = tempsdf.parse(time);
			return date.getTime()+"";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}