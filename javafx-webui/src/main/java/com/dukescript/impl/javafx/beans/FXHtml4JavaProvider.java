package com.dukescript.impl.javafx.beans;

import com.dukescript.api.javafx.beans.FXBeanInfo;
import com.dukescript.spi.javafx.beans.FXBeanInfoProvider;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = FXBeanInfoProvider.class)
public final class FXHtml4JavaProvider extends FXBeanInfoProvider<FXHtmlAdapter> {
    public FXHtml4JavaProvider() {
        super(FXHtmlAdapter.class);
    }

    @Override
    public FXHtmlAdapter findExtraInfo(FXBeanInfo info) {
        return new FXHtmlAdapter(info);
    }
}
