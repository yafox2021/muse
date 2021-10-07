package org.yafox.muse.runtime;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;
import org.yafox.muse.runtime.MuseMethodImprover;
import org.yafox.muse.service.DemoService;

public class MuseMethodImproverTest {

    @Test
    public void testParameterNames() throws Exception {
        MuseMethodImprover resolver = new MuseMethodImprover();
        Method method = DemoService.class.getMethod("hello", String.class);
        
        String[] parameterNames = resolver.parameterNames(method);
        
        assertArrayEquals(new String[] {"name"}, parameterNames);
        assertEquals("name", parameterNames[0]);
    }

    @Test
    public void testParameterAlisNames() throws Exception {
        MuseMethodImprover resolver = new MuseMethodImprover();
        Method method = DemoService.class.getMethod("hello", String.class);
        
        String[] parameterAlisNames = resolver.parameterAlisNames(method);
        
        assertArrayEquals(new String[] {"name"}, parameterAlisNames);
        assertEquals("name", parameterAlisNames[0]);
    }

}
