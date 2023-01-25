package ru.netology.web.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.*;
import io.qameta.allure.selenide.AllureSelenide;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.OrderCardPage;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Selenide.open;

@Epic("Страница покупки тура")
public class BuyTourTest {
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
        open("http://localhost:8080");
    }

    //---ОБЩЕЕ---
    @Feature("Карточка тура")
    @Story("Общее")
    @Test
    @DisplayName("Проверяем, что пустая форма не будет отправлена на сервер")
    void shouldNotSubmitAnEmptyForm() {
        var OrderCardPage = new OrderCardPage();
        var CardPaymentOnCreditPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentOnCreditPage.goToFormPage();
        FormPage.formNotSend();
    }

    //---ФУНКЦИОНАЛ КНОПОК НА СТРАНИЦЕ---
    @Feature("Карточка тура")
    @Story("Кнопки 'Купить' и 'Купить в кредит'")
    @Test
    @DisplayName("Проверяем статус кнопок на изначальной карточке покупки тура (обе должны быть красного цвета)")
    void shouldCheckButtonStatusOnTheOrderCard() {
        var OrderCardPage = new OrderCardPage();
        OrderCardPage.buttonStatusExtraStart();
    }

    @Feature("Карточка тура")
    @Story("Кнопки 'Купить' и 'Купить в кредит'")
    @Test
    @DisplayName("Проверяем статус кнопок при клике на каждую (та, на которую кликнули должна быть белой, другая красной)")
    void shouldCheckButtonsStatus() {
        var OrderCardPage = new OrderCardPage();
        OrderCardPage.buttonStatusExtraPayment();
        OrderCardPage.buttonStatusExtraPaymentOnCredit();
    }

    @Feature("Карточка тура")
    @Story("Кнопки 'Купить' и 'Купить в кредит'")
    @Test
    @DisplayName("Проверяем, что при нажатии на конкретную кнопку переходим к нужной форме. Сначала к одной, потом к другой")
    void shouldGoesToTheDesiredFormOnButtonClick() {
        var OrderCardPage = new OrderCardPage();
        OrderCardPage.goToPaymentPage();
        OrderCardPage.goToPaymentOnCreditPage();
    }

    @Feature("Карточка тура")
    @Story("Кнопка 'Продолжить'")
    @Test
    @DisplayName("Проверяем, что, введя в форму валидные данные, нажав на кнопку «Продолжить» она на некоторое время становится некликабельной")
    void shouldNotBeClickableButtonAfterClick() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.goToNotificationPage(formFieldsInfo);
    }

    //---ВЫПАДАЮЩИЕ СООБЩЕНИЯ НА ОТВЕТ ОТ БАНКОВСКИХ СЕРВИСОВ---
    @Feature("Карточка тура")
    @Story("Выпадающие сообщения")
    @Test
    @DisplayName("Проверяем выпадающее сообщение при покупке тура по карте, когда банк одобрил покупку")
    void shouldSuccessfulSendTheFormOnThePayment() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.goToNotificationPage(formFieldsInfo);
        FormPage.notificationOk();
    }

    @Feature("Карточка тура")
    @Story("Выпадающие сообщения")
    @Test
    @DisplayName("Проверяем выпадающее сообщение при покупке тура в кредит, когда банк одобрил кредит")
    void shouldSuccessfulSendTheFormOnThePaymentOnTheCredit() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        var CardPaymentOnCreditPage = OrderCardPage.goToPaymentOnCreditPage();
        var FormPage = CardPaymentOnCreditPage.goToFormPage();
        FormPage.goToNotificationPage(formFieldsInfo);
        FormPage.notificationOk();
    }

    @Feature("Карточка тура")
    @Story("Выпадающие сообщения")
    @Test
    @DisplayName("Проверяем выпадающее сообщение при покупке тура по карте, когда банк отказал в покупке")
    void shouldBeDeniedPayment() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setCardNumber("4444444444444442");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.goToNotificationPage(formFieldsInfo);
        FormPage.notificationError();
    }

    @Feature("Карточка тура")
    @Story("Выпадающие сообщения")
    @Test
    @DisplayName("Проверяем выпадающее сообщение при покупке тура в кредит, когда банк отказал в выдаче кредита")
    void shouldBeDeniedPaymentOnTheCredit() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setCardNumber("4444444444444442");
        var CardPaymentOnCreditPage = OrderCardPage.goToPaymentOnCreditPage();
        var FormPage = CardPaymentOnCreditPage.goToFormPage();
        FormPage.goToNotificationPage(formFieldsInfo);
        FormPage.notificationError();
    }

    //---ВАЛИДАЦИЯ ПОЛЕЙ ФОРМЫ---
    //---ПОЛЕ НОМЕРА КАРТЫ---
    @Feature("Форма")
    @Story("Поле номера карты")
    @ParameterizedTest
    @CsvFileSource(files = "src/test/resources/InvalidValuesCardNumber.csv")
    @DisplayName("Проверяем, что в поле карты нельзя ввести некоторые невалидные значения")
    void shouldCheckWhatCannotBeEnteredInTheCardNumberFieldSomeInvalidValues(String text) {
        var OrderCardPage = new OrderCardPage();
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.setFieldValue("Номер карты", text);
        String expected = "";
        String actual = FormPage.getFieldValue("Номер карты");
        Assertions.assertEquals(expected, actual);
    }

    @Feature("Форма")
    @Story("Поле номера карты")
    @Test
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле карты не заполнено")
    void shouldShowAnErrorMessageBelowTheCardNumberField_EmptyField() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setCardNumber("");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("Номер карты", "Поле обязательно для заполнения");
    }

    @Feature("Форма")
    @Story("Поле номера карты")
    @Test
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле карты заполнено нулями")
    void shouldShowAnErrorMessageBelowTheCardNumberField_OnlyZero() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setCardNumber("0000000000000000");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("Номер карты", "Неверный формат");
    }

    @Feature("Форма")
    @Story("Поле номера карты")
    @Test
    @DisplayName("Проверяем, что введение пробела в поле карты игнорируется")
    void shouldBeSpaceIgnored() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setCardNumber("4444 4444 4444 4441");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.goToNotificationPage(formFieldsInfo);
    }

    @Feature("Форма")
    @Story("Поле номера карты")
    @Test
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле карты заполнено 15-ю символами")
    void shouldShowAnErrorMessageBelowTheCardNumberFieldWhenEntered15Symbol() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setCardNumber("444444444444441");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("Номер карты", "Неверный формат");

    }

    @Feature("Форма")
    @Story("Поле номера карты")
    @Test
    @DisplayName("Проверяем, что нельзя ввести в поле карты более 16-ти цифр")
    void canOnlyEnter16Digits() {
        var OrderCardPage = new OrderCardPage();
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.setFieldValue("Номер карты", "44444444444444441");
        String expected = "4444 4444 4444 4444";
        String actual = FormPage.getFieldValue("Номер карты");
        Assertions.assertEquals(expected, actual);
    }

    //---ПОЛЕ МЕСЯЦА---
    @Feature("Форма")
    @Story("Поле месяца")
    @ParameterizedTest
    @CsvFileSource(files = "src/test/resources/InvalidValuesMonth.csv")
    @DisplayName("Проверяем, что в поле месяца нельзя ввести некоторые невалидные значения")
    void shouldCheckWhatCannotBeEnteredInTheMonthFieldSomeInvalidValues(String text) {
        var OrderCardPage = new OrderCardPage();
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.setFieldValue("Месяц", text);
        String expected = "";
        String actual = FormPage.getFieldValue("Месяц");
        Assertions.assertEquals(expected, actual);
    }

    @Feature("Форма")
    @Story("Поле месяца")
    @Test
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле месяца не заполнено")
    void shouldShowAnErrorMessageBelowTheMonthField_EmptyField() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setMonth("");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("Месяц", "Поле обязательно для заполнения");
    }

    @Feature("Форма")
    @Story("Поле месяца")
    @Test
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле месяца заполнено нулями")
    void shouldShowAnErrorMessageBelowTheMonthField_OnlyZero() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setMonth("00");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("Месяц", "Неверный формат");
    }

    @Feature("Форма")
    @Story("Поле месяца")
    @Test
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле месяца заполнено одним символом")
    void shouldShowAnErrorMessageBelowTheMonthFieldWhenEnteredOneSymbol() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setMonth("3");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("Месяц", "Неверный формат");
    }

    @Feature("Форма")
    @Story("Поле месяца")
    @Test
    @DisplayName("Проверяем, что нельзя ввести в поле месяц более 2-х цифр")
    void canOnlyEnter2DigitsTheMonthField() {
        var OrderCardPage = new OrderCardPage();
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.setFieldValue("Месяц", "011");
        String expected = "01";
        String actual = FormPage.getFieldValue("Месяц");
        Assertions.assertEquals(expected, actual);
    }

    @Feature("Форма")
    @Story("Поле месяца")
    @Test
    @DisplayName("Проверяем, что в поле месяца нельзя указать несуществующий месяцев")
    void shouldShowAnErrorMessageBelowTheMonthField_13Month() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setMonth("13");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("Месяц", "Неверно указан срок действия карты");
    }

    @Feature("Форма")
    @Story("Поле месяца")
    @Test
    @DisplayName("Проверяем выпадающее сообщения об ошибке, когда в форме указана карта, срок действия которой истек месяц назад")
    void shouldShowAnErrorMessageBelowTheMonthField_CardExpiredLastMonth() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        var generatesMonth = DataHelper.prevMonth();
        formFieldsInfo.setMonth(String.format("|%02d|", generatesMonth.getMonthValue()));
        formFieldsInfo.setYear(String.format("%d", generatesMonth.getYear()).substring(2));
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("Месяц", "Истёк срок действия карты");
    }

    //---ПОЛЕ ГОДА---
    @Feature("Форма")
    @Story("Поле года")
    @ParameterizedTest
    @CsvFileSource(files = "src/test/resources/InvalidValuesYear.csv")
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле года заполнено невалидными значениями")
    void shouldCheckWhatCannotBeEnteredInTheYearFieldSomeInvalidValues(String text) {
        var OrderCardPage = new OrderCardPage();
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.setFieldValue("Год", text);
        String expected = "";
        String actual = FormPage.getFieldValue("Год");
        Assertions.assertEquals(expected, actual);
    }

    @Feature("Форма")
    @Story("Поле года")
    @Test
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле года не заполнено")
    void shouldShowAnErrorMessageBelowTheYearField_EmptyField() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setYear("");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("Год", "Поле обязательно для заполнения");
    }

    @Feature("Форма")
    @Story("Поле года")
    @Test
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле года заполнено нулями")
    void shouldShowAnErrorMessageBelowTheYearField_OnlyZero() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setYear("00");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("Год", "Истёк срок действия карты");
    }

    @Feature("Форма")
    @Story("Поле года")
    @Test
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле года заполнено одним символом")
    void shouldShowAnErrorMessageBelowTheYearFieldWhenEnteredOneSymbol() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setYear("3");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("Год", "Неверный формат");
    }

    @Feature("Форма")
    @Story("Поле года")
    @Test
    @DisplayName("Проверяем, что нельзя ввести в поле года более 2-х цифр")
    void canOnlyEnter2DigitsTheYearField() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.setFieldValue("Год", formFieldsInfo.getYear() + "3");
        String expected = formFieldsInfo.getYear();
        String actual = FormPage.getFieldValue("Год");
        Assertions.assertEquals(expected, actual);
    }

    @Feature("Форма")
    @Story("Поле года")
    @Test
    @DisplayName("Проверяем выпадающее сообщения об ошибке, когда в форме указана карта, срок действия которой истек год назад")
    void shouldShowAnErrorMessageBelowTheYearField_CardExpiredLastYear() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        var generatesYear = DataHelper.prevYear();
        formFieldsInfo.setMonth(String.format("|%02d|", generatesYear.getMonthValue()));
        formFieldsInfo.setYear(String.format("%d", generatesYear.getYear()).substring(2));
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("Год", "Истёк срок действия карты");
    }

    //---ПОЛЕ ВЛАДЕЛЬЦА---
    @Feature("Форма")
    @Story("Поле владельца")
    @ParameterizedTest
    @CsvFileSource(files = "src/test/resources/InvalidValuesOwner.csv")
    @DisplayName("Проверяем, что в поле владельца нельзя ввести некоторые невалидные значения")
    void shouldCheckWhatCannotBeEnteredInTheOwnerFieldSomeInvalidValues(String text) {
        var OrderCardPage = new OrderCardPage();
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.setFieldValue("Владелец", text);
        String expected = "";
        String actual = FormPage.getFieldValue("Владелец");
        Assertions.assertEquals(expected, actual);
    }

    @Feature("Форма")
    @Story("Поле владельца")
    @ParameterizedTest
    @CsvFileSource(files = "src/test/resources/ValidValuesOwner.csv")
    @DisplayName("Проверяем, что форма отправляется, заполнив поле владельца некоторыми валидными значениями")
    void shouldSuccessfullySubmitFormFilledWithSomeValidValues(String text) {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setOwner(text);
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.goToNotificationPage(formFieldsInfo);
    }

    @Feature("Форма")
    @Story("Поле владельца")
    @Test
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле владельца не заполнено")
    void shouldShowAnErrorMessageBelowTheOwnerField_EmptyField() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setOwner("");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("Владелец", "Поле обязательно для заполнения");
    }

    @Feature("Форма")
    @Story("Поле владельца")
    @Test
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле владельца заполнено одним символом")
    void shouldShowAnErrorMessageBelowTheOwnerFieldWheEnteredOneSymbol() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setOwner("Q");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("Владелец", "Неверный формат");
    }

    @Feature("Форма")
    @Story("Поле владельца")
    @Test
    @DisplayName("Проверяем, что нельзя ввести в поле владельца более 150-ти симвалов")
    void canOnlyEnter150SymbolsTheOwnerField() {
        var OrderCardPage = new OrderCardPage();
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.setFieldValue("Владелец", "ANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANN");
        String expected = "ANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAANNAAN";
        String actual = FormPage.getFieldValue("Владелец");
        Assertions.assertEquals(expected, actual);
    }

    @Feature("Форма")
    @Story("Поле владельца")
    @Test
    @DisplayName("Проверяем, что при введении в поле владельца символов в нижнем регистре, они будут преобразованы в верхний регистр")
    void shouldUppercaseConversion() {
        var OrderCardPage = new OrderCardPage();
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.setFieldValue("Владелец", "anna");
        String expected = "ANNA";
        String actual = FormPage.getFieldValue("Владелец");
        Assertions.assertEquals(expected, actual);
    }

    //---ПОЛЕ CVC/CVV---
    @Feature("Форма")
    @Story("Поле CVC/CVV")
    @ParameterizedTest
    @CsvFileSource(files = "src/test/resources/InvalidValuesCcvCvv.csv")
    @DisplayName("Проверяем, что в поле CVC/CVV нельзя ввести некоторые невалидные значения")
    void shouldCheckWhatCannotBeEnteredInTheCcvCvvFieldSomeInvalidValues(String text) {
        var OrderCardPage = new OrderCardPage();
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.setFieldValue("CVC/CVV", text);
        String expected = "";
        String actual = FormPage.getFieldValue("CVC/CVV");
        Assertions.assertEquals(expected, actual);
    }

    @Feature("Форма")
    @Story("Поле CVC/CVV")
    @Test
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле CVC/CVV не заполнено")
    void shouldShowAnErrorMessageBelowTheCcvCvvField_EmptyField() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setCcvCvv("");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("CVC/CVV", "Поле обязательно для заполнения");
    }

    @Feature("Форма")
    @Story("Поле CVC/CVV")
    @Test
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле CVC/CVV заполнено нулями")
    void shouldShowAnErrorMessageBelowTheCcvCvvField_OnlyZero() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setCcvCvv("000");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("CVC/CVV", "Неверный формат");
    }


    @Feature("Форма")
    @Story("Поле CVC/CVV")
    @Test
    @DisplayName("Проверяем выпадающее сообщение об ошибке, когда поле CVC/CVV заполнено двумя символами")
    void shouldShowAnErrorMessageBelowTheCcvCvvFieldWheEnteredTwoSymbol() {
        var OrderCardPage = new OrderCardPage();
        var formFieldsInfo = DataHelper.getValidFieldSet();
        formFieldsInfo.setCcvCvv("23");
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.fillingOutFormFields(formFieldsInfo);
        FormPage.formNotSend();
        FormPage.errorMessage("CVC/CVV", "Неверный формат");
    }

    @Feature("Форма")
    @Story("Поле CVC/CVV")
    @Test
    @DisplayName("Проверяем, что нельзя ввести в поле CVC/CVV более 3-х цифр")
    void canOnlyEnter3DigitsTheCcvCvvField() {
        var OrderCardPage = new OrderCardPage();
        var CardPaymentPage = OrderCardPage.goToPaymentPage();
        var FormPage = CardPaymentPage.goToFormPage();
        FormPage.setFieldValue("CVC/CVV", "4321");
        String expected = "432";
        String actual = FormPage.getFieldValue("CVC/CVV");
        Assertions.assertEquals(expected, actual);
    }

    //---БАЗА ДАННЫХ---
    @Feature("База данных")
    @Story("Успешные операции")
    @Test
    @DisplayName("Проверяем, что приложение сохранет в своей БЗ успешно совершенный платеж по карте")
    void shouldStoreTheSuccessfullyTheCompletedCardPaymentInTheDatabase() {
        var dataSQL = "SELECT COUNT(id) FROM payment_entity WHERE status = 'APPROVED'";
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:8080/app", "app", "pass");) {
            Long count = runner.query(conn, dataSQL, new ScalarHandler<Long>());
            var OrderCardPage = new OrderCardPage();
            var formFieldsInfo = DataHelper.getValidFieldSet();
            var CardPaymentPage = OrderCardPage.goToPaymentPage();
            var FormPage = CardPaymentPage.goToFormPage();
            FormPage.goToNotificationPage(formFieldsInfo);
            FormPage.notificationOk();
            Long actualCount = runner.query(conn, dataSQL, new ScalarHandler<Long>());
            Assertions.assertEquals(count.intValue() + 1, actualCount.intValue());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Feature("База данных")
    @Story("Успешные операции")
    @Test
    @DisplayName("Проверяем, что приложение сохранет в своей БЗ успешно совершенную покупку в кредит")
    void shouldStoreTheSuccessfullyTheCompletedPaymentOnTheCreditInTheDatabase() {
        var dataSQL = "SELECT COUNT(id) FROM credit_request_entity WHERE status = 'APPROVED'";
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:8080/app", "app", "pass");) {
            Long count = runner.query(conn, dataSQL, new ScalarHandler<Long>());
            var OrderCardPage = new OrderCardPage();
            var formFieldsInfo = DataHelper.getValidFieldSet();
            var CardPaymentOnCreditPage = OrderCardPage.goToPaymentOnCreditPage();
            var FormPage = CardPaymentOnCreditPage.goToFormPage();
            FormPage.goToNotificationPage(formFieldsInfo);
            FormPage.activeNotification();
            Long actualCount = runner.query(conn, dataSQL, new ScalarHandler<Long>());
            Assertions.assertEquals(count.intValue() + 1, actualCount.intValue());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Feature("База данных")
    @Story("Отказ в операции")
    @Test
    @DisplayName("Проверяем, что приложение сохранет в своей БЗ отказ в совершении платежа по карте")
    void shouldStoreTheDenialOfCardPaymentInTheDatabase() {
        var dataSQL = "SELECT COUNT(id) FROM payment_entity WHERE status = 'DECLINED'";
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:8080/app", "app", "pass");) {
            Long count = runner.query(conn, dataSQL, new ScalarHandler<Long>());
            var OrderCardPage = new OrderCardPage();
            var formFieldsInfo = DataHelper.getValidFieldSet();
            formFieldsInfo.setCardNumber("4444444444444442");
            var CardPaymentPage = OrderCardPage.goToPaymentPage();
            var FormPage = CardPaymentPage.goToFormPage();
            FormPage.goToNotificationPage(formFieldsInfo);
            Long actualCount = runner.query(conn, dataSQL, new ScalarHandler<Long>());
            Assertions.assertEquals(count.intValue() + 1, actualCount.intValue());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Feature("База данных")
    @Story("Отказ в операции")
    @Test
    @DisplayName("Проверяем, что приложение сохранет в своей БЗ отказ в совершении покупке в кредит")
    void shouldStoreTheDenialOfPaymentOnTheCreditInTheDatabase() {
        var dataSQL = "SELECT COUNT(id) FROM credit_request_entity WHERE status = 'DECLINED'";
        var runner = new QueryRunner();
        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:8080/app", "app", "pass");) {
            Long count = runner.query(conn, dataSQL, new ScalarHandler<Long>());
            var OrderCardPage = new OrderCardPage();
            var formFieldsInfo = DataHelper.getValidFieldSet();
            formFieldsInfo.setCardNumber("4444444444444442");
            var CardPaymentOnCreditPage = OrderCardPage.goToPaymentOnCreditPage();
            var FormPage = CardPaymentOnCreditPage.goToFormPage();
            FormPage.goToNotificationPage(formFieldsInfo);
            Long actualCount = runner.query(conn, dataSQL, new ScalarHandler<Long>());
            Assertions.assertEquals(count.intValue() + 1, actualCount.intValue());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}