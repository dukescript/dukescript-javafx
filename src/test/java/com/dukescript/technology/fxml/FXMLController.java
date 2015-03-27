package com.dukescript.technology.fxml;

import net.java.html.json.Function;
import net.java.html.json.Model;
import net.java.html.json.Property;

@Model(className = "ViewModel", properties = {
    @Property(name = "testString", type = String.class),
    @Property(name = "testList", type = String.class, array = true)
})
public class FXMLController {

    @Function
    static void handleButtonAction(ViewModel vm) {
        vm.setTestString("Hello World!");
        vm.getTestList().add("val4");
    }

}
