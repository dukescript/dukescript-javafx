Use __FXML__ to define your __UI__ and use [@Model](http://bits.netbeans.org/html+java/1.1/net/java/html/json/Model.html)
to define your __MVVM__ logic. To use the system in your __JavaFX__ application, create standard Scene.xml file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?import java.lang.*?>
<?import java.util.*?>
<?import javafx.scene.*?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<AnchorPane id="AnchorPane" prefHeight="200" prefWidth="320" xmlns:fx="http://javafx.com/fxml/1" >
    <children>
        <Button layoutX="126" layoutY="90" text="Click Me!" onAction="$controller.handleButtonAction"  />
        <Label layoutX="126" layoutY="120" minHeight="16" minWidth="69" fx:id="label" text="${controller.testString}" />
        <ListView layoutX="126" layoutY="160" minHeight="60" minWidth="100" items = "${controller.testList}">
        </ListView>
    </children>
</AnchorPane>
```

The FXML is referencing properties of your controller class, which can be easily defined using 
[@Model](http://bits.netbeans.org/html+java/1.1/net/java/html/json/Model.html) annotation:

```java
@Model(className = "ViewModel", properties = {
    @Property(name = "testString", type = String.class),
    @Property(name = "testList", type = String.class, array = true)
})
public class SimpleController {
    @Function
    static void handleButtonAction(ViewModel vm) {
        vm.setTestString("Hello World!");
        vm.getTestList().add("val4");
    }
}
```

Such model carries two properties and a function __handleButtonAction__
which acts on the model and gets invoked by the button defined in the __FXML__
file once it is clicked. At the end, one just instantiates 
the whole scene and binds it together with the controller:

```java
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SimpleScene.fxml"));
viewModel = new ViewModel("Hallo Hallo", "val1", "val2", "val3");
fxmlLoader.setController(Models.toRaw(viewModel));
Parent rootNode = fxmlLoader.load();
```

Your _model view view model_ completely separated from the __JavaFX UI__ definition has just become reality!