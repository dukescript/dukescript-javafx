package com.dukescript.demo.javafx.webui;

import java.util.List;
import net.java.html.junit.BrowserRunner;
import static org.junit.Assert.assertEquals;
import org.junit.runner.RunWith;
import org.junit.Test;

/** Tests for behavior of your application in isolation. Verify
 * behavior of your MVVC code in a unit test.
 */
@RunWith(BrowserRunner.class)
public class DataModelTest {
    @Test public void testUIModelWithoutUI() {
        DataModel model = new DataModel();
        model.message.set("Hello World!");

        List<String> arr = model.words.getValue();
        assertEquals("Six words always", arr.size(), 6);
        assertEquals("Hello is the first word", "Hello", arr.get(0));
        assertEquals("World is the second word", "World!", arr.get(1));
    }
}
