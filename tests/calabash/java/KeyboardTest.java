package calabash.java;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.Assert.assertEquals;

public class KeyboardTest extends CalabashBaseTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldEnterAlphabets() throws CalabashException {
        UIElement textField = iosApplication.query("textField").get(0);
        textField.touch();
        iosApplication.getKeyboard().enterText("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        assertEquals("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", textField.getText());
    }

    @Test
    public void shouldEnterNumbers() throws CalabashException {
        UIElement textField = iosApplication.query("textField").get(0);
        textField.touch();
        iosApplication.getKeyboard().enterText("1234567890");
        assertEquals("1234567890", textField.getText());
    }

    @Test
    public void shouldEnterSpecialCharAsText() throws CalabashException {
        UIElement textField = iosApplication.query("textField").get(0);
        textField.touch();
        //todo entering \
        iosApplication.getKeyboard().enterText("!@#$%^&*()_+{}|:\"<>?-=[];',./");
        assertEquals("!@#$%^&*()_+{}|:\"<>?-=[];',./", textField.getText());
    }

    @Test
    public void shouldEnterSpecialChars() throws CalabashException {
        UIElement textField = iosApplication.query("textField").get(0);
        textField.touch();
        iosApplication.getKeyboard().pressSpecialKey(SpecialKeys.Exclamation);
        iosApplication.getKeyboard().pressSpecialKey(SpecialKeys.QuestionMark);
        assertEquals("!?", textField.getText());
        iosApplication.getKeyboard().pressSpecialKey(SpecialKeys.Delete);
        assertEquals("!", textField.getText());
    }

    @Test
    public void shouldThrowExceptionWhenUsingKeyboardBeforeLaunchingKeyboard() throws CalabashException {
        expectedException.expect(CalabashException.class);
        expectedException.expectMessage("Failed to enter text: hi. (RuntimeError) no visible keyboard");
        iosApplication.getKeyboard().enterText("hi");
    }

    @Test
    public void shouldRemoveKeyboardAfterPressingReturn() throws CalabashException {
        UIElement textField = iosApplication.query("textField").get(0);
        textField.touch();
        iosApplication.getKeyboard().pressSpecialKey(SpecialKeys.Return);

        expectedException.expect(CalabashException.class);
        expectedException.expectMessage("Failed to enter text: after return. (RuntimeError) no visible keyboard");
        iosApplication.getKeyboard().enterText("after return");
    }

}
