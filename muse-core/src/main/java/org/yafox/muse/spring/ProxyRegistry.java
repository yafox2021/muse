package org.yafox.muse.spring;

import java.io.Closeable;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.yafox.muse.Dynamic;

public class ProxyRegistry implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private Dynamic dynamic;
    
    private ApplicationContext applicationContext;

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        try {
            Resource[] resources = this.applicationContext.getResources("classpath*:/proxy/*.properties");
            
            for (Resource resource : resources) {
                Properties properties = buildProperties(resource);
                String fileName = resource.getFilename();
                String handlerName = fileName.substring(0, fileName.indexOf(".properties"));
                Set<String> beanNames = properties.stringPropertyNames();

                for (String beanName : beanNames) {
                    String interfaceTypeName = properties.getProperty(beanName);
                    Class<?> interfaceType = Class.forName(interfaceTypeName);
                    if (!interfaceType.isInterface()) {
                        throw new Exception(interfaceTypeName + " is not an interface");
                    }
                    Class<?> proxyType = dynamic.getProxyType(interfaceType);
                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(proxyType);
                    builder.addPropertyValue("name", beanName);
                    builder.addPropertyReference("proxyHandler", handlerName);

                    registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    private Properties buildProperties(Resource resource) throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = resource.getInputStream();
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties;
        } finally {
            close(inputStream);
        }
    }
    
    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {

            }
        }
    }

    public Dynamic getDynamic() {
        return dynamic;
    }

    public void setDynamic(Dynamic dynamic) {
        this.dynamic = dynamic;
    }
    
    

}
