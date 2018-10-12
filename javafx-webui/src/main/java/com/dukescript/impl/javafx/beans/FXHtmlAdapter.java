package com.dukescript.impl.javafx.beans;

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
        this.funcs = new ArrayList<>(info.getFunctions().values());
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
