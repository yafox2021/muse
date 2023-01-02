package org.yafox.muse.spring;

import java.io.Closeable;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

public class ValidatorRegistry implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        try {
            Resource[] resources = this.applicationContext.getResources("classpath*:/validators.properties");
            for (Resource resource : resources) {
                Properties properties = buildProperties(resource);
                
                Set<String> beanNames = properties.stringPropertyNames();
                
                for (String beanName : beanNames) {
                    String validatorClassName = properties.getProperty(beanName);
                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(validatorClassName);
                    builder.setScope(BeanDefinition.SCOPE_PROTOTYPE);
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
}
