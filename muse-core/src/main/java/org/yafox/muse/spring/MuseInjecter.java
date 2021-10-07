package org.yafox.muse.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.yafox.muse.runtime.MuseDynamic;
import org.yafox.muse.runtime.MuseErrorHandler;
import org.yafox.muse.runtime.MuseInvokerBucket;
import org.yafox.muse.runtime.MuseLauncher;

public class MuseInjecter implements BeanDefinitionRegistryPostProcessor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger("muse");

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        
        BeanDefinitionBuilder methodImproverBuilder = BeanDefinitionBuilder.genericBeanDefinition(org.yafox.muse.runtime.MuseMethodImprover.class);
        methodImproverBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        
        BeanDefinitionBuilder dynamicBuilder = BeanDefinitionBuilder.genericBeanDefinition(MuseDynamic.class);
        dynamicBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        dynamicBuilder.addPropertyReference("methodImprover", "museMethodImprover");
        
        BeanDefinitionBuilder palletBuilder = BeanDefinitionBuilder.genericBeanDefinition(MusePallet.class);
        palletBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        
        BeanDefinitionBuilder invokerBucketBuilder = BeanDefinitionBuilder.genericBeanDefinition(MuseInvokerBucket.class);
        invokerBucketBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        
        BeanDefinitionBuilder errorHandlerBuilder = BeanDefinitionBuilder.genericBeanDefinition(MuseErrorHandler.class);
        errorHandlerBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        errorHandlerBuilder.setInitMethodName("init");
        
        BeanDefinitionBuilder launcherBuilder = BeanDefinitionBuilder.genericBeanDefinition(MuseLauncher.class);
        launcherBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
        launcherBuilder.setInitMethodName("launch");
        launcherBuilder.addPropertyReference("dynamic", "museDynamic");
        launcherBuilder.addPropertyReference("pallet", "musePallet");
        launcherBuilder.addPropertyReference("invokerBucket", "museInvokerBucket");
        
        BeanDefinitionBuilder museProxyRegistryBuilder = BeanDefinitionBuilder.genericBeanDefinition(MuseProxyRegistry.class);
        museProxyRegistryBuilder.addPropertyReference("dynamic", "museDynamic");
        
        
        registry.registerBeanDefinition("museMethodImprover", methodImproverBuilder.getBeanDefinition());
        registry.registerBeanDefinition("museDynamic", dynamicBuilder.getBeanDefinition());
        registry.registerBeanDefinition("musePallet", palletBuilder.getBeanDefinition());
        registry.registerBeanDefinition("museInvokerBucket", invokerBucketBuilder.getBeanDefinition());
        registry.registerBeanDefinition("museErrorHandler", errorHandlerBuilder.getBeanDefinition());
        registry.registerBeanDefinition("museLauncher", launcherBuilder.getBeanDefinition());
        registry.registerBeanDefinition("museProxyRegistry", museProxyRegistryBuilder.getBeanDefinition());
        
        String logo = "\r\n" + 
                "      ___           ___           ___           ___     \r\n" + 
                "     /\\__\\         /\\__\\         /\\  \\         /\\  \\    \r\n" + 
                "    /::|  |       /:/  /        /::\\  \\       /::\\  \\   \r\n" + 
                "   /:|:|  |      /:/  /        /:/\\ \\  \\     /:/\\:\\  \\  \r\n" + 
                "  /:/|:|__|__   /:/  /  ___   _\\:\\~\\ \\  \\   /::\\~\\:\\  \\ \r\n" + 
                " /:/ |::::\\__\\ /:/__/  /\\__\\ /\\ \\:\\ \\ \\__\\ /:/\\:\\ \\:\\__\\\r\n" + 
                " \\/__/~~/:/  / \\:\\  \\ /:/  / \\:\\ \\:\\ \\/__/ \\:\\~\\:\\ \\/__/\r\n" + 
                "       /:/  /   \\:\\  /:/  /   \\:\\ \\:\\__\\    \\:\\ \\:\\__\\  \r\n" + 
                "      /:/  /     \\:\\/:/  /     \\:\\/:/  /     \\:\\ \\/__/  \r\n" + 
                "     /:/  /       \\::/  /       \\::/  /       \\:\\__\\    \r\n" + 
                "     \\/__/         \\/__/         \\/__/         \\/__/    \r\n" + 
                "";
        LOGGER.info(logo);
    }

    
}
