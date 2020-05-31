package ru.netology.diploma.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import ru.netology.diploma.data.DataHelper;
import ru.netology.diploma.data.MySqlUtils;
import ru.netology.diploma.page.DashboardPage;

import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;

class PayCreditMySQLTest {

    private static MySqlUtils mySqlUtils;
    private DataHelper dataHelper = new DataHelper();

    @BeforeAll
    static void setUp() throws SQLException {
        mySqlUtils = new MySqlUtils();
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void setBack() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void openPage() {
        open("http://localhost:8081");
    }

    @AfterEach
    void cleanUp() {
        mySqlUtils.cleanAll();
    }

    @Test
    void shouldPayPositive() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay(cardNumber, month, year, owner, code);
        dashboardPage.assertSuccess();
        Assertions.assertEquals("APPROVED", mySqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldPayDeclinedCard() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getDeclined();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay(cardNumber, month, year, owner, code);
        dashboardPage.assertError();
        Assertions.assertEquals("DECLINED", mySqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldPayOtherCard() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getNotOnTheList();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay(cardNumber, month, year, owner, code);
        dashboardPage.assertError();
        Assertions.assertNull(mySqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldPayExpiredCard() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInThePast().getMonth();
        val year = dataHelper.randomMonthAndYearInThePast().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay(cardNumber, month, year, owner, code);
        dashboardPage.assertErrorExpiredDate(1);
        Assertions.assertNull(mySqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldPayExpiredMonth() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.expiredOneMonth().getMonth();
        val year = dataHelper.expiredOneMonth().getYear();
        val owner = dataHelper.generateRandomCode();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay(cardNumber, month, year, owner, code);
        dashboardPage.assertErrorExpiredDate(1);
        Assertions.assertNull(mySqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldPayWrongFormat() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.invalidYearAndMonth().getMonth();
        val year = dataHelper.invalidYearAndMonth().getYear();
        val owner = dataHelper.generateNameRussian();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay(cardNumber, month, year, owner, code);
        dashboardPage.assertErrorInvalidDate(2);
        dashboardPage.assertErrorInvalidFormat(1);
        Assertions.assertNull(mySqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldPayTooFewSymbols() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getTooShort();
        val oneNumber = dataHelper.generateRandomSymbol().getNumber();
        val oneLetter = dataHelper.generateRandomSymbol().getLetter();
        dashboardPage.pay(cardNumber, oneNumber, oneNumber, oneLetter, oneNumber);
        dashboardPage.assertErrorInvalidFormat(5);
        Assertions.assertNull(mySqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldPayEmptyFields() {
        val dashboardPage = new DashboardPage();
        val emptyField = dataHelper.getEmptyField();
        dashboardPage.pay(emptyField, emptyField, emptyField, emptyField, emptyField);
        dashboardPage.assertErrorRequiredField(5);
        Assertions.assertNull(mySqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldObtainCreditPositive() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.credit(cardNumber, month, year, owner, code);
        dashboardPage.assertSuccess();
        Assertions.assertEquals("APPROVED", mySqlUtils.getLastCreditStatus());
    }

    @Test
    void shouldObtainCreditDeclinedCard() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getDeclined();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.credit(cardNumber, month, year, owner, code);
        dashboardPage.assertError();
        Assertions.assertEquals("DECLINED", mySqlUtils.getLastCreditStatus());
    }

    @Test
    void shouldObtainCreditOtherCard() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getNotOnTheList();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.credit(cardNumber, month, year, owner, code);
        dashboardPage.assertError();
        Assertions.assertNull(mySqlUtils.getLastCreditStatus());
    }

    @Test
    void shouldObtainCreditExpiredCard() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInThePast().getMonth();
        val year = dataHelper.randomMonthAndYearInThePast().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.credit(cardNumber, month, year, owner, code);
        dashboardPage.assertErrorExpiredDate(1);
        Assertions.assertNull(mySqlUtils.getLastCreditStatus());
    }

    @Test
    void shouldObtainCreditExpiredMonth() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.expiredOneMonth().getMonth();
        val year = dataHelper.expiredOneMonth().getYear();
        val owner = dataHelper.generateRandomCode();
        val code = dataHelper.generateRandomCode();
        dashboardPage.credit(cardNumber, month, year, owner, code);
        dashboardPage.assertErrorExpiredDate(1);
        Assertions.assertNull(mySqlUtils.getLastCreditStatus());
    }

    @Test
    void shouldObtainCreditWrongFormat() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.invalidYearAndMonth().getMonth();
        val year = dataHelper.invalidYearAndMonth().getYear();
        val owner = dataHelper.generateNameRussian();
        val code = dataHelper.generateRandomCode();
        dashboardPage.credit(cardNumber, month, year, owner, code);
        dashboardPage.assertErrorInvalidDate(2);
        dashboardPage.assertErrorInvalidFormat(1);
        Assertions.assertNull(mySqlUtils.getLastCreditStatus());
    }

    @Test
    void shouldObtainCreditTooFewSymbols() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getTooShort();
        val oneNumber = dataHelper.generateRandomSymbol().getNumber();
        val oneLetter = dataHelper.generateRandomSymbol().getLetter();
        dashboardPage.credit(cardNumber, oneNumber, oneNumber, oneLetter, oneNumber);
        dashboardPage.assertErrorInvalidFormat(5);
        Assertions.assertNull(mySqlUtils.getLastCreditStatus());
    }

    @Test
    void shouldObtainCreditEmptyFields() {
        val dashboardPage = new DashboardPage();
        val emptyField = dataHelper.getEmptyField();
        dashboardPage.credit(emptyField, emptyField, emptyField, emptyField, emptyField);
        dashboardPage.assertErrorRequiredField(5);
        Assertions.assertNull(mySqlUtils.getLastCreditStatus());
    }
}