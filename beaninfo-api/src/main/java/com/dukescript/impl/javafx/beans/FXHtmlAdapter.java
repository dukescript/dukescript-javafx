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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.event.EventHandler;
import org.netbeans.html.json.spi.Proto;

final class FXHtmlAdapter implements ChangeListener<Object> {
    private final WeakChangeListener listener;
    private final Map<String, ObservableValue<?>> properties;
    final List<ObservableValue<?>> props;
    final List<ReadOnlyProperty<? extends EventHandler<? super ActionDataEvent>>> funcs;
    final Proto proto;

    FXHtmlAdapter(FXBeanInfo info) {
        this.properties = info.getProperties();
        this.props = new ArrayList<>(this.properties.values());
        this.funcs = new ArrayList<ReadOnlyProperty<? extends EventHandler<? super ActionDataEvent>>>(info.getActions().values());
        this.listener = new WeakChangeListener<>(this);
        for (ObservableValue<?> ov : this.properties.values()) {
            ov.addListener(listener);
        }
        this.proto = FXHtml4Java.findProto(info);
    }

    @Override
    public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
        for (Map.Entry<String, ObservableValue<?>> entry : properties.entrySet()) {
            if (entry.getValue() == observable) {
                proto.valueHasMutated(entry.getKey(), oldValue, newValue);
            }
        }
    }

}
