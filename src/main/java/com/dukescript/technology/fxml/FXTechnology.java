package com.dukescript.technology.fxml;

/*
 * #%L
 * DukeScript and FXML UI
 * %%
 * Copyright (C) 2015 Eppleton IT Consulting
 * %%
 * COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Common Development and Distrubtion License as
 * published by the Sun Microsystems, either version 1.0 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the Common Development and Distrubtion
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-1.0.html>.
 * #L%
 */

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

/**
 *
 * @author antonepple
 */
@Contexts.Id("fxml")
final class FXTechnology implements Technology.BatchInit<Object> {

    @Override
    public OMap wrapModel(Object model, PropertyBinding[] propArr, FunctionBinding[] funcArr) {
        return new OMap(model, propArr, funcArr);
    }

    @Override
    public OMap wrapModel(Object model) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <M> M toModel(Class<M> modelClass, Object data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bind(PropertyBinding b, Object model, Object data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void valueHasMutated(Object data, String propertyName) {
        ((OMap)data).fire(propertyName);
    }

    @Override
    public void expose(FunctionBinding fb, Object model, Object d) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void applyBindings(Object data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object wrapArray(Object[] arr) {
        return FXCollections.observableArrayList(arr);
    }

    @Override
    public void runSafe(Runnable r) {
        throw new UnsupportedOperationException();
    }

    private static class OMap extends HashMap<String, Object> implements ObservableMap<String, Object> {

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
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public boolean wasRemoved() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public String getKey() {
                        return propertyName;
                    }

                    @Override
                    public Object getValueAdded() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public Object getValueRemoved() {
                        throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeListener(InvalidationListener listener) {
            throw new UnsupportedOperationException();
        }
    }

}
