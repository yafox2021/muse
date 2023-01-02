package org.yafox.muse.runtime;

import java.io.Closeable;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.yafox.muse.FaultHandler;

public class FaultHandlerFactory {

    private String configName = "fault";

    private boolean hideExceptionType = false;

    public FaultHandler createFaultHandler() throws Exception {

        Map<String, Integer> faultCodeMap = new HashMap<String, Integer>();
        Map<String, String> faultMsgMap = new HashMap<String, String>();
        Map<String, String> faultExtMap = new HashMap<String, String>();

        Map<String, String> codeMap = loadMapFromProperties(configName + "-code.properties");
        Set<Entry<String, String>> entrySet = codeMap.entrySet();
        for (Entry<String, String> entry : entrySet) {
            faultCodeMap.put(entry.getKey(), Integer.valueOf(entry.getValue()));
        }

        faultMsgMap = loadMapFromProperties(configName + "-msg.properties");
        faultExtMap = loadMapFromProperties(configName + "-ext.properties");

        FaultHandlerImpl faultHandler = new FaultHandlerImpl();

        faultHandler.setFaultCodeMap(faultCodeMap);
        faultHandler.setFaultMsgMap(faultMsgMap);
        faultHandler.setFaultExtMap(faultExtMap);

        return faultHandler;
    }

    protected Map<String, String> loadMapFromProperties(String fileName) throws Exception {
        ClassLoader classLoader = FaultHandlerFactory.class.getClassLoader();
        Enumeration<URL> resources = classLoader.getResources(fileName);
        Map<String, String> result = new HashMap<String, String>();

        while (resources.hasMoreElements()) {
            URL url = (URL) resources.nextElement();
            InputStream inputStream = null;
            try {
                inputStream = url.openStream();
                Properties properties = new Properties();
                properties.load(inputStream);
                Set<String> names = properties.stringPropertyNames();
                for (String name : names) {
                    String property = properties.getProperty(name);
                    result.put(name, property.trim());
                }
            } finally {
                close(inputStream);
            }
        }

        return result;
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
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
