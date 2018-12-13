package cn.sskbskdrin.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@SuppressLint("SimpleDateFormat")
public class DateUtils {

	private static final String datePattern = "yyyy-MM-dd";

	private static final String timePattern = "HH:mm";

	public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm";

	public static final String DATE_TIME_PATTERN2 = "yyyy-MM-dd";

	public static final String DATE_TIMES_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static final String DATE_TIMES_PATTERN_CN = "yyyy年M月d日 HH:mm:ss";

	public static final String DATE_TIME_PATTERN_CN = "M月d日 HH:mm";

	public static final String DATE_DATE_PATTERN_CN = "yyyy年M月d日";

	public static final String PATTERN_YEAR = "yyyy";

	public static final String PATTERN_MONTH = "MM";

	public static final String PATTERN_DAY = "dd";

	public static String getDatePattern() {
		return datePattern;
	}

	public static final String getDate(Date aDate) {
		SimpleDateFormat df = null;
		String returnValue = "";

		if (aDate != null) {
			df = new SimpleDateFormat(datePattern);
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}

	public static final Date convertStringToDate(String aMask, String strDate)
			throws ParseException {
		SimpleDateFormat df = null;
		Date date = null;

		df = new SimpleDateFormat(aMask);

		try {
			date = df.parse(strDate);
		} catch (ParseException pe) {
			throw new ParseException(pe.getMessage(), pe.getErrorOffset());
		}

		return (date);
	}

	public static String getTimeNow(Date theTime) {
		return getDateTime(timePattern, theTime);
	}

	public static Calendar getToday() throws ParseException {
		Date today = new Date();
		SimpleDateFormat df = new SimpleDateFormat(datePattern);

		String todayAsString = df.format(today);
		Calendar cal = new GregorianCalendar();
		cal.setTime(convertStringToDate(todayAsString));

		return cal;
	}

	public static final String getDateTime(String aMask, Date aDate) {
		SimpleDateFormat df = null;
		String returnValue = "";

		if (aDate == null) {
		} else {
			df = new SimpleDateFormat(aMask);
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}

	public static final String convertDateToString(Date aDate) {
		return getDateTime(datePattern, aDate);
	}

	public static final String convertDateToStringWithHHmm(Date aDate) {
		return getDateTime(DATE_TIME_PATTERN, aDate);
	}

	public static final String convertDateToStringWithHHmmss(Date aDate) {
		return getDateTime(DATE_TIMES_PATTERN, aDate);
	}

	public static Date convertStringToDate(String strDate)
			throws ParseException {
		Date aDate = null;

		try {

			aDate = convertStringToDate(datePattern, strDate);
		} catch (ParseException pe) {
			pe.printStackTrace();
			throw new ParseException(pe.getMessage(), pe.getErrorOffset());
		}

		return aDate;
	}

	public static byte[] convertStringToBytes(String value) {
		return value.getBytes();
	}

	public static byte convertStringToByte(String value) {
		if (value.getBytes().length > 0) {
			return value.getBytes()[0];
		} else {
			return "0".getBytes()[0];
		}
	}

	public static final String convertDateToString(String datePattern,
			Date aDate) {
		return getDateTime(datePattern, aDate);
	}

	public static final String getDateWithFormat(Date aDate, String format) {
		SimpleDateFormat df = null;
		String returnValue = "";

		if (aDate != null) {
			df = new SimpleDateFormat(format);
			returnValue = df.format(aDate);
		}

		return (returnValue);
	}

	/**
	 * covert a string object representing a date in the format "yyyy-mm-dd" to
	 * java.sql.Date
	 * 
	 * @param strDate
	 * @return
	 */
	public static java.sql.Date covertStringToSqlDate(String strDate) {

		if (strDate
				.matches("(19|20)\\d\\d-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])")) {
			return java.sql.Date.valueOf(strDate);
		} else {
			return null;
		}
	}

}
