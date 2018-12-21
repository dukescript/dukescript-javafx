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

import javafx.beans.property.ReadOnlyProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/** Property representing an {@link EventHandler}. Use one of
 * {@link FXBeanInfo.Builder#action} methods to create instance of the 
 * property rather than implementing the interface manually.
 * <p>
 * The following example defines a class with three action methods
 * the uses their handle references to register them into the builder:
 * <p>
 * {@codesnippet com.dukescript.javafx.tests.BeanInfoCheck#CountingBean}
 * <p>
 * The handler method may take no arguments, a single {@link ActionEvent} parameter
 * or a single {@link ActionDataEvent} parameter.
 * The registered methods are then accessible via {@link FXBeanInfo#getActions()}
 * map.
 * 
 * @since 0.4
 */
public interface EventHandlerProperty extends ReadOnlyProperty<EventHandler<? super ActionDataEvent>> {
}
