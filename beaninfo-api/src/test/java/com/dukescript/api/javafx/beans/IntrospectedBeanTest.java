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

import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import net.java.html.json.Models;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class IntrospectedBeanTest {
    @FXBeanInfo.Generate
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

        int notAnAction() {
            return 0;
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
        assertNull(info.getActions().get("notAnAction"));

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

    // BEGIN: IntrospectedBeanTest.Empty
    @FXBeanInfo.Generate
    class Empty extends EmptyBeanInfo {
        public Empty() {
            assert this instanceof FXBeanInfo.Provider;
            assert getFXBeanInfo() != null;
        }
    }
    // END: IntrospectedBeanTest.Empty

    @Test
    public void checkEmpty() {
        assertNotNull("Empty bean created", new Empty());
    }

    // BEGIN: IntrospectedBeanTest.Properties
    @FXBeanInfo.Generate
    class Properties extends PropertiesBeanInfo {
        final StringProperty word = new SimpleStringProperty(this, "name");
        final IntegerProperty count = new SimpleIntegerProperty(this, "count");
        final boolean upperCase;
        final ObservableValue<String> sentence;

        public Properties(boolean upperCase) {
            this.upperCase = upperCase;
            this.sentence = Bindings.createStringBinding(() -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < count.get(); i++) {
                    sb.append(word.get());
                }
                String text = sb.toString();
                return upperCase ? text.toUpperCase() : text;
            }, word, count);

            FXBeanInfo info = getFXBeanInfo();
            Map<String, ObservableValue<?>> p = info.getProperties();
            assert p.get("word") != null;
            assert p.get("count") != null;
            assert p.get("upperCase") != null;
            assert p.get("sentence") != null;
        }
    }
    // END: IntrospectedBeanTest.Properties

    @Test
    public void checkPropertiesNormalCase() {
        Properties normalCase = new Properties(false);
        normalCase.count.set(3);
        normalCase.word.set("Aa");
        assertEquals("AaAaAa", normalCase.sentence.getValue());
    }

    @Test
    public void checkPropertiesUpperCase() {
        Properties upperCase = new Properties(true);
        upperCase.count.set(3);
        upperCase.word.set("Aa");
        assertEquals("AAAAAA", upperCase.sentence.getValue());
    }

    // BEGIN: IntrospectedBeanTest.Actions
    @FXBeanInfo.Generate
    class Actions extends ActionsBeanInfo {
        void init() {
        }

        void onAction(ActionEvent ev) {
        }

        void onActionWithData(ActionDataEvent ev) {
        }

        Actions() {
            FXBeanInfo info = getFXBeanInfo();
            Map<String, EventHandlerProperty> a = info.getActions();
            assert a.get("init") != null;
            assert a.get("onAction") != null;
            assert a.get("onActionWithData") != null;
        }
    }
    // END: IntrospectedBeanTest.Actions

    @Test
    public void checkActions() {
        assertNotNull("Actions object created", new Actions());
    }
}
