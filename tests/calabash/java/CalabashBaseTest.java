package calabash.java;

import org.junit.After;
import org.junit.Before;

import static calabash.java.TestUtil.initializeAndStart;

public class CalabashBaseTest {
    protected IOSApplication iosApplication;

    @Before
    public void setup() throws Exception {
        iosApplication = initializeAndStart("FirstDemo");
        iosApplication.waitForElementsExist(new String[]{"textField"});
    }

    @After
    public void tearDown() throws Exception {
        if (iosApplication != null)
            iosApplication.exit();
        TestUtil.clearAppDir();
    }

}
