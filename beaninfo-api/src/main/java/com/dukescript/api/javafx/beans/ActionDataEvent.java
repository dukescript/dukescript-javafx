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

import com.dukescript.impl.javafx.beans.ActionDataEventFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import net.java.html.json.Models;

/** Enhanced version of {@link ActionEvent}. Actions that can be invoked
 * on a {@linkplain FXBeanInfo#getBean() bean} are
 * {@linkplain FXBeanInfo#getFunctions() represented as properties}
 * with {@link EventHandler} that accepts {@link ActionEvent} or its
 * {@link ActionDataEvent} subclass.
 * <p>
 * Stick to {@link ActionEvent} if it provides enough information. Use
 * this subclass if additional
 * information about the
 * {@link #getProperty(java.lang.Class, java.lang.String) event properties}
 * or type-safe view of the {@link #getSource(java.lang.Class) event source}
 * is needed.
 */
public final class ActionDataEvent extends ActionEvent {
    private static final ActionDataEventFactory FACTORY = new ActionDataEventFactory() {
        @Override
        public ActionDataEvent create(ActionDataEventFactory.Convertor owner, FXBeanInfo.Provider model, Object source, Object event) {
            return new ActionDataEvent(owner, model, source, source);
        }
    };

    private final FXBeanInfo.Provider model;
    private final ActionDataEventFactory.Convertor conv;
    private final Object event;

    ActionDataEvent(ActionDataEventFactory.Convertor conv, FXBeanInfo.Provider model, Object source, Object event) {
        super(source, null);
        this.model = model;
        this.conv = conv;
        this.event = event;
    }

    /** Obtains property of the event.
     *
     * @param <T> type of the property usually {@link String} or {@link Integer} or {@link Double}
     * @param as the class of requested type
     * @param name name of the property to obtain
     * @return the value of the property or {@code null}
     */
    public <T> T getProperty(Class<T> as, String name) {
        Object value;
        if (as == String.class) {
            value = conv.toString(model.getFXBeanInfo(), event, name);
        } else if (Number.class.isAssignableFrom(as)) {
            value = conv.toNumber(model.getFXBeanInfo(), event, name);
        } else {
            value = null;
        }
        return extractValue(as, value);
    }

    /** Obtains the source of the event.
     *
     * @param <T> type of the source that is requested
     * @param as class of the requested type
     * @return instance of the requested type
     * @throws ClassCastException when the requested type isn't correct
     */
    public <T> T getSource(Class<T> as) {
        return extractValue(as, getSource());
    }

    private <T> T extractValue(Class<T> as, Object d) {
        Object obj;
        if (Models.isModel(as)) {
            obj = conv.toModel(model.getFXBeanInfo(), as, d);
        } else {
            obj = d;
        }
        return conv.extractValue(as, obj);
    }
}
