package org.yafox.muse.spring;

import org.springframework.beans.factory.FactoryBean;
import org.yafox.muse.FaultHandler;
import org.yafox.muse.runtime.FaultHandlerFactory;

public class FaultHandlerFactoryBean implements FactoryBean<FaultHandler> {

    private String configName = "fault";

    private boolean hideExceptionType = false;
    
    @Override
    public FaultHandler getObject() throws Exception {
        FaultHandlerFactory factory = new FaultHandlerFactory();
        factory.setConfigName(configName);
        factory.setHideExceptionType(hideExceptionType);
        return factory.createFaultHandler();
    }

    @Override
    public Class<?> getObjectType() {
        return FaultHandler.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public boolean isHideExceptionType() {
        return hideExceptionType;
    }

    public void setHideExceptionType(boolean hideExceptionType) {
        this.hideExceptionType = hideExceptionType;
    }

}
