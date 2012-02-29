package com.dankronstal.dailyexpenses;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dankronstal.dailyexpenses.R;
import com.dankronstal.dailyexpenses.data.Expense;
import com.dankronstal.dailyexpenses.data.ExpenseContentProvider;
import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;

public class ExpenseReporting extends Activity {
	
	private static String ASCENDING = " ASC ";
	private static String DESCENDING = " DESC ";
		
	private String currentlySortedBy = ExpenseContentProvider.DATEINCURRED;
	
	protected TextView txtFirstDate;
	protected TextView txtLastDate;
	protected TextView txtAvgSpend;
	protected LinearLayout layoutReport;
	protected TableLayout tblExpenses;
	
	protected ExpenseContentProvider ecp = new ExpenseContentProvider();
	
	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);
		
		tblExpenses = (TableLayout) findViewById(R.id.tblExpenses);
		tblExpenses.removeAllViews();
		ArrayList<Expense> expenses = getAllExpenses(currentlySortedBy);
		bindReportTable(expenses);	
				
		try{
		graphViewTest(expenses);
		}catch(Exception e){
			Log.getStackTraceString(e);
		}
		
		
	}

	/**
	 * http://www.jjoe64.com/p/graphview-library.html
	 * @param expenses
	 */
	private void graphViewTest(ArrayList<Expense> expenses) {
		int[] colors = new int[]{ 
				Color.rgb(200, 50, 00),
				Color.rgb(90, 250, 00),
				Color.rgb(90, 250, 250)
		};
		//get unique labels
		ArrayList<String> categories = new ArrayList<String>();
        if(expenses.size() > 0){
        	for(Expense e : expenses)
			if(!categories.contains(e.getCategory()))
				categories.add(e.getCategory());
        }
        
        ArrayList<GraphViewSeries> gvs = new ArrayList();
        for(int i = 0; i < categories.size(); i++){
        	GraphViewData[] gvd = new GraphViewData[expenses.size()];
        	for(int j = 0; j < expenses.size(); j++){
        		if(expenses.get(j).getCategory().equals(categories.get(i))){
        			gvd[j] = new GraphViewData(Double.valueOf(j), expenses.get(j).getAmount());        				
        		}else{
        			gvd[j] = new GraphViewData(Double.valueOf(j), 0.0d);
        		}
        			
        	}
        	gvs.add(new GraphViewSeries(categories.get(i), colors[i], gvd));
        }
       		
		GraphViewData[] data = new GraphViewData[expenses.size()];
		String[] labels = new String[expenses.size()];
		for(int i = 0; i < expenses.size(); i++){
			data[i] = new GraphViewData(Double.valueOf(String.valueOf(i)), expenses.get(i).getAmount());
			labels[i] = DateHelper.makeShortDate(expenses.get(i).getDateIncurred());
		}
		GraphViewSeries dataSeries = new GraphViewSeries(data);
		  
		GraphView graphView = new BarGraphView(  
		      this // context  
		      , "Spend Trend" // heading  
		);
	    for(GraphViewSeries series : gvs){
	    	graphView.addSeries(series); // data 
	    }
		//graphView.setHorizontalLabels(labels);  
		//graphView.setVerticalLabels(new String[]{"More", "Some", "Less"});
		
//		graphView.setShowLegend(true);  
//		graphView.setLegendAlign(LegendAlign.BOTTOM);  
//		graphView.setLegendWidth(200); 
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.layoutReport);
		layout.addView(graphView);
	}

	/**
	 * @param expenses
	 */
	private void bindReportTable(ArrayList<Expense> expenses) {
		if(expenses.size() > 0)
		{
			//title row:
			TableRow row = (TableRow)LayoutInflater.from(ExpenseReporting.this).inflate(R.layout.report_row, null);
			((TextView)row.findViewById(R.id.txtRowID)).setText("hidden_id");
			((TextView)row.findViewById(R.id.txtRowCurrency)).setText("hidden_currency");
			((TextView)row.findViewById(R.id.txtRowExchange)).setText("hidden_exchange");
			((TextView)row.findViewById(R.id.txtRowCategory)).setText(ExpenseContentProvider.CATEGORY);
			((TextView)row.findViewById(R.id.txtRowCategory)).setOnClickListener(lReportTitleCategoryClicked);
			((TextView)row.findViewById(R.id.txtRowAmount)).setText(ExpenseContentProvider.AMOUNT);
			((TextView)row.findViewById(R.id.txtRowAmount)).setOnClickListener(lReportTitleAmountClicked);
			((TextView)row.findViewById(R.id.txtRowDate)).setText(ExpenseContentProvider.DATEINCURRED);
			((TextView)row.findViewById(R.id.txtRowDate)).setOnClickListener(lReportTitleDateClicked);
			tblExpenses.addView(row);			
			
			//following from http://stackoverflow.com/questions/5183968/how-to-add-rows-dynamically-into-table-layout
			for(Expense e : expenses){				
			    try {
			    	// Inflate row "template" and fill out the fields.
					row = (TableRow)LayoutInflater.from(ExpenseReporting.this).inflate(R.layout.report_row, null);
					((TextView)row.findViewById(R.id.txtRowID)).setText(Integer.toString(e.getId()));
					((TextView)row.findViewById(R.id.txtRowCategory)).setText(e.getCategory());
					((TextView)row.findViewById(R.id.txtRowAmount)).setText(ExpenseContentProvider.MONEY_FORMAT.format(e.getAmount()));//TODO: fix magic string
					((TextView)row.findViewById(R.id.txtRowCurrency)).setText(e.getCurrency());
					((TextView)row.findViewById(R.id.txtRowExchange)).setText(Double.toString(e.getExchange()));
					((TextView)row.findViewById(R.id.txtRowDate)).setText(e.getDateIncurred());
					//row.setOnClickListener(lEditRowClicked);
					tblExpenses.addView(row);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			tblExpenses.requestLayout();     // Not sure if this is needed.
		}
	}

	/**
	 * 
	 * @return
	 */
	private ArrayList<Expense> getAllExpenses(String sortOrder) {
		String[] sortBits = currentlySortedBy.split(" ");
		if(sortBits[0].compareTo(sortOrder) == 0)
			if(sortBits.length == 1)
				sortOrder = sortOrder + DESCENDING;
			else
				sortOrder += currentlySortedBy.indexOf(DESCENDING) > 0 ? ASCENDING : DESCENDING;
		currentlySortedBy = sortOrder;
		Cursor c = getContentResolver().query(ExpenseContentProvider.CONTENT_URI, ExpenseContentProvider.FULL_PROJECTION, null, null, sortOrder);
		ArrayList<Expense> expenses = new ArrayList<Expense>();
		if (c.moveToFirst()) {
	    	do{
				expenses.add(new Expense(
						c.getInt(0),
						c.getDouble(1),
						c.getString(2),
						c.getString(3),							
						c.getString(4),
						c.getDouble(5)));
			}while(c.moveToNext());
	    }
	    c.close();
		return expenses;
	}
	
	private OnClickListener lReportTitleCategoryClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {			
			tblExpenses = (TableLayout) findViewById(R.id.tblExpenses);
			tblExpenses.removeAllViews();
			ArrayList<Expense> expenses = getAllExpenses(ExpenseContentProvider.CATEGORY);
			bindReportTable(expenses);
		}
	};
	
	private OnClickListener lReportTitleAmountClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			tblExpenses = (TableLayout) findViewById(R.id.tblExpenses);
			tblExpenses.removeAllViews();
			ArrayList<Expense> expenses = getAllExpenses(ExpenseContentProvider.AMOUNT);
			bindReportTable(expenses);
		}
	};
	
	private OnClickListener lReportTitleDateClicked = new OnClickListener() {
		@Override
		public void onClick(View v) {
			tblExpenses = (TableLayout) findViewById(R.id.tblExpenses);
			tblExpenses.removeAllViews();
			ArrayList<Expense> expenses = getAllExpenses(ExpenseContentProvider.DATEINCURRED);
			bindReportTable(expenses);
		}
	};
}