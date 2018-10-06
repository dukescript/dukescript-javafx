package javafx.webui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import org.netbeans.html.json.spi.Proto;

public final class FXBeanInfo {
    private final Object bean;
    private final ChangeListener listener;
    final Proto proto;
    private final Map<String, ObservableValue<?>> properties;
    private final Map<String, ReadOnlyProperty<EventHandler<ActionEvent>>> functions;
    final List<ObservableValue<?>> props;
    final List<ReadOnlyProperty<EventHandler<ActionEvent>>> funcs;

    private FXBeanInfo(Object bean, Map<String, ObservableValue<?>> properties, Map<String, ReadOnlyProperty<EventHandler<ActionEvent>>> functions) {
        this.bean = bean;
        this.properties = Collections.unmodifiableMap(properties);
        this.functions = Collections.unmodifiableMap(functions);
        this.props = new ArrayList<>(this.properties.values());
        this.funcs = new ArrayList<>(this.functions.values());
        this.proto = FXHtml4Java.findProto(this);
        this.listener = (observable, oldValue, newValue) -> {
            for (Map.Entry<String, ObservableValue<?>> entry : properties.entrySet()) {
                if (entry.getValue() == observable) {
                    proto.valueHasMutated(entry.getKey(), oldValue, newValue);
                }
            }
        };
        WeakChangeListener weakListener = new WeakChangeListener<>(listener);
        for (ObservableValue<?> ov : this.props) {
            ov.addListener(weakListener);
        }
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
