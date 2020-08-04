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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
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
        this.properties = properties == null ? Collections.<String, ObservableValue<?>>emptyMap() : Collections.unmodifiableMap(properties);
        this.functions = functions == null ? Collections.<String, EventHandlerProperty>emptyMap() : Collections.unmodifiableMap(functions);
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

    /** Properties of {@linkplain #getBean() this bean}. Properties can
     * depend on each other via so called {@link Bindings}:
     *
     * {@codesnippet com.dukescript.api.javafx.beans.BindingsTest#multiplyBean}
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

    /** {@link EventHandler} properties for {@linkplain #getBean() this bean}.
     * Use following code to invoke them:
     * <p>
     * {@codesnippet com.dukescript.javafx.tests.BeanInfoCheck#invokeEventHandlerProperty}
     *
     * @return immutable map of available {@link EventHandler event handlers}
     * @since 0.4
     * @see ActionDataEvent
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

    /**
     * Single method interface for JavaFX beans that can provide {@link FXBeanInfo}.
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
     * Generate {@link Provider} and its {@link FXBeanInfo} rather than
     * {@linkplain Builder constructing it manually}. Classical way of
     * creating {@link FXBeanInfo} is to use the {@link Builder} and
     * manually specify each {@link Builder#property property}
     * and {@link Builder#action action} attributes.
     *
     * <h5>Generating {@link FXBeanInfo}</h5>
     *
     * Sometimes that is a tedious work that can be automated
     * by using the {@link Generate} annotation. Annotate your bean class
     * with this annotation and make it extend non-existing(<b>!</b>) super
     * class. That class is going to be automatically generated during compilation.
     * It will implement {@link Provider} and return a {@link FXBeanInfo}
     * based on the fields and methods of your class.
     * <p>
     * {@codesnippet IntrospectedBeanTest.Empty}
     *
     * <h5>Properties</h5>
     *
     * JavaFX properties are represented by various classes that implement
     * {@link ReadOnlyProperty} interface. All non-private and {@code final} fields
     * of your class are going to be registered. Derived properties are registered
     * as bindings providing an {@link ObservableValue}. Constant properties
     * are simple fields like {@code int}, {@code double} or {@link String}.
     * <p>
     *
     * {@codesnippet IntrospectedBeanTest.Properties}
     *
     * All fields have to be {@code final} and must not be {@code private}!
     *
     * <h5>Actions</h5>
     *
     * Callback actions are defined as non-private instance methods that return
     * {@link void} and accept no arguments or a single {@link ActionEvent}
     * or {@link ActionDataEvent} argument.
     * <p>
     * {@codesnippet IntrospectedBeanTest.Actions}
     *
     * Avoid overloaded methods. Only one name can be used for an action
     * and property per bean class.
     *
     * @since 0.6
     */
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.TYPE)
    public static @interface Generate {
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
        private final Object bean;
        private Map<String, ObservableValue<?>> properties;
        private Map<String, EventHandlerProperty> functions;

        Builder(Object bean) {
            this.bean = bean;
        }

        /** Registers a property into the builder.
         *
         * @param p the property
         * @return {@code this} builder
         */
        public Builder property(ReadOnlyProperty<?> p) {
            return property(p.getName(), p);
        }

        /** Registers a property/value with provided name into the
         * builder.
         *
         * @param name non-null name of the property
         * @param p the observable value
         * @return {@code this} builder
         */
        public Builder property(String name, ObservableValue<?> p) {
            Objects.requireNonNull(name, "Property must have a name: " + p);
            if (p instanceof EventHandlerProperty) {
                return action(name, (EventHandlerProperty) p);
            }
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
         * @return {@code this} builder
         */
        public <T> Builder constant(String name, T value) {
            return property(name, new ConstantValue<>(value));
        }

        /** Registers parameter-less action. Works well with method
         * references to parameter-less methods. Following example defines
         * one such action:
         * <p>
         * {@codesnippet com.dukescript.javafx.tests.BeanInfoCheck#CountingBean}
         *
         * @param name name of the property
         * @param handler parameter-less handler
         * @since 0.4
         * @return {@code this} builder
         */
        public Builder action(String name, final Runnable handler) {
            SimpleEventHandlerProperty prop = new SimpleEventHandlerProperty(bean, name, new EventHandler<ActionDataEvent>() {
                @Override
                public void handle(ActionDataEvent t) {
                    handler.run();
                }
            });
            return action(prop);
        }

        /** Registers action with event parameter. Works well with
         * references to methods that accept {@link ActionEvent} or
         * {@link ActionDataEvent} parameter. Following example defines
         * such actions:
         * <p>
         * {@codesnippet com.dukescript.javafx.tests.BeanInfoCheck#CountingBean}
         *
         * @param name name of the property
         * @param handler parameter-less handler
         * @since 0.4
         * @return {@code this} builder
         */
        public Builder action(String name, EventHandler<? super ActionDataEvent> handler) {
            SimpleEventHandlerProperty prop = new SimpleEventHandlerProperty(bean, name, handler);
            return action(prop);
        }

        /** @deprecated to be removed, use {@link #action(com.dukescript.api.javafx.beans.EventHandlerProperty)}
         * @deprecated
         */
        @Deprecated
        public Builder action(ReadOnlyProperty<? extends EventHandler<? super ActionDataEvent>> p) {
            System.err.println("this method will be removed: " + p);
            EventHandlerProperty ehp;
            if (p instanceof EventHandlerProperty) {
                ehp = (EventHandlerProperty) p;
            } else {
                ehp = new DelegateEventHandlerProperty(p);
            }
            return action(ehp);
        }

        /** Generic way to register an action property.
         * Actions are defined as {@link EventHandlerProperty}
         * instances. This method allows direct registration of such property. However,
         * rather than using this method directly, consider registering the
         * actions via method references:
         * <p>
         * {@codesnippet com.dukescript.javafx.tests.BeanInfoCheck#CountingBean}
         * <p>
         * In case manipulation with actual {@link ObjectProperty} is needed, here
         * is an example showing a path through all the generic signatures:
         * <p>
         * {@codesnippet com.dukescript.api.javafx.beans.EventHandlerPropertyTest#CountingBean}
         *
         * @param p instance of {@link EventHandlerProperty} to register
         * @return {@code this} builder
         * @since 0.4
         */
        public Builder action(EventHandlerProperty p) {
            return action(p.getName(), p);
        }

        private Builder action(String name, EventHandlerProperty p) {
            if (this.functions == null) {
                this.functions = new LinkedHashMap<>();
            }
            if (name == null) {
                throw new NullPointerException("No name for " + p);
            }
            EventHandlerProperty prev = this.functions.put(name, p);
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
