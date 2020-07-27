package ru.netology.sql.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Condition.visible;

public class VerificationPage {
    private SelenideElement codeField = $("[data-test-id=code] input");
    private SelenideElement verifyButton = $("[data-test-id=action-verify]");
    private SelenideElement errorWindow = $("[data-test-id=error-notification]");


    public VerificationPage() {
        codeField.shouldBe(visible);
    }

    public DashboardPage validVerify(String verificationCode) {
        codeField.setValue(verificationCode);
        verifyButton.click();
        return new DashboardPage();
    }
    public SelenideElement NoValidVerify() {
        codeField.setValue("123");
        verifyButton.click();
        return errorWindow;
    }

    public void assertErrorText() {
        errorWindow.shouldHave(text("Неверно указан код! Попробуйте ещё раз."));
    }
}
