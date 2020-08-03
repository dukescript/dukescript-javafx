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
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import com.dukescript.api.javafx.beans.FXBeanInfo;
import com.dukescript.api.javafx.beans.SimpleEventHandlerProperty;
import net.java.html.json.Models;

@FXBeanInfo.Introspect
final class DataModel extends DataModelBeanInfo {
    final ObjectProperty<String> message = new SimpleObjectProperty<>(this, "message");
    final ObservableValue<List<String>> words = Bindings.createObjectBinding(() -> {
        return words(message.get());
    }, message);
    final ListProperty<HistoryElement> history = new SimpleListProperty<>(this, "history", FXCollections.observableArrayList());
    final BooleanProperty rotating = new SimpleBooleanProperty(this, "rotating");
    final SimpleEventHandlerProperty turnAnimationOn = new SimpleEventHandlerProperty(this, "turnAnimationOn");
    final SimpleEventHandlerProperty turnAnimationOff = new SimpleEventHandlerProperty(this, "turnAnimationOff");
    final SimpleEventHandlerProperty rotate5s = new SimpleEventHandlerProperty(this, "rotate5s");
    final SimpleEventHandlerProperty showScreenSize = new SimpleEventHandlerProperty(this, "showScreenSize");
    final SimpleEventHandlerProperty removeFromHistory = new SimpleEventHandlerProperty(this, "removeFromHistory");

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

    @FXBeanInfo.Introspect
    static final class HistoryElement extends HistoryElementBeanInfo {
        final String message;

        HistoryElement(String message) {
            this.message = message;
        }
    }
}
