package com.dukescript.technology.fxml;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import net.java.html.json.Models;
import org.junit.Assert;
import org.junit.Test;
import static org.loadui.testfx.Assertions.assertNodeExists;
import org.loadui.testfx.GuiTest;

public class SimpleTest extends GuiTest {

    @Override
    public Parent getRootNode() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));
            viewModel = new ViewModel("Hallo Hallo", "val1", "val2", "val3");
            fxmlLoader.setController(Models.toRaw(viewModel));
            Parent root = fxmlLoader.load();
            return root;
        } catch (IOException ex) {
            Logger.getLogger(SimpleTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    private ViewModel viewModel;

    
     @Test
     public void testClickButton() {
         assertNodeExists( "Hallo Hallo" );
         Assert.assertEquals(viewModel.getTestList().size(),3);
         click("Click Me!");
         assertNodeExists( "Hello World!" );
         Assert.assertEquals(viewModel.getTestList().size(),4);
     }
    


}
