/**
 * 
 */
package com.dankronstal.dailyexpenses.data;

/**
 * @author DKronsta
 * Data object representing an Expense record
 */
public class Expense {	

	private int id;
	private double amount;
	private String category;
	private String currency;
	private String dateIncurred;
	private double exchange;
	
	public Expense(int id, double amount, String category, String currency,
			String dateIncurred, double exchange) {
		this.id = id;
		this.amount = amount;
		this.category = category;
		this.currency = currency;
		this.dateIncurred = dateIncurred;
		this.exchange = exchange;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the amount
	 */
	public double getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	/**
	 * @return the date
	 */
	public String getDateIncurred() {
		return dateIncurred;
	}
	/**
	 * @param date the date to set
	 */
	public void setDateIncurred(String dateIncurred) {
		this.dateIncurred = dateIncurred;
	}
	/**
	 * @return the exchange
	 */
	public double getExchange() {
		return exchange;
	}
	/**
	 * @param exchange the exchange to set
	 */
	public void setExchange(double exchange) {
		this.exchange = exchange;
	}	
}
