package ru.netology.sql.page;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.sql.data.User;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private SelenideElement loginField = $("[data-test-id=login] input");
    private SelenideElement passwordField = $("[data-test-id=password] input");
    private SelenideElement loginButton = $("[data-test-id=action-login]");
    private SelenideElement errorWindow = $("[data-test-id=error-notification]");

    public VerificationPage validLogin(User user) {
        loginField.setValue(user.getLogin());
        if (user.getLogin().equals("vasya")) {
            passwordField.setValue("qwerty123");
        } else if (user.getLogin().equals("petya")) {
            passwordField.setValue("123qwerty");
        } else passwordField.setValue(user.getPassword());
        loginButton.click();
        return new VerificationPage();
    }

    public void noValidLogin(User user) {
        loginField.setValue(user.getLogin().substring(1));
        passwordField.setValue(user.getPassword());
        loginButton.click();
    }

    public void noValidPassword(User user) {
        loginField.setValue(user.getLogin());
        passwordField.setValue(user.getPassword());
        loginButton.click();
    }

    public void noValidPasswordThreeTimes(User user) {
        this.noValidPassword(user);
        passwordField.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE, Keys.ESCAPE));
        passwordField.setValue(user.getPassword());
        loginButton.click();
        passwordField.sendKeys(Keys.chord(Keys.CONTROL, "a", Keys.DELETE, Keys.ESCAPE));
        passwordField.setValue(user.getPassword());
        loginButton.click();
    }

    public void assertErrorText() {
        errorWindow.shouldHave(text("Неверно указан логин или пароль"));
    }
}
