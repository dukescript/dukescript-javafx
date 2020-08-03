package com.dukescript.api.javafx.beans;

/*-
 * #%L
 * DukeScript JavaFX Extensions - a library from the "DukeScript" project.
 * %%
 * Copyright (C) 2020 Dukehoff GmbH
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import net.java.html.json.Models;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class IntrospectedBeanTest {
    @FXBeanInfo.Introspect
    static class SampleComponent extends SampleComponentBeanInfo {
        final BooleanProperty ok = new SimpleBooleanProperty(this, "ok", true);
        final StringProperty fine = new SimpleStringProperty(this, "fine", "ok");
        final String immutable = "Hi";
        final EventHandlerProperty callback = new SimpleEventHandlerProperty(this::callback);

        private int counter;
        private ActionEvent ev;

        private void callback(ActionEvent ev) {
            counter++;
            this.ev = ev;
        }

        void noArgCallback() {
            counter++;
            this.ev = null;
        }

        void actionCallback(ActionEvent ev) {
            counter++;
            this.ev = ev;
        }

        void actionDataCallback(ActionDataEvent ev) {
            counter += ev.getProperty(Number.class, null).intValue();
            this.ev = ev;
        }

        static void ignore() {
        }
    }

    @Test
    public void checkIntrospectedSampleComponent() throws Exception {
        final SampleComponent bean = new SampleComponent();
        final Object component = bean;
        assertTrue("This is a model!", Models.isModel(component.getClass()));
        assertTrue("Provides bean info", component instanceof FXBeanInfo.Provider);
        FXBeanInfo.Provider p = (FXBeanInfo.Provider) component;
        FXBeanInfo info = p.getFXBeanInfo();
        assertNotNull("Info provided", info);
        assertNotNull(info.getProperties().get("ok"));
        assertNotNull(info.getProperties().get("fine"));
        assertNotNull(info.getProperties().get("immutable"));
        assertNull(info.getProperties().get("noProp"));

        assertNull("Callback isn't a property", info.getProperties().get("callback"));
        EventHandlerProperty callback = info.getActions().get("callback");
        assertNotNull("Callback is an action", callback);

        ActionDataEvent ev = new ActionDataEvent(bean, this, 5);

        assertEquals("No counter", 0, bean.counter);
        callback.getValue().handle(ev);
        assertEquals("Incremented counter", 1, bean.counter);
        assertEquals("The right event", bean.ev, ev);

        EventHandlerProperty noArgCallback;
        assertNotNull(noArgCallback = info.getActions().get("noArgCallback"));
        EventHandlerProperty actionCallback;
        assertNotNull(actionCallback = info.getActions().get("actionCallback"));
        EventHandlerProperty actionDataCallback;
        assertNotNull(actionDataCallback = info.getActions().get("actionDataCallback"));

        bean.counter = 0;
        noArgCallback.getValue().handle(ev);
        assertEquals("One", 1, bean.counter);
        assertNull("No event", bean.ev);

        bean.counter = 0;
        actionCallback.getValue().handle(ev);
        assertEquals("One", 1, bean.counter);
        assertEquals("The event", bean.ev, ev);

        bean.counter = 0;
        actionDataCallback.getValue().handle(ev);
        assertEquals("Plus five", 5, bean.counter);
        assertEquals("The event", bean.ev, ev);
    }
}
