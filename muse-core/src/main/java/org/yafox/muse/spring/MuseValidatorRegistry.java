package org.yafox.muse.spring;

import java.io.Closeable;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

public class MuseValidatorRegistry implements BeanDefinitionRegistryPostProcessor {

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        try {
            Enumeration<URL> resources = MuseValidatorRegistry.class.getClassLoader().getResources("muse/validators.properties");

            Map<String, String> validatorInfoMap = new HashMap<String, String>();

            while (resources.hasMoreElements()) {
                URL url = (URL) resources.nextElement();
                Properties props = buildProperties(url);

                Set<String> stringPropertyNames = props.stringPropertyNames();
                for (String name : stringPropertyNames) {
                    validatorInfoMap.put(name, props.getProperty(name));
                }

                Set<Entry<String, String>> entrySet = validatorInfoMap.entrySet();
                for (Entry<String, String> entry : entrySet) {
                    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(entry.getValue());
                    builder.setScope(BeanDefinition.SCOPE_PROTOTYPE);
                    registry.registerBeanDefinition(entry.getKey(), builder.getBeanDefinition());
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private Properties buildProperties(URL url) throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = url.openStream();
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
