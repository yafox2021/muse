package org.yafox.muse.spring;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yafox.muse.Pallet;

public class MusePallet implements Pallet, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MusePallet.class);
    
    private ApplicationContext applicationContext;
    
    private PathMatchingResourcePatternResolver resolver;

    public Object getBean(String name) throws Exception {
        return applicationContext.getBean(name);
    }

    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws Exception {
        return applicationContext.getBeansWithAnnotation(annotationType);
    }

    public String loadResourceAsString(String resourceId) throws Exception {
        Resource resource = resolver.getResource(resourceId);
        if (!resource.exists()) {
            return null;
        }
        
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bs = new byte[1024];
            int len = -1;
            while ((len = inputStream.read(bs)) != -1) {
                baos.write(bs, 0, len);
            }

            return new String(baos.toByteArray(), "utf8");
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }

    }

    protected void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            LOGGER.error("error in close", e);
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        resolver = new PathMatchingResourcePatternResolver(this.applicationContext);
    }

}
