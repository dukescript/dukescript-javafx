/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dukescript.javafx.fxbeaninfo;

import com.dukescript.api.javafx.beans.FXBeanInfo;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 *
 * @author antonepple
 */
public class HTMLController implements FXBeanInfo.Provider {

    private final StringProperty labelText = new SimpleStringProperty(this, "labelText", "");
    private final Property<EventHandler<ActionEvent>> action =
            new SimpleObjectProperty<EventHandler<ActionEvent>>(
                    this, "action",
                    (e) -> labelText.set("Hello World!"));
    
    private final FXBeanInfo info = FXBeanInfo
            .create(this)
            .action(action)
            .property(labelText)
            .build();
    
    @Override
    public FXBeanInfo getFXBeanInfo() {
        return info;
    }

}
