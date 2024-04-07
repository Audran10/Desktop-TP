package com.example.financemanager.controller;

import com.example.financemanager.db.ExpenseDAO;
import com.example.financemanager.db.IncomeDAO;
import com.example.financemanager.model.Expense;
import com.example.financemanager.model.Income;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.ChoiceBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class DashboardController {

    @FXML
    private PieChart pieChart;

    @FXML
    private LineChart<String, Float> lineChart;

    @FXML
    private BarChart<String, Float> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private CategoryAxis xAxisBar;

    @FXML
    private NumberAxis yAxisBar;

    @FXML
    private ChoiceBox<String> periodChoiceBox;

    private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM yy");
    private final static DateTimeFormatter FULL_DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy");

    public void initialize() {
        LocalDate currentDate = LocalDate.now();

        for (int i = 0; i < 12; i++) {
            periodChoiceBox.getItems().add(currentDate.minusMonths(i).format(FULL_DATE_FORMAT));
        }

        // Configurer l'axe des catégories avec les étiquettes des mois pour les 12 derniers mois
        xAxisBar.setCategories(FXCollections.observableArrayList(
                currentDate.minusMonths(0).format(DATE_FORMAT),
                currentDate.minusMonths(1).format(DATE_FORMAT),
                currentDate.minusMonths(2).format(DATE_FORMAT),
                currentDate.minusMonths(3).format(DATE_FORMAT),
                currentDate.minusMonths(4).format(DATE_FORMAT),
                currentDate.minusMonths(5).format(DATE_FORMAT),
                currentDate.minusMonths(6).format(DATE_FORMAT),
                currentDate.minusMonths(7).format(DATE_FORMAT),
                currentDate.minusMonths(8).format(DATE_FORMAT),
                currentDate.minusMonths(9).format(DATE_FORMAT),
                currentDate.minusMonths(10).format(DATE_FORMAT),
                currentDate.minusMonths(11).format(DATE_FORMAT)
        ));

        periodChoiceBox.getSelectionModel().selectFirst();
    }


    private List<Expense> loadExpenses(LocalDate currentMonth) {

        List<Expense> lastExpenses = ExpenseDAO.findLastExpensesEndingAtCurrentMonth(12, currentMonth);

        if (lastExpenses.isEmpty()) {
            return null;
        }

        pieChart.getData().clear();
        lineChart.getData().clear();

        pieChart.getData().addAll(
                new PieChart.Data("Logement", lastExpenses.getFirst().getHousing()),
                new PieChart.Data("Nourriture", lastExpenses.getFirst().getFood()),
                new PieChart.Data("Sortie", lastExpenses.getFirst().getGoingOut()),
                new PieChart.Data("Transport", lastExpenses.getFirst().getTransportation()),
                new PieChart.Data("Voyage", lastExpenses.getFirst().getTravel()),
                new PieChart.Data("Impôts", lastExpenses.getFirst().getTax()),
                new PieChart.Data("Autres", lastExpenses.getFirst().getOther())
        );

        XYChart.Series<String, Float> seriesHousing = new XYChart.Series<>();
        seriesHousing.setName("Logement");
        XYChart.Series<String, Float> seriesFood = new XYChart.Series<>();
        seriesFood.setName("Nourriture");
        XYChart.Series<String, Float> seriesGoingOut = new XYChart.Series<>();
        seriesGoingOut.setName("Sortie");
        XYChart.Series<String, Float> seriesTransportation = new XYChart.Series<>();
        seriesTransportation.setName("Transport");
        XYChart.Series<String, Float> seriesTravel = new XYChart.Series<>();
        seriesTravel.setName("Voyage");
        XYChart.Series<String, Float> seriesTax = new XYChart.Series<>();
        seriesTax.setName("Impôts");
        XYChart.Series<String, Float> seriesOther = new XYChart.Series<>();
        seriesOther.setName("Autres");
        XYChart.Series<String, Float> seriesTotal = new XYChart.Series<>();
        seriesTotal.setName("Mes dépenses");

        lastExpenses.stream().sorted(Comparator.comparing(Expense::getDate)).forEach(expense -> {
            seriesHousing.getData().add(new XYChart.Data<>(expense.getDate().format(DATE_FORMAT), expense.getHousing()));
            seriesFood.getData().add(new XYChart.Data<>(expense.getDate().format(DATE_FORMAT), expense.getFood()));
            seriesGoingOut.getData().add(new XYChart.Data<>(expense.getDate().format(DATE_FORMAT), expense.getGoingOut()));
            seriesTransportation.getData().add(new XYChart.Data<>(expense.getDate().format(DATE_FORMAT), expense.getTransportation()));
            seriesTravel.getData().add(new XYChart.Data<>(expense.getDate().format(DATE_FORMAT), expense.getTravel()));
            seriesTax.getData().add(new XYChart.Data<>(expense.getDate().format(DATE_FORMAT), expense.getTax()));
            seriesOther.getData().add(new XYChart.Data<>(expense.getDate().format(DATE_FORMAT), expense.getOther()));
            seriesTotal.getData().add(new XYChart.Data<>(expense.getDate().format(DATE_FORMAT), expense.getTotal()));
        });

        lineChart.getData().addAll(
                seriesHousing,
                seriesFood,
                seriesGoingOut,
                seriesTransportation,
                seriesTravel,
                seriesTax,
                seriesOther
        );

        barChart.getData().add(seriesTotal);
        return lastExpenses;
    }

    private List<Income> loadIncomes(LocalDate currentMonth) {
        List<Income> lastIncomes = IncomeDAO.findLastIncomesEndingAtCurrentMonth(12, currentMonth);

        if (lastIncomes.isEmpty()) {
            return null;
        }

        barChart.getData().clear();

        XYChart.Series<String, Float> seriesIncomes = new XYChart.Series<>();
        seriesIncomes.setName("Mes revenus");

        // Ajoutez des données à la série de données
        lastIncomes.stream()
                .sorted(Comparator.comparing(Income::getDate))
                .forEach(income -> seriesIncomes.getData().add(new XYChart.Data<>(income.getDate().format(DATE_FORMAT), income.getTotal())));

        // Ajoutez la série de données au BarChart
        barChart.getData().add(seriesIncomes);
        return lastIncomes;
    }


    public void changePeriod(ActionEvent actionEvent) {
        var periodSelected = periodChoiceBox.getSelectionModel().getSelectedItem();
        LocalDate dateSelected = LocalDate.parse("30 " + periodSelected, DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        loadIncomes(dateSelected);
        loadExpenses(dateSelected);
    }
}
