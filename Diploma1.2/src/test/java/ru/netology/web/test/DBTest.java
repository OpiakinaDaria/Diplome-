package ru.netology.web.test;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.OrderCardPage;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DBTest {
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

