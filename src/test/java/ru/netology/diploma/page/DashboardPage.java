package ru.netology.diploma.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {

    private SelenideElement heading = $(withText("Путешествие"));

    private SelenideElement buyButton = $(withText("Купить"));
    private SelenideElement creditButton = $(withText("Купить в кредит"));

    private SelenideElement success = $(withText("Успешно"));
    private SelenideElement error = $(withText("Ошибка"));

    public DashboardPage() {
        heading.shouldBe(visible);
    }

    public PaymentPage pay() {
        buyButton.click();
        return new PaymentPage();
    }

    public CreditPage credit() {
        creditButton.click();
        return new CreditPage();
    }

    public void assertSuccess() {
        success.waitUntil(visible, 15000);
    }

    public void assertError() {
        error.waitUntil(visible, 15000);
    }
}
