package org.yafox.muse.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Test;
import org.yafox.muse.Fault;

public class FaultHandlerImplTest {

    @Test
    public void testSpecial() throws Exception {
        FaultHandlerFactory handlerFactory = new FaultHandlerFactory();
        FaultHandlerImpl h = (FaultHandlerImpl) handlerFactory.createFaultHandler();
        
        Integer code = h.findCode(new SQLException());
        assertTrue(code == 2);
        assertEquals("error in sql", h.findMsg(new SQLException()));
    }
    
    @Test
    public void testDefault() throws Exception {
        FaultHandlerFactory handlerFactory = new FaultHandlerFactory();
        FaultHandlerImpl faultHandler = (FaultHandlerImpl) handlerFactory.createFaultHandler();
        
        Fault fault = faultHandler.handle(new Exception("error info"));
        assertEquals("error info", fault.getMsg());
    }

}
