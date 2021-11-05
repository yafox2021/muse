package org.yafox.muse.runtime;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yafox.muse.Dynamic;
import org.yafox.muse.Invoker;
import org.yafox.muse.InvokerBucket;
import org.yafox.muse.Launcher;
import org.yafox.muse.MuseInvoker;
import org.yafox.muse.Pallet;
import org.yafox.muse.annotation.Fn;
import org.yafox.muse.annotation.Svc;
import org.yafox.muse.assign.Assignment;
import org.yafox.muse.assign.AssignmentBuilder;
import org.yafox.muse.validate.Validation;
import org.yafox.muse.validate.ValidationBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MuseLauncher implements Launcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MuseLauncher.class);

    private Dynamic dynamic;

    private Pallet pallet;

    private InvokerBucket invokerBucket;
    
    @SuppressWarnings("rawtypes")
    public void launch() throws Exception {
        Map<String, Object> svcBeanMap = pallet.getBeansWithAnnotation(Svc.class);
        Set<Class> serviceInterfaceSet = buildServiceInterfaceSet(svcBeanMap.values());
        Map<String, Method> serviceMethodMap = buildServiceMethodMap(serviceInterfaceSet);
        Map<String, Class> invokerTypeMap = buildInvokerTypeMap(serviceMethodMap);

        Set<String> serviceIdSet = invokerTypeMap.keySet();
        Gson gson = new Gson();
        for (String id : serviceIdSet) {
            String configJsonString = pallet.getString("/rules" + id + ".json");
            if (configJsonString == null) {
                continue;
            }
            JsonObject jsonObject = gson.fromJson(configJsonString, JsonObject.class);
            Set<String> rules = jsonObject.keySet();
            Class invokerType = invokerTypeMap.get(id);
            for (String rule : rules) {
                JsonObject ruleJsonObject = jsonObject.get(rule).getAsJsonObject();
                String beanName = ruleJsonObject.get("beanName").getAsString();
                Object target = pallet.getBean(beanName);
                if (target == null) {
                    throw new Exception("error in file /rules" + id + ".json no bean found for name " + beanName);
                }
                AbstractInvoker invoker = (AbstractInvoker) invokerType.newInstance();
                invoker.setId(id);
                invoker.setTarget(target);
                Invoker wrapInvoker = wrap(invoker, ruleJsonObject);
                invokerBucket.addInvoker(id + "@" + rule, wrapInvoker);
            }
        }
    }

    private Invoker wrap(AbstractInvoker invoker, JsonObject jsonObject) throws Exception {
        MuseInvoker museInvoker = new MuseInvoker();
        museInvoker.setDelegate(invoker);
        
        JsonElement assignJsonElement = jsonObject.get("assignment");
        if (assignJsonElement != null && !assignJsonElement.isJsonNull()) {
            JsonObject assignmentJsonObject = assignJsonElement.getAsJsonObject();
            Assignment assignment = AssignmentBuilder.build(assignmentJsonObject, pallet);
            museInvoker.setAssignment(assignment);
        }
        
        JsonElement validateJsonElement = jsonObject.get("validation");
        if (validateJsonElement != null && !validateJsonElement.isJsonNull()) {
            JsonObject validateJsonObject = validateJsonElement.getAsJsonObject();
            Validation validation = ValidationBuilder.build(validateJsonObject, pallet);
            museInvoker.setValidation(validation);
        }
        
        JsonElement maskJsonElement = jsonObject.get("mask");
        if (maskJsonElement != null && !maskJsonElement.isJsonNull()) {
            JsonObject maskJsonObject = maskJsonElement.getAsJsonObject();
            Assignment mask = AssignmentBuilder.build(maskJsonObject, pallet);
            museInvoker.setMask(mask);
        }
        
        return museInvoker;
    }

    @SuppressWarnings("rawtypes")
    public Set<Class> buildServiceInterfaceSet(Iterable<Object> beans) throws Exception {
        Set<Class> svcInterfaceSet = new HashSet<Class>();

        for (Object bean : beans) {
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            for (Class<?> item : interfaces) {
                if (item.isAnnotationPresent(Svc.class)) {
                    svcInterfaceSet.add(item);
                    LOGGER.info("svc class " + item.getName());
                }
            }
        }
        return svcInterfaceSet;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Map<String, Method> buildServiceMethodMap(Set<Class> interfaceSet) throws Exception {

        Map<String, Method> methodMap = new HashMap<String, Method>();

        for (Class svcInterface : interfaceSet) {
            Svc svcAnnotation = (Svc) svcInterface.getAnnotation(Svc.class);
            String prefix = svcAnnotation.value();
            Method[] methods = svcInterface.getMethods();
            for (Method method : methods) {
                String suffix = method.getName();
                Fn fnAnnotation = method.getAnnotation(Fn.class);
                if (fnAnnotation != null && !"".equals(fnAnnotation.value())) {
                    suffix = fnAnnotation.value();
                }
                String methodId = buildInvokerTypeId(prefix, suffix);
                if (methodMap.containsKey(methodId)) {
                    Method originalMethod = methodMap.get(methodId);
                    throw new Exception("multiple method conflict by id " + methodId + "[" + method.getDeclaringClass().getName() + "#" + method.getName() + "," + originalMethod.getDeclaringClass().getName() + "#"
                            + originalMethod.getName() + "]");
                } else {
                    methodMap.put(methodId, method);
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("assign " + methodId + " to " + method.getDeclaringClass().getName() + "#" + method.getName());
                    }
                }
            }
        }

        return methodMap;
    }

    @SuppressWarnings("rawtypes")
    public Map<String, Class> buildInvokerTypeMap(Map<String, Method> serviceMethodMap) throws Exception {
        Map<String, Class> invokerTypeMap = new HashMap<String, Class>();
        Set<Entry<String, Method>> entrySet = serviceMethodMap.entrySet();
        for (Entry<String, Method> entry : entrySet) {
            String id = entry.getKey();
            Method method = entry.getValue();
            Class<?> invokerType = dynamic.getInvokerType(method);
            invokerTypeMap.put(id, invokerType);
        }
        return invokerTypeMap;
    }

    public String buildInvokerTypeId(String prefix, String suffix) {
        String temp = "/" + prefix + "/" + suffix;
        temp = temp.replaceAll("/+", "/");
        if (temp.endsWith("/")) {
            temp = temp.substring(0, temp.length() - 1);
        }
        return temp;
    }

    public Dynamic getDynamic() {
        return dynamic;
    }

    public void setDynamic(Dynamic dynamic) {
        this.dynamic = dynamic;
    }

    public Pallet getPallet() {
        return pallet;
    }

    public void setPallet(Pallet pallet) {
        this.pallet = pallet;
    }

    public InvokerBucket getInvokerBucket() {
        return invokerBucket;
    }

    public void setInvokerBucket(InvokerBucket invokerBucket) {
        this.invokerBucket = invokerBucket;
    }

}
