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
