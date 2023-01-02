## 入门代码示例

​	为演示方便，演示的例子采用jetty做为服务器，spring容器用xml的配置形态。 

1. 新建maven工程，`pom.xml`文件内容如下:

   ```xml
   <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
   	<modelVersion>4.0.0</modelVersion>
   
   	<groupId>org.yafox</groupId>
   	<artifactId>muse-guide</artifactId>
   	<version>1.0.0</version>
   	<packaging>jar</packaging>
   
   	<name>muse-guide</name>
   	<url>http://maven.apache.org</url>
   
   	<properties>
   		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
   	</properties>
   
   	<dependencies>
   		<dependency>
   			<groupId>junit</groupId>
   			<artifactId>junit</artifactId>
   			<version>4.10</version>
   			<scope>test</scope>
   		</dependency>
   		<!-- 引入jetty-server是为了在应用程序中启动一个web服务器，用于处理http请求 -->
   		<dependency>
   			<groupId>org.eclipse.jetty</groupId>
   			<artifactId>jetty-server</artifactId>
   			<version>9.4.43.v20210629</version>
   		</dependency>
   		<!-- 引入json是muse启动过程需要，并且在处理http的json数据需要 -->
   		<dependency>
   			<groupId>com.google.code.gson</groupId>
   			<artifactId>gson</artifactId>
   			<version>2.8.5</version>
   		</dependency>
   
           <!-- muse核心库 -->
   		<dependency>
   			<groupId>org.yafox</groupId>
   			<artifactId>muse-core</artifactId>
   			<version>1.0.0</version>
   		</dependency>
           
           <!--引入spring容器管理Bean-->
   		<dependency>
   			<groupId>org.springframework</groupId>
   			<artifactId>spring-context</artifactId>
   			<version>5.3.5</version>
   		</dependency>
   
   		<!--核心log4j2jar包 -->
   		<dependency>
   			<groupId>org.apache.logging.log4j</groupId>
   			<artifactId>log4j-api</artifactId>
   			<version>2.4.1</version>
   		</dependency>
   		<dependency>
   			<groupId>org.apache.logging.log4j</groupId>
   			<artifactId>log4j-core</artifactId>
   			<version>2.4.1</version>
   		</dependency>
   		<!--用于与slf4j保持桥接 -->
   		<dependency>
   			<groupId>org.apache.logging.log4j</groupId>
   			<artifactId>log4j-slf4j-impl</artifactId>
   			<version>2.4.1</version>
   		</dependency>
   	</dependencies>
   
   	<build>
   		<plugins>
   			<plugin>
   				<groupId>org.apache.maven.plugins</groupId>
   				<artifactId>maven-compiler-plugin</artifactId>
   				<version>3.7.0</version>
   				<configuration>
   					<!-- http://maven.apache.org/plugins/maven-compiler-plugin/ -->
   					<source>1.8</source>
   					<target>1.8</target>
   				</configuration>
   			</plugin>
   		</plugins>
   	</build>
   </project>
   ```

   

2. 新建`DemoService`接口，在接口上加上`@Svc("demo")` 声明此接口可以发布为服务

   ```java
   package org.yafox.guide.base.service;
   
   import org.yafox.muse.annotation.Param;
   import org.yafox.muse.annotation.Svc;
   
   @Svc("demo")
   public interface DemoService {
   
       String sayHello(@Param("name")String name);
       
   }
   ```

   

3. 新建`DemoServiceImpl`类实现以上的 `DemoService`

   ```java
   package org.yafox.guide.base.service.impl;
   
   import org.yafox.guide.base.service.DemoService;
   
   public class DemoServiceImpl implements DemoService {
   
       @Override
       public String sayHello(String name) {
           return "hello " + name;
       }
   
   }
   ```

4. 新建`HttpHandler` 用于处理`http`请求

   ```java
   package org.yafox.guide;
   
   import java.io.IOException;
   import java.io.PrintWriter;
   
   import javax.servlet.ServletException;
   import javax.servlet.http.HttpServletRequest;
   import javax.servlet.http.HttpServletResponse;
   
   import org.eclipse.jetty.server.Request;
   import org.eclipse.jetty.server.handler.DefaultHandler;
   import org.yafox.muse.FaultHandler;
   import org.yafox.muse.Invoker;
   import org.yafox.muse.InvokerBucket;
   
   import com.google.gson.Gson;
   import com.google.gson.GsonBuilder;
   
   public class HttpHandler extends DefaultHandler {
   
       /**
        * 所有已发布的服务均可从此Bucket中取得
        */
       private InvokerBucket invokerBucket;
   
       /**
        * 程序运行期出了异常，可交由此处理进行处理
        */
       private FaultHandler faultHandler;
   
       /**
        * 用于处理请求数据与响应
        */
       private Gson gson = new GsonBuilder().setPrettyPrinting().create();
       
       /**
        * 此方法将处理所有的http请求
        */
       @Override
       public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
               throws IOException, ServletException {
         
           response.setCharacterEncoding("utf-8");
           response.setContentType("application/json");
           
           PrintWriter writer = response.getWriter();
           Object resp = null;
           try {
               // 这里根据请求的uri来确定应该是由哪个服务来响应
               String serviceId = request.getRequestURI();
               //从请求参数中获取得到调用此服务使用的规则, 此处是Demo为了方便从URL上进行传递，实际生产项目中，此值可取当前用户的角色名，就演变成了 RBAC的权限控制
               String rule = request.getParameter("rule");
               Invoker invoker = invokerBucket.findInvoker(serviceId + "@" + rule);
               
               // 找不到服务的情况有，serviceId声明的服务就不存在，或者指定的规则名不存在
               if (invoker == null) {
                   throw new Exception("无法调用到服务");
               }
               
               // 使用http请求体，构建一个请求对象(在这里要求请求体为json格式)，开发人员也可根据自己项目需求定制
               Object req = gson.fromJson(request.getReader(), invoker.getRequestType());
               
               // 执行调用业务代码
               resp = invoker.invoke(req);
           } catch (Throwable e) {
               // 这里打印是为了方便入门者方便看日志
               e.printStackTrace();
               // 如果出了异常，交由这个处理器进行处理
               resp = faultHandler.handle(e);
           } finally {
               // 最终把结果转成json写到客户端
               writer.write(gson.toJson(resp));
               writer.flush();
           }
           
       }
   
       public InvokerBucket getInvokerBucket() {
           return invokerBucket;
       }
   
       public void setInvokerBucket(InvokerBucket invokerBucket) {
           this.invokerBucket = invokerBucket;
       }
   
       public FaultHandler getFaultHandler() {
           return faultHandler;
       }
   
       public void setFaultHandler(FaultHandler faultHandler) {
           this.faultHandler = faultHandler;
       }
   
   }
   
   ```

5. 创建`App` 作为程序启动入口

   ```java
   package org.yafox.guide;
   
   import org.eclipse.jetty.server.Server;
   import org.springframework.context.support.ClassPathXmlApplicationContext;
   
   /**
    * 程序启动入口,启动一个spring容器，启动一个jetty作为http服务器
    * 由spring中的HttpHandler类进行http处理
    *
    */
   public class App 
   {
       public static void main( String[] args ) throws Exception
       {
           // 启动一个spring容器
           ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("app.xml");
   
           // 从容器中获取一个http的处理器
           HttpHandler httpHandler = ctx.getBean(HttpHandler.class);
           
           // 创建jetty服务器， 指定端口
           Server server = new Server(80);
           
           // 把取出的处理器设置成Jetty服务器的处理器
           server.setHandler(httpHandler);
           
           // 启动 jetty
           server.start();
       }
   }
   ```

   

6. 新建`src/main/resources/app.xml`并注册Bean

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
                              http://www.springframework.org/schema/beans/spring-beans.xsd">
   
       <!-- 由此bean来衔接muse与spring,并负责启动muse -->
       <bean class="org.yafox.muse.spring.Muse4Spring"></bean>
   
       <!-- jetty用于处理http的handler -->
       <bean name="httpHandler" class="org.yafox.guide.HttpHandler">
           <property name="invokerBucket" ref="invokerBucket"></property>
           <property name="faultHandler" ref="faultHandler"></property>
       </bean>
   
       <!-- 业务代码 -->
       <bean name="demoService"
           class="org.yafox.guide.base.service.impl.DemoServiceImpl"></bean>
   
   </beans>
   ```

   

7. 新建`src/main/resources/log4j2.xml` 配置日志输出

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <Configuration status="off" monitorInterval="1800">
       <Appenders>
           <Console name="Console" target="SYSTEM_OUT">
               <PatternLayout
                   pattern="%d %-5p (%F:%L) - %m%n" />
           </Console>
       </Appenders>
   
       <Loggers>
   
           <root level="info" includeLocation="true">
               <appender-ref ref="Console" />
           </root>
       </Loggers>
   </Configuration> 
   ```

   

8. 新建服务发布文件`src/main/resources/rules/demo/sayHello.json` 用于描述服务发布规则

   ```json
   {
   	"r1": {
   		"beanName": "demoService"
   	}
   }
   ```

   

9. 使用`postman`进行`POST`服务请求 `http://localhost/demo/sayHello?rule=r1` 请求体为 `{"name":"张三"}`,得到响应如下

   ```json
   {
       "code": 0,
       "data": "hello 张三"
   }
   ```

   

