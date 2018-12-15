package com.dukescript.api.javafx.beans;

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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class EventHandlerTest implements FXBeanInfo.Provider {
    private int count;
    private FXBeanInfo info;

    @FXML
    public void incrementByOne() {
        count++;
    }

    @FXML
    public void incrementByThis(ActionEvent ev) {
        if (ev == null) {
            count = -1;
            return;
        }
        Number n = (Number) ev.getSource();
        count += n.intValue();
    }

    @Before
    public void generateInfo() {
        info = FXBeanInfo.newBuilder(this)
            .action("incrementByOne", this::incrementByOne)
            .action("incrementByThis", this::incrementByThis)
            .build();
    }

    @Test
    public void testIncrementByOne() {
        EventHandlerProperty p = info.getActions().get("incrementByOne");
        assertNotNull("property found", p);
        final EventHandler<? super ActionDataEvent> h = p.getValue();
        assertNotNull("it has handler", h);
        h.handle(null);
        assertEquals(1, count);
    }

    @Test
    public void testIncrementByThis() {
        EventHandlerProperty p = info.getActions().get("incrementByThis");
        assertNotNull("property found", p);
        final EventHandler<? super ActionDataEvent> h = p.getValue();
        assertNotNull("it has handler", h);
        h.handle(null);
        assertEquals(-1, count);
    }

    @Override
    public FXBeanInfo getFXBeanInfo() {
        return info;
    }
}
