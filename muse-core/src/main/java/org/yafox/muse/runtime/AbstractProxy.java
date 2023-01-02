package org.yafox.muse.runtime;

import java.lang.reflect.Method;

import org.yafox.muse.Proxy;
import org.yafox.muse.ProxyHandler;

public abstract class AbstractProxy implements Proxy {

    private ProxyHandler proxyHandler;

    private String name;

    public Object handle(String name, String spi, Method method, Object input, Class<?> responseType) throws Exception {
        if (this.name != null) {
            name = this.name;
        }

        if (proxyHandler != null) {
            return proxyHandler.handle(name, spi, method, input, responseType);
        }

        return responseType.getDeclaredConstructor().newInstance();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProxyHandler getProxyHandler() {
        return proxyHandler;
    }

    public void setProxyHandler(ProxyHandler proxyHandler) {
        this.proxyHandler = proxyHandler;
    }

}
