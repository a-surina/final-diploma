package ru.netology.diploma.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

public class DataHelper {
    private static final String[] myMonthList = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    private Faker faker;
    private LocalDate today = LocalDate.now();
    private DateTimeFormatter formatterYears = DateTimeFormatter.ofPattern("yy");
    private DateTimeFormatter formatterMonth = DateTimeFormatter.ofPattern("MM");

    public DataHelper() {
    }

    public CardNumber getCardNumber() {
        return new CardNumber("4444444444444441", "4444444444444442", "8888888888888888", "444444444444444");
    }

    public Date randomMonthAndYearInFuture() {
        int n = (int) Math.floor(Math.random() * myMonthList.length);
        LocalDate newDate = today.plusYears(2);
        return new Date(formatterYears.format(newDate), myMonthList[n]);
    }

    public Date randomMonthAndYearInThePast() {
        int n = (int) Math.floor(Math.random() * myMonthList.length);
        LocalDate newDate = today.minusYears(2);
        return new Date(formatterYears.format(newDate), myMonthList[n]);
    }

    public Date invalidYearAndMonth() {
        Random random = new Random();
        return new Date(Integer.toString(35 + random.nextInt(74)), Integer.toString(13 + random.nextInt(86)));
//   Рандомное число от 35 до 99 для года и от 13 до 99 для месяца.
//   И то, и дугое будет восприниматься как некорректное: не бывает 13-го месяца, а 35 и 99 года слишком далеко либо в прошлом, либо в будущем.
    }

    public Date expiredOneMonth() {
        LocalDate newDate = today.minusMonths(1);
        return new Date(formatterYears.format(newDate), formatterMonth.format(newDate));
    }

    public String generateName() {
        faker = new Faker(new Locale("en", "RU"));
        return faker.name().lastName() + " " + faker.name().firstName();
    }

    public String generateNameRussian() {
        faker = new Faker(new Locale("ru", "Russia"));
        return faker.name().lastName() + " " + faker.name().firstName();
    }

    public String generateRandomCode() {
        Random random = new Random();
        return Integer.toString(random.nextInt(900) + 100);
    }

    public String getEmptyField() {
        return "";
    }

    public RandomSymbol generateRandomSymbol() {
        Random random = new Random();
        char c = (char) (random.nextInt(26) + 'a');
        return new RandomSymbol(Integer.toString(random.nextInt(10)), Character.toString(c));
    }

    @Value
    public static class CardNumber {
        private String approved;
        private String declined;
        private String notOnTheList;
        private String tooShort;
    }

    @Value
    public static class Date {
        private String year;
        private String month;
    }

    @Value
    public static class RandomSymbol {
        private String number;
        private String letter;
    }
}
