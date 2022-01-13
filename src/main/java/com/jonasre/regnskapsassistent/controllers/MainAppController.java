package com.jonasre.regnskapsassistent.controllers;

import com.jonasre.regnskapsassistent.Regnskapsassistent;
import com.jonasre.regnskapsassistent.model.Category;
import com.jonasre.regnskapsassistent.model.Transaction;
import com.jonasre.regnskapsassistent.util.FileManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainAppController {
    // MenuItems from top MenuBar
    public MenuItem menuSave;
    public MenuItem menuSaveAs;
    public MenuItem menuExport;

    public TableView<Transaction> table; //Main table
    public TableViewSelectionModel<Transaction> selection;

    // TableColumns
    public TableColumn<Transaction, LocalDate> tableDate;
    public TableColumn<Transaction, String> tableExplaination;
    public TableColumn<Transaction, String> tableAmount;

    // Input area
    public Label dateText;
    public Label explainationText;
    public Label amountText;
    public TextField fundedInput;
    public TextArea commentInput;
    public Button addCategoryButton;
    public ChoiceBox<Category> categoryDropdown;

    // Control panel
    public Button fundedButton;
    public Button prevButton;
    public Button nextButton;

    public Transaction selectedTransaction;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    void exit(ActionEvent event) {
        System.out.println("Exit");
        System.exit(0);
    }

    @FXML
    void initialize() {
        selection = table.getSelectionModel();
        tableDate.setCellValueFactory(new PropertyValueFactory<Transaction, LocalDate>("Date"));
        tableExplaination.setCellValueFactory(new PropertyValueFactory<Transaction, String>("Text"));
        tableAmount.setCellValueFactory(new PropertyValueFactory<Transaction, String>("OriginalAndFunded"));
        
        // Updates categories
        categoryDropdown.setItems(Regnskapsassistent.categories);

        // Updates which category belongs to loaded transaction
        // from https://stackoverflow.com/questions/14522680/javafx-choicebox-events
        categoryDropdown.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends Category> observable, Category oldValue,
                        Category newValue) -> {
                            if (selectedTransaction != null) {
                                selectedTransaction.setCategory(newValue);
                            }
                        });

        // Updates funded status on loaded transaction, colors fundedButton and refreshes table
        fundedInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedTransaction == null) {
                return;
            }
            if (fundedInput.getText().equals("") || fundedInput.getText().equals("0.0")) {
                selectedTransaction.setFundedAmount(0);
                selectedTransaction.setUnfunded();
            } else {
                if (Double.parseDouble(fundedInput.getText()) > selectedTransaction.getAmount()) {
                    selectedTransaction.setFundedAmount(selectedTransaction.getAmount());
                } else {
                    selectedTransaction.setFundedAmount(Double.parseDouble(fundedInput.getText()));
                }
                selectedTransaction.setFunded();
            }
            colorFundedButton();
            table.refresh();
        });

        // Updates comment as text is written
        commentInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (selectedTransaction != null) {
                selectedTransaction.setComment(commentInput.getText());
            }
        });

        // Changes row color based on transaction funded status
        table.setRowFactory(tv -> new TableRow<Transaction>() {
            @Override
            public void updateItem(Transaction transaction, boolean empty) {
                super.updateItem(transaction, empty);
                this.getStyleClass().removeAll("rowInbound", "rowFunded", "rowSemi", "rowUnfunded");

                if (transaction == null || empty) {
                    setStyle("");
                } else if (!transaction.isOutbound()) {
                    this.getStyleClass().add("rowInbound");
                } else if (!transaction.isFunded()) {
                    this.getStyleClass().add("rowUnfunded");
                } else if (transaction.getAmount() == transaction.getFundedAmount()) {
                    this.getStyleClass().add("rowFunded");
                } else {
                    this.getStyleClass().add("rowSemi");
                }
            }
        });
        // THIS FORMATTER MAKES IT IMPOSSIBLE TO UPDATE fundedInput ON LOADING A
        // TRANSACTION
        // Avoid text in input
        /*
         * DecimalFormat format = new DecimalFormat( "#.00" );
         * 
         * fundedInput.setTextFormatter( new TextFormatter<>(c -> { if (
         * c.getControlNewText().isEmpty()) return c;
         * 
         * ParsePosition parsePosition = new ParsePosition( 0 ); Object object =
         * format.parse( c.getControlNewText(), parsePosition );
         * 
         * if ( object == null || parsePosition.getIndex() <
         * c.getControlNewText().length()) { return null; } else { return c; } }));
         */   
    }

    // Loads a Transaction-object for editing
    @FXML
    void loadTransaction(MouseEvent event) {
        selectedTransaction = selection.getSelectedItem();
        if (selectedTransaction == null) {
            return;
        }
        // table.scrollTo(selectedTransaction);
        colorFundedButton();
        dateText.setText(selectedTransaction.getDate().toString());
        explainationText.setText(selectedTransaction.getText());
        amountText.setText(selectedTransaction.getAmount() + "");
        fundedInput.setText(selectedTransaction.getFundedAmount() + "");
        categoryDropdown.setValue(selectedTransaction.getCategory());
        commentInput.setText(selectedTransaction.getComment());

        // Enable/disable input fields and buttons depending on whether the 
        // transaction is outbound or not
        boolean inbound = !selectedTransaction.isOutbound();
        fundedInput.setDisable(inbound);
        fundedButton.setDisable(inbound);
        commentInput.setDisable(false);
        // Only enabled if transaction is outbound and categories are available
        categoryDropdown.setDisable(inbound || Regnskapsassistent.categories.isEmpty());
    }

    // Called when pressing fundedButton.
    // Sets the transaction as funded/unfunded, and colors the button
    @FXML
    void fundTransaction(ActionEvent event) {
        if (selectedTransaction == null || !selectedTransaction.isOutbound()) {
            return;
        }
        selectedTransaction.toggleFunded();
        colorFundedButton();

        if (!selectedTransaction.isFunded()) {
            selectedTransaction.setFundedAmount(0);
        } else if (selectedTransaction.getFundedAmount() == 0) {
            selectedTransaction.setFundedAmount(selectedTransaction.getAmount());
        }
        fundedInput.setText(selectedTransaction.getFundedAmount() + "");
        table.refresh();
    }

    // Called by multiple methods
    // Colors fundedButton based on the funded status of the loaded transaction
    @FXML
    void colorFundedButton() {
        // Gives fundedButton the appropriate color depending on the status of the transaction
        fundedButton.getStyleClass().removeAll("fundedButtonTrue", "fundedButtonSemi", "fundedButtonFalse");
        // In this way you're sure you have no styles applied to your object button
        // then you specify the class you would give to the button
        if (!selectedTransaction.isOutbound()) {
            return;
        } else if (!selectedTransaction.isFunded() || selectedTransaction.getFundedAmount() == 0) {
            fundedButton.getStyleClass().add("fundedButtonFalse");
        } else if (selectedTransaction.getFundedAmount() < selectedTransaction.getAmount()) {
            fundedButton.getStyleClass().add("fundedButtonSemi");
        } else {
            fundedButton.getStyleClass().add("fundedButtonTrue");
        }
    }

    // Selects the transaction underneath the one currently selected
    // Skips inbound transactions
    @FXML
    void nextTransaction() {
        if (selectedTransaction == null) {
            return;
        }
        Transaction last;
        Transaction current;
        do {
            last = selectedTransaction;
            selection.selectNext();
            current = selection.getSelectedItem();
        } while (!current.isOutbound() && current != last);
        loadTransaction(null);
    }

    // Selects the transaction above the one currently selected
    // Skips inbound transactions
    @FXML
    void previousTransaction() {
        if (selectedTransaction == null) {
            return;
        }
        Transaction last;
        Transaction current;
        do {
            last = selectedTransaction;
            selection.selectPrevious();
            current = selection.getSelectedItem();
        } while (!current.isOutbound() && current != last);
        loadTransaction(null);
    }

    // Opens managing of categories
    @FXML
    void openCategoriesWindow(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(MainAppController.class.getResource("/views/categories.fxml"));
        stage.setScene(new Scene(root));
        stage.setTitle("Behandle kategorier");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(
            ((Node)event.getSource()).getScene().getWindow() );
        stage.show();
        
        // Used to enable/disable categoriesdropdown
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                loadTransaction(null);
            }
        });
    }

    // Selects and opens a transactionlog .txt-file
    @FXML
    void openTransactionLog() {
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Open transactionlog");
        filechooser.getExtensionFilters().add(new ExtensionFilter("Tekstfil", "*.txt"));
        File userDirectory = new File(System.getProperty("user.dir"));
        if (!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        filechooser.setInitialDirectory(userDirectory);
        File file = filechooser.showOpenDialog(Regnskapsassistent.scene.getWindow());
        if (file == null) {
            System.out.println("Filvalg ble avbrutt");
            return;
        }

        Regnskapsassistent.loadTransactionList(file);
        table.setItems(Regnskapsassistent.transactions);
        selection.selectFirst();
        loadTransaction(null);
        Regnskapsassistent.workFile = null;

        // Enables buttons
        menuSave.setDisable(false);
        menuSaveAs.setDisable(false);
        menuExport.setDisable(false);
        addCategoryButton.setDisable(false);
        prevButton.setDisable(false);
        nextButton.setDisable(false);
    }

    // Exports work to file
    @FXML
    void export() {
        System.out.println("Export");
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Lagre som");
        filechooser.getExtensionFilters().add(new ExtensionFilter("Tekstfil", "*.txt"));
        File userDirectory = new File(System.getProperty("user.dir"));
        if (!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        filechooser.setInitialDirectory(userDirectory);
        File file = filechooser.showSaveDialog(Regnskapsassistent.scene.getWindow());
        if (file == null) {
            System.out.println("Lagring ble avbrutt");
            return;
        }

        FileManager.exportWork(file);
    }

    // Lets user select .ras-file, then loads it
    @FXML
    void openWork() {
        System.out.println("Open work");

        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Open work");
        filechooser.getExtensionFilters().add(new ExtensionFilter("Arbeidsfil", "*.ras"));

        File userDirectory = new File(System.getProperty("user.dir"));
        if (!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        filechooser.setInitialDirectory(userDirectory);
        File file = filechooser.showOpenDialog(Regnskapsassistent.scene.getWindow());
        if (file == null) {
            System.out.println("Filvalg ble avbrutt");
            return;
        }

        Regnskapsassistent.loadWork(file);
        table.setItems(Regnskapsassistent.transactions);
        categoryDropdown.setItems(Regnskapsassistent.categories);
        selection.selectFirst();
        loadTransaction(null);
        Regnskapsassistent.workFile = file;
        
        // Enables menuitems for save and export
        menuSave.setDisable(false);
        menuSaveAs.setDisable(false);
        menuExport.setDisable(false);
    }

    // Overwrites current savefile and updates it
    // If there is no current savefile, a filechoosere appears
    @FXML
    void saveWork() {
        System.out.println("Save work");
        if (Regnskapsassistent.workFile != null) {
            FileManager.saveWork(Regnskapsassistent.workFile);
        } else {
            saveWorkAs();
        }
    }

    // Lets user select filename and location to save work (.ras)
    @FXML
    void saveWorkAs() {
        System.out.println("Save work as");
        
        FileChooser filechooser = new FileChooser();
        filechooser.setTitle("Lagre som");
        filechooser.getExtensionFilters().add(new ExtensionFilter("Arbeidsfil", "*.ras"));

        File userDirectory = new File(System.getProperty("user.dir"));
        if (!userDirectory.canRead()) {
            userDirectory = new File("c:/");
        }
        filechooser.setInitialDirectory(userDirectory);
        File file = filechooser.showSaveDialog(Regnskapsassistent.scene.getWindow());
        if (file == null) {
            System.out.println("Lagring ble avbrutt");
            return;
        }

        FileManager.saveWork(file);
        Regnskapsassistent.workFile = file;
    }

    @FXML
    void aboutAlert() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Om");
        alert.setHeaderText("Regnskapsassistent v"+Regnskapsassistent.version);
        alert.setContentText("Laget av Jonas Reinholdt\n"
            +"https://github.com/jonasre/Regnskapsassistent");

        alert.showAndWait();
    }
}
