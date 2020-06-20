package ru.netology.diploma.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import ru.netology.diploma.data.DataHelper;
import ru.netology.diploma.data.SqlUtils;
import ru.netology.diploma.page.DashboardPage;
import ru.netology.diploma.page.CreditPaymentDataForm;
import java.lang.String;

import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;

class PayCreditMySQLTest {

    private static SqlUtils sqlUtils;
    private DataHelper dataHelper = new DataHelper();
    private final static String APPROVED = "APPROVED";
    private final static String DECLINED = "DECLINED";

    @BeforeAll
    static void setUp() throws SQLException {
        String urlMySQL = "jdbc:mysql://localhost:3306/app";
        sqlUtils = new SqlUtils(urlMySQL);
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
        sqlUtils.cleanAll();
    }

    @Test
    void shouldPayPositive() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new CreditPaymentDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.assertSuccess();
        Assertions.assertEquals(APPROVED, sqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldPayDeclinedCard() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getDeclined();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new CreditPaymentDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.assertError();
        Assertions.assertEquals(DECLINED, sqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldPayOtherCard() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getNotOnTheList();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new CreditPaymentDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.assertError();
        Assertions.assertNull(sqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldPayExpiredCard() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInThePast().getMonth();
        val year = dataHelper.randomMonthAndYearInThePast().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new CreditPaymentDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorExpiredDate(1);
        Assertions.assertNull(sqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldPayExpiredMonth() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.expiredOneMonth().getMonth();
        val year = dataHelper.expiredOneMonth().getYear();
        val owner = dataHelper.generateRandomCode();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new CreditPaymentDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorExpiredDate(1);
        Assertions.assertNull(sqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldPayWrongFormat() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.invalidYearAndMonth().getMonth();
        val year = dataHelper.invalidYearAndMonth().getYear();
        val owner = dataHelper.generateNameRussian();
        val code = dataHelper.generateRandomCode();
        dashboardPage.pay();
        val form = new CreditPaymentDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorInvalidDate(2);
        form.assertErrorInvalidFormat(1);
        Assertions.assertNull(sqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldPayTooFewSymbols() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getTooShort();
        val oneNumber = dataHelper.generateRandomSymbol().getNumber();
        val oneLetter = dataHelper.generateRandomSymbol().getLetter();
        dashboardPage.pay();
        val form = new CreditPaymentDataForm();
        form.fillIn(cardNumber, oneNumber, oneNumber, oneLetter, oneNumber);
        form.assertErrorInvalidFormat(5);
        Assertions.assertNull(sqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldPayEmptyFields() {
        val dashboardPage = new DashboardPage();
        val emptyField = dataHelper.getEmptyField();
        dashboardPage.pay();
        val form = new CreditPaymentDataForm();
        form.fillIn(emptyField, emptyField, emptyField, emptyField, emptyField);
        form.assertErrorRequiredField(5);
        Assertions.assertNull(sqlUtils.getLastPaymentStatus());
    }

    @Test
    void shouldObtainCreditPositive() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.credit();
        val form = new CreditPaymentDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.assertSuccess();
        Assertions.assertEquals(APPROVED, sqlUtils.getLastCreditStatus());
    }

    @Test
    void shouldObtainCreditDeclinedCard() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getDeclined();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.credit();
        val form = new CreditPaymentDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.assertError();
        Assertions.assertEquals(DECLINED, sqlUtils.getLastCreditStatus());
    }

    @Test
    void shouldObtainCreditOtherCard() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getNotOnTheList();
        val month = dataHelper.randomMonthAndYearInFuture().getMonth();
        val year = dataHelper.randomMonthAndYearInFuture().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.credit();
        val form = new CreditPaymentDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        dashboardPage.assertError();
        Assertions.assertNull(sqlUtils.getLastCreditStatus());
    }

    @Test
    void shouldObtainCreditExpiredCard() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.randomMonthAndYearInThePast().getMonth();
        val year = dataHelper.randomMonthAndYearInThePast().getYear();
        val owner = dataHelper.generateName();
        val code = dataHelper.generateRandomCode();
        dashboardPage.credit();
        val form = new CreditPaymentDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorExpiredDate(1);
        Assertions.assertNull(sqlUtils.getLastCreditStatus());
    }

    @Test
    void shouldObtainCreditExpiredMonth() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.expiredOneMonth().getMonth();
        val year = dataHelper.expiredOneMonth().getYear();
        val owner = dataHelper.generateRandomCode();
        val code = dataHelper.generateRandomCode();
        dashboardPage.credit();
        val form = new CreditPaymentDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorExpiredDate(1);
        Assertions.assertNull(sqlUtils.getLastCreditStatus());
    }

    @Test
    void shouldObtainCreditWrongFormat() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getApproved();
        val month = dataHelper.invalidYearAndMonth().getMonth();
        val year = dataHelper.invalidYearAndMonth().getYear();
        val owner = dataHelper.generateNameRussian();
        val code = dataHelper.generateRandomCode();
        dashboardPage.credit();
        val form = new CreditPaymentDataForm();
        form.fillIn(cardNumber, month, year, owner, code);
        form.assertErrorInvalidDate(2);
        form.assertErrorInvalidFormat(1);
        Assertions.assertNull(sqlUtils.getLastCreditStatus());
    }

    @Test
    void shouldObtainCreditTooFewSymbols() {
        val dashboardPage = new DashboardPage();
        val cardNumber = dataHelper.getCardNumber().getTooShort();
        val oneNumber = dataHelper.generateRandomSymbol().getNumber();
        val oneLetter = dataHelper.generateRandomSymbol().getLetter();
        dashboardPage.credit();
        val form = new CreditPaymentDataForm();
        form.fillIn(cardNumber, oneNumber, oneNumber, oneLetter, oneNumber);
        form.assertErrorInvalidFormat(5);
        Assertions.assertNull(sqlUtils.getLastCreditStatus());
    }

    @Test
    void shouldObtainCreditEmptyFields() {
        val dashboardPage = new DashboardPage();
        val emptyField = dataHelper.getEmptyField();
        dashboardPage.credit();
        val form = new CreditPaymentDataForm();
        form.fillIn(emptyField, emptyField, emptyField, emptyField, emptyField);
        form.assertErrorRequiredField(5);
        Assertions.assertNull(sqlUtils.getLastCreditStatus());
    }
}
