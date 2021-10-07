package org.yafox.muse.runtime;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.junit.Test;
import org.yafox.muse.Bindable;
import org.yafox.muse.Enumable;
import org.yafox.muse.Proxy;
import org.yafox.muse.ProxyHandler;
import org.yafox.muse.annotation.Mark;
import org.yafox.muse.runtime.AbstractInvoker;
import org.yafox.muse.runtime.MuseDynamic;
import org.yafox.muse.runtime.MuseMethodImprover;
import org.yafox.muse.service.DemoService;
import org.yafox.muse.service.impl.DemoServiceImpl;
import org.yafox.muse.util.BeanUtil;

public class MuseDynamicTest {

    @Test
    public void testMethodToPackage() throws Exception {
        Method method = InvocationHandler.class.getMethod("invoke", Object.class, Method.class, Object[].class);
        MuseDynamic impl = new MuseDynamic();
        String result = impl.methodToPackage(method);
        assertEquals("java.lang.reflect.InvocationHandler.invoke.v83hsd263qqfax2u", result);
    }
    
    @Test
    public void testSpliteMethodToPackage() throws Exception {
        MuseDynamic impl = new MuseDynamic();
        String[] parts = impl.splitMethodPackage("java.lang.reflect.InvocationHandler.invoke.v83hsd263qqfax2u");
        assertEquals("java.lang.reflect.InvocationHandler", parts[0]);
        assertEquals("invoke", parts[1]);
        assertEquals("v83hsd263qqfax2u", parts[2]);
    }
    
    @Test
    public void testDumpInvokerNormal() throws Exception {
        MuseDynamic impl = new MuseDynamic();
        impl.setMethodImprover(new MuseMethodImprover());
        
        Method method = DemoService.class.getMethod("hello", String.class);
        
        Class<?> invokerType = impl.getInvokerType(method);
        
        System.out.println(AbstractInvoker.class.isAssignableFrom(invokerType));
        
        AbstractInvoker inst = (AbstractInvoker) invokerType.newInstance();
        Class<?> requestType = inst.getRequestType();
        Object req = requestType.newInstance();
        Class<?> responseType = inst.getResponseType();
        Object res = responseType.newInstance();
        
        inst.setId("svc001");
        
        Field declaredField = requestType.getDeclaredField("name");
        System.out.println(declaredField);
        System.out.println(declaredField.getAnnotation(Mark.class));
        
        Enumable enumable = (Enumable) req;
        
        String[] names = enumable.names();
        Object[] values = enumable.values();
        for (int i = 0; i < names.length; i++) {
            System.out.println(names[i] + " : " + values[i]);
        }
        
        Bindable bindable = (Bindable) res;
        
        bindable.bind("hello");
        
        inst.setTarget(new DemoServiceImpl());
        
        BeanUtil.set(req, "name", "val1");
        
        assertEquals("hello", bindable.value());
        
        res = inst.invoke(req);
        System.out.println(inst);
        
    }
    
    @Test(expected = Exception.class)
    public void testDumpInvoker() throws Exception {
        MuseDynamic impl = new MuseDynamic();
        impl.setMethodImprover(new MuseMethodImprover());
        
        Method method = DemoService.class.getMethod("hello", String.class);
        
        Class<?> invokerType = impl.getInvokerType(method);
        
        System.out.println(AbstractInvoker.class.isAssignableFrom(invokerType));
        
        AbstractInvoker inst = (AbstractInvoker) invokerType.newInstance();
        Class<?> requestType = inst.getRequestType();
        Object req = requestType.newInstance();
        Class<?> responseType = inst.getResponseType();
        Object res = responseType.newInstance();
        
        inst.setId("svc001");
        
        Field declaredField = requestType.getDeclaredField("name");
        System.out.println(declaredField);
        System.out.println(declaredField.getAnnotation(Mark.class));
        
        Enumable enumable = (Enumable) req;
        
        String[] names = enumable.names();
        Object[] values = enumable.values();
        for (int i = 0; i < names.length; i++) {
            System.out.println(names[i] + " : " + values[i]);
        }
        
        Bindable bindable = (Bindable) res;
        
        bindable.bind("hello");
        
        inst.setTarget(new DemoServiceImpl());
        
        res = inst.invoke(req);
        System.out.println(inst);
        
    }
    
    @Test
    public void testBindable() throws Exception {
        MuseDynamic impl = new MuseDynamic();
        impl.setMethodImprover(new MuseMethodImprover());
        
        Method method = DemoService.class.getMethod("add", int.class, int.class);
        
        Class<?> responseType = impl.getResponseType(method);
        
        Bindable resp = (Bindable) responseType.newInstance();
        
        resp.bind(110);
        
        assertEquals(110, resp.value());
    }
    
    @Test
    public void testDumpProxy() throws Exception {
        MuseDynamic impl = new MuseDynamic();
        impl.setMethodImprover(new MuseMethodImprover());
        
        Class<?> proxyType = impl.getProxyType(DemoService.class);
        
        Proxy proxy = (Proxy) proxyType.newInstance();
        
        proxy.setProxyHandler(new ProxyHandler() {
            
            public Object handle(String name, String methodId, Method method, Object input, Class<?> responseType) throws Exception {
                Bindable response = (Bindable) responseType.newInstance();
                response.bind("stop");
                return response;
            }
        });
        
        System.out.println(((DemoService)proxy).hello("zhangsan"));
        
    }

}
