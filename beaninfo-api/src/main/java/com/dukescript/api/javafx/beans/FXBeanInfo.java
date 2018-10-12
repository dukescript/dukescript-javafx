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

import com.dukescript.impl.javafx.beans.ConstantValue;
import com.dukescript.spi.javafx.beans.FXBeanInfoProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;

public final class FXBeanInfo {
    private final Object bean;
    private final Object extra;
    private final Map<String, ObservableValue<?>> properties;
    private final Map<String, ReadOnlyProperty<? extends EventHandler<? super ActionDataEvent>>> functions;

    private FXBeanInfo(
        Object bean,
        Map<String, ObservableValue<?>> properties,
        Map<String, ReadOnlyProperty<? extends EventHandler<? super ActionDataEvent>>> functions
    ) {
        this.bean = bean;
        this.properties = properties == null ? Collections.emptyMap() : Collections.unmodifiableMap(properties);
        this.functions = functions == null ? Collections.emptyMap() : Collections.unmodifiableMap(functions);

        List<Object> data = new ArrayList<>();
        for (FXBeanInfoProvider<?> p : ServiceLoader.load(FXBeanInfoProvider.class)) {
            Object info = p.findExtraInfo(this);
            if (info != null) {
                data.add(info);
            }
        }

        this.extra = data.isEmpty() ? null : data.size() == 1 ? data.get(0) : data.toArray();
    }

    public Object getBean() {
        return bean;
    }

    public Map<String, ObservableValue<?>> getProperties() {
        return properties;
    }

    public Map<String, ReadOnlyProperty<? extends EventHandler<? super ActionDataEvent>>> getFunctions() {
        return functions;
    }

    public <T> T lookup(Class<T> extraInfo) {
        if (extraInfo.isArray()) {
            return null;
        }
        if (extraInfo.isInstance(extra)) {
            return extraInfo.cast(extra);
        }
        if (extra instanceof Object[]) {
            for (Object e : ((Object[]) extra)) {
                if (extraInfo.isInstance(e)) {
                    return extraInfo.cast(e);
                }
            }
        }
        return null;
    }

    public static interface Provider {
        FXBeanInfo getFXBeanInfo();
    }

    public static Builder create(Object bean) {
        return new Builder(bean);
    }

    public static final class Builder {
        private Object bean;
        private Map<String, ObservableValue<?>> properties;
        private Map<String, ReadOnlyProperty<? extends EventHandler<? super ActionDataEvent>>> functions;

        Builder(Object bean) {
            this.bean = bean;
        }

        public Builder property(ReadOnlyProperty<?> p) {
            return property(p.getName(), p);
        }

        public Builder property(String name, ObservableValue<?> p) {
            if (this.properties == null) {
                this.properties = new LinkedHashMap<>();
            }
            this.properties.put(name, p);
            return this;
        }

        public <T> Builder constant(String name, T value) {
            return property(name, new ConstantValue<T>(value));
        }

        public Builder action(ReadOnlyProperty<? extends EventHandler<? super ActionDataEvent>> p) {
            if (this.functions == null) {
                this.functions = new LinkedHashMap<>();
            }
            final String name = p.getName();
            if (name == null) {
                throw new NullPointerException("No name for " + p);
            }
            ReadOnlyProperty<? extends EventHandler<? super ActionDataEvent>> prev = this.functions.put(name, p);
            if (prev != null) {
                this.functions.put(name, prev);
                throw new IllegalArgumentException("Cannot redefine " + prev + " with " + p);
            }
            return this;
        }

        public FXBeanInfo build() {
            FXBeanInfo info = new FXBeanInfo(bean, this.properties, this.functions);
            this.properties = null;
            this.functions = null;
            return info;
        }
    }
}
