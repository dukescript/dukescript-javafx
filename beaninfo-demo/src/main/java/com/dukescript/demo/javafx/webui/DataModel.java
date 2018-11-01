package com.dukescript.demo.javafx.webui;

/*-
 * #%L
 * DukeScript JavaFX Extensions - a library from the "DukeScript" project.
 * %%
 * Copyright (C) 2018 Dukehoff GmbH
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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import com.dukescript.api.javafx.beans.ActionDataEvent;
import com.dukescript.api.javafx.beans.FXBeanInfo;
import net.java.html.json.Models;

final class DataModel implements FXBeanInfo.Provider {
    final ObjectProperty<String> message = new SimpleObjectProperty<>(this, "message");
    final ObservableValue<List<String>> words = Bindings.createObjectBinding(() -> {
        return words(message.get());
    }, message);
    final ListProperty<HistoryElement> history = new SimpleListProperty<>(this, "history", FXCollections.observableArrayList());
    final BooleanProperty rotating = new SimpleBooleanProperty(this, "rotating");
    final Property<EventHandler<Event>> turnAnimationOn = new SimpleObjectProperty<>(this, "turnAnimationOn");
    final Property<EventHandler<ActionEvent>> turnAnimationOff = new SimpleObjectProperty<>(this, "turnAnimationOff");
    final Property<EventHandler<ActionEvent>> rotate5s = new SimpleObjectProperty<>(this, "rotate5s");
    final Property<EventHandler<ActionEvent>> showScreenSize = new SimpleObjectProperty<>(this, "showScreenSize");
    final Property<EventHandler<ActionDataEvent>> removeFromHistory = new SimpleObjectProperty<>(this, "removeFromHistory");
    final FXBeanInfo info = FXBeanInfo.newBuilder(this).
            property(message).
            property(rotating).
            property("words", words).
            property(history).
            action(turnAnimationOn).
            action(turnAnimationOff).
            action(rotate5s).
            action(showScreenSize).
            action(removeFromHistory).
            build();

    public DataModel() {
        message.addListener((observable, oldValue, newValue) -> {
            history.add(new HistoryElement(newValue));
        });
        turnAnimationOn.setValue((event) -> rotating.set(true));
        turnAnimationOff.setValue((event) -> rotating.set(false));
        rotate5s.setValue((event) -> {
            rotating.set(true);
            java.util.Timer timer = new java.util.Timer("Rotates a while");
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    rotating.set(false);
                }
            }, 5000);
        });
        showScreenSize.setValue((event) -> message.set("Screen size is unknown"));
        removeFromHistory.setValue((event) -> {
            HistoryElement h = event.getSource(HistoryElement.class);
            history.remove(h);
            if (Objects.equals(h.message, message.get())) {
                message.set("Message has been removed from history");
            }
        });
    }

    @Override
    public FXBeanInfo getFXBeanInfo() {
        return info;
    }

    static List<String> words(String message) {
        String[] arr = new String[6];
        String[] words = message == null ? new String[0] : message.split(" ", 6);
        for (int i = 0; i < 6; i++) {
            arr[i] = words.length > i ? words[i] : "!";
        }
        return Arrays.asList(arr);
    }

    /**
     * Called when the page is ready.
     */
    static void onPageLoad() {
        DataModel ui = new DataModel();
        ui.message.set("Hello World from HTML and Java!");
        Models.applyBindings(ui);
    }

    private static final class HistoryElement implements FXBeanInfo.Provider {
        final String message;
        final FXBeanInfo info;

        HistoryElement(String message) {
            this.message = message;
            this.info = FXBeanInfo.newBuilder(this).
                constant("message", message).
                build();
        }

        @Override
        public FXBeanInfo getFXBeanInfo() {
            return info;
        }
    }
}
