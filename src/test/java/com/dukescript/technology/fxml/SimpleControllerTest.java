package com.dukescript.technology.fxml;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import net.java.html.json.Function;
import net.java.html.json.Model;
import net.java.html.json.Models;
import net.java.html.json.Property;
import org.junit.Assert;
import org.junit.Test;
import static org.loadui.testfx.Assertions.assertNodeExists;
import org.loadui.testfx.GuiTest;

@Model(className = "ViewModel", properties = {
    @Property(name = "testString", type = String.class),
    @Property(name = "testList", type = String.class, array = true)
})
public class SimpleControllerTest extends GuiTest {
    @Function
    static void handleButtonAction(ViewModel vm) {
        vm.setTestString("Hello World!");
        vm.getTestList().add("val4");
    }

    @Override
    public Parent getRootNode() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SimpleScene.fxml"));
            viewModel = new ViewModel("Hallo Hallo", "val1", "val2", "val3");
            fxmlLoader.setController(Models.toRaw(viewModel));
            return (Parent)fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(SimpleControllerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    private ViewModel viewModel;

    
     @Test
     public void testClickButton() {
         assertNodeExists( "Hallo Hallo" );
         Assert.assertEquals(3, viewModel.getTestList().size());
         click("Click Me!");
         assertNodeExists( "Hello World!" );
         Assert.assertEquals(4, viewModel.getTestList().size());
     }
    


}
