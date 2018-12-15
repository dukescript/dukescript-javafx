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
import java.util.Objects;
import java.util.ServiceLoader;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;

/** *  Data with information about {@linkplain #getProperties() properties}
 * and {@link #getActions() actions} of a JavaFX bean. Beans supporting this
 * protocol should implement {@link Provider} - use its {@link Provider#getFXBeanInfo()}
 * method to obtain instance of the info for given bean.
 * <p>
 * If you have a bean willing to provide the info, use {@link FXBeanInfo#newBuilder(java.lang.Object)}
 * and {@link Builder} to create it. Then return it from your
 * {@link Provider#getFXBeanInfo()} method. Here is an example defining
 * one string property and one action:
 *
 * {@codesnippet com.dukescript.javafx.fxbeaninfo.HTMLController}
 */
public final class FXBeanInfo {
    private final Object bean;
    private final Object extra;
    private final Map<String, ObservableValue<?>> properties;
    private final Map<String, EventHandlerProperty> functions;

    private FXBeanInfo(
        Object bean,
        Map<String, ObservableValue<?>> properties,
        Map<String, EventHandlerProperty> functions
    ) {
        this.bean = bean;
        this.properties = properties == null ? Collections.emptyMap() : Collections.unmodifiableMap(properties);
        this.functions = functions == null ? Collections.emptyMap() : Collections.unmodifiableMap(functions);
        List<Object> data = new ArrayList<>();
        if (bean!=null){
            for (FXBeanInfoProvider<?> p : ServiceLoader.load(FXBeanInfoProvider.class)) {
                Object info = p.findExtraInfo(this);
                if (info != null) {
                    data.add(info);
                }
            }
        }

        this.extra = data.isEmpty() ? null : data.size() == 1 ? data.get(0) : data.toArray();
    }

    /** The associated bean.
     *
     * @return object this info {@link #newBuilder(java.lang.Object) was created} for
     */
    public Object getBean() {
        return bean;
    }

    /** Properties of {@linkplain #getBean() this bean}.
     *
     * @return immutable map of available properties
     */
    public Map<String, ObservableValue<?>> getProperties() {
        return properties;
    }

    /**
     * @deprecated use {@link #getActions()}
     */
    @Deprecated
    public Map<String, EventHandlerProperty> getFunctions() {
        return functions;
    }

    /** Invocable handlers for {@linkplain #getBean() this bean}.
     *
     * @return immutable map of available {@link EventHandler event handlers}
     * @since 0.3
     */
    public Map<String, EventHandlerProperty> getActions() {
        return functions;
    }

    /** Extra information associated with this bean info.
     *
     * @param <T> type of the information
     * @param type class describing the requested type
     * @return {code null} or an instance of requested type
     * @see FXBeanInfoProvider
     */
    public <T> T lookup(Class<T> type) {
        if (type.isArray()) {
            return null;
        }
        if (type.isInstance(extra)) {
            return type.cast(extra);
        }
        if (extra instanceof Object[]) {
            for (Object e : ((Object[]) extra)) {
                if (type.isInstance(e)) {
                    return type.cast(e);
                }
            }
        }
        return null;
    }

    /** *  Single method interface for JavaFX beans that can provide {@link FXBeanInfo}.
     * Use {@link #newBuilder(java.lang.Object)} and {@link Builder#build()} to
     * create an instance of your info in your bean constructor and assign
     * it to a {@code final} field. Then return it from the {@link #getFXBeanInfo()}
     * method.
     */
    public static interface Provider {
        /** Provides the info about {@code this} bean. Return always the same
         * value of the info for the same bean.
         *
         * @return constant info describing {@code this} bean
         */
        FXBeanInfo getFXBeanInfo();
    }

    /**
     * Start creating new {@link FXBeanInfo} for provided {@code bean}.
     * Use {@link Builder} methods like {@link Builder#property(javafx.beans.property.ReadOnlyProperty)}
     * or {@link Builder#action(javafx.beans.property.ReadOnlyProperty)} followed
     * by final {@link Builder#build()} to create the info.
     *
     * @param bean the bean to create the info for
     * @return builder to create the info with
     * @since 0.3
     */
    public static Builder newBuilder(Object bean) {
        return new FXBeanInfo(null, null, null).new Builder(bean);
    }

    /**
     * @deprecated use {@link #newBuilder(java.lang.Object)}
     */
    @Deprecated
    public static Builder create(Object bean) {
        return newBuilder(bean);
    }

    /**
     * Builder (obtained via {@link #newBuilder(java.lang.Object)} method) to
     * create a description of a JavaFX bean and represent it as
     * {@link FXBeanInfo}.
     */
    public final class Builder {
        private Object bean;
        private Map<String, ObservableValue<?>> properties;
        private Map<String, EventHandlerProperty> functions;

        Builder(Object bean) {
            this.bean = bean;
        }

        /** Registers another property into be builder.
         *
         * @param p the property
         * @return {@code this}
         */
        public Builder property(ReadOnlyProperty<?> p) {
            return property(p.getName(), p);
        }

        /** Registers another property/value with provided name into the
         * builder.
         *
         * @param name non-null name of the property
         * @param p the observable value
         * @return {@code this}
         */
        public Builder property(String name, ObservableValue<?> p) {
            Objects.requireNonNull(p, "Property must have a name: " + p);
            if (this.properties == null) {
                this.properties = new LinkedHashMap<>();
            }
            this.properties.put(name, p);
            return this;
        }

        /** Registers a property with a constant value.
         *
         * @param <T> type of the value
         * @param name non-null name of the property
         * @param value the constant value of the property
         * @return {@code this}
         */
        public <T> Builder constant(String name, T value) {
            return property(name, new ConstantValue<T>(value));
        }

        /** Registers new property.
         *
         * @param name name of the property
         * @param handler parameter-less handler
         * @since 0.4
         * @return
         */
        public Builder action(String name, Runnable handler) {
            SimpleEventHandlerProperty prop = new SimpleEventHandlerProperty(bean, name, new EventHandler<ActionDataEvent>() {
                @Override
                public void handle(ActionDataEvent t) {
                    handler.run();
                }
            });
            return action(prop);
        }

        public Builder action(String name, EventHandler<? super ActionDataEvent> handler) {
            SimpleEventHandlerProperty prop = new SimpleEventHandlerProperty(bean, name, handler);
            return action(prop);
        }

        public Builder action(ReadOnlyProperty<? extends EventHandler<? super ActionDataEvent>> p) {
            EventHandlerProperty ehp;
            if (p instanceof EventHandlerProperty) {
                ehp = (EventHandlerProperty) p;
            } else {
                ehp = new DelegateEventHandlerProperty(p);
            }
            if (this.functions == null) {
                this.functions = new LinkedHashMap<>();
            }
            final String name = p.getName();
            if (name == null) {
                throw new NullPointerException("No name for " + p);
            }
            EventHandlerProperty prev = this.functions.put(name, ehp);
            if (prev != null) {
                this.functions.put(name, prev);
                throw new IllegalArgumentException("Cannot redefine " + prev + " with " + p);
            }
            return this;
        }

        /**
         * Builds {@link FXBeanInfo} from provided properties and actions.
         * It clears the so far specified values.
         * @return the JavaFX bean info
         */
        public FXBeanInfo build() {
            FXBeanInfo info = new FXBeanInfo(bean, this.properties, this.functions);
            this.properties = null;
            this.functions = null;
            return info;
        }
    }
}
