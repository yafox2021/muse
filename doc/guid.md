# 使用指引

## 1. API

#### 服务声名

在需要发布为服务的`API`接口上写入`@Svc` 注解，声名此接口可发布为服务, 在参数上使用 `@Param` 注解指名参数名, 如以下代码可发布为两个服务，服务标识(`serviceId`)分别为 `/user/findUser` 与 `/user/addUser`

```java
@Svc("user")
public interface UserService {
    
    User findUser(@Param("id")String id);
    
    void addUser(@Param("user")User user);
}
```

#### 服务规则配置

在`src/main/resources/rules`目录下创建`/user/findUser.json` 文件，用来描述服务`/user/findUser`的发布过程

```json
{
    "rule1": {
        "assignment":{
            "id":"currentUserId"
        },
        "beanName":"userServiceImpl"
    },
    "rule2": {
        "validation":{
            "id":[{"type":"required", "message":"id必填"}]
        },
        "beanName":"userServiceImpl2"
    },
    "rule3": {
        "validation":{
            "id":[{"type":"required", "message":"id必填"},{"type":"length", "min":5, "message":"id最小长度为5"}]
        },
        "beanName":"userServiceImpl2"
    },
    "rule4": {
        "mask":{
            "data/tel":"telMask"
        },
        "beanName":"userServiceImpl"
    }
}
```

以上描述了发布`/user/findUser`的三条规则`rule1` 、`rule2` 与 `rule3`。

​        `rule1` 描述，参数 `id` 的值将由名字为 `currentUserId` 或 `currentUserIdEvaluation`  (实现了接口 `org.yafox.muse.Evaluation`) 的 `bean` 为其提供值，服务调用的逻辑是由名字为 `userServiceImpl`的 `bean` 提供的。

​        `rule2` 描述，参数`id`上有一个名字为 `required` 或 `requiredValidator` （实现了接口 `org.yafox.muse.Validator`）的 `bean` 为其提供校验逻辑，服务调用的逻辑是由名字为 `userServiceImpl2`的 `bean` 提供的。

​        `rule3`描述，参数`id`上有两个检验器，分别为 `required` 和 `length`， 服务调用的逻辑是由名字为 `userServiceImpl2`的 `bean` 提供的。

​        `rule4`描述，调用服务的返回值的`tel` 字段属性将由名字为  `telMask` 或 `telMaskEvaluation`  (实现了接口 `org.yafox.muse.Evaluation`) 的 `bean` 为其提供值。

#### 规则文件描述

```json
{
    "规则名": {
        "assignment": {
            "path/to/param": "Evaluation的BeanName"
        },
        "validation": {
            "path/to/param":[{"type":"Validator的BeanName", "Bean的属性名":"Bean的属性值"},
                             {"type":"Validator的BeanName", "Bean的属性名":"Bean的属性值"}]
        },
        "mask": {
            "path/to/param": "Evaluation的BeanName"
        },
        "beanName":"Bean实例名"
    }
}
```



​        `path/to/param` 参考 `xpath`, 遇到多值的时候，使用 `*`, 比如 `student/books/*/name` 表示的是 学生的书本列表的每一本书的名字。



#### InvokerBucket

当系统启动后，发布的服务都将在 `org.yafox.muse.InvokerBucket`中得到, `invokerBucket` 将会注册一个实例在`spring`中

```java
package org.yafox.muse;

public interface InvokerBucket {

    void addInvoker(String id, Invoker invoker);
    
    Invoker findInvoker(String id);
}

```

调用 `findInvoker` 从而可以找到服务调用入口， `id` 的取值为 `serviceId@ruleName`,  如上所示代码的`id` 可取值为 `/user/findUser@Rule1`、

`/user/findUser@Rule2`、`/user/findUser@Rule3`、`/user/findUser@Rule4` , 这就是基于规则的访问控制。



#### 赋值器接口

应用中实现 `org.yafox.muse.Evaluation` 接口，并注册为 `scope="prototype"` 的 `bean` ，配置文件即可使用此赋值器 (不限制使用spring注解或者xml的方式)

```java
package org.yafox.muse;

public interface Evaluation {

    Object evaluate(Object oldValue) throws Exception;
    
}
```

#### 校验器接口

应用中实现 `org.yafox.muse.Validator` 接口，并注册为 `scope="prototype"` 的 `bean` ，配置文件即可使用此检验器 (不限制使用spring注解或者xml的方式)

```java
package org.yafox.muse;

public interface Validator {

    void validate(Object target) throws Exception;
    
}
```



#### 掩码器接口

同：赋值器接口

## 2. SPI

#### 什么是 SPI

`spi` 的定义 `Service Provider Interface`,  通俗的讲，就是为了能完成你本身应用程序的功能，需要借助的外部的服务能力，但服务能力的规格是由你应用来定义。比如你的应用程序有用户保存的能力，得借助外部的存储能力为你提供服务，但保存的接口是由你应用来确定。

```java
package org.example;

public interface UserDAO {
    
    void save(User user);
}
```



#### 声名SPI

根据应用需求定义， 在方法上加上`@SPI` 注解(可选)，在方法参数上加 `@Param` 注解。

```java
package org.example;

import org.yafox.muse.annotation.Param;
import org.yafox.muse.annotation.SPI;

public interface UserDAO {
    
    @SPI("user.save")
    void save(@Param("user")User user);
}
```



#### 注册SPI

在 `src/main/resources/proxy/` 目录下， 创建 `{proxyHandlerBeanName}.properties` 文件（如 `myBatisProxyHandler.properties`）

```properties
# 格式如下
# beanName=beanType
userDAO=org.example.UserDAO
```

做如上配置后，当spring启动后，将注册名为 `userDAO`的类型为 `org.example.UserDAO` 的代理类的 `bean`， 其中的方法处理逻辑均由名字为 `myBatisProxyHandler` 实现了 `org.yafox.muse.ProxyHandler` 的`bean` 处理

#### 代理处理器(ProxyHandler)

`ProxyHandler`接口声名如下：

```java
package org.yafox.muse;

import java.lang.reflect.Method;

public interface ProxyHandler {

    /**
     * @param name 注册的 bean的名字
     * @param spi  方法上如有 @SPI("value")注解, 则为valu值 
     * @param method 当前调用的SPI的方法
     * @param input 调用参数的请求对象
     * @param responseType 调用结果的包装类型 {code:0, data: null} 的结构
     * @return 需要返回 responseType实例
     * @throws Exception
     */
    Object handle(String name, String spi, Method method, Object input, Class<?> responseType) throws Exception;
    
}
```



举个例子, 仅供参考，根据自己项目需要编写

```java
package org.yafox.guide.proxy.handler;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.yafox.muse.Bindable;
import org.yafox.muse.ProxyHandler;

public class MyBatisProxyHandler implements ProxyHandler {

    private SqlSessionFactory sessionFactory;

    @Override
    public Object handle(String name, String spi, Method method, Object input, Class<?> responseType) throws Exception {
        SqlSession session = null;
        String methodName = method.getName();
        String sqlId = method.getDeclaringClass().getName() + "." + methodName;
        
        Bindable response = (Bindable) responseType.getConstructor().newInstance();
        try {
            session = sessionFactory.openSession();
            
            if (methodName.startsWith("list")) {
                List<Object> rs = session.selectList(sqlId, input);
                response.bind(rs);
            } else if (methodName.startsWith("select")) {
                Object rs = session.selectOne(sqlId, input);
                response.bind(rs);
            } else {
                // 更新或者删除其他的逻辑， 这里只是举个示例，根据自己的项目习惯
            }
            
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return response;
    }

    public SqlSessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}

```

























