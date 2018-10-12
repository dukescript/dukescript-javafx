package com.dukescript.api.javafx.beans;

import com.dukescript.impl.javafx.beans.ActionDataEventFactory;
import javafx.event.ActionEvent;
import net.java.html.json.Models;

public final class ActionDataEvent extends ActionEvent {
    private static final ActionDataEventFactory FACTORY = new ActionDataEventFactory() {
        @Override
        public ActionDataEvent create(ActionDataEventFactory.Convertor owner, FXBeanInfo.Provider model, Object source, Object event) {
            return new ActionDataEvent(owner, model, source, source);
        }
    };

    private final FXBeanInfo.Provider model;
    private final ActionDataEventFactory.Convertor conv;
    private final Object event;

    ActionDataEvent(ActionDataEventFactory.Convertor conv, FXBeanInfo.Provider model, Object source, Object event) {
        super(source, null);
        this.model = model;
        this.conv = conv;
        this.event = event;
    }

    public <T> T getProperty(Class<T> as, String name) {
        Object value;
        if (as == String.class) {
            value = conv.toString(model.getFXBeanInfo(), event, name);
        } else if (Number.class.isAssignableFrom(as)) {
            value = conv.toNumber(model.getFXBeanInfo(), event, name);
        } else {
            value = null;
        }
        return extractValue(as, value);
    }

    public <T> T getSource(Class<T> as) {
        return extractValue(as, getSource());
    }

    private <T> T extractValue(Class<T> as, Object d) {
        Object obj;
        if (Models.isModel(as)) {
            obj = conv.toModel(model.getFXBeanInfo(), as, d);
        } else {
            obj = d;
        }
        return conv.extractValue(as, obj);
    }
}
