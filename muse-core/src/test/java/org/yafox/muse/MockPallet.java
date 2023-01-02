package org.yafox.muse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MockPallet implements Pallet {

    private Map<String, Object> beanMap = new HashMap<String, Object>();
    
    @SuppressWarnings("rawtypes")
    private Map<String, Class> beanTypeMap = new HashMap<String, Class>();
    
    private String basePath = "";
    
    public void addBean(String name, Object bean) {
        this.beanMap.put(name, bean);
    }
    
    public void addBeanType(String name, Class<?> type) {
        this.beanTypeMap.put(name, type);
    }
    
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
    
    @SuppressWarnings("rawtypes")
    public Object getBean(String name) throws Exception {
        Object bean = beanMap.get(name);
        if (bean != null) {
            return bean;
        }
        
        Class beanType = beanTypeMap.get(name);
        
        if (beanType != null) {
            return beanType.newInstance();
        }
        
        return null;
    }

    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        
        Set<Entry<String, Object>> entrySet = this.beanMap.entrySet();
        
        for (Entry<String, Object> entry : entrySet) {
            Object value = entry.getValue();
            Class<?>[] interfaces = value.getClass().getInterfaces();
            for (Class<?> ifItem : interfaces) {
                if (ifItem.getAnnotation(annotationType) != null) {
                    result.put(entry.getKey(), value);
                    break;
                }
            }
            
        }
        
        return result;
    }

    public String getString(String resourceId) throws Exception {
        InputStream inputStream = null;
        StringBuilder builder = new StringBuilder();
        try {
            inputStream = MockPallet.class.getResourceAsStream(resourceId);
            if (inputStream == null) {
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        
        return builder.toString();
    }

}
