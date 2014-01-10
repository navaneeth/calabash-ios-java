package calabash.java;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class QueryTest extends CalabashBaseTest {

    @Test
    public void shouldQueryForMultipleElements() throws CalabashException {
        UIElements buttons = iosApplication.query("button");
        assertEquals(2, buttons.size());
    }

    @Test
    public void shouldQueryWithFilters() throws CalabashException {
        UIElements buttons = iosApplication.query("buttonLabel text:'Touch me'");
        assertEquals(1, buttons.size());
    }
}
