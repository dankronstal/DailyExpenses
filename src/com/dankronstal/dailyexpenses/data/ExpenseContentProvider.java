/*
 ******************************************************************************
 * Parts of this code sample are licensed under Apache License, Version 2.0   *
 * Copyright (c) 2009, Android Open Handset Alliance. All rights reserved.    *
 *																			  *																			*
 * Except as noted, this code sample is offered under a modified BSD license. *
 * Copyright (C) 2010, Motorola Mobility, Inc. All rights reserved.           *
 * 																			  *
 * For more details, see MOTODEV_Studio_for_Android_LicenseNotices.pdf        * 
 * in your installation folder.                                               *
 ******************************************************************************
 */
package com.dankronstal.dailyexpenses.data;

import java.text.DecimalFormat;
import java.util.*;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.net.*;
import android.text.*;

import com.dankronstal.dailyexpenses.DateHelper;

public class ExpenseContentProvider extends ContentProvider {
	
	private ExpenseHelper dbHelper;
	private static HashMap<String, String> EXPENSE_PROJECTION_MAP;
	private static final String TABLE_NAME = "expense";
	private static final String AUTHORITY = "com.dankronstal.dailyexpenses.data.expensecontentprovider";

	public static DecimalFormat MONEY_FORMAT = new DecimalFormat("$0.00");
	public static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + TABLE_NAME);
	public static final Uri ID_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/id");
	public static final Uri AMOUNT_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/amount");
	public static final Uri CATEGORY_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/category");
	public static final Uri CURRENCY_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/currency");
	public static final Uri DATEINCURRED_FIELD_CONTENT_URI = Uri
			.parse("content://" + AUTHORITY + "/" + TABLE_NAME.toLowerCase()
					+ "/dateincurred");
	public static final Uri EXCHANGE_FIELD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME.toLowerCase() + "/exchange");

	public static final String DEFAULT_SORT_ORDER = "ID ASC";

	private static final UriMatcher URL_MATCHER;

	private static final int EXPENSE = 1;
	private static final int EXPENSE_ID = 2;
	private static final int EXPENSE_AMOUNT = 3;
	private static final int EXPENSE_CATEGORY = 4;
	private static final int EXPENSE_CURRENCY = 5;
	private static final int EXPENSE_DATEINCURRED = 6;
	private static final int EXPENSE_EXCHANGE = 7;

	// Content values keys (using column names)
	public static final String ID = "ID";
	public static final String AMOUNT = "Amount";
	public static final String CATEGORY = "Category";
	public static final String CURRENCY = "Currency";
	public static final String DATEINCURRED = "DateIncurred";
	public static final String EXCHANGE = "Exchange";

	public static final String[] FULL_PROJECTION = new String[]{
		ExpenseContentProvider.ID,
		ExpenseContentProvider.AMOUNT,
		ExpenseContentProvider.CATEGORY,
		ExpenseContentProvider.CURRENCY,
		ExpenseContentProvider.DATEINCURRED,
		ExpenseContentProvider.EXCHANGE}; 
	
	public boolean onCreate() {
		dbHelper = new ExpenseHelper(getContext(), true);
		return (dbHelper == null) ? false : true;
	}

	public Cursor query(Uri url, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteDatabase mDB = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (URL_MATCHER.match(url)) {
		case EXPENSE:
			qb.setTables(TABLE_NAME);
			qb.setProjectionMap(EXPENSE_PROJECTION_MAP);
			break;
		case EXPENSE_ID:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("id='" + url.getPathSegments().get(2) + "'");
			break;
		case EXPENSE_AMOUNT:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("amount='" + url.getPathSegments().get(2) + "'");
			break;
		case EXPENSE_CATEGORY:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("category='" + url.getPathSegments().get(2) + "'");
			break;
		case EXPENSE_CURRENCY:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("currency='" + url.getPathSegments().get(2) + "'");
			break;
		case EXPENSE_DATEINCURRED:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("dateincurred='" + url.getPathSegments().get(2)
					+ "'");
			break;
		case EXPENSE_EXCHANGE:
			qb.setTables(TABLE_NAME);
			qb.appendWhere("exchange='" + url.getPathSegments().get(2) + "'");
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
		String orderBy = "";
		if (TextUtils.isEmpty(sort)) {
			orderBy = DEFAULT_SORT_ORDER;
		} else {
			orderBy = sort;
		}
		Cursor c = qb.query(mDB, projection, selection, selectionArgs, null,
				null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), url);
		return c;
	}

	public String getType(Uri url) {
		switch (URL_MATCHER.match(url)) {
		case EXPENSE:
			return "vnd.android.cursor.dir/vnd.com.dailyexpenses.data.expense";
		case EXPENSE_ID:
			return "vnd.android.cursor.item/vnd.com.dailyexpenses.data.expense";
		case EXPENSE_AMOUNT:
			return "vnd.android.cursor.item/vnd.com.dailyexpenses.data.expense";
		case EXPENSE_CATEGORY:
			return "vnd.android.cursor.item/vnd.com.dailyexpenses.data.expense";
		case EXPENSE_CURRENCY:
			return "vnd.android.cursor.item/vnd.com.dailyexpenses.data.expense";
		case EXPENSE_DATEINCURRED:
			return "vnd.android.cursor.item/vnd.com.dailyexpenses.data.expense";
		case EXPENSE_EXCHANGE:
			return "vnd.android.cursor.item/vnd.com.dailyexpenses.data.expense";

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
	}

	public Uri insert(Uri url, ContentValues initialValues) {
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		long rowID;
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		if (URL_MATCHER.match(url) != EXPENSE) {
			throw new IllegalArgumentException("Unknown URL " + url);
		}

		rowID = mDB.insert("expense", "expense", values);
		if (rowID > 0) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		}
		throw new SQLException("Failed to insert row into " + url);
	}

	public int delete(Uri url, String where, String[] whereArgs) {
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		int count;
		String segment = "";
		switch (URL_MATCHER.match(url)) {
		case EXPENSE:
			count = mDB.delete(TABLE_NAME, where, whereArgs);
			break;
		case EXPENSE_ID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case EXPENSE_AMOUNT:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"amount="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case EXPENSE_CATEGORY:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"category="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case EXPENSE_CURRENCY:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"currency="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case EXPENSE_DATEINCURRED:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"dateincurred="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case EXPENSE_EXCHANGE:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.delete(TABLE_NAME,
					"exchange="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	public int update(Uri url, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase mDB = dbHelper.getWritableDatabase();
		int count;
		String segment = "";
		switch (URL_MATCHER.match(url)) {
		case EXPENSE:
			count = mDB.update(TABLE_NAME, values, where, whereArgs);
			break;
		case EXPENSE_ID:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"id="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case EXPENSE_AMOUNT:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"amount="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case EXPENSE_CATEGORY:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"category="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case EXPENSE_CURRENCY:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"currency="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case EXPENSE_DATEINCURRED:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"dateincurred="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case EXPENSE_EXCHANGE:
			segment = "'" + url.getPathSegments().get(2) + "'";
			count = mDB.update(TABLE_NAME, values,
					"exchange="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URL " + url);
		}
		getContext().getContentResolver().notifyChange(url, null);
		return count;
	}

	static {
		URL_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase(), EXPENSE);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/id" + "/*",
				EXPENSE_ID);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/amount"
				+ "/*", EXPENSE_AMOUNT);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/category"
				+ "/*", EXPENSE_CATEGORY);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/currency"
				+ "/*", EXPENSE_CURRENCY);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase()
				+ "/dateincurred" + "/*", EXPENSE_DATEINCURRED);
		URL_MATCHER.addURI(AUTHORITY, TABLE_NAME.toLowerCase() + "/exchange"
				+ "/*", EXPENSE_EXCHANGE);

		EXPENSE_PROJECTION_MAP = new HashMap<String, String>();
		EXPENSE_PROJECTION_MAP.put(ID, "id");
		EXPENSE_PROJECTION_MAP.put(AMOUNT, "amount");
		EXPENSE_PROJECTION_MAP.put(CATEGORY, "category");
		EXPENSE_PROJECTION_MAP.put(CURRENCY, "currency");
		EXPENSE_PROJECTION_MAP.put(DATEINCURRED, "dateincurred");
		EXPENSE_PROJECTION_MAP.put(EXCHANGE, "exchange");

	}
	
	/**
	 * getTodaysSpend:
	 * use a rawQuery to retrieve the total amount spent so far.
	 * @param context
	 * @param todaysDate
	 * @return
	 */
	public double getTodaysSpend(Context context, String todaysDate) {
		dbHelper = new ExpenseHelper(context, true);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		double spend = 0;
		//Cursor cursor = dbHelper.rawQuery("SELECT sum("+COLUM_NNAME+") FROM "+TABLE_NAME +" WHERE "+WHERE_CLAUSE , null);
		Cursor cursor = db.rawQuery("SELECT sum("+AMOUNT+") FROM "+TABLE_NAME+" WHERE "+DATEINCURRED+" like '" + DateHelper.makeShortDate(todaysDate) + "%'" , null);		
		cursor.moveToFirst();
		spend = cursor.getDouble(0);
		cursor.close();
		dbHelper.close();
		return spend;
	}
	
	/**
	 * getMinDate:
	 * return the most chronologically distant date in the database
	 * @param context
	 * @return
	 */
	public Date getMinDate(Context context){
		dbHelper = new ExpenseHelper(context, true);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT MIN(date(" + DATEINCURRED + ")) as minDate FROM " + TABLE_NAME, null);
		Date minDate = new Date();
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			String minDateString = cursor.getString(0);
			minDate = DateHelper.StringToDate(minDateString);
		}
		cursor.close();
		cursor.deactivate();
		dbHelper.close();
		return minDate;
	}
	
	/**
	 * 
	 * @param context
	 * @param selection ("where" clause)
	 * @return
	 */
	public ArrayList<Expense> getExpenseList(Context context, String selection){
		dbHelper = new ExpenseHelper(context, true);
		ArrayList<Expense> expenses = new ArrayList<Expense>();   
        try {
			String[] projection = new String[]{
					ExpenseContentProvider.ID,
					ExpenseContentProvider.AMOUNT,
					ExpenseContentProvider.CATEGORY,
					ExpenseContentProvider.CURRENCY,
					ExpenseContentProvider.DATEINCURRED,
					ExpenseContentProvider.EXCHANGE}; 
			Cursor results = query(ExpenseContentProvider.CONTENT_URI, projection, selection, null, null);
			if(results != null && results.getCount() > 0){
				results.moveToFirst();
				do{
					expenses.add(new Expense(
							results.getInt(0),
							results.getDouble(1),
							results.getString(2),
							results.getString(3),							
							results.getString(4),
							results.getDouble(5)));
				}while(results.moveToNext());
			}
			results.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        dbHelper.close();
        return expenses;
	}
}
