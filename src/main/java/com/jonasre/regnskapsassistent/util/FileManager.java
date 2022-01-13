package com.jonasre.regnskapsassistent.util;

import com.jonasre.regnskapsassistent.Regnskapsassistent;
import com.jonasre.regnskapsassistent.model.Category;
import com.jonasre.regnskapsassistent.model.Transaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class FileManager {
  // Opens transaction log
  public static ObservableList<Transaction> read(File file) {
    ObservableList<Transaction> transactionlist = FXCollections.observableArrayList();
    try {
      Scanner reader = new Scanner(file, "UTF-8");
      reader.nextLine(); // Ignores first line of the file

      // Reads file
      while (reader.hasNextLine()) {
        String line = reader.nextLine();
        line = line.replace("\"", "");
        String[] bits = line.split(";");
        LocalDate date = datify(bits[0]);
        boolean outbound = true;
        String amountSTR;
        if (bits.length == 5) {
          outbound = false;
          amountSTR = bits[4];
        } else {
          amountSTR = bits[3];
        }
        //amountSTR = amountSTR.replace(".", "");
        amountSTR = amountSTR.replace(",", ".");
        double amount = Double.parseDouble(amountSTR);
        transactionlist.add(new Transaction(date, bits[1], amount, outbound));
      }
      reader.close();

    } catch (FileNotFoundException e) {
      System.out.println("File not found, terminating");
      System.exit(1);
    }
    return transactionlist;
  }

  // Exports work to file
  public static void exportWork(File file) {
    try {
      PrintWriter writer = new PrintWriter(file, "UTF-8");
      double total = 0;

      for (Category cat : Regnskapsassistent.categories) {
        if (cat.getTransactions().size() == 0) {
          continue;
        }
        writer.println("--" + cat + "--");
        double catTotal = 0;
        Collections.sort(cat.getTransactions());
        for (Transaction t : cat.getTransactions()) {
          if (t.isFunded()) {
            catTotal += t.getFundedAmount();
            writer.println(t.toExportString());
          }
        }
        writer.println("\nSum for " + cat + ": " + catTotal + "\n\n\n");
        total += catTotal;
      }

      ArrayList<Transaction> uncatList = new ArrayList<>();
      for (Transaction t : Regnskapsassistent.transactions) {
        if (t.getCategory() == null && t.isFunded()) {
          uncatList.add(t);
        }
      }

      if (uncatList.size() > 0) {
        // Uncategorized
        writer.println("--Ukategorisert--");
        double catTotal = 0;
        Collections.sort(uncatList);
        for (Transaction t : uncatList) {
          catTotal += t.getFundedAmount();
          writer.println(t.toExportString());
        }
        writer.println("\nSum for ukategorisert: " + catTotal + "\n\n\n");
        total += catTotal;
      }
      writer.println("------------------------------------");
      writer.println("Total sluttsum for alle kategorier:\n" + total);
      writer.println("------------------------------------");
      writer.println("\n\nDenne tekstfilen ble generert av Regnskapsassistent");

      writer.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

   
  // Saves work to .ras-file
  public static void saveWork(File file) {
    /*Format:
    category1
    category2
    ..
    categoryN
    #
    date;text;amount;outbound;fundedAmount;category;comment\n
    */
    try {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "Cp1252"));

      for (Category c : Regnskapsassistent.categories) {
        writer.write(c+"\n");
      }
      writer.write("#\n");
      for (Transaction t : Regnskapsassistent.transactions) {
        writer.write(t.getDate()+";");
        writer.write(t.getText()+";");
        writer.write(t.getAmount()+";");
        writer.write(t.isOutbound()+";");
        writer.write(t.getFundedAmount()+";");
        if (t.getCategory() == null) {
          writer.write("/null/;");
        } else {
          writer.write(t.getCategory()+";");
        }
        writer.write(t.getComment()+"\n");
      }

      writer.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Opens workfile (.ras)
  public static void loadWork(File file) {
    try {
      Scanner reader = new Scanner(file);
      String line = "";

      // Categories
      Regnskapsassistent.categories = FXCollections.observableArrayList();
      line = reader.nextLine();
      while (!line.equals("#")) {
        Regnskapsassistent.categories.add(new Category(line));
        line = reader.nextLine();
      }
      
      // Transactions
      Regnskapsassistent.transactions = FXCollections.observableArrayList();
      while (reader.hasNextLine()) {
        Transaction t = createTransaction(reader.nextLine());
        Regnskapsassistent.transactions.add(t);
      }

      reader.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  // Parses string to LocalDate-object
  private static LocalDate datify(String x) {
    String[] bits = x.split("\\.");
    String text = bits[2] + "-" + bits[1] + "-" + bits[0];
    return LocalDate.parse(text);
  }
  
  // Turns transaction-line from .ras-file into Transaction-object
  private static Transaction createTransaction(String line) {
    // date;text;amount;outbound;fundedAmount;category;comment\n
    String[] bits = line.split(";");
    LocalDate date = LocalDate.parse(bits[0]);
    String text = bits[1];
    double amount = Double.parseDouble(bits[2]);
    boolean outbound = Boolean.parseBoolean(bits[3]);
    double fundedAmount = Double.parseDouble(bits[4]);
    Category category = null;
    if (!bits[5].equals("/null/")) {
      for (Category c : Regnskapsassistent.categories) {
        if (c.toString().equals(bits[5])) {
          category = c;
          break;
        }
      }
      if (category == null) {
        System.out.println("Transaksjonen skulle ha kategorien "+bits[5]+", men denne ble ikke funnet");
      }
    }
    
    Transaction transaction = new Transaction(date, text, amount, outbound);
    transaction.setFundedAmount(fundedAmount);
    if (fundedAmount > 0) {
      transaction.setFunded();
    }
    transaction.setCategory(category);
    if (bits.length == 7) {
      transaction.setComment(bits[6]);
    }

    return transaction;
  }
}
