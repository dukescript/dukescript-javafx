/*
 * The MIT License
 *
 * Copyright 2019 devel.
 *
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
 */
package com.dukescript.impl.javafx.beans;

import com.dukescript.api.javafx.beans.FXBeanInfo;
import com.dukescript.api.javafx.beans.SimpleEventHandlerProperty;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.SimpleIntegerProperty;
import net.java.html.BrwsrCtx;
import net.java.html.json.Models;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.eq;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import org.netbeans.html.context.spi.Contexts;
import org.netbeans.html.json.spi.FunctionBinding;
import org.netbeans.html.json.spi.PropertyBinding;
import org.netbeans.html.json.spi.Proto;
import org.netbeans.html.json.spi.Technology;

public class DynamicPropertiesTest {

    private PrintStream prevErr;
    public static final class Bean implements FXBeanInfo.Provider {
        private final FXBeanInfo info;

        public Bean(List<String> propNames, List<String> actionNames) {
            FXBeanInfo.Builder b = FXBeanInfo.newBuilder(this);
            for (String n : propNames) {
                b.property(n, new SimpleIntegerProperty(this, n, 0));
            }
            for (String n : actionNames) {
                b.action(new SimpleEventHandlerProperty(this, n, (t) -> {
                    SimpleIntegerProperty sip = (SimpleIntegerProperty) getFXBeanInfo().getProperties().get(n);
                    sip.set(sip.get() + 1);
                }));
            }
            this.info = b.build();
        }

        @Override
        public FXBeanInfo getFXBeanInfo() {
            return info;
        }
    }

    @Before
    public void storeErr() {
        prevErr = System.err;
    }

    @After
    public void resetErr() {
        System.setErr(prevErr);
    }

    @Test
    public void oneTwoThreeProperty() {
        boolean[] ok = { false };
        Technology.BatchInit<?> myTechnology = Mockito.mock(Technology.BatchInit.class);
        BrwsrCtx ctx = Contexts.newBuilder().register(Technology.class, myTechnology, 10).build();
        ctx.execute(() -> {
            Bean b = new Bean(Arrays.asList("one", "two", "three"), Arrays.asList("two"));
            Proto proto = assertProto(b);
            assertNotNull(proto);

            Models.applyBindings(b);
            ArgumentCaptor<PropertyBinding[]> props = ArgumentCaptor.forClass(PropertyBinding[].class);
            ArgumentCaptor<FunctionBinding[]> funcs = ArgumentCaptor.forClass(FunctionBinding[].class);
            verify(myTechnology).wrapModel(eq(b), props.capture(), funcs.capture());

            final PropertyBinding[] propArr = assertProps(props.getValue(), "one", "two", "three");
            final FunctionBinding[] funcArr = assertFuncs(funcs.getValue(), "two");

            assertEquals(3, propArr.length);
            assertEquals(0, propArr[0].getValue());
            assertEquals(0, propArr[1].getValue());
            assertEquals(0, propArr[2].getValue());

            assertEquals(1, funcArr.length);
            funcArr[0].call(null, null);
            assertEquals("Incremented to one", 1, propArr[1].getValue());
            assertEquals(0, propArr[0].getValue());
            assertEquals(0, propArr[2].getValue());

            ok[0] = true;
        });
        assertTrue(ok[0]);
    }

    @Test
    public void fiveSixProperty() {
        boolean[] ok = { false };
        Technology.BatchInit<?> myTechnology = Mockito.mock(Technology.BatchInit.class);
        BrwsrCtx ctx = Contexts.newBuilder().register(Technology.class, myTechnology, 10).build();
        ctx.execute(() -> {
            Bean b = new Bean(Arrays.asList("five", "six"), Arrays.asList("six", "five"));
            Proto proto = assertProto(b);
            assertNotNull(proto);

            Models.applyBindings(b);
            ArgumentCaptor<PropertyBinding[]> props = ArgumentCaptor.forClass(PropertyBinding[].class);
            ArgumentCaptor<FunctionBinding[]> funcs = ArgumentCaptor.forClass(FunctionBinding[].class);
            verify(myTechnology).wrapModel(eq(b), props.capture(), funcs.capture());

            final PropertyBinding[] propArr = assertProps(props.getValue(), "five", "six");
            final FunctionBinding[] funcArr = assertFuncs(funcs.getValue(), "six", "five");

            assertEquals(2, propArr.length);
            assertEquals(0, propArr[0].getValue());
            assertEquals(0, propArr[1].getValue());

            assertEquals(2, funcArr.length);
            assertEquals("six", funcArr[0].getFunctionName());
            funcArr[0].call(null, null);

            assertEquals(0, propArr[0].getValue());
            assertEquals(1, propArr[1].getValue());

            assertEquals("five", funcArr[1].getFunctionName());
            funcArr[1].call(null, null);

            assertEquals(1, propArr[0].getValue());
            assertEquals(1, propArr[1].getValue());

            ok[0] = true;
        });
        assertTrue(ok[0]);
    }

    @Test
    public void twoSixPropertyNoThree() {
        boolean[] ok = {false};
        Technology.BatchInit<?> myTechnology = Mockito.mock(Technology.BatchInit.class);
        BrwsrCtx ctx = Contexts.newBuilder().register(Technology.class, myTechnology, 10).build();
        ctx.execute(() -> {
            Bean b = new Bean(Arrays.asList("two", "six"), Arrays.asList("three"));
            Proto proto = assertProto(b);
            assertNotNull(proto);

            Models.applyBindings(b);
            ArgumentCaptor<PropertyBinding[]> props = ArgumentCaptor.forClass(PropertyBinding[].class);
            ArgumentCaptor<FunctionBinding[]> funcs = ArgumentCaptor.forClass(FunctionBinding[].class);
            verify(myTechnology).wrapModel(eq(b), props.capture(), funcs.capture());

            final PropertyBinding[] propArr = assertProps(props.getValue(), "two", "six");
            final FunctionBinding[] funcArr = assertFuncs(funcs.getValue(), "three");

            assertEquals(2, propArr.length);
            assertEquals(0, propArr[0].getValue());
            assertEquals(0, propArr[1].getValue());

            assertEquals(1, funcArr.length);
            assertEquals("three", funcArr[0].getFunctionName());

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            System.setErr(new PrintStream(bos));
            funcArr[0].call(null, null);
            final String err = bos.toString();
            assertNotEquals(err, -1, err.indexOf("NullPointerException"));

            assertEquals("No change", 0, propArr[0].getValue());
            assertEquals("No change", 0, propArr[1].getValue());

            ok[0] = true;
        });
        assertTrue(ok[0]);

    }

    private static Proto assertProto(Bean b) {
        final FXHtmlAdapter adapter = b.getFXBeanInfo().lookup(FXHtmlAdapter.class);
        assertNotNull("Adapter found for " + b, adapter);
        assertNotNull("Proto found for " + b, adapter.proto);
        return adapter.proto;
    }
    private static PropertyBinding[] assertProps(PropertyBinding[] all, String... find) {
        PropertyBinding[] ret = new PropertyBinding[find.length];
        ALL: for (int i = 0; i < find.length; i++) {
            for (int j = 0; j < all.length; j++) {
                if (all[j] == null) {
                    continue;
                }
                if (all[j].getPropertyName().equals(find[i])) {
                    ret[i] = all[j];
                    continue ALL;
                }
            }
            fail("Cannot find property " + find[i] + " among " + Arrays.toString(all));
        }
        return ret;
    }
    private static FunctionBinding[] assertFuncs(FunctionBinding[] all, String... find) {
        FunctionBinding[] ret = new FunctionBinding[find.length];
        ALL: for (int i = 0; i < find.length; i++) {
            for (int j = 0; j < all.length; j++) {
                if (all[j] == null) {
                    continue;
                }
                if (all[j].getFunctionName().equals(find[i])) {
                    ret[i] = all[j];
                    continue ALL;
                }
            }
            fail("Cannot find property " + find[i] + " among " + Arrays.toString(all));
        }
        return ret;
    }
}
