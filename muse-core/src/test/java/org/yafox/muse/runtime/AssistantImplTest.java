package org.yafox.muse.runtime;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Test;
import org.yafox.muse.service.DemoService;

public class AssistantImplTest {

    @Test
    public void testParameterNames() throws Exception {
        AssistantImpl resolver = new AssistantImpl();
        Method method = DemoService.class.getMethod("hello", String.class);
        
        String[] parameterNames = resolver.parameterNames(method);
        
        assertArrayEquals(new String[] {"name"}, parameterNames);
        assertEquals("name", parameterNames[0]);
    }

    @Test
    public void testParameterAlisNames() throws Exception {
        AssistantImpl resolver = new AssistantImpl();
        Method method = DemoService.class.getMethod("hello", String.class);
        
        String[] parameterAlisNames = resolver.parameterAlisNames(method);
        
        assertArrayEquals(new String[] {"name"}, parameterAlisNames);
        assertEquals("name", parameterAlisNames[0]);
    }

}
