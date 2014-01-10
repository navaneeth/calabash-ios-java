package calabash.java;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class WaitTest extends CalabashBaseTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldThrowExceptionIfWaitConditionFails() throws Exception {
        expectedException.expect(OperationTimedoutException.class);
        iosApplication.waitFor(new ICondition() {
            @Override
            public boolean test() throws CalabashException {
                return iosApplication.query("button marked:'some foo element'").size() == 1;
            }
        });

    }

    @Test
    public void shouldRetryTimesSpecified() throws Exception {
        final List<Integer> times = new ArrayList<Integer>();
        final String timeoutMessage = "custom timeout message";
        int retryFreqInSec = 5;
        int timeoutInSec = 20;
        expectedException.expect(OperationTimedoutException.class);
        expectedException.expectMessage(new BaseMatcher<String>() {

            @Override
            public boolean matches(Object o) {
                return o.toString().contains(timeoutMessage);
            }

            @Override
            public void describeTo(Description description) {

            }
        });

        iosApplication.waitFor(new ICondition() {
            @Override
            public boolean test() throws CalabashException {
                times.add(1);
                return iosApplication.query("button marked:'some foo element'").size() == 1;
            }
        }, new WaitOptions(timeoutInSec, retryFreqInSec, 0, timeoutMessage, false));

        assertEquals(timeoutInSec/retryFreqInSec, times.size());
    }

}