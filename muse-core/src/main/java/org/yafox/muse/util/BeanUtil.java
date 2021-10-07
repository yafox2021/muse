package org.yafox.muse.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanUtil {

    private static Map<String, Method> writeMethodMap = new HashMap<String, Method>();

    private static Map<String, Method> readMethodMap = new HashMap<String, Method>();

    public static void set(Object target, String name, Object value) throws Exception {
        Class<? extends Object> type = target.getClass();
        String key = resolveKey(type, name);
        Method method = writeMethodMap.get(key);
        if (method == null) {
            synchronized (writeMethodMap) {
                method = writeMethodMap.get(key);
                if (method == null) {
                    method = obtainWriteMethod(type, name);
                    writeMethodMap.put(key, method);
                }
            }
        }

        if (method != null) {
            method.invoke(target, value);
        }
    }

    public static Object get(Object target, String name) throws Exception {
        Class<? extends Object> type = target.getClass();
        String key = resolveKey(type, name);
        Method method = readMethodMap.get(key);
        if (method == null) {
            synchronized (readMethodMap) {
                method = readMethodMap.get(key);
                if (method == null) {
                    method = obtainReadMethod(type, name);
                    readMethodMap.put(key, method);
                }
            }
        }
        if (method != null) {
            return method.invoke(target);
        }

        return null;

    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void set(Object target, int index, Object value) throws Exception {
        if (target instanceof List) {
            ((List)target).set(index, value);
        } else if (target.getClass().isArray()) {
            if (index < Array.getLength(target)) {
                Array.set(target, index, value);
            }
        }
    }
    
    @SuppressWarnings("rawtypes")
    public static Object get(Object target, int index) throws Exception {
        if (target instanceof List) {
            List list = (List)target;
            if (index < list.size()) {
                list.get(index);
            }
        } else if (target.getClass().isArray()) {
            if (index < Array.getLength(target)) {
                Array.get(target, index);
            }
        }
        return null;
    }

    public static String resolveKey(Class<?> type, String name) throws Exception {
        return type.getName() + "#" + name;
    }

    public static Method obtainReadMethod(Class<?> type, String name) throws Exception {
        String expectMethodName = "get" + upperFirst(name);
        String expectMethodName2 = "is" + upperFirst(name);
        Method targetMethod = null;
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(expectMethodName) && method.getParameterTypes().length == 0) {
                targetMethod = method;
                break;
            }
            
            if (method.getName().equals(expectMethodName2) && method.getParameterTypes().length == 0 && boolean.class.equals(method.getReturnType())) {
                targetMethod = method;
                break;
            }
        }
        return targetMethod;
    }

    public static Method obtainWriteMethod(Class<?> type, String name) throws Exception {
        String expectMethodName = "set" + upperFirst(name);
        Method targetMethod = null;
        Method[] methods = type.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(expectMethodName) && void.class.equals(method.getReturnType()) && method.getParameterTypes().length == 1) {
                targetMethod = method;
                break;
            }
        }
        return targetMethod;
    }

    protected static String upperFirst(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static void copy(Object source, Object target) throws Exception {
        Class<? extends Object> sourceType = source.getClass();
        Class<? extends Object> targetType = target.getClass();
        if (!sourceType.equals(targetType)) {
            return;
        }
        copy(source, target, sourceType);
    }
    
    public static void copy(Object source, Object target, Class<?> withType) throws Exception {
        if (Object.class.equals(withType)) {
            return;
        }
        
        if (!withType.isInstance(source) || !withType.isInstance(target)) {
            return;
        }
        
        Field[] declaredFields = withType.getDeclaredFields();
        
        for (Field field : declaredFields) {
            String fieldName = field.getName();
            Object value = get(source, fieldName);
            if (value != null) {
                set(target, fieldName, value);
            }
        }
        
        Class<?> superclass = withType.getSuperclass();
        
        copy(source, target, superclass);
    }
}
