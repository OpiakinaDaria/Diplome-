package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;

public abstract class AbstractPaymentPage {
    private ElementsCollection headings = $$("h3");
    public AbstractPaymentPage(String title){headings.find(Condition.exactText(title)).shouldBe(visible);}
    public FormPage goToFormPage(){
        return new FormPage();
    }
}