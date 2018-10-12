package com.dukescript.spi.javafx.beans;

import com.dukescript.api.javafx.beans.FXBeanInfo;

public abstract class FXBeanInfoProvider<T> {
    private final Class<T> type;

    protected FXBeanInfoProvider(Class<T> type) {
        this.type = type;
    }

    public abstract T findExtraInfo(FXBeanInfo info);
}
