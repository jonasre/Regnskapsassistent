package com.jonasre.regnskapsassistent;

import java.util.ArrayList;

class Category {
	private String name;
	private ArrayList<Transaction> transactions;

	public Category(String name) {
		this.name = name;
		transactions = new ArrayList<>();
	}

	public void addTransaction(Transaction t) {
		if (!transactions.contains(t)) {
			transactions.add(t);
		}
	}

	public void removeTransaction(Transaction t) { //called from Transaction object
		transactions.remove(t);
	}

	public double calculateSum() {
		double sum = 0;
		for (Transaction t : transactions) {
			sum += t.getFundedAmount();
		}
		return sum;
	}

	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}

	public void setName(String newName) {
		name = newName;
	}

	@Override
	public String toString() {
		return name;
	}
}