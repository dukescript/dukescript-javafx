package com.dukescript.impl.javafx.beans;

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
import com.dukescript.api.javafx.beans.FXBeanInfo;

public abstract class ActionDataEventFactory {
    private static ActionDataEventFactory INSTANCE;
    static {
        try {
            Class<?> c = Class.forName(ActionDataEvent.class.getName(), true, ActionDataEvent.class.getClassLoader());
            assert c == ActionDataEvent.class;
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected ActionDataEventFactory() {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        }
        INSTANCE = this;
    }
    protected abstract ActionDataEvent create(Convertor owner, FXBeanInfo.Provider model, Object source, Object event);

    protected final Convertor none() {
        return NONE;
    }

    public static ActionDataEvent newEvent(Convertor owner, FXBeanInfo.Provider model, Object source, Object event) {
        return INSTANCE.create(owner, model, source, event);
    }

    public static interface Convertor {
        public Object toString(FXBeanInfo fxBeanInfo, Object event, String name);

        public Object toNumber(FXBeanInfo fxBeanInfo, Object event, String name);

        public <T> Object toModel(FXBeanInfo fxBeanInfo, Class<T> as, Object d);

        public <T> T extractValue(Class<T> as, Object obj);
    }
    private static final Convertor NONE = new Convertor() {
        @Override
        public Object toString(FXBeanInfo fxBeanInfo, Object event, String name) {
            return event.toString();
        }

        @Override
        public Object toNumber(FXBeanInfo fxBeanInfo, Object event, String name) {
            return event instanceof Number ? event : null;
        }

        @Override
        public <T> Object toModel(FXBeanInfo fxBeanInfo, Class<T> as, Object obj) {
            return as.isInstance(obj) ? as.cast(obj) : null;
        }

        @Override
        public <T> T extractValue(Class<T> as, Object obj) {
            return as.isInstance(obj) ? as.cast(obj) : null;
        }
    };
}
