package com.dukescript.technology.fxml;

/*
 * #%L
 * DukeScript and FXML UI
 * %%
 * Copyright (C) 2015 Eppleton IT Consulting
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
