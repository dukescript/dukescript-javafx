package javafx.webui;

import javafx.event.ActionEvent;
import net.java.html.json.Models;

public final class ActionDataEvent extends ActionEvent {
    private final FXBeanInfo.Provider model;
    private final FXHtml4Java owner;
    private final Object event;

    ActionDataEvent(FXHtml4Java owner, FXBeanInfo.Provider model, Object source, Object event) {
        super(source, null);
        this.model = model;
        this.owner = owner;
        this.event = event;
    }

    public <T> T getProperty(Class<T> as, String name) {
        Object value;
        if (as == String.class) {
            value = model.getFXBeanInfo().proto.toString(event, name);
        } else if (Number.class.isAssignableFrom(as)) {
            value = model.getFXBeanInfo().proto.toNumber(event, name);
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
            obj = model.getFXBeanInfo().proto.toModel(as, d);
        } else {
            obj = d;
        }
        return owner.extractValue(as, obj);
    }
}
