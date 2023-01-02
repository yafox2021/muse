package org.yafox.muse.runtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.junit.Test;
import org.yafox.muse.Bindable;
import org.yafox.muse.Proxy;
import org.yafox.muse.ProxyHandler;
import org.yafox.muse.service.DemoService;
import org.yafox.muse.service.impl.DemoServiceImpl;
import org.yafox.muse.util.BeanUtil;

public class DynamicImplTest {

    @Test
    public void testMethodToPackage() throws Exception {
        Method method = InvocationHandler.class.getMethod("invoke", Object.class, Method.class, Object[].class);
        DynamicImpl impl = new DynamicImpl();
        String result = impl.methodToPkg(method);
        assertEquals("java.lang.reflect.InvocationHandler.invoke.p0", result);
    }
    
    @Test
    public void testDumpInvokerNormal() throws Exception {
        DynamicImpl impl = new DynamicImpl();
        impl.setAssistant(new AssistantImpl());
        
        Method method = DemoService.class.getMethod("hello", String.class);
        
        Class<?> invokerType = impl.getInvokerType(method);
        
        impl.getRequestType(method).getDeclaredConstructor().newInstance();
        
        assertTrue(AbstractInvoker.class.isAssignableFrom(invokerType));
        
        AbstractInvoker inst = (AbstractInvoker) invokerType.newInstance();
        Class<?> requestType = inst.getRequestType();
        Object req = requestType.getConstructor().newInstance();
        
        inst.setId("svc001");

        inst.setTarget(new DemoServiceImpl());
        
        BeanUtil.set(req, "name", "zhangsan");

        Bindable res = (Bindable) inst.invoke(req);
        assertNotNull("hello zhangsan", res.value());
    }
    
    @Test(expected = Exception.class)
    public void testDumpInvoker() throws Exception {
        DynamicImpl impl = new DynamicImpl();
        impl.setAssistant(new AssistantImpl());
        
        Method method = DemoService.class.getMethod("hello", String.class);
        
        Class<?> invokerType = impl.getInvokerType(method);
        
        AbstractInvoker inst = (AbstractInvoker) invokerType.getConstructor().newInstance();
        Class<?> requestType = inst.getRequestType();
        Object req = requestType.getConstructor().newInstance();
        
        inst.setId("svc001");
                
        inst.setTarget(new DemoServiceImpl());
        
        inst.invoke(req);
    }
    
    @Test
    public void testBindable() throws Exception {
        DynamicImpl impl = new DynamicImpl();
        impl.setAssistant(new AssistantImpl());
        
        Method method = DemoService.class.getMethod("add", int.class, int.class);
        
        Class<?> responseType = impl.getResponseType(method);
        
        Bindable resp = (Bindable) responseType.newInstance();
        
        resp.bind(110);
        
        assertEquals(110, resp.value());
    }
    
    @Test
    public void testDumpProxy() throws Exception {
        DynamicImpl impl = new DynamicImpl();
        impl.setAssistant(new AssistantImpl());
        
        Class<?> proxyType = impl.getProxyType(DemoService.class);
        
        Proxy proxy = (Proxy) proxyType.getConstructor().newInstance();
        
        proxy.setProxyHandler(new ProxyHandler() {
            
            public Object handle(String name, String methodId, Method method, Object input, Class<?> responseType) throws Exception {
                Bindable response = (Bindable) responseType.getConstructor().newInstance();
                response.bind("proxy value");
                return response;
            }
        });
        
        assertEquals("proxy value", ((DemoService)proxy).hello("zhangsan"));
        
    }

}
