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

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;

/**
 * Simple implementation of a property
 * representing an {@link FXBeanInfo#getActions() action} in the
 * {@link FXBeanInfo}.
 * <p>
 * It is usually easier to use
 * {@link FXBeanInfo.Builder#action(java.lang.String, java.lang.Runnable)}
 * or
 * {@link FXBeanInfo.Builder#action(java.lang.String, javafx.event.EventHandler)},
 * but should properties be the preferred way to represent actions,
 * here is how to use them:
 * <p>
 * {@codesnippet com.dukescript.api.javafx.beans.EventHandlerPropertyTest#CountingBean}
 *
 * @since 0.4
 */
public class SimpleEventHandlerProperty extends
    SimpleObjectProperty<EventHandler<? super ActionDataEvent>>
    implements EventHandlerProperty {

    /** Default constructor.
     * @since 0.4
     */
    public SimpleEventHandlerProperty() {
        super();
    }

    /** Constructor with handler.
     *
     * @param handler provide the handler
     * @since 0.4
     */
    public SimpleEventHandlerProperty(EventHandler<? super ActionDataEvent> handler) {
        super(handler);
    }

    /** Constructor with bean and name.
     *
     * @param bean the bean
     * @param name the name of the property
     */
    public SimpleEventHandlerProperty(Object bean, String name) {
        super(bean, name);
    }

    /** Constructor with all attributes.
     *
     * @param bean the bean
     * @param name name of the property
     * @param handler handler
     *
     * @since 0.4
     */
    public SimpleEventHandlerProperty(
        Object bean, String name, EventHandler<? super ActionDataEvent> handler
    ) {
        super(bean, name, handler);
    }
}
