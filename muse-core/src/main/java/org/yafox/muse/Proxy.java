package org.yafox.muse;

public interface Proxy extends ProxyHandler {

    public ProxyHandler getProxyHandler();

    public void setProxyHandler(ProxyHandler proxyHandler);
    
}
