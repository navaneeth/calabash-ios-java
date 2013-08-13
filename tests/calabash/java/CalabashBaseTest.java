package calabash.java;

import org.junit.After;

public class CalabashBaseTest {
    protected IOSApplication iosApplication;

    @After
    public void tearDown() throws Exception {
        if (iosApplication != null)
            iosApplication.exit();
        TestUtil.clearAppDir();
    }

}
