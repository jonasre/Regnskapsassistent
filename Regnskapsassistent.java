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

public class Regnskapsassistent extends Application {
  public static ObservableList<Transaction> transactions;
  public static ObservableList<Category> categories;
  public static Scene scene;
  public static File workFile;

  public static void main(String[] args) {
    workFile = null;

    categories = FXCollections.observableArrayList();
    categories.addAll(new Category("Mat og drikke"), new Category("Trening")); //temp

    launch();
  }

  @Override
  public void start(Stage stage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("mainApp.fxml"));
    scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle("Regnskapsassistent");
    stage.show();

  }

  public static void loadTransactionList(File file) {
    transactions = FileReader.read(file);
  }


}
