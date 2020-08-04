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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;

public class FXBeanInfoProcessorTest {
    public FXBeanInfoProcessorTest() {
    }

    @FXBeanInfo.Generate
    @FXBeanInfoProcessor.ExpectError("Only use @Introspect on classes")
    public interface IntrospectedInterfaceError {
    }

    @FXBeanInfo.Generate
    @FXBeanInfoProcessor.ExpectError("Only use @Introspect on classes")
    public @interface IntrospectedAnnotationError {
    }

    @FXBeanInfo.Generate
    @FXBeanInfoProcessor.ExpectError("Class must extend another class. Try adding: extends BeanWithoutExtendsBeanInfo")
    public class BeanWithoutExtends {
    }

    @FXBeanInfo.Generate
    @FXBeanInfoProcessor.ExpectError("Class cannot be private")
    private static class PrivateClazz extends GenericBeanInfo {
    }

    @FXBeanInfo.Generate
    public static class Generic extends GenericBeanInfo {
    }

    @FXBeanInfo.Generate
    static class OverloadedProperties extends OverloadedPropertiesBeanInfo {
        @FXBeanInfoProcessor.ExpectError("text is defined multiple times")
        final StringProperty text = new SimpleStringProperty();

        @FXBeanInfoProcessor.ExpectError("text is defined multiple times")
        final void text() {
        }

        @FXBeanInfoProcessor.ExpectError("text is defined multiple times")
        final void text(ActionEvent ev) {
        }

        @FXBeanInfoProcessor.ExpectError("text is defined multiple times")
        final void text(ActionDataEvent ev) {
        }
    }
}
