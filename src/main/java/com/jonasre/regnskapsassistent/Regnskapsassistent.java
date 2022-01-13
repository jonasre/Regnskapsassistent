package com.jonasre.regnskapsassistent;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/*
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
*/
import java.io.File;

import com.jonasre.regnskapsassistent.model.Category;
import com.jonasre.regnskapsassistent.model.Transaction;
import com.jonasre.regnskapsassistent.util.FileReader;

public class Regnskapsassistent extends Application {
  public static ObservableList<Transaction> transactions;
  public static ObservableList<Category> categories;
  public static Scene scene;
  public static File workFile;

  public static void main(String[] args) {
    workFile = null;
    categories = FXCollections.observableArrayList();
    
    launch();
  }

  @Override
  public void start(Stage stage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("/views/mainApp.fxml"));
    scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle("Regnskapsassistent");
    stage.setMinWidth(1120);
    stage.setMinHeight(700);
    stage.show();

  }

  public static void loadTransactionList(File file) {
    reset();
    transactions = FileReader.read(file);
  }

  public static void loadWork(File file) {
    reset();
    FileReader.loadWork(file);
  }

  private static void reset() {
    workFile = null;
    categories.clear();
  }


}
