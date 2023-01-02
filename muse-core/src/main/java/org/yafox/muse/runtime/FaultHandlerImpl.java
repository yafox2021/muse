package org.yafox.muse.runtime;

import java.util.Map;

import org.yafox.muse.Fault;
import org.yafox.muse.FaultHandler;

public class FaultHandlerImpl implements FaultHandler {

    private Map<String, Integer> faultCodeMap = null;

    private Map<String, String> faultMsgMap = null;

    private Map<String, String> faultExtMap = null;

    private boolean hideExceptionType = false;

    public Fault handle(Throwable t) {
        FaultImpl fault = new FaultImpl();
        fault.setCode(findCode(t));
        if (t instanceof Fault) {
            Fault config = (Fault) t;
            fault.setCode(config.getCode());
            fault.setMsg(config.getMsg());
            fault.setExt(config.getExt());
        } else {
            fault.setMsg(findMsg(t));
        }

        if (!hideExceptionType) {
            fault.setException(t.getClass().getName());
        }

        return fault;
    }

    @SuppressWarnings("rawtypes")
    protected Integer findCode(Throwable t) {
        Integer code = 1;
        Class type = t.getClass();

        while (!Object.class.equals(type)) {
            String className = type.getName();
            if (faultCodeMap.containsKey(className)) {
                code = faultCodeMap.get(className);
                break;
            } else {
                type = type.getSuperclass();
            }
        }

        return code;
    }

    protected String findMsg(Throwable t) {
        String msg = faultMsgMap.get(t.getClass().getName());
        if (msg == null) {
            msg = t.getMessage();
        }
        return msg;
    }

    protected String findExt(Throwable t) {
        return faultExtMap.get(t.getClass().getName());
    }

    public Map<String, Integer> getFaultCodeMap() {
        return faultCodeMap;
    }

    public void setFaultCodeMap(Map<String, Integer> faultCodeMap) {
        this.faultCodeMap = faultCodeMap;
    }

    public Map<String, String> getFaultMsgMap() {
        return faultMsgMap;
    }

    public void setFaultMsgMap(Map<String, String> faultMsgMap) {
        this.faultMsgMap = faultMsgMap;
    }

    public Map<String, String> getFaultExtMap() {
        return faultExtMap;
    }

    public void setFaultExtMap(Map<String, String> faultExtMap) {
        this.faultExtMap = faultExtMap;
    }

    public boolean isHideExceptionType() {
        return hideExceptionType;
    }

    public void setHideExceptionType(boolean hideExceptionType) {
        this.hideExceptionType = hideExceptionType;
    }

}
