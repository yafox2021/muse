package org.yafox.muse.runtime;

import java.io.Closeable;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.yafox.muse.ErrorHandler;

import java.util.Properties;
import java.util.Set;

public class MuseErrorHandler implements ErrorHandler {
    
    private Map<String, Integer> errorCodeMap = null;
    
    private Map<String, String> errorMsgMap = null;
    
    public void init() throws Exception {
        Map<String, Integer> newErrorCodeMap = new HashMap<String, Integer>();
        
        Map<String, String> codeMap = loadMapFromProperties("muse-code.properties");
        Set<Entry<String, String>> entrySet = codeMap.entrySet();
        for (Entry<String, String> entry : entrySet) {
            newErrorCodeMap.put(entry.getKey(), new Integer(entry.getValue()));
        }
        
        errorMsgMap = loadMapFromProperties("muse-msg.properties");
        errorCodeMap = newErrorCodeMap;
    }
    
    protected Map<String, String> loadMapFromProperties(String fileName) throws Exception {
        ClassLoader classLoader = MuseError.class.getClassLoader();
        Enumeration<URL> resources = classLoader.getResources(fileName);
        Map<String, String> result = new HashMap<String, String>();
        
        while (resources.hasMoreElements()) {
            URL url = (URL) resources.nextElement();
            InputStream inputStream = null;
            try {
                inputStream = url.openStream();
                Properties properties = new Properties();
                properties.load(inputStream);
                Set<String> names = properties.stringPropertyNames();
                for (String name : names) {
                    String property = properties.getProperty(name);
                    result.put(name, property.trim());
                }
            } finally {
                close(inputStream);
            }
        }
        
        return result;
    }
    
    
    
    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }
    
    public Object handle(Throwable t) {
        MuseError error = new MuseError();
        if (t instanceof CustomizedException) {
            CustomizedException customizedException = (CustomizedException) t;
            error.setCode(customizedException.getCode());
            error.setMsg(customizedException.getMsg());
            error.setExt(customizedException.getExt());
        } else {
            error.setMsg(findMsg(t));
        }

        if (error.getCode() == 0) {
            Integer code = findCode(t);
            error.setCode(code);
        }
        
        error.setException(t.getClass().getName());
        return error;
    }
    
    @SuppressWarnings("rawtypes")
    protected Integer findCode(Throwable t) {
        Integer code = 1;
        if (errorCodeMap == null) {
            return code;
        }
        
        Class type = t.getClass();
        while(!errorCodeMap.containsKey(type.getName())) {
            type = type.getSuperclass();
            if (Throwable.class.equals(type)) {
                break;
            }
        }
        
        Integer configValue = errorCodeMap.get(type.getName());
        return configValue == null ? code : configValue;
    }
    
    protected String findMsg(Throwable t) {
        if (errorCodeMap == null) {
            return t.getMessage();
        }
        
        String msg = errorMsgMap.get(t.getClass().getName());
        if (msg == null) {
            msg = t.getMessage();
        }
        return msg;
    }

}
