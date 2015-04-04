package com.dukescript.technology.fxml;

import org.netbeans.html.context.spi.Contexts;
import org.netbeans.html.json.spi.Technology;
import org.openide.util.lookup.ServiceProvider;

/**
 */
@ServiceProvider(service = Contexts.Provider.class)
public class FXTechnologyProvider implements Contexts.Provider {

    @Override
    public void fillContext(Contexts.Builder context, Class<?> requestor) {
        context.register(Technology.class, new FXTechnology(), 10);
    }
    
}
