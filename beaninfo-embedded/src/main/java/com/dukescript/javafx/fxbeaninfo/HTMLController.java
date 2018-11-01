package com.dukescript.javafx.fxbeaninfo;

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

import com.dukescript.api.javafx.beans.FXBeanInfo;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// BEGIN: com.dukescript.javafx.fxbeaninfo.HTMLController
public class HTMLController implements FXBeanInfo.Provider {
    private final StringProperty labelText =
            new SimpleStringProperty(this, "labelText", "");
    private final Property<EventHandler<ActionEvent>> action =
            new SimpleObjectProperty<EventHandler<ActionEvent>>(
                    this, "action",
                    (e) -> labelText.set("Hello World!"));
    
    private final FXBeanInfo info = FXBeanInfo
            .newBuilder(this)
            .action(action)
            .property(labelText)
            .build();
    
    @Override
    public FXBeanInfo getFXBeanInfo() {
        return info;
    }
}
// END: com.dukescript.javafx.fxbeaninfo.HTMLController
