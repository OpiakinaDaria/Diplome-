package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.web.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FormPage {
    private ElementsCollection fieldSet = $$(".input__top");
    private SelenideElement button = $$(".button__text").find(Condition.exactText("Продолжить"));
    private SelenideElement notClickableButton = $$(".button__text").find(Condition.exactText("Отправляем запрос в Банк...")).closest("button");
    private SelenideElement notificationError = $(".notification_status_error");
    private SelenideElement notificationOk = $(".notification_status_ok");
    private SelenideElement activeNotification = $(".notification_visible");

    public void fillingOutFormFields(DataHelper.FieldSet info){
        fieldSet.find(Condition.exactText("Номер карты")).closest(".input__inner").$(".input__control").setValue(info.getCardNumber());
        fieldSet.find(Condition.exactText("Месяц")).closest(".input__inner").$(".input__control").setValue(info.getMonth());
        fieldSet.find(Condition.exactText("Год")).closest(".input__inner").$(".input__control").setValue(info.getYear());
        fieldSet.find(Condition.exactText("Владелец")).closest(".input__inner").$(".input__control").setValue(info.getOwner());
        fieldSet.find(Condition.exactText("CVC/CVV")).closest(".input__inner").$(".input__control").setValue(info.getCcvCvv());
    }

    public void formNotSend(){
        button.click();
        notClickableButton.shouldBe(not(visible));
    }

    public void goToNotificationPage(DataHelper.FieldSet info){
        fillingOutFormFields(info);
        button.click();
        notClickableButton.shouldBe(visible, disabled);
    }
    public void activeNotification(){
        activeNotification.shouldBe(Condition.visible, Duration.ofSeconds(15));
    }
    public void notificationError(){
        notificationError.shouldHave(Condition.text("Ошибка"), Duration.ofSeconds(15)).shouldBe(Condition.visible);
    }

    public void notificationOk(){
        notificationOk.shouldHave(Condition.text("Успешно"), Duration.ofSeconds(15)).shouldBe(Condition.visible);
    }

    public void errorMessage(String field, String textErrorMessage){
        fieldSet.find(Condition.exactText(field)).closest(".input__inner").$(".input__sub").shouldHave(Condition.exactText(textErrorMessage)).shouldBe(visible);
    }

    public void setFieldValue(String field, String values){
        fieldSet.find(Condition.exactText(field)).closest(".input__inner").$(".input__control").setValue(values);
    }
    public String getFieldValue(String field){
        return fieldSet.find(Condition.exactText(field)).closest(".input__inner").$(".input__control").val();
    }

}