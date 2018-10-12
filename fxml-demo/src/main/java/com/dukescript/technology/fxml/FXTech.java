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

import net.java.html.json.Function;
import net.java.html.json.Model;
import net.java.html.json.Property;
import org.netbeans.html.context.spi.Contexts;
import org.netbeans.html.context.spi.Contexts.Builder;
import org.netbeans.html.context.spi.Contexts.Id;
import org.netbeans.html.json.spi.Technology;
import org.openide.util.lookup.ServiceProvider;

/** Use <b>FXML</b> to define your UI and use {@link Model @Model} to define
 * your MVVM logic. To use the system
 * in your <b>JavaFX</b> application, create standard <em>Scene.xml</em> file:
 * <pre>

&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;?import java.lang.*?&gt;
&lt;?import java.util.*?&gt;
&lt;?import javafx.scene.*?&gt;
&lt;?import javafx.scene.control.*?&gt;
&lt;?import javafx.scene.layout.*?&gt;

&lt;AnchorPane id="AnchorPane" prefHeight="200" prefWidth="320" xmlns:fx="http://javafx.com/fxml/1" &gt;
    &lt;children&gt;
        &lt;Button layoutX="126" layoutY="90" text="Click Me!" onAction="<b>$controller.handleButtonAction</b>"  /&gt;
        &lt;Label layoutX="126" layoutY="120" minHeight="16" minWidth="69" fx:id="label" text="<b>${controller.testString}</b>" /&gt;
        &lt;ListView layoutX="126" layoutY="160" minHeight="60" minWidth="100" items = "<b>${controller.testList}</b>"&gt;
        &lt;/ListView&gt;
    &lt;/children&gt;
&lt;/AnchorPane&gt;
</pre>
The <b>FXML</b> is referencing properties of your controller class, which can
be easily defined using {@link Model @Model} annotation:
<pre>

{@link Model @Model}(className = "ViewModel", properties = {
    {@link Property @Property}(name = "<b>testString</b>", type = String.class),
    {@link Property @Property}(name = "<b>testList</b>", type = String.class, array = true)
})
public class SimpleController {
    {@link Function @Function}
    static void <b>handleButtonAction</b>(ViewModel vm) {
        vm.setTestString("Hello World!");
        vm.getTestList().add("val4");
    }
}
</pre>
Such model carries two properties and a function <b>handleButtonAction</b> which
acts on the model and gets invoked by the button defined in the <b>FXML</b> file once
it is clicked. At the end, one just instantiates the whole scene and binds it
together with the controller:
<pre>

FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SimpleScene.fxml"));
viewModel = new ViewModel("Hallo Hallo", "val1", "val2", "val3");
fxmlLoader.setController(Models.toRaw(viewModel));
Parent rootNode = fxmlLoader.load();
</pre>
* 
* Your model view view model completely separated from the <b>JavaFX</b> UI definition has
* just become reality!
 */
@ServiceProvider(service = Contexts.Provider.class)
public class FXTech implements Contexts.Provider {
    /** Registers a {@link Technology} that plays well with <b>FXML</b>.
     * The {@link Id technology id} of the registered technology is
     * <em>fxml</em> and it is {@link Builder#register(java.lang.Class, java.lang.Object, int) registered} at position 10.
     * 
     * @param context context to register the technology into
     * @param requestor ignored
     */
    @Override
    public void fillContext(Builder context, Class<?> requestor) {
        context.register(Technology.class, new FXTechnology(), 10);
    }
}
