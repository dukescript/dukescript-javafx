/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dukescript.technology.fxml;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.event.EventHandler;
import org.netbeans.html.context.spi.Contexts;
import org.netbeans.html.json.spi.FunctionBinding;
import org.netbeans.html.json.spi.PropertyBinding;
import org.netbeans.html.json.spi.Technology;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author antonepple
 */
public class FXTechnology implements Technology.BatchInit<FXTechnology.OMap> {

    @Override
    public OMap wrapModel(Object model, PropertyBinding[] propArr, FunctionBinding[] funcArr) {
        return new OMap(model, propArr, funcArr);
    }

    @Override
    public OMap wrapModel(Object model) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <M> M toModel(Class<M> modelClass, Object data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void bind(PropertyBinding b, Object model, FXTechnology.OMap data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void valueHasMutated(OMap data, String propertyName) {
        data.fire(propertyName);
    }

    @Override
    public void expose(FunctionBinding fb, Object model, OMap d) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void applyBindings(OMap data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object wrapArray(Object[] arr) {
        return FXCollections.observableArrayList(arr);
    }

    @Override
    public void runSafe(Runnable r) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class OMap extends HashMap<String, Object> implements ObservableMap<String, Object> {

        private List<MapChangeListener<? super String, Object>> listeners = new CopyOnWriteArrayList<>();
        private PropertyBinding[] propArr;

        public OMap() {
        }

        private OMap(Object model, PropertyBinding[] propArr, FunctionBinding[] funcArr) {
            this.propArr = propArr;
            for (PropertyBinding prop : propArr) {
                this.put(prop.getPropertyName(), prop.getValue());
            }
            for (final FunctionBinding func : funcArr) {
                this.put(func.getFunctionName(), new EventHandler<Event>() {

                    @Override
                    public void handle(Event event) {
                        func.call(this, event);
                    }
                });
            }
        }

        private void fire(final String propertyName) {
            for (PropertyBinding prop : propArr) {
                this.put(prop.getPropertyName(), prop.getValue());
            }
            for (MapChangeListener<? super String, Object> listener : listeners) {

                listener.onChanged(new MapChangeListener.Change<String, Object>(this) {

                    @Override
                    public boolean wasAdded() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public boolean wasRemoved() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public String getKey() {
                        return propertyName;
                    }

                    @Override
                    public Object getValueAdded() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }

                    @Override
                    public Object getValueRemoved() {
                        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
                });
            }
        }

        @Override
        public void addListener(MapChangeListener<? super String, ? super Object> listener) {
            this.listeners.add(listener);
        }

        @Override
        public void removeListener(MapChangeListener<? super String, ? super Object> listener) {
            listeners.remove(listener);
        }

        @Override
        public void addListener(InvalidationListener listener) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void removeListener(InvalidationListener listener) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @ServiceProvider(service = Contexts.Provider.class)
    public static class Bla implements Contexts.Provider {

        @Override
        public void fillContext(Contexts.Builder context, Class<?> requestor) {
            context.register(Technology.class, new FXTechnology(), 10);
        }

    }
}
