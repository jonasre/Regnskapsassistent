package com.jonasre.regnskapsassistent.controllers;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jonasre.regnskapsassistent.Regnskapsassistent;
import com.jonasre.regnskapsassistent.model.Category;
import com.jonasre.regnskapsassistent.model.Transaction;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class CategoriesController {

	public ListView<Category> listView;
	public TextField input;
	public Button buttonRemove;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    void initialize() {
		listView.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>() {
			@Override
			public ListCell<Category> call(ListView<Category> lv) {
				TextFieldListCell<Category> cell = new TextFieldListCell<Category>(){
					@Override
					public void updateItem(Category item, boolean empty) {
						super.updateItem(item, empty);
						if (empty || item == null) {
							setStyle("");
						}
					}

					@Override
					public void commitEdit(Category item) {
						if (item != null) {
							super.commitEdit(item);
						} else {
							//if item is null, the name used was not accepted
							cancelEdit();
						}
					}
				};


				cell.setConverter(new StringConverter<Category>() {
					@Override
					public String toString(Category object) {
						return object.toString();
					}
	
					//Makes sure that the name is ok for use
					@Override
					public Category fromString(String string) {
						String s = checkInput(string);
						if (s == null) {
							return null;
						}
						Category c = cell.getItem();
						c.setName(s);
						return c;
					}
				});
				return cell;
			}
		});

		input.setOnKeyPressed(event -> {
			if(event.getCode() == KeyCode.ENTER){
			  addCategory(null);
			}
		 });
		
		loadCategories();
		
	}

	// Adds category to list unless string is empty or only contains whitespace
	@FXML
	void addCategory(ActionEvent event) {
		String s = checkInput(input.getText());
		if (s != null) {
			listView.getItems().add(new Category(s));
			input.setText("");
		}
	}

	// Removes category from list
	@FXML
	void removeCategory(ActionEvent event) {
		Category category = listView.getSelectionModel().getSelectedItem();
		int associatedTransactions = category.getTransactions().size();
		if (associatedTransactions == 0) {
			listView.getItems().remove(category);
			return;
		}

		// awkward workaround to insert unicode character "å"
		String prompt = associatedTransactions + " transaksjoner har denne kategorien. Er du sikker p"+new String(Character.toChars(229))+" at du vil fjerne den?";
		Alert alert = new Alert(AlertType.CONFIRMATION, prompt);
		alert.setHeaderText("Fjerne kategori '"+category+"'?");
		Optional<ButtonType> result = alert.showAndWait();
		
		if (result.isPresent() && result.get() == ButtonType.OK) {
			// Removes category from affected transactions
			for (Transaction t : category.getTransactions()) {
				t.setAbsoluteCategory(null);
			}
			listView.getItems().remove(category);
		}
	}

	// Enables button after selecting a category
	@FXML
	void updateButton(MouseEvent event) {
		if (listView.getSelectionModel().getSelectedItem() != null) {
			buttonRemove.setDisable(false);
		}
	}

	private void loadCategories() {
		listView.setItems(Regnskapsassistent.categories);
	}

	private String checkInput(String input) {
		//If name string is NOT only whitespace
		if (!input.replaceAll("\\s+","").equals("")) {
			return input.trim(); //Remove whitespace, before and trailing
		}
		return null;
	}
}
