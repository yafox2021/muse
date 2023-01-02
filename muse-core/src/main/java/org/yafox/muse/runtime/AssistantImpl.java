package org.yafox.muse.runtime;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.yafox.muse.Assistant;
import org.yafox.muse.annotation.Param;

public class AssistantImpl implements Assistant {

    public String[] parameterNames(Method method) throws Exception {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        int length = parameterTypes.length;
        String[] parameterNames = new String[length];
        Set<String> checkSet = new HashSet<String>();
        for (int i = 0; i < length; i++) {
            parameterNames[i] = "arg" + i;
            Param paramAnnotation = findParamAnnotation(parameterAnnotations[i]);
            if (paramAnnotation != null && !"".equals(paramAnnotation.value().trim())) {
                parameterNames[i] = paramAnnotation.value().trim();
            }
            if (checkSet.contains(parameterNames[i])) {
                throw new Exception("param name " + parameterNames[i] + " conflict in " + method.getDeclaringClass().getName() + "#" + method.getName());
            }
            checkSet.add(parameterNames[i]);
        }
        return parameterNames;
    }

    public String[] parameterAlisNames(Method method) throws Exception {
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        int length = parameterTypes.length;
        String[] parameterAlisNames = new String[length];
        Set<String> checkSet = new HashSet<String>();
        for (int i = 0; i < length; i++) {
            parameterAlisNames[i] = "arg" + i;
            Param paramAnnotation = findParamAnnotation(parameterAnnotations[i]);
            if (paramAnnotation != null) {
                String alis = paramAnnotation.alis().trim();
                String name = paramAnnotation.value().trim();
                if (!"".equals(alis)) {
                    parameterAlisNames[i] = alis;
                } else if (!"".equals(name)) {
                    parameterAlisNames[i] = name;
                }

            }
            if (checkSet.contains(parameterAlisNames[i])) {
                throw new Exception("param alis name " + parameterAlisNames[i] + " conflict in " + method.getDeclaringClass().getName() + "#" + method.getName());
            }
            checkSet.add(parameterAlisNames[i]);
        }
        return parameterAlisNames;
    }

    protected Param findParamAnnotation(Annotation[] annotations) {
        Param param = null;
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof Param) {
                    param = (Param) annotation;
                }
            }
        }
        return param;
    }

    public String spi(Method method) throws Exception {
        return method.getName();
    }

}
