package com.example.financemanager.controller;

import com.example.financemanager.FinanceTrackerApplication;
import com.example.financemanager.model.Income;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class IncomeDialog extends Dialog<Income> {
    @FXML
    private TextField dateField;

    @FXML
    private TextField salaryField;

    @FXML
    private TextField helpField;

    @FXML
    private TextField autoBusinessField;

    @FXML
    private TextField passiveIncomeField;

    @FXML
    private TextField otherField;

    @FXML
    private ButtonType createButton;

    public IncomeDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(FinanceTrackerApplication.class.getResource("income-dialog.fxml"));
            loader.setController(this);

            DialogPane dialogPane = loader.load();

            setTitle("Ajouter des Revenues");
            setDialogPane(dialogPane);
            initResultConverter();

            // Disable button when all field are not filled
            computeIfButtonIsDisabled();

            // Ensure only numeric input are set in the fields
            forceDoubleFormat();

            forceDateFormat();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void forceDateFormat() {
        UnaryOperator<TextFormatter.Change> dateValidationFormatter = t -> {
            if (t.isAdded()) {
                if (t.getControlText().length() > 8) {
                    t.setText("");
                } else if (t.getControlText().matches(".*[0-9]{2}")) {
                    if (t.getText().matches("[^/]")) {
                        t.setText("");
                    }
                } else if (t.getText().matches("[^0-9]")) {
                    t.setText("");
                }
            }
            return t;
        };
        dateField.setTextFormatter(new TextFormatter<>(dateValidationFormatter));
    }

    private void forceDoubleFormat() {
        UnaryOperator<TextFormatter.Change> numberValidationFormatter = t -> {
            if (t.isReplaced())
                if(t.getText().matches("[^0-9]"))
                    t.setText(t.getControlText().substring(t.getRangeStart(), t.getRangeEnd()));


            if (t.isAdded()) {
                if (t.getControlText().contains(".")) {
                    if (t.getText().matches("[^0-9]")) {
                        t.setText("");
                    }
                } else if (t.getText().matches("[^0-9.]")) {
                    t.setText("");
                }
            }
            return t;
        };
        salaryField.setTextFormatter(new TextFormatter<>(numberValidationFormatter));
        helpField.setTextFormatter(new TextFormatter<>(numberValidationFormatter));
        autoBusinessField.setTextFormatter(new TextFormatter<>(numberValidationFormatter));
        passiveIncomeField.setTextFormatter(new TextFormatter<>(numberValidationFormatter));
        otherField.setTextFormatter(new TextFormatter<>(numberValidationFormatter));
    }

    private void computeIfButtonIsDisabled() {
        getDialogPane().lookupButton(createButton).disableProperty().bind(
                Bindings.createBooleanBinding(() -> dateField.getText().trim().isEmpty(), dateField.textProperty())
                        .or(Bindings.createBooleanBinding(() -> salaryField.getText().trim().isEmpty(), salaryField.textProperty())
                                .or(Bindings.createBooleanBinding(() -> helpField.getText().trim().isEmpty(), helpField.textProperty())
                                    .or(Bindings.createBooleanBinding(() -> autoBusinessField.getText().trim().isEmpty(), autoBusinessField.textProperty())
                                            .or(Bindings.createBooleanBinding(() -> passiveIncomeField.getText().trim().isEmpty(), passiveIncomeField.textProperty())
                                                    .or(Bindings.createBooleanBinding(() -> otherField.getText().trim().isEmpty(), otherField.textProperty())
                                                    )
                                        )))));
    }

    private void initResultConverter() {
        setResultConverter(buttonType -> {
            if (!Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                return null;
            }

            return new Income(
                    LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd/MM/yy")),
                    Float.parseFloat(salaryField.getText()),
                    Float.parseFloat(helpField.getText()),
                    Float.parseFloat(autoBusinessField.getText()),
                    Float.parseFloat(passiveIncomeField.getText()),
                    Float.parseFloat(otherField.getText())
            );
        });
    }

    @FXML
    private void initialize() {

    }
}

