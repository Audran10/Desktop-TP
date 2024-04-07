package com.example.financemanager.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Income {
    private final LocalDate date;
    private final float total;
    private final float salary;
    private final float help;
    private final float auto_business;
    private final float passive_income;
    private final float other;

    private final static String PRICE_FORMAT = "%.2f €";
    private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM yyyy");

    public Income(LocalDate date, float salary, float help, float auto_business, float passive_income, float other) {
        this.date = date;
        this.total = salary + help + auto_business + passive_income + other;
        this.help = help;
        this.salary = salary;
        this.auto_business = auto_business;
        this.passive_income = passive_income;
        this.other = other;
    }

    public StringProperty dateProperty() { return new SimpleStringProperty(date.format(DATE_FORMAT)); }

    public LocalDate getDate() {
        return date;
    }

    public float getTotal() {
        return total;
    }

    public float getSalary() {
        return salary;
    }

    public float getHelp() { return help; }

    public float getAuto_business() {
        return auto_business;
    }

    public float getPassive_income() {
        return passive_income;
    }

    public float getOther() {
        return other;
    }

    @Override
    public String toString() {
        return "Income{" +
                "date=" + date +
                ", total=" + total +
                ", salary=" + salary +
                ", help=" + help +
                ", auto business=" + auto_business +
                ", passive income=" + passive_income +
                ", other=" + other +
                '}';
    }

    public int compareTo(Income income) {
        return -this.date.compareTo(income.date);
    }
}
