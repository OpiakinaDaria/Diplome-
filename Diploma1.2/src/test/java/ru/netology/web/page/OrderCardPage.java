package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class OrderCardPage {
    private SelenideElement heading = $("h2");
    private ElementsCollection buttonList = $$(".button__text");
    private SelenideElement buttonPayment = buttonList.find(Condition.exactText("Купить")).closest("button");
    private SelenideElement buttonPaymentOnCredit = buttonList.find(Condition.exactText("Купить в кредит")).closest("button");

    public OrderCardPage() {
        heading.shouldHave(Condition.exactText("Путешествие дня")).shouldBe(visible);
    }

    public CardPaymentPage goToPaymentPage() {
        buttonPayment.click();
        return new CardPaymentPage();
    }

    public PaymentOnCreditPage goToPaymentOnCreditPage() {
        buttonPaymentOnCredit.click();
        return new PaymentOnCreditPage();
    }

    public void buttonStatusExtraStart() {
        buttonPaymentOnCredit.shouldHave(cssClass("button_view_extra"));
        buttonPayment.shouldHave(cssClass("button_view_extra"));
    }

    public void buttonStatusExtraPayment() {
        buttonPayment.click();
        buttonPayment.shouldHave(not(cssClass("button_view_extra")));
        buttonPaymentOnCredit.shouldHave(cssClass("button_view_extra"));
    }

    public void buttonStatusExtraPaymentOnCredit() {
        buttonPaymentOnCredit.click();
        buttonPaymentOnCredit.shouldHave(not(cssClass("button_view_extra")));
        buttonPayment.shouldHave(cssClass("button_view_extra"));
    }
}
