package javafx.webui;

import java.util.Map;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.event.EventHandler;
import net.java.html.BrwsrCtx;
import org.netbeans.html.json.spi.Proto;

final class FXHtml4Java extends Proto.Type<FXBeanInfo.Provider> {
    private static final ClassValue<FXHtml4Java[]> TYPES = new ClassValue<FXHtml4Java[]>() {
        @Override
        protected FXHtml4Java[] computeValue(Class<?> type) {
            return new FXHtml4Java[1];
        }
    };

    static Proto findProto(FXBeanInfo info) {
        Class<?> clazz = info.getBean().getClass();
        FXHtml4Java[] data = TYPES.get(clazz);
        if (data[0] == null) {
            Class<? extends FXBeanInfo.Provider> pClass = clazz.asSubclass(FXBeanInfo.Provider.class);
            data[0] = new FXHtml4Java(pClass, info);
        }
        BrwsrCtx ctx = BrwsrCtx.findDefault(clazz);
        return data[0].createProto(info.getBean(), ctx);
    }

    FXHtml4Java(Class<? extends FXBeanInfo.Provider> clazz, FXBeanInfo info) {
        super(clazz, clazz, info.getProperties().size(), info.getFunctions().size());

        int index = 0;
        for (Map.Entry<String, ObservableValue<?>> e : info.getProperties().entrySet()) {
            String name = e.getKey();
            ObservableValue<?> p = e.getValue();
            boolean readOnly = !(p instanceof WritableValue);
            boolean constant = p instanceof ConstantValue;
            registerProperty(name, index++, readOnly, constant);
        }
        index = 0;
        for (Map.Entry<String, ReadOnlyProperty<? extends EventHandler<? super ActionDataEvent>>> e : info.getFunctions().entrySet()) {
            String name = e.getKey();
            registerFunction(name, index++);
        }
    }

    @Override
    protected void setValue(FXBeanInfo.Provider model, int i, Object o) {
        ObservableValue p = model.getFXBeanInfo().props.get(i);
        if (p instanceof Property) {
            ((Property) p).setValue(o);
        }
    }

    @Override
    protected Object getValue(FXBeanInfo.Provider model, int i) {
        ObservableValue p = model.getFXBeanInfo().props.get(i);
        return p.getValue();
    }

    @Override
    protected void call(FXBeanInfo.Provider model, int i, Object o, Object o1) throws Exception {
        ReadOnlyProperty<? extends EventHandler<? super ActionDataEvent>> p = model.getFXBeanInfo().funcs.get(i);
        EventHandler<? super ActionDataEvent> fn = p.getValue();
        if (fn != null) {
            fn.handle(new ActionDataEvent(this, model, o, o1));
        }
    }

    @Override
    protected FXBeanInfo.Provider cloneTo(FXBeanInfo.Provider model, BrwsrCtx bc) {
        throw new UnsupportedOperationException("cloneTo");
    }

    @Override
    protected FXBeanInfo.Provider read(BrwsrCtx bc, Object o) {
        throw new UnsupportedOperationException("read");
    }

    @Override
    protected void onChange(FXBeanInfo.Provider model, int i) {
        throw new UnsupportedOperationException("onChange");
    }

    @Override
    protected Proto protoFor(Object o) {
        if (o instanceof FXBeanInfo.Provider) {
            FXBeanInfo.Provider p = (FXBeanInfo.Provider) o;
            return p.getFXBeanInfo().proto;
        }
        return null;
    }

}
