package org.yafox.muse.runtime;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;
import org.yafox.muse.runtime.MuseErrorHandler;

public class MuseErrorHandlerTest {

    @Test
    public void testSpecial() throws Exception {
        MuseErrorHandler h = new MuseErrorHandler();
        h.init();
        Integer code = h.findErrorCode(new SQLException());
        assertTrue(code == 2);
        assertEquals("error in sql", h.findErrorMsg(new SQLException()));
    }
    
    @Test
    public void testDefault() throws Exception {
        MuseErrorHandler h = new MuseErrorHandler();
        h.init();
        Object e = h.handle(new Exception("info"));
        System.out.println(e);
    }

}
