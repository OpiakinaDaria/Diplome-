package ru.netology.web.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.OrderCardPage;

import static com.codeborne.selenide.Selenide.open;

@Epic("Работа с базой данных")
public class DBTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setupTest() {
        open(System.getProperty("application.address"));
    }

    //---БАЗА ДАННЫХ---
    @Feature("База данных")
    @Story("Успешные операции")
    @Test
    @DisplayName("Проверяем, что приложение сохранет в своей БЗ успешно совершенный платеж по карте")
    void shouldStoreTheSuccessfullyTheCompletedCardPaymentInTheDatabase() {
        var rep = new PaymentEntityRepository();
        int count = rep.getStatusCount("APPROVED");

        var orderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        var cardPaymentPage = orderCardPage.goToPaymentPage();
        var formPage = cardPaymentPage.goToFormPage();
        formPage.goToNotificationPage(formFieldsInfo);
        formPage.activeNotification();

        int actualCount = rep.getStatusCount("APPROVED");

        Assertions.assertEquals(count + 1, actualCount);
    }

    @Feature("База данных")
    @Story("Успешные операции")
    @Test
    @DisplayName("Проверяем, что приложение сохранет в своей БЗ успешно совершенную покупку в кредит")
    void shouldStoreTheSuccessfullyTheCompletedPaymentOnTheCreditInTheDatabase() {
        var rep = new CreditRequestEntityRepository();
        int count = rep.getStatusCount("APPROVED");

        var orderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        var cardPaymentOnCreditPage = orderCardPage.goToPaymentOnCreditPage();
        var formPage = cardPaymentOnCreditPage.goToFormPage();
        formPage.goToNotificationPage(formFieldsInfo);
        formPage.activeNotification();

        int actualCount = rep.getStatusCount("APPROVED");

        Assertions.assertEquals(count + 1, actualCount);
    }

    @Feature("База данных")
    @Story("Отказ в операции")
    @Test
    @DisplayName("Проверяем, что приложение сохранет в своей БЗ отказ в совершении платежа по карте")
    void shouldStoreTheDenialOfCardPaymentInTheDatabase() {
        var rep = new PaymentEntityRepository();
        int count = rep.getStatusCount("DECLINED");

        var orderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setCardNumber("4444444444444442");
        var cardPaymentPage = orderCardPage.goToPaymentPage();
        var formPage = cardPaymentPage.goToFormPage();
        formPage.goToNotificationPage(formFieldsInfo);
        formPage.activeNotification();

        int actualCount = rep.getStatusCount("DECLINED");

        Assertions.assertEquals(count + 1, actualCount);

    }

    @Feature("База данных")
    @Story("Отказ в операции")
    @Test
    @DisplayName("Проверяем, что приложение сохранет в своей БЗ отказ в совершении покупке в кредит")
    void shouldStoreTheDenialOfPaymentOnTheCreditInTheDatabase() {
        var rep = new CreditRequestEntityRepository();
        int count = rep.getStatusCount("DECLINED");

        var orderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setCardNumber("4444444444444442");
        var cardPaymentOnCreditPage = orderCardPage.goToPaymentOnCreditPage();
        var formPage = cardPaymentOnCreditPage.goToFormPage();
        formPage.goToNotificationPage(formFieldsInfo);
        formPage.activeNotification();

        int actualCount = rep.getStatusCount("DECLINED");

        Assertions.assertEquals(count + 1, actualCount);
    }
}