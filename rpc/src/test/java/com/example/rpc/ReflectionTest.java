package com.example.rpc;

import com.example.rpc.client.Stub;
import com.example.rpc.examples.MyMath;
import com.example.rpc.examples.MyMathImpl;
import com.example.rpc.examples.Point;
import com.example.rpc.remoteObj.RequestMessage;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;







public class ReflectionTest {
    @Test
    public void testRetrieveArgsAndReturn() {
        Class<?> ifcClass = MyMath.class;
        Method[] methods = ifcClass.getMethods();
        for(Method method:methods) {
            String name = method.getName();
            System.out.println("fncName= "+name);
            Class<?>[] parameterTypes = method.getParameterTypes();
            for (Class<?> parameterType : parameterTypes) {
                System.out.println("Parameter Type: " + parameterType.getName());
            }
            Class<?> returnType = method.getReturnType();
            System.out.println("Return type: "+returnType.getName());
        }
    }

    @Test
    public void testRetrieveImplMethods() throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> ifcClass = MyMath.class;
        Object impl = (Object) new MyMathImpl();
        Class<?> implClass = impl.getClass();
        Map<String, Method> methodMap = new HashMap<>();
        for(Method method: ifcClass.getMethods()) {
            String methodName = method.getName();
            methodMap.put(methodName, null);
        }
        for(Method method: implClass.getMethods()) {
            String methodName = method.getName();
            if(methodMap.containsKey(methodName)) {
                methodMap.put(methodName, method);
            }
        }

        Object res = methodMap.get("Add").invoke(impl, new Object[]{1,2});
        System.out.println(res);


    }

    @Test
    public void createImplementation() throws IOException {
        MyMath myMath = (MyMath) Stub.create(MyMath.class, "127.0.0.1", 1234);
        myMath.Add(1,2);
        Point p1 = new Point(1,2), p2 = new Point(3,4);
        myMath.AddPoint(p1, p2);
    }
}



