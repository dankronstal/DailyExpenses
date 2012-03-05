/**
 * 
 */
package com.dankronstal.dailyexpenses;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.widget.DatePicker;

/**
 * @author DKronsta
 * Class to help switch date/string values for use in SQLite database
 */
public class DateHelper {
	
	public static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	
	/**
	 * Format date to conform to SQLite style: "yyyy-MM-dd HH:mm:ss.SSS".
	 * @param dateToConvert
	 * @return
	 */
	public static String DateToString(Date dateToConvert)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		return dateFormat.format(dateToConvert);
	}
	
	/**
	 * Format string from SQLite data to proper Date.
	 * @param stringToConvert
	 * @return
	 */
	public static Date StringToDate(String stringToConvert)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		try {
			return dateFormat.parse(stringToConvert);
		} catch (ParseException e) {
			return new Date();
		}
	}

	/**
	 * makeDateString:
	 * Receives properties from DatePicker object and returns date formatted in SQLite format.
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @return
	 */
	public static String makeDateString(int year, int month, int dayOfMonth) {
		String dateString = new String();
		dateString = makeShortDate(year, month, dayOfMonth);
		return dateString + " 12:00:00.000";
	}

	public static String makeShortDate(String todaysDate) {
		Date d = StringToDate(todaysDate);
		int month = d.getMonth() + 1;
		todaysDate = String.valueOf(d.getYear() + 1900) + 
				"-" + (month < 10 ? "0" + month : String.valueOf(month)) + 
				"-" + (d.getDate() < 10 ? "0" + d.getDate() : String.valueOf(d.getDate()));
		return todaysDate;
	}

	public static String makeShortDate(DatePicker datePicker) {
		int month = datePicker.getMonth() + 1;
		String theDate = String.valueOf(datePicker.getYear()) + 
				"-" + (month < 10 ? "0" + month : String.valueOf(month)) + 
				"-" + (datePicker.getDayOfMonth() < 10 ? "0" + datePicker.getDayOfMonth() : String.valueOf(datePicker.getDayOfMonth()));
		return theDate;
	}

	public static String makeShortDate(int year, int month, int dayOfMonth) {
		String theDate = String.valueOf(year) + 
				"-" + (month < 10 ? "0" + month : String.valueOf(month)) + 
				"-" + (dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth));
		return theDate;
	}
}
