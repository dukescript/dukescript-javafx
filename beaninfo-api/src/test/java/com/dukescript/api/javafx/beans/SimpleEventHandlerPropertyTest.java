package com.dukescript.api.javafx.beans;

/*-
 * #%L
 * DukeScript JavaFX Extensions - a library from the "DukeScript" project.
 * %%
 * Copyright (C) 2018 Dukehoff GmbH
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

import javafx.event.EventHandler;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class SimpleEventHandlerPropertyTest {
    // BEGIN: com.dukescript.api.javafx.beans.EventHandlerPropertyTest#CountingBean
    class CountingBean implements FXBeanInfo.Provider {
        private int count;

        final EventHandlerProperty incrementAction =
        new SimpleEventHandlerProperty(this, "actionWithoutParameters", (ev) -> {
            count++;
        });

        final EventHandlerProperty addAction =
        new SimpleEventHandlerProperty(this, "actionWithEvent", (ev) -> {
            count += ((Number) ev.getSource()).intValue();
        });

        final EventHandlerProperty addTwiceAction =
        new SimpleEventHandlerProperty(this, "actionWithData", (ev) -> {
            count += ev.getSource(Number.class).intValue();
            count += ev.getProperty(Number.class, "any").intValue();
        });

        private final FXBeanInfo info = FXBeanInfo.newBuilder(this)
            .action(this.incrementAction)
            .action(this.addAction)
            .action(this.addTwiceAction)
            .build();

        @Override
        public FXBeanInfo getFXBeanInfo() {
            return info;
        }
    }
    // END: com.dukescript.api.javafx.beans.EventHandlerPropertyTest#CountingBean

    private CountingBean bean;
    private FXBeanInfo info;

    @Before
    public void createCountingBean() {
        bean = new CountingBean();
        info = bean.getFXBeanInfo();
    }

    @Test
    public void testIncrementByOne() {
        EventHandlerProperty p = info.getActions().get("actionWithoutParameters");
        assertNotNull("property found", p);
        final EventHandler<? super ActionDataEvent> h = p.getValue();
        assertNotNull("it has handler", h);
        h.handle(null);
        assertEquals(1, bean.count);
    }

    @Test
    public void testIncrementByThis() {
        EventHandlerProperty p = info.getActions().get("actionWithEvent");
        assertNotNull("property found", p);
        final EventHandler<? super ActionDataEvent> h = p.getValue();
        assertNotNull("it has handler", h);
        ActionDataEvent ev = new ActionDataEvent(bean, 3, null);
        h.handle(ev);
        assertEquals(3, bean.count);
    }

    @Test
    public void testIncrementByEvent() {
        EventHandlerProperty p = info.getActions().get("actionWithData");
        assertNotNull("property found", p);
        final EventHandler<? super ActionDataEvent> h = p.getValue();
        assertNotNull("it has handler", h);
        ActionDataEvent ev = new ActionDataEvent(bean, 3, 5);
        h.handle(ev);
        assertEquals(8, bean.count);
    }
}
