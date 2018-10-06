package com.dukescript.demo.javafx.webui;

import java.util.Arrays;
import java.util.List;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.webui.FXBeanInfo;
import net.java.html.json.Models;

final class DataModel implements FXBeanInfo.Provider {
    final ObjectProperty<String> message = new SimpleObjectProperty<>(this, "message");
    final ObservableValue<List<String>> words = new ObjectBinding<List<String>>() {
        {
            bind(message);
        }
        @Override
        protected List<String> computeValue() {
            return words(message.get());
        }
    };
    final BooleanProperty rotating = new SimpleBooleanProperty(this, "rotating");
    final Property<EventHandler<ActionEvent>> turnAnimationOn = new SimpleObjectProperty<>(this, "turnAnimationOn");
    final Property<EventHandler<ActionEvent>> turnAnimationOff = new SimpleObjectProperty<>(this, "turnAnimationOff");
    final Property<EventHandler<ActionEvent>> rotate5s = new SimpleObjectProperty<>(this, "rotate5s");
    final Property<EventHandler<ActionEvent>> showScreenSize = new SimpleObjectProperty<>(this, "showScreenSize");
    final FXBeanInfo info = FXBeanInfo.create(this).
            property(message).
            property(rotating).
            property("words", words).
            action(turnAnimationOn).
            action(turnAnimationOff).
            action(rotate5s).
            action(showScreenSize).
            build();

    public DataModel() {
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
    static void onPageLoad(PlatformServices services) {
        DataModel ui = new DataModel();
        ui.message.set("Hello World from HTML and Java!");
        Models.applyBindings(ui);
    }
}
