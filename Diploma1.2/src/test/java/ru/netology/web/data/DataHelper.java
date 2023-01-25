package ru.netology.web.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DataHelper {
    private DataHelper() {
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class FieldSet {
        private String cardNumber;
        private String month;
        private String year;
        private String owner;
        private String ccvCvv;
    }

    public static FieldSet getValidFieldSet() {
        var random = new Random();
        var date = generateDate();
        return new FieldSet("4444444444444441", String.format("|%02d|", date.getMonthValue()), String.format("%d", date.getYear()).substring(2), "ANNA", String.format("|%03d|", random.nextInt(999)));
    }


    public static LocalDate generateDate() {
        long minDay = LocalDate.now().toEpochDay();
        long maxDay = LocalDate.now().plusYears(5).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public static LocalDate prevMonth() {
        return LocalDate.now().minusMonths(1);
    }

    public static LocalDate prevYear() {
        return LocalDate.now().minusYears(1);
    }

}