package org.yafox.muse.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.yafox.muse.runtime.AssistantImpl;
import org.yafox.muse.runtime.DynamicImpl;
import org.yafox.muse.runtime.InvokerBucketImpl;
import org.yafox.muse.runtime.MuseLauncher;

public class Muse4Spring implements BeanDefinitionRegistryPostProcessor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("muse");

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        
        LOGGER.info("muse prepare to start");
        
        BeanDefinitionBuilder assistantBuilder = BeanDefinitionBuilder.genericBeanDefinition(AssistantImpl.class);
        assistantBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        
        BeanDefinitionBuilder dynamicBuilder = BeanDefinitionBuilder.genericBeanDefinition(DynamicImpl.class);
        dynamicBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        dynamicBuilder.addPropertyReference("assistant", "assistant");
        
        BeanDefinitionBuilder palletBuilder = BeanDefinitionBuilder.genericBeanDefinition(SpringPallet.class);
        palletBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        
        BeanDefinitionBuilder invokerBucketBuilder = BeanDefinitionBuilder.genericBeanDefinition(InvokerBucketImpl.class);
        invokerBucketBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        
        BeanDefinitionBuilder faultHandlerBuilder = BeanDefinitionBuilder.genericBeanDefinition(FaultHandlerFactoryBean.class);
        faultHandlerBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        
        BeanDefinitionBuilder launcherBuilder = BeanDefinitionBuilder.genericBeanDefinition(MuseLauncher.class);
        launcherBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        launcherBuilder.setInitMethodName("launch");
        launcherBuilder.addPropertyReference("dynamic", "dynamic");
        launcherBuilder.addPropertyReference("pallet", "pallet");
        launcherBuilder.addPropertyReference("invokerBucket", "invokerBucket");
        
        BeanDefinitionBuilder proxyRegistryBuilder = BeanDefinitionBuilder.genericBeanDefinition(ProxyRegistry.class);
        proxyRegistryBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        proxyRegistryBuilder.addPropertyReference("dynamic", "dynamic");
        
        BeanDefinitionBuilder validatorRegistryBuilder = BeanDefinitionBuilder.genericBeanDefinition(ValidatorRegistry.class);
        validatorRegistryBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        
        registry.registerBeanDefinition("assistant", assistantBuilder.getBeanDefinition());
        registry.registerBeanDefinition("dynamic", dynamicBuilder.getBeanDefinition());
        registry.registerBeanDefinition("pallet", palletBuilder.getBeanDefinition());
        registry.registerBeanDefinition("invokerBucket", invokerBucketBuilder.getBeanDefinition());
        registry.registerBeanDefinition("faultHandler", faultHandlerBuilder.getBeanDefinition());
        registry.registerBeanDefinition("museLauncher", launcherBuilder.getBeanDefinition());
        registry.registerBeanDefinition("proxyRegistry", proxyRegistryBuilder.getBeanDefinition());
        registry.registerBeanDefinition("validatorRegistry", validatorRegistryBuilder.getBeanDefinition());

    }

    
}
