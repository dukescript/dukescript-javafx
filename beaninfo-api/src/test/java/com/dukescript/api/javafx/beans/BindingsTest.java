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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class BindingsTest {
    @Test
    public void multiplyBean() {
        // BEGIN: com.dukescript.api.javafx.beans.BindingsTest#multiplyBean
        class Multiply implements FXBeanInfo.Provider {
            final IntegerProperty a = new SimpleIntegerProperty(this, "a", 0);
            final IntegerProperty b = new SimpleIntegerProperty(this, "b", 0);

            /** function to multiply two properties */
            int mul() {
                return a.get() * b.get();
            }
            /** property that depends on a and b and uses {@link #mul()} */
            final IntegerBinding mul = Bindings.createIntegerBinding(this::mul, a, b);

            final FXBeanInfo info = FXBeanInfo.newBuilder(this).
                property(a).
                property(b).
                property("mul", mul).
                property("add", Bindings.createIntegerBinding(
                    // defining derived property as lamda function
                    () -> a.get() + b.get(), a, b
                )).
                build();

            @Override
            public FXBeanInfo getFXBeanInfo() {
                return info;
            }
        }

        Multiply m = new Multiply();
        m.a.set(13);
        m.b.set(7);

        final FXBeanInfo mInfo = m.getFXBeanInfo();
        ObservableValue<?> add = mInfo.getProperties().get("add");
        ObservableValue<?> mul = mInfo.getProperties().get("mul");

        assertEquals(20, add.getValue());
        assertEquals(91, mul.getValue());
        // END: com.dukescript.api.javafx.beans.BindingsTest#multiplyBean
    }
}
