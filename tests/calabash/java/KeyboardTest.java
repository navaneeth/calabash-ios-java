package calabash.java;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;

import static calabash.java.TestUtil.initializeAndStart;
import static junit.framework.Assert.assertEquals;

public class KeyboardTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private IOSApplication iosApplication;

    @Before
    public void setUp() throws Exception {
        CalabashConfiguration calabashConfiguration = new CalabashConfiguration();
        calabashConfiguration.setLogsDirectory(new File("/tmp"));
        iosApplication = initializeAndStart("FirstDemo", calabashConfiguration);
        iosApplication.waitForElementsExist(new String[]{"textField"});
    }

    @After
    public void tearDown() throws Exception {
        if (iosApplication != null)
            iosApplication.exit();
        TestUtil.clearAppDir();
    }

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
        iosApplication.getKeyboard().enterText("!@#$%^&*()_+{}|:\"<>?-=[]\\;',./");
        assertEquals("!@#$%^&*()_+{}|:\"<>?-=[]\\;',./", textField.getText());
    }

    @Test
    public void shouldEnterSpecialChars() throws CalabashException {
        UIElement textField = iosApplication.query("textField").get(0);
        textField.touch();
        iosApplication.getKeyboard().pressSpecialKey(SpecialKeys.More);
        iosApplication.getKeyboard().pressSpecialKey(SpecialKeys.Exclamation);
        iosApplication.getKeyboard().pressSpecialKey(SpecialKeys.QuestionMark);
        assertEquals("!?", textField.getText());
        iosApplication.getKeyboard().pressSpecialKey(SpecialKeys.Delete);
        assertEquals("!", textField.getText());
    }

    @Test
    public void shouldThrowExceptionWhenUsingKeyboardBeforeLaunchingKeyboard() throws CalabashException {
        expectedException.expect(CalabashException.class);
        expectedException.expectMessage("Failed to enter text: hi. (RuntimeError) No visible keyboard");
        iosApplication.getKeyboard().enterText("hi");
    }

    @Test
    public void shouldRemoveKeyboardAfterPressingReturn() throws CalabashException {
        UIElement textField = iosApplication.query("textField").get(0);
        textField.touch();
        iosApplication.getKeyboard().pressSpecialKey(SpecialKeys.Return);

        expectedException.expect(CalabashException.class);
        expectedException.expectMessage("Failed to enter text: after return. (RuntimeError) No visible keyboard");
        iosApplication.getKeyboard().enterText("after return");
    }

}
