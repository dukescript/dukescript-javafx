package javafx.webui;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public final class FXBeanInfo {
    private final Object bean;
    private final Map<String, ObservableValue<?>> properties;
    private final Map<String, ReadOnlyProperty<EventHandler<ActionEvent>>> functions;

    private FXBeanInfo(Object bean, Map<String, ObservableValue<?>> properties, Map<String, ReadOnlyProperty<EventHandler<ActionEvent>>> functions) {
        this.bean = bean;
        this.properties = Collections.unmodifiableMap(properties);
        this.functions = Collections.unmodifiableMap(functions);
    }

    public Object getBean() {
        return bean;
    }

    public Map<String, ObservableValue<?>> getProperties() {
        return properties;
    }

    public Map<String, ReadOnlyProperty<EventHandler<ActionEvent>>> getFunctions() {
        return functions;
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
        private Map<String, ReadOnlyProperty<EventHandler<ActionEvent>>> functions;

        Builder(Object bean) {
            this.bean = bean;
        }

        public Builder property(String name, ObservableValue<?> p) {
            if (this.properties == null) {
                this.properties = new LinkedHashMap<>();
            }
            this.properties.put(name, p);
            return this;
        }

        public Builder action(String name, ReadOnlyProperty<EventHandler<ActionEvent>> p) {
            if (this.functions == null) {
                this.functions = new LinkedHashMap<>();
            }
            this.functions.put(name, p);
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
