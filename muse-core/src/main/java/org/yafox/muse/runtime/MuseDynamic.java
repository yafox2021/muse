package org.yafox.muse.runtime;

import static org.objectweb.asm52.Opcodes.AASTORE;
import static org.objectweb.asm52.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm52.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm52.Opcodes.ACC_STATIC;
import static org.objectweb.asm52.Opcodes.ACC_SUPER;
import static org.objectweb.asm52.Opcodes.ALOAD;
import static org.objectweb.asm52.Opcodes.ANEWARRAY;
import static org.objectweb.asm52.Opcodes.ARETURN;
import static org.objectweb.asm52.Opcodes.ASTORE;
import static org.objectweb.asm52.Opcodes.BIPUSH;
import static org.objectweb.asm52.Opcodes.CHECKCAST;
import static org.objectweb.asm52.Opcodes.DUP;
import static org.objectweb.asm52.Opcodes.GETFIELD;
import static org.objectweb.asm52.Opcodes.GETSTATIC;
import static org.objectweb.asm52.Opcodes.ILOAD;
import static org.objectweb.asm52.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm52.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm52.Opcodes.INVOKESTATIC;
import static org.objectweb.asm52.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm52.Opcodes.IRETURN;
import static org.objectweb.asm52.Opcodes.NEW;
import static org.objectweb.asm52.Opcodes.POP;
import static org.objectweb.asm52.Opcodes.PUTFIELD;
import static org.objectweb.asm52.Opcodes.PUTSTATIC;
import static org.objectweb.asm52.Opcodes.RETURN;
import static org.objectweb.asm52.Opcodes.V1_5;
import static org.objectweb.asm52.Opcodes.ACONST_NULL;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm52.AnnotationVisitor;
import org.objectweb.asm52.ClassWriter;
import org.objectweb.asm52.FieldVisitor;
import org.objectweb.asm52.MethodVisitor;
import org.objectweb.asm52.Type;
import org.yafox.muse.Bindable;
import org.yafox.muse.Dynamic;
import org.yafox.muse.Enumable;
import org.yafox.muse.MethodImprover;
import org.yafox.muse.annotation.Mark;

public class MuseDynamic extends ClassLoader implements Dynamic {

    public static final String SUFFIX_INVOKER = ".Invoker";

    public static final String SUFFIX_REQUEST = ".Request";

    public static final String SUFFIX_RESPONSE = ".Response";

    public static final String SUFFIX_PROXY = ".Proxy";

    private MethodImprover methodImprover;

    public Class<?> getProxyType(Class<?> interfaceType) throws Exception {
        return loadClass(interfaceType.getName() + SUFFIX_PROXY);
    }

    public Class<?> getInvokerType(Method method) throws Exception {
        return loadClass(methodToPackage(method) + SUFFIX_INVOKER);
    }
    
    public Class<?> getRequestType(Method method) throws Exception {
        return loadClass(methodToPackage(method) + SUFFIX_REQUEST);
    }
    
    public Class<?> getResponseType(Method method) throws Exception {
        return loadClass(methodToPackage(method) + SUFFIX_RESPONSE);
    }

    protected byte[] dumpRequestType(String name) throws Exception {
        String methodPackage = name.substring(0, name.lastIndexOf("."));
        Method method = resolveMethod(methodPackage);
        return dumpRequestType(method);
    }

    protected byte[] dumpResponseType(String name) throws Exception {
        String serviceId = name.substring(0, name.lastIndexOf("."));
        Method method = resolveMethod(serviceId);
        return dumpResponseType(method);
    }

    protected byte[] dumpInvokerType(String name) throws Exception {
        String methodPackage = name.substring(0, name.lastIndexOf("."));
        Method method = resolveMethod(methodPackage);
        return dumpInvokerType(method);
    }

    protected byte[] dumpProxyType(String name) throws Exception {
        String targetTypeName = name.substring(0, name.lastIndexOf("."));
        return dumpProxyType(loadClass(targetTypeName));
    }

    protected byte[] dumpRequestType(Method method) throws Exception {
        String methodPackage = methodToPackage(method);
        String typeName = toInternalName(methodPackage + SUFFIX_REQUEST);
        String superTypeName = "java/lang/Object";

        ClassWriter cw = createClassWriter();
        MethodVisitor mv = null;
        FieldVisitor fv = null;
        AnnotationVisitor av = null;

        cw.visit(V1_5, ACC_PUBLIC, typeName, null, superTypeName, new String[] { Type.getInternalName(Enumable.class) });

        java.lang.reflect.Type[] genericFieldTypes = method.getGenericParameterTypes();
        Class<?>[] fieldTypes = method.getParameterTypes();
        String[] fieldNames = parameterNames(method);
        String[] fieldAlisNames = parameterAlisNames(method);

        String markDescriptor = Type.getDescriptor(Mark.class);

        // 生成请求的字段
        for (int i = 0; i < fieldTypes.length; i++) {
            Class<?> fieldType = fieldTypes[i];
            String descriptor = Type.getDescriptor(fieldType);
            String signature = toSignature(genericFieldTypes[i].toString());
            fv = cw.visitField(ACC_PRIVATE, fieldNames[i], descriptor, signature, null);
            if (fieldAlisNames[i] != null) {
                av = fv.visitAnnotation(markDescriptor, true);
                av.visit("value", fieldAlisNames[i]);
                fv.visitEnd();
            }

        }

        // 生成构造函数
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superTypeName, "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // 生成GET SET
        for (int i = 0; i < fieldTypes.length; i++) {
            String fieldName = fieldNames[i];
            String fieldTypeDesc = Type.getDescriptor(fieldTypes[i]);
            String fieldSignature = toSignature(genericFieldTypes[i].toString());

            // GET
            String getSignature = (fieldSignature == null) ? null : ("()" + fieldSignature);
            mv = cw.visitMethod(ACC_PUBLIC, "get" + upperFirst(fieldName), "()" + fieldTypeDesc, getSignature, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, typeName, fieldName, fieldTypeDesc);
            mv.visitInsn(xReturn(fieldTypeDesc));
            mv.visitMaxs(0, 0);
            mv.visitEnd();

            // SET
            String setSignature = (fieldSignature == null) ? null : ("(" + fieldSignature + ")V");
            mv = cw.visitMethod(ACC_PUBLIC, "set" + upperFirst(fieldName), "(" + fieldTypeDesc + ")V", setSignature, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);

            mv.visitVarInsn(xLoad(fieldTypeDesc), 1);
            mv.visitFieldInsn(PUTFIELD, typeName, fieldName, fieldTypeDesc);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();

        }

        mv = cw.visitMethod(ACC_PUBLIC, "names", "()[Ljava/lang/String;", null, null);
        mv.visitIntInsn(BIPUSH, fieldTypes.length);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/String");
        for (int i = 0; i < fieldTypes.length; i++) {
            String fieldName = fieldNames[i];
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, i);
            mv.visitLdcInsn(fieldName);
            mv.visitInsn(AASTORE);
        }
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        mv = cw.visitMethod(ACC_PUBLIC, "values", "()[Ljava/lang/Object;", null, null);
        mv.visitIntInsn(BIPUSH, fieldTypes.length);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
        for (int i = 0; i < fieldTypes.length; i++) {
            String fieldName = fieldNames[i];
            String fieldTypeDesc = Type.getDescriptor(fieldTypes[i]);
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, i);
            mv.visitIntInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, typeName, fieldName, fieldTypeDesc);
            mv.visitInsn(AASTORE);
        }
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        cw.visitEnd();
        return cw.toByteArray();
    }

    public byte[] dumpResponseType(Method method) throws Exception {
        String methodPackage = methodToPackage(method);
        String typeName = toInternalName(methodPackage + SUFFIX_RESPONSE);
        String superTypeName = "java/lang/Object";

        java.lang.reflect.Type genericReturnType = method.getGenericReturnType();
        Class<?> returnType = method.getReturnType();

        ClassWriter cw = createClassWriter();
        MethodVisitor mv = null;

        cw.visit(V1_5, ACC_PUBLIC, typeName, null, superTypeName, new String[] { Type.getInternalName(Bindable.class) });

        cw.visitField(ACC_PRIVATE, "code", "I", null, null).visitEnd();

        boolean isVoidReturn = Void.TYPE.equals(returnType);
        if (!isVoidReturn) {
            String decriptor = Type.getDescriptor(returnType);
            String signature = toSignature(genericReturnType.toString());
            cw.visitField(ACC_PRIVATE, "data", decriptor, signature, null).visitEnd();
        }

        // 生成构造函数
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superTypeName, "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // getCode
        mv = cw.visitMethod(ACC_PUBLIC, "getCode", "()I", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, typeName, "code", "I");
        mv.visitInsn(IRETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // setCode
        mv = cw.visitMethod(ACC_PUBLIC, "setCode", "(I)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ILOAD, 1);
        mv.visitFieldInsn(PUTFIELD, typeName, "code", "I");
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        String fieldTypeDesc = Type.getDescriptor(returnType);
        String fieldSignature = toSignature(genericReturnType.toString());

        if (!isVoidReturn) {

            // getData
            String getSignature = (fieldSignature == null) ? null : ("()" + fieldSignature);
            mv = cw.visitMethod(ACC_PUBLIC, "getData", "()" + fieldTypeDesc, getSignature, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, typeName, "data", fieldTypeDesc);
            mv.visitInsn(xReturn(fieldTypeDesc));
            mv.visitMaxs(0, 0);
            mv.visitEnd();

            // setData
            String setSignature = (fieldSignature == null) ? null : ("(" + fieldSignature + ")V");
            mv = cw.visitMethod(ACC_PUBLIC, "setData", "(" + fieldTypeDesc + ")V", setSignature, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(xLoad(fieldTypeDesc), 1);
            mv.visitFieldInsn(PUTFIELD, typeName, "data", fieldTypeDesc);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        mv = cw.visitMethod(ACC_PUBLIC, "bind", "(Ljava/lang/Object;)V", null, null);
        mv.visitCode();
        if (!isVoidReturn) {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);

            if (Integer.TYPE.equals(returnType)) {
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(Integer.class));
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            } else if (Byte.TYPE.equals(returnType)) {
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(Byte.class));
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
            } else if (Short.TYPE.equals(returnType)) {
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(Short.class));
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
            } else if (Long.TYPE.equals(returnType)) {
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(Long.class));
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            } else if (Float.TYPE.equals(returnType)) {
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(Float.class));
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
            } else if (Double.TYPE.equals(returnType)) {
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(Double.class));
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            } else if (Boolean.TYPE.equals(returnType)) {
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(Boolean.class));
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            } else if (Character.TYPE.equals(returnType)) {
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(Character.class));
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
            } else {
                mv.visitTypeInsn(CHECKCAST, Type.getInternalName(returnType));
            }

            mv.visitFieldInsn(PUTFIELD, typeName, "data", fieldTypeDesc);
        }
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        
        mv = cw.visitMethod(ACC_PUBLIC, "value", "()Ljava/lang/Object;", null, null);
        mv.visitCode();
        if (isVoidReturn) {
            mv.visitInsn(ACONST_NULL);
        } else {
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, typeName, "data", fieldTypeDesc);
            
            if (Integer.TYPE.equals(returnType)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            } else if (Byte.TYPE.equals(returnType)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
            } else if (Short.TYPE.equals(returnType)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
            } else if (Long.TYPE.equals(returnType)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
            } else if (Float.TYPE.equals(returnType)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            } else if (Double.TYPE.equals(returnType)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
            } else if (Boolean.TYPE.equals(returnType)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            } else if (Character.TYPE.equals(returnType)) {
                mv.visitMethodInsn(INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
            }
        }
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        
        

        cw.visitEnd();
        return cw.toByteArray();
    }

    public byte[] dumpInvokerType(Method method) throws Exception {
        Class<?> serviceType = method.getDeclaringClass();
        String methodPackage = methodToPackage(method);
        String typeName = toInternalName(methodPackage + SUFFIX_INVOKER);
        String superTypeName = Type.getInternalName(AbstractInvoker.class);

        String targetTypeName = Type.getInternalName(serviceType);
        String targetTypeDesc = Type.getDescriptor(serviceType);

        String requestTypeName = toInternalName((methodPackage + SUFFIX_REQUEST));
        String requestTypeDesc = "L" + requestTypeName + ";";

        String responseTypeName = toInternalName((methodPackage + SUFFIX_RESPONSE));
        String responseTypeDesc = "L" + responseTypeName + ";";

        ClassWriter cw = createClassWriter();
        FieldVisitor fv = null;
        MethodVisitor mv = null;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, typeName, null, superTypeName, null);

        // 生成委托字段
        fv = cw.visitField(ACC_PRIVATE, "target", targetTypeDesc, null, null);
        fv.visitEnd();

        // 生成构造函数
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superTypeName, "<init>", "()V", false);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(Type.getType(serviceType));
        mv.visitLdcInsn(method.getName());
        Class<?>[] parameterTypes = method.getParameterTypes();
        int parameterLength = parameterTypes.length;

        mv.visitIntInsn(BIPUSH, parameterLength);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");

        for (int i = 0; i < parameterLength; i++) {
            mv.visitInsn(DUP);
            mv.visitIntInsn(BIPUSH, i);
            Class<?> parameterType = parameterTypes[i];
            if (Integer.TYPE.equals(parameterType)) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
            } else if (Long.TYPE.equals(parameterType)) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
            } else if (Float.TYPE.equals(parameterType)) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
            } else if (Double.TYPE.equals(parameterType)) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
            } else if (Byte.TYPE.equals(parameterType)) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
            } else if (Short.TYPE.equals(parameterType)) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
            } else if (Character.TYPE.equals(parameterType)) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
            } else if (Boolean.TYPE.equals(parameterType)) {
                mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
            } else {
                mv.visitLdcInsn(Type.getType(parameterType));
            }

            mv.visitInsn(AASTORE);
        }

        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
        mv.visitFieldInsn(PUTFIELD, typeName, "method", "Ljava/lang/reflect/Method;");

        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // 生成setTarget方法
        mv = cw.visitMethod(ACC_PUBLIC, "setTarget", "(Ljava/lang/Object;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, targetTypeName);
        mv.visitFieldInsn(PUTFIELD, typeName, "target", targetTypeDesc);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // 生成getRequestType方法
        mv = cw.visitMethod(ACC_PUBLIC, "getRequestType", "()Ljava/lang/Class;", "()Ljava/lang/Class<*>;", null);
        mv.visitCode();
        mv.visitLdcInsn(Type.getType(requestTypeDesc));
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // 生成getRequestType方法
        mv = cw.visitMethod(ACC_PUBLIC, "getResponseType", "()Ljava/lang/Class;", "()Ljava/lang/Class<*>;", null);
        mv.visitCode();
        mv.visitLdcInsn(Type.getType(responseTypeDesc));
        mv.visitInsn(ARETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        // 处理逻辑invoke
        String[] parameterNames = parameterNames(method);
        Class<?> returnType = method.getReturnType();

        mv = cw.visitMethod(ACC_PUBLIC, "invoke", "(Ljava/lang/Object;)Ljava/lang/Object;", null, new String[] { "java/lang/Exception" });
        mv.visitCode();

        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, requestTypeName);
        mv.visitVarInsn(ASTORE, 2);

        mv.visitTypeInsn(NEW, responseTypeName);
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, responseTypeName, "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 3);

        mv.visitVarInsn(ALOAD, 3);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, typeName, "target", targetTypeDesc);

        if (parameterTypes.length > 0) {
            for (int i = 0; i < parameterTypes.length; i++) {
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKEVIRTUAL, requestTypeName, "get" + upperFirst(parameterNames[i]), "()" + Type.getDescriptor(parameterTypes[i]), false);
            }
        }

        boolean interfaceFlag = serviceType.isInterface();
        mv.visitMethodInsn(interfaceFlag ? INVOKEINTERFACE : INVOKEVIRTUAL, targetTypeName, method.getName(), Type.getMethodDescriptor(method), interfaceFlag);

        if (!Void.TYPE.equals(returnType)) {
            mv.visitMethodInsn(INVOKEVIRTUAL, responseTypeName, "setData", "(" + Type.getDescriptor(returnType) + ")V", false);
            mv.visitVarInsn(ALOAD, 3);
        }

        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }

    public byte[] dumpProxyType(Class<?> serviceType) throws Exception {

        String serviceTypeName = serviceType.getName();

        String typeName = toInternalName(serviceTypeName + SUFFIX_PROXY);
        String superTypeName = Type.getInternalName(AbstractProxy.class);

        ClassWriter cw = createClassWriter();
        MethodVisitor mv = null;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, typeName, null, superTypeName, new String[] { Type.getInternalName(serviceType) });

        Method[] methods = serviceType.getMethods();
        int methodLength = methods.length;

        String methodTypeDescriptor = Type.getDescriptor(Method.class);
        for (int i = 0; i < methodLength; i++) {
            cw.visitField(ACC_PRIVATE + ACC_STATIC, "M" + i, methodTypeDescriptor, null, null).visitEnd();
        }

        mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        mv.visitCode();

        for (int i = 0; i < methodLength; i++) {
            Method method = methods[i];
            mv.visitLdcInsn(Type.getType(serviceType));
            mv.visitLdcInsn(method.getName());
            Class<?>[] parameterTypes = method.getParameterTypes();
            int parameterLength = parameterTypes.length;

            mv.visitIntInsn(BIPUSH, parameterLength);
            mv.visitTypeInsn(ANEWARRAY, "java/lang/Class");

            for (int j = 0; j < parameterLength; j++) {
                mv.visitInsn(DUP);
                mv.visitIntInsn(BIPUSH, j);
                Class<?> parameterType = parameterTypes[j];
                if (Integer.TYPE.equals(parameterType)) {
                    mv.visitFieldInsn(GETSTATIC, "java/lang/Integer", "TYPE", "Ljava/lang/Class;");
                } else if (Long.TYPE.equals(parameterType)) {
                    mv.visitFieldInsn(GETSTATIC, "java/lang/Long", "TYPE", "Ljava/lang/Class;");
                } else if (Float.TYPE.equals(parameterType)) {
                    mv.visitFieldInsn(GETSTATIC, "java/lang/Float", "TYPE", "Ljava/lang/Class;");
                } else if (Double.TYPE.equals(parameterType)) {
                    mv.visitFieldInsn(GETSTATIC, "java/lang/Double", "TYPE", "Ljava/lang/Class;");
                } else if (Byte.TYPE.equals(parameterType)) {
                    mv.visitFieldInsn(GETSTATIC, "java/lang/Byte", "TYPE", "Ljava/lang/Class;");
                } else if (Short.TYPE.equals(parameterType)) {
                    mv.visitFieldInsn(GETSTATIC, "java/lang/Short", "TYPE", "Ljava/lang/Class;");
                } else if (Character.TYPE.equals(parameterType)) {
                    mv.visitFieldInsn(GETSTATIC, "java/lang/Character", "TYPE", "Ljava/lang/Class;");
                } else if (Boolean.TYPE.equals(parameterType)) {
                    mv.visitFieldInsn(GETSTATIC, "java/lang/Boolean", "TYPE", "Ljava/lang/Class;");
                } else {
                    mv.visitLdcInsn(Type.getType(parameterType));
                }

                mv.visitInsn(AASTORE);
            }

            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            mv.visitFieldInsn(PUTSTATIC, typeName, "M" + i, "Ljava/lang/reflect/Method;");
        }

        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // 生成构造函数
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superTypeName, "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        for (int methodIndex = 0; methodIndex < methodLength; methodIndex++) {
            Method method = methods[methodIndex];
            String methodPackage = methodToPackage(method);

            String methodName = method.getName();

            String requestTypeName = toInternalName((methodPackage + SUFFIX_REQUEST));

            String responseTypeName = toInternalName((methodPackage + SUFFIX_RESPONSE));
            String responseTypeDesc = "L" + responseTypeName + ";";

            String methodDescriptor = Type.getMethodDescriptor(method);
            Class<?>[] exceptionTypes = method.getExceptionTypes();
            String[] exceptions = null;

            if (exceptionTypes != null && exceptionTypes.length > 0) {
                exceptions = new String[exceptionTypes.length];
                for (int i = 0; i < exceptionTypes.length; i++) {
                    exceptions[i] = Type.getInternalName(exceptionTypes[i]);
                }
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            String[] parameterNames = parameterNames(method);
            List<TypeAndIndex> parameterTypeAndIndexList = new ArrayList<TypeAndIndex>();

            int offset = 1;
            for (int i = 0; i < parameterTypes.length; i++) {
                String paramTypeDesc = Type.getDescriptor(parameterTypes[i]);

                TypeAndIndex typeAndIndex = new TypeAndIndex();
                typeAndIndex.setIndex(i + offset);
                typeAndIndex.setType(paramTypeDesc);
                typeAndIndex.setName(parameterNames[i]);
                typeAndIndex.setXload(xLoad(paramTypeDesc));
                typeAndIndex.setXstore(xStore(paramTypeDesc));
                typeAndIndex.setXreturn(xReturn(paramTypeDesc));
                parameterTypeAndIndexList.add(typeAndIndex);

                if ("J".equals(paramTypeDesc) || "D".equals(paramTypeDesc)) {
                    offset++;
                }
            }

            TypeAndIndex requestTypeAndIndex = new TypeAndIndex();
            requestTypeAndIndex.setIndex(parameterNames.length + offset);
            requestTypeAndIndex.setType(requestTypeName);

            mv = cw.visitMethod(ACC_PUBLIC, methodName, methodDescriptor, toSignature(methodDescriptor), exceptions);
            mv.visitCode();

            mv.visitTypeInsn(NEW, requestTypeAndIndex.getType());
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, requestTypeAndIndex.getType(), "<init>", "()V", false);
            mv.visitVarInsn(ASTORE, requestTypeAndIndex.getIndex());

            for (TypeAndIndex parameterTypeAndIndex : parameterTypeAndIndexList) {
                mv.visitVarInsn(ALOAD, requestTypeAndIndex.getIndex());
                mv.visitVarInsn(parameterTypeAndIndex.getXload(), parameterTypeAndIndex.getIndex());
                mv.visitMethodInsn(INVOKEVIRTUAL, requestTypeAndIndex.getType(), "set" + upperFirst(parameterTypeAndIndex.getName()), "(" + parameterTypeAndIndex.getType() + ")V", false);
            }

            Class<?> returnType = method.getReturnType();
            String returnTypeDescriptor = Type.getDescriptor(returnType);

            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(methodPackage);
            mv.visitLdcInsn(methodId(method));
            mv.visitFieldInsn(GETSTATIC, typeName, "M" + methodIndex, "Ljava/lang/reflect/Method;");

            mv.visitVarInsn(ALOAD, requestTypeAndIndex.getIndex());
            mv.visitLdcInsn(Type.getType(responseTypeDesc));
            mv.visitMethodInsn(INVOKEVIRTUAL, typeName, "handle", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/reflect/Method;Ljava/lang/Object;Ljava/lang/Class;)Ljava/lang/Object;", false);

            if (Void.TYPE.equals(returnType)) {
                mv.visitInsn(POP);
                mv.visitInsn(RETURN);
            } else {
                mv.visitTypeInsn(CHECKCAST, responseTypeName);
                mv.visitMethodInsn(INVOKEVIRTUAL, responseTypeName, "getData", "()" + returnTypeDescriptor, false);
                String descriptor = Type.getDescriptor(returnType);
                if (descriptor.length() != 1) {
                    mv.visitTypeInsn(CHECKCAST, Type.getInternalName(returnType));
                }
                mv.visitInsn(xReturn(returnTypeDescriptor));
            }

            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }

        return cw.toByteArray();
    }

    protected Method resolveMethod(String methodPackage) throws Exception {
        String[] parts = splitMethodPackage(methodPackage);
        Method method = null;
        Class<?> classType = loadClass(parts[0]);
        Method[] declaredMethods = classType.getDeclaredMethods();
        for (Method item : declaredMethods) {
            String methodName = item.getName();

            if (!parts[1].equals(methodName)) {
                continue;
            }

            String hashStr = hash(Type.getMethodDescriptor(item));
            if (parts[2].equals(hashStr)) {
                method = item;
                break;
            }
        }
        return method;
    }

    private String toInternalName(String name) {
        return name.replace(".", "/");
    }

    protected String methodToPackage(Method method) {
        Class<?> declaringClass = method.getDeclaringClass();
        String methodName = method.getName();
        String methodDescriptor = Type.getMethodDescriptor(method);

        StringBuilder builder = new StringBuilder();
        builder.append(declaringClass.getName());
        builder.append(".");
        builder.append(methodName);
        builder.append(".");
        builder.append(hash(methodDescriptor));
        return builder.toString();
    }

    protected String[] splitMethodPackage(String methodPackage) {
        String text = methodPackage;
        int dotPos = 0;
        String[] parts = new String[3];

        dotPos = text.lastIndexOf('.');
        parts[2] = text.substring(dotPos + 1);

        text = text.substring(0, dotPos);
        dotPos = text.lastIndexOf('.');
        parts[1] = text.substring(dotPos + 1);
        parts[0] = text.substring(0, dotPos);

        return parts;
    }

    protected String hash(String text) {
        StringBuilder builder = new StringBuilder();
        int h = 0;
        int l = 0;
        char val[] = text.toCharArray();

        int len = val.length;
        for (int i = 0; i < len; i++) {
            h = 31 * h + val[i];
            l = 31 * l + val[(i + 1) % len];
        }

        if (l < 0) {
            l = -l;
        }

        if (h < 0) {
            h = -h;
        }
        builder.append("v");
        builder.append(len);
        builder.append("h");
        builder.append(Integer.toString(h, 36));
        builder.append(Integer.toString(l, 36));

        return builder.toString();
    }

    protected ClassWriter createClassWriter() {
        return new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    }

    protected String toSignature(String text) {
        if (text.indexOf("<") == -1) {
            return null;
        }
        text = text.replace(" ", "");
        StringBuilder builder = new StringBuilder();
        builder.append("L");
        char[] charArray = text.toCharArray();
        for (char c : charArray) {
            if ('.' == c) {
                builder.append('/');
            } else if ('<' == c) {
                builder.append(c);
                builder.append('L');
            } else if (',' == c) {
                builder.append(';');
                builder.append('L');
            } else if ('>' == c) {
                builder.append(';');
                builder.append(c);
            } else {
                builder.append(c);
            }
        }
        builder.append(';');
        return builder.toString();
    }

    protected String upperFirst(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    /**
     * @param fieldTypeDesc
     * @return
     */
    protected int xLoad(String fieldTypeDesc) {
        if (fieldTypeDesc.length() == 1) {
            return VmOps.valueOf(fieldTypeDesc).xLoad;
        }
        return VmOps.A.xLoad;
    }

    /**
     * @param fieldTypeDesc
     * @return
     */
    protected int xReturn(String fieldTypeDesc) {
        if (fieldTypeDesc.length() == 1) {
            return VmOps.valueOf(fieldTypeDesc).xReturn;
        }
        return VmOps.A.xReturn;
    }

    /**
     * @param fieldTypeDesc
     * @return
     */
    protected int xStore(String fieldTypeDesc) {
        if (fieldTypeDesc.length() == 1) {
            return VmOps.valueOf(fieldTypeDesc).xStore;
        }
        return VmOps.A.xStore;
    }

    private String[] parameterNames(Method method) throws Exception {
        return methodImprover.parameterNames(method);
    }

    private String[] parameterAlisNames(Method method) throws Exception {
        return methodImprover.parameterAlisNames(method);
    }

    private String methodId(Method method) throws Exception {
        String methodId = methodImprover.methodId(method);
        return methodId == null ? "" : methodId;
    }

    public MethodImprover getMethodImprover() {
        return methodImprover;
    }

    public void setMethodImprover(MethodImprover methodImprover) {
        this.methodImprover = methodImprover;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            byte[] bs = null;
            if (name.endsWith(SUFFIX_INVOKER)) {
                bs = dumpInvokerType(name);
            } else if (name.endsWith(SUFFIX_REQUEST)) {
                bs = dumpRequestType(name);
            } else if (name.endsWith(SUFFIX_RESPONSE)) {
                bs = dumpResponseType(name);
            } else if (name.endsWith(SUFFIX_PROXY)) {
                bs = dumpProxyType(name);
            } else {
                throw new ClassNotFoundException(name);
            }

            return defineClass(name, bs, 0, bs.length);
        } catch (Exception e) {
            throw new ClassNotFoundException("error", e);
        }

    }

}
