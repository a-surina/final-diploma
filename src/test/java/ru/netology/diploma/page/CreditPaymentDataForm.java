package ru.netology.diploma.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import java.lang.String;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;

public class CreditPaymentDataForm {

    private SelenideElement form = $(By.tagName("form"));

    private SelenideElement cardNumberField = $("[placeholder='0000 0000 0000 0000']");
    private SelenideElement monthField = $("[placeholder='08']");
    private SelenideElement yearField = $("[placeholder='22']");
    private SelenideElement ownerField = $x("//*[@id='root']/div/form/fieldset/div[3]/span/span[1]/span/span/span[2]/input");
    private SelenideElement codeField = $("[placeholder='999']");

    private SelenideElement confirmButton = $(withText("Продолжить"));

    private ElementsCollection formErrorInvalidFormat = $$(withText("Неверный формат"));
    private ElementsCollection formErrorRequiredField = $$(withText("Поле обязательно для заполнения"));
    private ElementsCollection formErrorInvalidDate = $$(withText("Неверно указан срок действия карты"));
    private ElementsCollection formErrorExpiredDate = $$(withText("Истёк срок действия карты"));

    public CreditPaymentDataForm() {
        form.shouldBe(visible);
    }

    public void fillIn(String number, String month, String year, String owner, String code) {
        cardNumberField.setValue(number);
        monthField.setValue(month);
        yearField.setValue(year);
        ownerField.setValue(owner);
        codeField.setValue(code);
        confirmButton.click();
    }

    public void assertErrorExpiredDate(int quantity) {
        formErrorExpiredDate.shouldHaveSize(quantity);
    }

    public void assertErrorInvalidDate(int quantity) {
        formErrorInvalidDate.shouldHaveSize(quantity);
    }

    public void assertErrorInvalidFormat(int quantity) {
        formErrorInvalidFormat.shouldHaveSize(quantity);
    }

    public void assertErrorRequiredField(int quantity) {
        formErrorRequiredField.shouldHaveSize(quantity);
    }
}
