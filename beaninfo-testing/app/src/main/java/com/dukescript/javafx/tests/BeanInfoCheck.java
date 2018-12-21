package com.dukescript.javafx.tests;

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


import com.dukescript.api.javafx.beans.ActionDataEvent;
import com.dukescript.api.javafx.beans.EventHandlerProperty;
import com.dukescript.api.javafx.beans.FXBeanInfo;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class BeanInfoCheck {
    @Test
    public void beanInfoCanBeCreated() {
        class ExposeBeanInfo implements FXBeanInfo.Provider {
            final FXBeanInfo info = FXBeanInfo.newBuilder(this).build();

            @Override
            public FXBeanInfo getFXBeanInfo() {
                return info;
            }
        }

        FXBeanInfo info = new ExposeBeanInfo().getFXBeanInfo();
        assertNotNull("Bean info created", info);
    }

    private static
    // BEGIN: com.dukescript.javafx.tests.BeanInfoCheck#CountingBean
    class CountingBean implements FXBeanInfo.Provider {
        int count;

        private void addAction(ActionEvent ev) {
            count += ((Number) ev.getSource()).intValue();
        }
        private void incrementAction() {
            count++;
        }
        private void addTwiceAction(ActionDataEvent ev) {
            count += ev.getSource(Number.class).intValue();
            count += ev.getProperty(Number.class, "any").intValue();
        }

        private final FXBeanInfo infoWithActions = FXBeanInfo.newBuilder(this)
                    .action("actionWithEvent", this::addAction)
                    .action("actionWithoutParameters", this::incrementAction)
                    .action("actionWithData", this::addTwiceAction)
                    .build();

        @Override
        public FXBeanInfo getFXBeanInfo() {
            return infoWithActions;
        }
    }
    // END: com.dukescript.javafx.tests.BeanInfoCheck#CountingBean

    @Test
    public void beanInfoCanHaveActions() {
        CountingBean bean = new CountingBean();
        FXBeanInfo info = bean.getFXBeanInfo();

        assertNotNull("Bean info created", info);
        final Map<String, EventHandlerProperty> actions = info.getActions();
        assertNotNull("It has actions", actions);
        assertEquals("Three actions found", 3, actions.size());

        EventHandlerProperty incrementByOne = actions.get("actionWithoutParameters");
        incrementByOne.getValue().handle(new ActionDataEvent(bean, this, null));
        assertEquals("Action was called", 1, bean.count);

        EventHandlerProperty addSource = actions.get("actionWithEvent");
        addSource.getValue().handle(new ActionDataEvent(bean, 4, null));
        assertEquals("Action was called", 5, bean.count);
        dispatchEvent(bean, "actionWithData");
        assertEquals("Action was called", 25, bean.count);
    }

    // BEGIN: com.dukescript.javafx.tests.BeanInfoCheck#invokeEventHandlerProperty
    private static void dispatchEvent(FXBeanInfo.Provider bean, String name) {
        final FXBeanInfo info = bean.getFXBeanInfo();
        final Map<String, EventHandlerProperty> actions = info.getActions();
        EventHandlerProperty property = actions.get(name);
        final EventHandler<? super ActionDataEvent> handler = property.getValue();
        handler.handle(new ActionDataEvent(bean, 7, 13));
    }
    // END: com.dukescript.javafx.tests.BeanInfoCheck#invokeEventHandlerProperty
}
