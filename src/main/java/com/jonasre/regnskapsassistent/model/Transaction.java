package com.jonasre.regnskapsassistent.model;

import java.time.LocalDate;
import java.text.DecimalFormat;

public class Transaction implements Comparable<Transaction> {
  private LocalDate date;
  private String text;
  private double amount;
  private double fundedAmount = 0;
  private Category category = null;
  private boolean outbound;
  private boolean funded = false;
  private String comment = "";

  private static DecimalFormat formatter = new DecimalFormat("#.0");


  public Transaction(LocalDate date, String text, double amount, boolean outbound) {
    this.date = date;
    this.text = text;
    this.amount = amount;
    this.outbound = outbound;
  }

  public void setInbound() {
    outbound = false;
  }

  public double getFundedAmount() {
    return fundedAmount;
  }

  public double getAmount() {
    return amount;
  }

  public String getText() {
    return text;
  }

  public LocalDate getDate() {
    return date;
  }

  // Sets category, removes transaction from other category if already registered there
  public void setCategory(Category newCategory) {
    if (category != null) {
      category.removeTransaction(this);
    }
    if (newCategory != null) {
      category = newCategory;
      newCategory.addTransaction(this);
    }
  }

  // Only sets category
  public void setAbsoluteCategory(Category newCategory) {
    category = newCategory;
  }

  public Category getCategory() {
    return category;
  }

  public void setFundedAmount(double x) {
    fundedAmount = x;
  }

  public void toggleFunded() {
    if (funded) {
      funded = false;
    } else {
      funded = true;
    }
  }

  public void setFunded() {
    funded = true;
  }

  public void setUnfunded() {
    funded = false;
  }

  public String getOriginalAndFunded() {
    return amount + " (" + fundedAmount + ")";
  }

  public void setComment(String s) {
    comment = s;
  }

  public String getComment() {
    return comment;
  }

  public boolean isOutbound() {
    return outbound;
  }

  public boolean isFunded() {
    return funded;
  }

  public String toExportString() {
    double percent = (fundedAmount/amount)*100;
    String money = fundedAmount+" kr ("+formatter.format(percent)+"% av "+amount+").";
    String note = "";
    if (!comment.equals("")) {
      note = "\n\tKommentar: "+comment;
    }
    String oe = new String(Character.toChars(248)); //ø
    return date+": "+text+".\n\tForespurt bel"+oe+"p: "+money+note+"\n";
  }

  @Override
  public String toString() {
    return date+", "+text+", "+amount+" kr, outbound: "+outbound+".";
  }

  @Override
  public int compareTo(Transaction other) {
    return this.date.compareTo(other.getDate());
  }
}
