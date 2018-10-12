package com.dukescript.impl.javafx.beans;

import com.dukescript.api.javafx.beans.ActionDataEvent;
import com.dukescript.api.javafx.beans.FXBeanInfo;

public abstract class ActionDataEventFactory {
    private static ActionDataEventFactory INSTANCE;
    static {
        try {
            Class<?> c = Class.forName(ActionDataEvent.class.getName(), true, ActionDataEvent.class.getClassLoader());
            assert c == ActionDataEvent.class;
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
        if (INSTANCE == null) {
            throw new IllegalStateException();
        }
    }

    protected ActionDataEventFactory() {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        }
        INSTANCE = this;
    }
    protected abstract ActionDataEvent create(Convertor owner, FXBeanInfo.Provider model, Object source, Object event);

    public static ActionDataEvent newEvent(Convertor owner, FXBeanInfo.Provider model, Object source, Object event) {
        return INSTANCE.create(owner, model, source, event);
    }

    public static interface Convertor {
        public Object toString(FXBeanInfo fxBeanInfo, Object event, String name);

        public Object toNumber(FXBeanInfo fxBeanInfo, Object event, String name);

        public <T> Object toModel(FXBeanInfo fxBeanInfo, Class<T> as, Object d);

        public <T> T extractValue(Class<T> as, Object obj);
    }
}
