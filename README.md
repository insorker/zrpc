# zrpc

A tiny rpc framework.

## 简介

ZRPC 是一个基于 Netty, Spring, Zookeeper 的 RPC 框架。

## 使用

详见 zrpc-test 目录下的样例。

通过 spring 管理的话只需要调用 `@ZRpcService` 和 `@ZRpcReference` 这两个注解即可快速开发。

### 变量调用

> 详见 zrpc-test 目录下的 variable 样例。

服务端程序

```java
public class VariableServer {

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 7512;
        String registryAddress = "你的注册中心地址（格式：IP:PORT)";

        ZRpcServer server = new RegistryServer(host, port, registryAddress);
        server.addService(new ServiceInfo("HelloWorld"), "HelloWorld");
        server.addService(new ServiceInfo("PersonService"), new PersonServiceImpl("Harry Potter"));
        server.start();
    }
}
```

客户端程序

```java
public class VariableClient {
    public static void main(String[] args) {
        ZRpcClient zRpcClient = SingletonDiscoveryClient.newInstance("1.94.213.53:2181");

        PersonService service = zRpcClient.createService(new ServiceInfo("PersonService"), PersonService.class);
        System.out.println(service.getName());
        service.setName("Hermione");
        System.out.println(service.getName());
        service.setName("Ron");
        System.out.println(service.getName());
    }
}
```

### 依赖注入

> 详见 zrpc-test 目录下的 config 样例。

properties 文件

```properties
# zrpc server
zrpc.server.host = 127.0.0.1
zrpc.server.port = 7513
# zookeeper server
zrpc.server.registry.address = 你的注册中心地址（格式：IP:PORT)
```

服务端配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">

    <!--scan service package-->
    <context:component-scan base-package="io.github.insorker.zrpc.test.config.server"/>

    <context:property-placeholder location="classpath:zrpc.properties"/>

    <bean id="zRpcServer" class="io.github.insorker.zrpc.server.RegistryServer">
        <constructor-arg name="host" value="${zrpc.server.host}"/>
        <constructor-arg name="port" value="${zrpc.server.port}"/>
        <constructor-arg name="registryAddress" value="${zrpc.server.registry.address}"/>
    </bean>

</beans>
```

客户端配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd">

    <!--scan service package-->
    <context:component-scan base-package="io.github.insorker.zrpc.test.config.client"/>

    <context:property-placeholder location="classpath:zrpc.properties"/>

    <bean id="zRpcClient" class="io.github.insorker.zrpc.client.SingletonDiscoveryClient"
          factory-method="newInstance">
        <constructor-arg name="registryAddress" value="${zrpc.server.registry.address}"/>
    </bean>

</beans>
```

服务端程序

```java
public class ConfigServer {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("config-server.xml");
    }
}
```

服务端注解类

```java
@ZRpcService("PersonService")
public class PersonServiceImpl implements PersonService {

    private String name = "nobody";

    public PersonServiceImpl() {

    }

    public PersonServiceImpl(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

客户端程序

```java
public class ConfigClient {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("config-client.xml");
        House house = context.getBean(House.class);
        house.whoLives();
    }
}
```

客户端注解类
```java
@Component
public class House {

    @ZRpcReference("PersonService")
    private PersonService personService;

    public void whoLives() {
        System.out.println(personService.getName());
    }
}
```
