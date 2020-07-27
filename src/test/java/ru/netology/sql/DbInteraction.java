package ru.netology.sql;

import com.github.javafaker.Faker;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.sql.data.User;
import ru.netology.sql.page.LoginPage;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DbInteraction {
    private static String url = "jdbc:mysql://192.168.99.100:3306/app";
    private static String user = "app";
    private static String password = "pass";
    private String serviceUrl = "http://localhost:9999/";
    private static List<User> users;


    @BeforeAll
    static void setUp() throws SQLException {
        val runner = new QueryRunner();
        val usersSQL = "SELECT * FROM users;";
        try (val conn = DriverManager.getConnection(url, user, password)) {
            users = runner.query(conn, usersSQL, new BeanListHandler<>(User.class));
        }
    }

    @AfterAll
    static void clearDB() throws SQLException {
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection(url, user, password)) {
            runner.update(conn, "DELETE FROM card_transactions;");
            runner.update(conn, "DELETE FROM auth_codes;");
            runner.update(conn, "DELETE FROM cards;");
            runner.update(conn, "DELETE FROM users;");
        }
    }

    @Test
    @DisplayName("Added user in DB and login")
    void shouldAddedUser() throws SQLException {

        val newUser = new User("1", "user", "password", "active");

        val runner = new QueryRunner();
        val addUserSQL = "INSERT INTO users (id, login, password) values(?, ?, ?);";
        val codeSQL = "SELECT code FROM auth_codes WHERE user_id = ? ORDER BY created DESC;";
        try (val conn = DriverManager.getConnection(url, user, password)) {
            runner.update(conn, addUserSQL, newUser.getId(), newUser.getLogin(), newUser.getPassword());
            open(serviceUrl);
            val loginPage = new LoginPage();
            val verificationPage = loginPage.validLogin(newUser);
            val code = runner.query(conn, codeSQL, new ScalarHandler<String>(), newUser.getId());
            verificationPage.validVerify(code);
        }
    }

    @Test
    @DisplayName("Positive test:first user")
    void shouldReloadDashboardPage() throws SQLException {
        val runner = new QueryRunner();
        val codeSQL = "SELECT code FROM auth_codes WHERE user_id = ? ORDER BY created DESC;";
        try (val conn = DriverManager.getConnection(url, user, password)) {
            open(serviceUrl);
            val loginPage = new LoginPage();
            val verificationPage = loginPage.validLogin(users.get(0));
            val code = runner.query(conn, codeSQL, new ScalarHandler<String>(), users.get(0).getId());
            verificationPage.validVerify(code);
        }
    }

}
