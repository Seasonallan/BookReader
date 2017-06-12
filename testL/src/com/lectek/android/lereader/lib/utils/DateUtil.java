package com.lectek.android.lereader.lib.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
 

/*
 * author xzz  2012-03-08
 * 参数： strSrc 表示目标日期  strNow是当前系统日期
 * 返回值：0表示今天，1表示昨天，间隔年份为2，其它-1，异常-2, 
 * */

public class DateUtil {
	private static final int NOW_DAY = 0;
	private static final int YESTER_DAY = 1;
	private static final int LAST_YEAR = 2;
	private static final int OTHER_DAY = -1;
	private static final int ERROR_DAY = -2;	
	
	public static int dateCompare(String strSrc) {
 
		try {
			// 设定时间的模板
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date srcDay = sdf.parse(strSrc);
			return dateCompareResult(srcDay);
		 
		} catch (Exception e) {
			return ERROR_DAY;
		}
	}
	  
	public static int dateCompareWithChinese(String strSrc) {
		 
		try {
			// 设定时间的模板
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			Date srcDay = sdf.parse(strSrc);
			return dateCompareResult(srcDay);
		 
		} catch (Exception e) {
			return ERROR_DAY;
		}
	}
	
	public static int dateCompareResult(Date srcDay) {
		try { 
			// 设定时间的模板
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 得到指定模范的时间
			Date nowDay = new Date();
			
			int year = (nowDay.getYear()- srcDay.getYear());
			if(year > 0){
				return LAST_YEAR;
			}  
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(nowDay);
			int todayInYear = calendar.get(Calendar.DAY_OF_YEAR);
			calendar.setTime(srcDay);
			int srcdayInYear = calendar.get(Calendar.DAY_OF_YEAR);
			int days = todayInYear - srcdayInYear;
			if(days == 0){
				return NOW_DAY;
			}else if(days == 1){
				return YESTER_DAY;
			}else{
				return OTHER_DAY;
			} 
		} catch (Exception e) {
			return ERROR_DAY;
		}
	}
	
	/**
	 * 获取当前时间
	 * */
	public static String getNowDay(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String day = sdf.format(date);
		return day;
	}
	
	/**
	 * 获取 完整格式时间
	 * @param date
	 * @return
	 */
	public static String getNowDayYMDHMS(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String day = sdf.format(date);
		return day;
	}
	
	/**
	 * 取得系统当前的时间.格式yyyyMMddHHmmss
	 * @return
	 */
	public static String getCurrentDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar calendar = Calendar.getInstance();
		String curDateString = dateFormat.format(calendar.getTime());
		return curDateString;
	}
	
	/**
	 * 取得系统当前的时间 yyyy-MM-dd HH-mm 格式
	 * @return
	 */
	public static String getCurrentTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
		Calendar calendar = Calendar.getInstance();
		String curDateString = dateFormat.format(calendar.getTime());
		return curDateString;
	}
	
	/**
	 * 取得系统当前的时间 yyyy-MM-dd HH:mm:ss 格式
	 * @return
	 */
	public static String getCurrentTimeStyle1() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		String curDateString = dateFormat.format(calendar.getTime());
		return curDateString;
	}
	
	/**
	 * 取得系统当前的时间 MM-dd HH:mm 格式
	 * @return
	 */
	public static String getCurrentTimeByMDHM() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
		Calendar calendar = Calendar.getInstance();
		String curDateString = dateFormat.format(calendar.getTime());
		return curDateString;
	}
	
	/**
	 * 判断目标时间，若是当前年份，则省略年。若不是则要包括对应年份。
	 * @param targetTime
	 * @return
	 */
	public static String filterYearTag(long targetTime){
		SimpleDateFormat sdf = null;
		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		if (targetTime > getBeginTime(currentYear)) {
			//指定时间大于年初第一天。则去除年份标识。
			sdf = new SimpleDateFormat("MM-dd HH:mm");
		}else {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}
		return sdf.format(new Date(targetTime));
	}
	
	/**
	 * 获取一年的起始时间
	 * @param year
	 * @return
	 */
	public static long getBeginTime(int year) {
		return getTime(year, 1, 1);
	}
	
	/**
	 * 根据年月日获取时间
	 * @param year
	 * @param month
	 * @param date
	 * @return
	 */
	public static long getTime(int year, int month, int date) {
		return getTime(year, month, date, 0, 0, 0);
	}
	
	/**
	 * 根据年月日时分秒获取时间
	 * @param year
	 * @param month
	 * @param date
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static long getTime(int year, int month, int date, int hour, int minute, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1 + Calendar.JANUARY);
		calendar.set(Calendar.DATE, date);
		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		return calendar.getTimeInMillis();
	}
	
	/**将时间格式为yyyy-MM-dd HH:mm的字符串 转换为 毫秒数*/
	public static long getFormatTimeInMillis(String formatTime){
		if (TextUtils.isEmpty(formatTime)) {
			return 0;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date d;
		try {
			d = sdf.parse(formatTime);
			return d.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	
	/**
	 * 是否满足访问书城的条件
	 * @param context
	 * @return
	 */
	public static boolean isAvoidAccessBookCity(Context context, String dealine){
		String deadlineStr = dealine;
		Calendar c = Calendar.getInstance();
		String currentTimeStr = getNowDay(c.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			long deadlineTime = ((Date)sdf.parse(deadlineStr)).getTime();
			long currentTime = ((Date)sdf.parse(currentTimeStr)).getTime();
			if (deadlineTime <= currentTime) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static String getTimeStr(int seconds) {
		int min = seconds % 3600 / 60;
		int sec = seconds % 60;
		int hour = seconds / 3600;
		String timeStr = "";
		if(hour > 0){
			if(hour < 10){
				timeStr += "0" + hour + ":";
			}else{
				timeStr += hour + ":";
			}
		}
		if(min < 10){
			timeStr += "0" + min;
		}else{
			timeStr += min;
		}
		if (sec < 10){
			timeStr += ":0" + sec;// 把音乐播放的进度，转换成时间
		}else{
			timeStr += ":" + sec;
		}
		return timeStr;
	}
	
	/**
	 * 获取时间方法，规则如下：
	 * 		时间雨系统时间处于统一年份显示：月、日、小时、分钟 (如：01-01 12:00)
	 * 		其他情况显示：年、月、日、小时、分钟(如：2014-01-01 12:00)
	 * @param time 传入的时间字符串格式必须为 "yyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static String getFormateTimeString(String time){
		Date nowDate = new Date(System.currentTimeMillis());
		SimpleDateFormat tempsdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
		Date date;
		String finaltime = "";
		try {
			date = tempsdf.parse(time);
			if(nowDate.getYear() == date.getYear()){
				SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
				finaltime = sdf.format(date);
			}else{
				SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm");
				finaltime = sdf.format(date);
				return finaltime;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return finaltime;
	}
	
	/**
	 * 根据出生日期获取年龄
	 * @param time
	 * @return
	 */
	public static int getAgeByBirthday(String time){
		int age = 0;
		Date nowDate = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		Date temp;
		try {
			temp = sdf.parse(time);
			int year = temp.getYear();
			int nowYear = nowDate.getYear();
			int month = temp.getMonth();
			int nowMonth = nowDate.getMonth();
			if(nowMonth < month){
				age = nowYear - year;
			}else{
				age = nowYear - year - 1;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return age;
		
	}
	
	/**
	 * 获取出生日期选择器的年份List
	 * @return
	 */
	public static List<String> getYearList(){
		Date date = new Date(System.currentTimeMillis());
		int year = date.getYear();
		List<String> yearList = new ArrayList<String>();
		int length = 100;	//获取多少年的年数
		for (int i = 0; i < length; i++) {
			int temp = year - i;
			yearList.add(temp + "");
		}
		return yearList;
	}
	
	/**
	 * 获取出生日期选择器的月份List
	 * @return
	 */
	public static List<String> getMonthList(){
		List<String> monthList = new ArrayList<String>();
		int length = 12;	//每年的月份数
		for (int i = 1; i <= length; i++) {
			monthList.add(i + "");
		}
		return monthList;
	}
	
	/**
	 * String转换成Date (日期格式yyyy-MM-dd HH:mm:ss)
	 * @param date
	 * @return
	 */
	public static Date parseDate(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date srcDay = null;
		try {
			srcDay = sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return srcDay;
	}
	
	
}
