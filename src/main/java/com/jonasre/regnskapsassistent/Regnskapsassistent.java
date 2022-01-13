package com.jonasre.regnskapsassistent;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.jonasre.regnskapsassistent.model.Category;
import com.jonasre.regnskapsassistent.model.Transaction;
import com.jonasre.regnskapsassistent.util.FileManager;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class Regnskapsassistent extends Application {
  public static String version;
  public static ObservableList<Transaction> transactions;
  public static ObservableList<Category> categories;
  public static Scene scene;
  public static File workFile;

  public static void main(String[] args) {
    version = getVersion();
    System.out.println(version);
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
    transactions = FileManager.read(file);
  }

  public static void loadWork(File file) {
    reset();
    FileManager.loadWork(file);
  }

  private static void reset() {
    workFile = null;
    categories.clear();
  }

  private static String getVersion() {
    MavenXpp3Reader reader = new MavenXpp3Reader();
    try {
      Model model = reader.read(new FileReader("pom.xml"));
      return model.getVersion();
    } catch (IOException | XmlPullParserException e) {
      e.printStackTrace();
      System.out.println("Error getting version");
      System.exit(1);
    }
    return null;
  }

}
