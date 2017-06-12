package com.lectek.android.lereader.lib.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.lectek.android.lereader.lib.net.http.AbsConnect;
import com.lectek.android.lereader.lib.net.http.HttpUtil;

public class BitmapUtil {

	private static final String TAG = "BitmapUtil";
	private static final int BUFFER_SIZE = 4 * 1024;

	public static Bitmap getImageInSdcard(Context context,
			String bookTempImagePath, String imageName, boolean autoClip) {
		Bitmap result = null;
		String filePath = bookTempImagePath + imageName;
		if (!FileUtil.isFileExists(filePath)) {
			return null;
		}

		if (autoClip) {

			InputStream is = null;
			try {
				is = new FileInputStream(filePath);
				result = BitmapUtil.clipScreenBoundsBitmap(
						context.getResources(), is);
			} catch (FileNotFoundException fnf) {
				fnf.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException ioe) {
					}
				}
			}
		}

		if (result == null) {
			result = getImageInSdcard(bookTempImagePath, imageName);
		}

		return result;
	}

	/**
	 * 
	 * @param context
	 * @param bookTempImagePath
	 * @param imageName
	 * @return
	 */
	public static Bitmap getImageInSdcardAutoScale(Context context, String bookTempImagePath, String imageName) {
		Bitmap result = null;
		String filePath = bookTempImagePath + imageName;
		if (!FileUtil.isFileExists(filePath)) {
			return null;
		}
		LogUtil.i("BitmapUtil.getImageInSdcard", String.format("file path: %s", filePath));
		InputStream is = null;
		try {
			is = new FileInputStream(filePath);
			result = getImageFromInputStreamAutoScale(context, is);
			
		} catch (FileNotFoundException fnf) {
			fnf.printStackTrace();
			LogUtil.i("BitmapUtil.getImageInSdcard", fnf.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.i("BitmapUtil.getImageInSdcard", e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe) {
				}
			}
		}

		if (result == null) {
			result = getImageInSdcard(bookTempImagePath, imageName);
		}

		return result;
	}

	public static Bitmap getImageFromInputStreamAutoScale(Context context, InputStream is) {
		Bitmap result = null;
		if (is == null) {
			return null;
		}
		try {
			
			if (!is.markSupported()) {
				ByteArrayBuffer baos = new ByteArrayBuffer(1024);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					baos.append(buffer, 0, len);
				}
				is.close();
				is = new ByteArrayInputStream(baos.buffer(), 0, baos.length());
			}
			
			 BitmapFactory.Options opts = new BitmapFactory.Options();
			 opts.inJustDecodeBounds = true;
			 BitmapFactory.decodeStream(is, null, opts);
			LogUtil.i("BitmapUtil.getImageInSdcard", String.format("inWidth:%s inHeight: %s", opts.outWidth, opts.outHeight));
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = getScaleSampleSize(context, opts);
			 LogUtil.i("BitmapUtil.getImageInSdcard", String.format("scaleSample:%s", opts.inSampleSize));
			is.reset(); 
			 result = BitmapFactory.decodeStream(is, null, opts);
			
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e("BitmapUtil.getImageInSdcard", e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe) {
				}
			}
		}
		return result;
	}
	
	private static int getScaleSampleSize(Context context, Options options) {
		float imageW = options.outWidth;
		float imageH = options.outHeight;
		int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
		int screenHeight = context.getResources().getDisplayMetrics().heightPixels;
		int resultSampleSize = 1;
		int refSize = -1;
		//判断大图
		if(imageW / imageH > 1) {
			if(imageW >  screenWidth) {
				refSize = (int)(((screenWidth / imageW) * imageW) * ((screenWidth / imageW) * imageH));
				LogUtil.i("BitmapUtil.getImageInSdcard", String.format("outWidth:%s outHeight: %s", screenWidth, screenHeight));
				
			}else if(imageW < (int)(screenWidth * 0.8f) && (imageW > (int)(screenWidth * 0.5f))) {
				refSize = (int)(imageW * 0.5 * imageH * 0.5);
				LogUtil.i("BitmapUtil.getImageInSdcard", String.format("outWidth:%s outHeight: %s", imageW * 0.5, imageH * 0.5));
			}else if(imageW < (int)(screenWidth * 0.5f) && imageW > (int)(screenWidth * 0.25f) ) {
				refSize = (int)(imageW * 0.7 * imageH * 0.7);
				LogUtil.i("BitmapUtil.getImageInSdcard", String.format("outWidth:%s outHeight: %s", imageW * 0.7, imageH * 0.7));
			}
			
		}else {
			if(imageH >  screenHeight) {
				refSize = (int)(((screenHeight / imageH) * imageW) * ((screenHeight / imageH) * imageH));
				LogUtil.i("BitmapUtil.getImageInSdcard", String.format("outWidth:%s outHeight: %s", screenWidth, screenHeight));
			}else if(imageH < (int)(screenHeight * 0.7f) && (imageH > (int)(screenHeight * 0.4f))) {
				refSize = (int)(imageW * 0.5 * imageH * 0.5);
				LogUtil.i("BitmapUtil.getImageInSdcard", String.format("outWidth:%s outHeight: %s", imageW * 0.5, imageH * 0.5));
			}else if(imageH < (int)(screenHeight * 0.4f) && imageH > (int)(screenHeight * 0.15f) ) {
				refSize = (int)(imageW * 0.8 * imageH * 0.8);
				LogUtil.i("BitmapUtil.getImageInSdcard", String.format("outWidth:%s outHeight: %s", imageW * 0.7, imageH * 0.7));
			}
		}
		
		if(refSize > 0) {
			resultSampleSize = computeSampleSize(options, -1, refSize);
		}
		
		return resultSampleSize;
	}
	
	public static Bitmap getImageInSdcard(String bookTempImagePath,
			String imageName) {
		if (TextUtils.isEmpty(imageName)) {
			return null;
		}
		String filePath = bookTempImagePath + imageName;
		if (!FileUtil.isFileExists(filePath)) {
			return null;
		}
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);

		// TODO:

		// -----Test----
		// BitmapFactory.Options opts = new BitmapFactory.Options();
		// opts.inJustDecodeBounds = true;
		// BitmapFactory.decodeFile(filePath, opts);
		//
		// opts.inSampleSize = computeSampleSize(opts, -1, 124*165);//84-112
		// opts.inJustDecodeBounds = false;
		// Bitmap bitmap = BitmapFactory.decodeFile(filePath, opts);
		return bitmap;
	}

	/**
	 * 存储图片到缓存目录�?
	 *
	 * @param name
	 *            图片名字
	 * @param bitmap
	 *            保存的图�?
	 */
	public static String saveImage(String bookTempPath,
			String bookTempImagePath, String name, Bitmap bitmap) {
		if (!FileUtil.isSDcardExist()) {
			LogUtil.v(TAG, "sdcard is not exist!");
			return null;
		}
		String filePath = null;
		try {
			FileUtil.checkPath(bookTempPath, true);
			FileUtil.checkPath(bookTempImagePath, true);
			StringBuilder sb = new StringBuilder();
			sb.append(bookTempImagePath);
			sb.append(name);
			// sb.append(".png");
			filePath = sb.toString();
			sb = null;
			File imgFile = new File(filePath);
			if (!imgFile.exists()) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				// like PNG which is lossless, will ignore the quality setting
				// :png忽略第二个参数
				if (bitmap.compress(Bitmap.CompressFormat.PNG, 0, out)) {
					BufferedOutputStream os = new BufferedOutputStream(
							new FileOutputStream(imgFile));
					os.write(out.toByteArray());
					os.flush();
					os.close();
				}
			}
		} catch (IOException e) {
			filePath = null;
			LogUtil.e(TAG, "save image err", e);
		}
		return filePath;
	}

	public static Bitmap fillBitmap(Bitmap bitmap, int mWidth, int mHeight) {
		if (bitmap == null) {
			return bitmap;
		}
		int imgW = bitmap.getWidth();
		int imgH = bitmap.getHeight();
		if (imgH == mHeight && imgW == mWidth) {
			return bitmap;
		} else {
			Rect srcR = new Rect(0, 0, imgW, imgH);
			Rect dstR = new Rect(0, 0, mWidth, mHeight);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			Bitmap tempBitmap = Bitmap.createBitmap(mWidth, mHeight,
					Config.RGB_565);
			final Canvas canvas = new Canvas(tempBitmap);
			canvas.save();
			canvas.drawBitmap(bitmap, srcR, dstR, paint);
			canvas.restore();
			return tempBitmap;
		}
	}

	public static void drawBitmap(Canvas canvas, Bitmap bitmap, int mWidth,
			int mHeight, Paint paint) {
		drawBitmap(canvas, bitmap, 0, 0, mWidth, mHeight, paint);
	}

	public static void drawBitmap(Canvas canvas, Bitmap bitmap, float left,
			float top, int mWidth, int mHeight, Paint paint) {
		if (canvas == null || bitmap == null) {
			return;
		}

		int imgW = bitmap.getWidth();
		int imgH = bitmap.getHeight();
		if (imgH == mHeight && imgW == mWidth) {
			canvas.drawBitmap(bitmap, left, top, paint);
		} else {
			Rect srcR = new Rect(0, 0, imgW, imgH);
			srcR.offset((int) left, (int) top);
			Rect dstR = new Rect(0, 0, mWidth, mHeight);
			dstR.offset((int) left, (int) top);
			canvas.drawBitmap(bitmap, srcR, dstR, paint);
		}
	}

	public static Bitmap getBitmap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		// opt.inSampleSize = 2;
		// 获取资源图片
		Bitmap bitmap = null;
		try {
			InputStream is = context.getResources().openRawResource(resId);
			bitmap = BitmapFactory.decodeStream(is, null, opt);
			is.close();
		} catch (IOException e) {
		}
		// bitmap = BitmapFactory.decodeResource(context.getResources(), resId,
		// opt);
		return bitmap;
	}

	// public static Bitmap downloadBitmap(String url) {
	// if (TextUtils.isEmpty(url)) {
	// return null;
	// }
	// LogUtil.v(TAG, "download bitmap url: " + url);
	// Bitmap bm = null;
	// DefaultHttpClient httpClient =
	// AbsConnect.getDefaultHttpClient(getContext());
	// try {
	// HttpGet httpGet = HttpConnect.getHttpGet(url);
	// HttpResponse httpResponse = httpClient.execute(httpGet);
	// HttpEntity httpEntiry = httpResponse.getEntity();
	// if (httpEntiry != null) {
	// InputStream is = httpEntiry.getContent();
	// byte[] responseByteArray = new byte[BUFFER_SIZE];
	// ByteArrayBuffer bab = new ByteArrayBuffer(BUFFER_SIZE);
	// int line = -1;
	// while ((line = is.read(responseByteArray)) != -1) {
	// bab.append(responseByteArray, 0, line);
	// responseByteArray = new byte[BUFFER_SIZE];
	// }
	// LogUtil.e("---download--length= "+bab.length());
	// bm = BitmapFactory.decodeByteArray(bab.toByteArray(), 0, bab.length());
	// bab.clear();
	// is.close();
	// }
	// } catch (Exception e) {
	// LogUtil.e(TAG, "download Bitmap Error!", e);
	// } finally {
	// if (httpClient != null) {
	// httpClient.getConnectionManager().shutdown();
	// httpClient = null;
	// }
	// }
	// LogUtil.v(TAG, "download Bitmap="+bm);
	// return bm;
	// }

	public static Bitmap downloadBitmap(String url, long maxSize,
			Context context) {
		if (TextUtils.isEmpty(url)) {
			return null;
		}
		LogUtil.v(TAG, "download bitmap url: " + url);
		boolean isSizeOut = false;
		Bitmap bm = null;
		DefaultHttpClient httpClient = AbsConnect.getDefaultHttpClient(context);
		InputStream is = null;
		try {
			HttpGet httpGet = HttpUtil.getHttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntiry = httpResponse.getEntity();
			Header h = httpResponse.getFirstHeader("Content-Length");
			if (h != null && h.getValue() != null
					&& TextUtils.isDigitsOnly(h.getValue())
					&& Integer.valueOf(h.getValue()) > maxSize) {
				isSizeOut = true;
			}
			if (httpEntiry != null) {
				is = httpEntiry.getContent();
				if (!isSizeOut) {
					byte[] responseByteArray = new byte[BUFFER_SIZE];
					ByteArrayBuffer bab = new ByteArrayBuffer(BUFFER_SIZE);
					int line = -1;
					while ((line = is.read(responseByteArray)) != -1) {
						if (bab.length() > maxSize) {
							isSizeOut = true;
							break;
						}
						bab.append(responseByteArray, 0, line);
						responseByteArray = new byte[BUFFER_SIZE];
					}
					if (!isSizeOut) {
						bm = BitmapFactory.decodeByteArray(bab.toByteArray(),
								0, bab.length());
					}
					bab.clear();
				} else {
					bm = clipScreenBoundsBitmap(context.getResources(), is);
				}
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "download Bitmap Error!", e);
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		LogUtil.v(TAG, "download Bitmap=" + bm);
		return bm;
	}

	public static Bitmap downloadBitmapToLocal(Context context, String bookTempPath, 
								String bookTempImagePath,String imageUrl, String imageId) {
		if (TextUtils.isEmpty(imageUrl)) {
			return null;
		}
		LogUtil.v(TAG, "download bitmap url: " + imageUrl);
		Bitmap bm = null;
		DefaultHttpClient httpClient = AbsConnect.getDefaultHttpClient(context);
		InputStream is = null;
		ByteArrayInputStream bis = null;
		try {
			HttpGet httpGet = HttpUtil.getHttpGet(imageUrl);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntiry = httpResponse.getEntity();
			
			if (httpEntiry != null) {
				is = httpEntiry.getContent();
				
				byte[] responseByteArray = new byte[BUFFER_SIZE];
				ByteArrayBuffer bab = new ByteArrayBuffer(BUFFER_SIZE);
				int line = -1;
				while ((line = is.read(responseByteArray)) != -1) {
					bab.append(responseByteArray, 0, line);
					responseByteArray = new byte[BUFFER_SIZE];
				}
				bis = new ByteArrayInputStream(bab.buffer(), 0, bab.length());
				bm = getImageFromInputStreamAutoScale(context, bis);
				bab.clear();
				try {
					BitmapUtil.saveImage(bookTempPath,bookTempImagePath,imageId, bm);
				}catch(Exception e) {}
			}
			
		} catch (Exception e) {
			LogUtil.e(TAG, "download Bitmap Error!", e);
		} finally {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {}
			}
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {}
			}
		}
		LogUtil.v(TAG, "download Bitmap=" + bm);
		return bm;
	}
	
	public static Bitmap clipScreenBoundsBitmap(Resources resources,
			InputStreamProvider isProvider) throws IOException {
		return clipScreenBoundsBitmap(resources, isProvider, 1);
	}

	public static Bitmap clipScreenBoundsBitmap(Resources resources,
			InputStreamProvider isProvider, float scaled) throws IOException {
		InputStream is = isProvider.newInputStream();
		Bitmap bitmap = null;
		if (is != null) {
			DisplayMetrics display = resources.getDisplayMetrics();
			Options opts = new BitmapFactory.Options();
			opts.inScaled = false;
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, opts);
			int targetW = (int) (display.widthPixels * scaled);
			int targetH = (int) (display.heightPixels * scaled);
			int imgW = opts.outWidth;
			int imgH = opts.outHeight;
			is.close();
			is = isProvider.newInputStream();
			if (is != null) {
				bitmap = clipBitmap(resources, is, targetW, targetH, imgW, imgH);
				is.close();
			}
		}
		return bitmap;
	}

	public static Bitmap clipScreenBoundsBitmap(Resources resources,
			InputStream is) throws IOException {
		return clipScreenBoundsBitmap(resources, is, 1);
	}

	public static Bitmap clipScreenBoundsBitmap(Resources resources,
			InputStream is, float scaled) throws IOException {
		if (is != null) {
			if (!is.markSupported()) {
				ByteArrayBuffer baos = new ByteArrayBuffer(1024);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = is.read(buffer)) != -1) {
					baos.append(buffer, 0, len);
				}
				is.close();
				is = new ByteArrayInputStream(baos.buffer(), 0, baos.length());
			}
			DisplayMetrics display = resources.getDisplayMetrics();
			Options opts = new BitmapFactory.Options();
			opts.inScaled = false;
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is, null, opts);
			int targetW = (int) (display.widthPixels * scaled);
			int targetH = (int) (display.heightPixels * scaled);
			int imgW = opts.outWidth;
			int imgH = opts.outHeight;
			is.reset();
			Bitmap bitmap = clipBitmap(resources, is, targetW, targetH, imgW,
					imgH);
			is.close();
			return bitmap;
		}
		return null;
	}

	public static Bitmap clipBitmap(Resources resources, InputStream is,
			int targetW, int targetH, int imgW, int imgH) {
		if (is != null) {
			DisplayMetrics display = resources.getDisplayMetrics();
			Options opts = new BitmapFactory.Options();
			float densityScaled = 1;
			float imgSize = imgW * imgH;
			float targetSize = targetW * targetH;
			if (imgSize > targetSize) {
				densityScaled = targetSize / imgSize;
			}
			opts = new BitmapFactory.Options();
			opts.inTargetDensity = display.densityDpi;
			opts.inDensity = (int) (display.densityDpi / densityScaled);
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inScaled = true;
			return BitmapFactory.decodeStream(is, null, opts);
		}
		return null;
	}

	public interface InputStreamProvider {
		public InputStream newInputStream();
	}

	/**
	 * 从文件获取Bitmap
	 *
	 * @param path
	 *            文件路径
	 * @param dstMaxWH
	 *            大小
	 */
	public static Bitmap getBitmapWithPath(String path, int dstMaxWH) {
		File srcFile = new File(path);

		// 路径文件不存在
		if (!srcFile.exists()) {
			return null;
		}

		try {
			// 打开源文件
			Bitmap srcBitmap;
			{
				java.io.InputStream is;
				is = new FileInputStream(srcFile);

				BitmapFactory.Options opts = getOptionsWithInSampleSize(
						srcFile.getPath(), dstMaxWH);
				srcBitmap = BitmapFactory.decodeStream(is, null, opts);
				is.close();
				if (srcBitmap == null)
					return null;
				else {
					return srcBitmap;
				}
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取长宽都不超过160dip的图片，基本思想是设置Options.inSampleSize按比例取得缩略图
	 */
	public static Options getOptionsWithInSampleSize(String filePath,
			int maxWidth) {
		Options bitmapOptions = new Options();
		bitmapOptions.inJustDecodeBounds = true;// 只取得outHeight(图片原始高度)和
		// outWidth(图片的原始宽度)而不加载图片
		BitmapFactory.decodeFile(filePath, bitmapOptions);
		bitmapOptions.inJustDecodeBounds = false;
		int inSampleSize = bitmapOptions.outWidth / (maxWidth / 10);// 应该直接除160的，但这里出16是为了增加一位数的精度
		if (inSampleSize % 10 != 0) {
			inSampleSize += 10;// 尽量取大点图片，否则会模糊
		}
		inSampleSize = inSampleSize / 10;
		if (inSampleSize <= 0) {// 判断200是否超过原始图片高度
			inSampleSize = 1;// 如果超过，则不进行缩放
		}
		bitmapOptions.inSampleSize = inSampleSize;
		return bitmapOptions;
	}

	/**
	 * 拍照后读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
			double w = options.outWidth;
			double h = options.outHeight;
			int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
			int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
			if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
			}
			if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
			} else if (minSideLength == -1) {
			return lowerBound;
			} else {
			return upperBound;
			}
			}
	
	public static Bitmap drawable2Bitmap(Drawable drawable) {  
		if(drawable != null && drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0) {
			Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),
									drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);  
			Canvas canvas = new Canvas(bitmap);  
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());  
			drawable.draw(canvas);  
			return bitmap;  
		}
		
		return null;
	}  

	public static byte[] bitmap2Bytes(Bitmap bm) {  
		if(bm != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);  
			return baos.toByteArray();
		}
		return null;
	}
}
