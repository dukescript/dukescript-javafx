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
import com.dukescript.api.javafx.beans.EventHandlerProperty;
import com.dukescript.api.javafx.beans.FXBeanInfo;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.event.EventHandler;
import net.java.html.BrwsrCtx;
import org.netbeans.html.json.spi.Proto;

final class FXHtml4Java extends Proto.Type<FXBeanInfo.Provider> implements ActionDataEventFactory.Convertor {
    private static final Map<Class,FXHtml4Java> TYPES = new HashMap<>();

    synchronized static Proto findProto(FXBeanInfo info, List<ObservableValue<?>> fillValues, List<EventHandlerProperty> fillFunctions) {
        Class<?> clazz = info.getBean().getClass();
        FXHtml4Java data = TYPES.get(clazz);
        if (data == null) {
            Class<? extends FXBeanInfo.Provider> pClass = clazz.asSubclass(FXBeanInfo.Provider.class);
            data = new FXHtml4Java(pClass, info.getProperties().size(), info.getActions().size());
            TYPES.put(clazz, data);
        }
        data.registerProperties(info, fillValues);
        data.registerFunctions(info, fillFunctions);
        BrwsrCtx ctx = BrwsrCtx.findDefault(clazz);
        return data.createProto(info.getBean(), ctx);
    }

    private final Set<String> propertyNames = new LinkedHashSet<>();
    private final Set<String> functionNames = new LinkedHashSet<>();

    FXHtml4Java(Class<? extends FXBeanInfo.Provider> clazz, int initialProps, int initialActions) {
        super(clazz, clazz, initialProps, initialActions);
    }

    private void registerFunctions(FXBeanInfo info, List<EventHandlerProperty> fill) {
        for (Map.Entry<String, EventHandlerProperty> e : info.getActions().entrySet()) {
            String name = e.getKey();
            int currentSize = functionNames.size();
            if (functionNames.add(name)) {
                registerFunction(name, currentSize);
            }
        }
        for (String fn : functionNames) {
            fill.add(info.getActions().get(fn));
        }
    }

    private void registerProperties(FXBeanInfo info, List<ObservableValue<?>> fill) {
        for (Map.Entry<String, ObservableValue<?>> e : info.getProperties().entrySet()) {
            String name = e.getKey();
            int currentSize = propertyNames.size();
            if (propertyNames.add(name)) {
                ObservableValue<?> p = e.getValue();
                boolean readOnly = !(p instanceof WritableValue);
                boolean constant = p instanceof ConstantValue;
                registerProperty(name, currentSize, readOnly, constant);
            }
        }
        for (String pn : propertyNames) {
            fill.add(info.getProperties().get(pn));
        }
    }

    private ObservableValue<?> valueAt(FXBeanInfo.Provider model, int i) {
        FXHtmlAdapter a = model.getFXBeanInfo().lookup(FXHtmlAdapter.class);
        return a == null ? null : a.props.get(i);
    }

    @Override
    protected void setValue(FXBeanInfo.Provider model, int i, Object o) {
        ObservableValue p = valueAt(model, i);
        if (p instanceof Property) {
            ((Property) p).setValue(o);
        }
    }

    @Override
    protected Object getValue(FXBeanInfo.Provider model, int i) {
        ObservableValue p = valueAt(model, i);
        return p.getValue();
    }

    @Override
    protected void call(FXBeanInfo.Provider model, int i, Object o, Object o1) throws Exception {
        ReadOnlyProperty<? extends EventHandler<? super ActionDataEvent>> p;
        FXHtmlAdapter a = model.getFXBeanInfo().lookup(FXHtmlAdapter.class);
        if (a != null) {
            p = a.funcs.get(i);
            EventHandler<? super ActionDataEvent> fn = p.getValue();
            if (fn != null) {
                fn.handle(ActionDataEventFactory.newEvent(this, model, o, o1));
            }
        }
    }

    @Override
    protected FXBeanInfo.Provider cloneTo(FXBeanInfo.Provider model, BrwsrCtx bc) {
        throw new UnsupportedOperationException("cloneTo");
    }

    @Override
    protected FXBeanInfo.Provider read(BrwsrCtx bc, Object o) {
        throw new UnsupportedOperationException("read");
    }

    @Override
    protected void onChange(FXBeanInfo.Provider model, int i) {
        throw new UnsupportedOperationException("onChange");
    }

    @Override
    protected Proto protoFor(Object o) {
        if (o instanceof FXBeanInfo.Provider) {
            FXBeanInfo.Provider p = (FXBeanInfo.Provider) o;
            FXHtmlAdapter a = p.getFXBeanInfo().lookup(FXHtmlAdapter.class);
            if (a != null) {
                return a.proto;
            }
        }
        return null;
    }

    @Override
    public Object toString(FXBeanInfo info, Object event, String name) {
        FXHtmlAdapter a = info.lookup(FXHtmlAdapter.class);
        if (a != null) {
            return a.proto.toString(event, name);
        }
        return null;
    }

    @Override
    public Object toNumber(FXBeanInfo info, Object event, String name) {
        FXHtmlAdapter a = info.lookup(FXHtmlAdapter.class);
        if (a != null) {
            return a.proto.toNumber(event, name);
        }
        return null;
    }

    @Override
    public <T> T toModel(FXBeanInfo info, Class<T> as, Object d) {
        FXHtmlAdapter a = info.lookup(FXHtmlAdapter.class);
        if (a != null) {
            return a.proto.toModel(as, d);
        }
        return null;
    }
}
