package org.shirdrn.dw.es.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import com.google.common.collect.Lists;

public class DateTimeUtils {
	public static final String DATE_FORMAT = "yyyy-MM-dd";

	public static final String TIME_FORMAT = "HH:mm:ss";

	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.S";

	private static final long MILLS_PER_DAY = 1 * 24 * 60 * 60 * 1000;
	
	public static String format(Date date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}
	
	/**
     * 将字符串时间转换成日期格式
     * 
     * @param dateStr
     * @param pattern  日期格式，可以为空，为空精确到天
     * @return
     */
    public static Date parse(String dateStr, String pattern) {
        DateFormat format = null;
        if (pattern == null || "".equals(pattern)) {
            format = new SimpleDateFormat(DATE_FORMAT);
        } else {
            format = new SimpleDateFormat(pattern);
        }
        Date date = null;
        try {
            if (dateStr == null || "".equals(dateStr)) {
                date = null;
            } else {
                date = format.parse(dateStr);
            }
        } catch (Exception e) {
            System.out.println("解析错误");
        }
        return date;
    }

	public static String format(long timestamp, String format) {
		Date date = new Date(timestamp);
		return format(date, format);
	}
	
	public static String getHour(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int h = cal.get(Calendar.HOUR_OF_DAY);
		return (h < 10) ? ("0" + h) : (h + "");
	}

	public static String format(Date date, int field, int amount, String format) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.add(field, amount);
		return format(gc.getTime(), format);
	}
	/**
	 * 日期相减，精确到秒   date1-data2
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long subtract(Date date1, Date date2) {
	    Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance();
	    cal1.setTime(date1);
	    cal2.setTime(date2);
	    return (cal1.getTimeInMillis() - cal2.getTimeInMillis())/1000;
	}

	public static String getNowString() {
		Date date = getNowDate();
		return format(date, DATE_TIME_FORMAT);
	}

	public static Date getNowDate() {
		return new Date();
	}
	
	public static long getDaysBetween(String date1, String date2, String format) {
		Date d1 = DateTimeUtils.parse(date1, format);
		Date d2 = DateTimeUtils.parse(date2, format);
		return Math.abs((d2.getTime() - d1.getTime()) / MILLS_PER_DAY);
	}
	
	public static String format(String srcDatetime, String srcFormat, String dstFormat) {
		Date d = parse(srcDatetime, srcFormat);
		return format(d, dstFormat);
	}
	
	public static LinkedList<String> getLatestHours(int nHours, String format) {
		LinkedList<String> hours = Lists.newLinkedList();
		Calendar c = Calendar.getInstance();
		for (int i = 0; i < nHours; i++) {
			c.add(Calendar.HOUR_OF_DAY, -1);
			hours.add(format(c.getTime(), format));
		}
		return hours;
	}
	
	public static int getDiffToNearestHour() {
		return getDiffToNearestHour(System.currentTimeMillis());
	}
	
	public static int getDiffToNearestHour(long timestamp) {
		Date date = new Date(timestamp);
		return getDiffToNearestHour(date);
	}
	
	@SuppressWarnings("deprecation")
	public static int getDiffToNearestHour(Date date) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR_OF_DAY, 1);
		Date toDate = c.getTime();
		toDate.setMinutes(0);
		toDate.setSeconds(0);
		return (int) (toDate.getTime() - date.getTime());
	}

	public static String getDateStrMinute(String date, String pattern) {
		Date d = parse(date, pattern);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		String monthStr = (month + 1) < 10 ? "0" + (month + 1) : (month + 1) + "";
		String dayStr = day < 10 ? "0" + day : day + "";
		String hourStr = hour < 10 ? "0" + hour : hour + "";
		String minuteStr = minute < 10 ? "0" + minute : minute + "";
		return  year + monthStr + dayStr + hourStr + minuteStr;
	}
	
	public static long getTimestamp(String date, String format) {
		return parse(date, format).getTime();
	}

	public static void main(String[] args) {
		System.out.println(parse("2015-06-29 10:00:00", DATE_TIME_FORMAT).getTime());
		System.out.println(parse("2015-06-29 10:01:00", DATE_TIME_FORMAT).getTime());
		System.out.println(parse("2015-06-29 10:02:00", DATE_TIME_FORMAT).getTime());
		
		long a = DateTimeUtils.getTimestamp("2014-01-12", DateTimeUtils.DATE_FORMAT) / 1000;
		long b = DateTimeUtils.getTimestamp("2014-01-12  00:00:00", DateTimeUtils.DATE_TIME_FORMAT) / 1000;
		System.out.println(a+":"+b);
		
		System.out.println(format(Long.parseLong("1429232317000"), "yyyy-MM-dd HH:mm:ss"));
	}
}
