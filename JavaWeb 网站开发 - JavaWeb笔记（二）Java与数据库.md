![QQ_1722353274297](https://s2.loli.net/2024/07/30/mM4bTFPGDdYzSv9.png)

# Java与数据库

在开始本章节之前，需要先完成以下前置课程学习：

* 《MySQL数据库技术》了解如何使用MySQL存储数据以及对数据的增删改查操作。

**注意：** 请务必完成前置课程学习，本章节默认各位已经完成前置课程学习，不会再对已讲解内容重复介绍。

前面我们学习了MySQL数据库以及编写各种SQL语句对数据库进行操作，那么如何通过Java如何去使用数据库来帮助我们存储数据呢，这将是本章节讨论的重点。

## JDBC操作数据库

JDBC是什么？JDBC英文名为：Java Data Base Connectivity（Java数据库连接）官方解释它是Java编程语言和广泛的数据库之间独立于数据库的连接标准的Java API，根本上说JDBC是一种规范，它提供的接口，一套完整的，允许便捷式访问底层数据库。可以用JAVA来写不同类型的可执行文件：JAVA应用程序、JAVA Applets、Java Servlet、JSP等，不同的可执行文件都能通过JDBC访问数据库，又兼备存储的优势。简单说它就是Java与数据库的连接的桥梁或者插件，用Java代码就能操作数据库的增删改查、存储过程、事务等。

我们可以发现，JDK自带了一个`java.sql`包，而这里面就定义了大量的接口，不同类型的数据库，都可以通过实现此接口，编写适用于自己数据库的实现类。而不同的数据库厂商实现的这套标准，我们称为`数据库驱动`。

![QQ_1721898279981](https://s2.loli.net/2024/07/25/Tdj91k3n8izFEyR.png)

### 准备工作

那么我们首先来进行一些准备工作，以便开始JDBC的学习：

* 配置IDEA或Navicat连接到我们的数据库，以便以后调试
* 创建一个新的用于学习的数据库
* 将mysql驱动jar依赖导入到项目中（推荐6.0版本以上，这里用到是8.0）

一个Java程序并不是一个人的战斗，我们可以在别人开发的基础上继续向上开发，其他的开发者可以将自己编写的Java代码打包为`jar`，我们只需要导入这个`jar`作为依赖，即可直接使用别人提供的代码，这样就不需要我们自己从头开始手撕了，就像我们直接去使用JDK提供的类一样。

首先选择项目结构：

![QQ_1721899101166](https://s2.loli.net/2024/07/25/4p6QUCnv3rKAyN9.png)

接着添加一个新的库：

![QQ_1721899193206](https://s2.loli.net/2024/07/25/qXFDB8xoSQsZjmK.png)

这样我们就成功将库引入了，可以在外部库栏目中看到我们引入的jar包：

![QQ_1721899313082](https://s2.loli.net/2024/07/25/58JvVen9FykLlQq.png)

### 使用JDBC连接数据库

现在我们已经成功引入了MySQL数据库驱动，接着就可以正式开始使用JDBC了。在开始之前可以先打印一下看看自己的依赖是否成功引入了：

```java
public static void main(String[] args) {
  	//DriverManager是管理数据库驱动的工具类，我们可以通过它来查看当前已经引入的驱动列表
    DriverManager.drivers().forEach(System.out::println);
}
```

正常情况下这里应该会打印MySQL的驱动类：

![QQ_1721900134689](https://s2.loli.net/2024/07/25/7yATgfIEeHmY6MG.png)

要访问一个数据库，第一步肯定是创建一个新的的连接，我们可以通过*DriverManager*来创建一个新的数据库连接：

```java
//使用getConnection方法来创建一个新的连接
try (Connection connection = DriverManager.getConnection("连接URL","用户名","密码")){

} catch (SQLException e) {
    e.printStackTrace();
}
```

这里最主要的就是连接数据库的URL，还记得我们在上一章介绍的网站URL吗，格式为 <协议>://<主机>:<端口>/<路径>，互联网上所有的资源，都有一个唯一确定的URL，而MySQL本身也是以一个服务端的形式运行的，我们要连接也需要对应的URL才可以：

```
jdbc:mysql://localhost:3306/study
```

接着我们需要创建一个用于执行SQL的Statement对象，然后就可以像在命令行中那样直接使用SQL命令了：

```java
try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/study","root","123456");
     Statement statement = connection.createStatement()){
    //使用executeQuery来执行一个查询SQL语句
    ResultSet set = statement.executeQuery("select * from user");  //选择user表全部内容
} catch (SQLException e) {
    e.printStackTrace();
}
```

这里我们会得到一个ResultSet对象，我们使用它来获取查询结果，它类似于迭代器，需要使用next来向下迭代，但是使用上会有一些小小的区别：

```java
ResultSet set = statement.executeQuery("select * from user");
while (set.next()) {   //使用next开始读取查询结果的下一行
    System.out.print(set.getInt("id") + " ");   //直接获取本行指定字段下的数据
    System.out.print(set.getString("name") + " ");
    System.out.println(set.getInt("age"));
}
```

是不是感觉非常简单？接下来我们会为各位小伙伴详细介绍这里用到的几个类。

### 了解DriverManager

我们首先来了解一下DriverManager是什么东西，它其实就是管理我们的数据库驱动的。

我们知道，要操作数据库，需要通过`DriverManager.getConnection`创建连接，而开始连接之前，JDBC会自动扫描我们所有引入的数据库驱动并进行加载，此时会调用`registerDriver`方法完成对Driver接口实现类的加载：

```java
public static void registerDriver(java.sql.Driver driver, DriverAction da)
    throws SQLException {
    /* Register the driver if it has not already been added to our list */
    if (driver != null) {
        registeredDrivers.addIfAbsent(new DriverInfo(driver, da));
    } else {
        // This is for compatibility with the original DriverManager
        throw new NullPointerException();
    }
    println("registerDriver: " + driver);
}
```

这里我们提到，系统会自动发现Driver接口的实现类并加载，这实际上是一种SPI机制：

> Java的SPI（Service Provider Interface）机制是一种服务发现机制，它允许通过接口来加载服务实现。SPI通常用于在框架或库中提供可插拔的功能，使得不同的实现可以被动态加载和使用。这个机制在Java的标准库中以及许多开源项目中得到了广泛应用。
>

我们之前多多少少接触到过API的概念，实际上我们使用的很多类都是一种API，比如集合类，我们使用的往往是其接口，我们不需要关心其具体实现内容，我们只需要知道接口定义某个方法的用途，按照接口的定义去使用即可。

而SPI则来到了另一侧，也就是接口定义了这些操作，我们需要去实现接口定义的这些操作，这样才可以使得这个功能可以实实在在地使用。

![QQ_1722268678295](https://s2.loli.net/2024/07/29/GMunCqD56TR1rP3.png)

因此，我们这里使用的MySQL数据库驱动，其实就是对JDBC官方定义的接口的一种实现，利用SPI机制就可以完成加载（实际上在META-INF/services目录下有记录），只不过这个加载过程比较复杂，我们就不深究了。

当数据库驱动加载完成之后，才可以继续完成数据库连接的建立，因为我们上一节的实例代码中直接就使用了`getConnection`创建连接，我们可以来观察一下`getConnection`做了什么：

```java
@CallerSensitive
public static Connection getConnection(String url,
    String user, String password) throws SQLException {
    java.util.Properties info = new java.util.Properties();
		//这里判断的是数据库用户名和密码是否填写
    if (user != null) { 
        info.put("user", user);
    }
   	...
		//接着会调用内部的私有getConnection方法
    return (getConnection(url, info, Reflection.getCallerClass()));
}
```

```java
private static Connection getConnection(
    String url, java.util.Properties info, Class<?> caller) throws SQLException {
    //这里需要通过调用此方法的类来获取其类加载器，便于后续加载数据库驱动
    ClassLoader callerCL = caller != null ? caller.getClassLoader() : null;
    if (callerCL == null || callerCL == ClassLoader.getPlatformClassLoader()) {
        callerCL = Thread.currentThread().getContextClassLoader();
    }
		//检查数据库连接的url是否为空，如果是直接报错
    if (url == null) {
        throw new SQLException("The url cannot be null", "08001");
    }
  	...
		//这一步用于确保数据库驱动已经全部加载，包括上面在一开始说的数据库驱动加载部分
    ensureDriversInitialized();
    // 遍历所有已经加载的驱动，然后有合适的就可以直接建立连接了
    SQLException reason = null;
    for (DriverInfo aDriver : registeredDrivers) {
        // 如果调用此方法的Class有权限加载并使用当前驱动，就可以开始创建连接了
        if (isDriverAllowed(aDriver.driver, callerCL)) {
            try {
                ...
                Connection con = aDriver.driver.connect(url, info);
                if (con != null) {
                    // 如果连接不为空，则创建成功，这里直接就返回了
                    ...
                    return (con);
                }
            } catch (SQLException ex) ...
        } else ...
    }
    // 如果代码走到这里那肯定是出问题了，要是一个能连接的驱动都没找到，直接报错
    if (reason != null)    {
        println("getConnection failed: " + reason);
        throw reason;
    }
		...
}
```

我们看到实际上在源代码中，有很多地方都会打印日志，为什么到使用的时候就没有了呢？

```java
public static void println(String message) {
    synchronized (logSync) {
        if (logWriter != null) {  //注意这里需要logWriter不为null才能打印
            logWriter.println(message);
          	...
        }
    }
}
```

所以，如果需要打印日志的话，我们给它设置一个logWriter即可：

```java
DriverManager.setLogWriter(new PrintWriter(System.out));
```

这样我们就可以在控制台看到日志打印信息了：

![QQ_1722271131107](https://s2.loli.net/2024/07/30/Wq3PJAHTip1XCys.png)

下一部分我们接着来介绍Connection和Statement。

### 了解Connection和Statement

Connection是数据库的连接对象，也可以称作是一次会话，它可以执行 SQL 语句并在连接上下文中返回结果，这在我们一开始的时候就已经尝试过了。

Connection对象中也包含数据库相关的一些信息，当我们连接成功后，可以通过`getMetaData`来获取数据库信息对象：

```java
DatabaseMetaData meta = connection.getMetaData();
System.out.println("数据库名称: " + meta.getDatabaseProductName());
System.out.println("数据库版本: " + meta.getDatabaseMajorVersion() + "." + meta.getDatabaseMinorVersion());
System.out.println("当前用户: " + meta.getUserName());
System.out.println("数据库驱动: " + meta.getDriverName());
System.out.println("数据库驱动版本: " + meta.getDriverMajorVersion() + "." + meta.getDriverMinorVersion());
System.out.println("数据库驱动: " + meta.getCatalogTerm());
```

我们也可以使用Connection对象获取一些连接上的信息，比如：

```java
System.out.println("当前选择的数据库: " + connection.getCatalog());
```

```java
System.out.println("数据库超时时间: " + connection.getNetworkTimeout() + "ms");
```

```java
// 不支持 = 0
// 读未提交 = 1
// 读已提交 = 2
// 可重复读 = 4
// 序列化 = 8
System.out.println("事务隔离级别: " + connection.getTransactionIsolation());
```

我们接着来看Statement对象，它是我们执行SQL语句的关键，它用于执行静态 SQL 语句并返回其生成结果的对象。

```java
ResultSet set = statement.executeQuery("select * from user");
```

可以看到这里我们使用`executeQuery`方法来查询了表中所有数据，除了这个方法之外，Statement还提供了用于各种DML和DQL以及批处理等操作的方法，我们会在下节中为大家逐步介绍。

### 执行DQL和DML操作

还记得我们在MySQL中学习的DQL和DML操作吗？它们是用于向数据库中查询、插入、删除和更新数据的操作，包括SELECT、UPDATE、INSERT、DELETE，我们首先从最简单的SELECT语句开始。

前面我们介绍了Statement对象，它为我们提供了很多方法用于执行SQL语句，其中`executeQuery`就是用于执行SELECT查询语句的，我们来看看它的使用方式，它在接口中是这样定义的：

```java
//执行给定的 SQL 语句，该语句返回单个 ResultSet 对象。
ResultSet executeQuery(String sql) throws SQLException;
```

这里在执行完SQL语句后，会返回一个ResultSet对象作为结果，它表示一个数据库结果集的数据表，通常通过执行查询数据库的语句来生成。

使用起来也很简单，类似于迭代器：

```java
//一开始的位置为第0行，读取第一行数据要先调用一次next
set.next();
```

![QQ_1722335538058](https://s2.loli.net/2024/07/30/n387eiPyG5w9QqF.png)

从0开始，每次需要查询下一行数据前都要执行一次`next`方法，接着我们可以使用各种`get`方法来获取当前行的数据，比如我们要获取当前这一行的name字段值：

```java
set.next();
System.out.println(set.getString("name"));
```

查询结果为：

![QQ_1722333294783](https://s2.loli.net/2024/07/30/vXFJ251eMn4pbNj.png)

**注意：** 如果使用列序号读取数据，列的下标是从1开始的。

但是，如果我们使用Statement在执行完一次executeQuery得到ResultSet后，再次执行一次查询，那么之前得到的ResultSet在读取时会出现错误：

> 默认情况下，每个Statement对象只能同时打开一个ResultSet对象。因此，如果一个ResultSet对象的读取与另一个对象的读取交错，则每个对象都必须由不同的Statement对象生成。如果存在打开的语句的当前对象，则Statement接口中的所有执行方法都会隐式关闭语句的当前ResultSet对象。

```java
ResultSet set = statement.executeQuery("select * from user order by id desc");
ResultSet set2 = statement.executeQuery("select * from user order by id desc");
set.next();
System.out.println(set.getString("name"));
```

得到错误：

![QQ_1722334285876](https://s2.loli.net/2024/07/30/H6iY7jUywJt3mWd.png)

我们接着来看DML操作，Statement为我们提供`executeUpdate`方法：

```java
//执行给定的 SQL 语句，该语句可以是 、 UPDATE或DELETE语句，也可以是INSERT不返回任何内容的 SQL 语句，例如 SQL DDL 语句
int executeUpdate(String sql) throws SQLException;

//当返回的行计数可能超过 Integer. MAX_VALUE时，应使用此方法。
long executeLargeUpdate(String sql) throws SQLException
```

这里返回的结果是一个int类型值，它代表：

1. SQL 数据操作语言 （DML） 语句执行生效的行计数。
2. 不返回任何内容的 SQL 语句，并且会直接得到 0 作为结果。

我们从insert语句开始，看看是如何使用的：

```java
int i = statement.executeUpdate("insert into user (name, age) values ('小明', 18)");
System.out.println("生效行数: " + i);
```

![QQ_1722335370807](https://s2.loli.net/2024/07/30/jN571UbaGpmzoBR.png)

实际上用起来感觉和我们在命令行直接执行SQL语句差不多。 

除了我们上面提到的`executeUpdate`和`executeQuery`方法外，还有一个普通的`execute`方法：

```java
// 执行给定的 SQL 语句，该语句可能会返回多个结果。
boolean execute(String sql) throws SQLException;
```

注意这个方法返回的结果是一个布尔类型结果，这个结果：

*  如果第一个结果是 ResultSet 对象，返回true
* 如果它是更新计数或没有结果，返回false

也就是说，如果我们执行完SQL语句返回的是一个ResultSet结果集对象，那么就是真，也就是说只有选择语句才可以得到结果集，其他的DML语句是不可能得到这个结果的，所以肯定是假。因此，这个方法一般用于我们不确定传入的SQL语句到底是DQL还是DML语句的时候使用。

```java
boolean result = statement.execute("select * from user");
System.out.println(result ? "存在结果集" : "不存在结果集");
```

如果我们执行的是一个DQL语句，那么这里会得到结果集，结果集可以通过`getResultSet`方法获取：

```java
statement.execute("select * from user");
ResultSet set = statement.getResultSet();   //主动获取ResultSet
while (set.next()) {
    System.out.println(set.getString("name"));
}
```

如果我们执行的时一个DML语句，那么这里会返回false，更新生效的行数可以通过`getUpdateCount`方法获取：

```java
statement.execute("update user set name = '小明' where id = 1");
System.out.println(statement.getUpdateCount());
```

这样，关于数据库的基本操作就介绍完毕了。

### 批处理操作

我们接着来看批处理操作，当我们要执行很多条语句时，大家可能会一个一个地提交：

```java
//现在要求把下面所有用户都插入到数据库中
List<String> users = List.of("小刚", "小强", "小王", "小美", "小黑子");
//使用for循环来一个一个执行insert语句
for (String user : users) {
    statement.executeUpdate("insert into user (name, age) values ('" + user + "', 18)");
}
```

虽然这样看似非常完美，也符合逻辑，但是实际上我们每次执行SQL语句，都像是去厨房端菜到客人桌上一样，我们每次上菜的时候只从厨房端一个菜，效率非常低，但是如果我们每次上菜推一个小推车装满N个菜一起上，效率就会提升很多，而数据库也是这样，我们每一次执行SQL语句，都需要一定的时间开销，但是如果我把这些任务合在一起告诉数据库，效率会截然不同：

![QQ_1722352388506](https://s2.loli.net/2024/07/30/7lxfue9jU8nMYLQ.png)

那么如何才能一口气全部交给数据库处理呢，最简单的方式是直接对我们的SQL语句进行优化，我们可以直接拼接出这样的一个字符串出来，只需要执行一次SQL即可：

```sql
INSERT INTO user (name, age) VALUES 
('小刚', 18),
('小强', 18),
('小王', 18),
('小美', 18),
('小黑子', 18);
```

但是有些时候很难实现对SQL语句的优化，我们也可以使用批处理来完成：

```java
List<String> users = List.of("小刚", "小强", "小王", "小美", "小黑子");
for (String user : users) {
  	//使用addBatch将一个SQL语句添加到批处理列表中
    statement.addBatch("insert into user (name, age) values ('" + user + "', 18)");
}
int[] results = statement.executeBatch();  //统一执行批处理
System.out.println(Arrays.toString(results));
```

我们可以使用`addBatch`方法来将任务添加到批处理队列中，所有任务添加完成后，再使用`executeBatch`一次性执行批处理操作，这样同样可以防止多次提交SQL命令。

并且这里会返回一个int数组代表每一个操作受影响的行数。

### 将查询结果映射为对象

现在我们从数据库中查询每条记录了，但是我们现在查询得到的数据依然是零零散散的，有没有更好的办法可以集中管理数据呢？实际上各位小伙伴不难发现，数据库中某一张表的一条记录，正好就是我们Java中某一个对象实体所包含的信息，而我们之前在设计数据库表的时候，也是这样去参考的：

```
1 小明 18
```

```java
public class User {
    private int id;
    private String name;
    private int age;
}
```

因此，为了方便，我们在查询到数据之后，一般会将每条记录都创建为一个对应的实体类对象。

我们接着来完善一下它：

```java
public class User {
    ...

    public User(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public void say(){
        System.out.println("我叫：" + name + "，编号为：" + id + "，我的年龄是：" + age);
    }
}
```

好了，我们现在就可以在查询数据的时候直接转换为我们的实体类对象了：

```java
while (set.next()) {
    User user = new User(set.getInt("id"),
            set.getString("name"),
            set.getInt("age"));
    user.say();
}
```

![QQ_1722411444101](https://s2.loli.net/2024/07/31/uzDIRM9CkX1bOAs.png)

只不过普通的类型对于我们操作不太方便，比如我们想要获取对象的一些数据，还需要额外编写对应的getter方法，为了方便，我们可以使用Java17新增的记录类型来编写，这是专用于数据表示的特殊类型，它在编译时自带了我们实体类封装所需要的getter方法，以及对应的比较方法重写，包括toString等，这样就不需要我们自己去编写了：

```java
public record User(int id, String name, int age) {
    public void say(){
        System.out.println("我叫：" + name + "，编号为：" + id + "，我的年龄是：" + age);
    }
}
```

效果和之前完全一样，并且我们可以直接使用它自动生成的方法：

```java
User user = new User(set.getInt("id"),
        set.getString("name"),
        set.getInt("age"));
System.out.println(user);
```

![QQ_1722411685367](https://s2.loli.net/2024/07/31/nQXGEJThAONbviK.png)

是不是感觉很方便？当然，除了使用Java17提供的记录类型之外，后面我们还会学习Lombok，它相比记录类型更加灵活和强大，利用注解同样可以在编译期完成对应方法的生成。

（选学）利用反射和泛型，直接得到对应的实体类，无需硬写类型：

```java
private static <T> T convert(ResultSet set, Class<T> clazz){
    try {
        Constructor<T> constructor = clazz.getConstructor(clazz.getConstructors()[0].getParameterTypes());   //默认获取第一个构造方法
        Class<?>[] param = constructor.getParameterTypes();  //获取参数列表
        Object[] object = new Object[param.length];  //存放参数
        for (int i = 0; i < param.length; i++) {   //是从1开始的
            object[i] = set.getObject(i+1);
            if(object[i].getClass() != param[i])
                throw new SQLException("错误的类型转换："+object[i].getClass()+" -> "+param[i]);
        }
        return constructor.newInstance(object);
    } catch (ReflectiveOperationException | SQLException e) {
        e.printStackTrace();
        return null;
    }
}
```

实际上，在后面我们会学习Mybatis框架，它对JDBC进行了深层次的封装，而它就进行类似上面反射的操作来便于我们对数据库数据与实体类的转换。

### 实现登陆与SQL注入攻击

在生活中，很多网站都支持用户名和密码登录，这也是一种安全机制，防止别人非法访问我们自己的账户。尤其是一些银行相关的网站，如果不设置密码就可以直接访问我们的账户，那随便来个人都可以转走我们的钱了。

通过一个密码验证，就可以很好地解决这个问题，在进行各种操作之前，需要使用账号密码登录，验证是你之后，才可以开始，这也是现在最主流的方式。

实际上想要设计一个这样的系统也很简单，同样是设计一张用户表，只不过这次我们需要带上用户自己设定的密码，并且用户的名字或是ID必须是唯一的，否则无法区分：

![QQ_1722413094272](https://s2.loli.net/2024/07/31/IbmOj4HJUsytQfa.png)

接着我们设计一个对应的实体类：

```java
public record Account (int id, String name, String password){
    public boolean verifyPassword(String password) {
        return this.password.equals(password);
    }
}
```

接着我们就可以从控制台接受数据并校验了：

```java
try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/web_study", "test", "123456");
     Statement statement = connection.createStatement();
     Scanner scanner = new Scanner(System.in)){
    System.out.print("请输入用户名: ");
    String username = scanner.nextLine();
    System.out.print("请输入密码: ");
    String password = scanner.nextLine();
    ResultSet set = statement.executeQuery("select * from account where name = '" + username + "'and password = '" + password + "'");
    if(set.next()) {
        Account account = new Account(set.getInt(1),
                set.getString(2), set.getString(3));
        System.out.println(account + " 登录成功");
    } else {
        System.out.println("用户名或密码错误");
    }
} catch (SQLException e) {
    e.printStackTrace();
}
```

![QQ_1722415319102](https://s2.loli.net/2024/07/31/18tfCopcKGgL5In.png)

这样我们就实现了一个最基本的用户名密码登录系统了，用户可以通过自己输入用户名和密码来登陆。

但是，实际上这样的系统存在一些问题，如果我们输入一些不太正常的内容：

```sh
请输入用户名: test
请输入密码: 1111' or 1=1; -- 
```

此时，我们的密码输入的是一个不太常规的内容：`1111' or 1=1; -- `，居然登录成功了，各位小伙伴可以想象一下如果这样的字符串拼接到我们的SQL语句中，会变成什么样呢？

```sql
select * from account where name = 'test' and password = '1111' or 1=1; --;'
```

由于在SQL中`--`为注释，所以最终执行的SQL就变成了这样：

```sql
select * from account where name = 'test' and password = '1111' or 1=1;
```

我们发现此时的SQL语句已经完全脱离我们想要的意思了，最后多出来了一个`or 1=1`，由于1=1一定为真，此时or后面的条件直接被判定为真了，导致后面的密码判定条件失效。

因此，如果允许这样的数据插入，那么我们原有的SQL语句结构就遭到了破坏，使得用户能够随意登陆别人的账号，所以我们可能需要限制用户的输入来防止用户输入一些SQL语句关键字，但是SQL关键字非常多，这并不是解决问题的最好办法，那该怎么办呢。

### PreparedStatement

我们发现，如果单纯地使用Statement来执行SQL命令，会存在严重的SQL注入攻击漏洞！而这种问题，我们可以使用PreparedStatement来解决。

>  `PreparedStatement` 在执行之前会被预编译，因此如果同样的 SQL 查询多次执行，性能会更好。数据库只需编译一次，而不是每次都编译，并且`PreparedStatement` 使用参数化查询，自动对输入进行转义，从而有效防止 SQL 注入攻击，通过使用参数，SQL 语句更加清晰，将值与查询逻辑分开，使得代码更易于维护和理解。

它相比我们之前使用的Statement来说，不仅性能更好，还更加安全。

我们还是以之前的用户登录为例，这次我们换成更加安全的PreparedStatement来操作：

```java
PreparedStatement statement = connection.prepareStatement("select * from account where name = ? and password = ?")
```

我们需要提前给到PreparedStatement一个SQL语句，并且使用`?`作为占位符，它会预编译一个SQL语句，通过直接将我们的内容进行替换的方式来填写数据。

现在我们修改一下之前的写法：

```java
try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/web_study", "test", "123456");
     PreparedStatement statement = connection.prepareStatement("select * from account where name = ? and password = ?");
     Scanner scanner = new Scanner(System.in)){
    System.out.print("请输入用户名: ");
    statement.setString(1, scanner.nextLine());  //我们需要手动调用set方法填入参数到对应位置上去
    System.out.print("请输入密码: ");
    statement.setString(2, scanner.nextLine());
    ResultSet set = statement.executeQuery();
    if(set.next()) {
        Account account = new Account(set.getInt(1),
                set.getString(2), set.getString(3));
        System.out.println(account + " 登录成功");
    } else {
        System.out.println("用户名或密码错误");
    }
} catch (SQLException e) {
    e.printStackTrace();
}
```

执行之前的SQL注入攻击案例：

![QQ_1722416161535](https://s2.loli.net/2024/07/31/kCcFwoa18Si2ysD.png)

我们发现，此时无法再被破解了。我们来看看实际执行的SQL语句是什么，这里直接打印Statement对象：

```java
System.out.println(statement);
```

实际执行的SQL语句如下：

```sql
select * from account where name = 'test' and password = '1111\' or 1=1; --'
```

此时我们发现，我们输入的恶意内容中单引号被转义了，也就是说它现在仅仅代表的是单引号这个字符，而真正用作囊括内容的是后面的那个单引号，也就是PreparedStatement自动将可能会产生歧义的地方进行了处理，保证了我们输入的内容是什么，那么这里填入的一定是什么。这样就可以防止语义被改变，从根源上解决SQL注入攻击。

### 事务操作

还记得我们在MySQL中讲解的事务概念吗？

> 事务是数据库管理系统中一个重要的概念，涉及到一系列操作的执行，这些操作要么全部成功，要么全部失败。

JDBC默认的事务处理行为是自动提交，所以前面我们执行一个SQL语句就会被直接提交（相当于没有启动事务）因此需要使JDBC进行事务管理时，首先要通过Connection对象调用`setAutoCommit(false)`方法, 将SQL语句的提交（commit）由驱动程序转交给应用程序负责。

```java
connection.setAutoCommit(false);
```

一旦关闭自动提交，那么现在执行所有的操作如果在最后不进行`commit()`来提交事务的话，那么所有的操作都会丢失，只有提交之后，所有的操作才会被保存：

```java
connection.setAutoCommit(false);
statement.executeUpdate("insert into user (name, age) values ('小强', 18)");
```

![QQ_1722421744878](https://s2.loli.net/2024/07/31/QOxKItqvPiM5C7L.png)

我们尝试在最后进行一次`commit()`操作即可正常保存：

```java
connection.setAutoCommit(false);
statement.executeUpdate("insert into user (name, age) values ('小强', 18)");
connection.commit();
```

和命令行一样，在同一个连接下，如果我们不提交的情况下使用查询，确实可以查到我们之前插入的数据，但是实际上它并不在数据库中：

```java
connection.setAutoCommit(false);
statement.executeUpdate("insert into user (name, age) values ('小强', 18)");
ResultSet rs = statement.executeQuery("select * from user");
while (rs.next()) System.out.println(rs.getString("name"));
```

我们也可以使用`rollback()`来手动回滚之前的全部操作：

```java
connection.rollback();
```

比如：

```java
statement.executeUpdate("insert into user (name, age) values ('小赵', 18)");
statement.executeUpdate("insert into user (name, age) values ('小刘', 18)");
connection.rollback();
//只有这里的插入最终生效了，前面的被回滚了
statement.executeUpdate("insert into user (name, age) values ('小强', 18)");
connection.commit();
```

有时候为了操作更方便，我们还可以自由创建回滚点，快速回滚到我们想要回滚的位置上：

```java
connection.setAutoCommit(false);
statement.executeUpdate("insert into user (name, age) values ('小赵', 18)");
Savepoint savepoint = connection.setSavepoint();
statement.executeUpdate("insert into user (name, age) values ('小刘', 18)");
connection.rollback(savepoint);  //回滚到回滚点，撤销前面到回滚点部分的全部操作
statement.executeUpdate("insert into user (name, age) values ('小强', 18)");
connection.commit();
```

通过开启事务，我们就可以更加谨慎地进行一些操作了，如果我们想从事务模式切换为原有的自动提交模式，我们可以直接将其设置回去：

```java
connection.setAutoCommit(false);
statement.executeUpdate("insert into user (name, age) values ('小赵', 18)");
connection.setAutoCommit(true);  //重新开启自动提交，开启时把之前的事务模式下的未提交的内容给提交了
statement.executeUpdate("insert into user (name, age) values ('小刘', 18)"); 
//没有commit也成功了
```

有关事务相关操作，我们就暂时先介绍到这里。

### 结果集高级用法

前面我们介绍了Statement，它用于执行SQL语句，我们这一节接着来讲解它的高级用法。

我们在创建Statement时，实际上可以单独配置Statement的结果集类型和并发性质，它们会决定得到的ResultSet具备的功能。默认情况下，使用不带任何参数的`createStatement()`返回 Statement 对象创建的结果集类型为TYPE_FORWARD_ONLY ，并发级别为 CONCUR_READ_ONLY。

我们先来看结果集类型：

* ResultSet.TYPE_FORWARD_ONLY - 顾名思义，结果集只能不断向前检索结果，不能倒回去看之前的。
* ResultSet.TYPE_SCROLL_INSENSITIVE - 支持任意滚动，但是对修改不敏感
* ResultSet.TYPE_SCROLL_SENSITIVE - 支持任意滚动，对修改敏感

默认情况下，我们得到的ResultSet只能向前检索：

```java
ResultSet rs = statement.executeQuery("select * from user");

rs.next();   //来到第一行
rs.next();   //来到第二行
rs.previous();  //此时会出现错误，因为默认情况下不允许向后
System.out.println(rs.getString(2));
```

也就是说，如果结果集类型为TYPE_FORWARD_ONLY，那么除了next之外的所有移动行相关的操作，都是不支持的。我们可以在创建Statement的时候手动设置为滚动类型，我们使用任意一种双向滚动类型即可：

```java
Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
```

此时随意跳转行数均可：

```java
rs.last();   //使用last直接跳转到最后一行
System.out.println(rs.getString(2));

rs.absolute(2);   //直接移动到第二行
System.out.println(rs.getString(2));

rs.relative(-1);   //相对于当前位置向上移动一行
System.out.println(rs.getString(2));
```

**注意：** 虽然双向滚动类型的ResultSet非常方便，但是由于随时都可能读取之前的数据，因此可能会导致内存持续占用不会释放，当数据量过大时，会导致内存溢出错误。

我们接着来看不同的并发级别：

* CONCUR_READ_ONLY - 只读模式，只能读取数据，不能修改
* CONCUR_UPDATABLE - 可更新模式，读取数据时也可以修改数据

我们可以在遍历ResultSet的时候直接对当前行数据进行更新，而不需要手动输入SQL命令：

```java
ResultSet rs = statement.executeQuery("select * from user");
while (rs.next()) {
    rs.updateInt("age", rs.getInt("age") + 1);  //直接对age字段进行修改
    rs.updateRow();   //更新完后需要统一执行一次updateRow才能生效
}
```

运行之后，可能会出现问题：

![QQ_1722530107567](https://s2.loli.net/2024/08/02/PyavEUFeDu5pxTY.png)

这是因为默认情况下我们的ResultSet为只读模式，我们需要在创建时手动将其修改为可更改模式：

```java
Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE)
```

这样，就可以顺利执行上面的操作了。

我们接着来看ResultSet的可保持性，它通常在事务模式下使用，它可以决定事务提交后是否保留ResultSet中的数据，默认情况下可保持性为HOLD_CURSORS_OVER_COMMIT，它有以下选项：

* HOLD_CURSORS_OVER_COMMIT - 在事务提交或回滚后，保留ResultSet的数据。 
* CLOSE_CURSORS_AT_COMMIT - 在事务提交或回滚后，关闭ResultSet。

至此，有关ResultSet的高级用法就介绍到这里。

## JUL日志系统

**注意：** 开启本板块前，请先完成Mybatis视频教程和Lombok视频教程，务必穿插学习后再继续向后学习JavaWeb剩余内容，实战部分也会用到哦。

**首先一问：** 为什么需要日志系统？

我们之前一直都在使用`System.out.println`来打印信息，但是，如果项目中存在大量的控制台输出语句，会显得很凌乱，而且日志的粒度是不够细的，假如我们现在希望，项目只在debug的情况下打印某些日志，而在实际运行时不打印日志，采用直接输出的方式就很难实现了，因此我们需要使用日志框架来规范化日志输出。

而JDK为我们提供了一个自带的日志框架，位于`java.util.logging`包下，我们可以使用此框架来实现日志的规范化打印，使用起来非常简单：

```java
public class Main {
    public static void main(String[] args) {
      	// 首先获取日志打印器，名称随意
        Logger logger = Logger.getLogger("test");
      	// 调用info来输出一个普通的信息，直接填写字符串即可
        logger.info("我是普通的日志");
    }
}
```

我们可以在主类中使用日志打印，得到日志的打印结果：

```
十一月 15, 2021 12:55:37 下午 com.test.Main main
信息: 我是普通的日志
```

我们发现，通过日志输出的结果会更加规范，在后续的学习中，日志将时刻伴随我们左右。

### JUL基本使用

日志的打印并不是简单的输出，有些时候我们可以会打印一些比较重要的日志信息，或是一些非常紧急的日志信息，根据不同类型的信息进行划分，日志一般分为7个级别，详细信息我们可以在Level类中查看：

```java
public class Level implements java.io.Serializable {
	...
  
    //出现严重故障的消息级别，值为1000，也是可用的日志级别中最大的
    public static final Level SEVERE = new Level("SEVERE",1000, defaultBundle);
    //存在潜在问题的消息级别，比如边充电边打电话就是个危险操作，虽然手机爆炸的概率很小，但是还是会有人警告你最好别这样做，这是日志级别中倒数第二大的
    public static final Level WARNING = new Level("WARNING", 900, defaultBundle);
    //所有常规提示日志信息都以INFO级别进行打印
    public static final Level INFO = new Level("INFO", 800, defaultBundle);
  	//以下日志级别依次降低，不太常用
    public static final Level CONFIG = new Level("CONFIG", 700, defaultBundle);
    public static final Level FINE = new Level("FINE", 500, defaultBundle);
    public static final Level FINER = new Level("FINER", 400, defaultBundle);
    public static final Level FINEST = new Level("FINEST", 300, defaultBundle);
 
  ...
}
```

之前通过`info`方法直接输出的结果就是使用的默认级别的日志，实际上每个级别都有一个对应的方法用于打印：

```java
public static void main(String[] args) {
    Logger logger = Logger.getLogger(Main.class.getName());
    logger.severe("severe");  //最高日志级别
    logger.warning("warning");
    logger.info("info"); //默认日志级别
    logger.config("config");
    logger.fine("fine");
    logger.finer("finer");
    logger.finest("finest");   //最低日志级别
}
```

当然，如果需要更加灵活地控制日志级别，我们也可以通过`log`方法来主动设定该条日志的输出级别：

```java
Logger logger = Logger.getLogger(Main.class.getName());
logger.log(Level.SEVERE, "严重的错误", new NullPointerException("祝你明天就遇到我"));
logger.log(Level.WARNING, "警告的内容");
logger.log(Level.INFO, "普通的信息");
logger.log(Level.CONFIG, "级别低于普通信息");
```

不过，可能会有小伙伴发现，某些级别的日志，并没有在控制台打印。这其实因为Logger默认情况下只会打印INFO级别以上的日志，而以下的日志则会直接省略，我们可以通过配置来进行调整，只不过调整日志打印级别比较麻烦，需要经过后续的学习我们再来讨论这个问题。

### 日志核心内容

前面我们介绍了日志的基本使用，我们接着来看日志打印的核心部分：Handler，它用于处理我们的日志内容打印，JDK为我们提供了很多种类的Handler用于多种不同类型的日志打印，比较常见的就是打印到控制台，当然我们也可以打印到一个日志文件中，名字一般为`xxx.log`这种格式。常用的Handler实现有：

* ConsoleHandler  -  将日志通过System. err打印到控制台，现在默认就是使用的这个。
* FileHandler  -  将日志直接写入到指定的文件中。
* SocketHandler   -  将日志利用Socket通过网络发送到另一个主机。

当然，一个Logger中可以包含多个Handler用于同时向不同的地方打印日志，我们可以通过`getHandlers`方法来获取Logger对象中已经配置的Handler对象：

```java
Logger logger = Logger.getLogger(Main.class.getName());
System.out.println(Arrays.toString(logger.getHandlers()));
```

此时打印的列表中不存在任何Handler对象，可见，我们创建的Logger默认是不带任何Handler对象的，那么我们之前的日志是怎么打印出来的呢？这实际上是Logger的父级提供的，这里我们先暂时不介绍继承关系。我们使用`setUseParentHandlers`方法来屏蔽所有父级提供的日志处理器：

```java
logger.setUseParentHandlers(false);
```

现在由于Logger没有配置任何Handler处理器，因此我们打印日志就不会有任何效果了。

我们可以来尝试自己配置一个用于控制台打印的Handler处理器，这里直接创建一个新的ConsoleHandler对象：

```java
ConsoleHandler handler = new ConsoleHandler();
logger.addHandler(handler);
logger.info("Hello World");
```

现在我们打印日志就可以出现想要的结果了：

```
8月 28, 2024 12:12:37 上午 com.test.Main main
信息: Hello World
```

是不是很简单？我们接着来尝试将日志记录到我们本地的文件中，这里使用FileHandler类型：

```java
FileHandler handler = new FileHandler("test.log", true);   //第二个参数开启后会续写已有的日志，如果不开启会直接覆盖重写
logger.addHandler(handler);
```

最后我们就可以得到一个日志文件了，默认是以XML格式进行写入的：

![QQ_1724775618370](https://s2.loli.net/2024/08/28/4mZsxMOGDA8jTBn.png)

这种格式有助于程序的日志读取，但是对于我们人来说，非常难以阅读，那有没有什么办法将文件的日志打印变成控制台那种格式呢？实际上每一个Handler都有一个Formatter对象，它用于控制日志的格式，默认情况下，ConsoleHandler会配置一个SimpleFormatter对象，格式为：

```
时间 类名 方法
日志级别: 日志内容
```

我们刚刚在FileHandler中见到的是默认生成的XMLFormatter，它会将日志以XML的形式进行打印，现在我们也可以手动修改它为SimpleFormatter类型：

```java
Handler handler = new FileHandler("test.log");
handler.setFormatter(new SimpleFormatter());
```

此时日志文件中写入的内容就是简单的日志格式了：

![QQ_1724776088333](https://s2.loli.net/2024/08/28/VJKTIlU51cBO6aS.png)

我们最后来看看上一节中遗留的日志级别问题，我们知道日志的默认打印级别为INFO，此时低于INFO的所有日志都是被屏蔽的，而要修改日志的默认打印级别，我们需要同时调整Handler和Logger的level属性：

```java
handler.setLevel(Level.FINEST);   //注意，填写的日志打印级别是什么，高于等于此级别的所有日志都会被打印
logger.setLevel(Level.FINEST);
logger.fine("Hello World");
```

现在我们再次打印低于INFO级别的日志就可以正确得到结果了。Logger类还为我们提供了两个比较特殊的日志级别，它们专门用于配置特殊情况：

```java
//表示直接关闭所有日志信息，值为int的最大值
public static final Level OFF = new Level("OFF",Integer.MAX_VALUE, defaultBundle);
//表示开启所有日志信息，无论是什么级别都进行打印
public static final Level ALL = new Level("ALL", Integer.MIN_VALUE, defaultBundle);
```

因为这这里OFF的值为int的最大值，也就是说没有任何日志级别的值大于它，因此，如果将打印等级配置为OFF，那么所有类型的日志信息都不会被打印了，而ALL则相反。

### 日志继承关系

JUL中Logger之间存在父子关系，这种父子关系类似于继承，我们可以通过Logger的`getParent`方法来获取其父Logger对象：

```java
Logger logger = Logger.getLogger(Main.class.getName());
System.out.println(logger.getParent());
```

这里我们会得到一个：

```
java.util.logging.LogManager$RootLogger@24d46ca6
```

这个RootLogger对象为所有日志记录器的最顶层父级对象，它包含一个默认的ConsoleHandler处理器用于进行控制台打印，而日志在打印时，子Logger会继承父Logger提供的所有Handler进行日志处理，因此我们在默认情况下才能正常使用日志打印：

```java
Logger logger = Logger.getLogger("test");
Logger parent = logger.getParent();
System.out.println(Arrays.toString(parent.getHandlers()));
```

根据我们上节课学习的知识，在默认情况下如果我们需要修改日志打印等级，那么同时也需要将父级的Handler也进行日志等级配置：

```java
parent.getHandlers()[0].setLevel(Level.ALL);
logger.setLevel(Level.ALL);
logger.finest("别吃我家鸽鸽下的蛋");
```

当然，如果我们在不屏蔽父级Handler的情况下为子级配置一个Handler，那么此时两个Handler都会生效：

```java
logger.addHandler(new ConsoleHandler());
logger.info("你干嘛");
```

日志中出现了两次：

```
8月 28, 2024 12:57:39 上午 com.test.Main main
信息: 你干嘛
8月 28, 2024 12:57:39 上午 com.test.Main main
信息: 你干嘛
```

不过需要注意一下顺序，当父级和子级都配置时，那么子级的Handler优先进行处理，接着才是父级。

除了默认的RootLogger作为父类，实际上Logger还会通过名称进行分级，自动构建一个继承关系，比如下面：

```java
Logger logger1 = Logger.getLogger("com");
Logger logger2 = Logger.getLogger("com.test");
Logger logger3 = Logger.getLogger("com.test.inner1");
Logger logger4 = Logger.getLogger("com.test.inner2");

System.out.println(logger4.getParent() == logger2);   //全true
System.out.println(logger3.getParent() == logger2);
System.out.println(logger2.getParent() == logger1);
```

就像包名一样，日志的名称会按照包的分级，进行自动继承，就像下面这个图一样：

![QQ_1724778477259](https://s2.loli.net/2024/08/28/4X8PqesSAC7ijZW.png)

至于为什么要这样进行划分，学习完下一节的内容就会明白了。

### 日志默认配置

在学习日志配置之前，我们先来学习一下Properties格式，Properties格式的文件是Java的一种配置文件，我们之前在学习Mybatis的时候学习了XML，但是我们发现XML配置文件读取实在是太麻烦，那么能否有一种简单一点的配置文件呢？此时就可以使用Properties文件，它的格式如下：

```properties
name=Test
desc=Description
```

该文件配置很简单，格式类似于我们Java中的Map键值对，中间使用等号进行连接。当然，键的名称我们也可以分为多级进行配置，每一级使用`.`进行划分，比如我们现在要配置数据库的连接信息，就可以编写为这种形式：

```properties
jdbc.datasource.driver=com.cj.mysql.Driver
jdbc.datasource.url=jdbc:mysql://localhost:3306/test
jdbc.datasource.username=test
jdbc.datasource.password=123456
```

那么配置文件编写好了，Java该如何进行读取呢？JDK为我们提供了一个叫做`Properties`的类型，它继承自Hashtable类（是HashMap的同步加锁版）使用起来和HashMap是差不多的：

```java
public class Properties extends Hashtable<Object,Object> {
```

我们现在就来创建一个试试看吧：

```java
Properties properties = new Properties();
properties.load(new FileReader("test.properties"));   //使用load方法读取本地文件中的所有配置到Map中
System.out.println(properties);
```

实际上，我们也可以通过这种方式来获取我们的一些系统属性，System类中有一个`getProperties`方法用于存储所有系统相关的属性值，这里我们打印一下系统名称和版本：

```java
Properties properties = System.getProperties();
System.out.println(properties.get("os.name"));
System.out.println(properties.get("os.version"));
```

当然，程序中的Properties对象也可以快速保存为一个对应的`.properties`文件：

```java
Properties properties = System.getProperties();
properties.store(new FileWriter("system.properties"), "系统属性");
```

了解完Properties之后，我们接着回到JUL中，实际上JUL也可以通过进行配置文件来规定日志打印器的一些默认值，比如我们现在想配置默认的日志打印级别：

```properties
# RootLogger 的默认处理器为
handlers=java.util.logging.ConsoleHandler
# RootLogger 的默认的日志级别
.level=ALL

# 配置ConsoleHandler的默认level
java.util.logging.ConsoleHandler.level=ALL
```

接着我们需要在程序开始之前加载这里的配置：

```java
LogManager manager = LogManager.getLogManager();   //获取LogManager读取配置文件
manager.readConfiguration(new FileInputStream("test.properties"));
Logger logger = Logger.getLogger("test");
logger.config("Hello World");
```

这样就可以通过配置文件的形式修改一些功能的默认属性了，而不需要我们再使用代码进行配置。实际上在JUL的这类内部也有着对应的配置处理操作，如果发现有默认配置优先使用配置里面的，比如Handler的构造方法：

```java
Handler(Level defaultLevel, Formatter defaultFormatter,
        Formatter specifiedFormatter) {

    LogManager manager = LogManager.getLogManager();
    String cname = getClass().getName();

    final Level level = manager.getLevelProperty(cname + ".level", defaultLevel);
    final Filter filter = manager.getFilterProperty(cname + ".filter", null);
    final Formatter formatter = specifiedFormatter == null
                                ? manager.getFormatterProperty(cname + ".formatter", defaultFormatter)
                                : specifiedFormatter;
    final String encoding = manager.getStringProperty(cname + ".encoding", null);
		...
}
```

关于使用配置文件的形式修改JUL部分内容的默认值就先讲解到这里。

### 自定义日志格式

前面我们提到了每一个Handler都可以配置一个对应的Formatter来决定日志打印的格式，除了官方为我们提供的两种默认格式外，我们也可以自定义我们想要的日志打印格式。

我们只需要继承Formatter类，就可以创建一个自定义的日志格式处理逻辑了：

```java
public class MyFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return "我是自定义日志格式";
    }
}
```

接着我们通过上节课的方式，直接把ConsoleHandler的默认Formatter配置为我们自己的类：

```properties
java.util.logging.ConsoleHandler.formatter=com.test.MyFormatter
```

现在随便打印一个日志看看控制台会输出什么吧：

![QQ_1724780782795](https://s2.loli.net/2024/08/28/7wld2r6WfbH9VZE.png)

其中参数为LogRecord，它提供了当前日志记录的相关信息，比如：

```java
@Override
public String format(LogRecord record) {
    System.out.println("所在类: " + record.getSourceClassName());
    System.out.println("方法名称: " + record.getSourceMethodName());
    System.out.println("日志级别: " + record.getLevel().getLocalizedName());
    return "我是自定义日志格式";
}
```

因此，我们也可以利用这些属性来编写一个类似于的SimpleFormatter的日志格式，比如这里包含类名、时间等，类似于下面图中的日志格式：

![QQ_1724781560808](https://s2.loli.net/2024/08/28/FWTolmRGuX2J1cU.png)

我们来尝试编写一下：

```java
public String format(LogRecord record) {
    StringBuilder builder = new StringBuilder();
    //日期
    Date date = new Date(record.getMillis());
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    builder.append(dateFormat.format(date));
    //级别
    builder.append("  ").append(record.getLevel());
    builder.append(" --- ");
    //线程名称
    builder.append('[').append(Thread.currentThread().getName()).append(']');
    //类名称
    builder.append(" ").append(String.format("%-15s", record.getSourceClassName()));
    //消息内容
    builder.append(" : ").append(record.getMessage());

    return builder.toString();
}
```

至此，有关JUL相关的基础内容我们就介绍到这里，下一节我们接着来介绍其他框架对于其的兼容性。

### 第三方框架兼容性

我们发现，如果我们现在需要全面使用日志系统，而不是传统的直接打印，那么就需要在每个类都去编写获取Logger的代码，这样显然是很冗余的，能否简化一下这个流程呢？

前面我们学习了Lombok，我们也体会到Lombok给我们带来的便捷，我们可以通过一个注解快速生成构造方法、Getter和Setter，同样的，Logger也是可以使用Lombok快速生成的。

```java
@Log
public class Main {
    public static void main(String[] args) {
        System.out.println("自动生成的Logger名称："+log.getName());
        log.info("我是日志信息");
    }
}
```

只需要添加一个`@Log`注解即可，添加后，我们可以直接使用一个静态变量log，而它就是自动生成的Logger。我们也可以手动指定名称：

```java
@Log(topic = "打工是不可能打工的")
public class Main {
    public static void main(String[] args) {
        System.out.println("自动生成的Logger名称："+log.getName());
        log.info("我是日志信息");
    }
}
```

我们接着来看Mybatis，经过前面的学习，我们知道，Mybatis也有日志系统，它详细记录了所有的数据库操作等，要开启日志系统，我们需要进行配置：

```xml
<setting name="logImpl" value="STDOUT_LOGGING" />
```

`logImpl`包括很多种配置项，包括 SLF4J | LOG4J | LOG4J2 | JDK_LOGGING | COMMONS_LOGGING | STDOUT_LOGGING | NO_LOGGING，而默认情况下是未配置，也就是说不打印。将其设定为STDOUT_LOGGING表示直接使用标准输出将日志信息打印到控制台，现在我们也可以将其设置为JDK提供的日志框架：

```xml
<setting name="logImpl" value="JDK_LOGGING" />
```

将其配置为JDK_LOGGING表示使用JUL进行日志打印，因为Mybatis的日志级别都比较低，因此我们需要设置一下`logging.properties`默认的日志级别：

```properties
# RootLogger 的默认处理器为
handlers=java.util.logging.ConsoleHandler
# RootLogger 的默认的日志级别
.level=ALL

# 配置ConsoleHandler的默认level
java.util.logging.ConsoleHandler.level=ALL
```

这样，Mybatis就可以正确使用JDK的日志框架进行日志打印了，只不过格式稍微有点炸裂，可能还是得我们自己编写一个自定义的Formatter才行。

## Junit单元测试

**首先一问：** 我们为什么需要单元测试？

随着我们的项目逐渐变大，比如我们之前编写的图书管理系统，我们都是边在写边在测试，而我们当时使用的测试方法，就是直接在主方法中运行测试，但是，在很多情况下，我们的项目可能会很庞大，不可能每次都去完整地启动一个项目来测试某一个功能，这样显然会降低我们的开发效率，因此，我们需要使用单元测试来帮助我们针对于某个功能或是某个模块单独运行代码进行测试，而不是启动整个项目，比如：

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World");
        func1();
        func2();
        func3();
    }
    
    private static void func1() {
        System.out.println("我是第一个功能");
    }

    private static void func2() {
        System.out.println("我是第二个功能");
    }

    private static void func3() {
        System.out.println("我是第三个功能");
    }
}
```

如果现在我们想单独测试某一个功能的对应方法，而不是让整个项目完全跑起来，这就非常麻烦了。而单元测试则可以针对某一个方法直接进行测试执行，无需完整启动项目。

![QQ_1724827763008](https://s2.loli.net/2024/08/28/sk8mJcetoxlYNBI.png)

同时，在我们项目的维护过程中，难免会涉及到一些原有代码的修改，很有可能出现改了代码导致之前的功能出现问题（牵一发而动全身），而我们又不一定能立即察觉到，因此，我们可以提前保存一些测试用例，每次完成代码后都可以跑一遍测试用例，来确保之前的功能没有因为后续的修改而出现问题。我们还可以利用单元测试来评估某个模块或是功能的耗时和性能，快速排查导致程序运行缓慢的问题，这些都可以通过单元测试来完成，可见单元测试对于开发的重要性。

### 初次使用JUnit

首先需要导入JUnit依赖，Jar包已经放在官网资源库网盘中，直接去下载即可。同时IDEA需要安装JUnit插件（终极版默认是已经捆绑安装的，因此无需多余配置）之后我们会在Maven课程中继续介绍JUnit在Maven项目中如何进行使用。

安装好之后，我们就可以直接上手使用了，使用方式很简单，只需添加一个`@Test`注解即可快速创建新的测试用例，这里我们尝试新建一个类用于单元测试：

```java
public class MainTest {
    
}
```

接着就可以编写我们的测试用例了，现在我们需要创建一个`public`的无参无返回值方法（不能是静态方法）并在方法内编写我们的需要进行测试的代码：

```java
public void test1(){
    Main.func1();
}
```

最后在方法上添加`@Test`注解，此时IDEA会提示我们可以运行，旁边出现运行按钮：

![QQ_1724828793159](https://s2.loli.net/2024/08/28/Q6uP3yz1NTVIqDF.png)

接着点击运行，就可以直接执行我们的测试方法了，然后可以在控制台看到当前的测试用例耗时以及状态：

![QQ_1724834749820](https://s2.loli.net/2024/08/28/WQoIXkt1iBjmO89.png)

一个测试类中可以同时有多个测试案例：

```java
public class MainTest {

    @Test
    public void test1(){
        Main.func1();
    }

    @Test
    public void test2(){
        Main.func2();
    }

    @Test
    public void test3(){
        Main.func3();
    }
}
```

我们只需要点击类旁边的运行按钮，就可以直接执行当前类中所有的测试案例：

![QQ_1724839789746](https://s2.loli.net/2024/08/28/X1Anuvgohb6OyF2.png)

有些时候，可能我们并不想开启其中某个测试用例，我们也可以使用`@Disable`来关闭某一个测试用例：

```java
@Test
@Disabled
public void test2(){

}
```

此时再次全部运行，将忽略二号测试案例进行测试：

![QQ_1725176910092](https://s2.loli.net/2024/09/01/oYsG2yTtpEXknA1.png)

我们还可以为测试案例添加一个自定义的名称，不然测试案例一多我们就分不清楚到底哪个案例是干嘛的，我们需要使用`@DisplayName`注解来为其命名：

```java
@Test
@DisplayName("这个是一个快乐的测试案例")
public void test1(){
```

这样我们的控制台也可以看到对应的名称：

![QQ_1724839198905](https://s2.loli.net/2024/08/28/GKXxyd9AQSMeHOE.png)

除此之外，Junit还提供了一些预设的名称生成器，按照一定规则进行名称处理，可以通过`@DisplayNameGeneration`注解来配置使用，列表如下：

| 显示名称生成器        | 行为                                       |
| :-------------------- | :----------------------------------------- |
| `Standard`            | 方法名称作为测试名称。                     |
| `Simple`              | 同上，但是会删除无参数方法的尾随括号。     |
| `ReplaceUnderscores`  | 同上，但是会用空格替换方法名称中的下划线。 |
| `IndicativeSentences` | 包含类名和方法名称连接之后的名称。         |

当然，对于一个测试案例来说，我们肯定希望测试的结果是我们所期望的一个值，因此，如果测试的结果并不是我们所期望的结果，那么这个测试就应该没有成功通过，我们可以通过断言工具类`Assertions`来对结果进行判定：

```java
@Test
public void test1(){
    Random random = new Random();
    int value = random.nextInt() % 2;   //生成一个随机数，进行对2取余操作
    Assertions.assertEquals(1, value);   //如果是单数则匹配成功，如果不是则匹配失败
}
```

当测试案例失败时，控制台会出现应该AssertionFailedError错误，同时IDEA也会提示我们测试失败：

![QQ_1724838573199](https://s2.loli.net/2024/08/28/8usXgxHIePUZVAk.png)

有关断言相关工具，我们会在下一节进行详细介绍。

### 断言工具

JUnit提供了非常多的断言操作，相比JUnit 4，它们都被封装在一个新的`Assertions`类中，这些断言操作基本上都是用于判断某个测试结果是否符合我们的预期情况，其中最简单的就是判断结果是否等于某个值，这与我们上一节演示的操作是一样的：

```java
@Test
public void test1(){
    int a = 10, b = 5;
    int c = a + b;
  	//判断结果是否相等，前面的是预期结果，后面的就是实际结果
    Assertions.assertEquals(15, c);
}
```

当断言操作发现实际结果与预期不符时，会直接抛出异常告诉我们这个测试案例没有通过，并最终以失败状态结束。我们也可以为本次断言添加一个`message`来助于我们快速了解是什么类型的测试结果出现问题：

```java
Assertions.assertEquals(14, c, "计算结果验证");
```

此时控制台就会得到：

![QQ_1725178634235](https://s2.loli.net/2024/09/01/MlftC67rANvuGao.png)

除了使用值进行比较外，我们也可以直接对某个`boolean`类型的结果快速进行判断，使用`assertTrue`方法：

```java
Assertions.assertTrue(14 == c, "计算结果验证");
```

与其相似的还有两个相同对象的判断：

```java
Assertions.assertSame(999, 999);  //判断两个值是否为同一个对象
```

如果判断流程比较复杂，我们也可以使用Java8的Lambda来编写结果判断逻辑，提供一个BooleanSupplier对象：

```java
Assertions.assertTrue(() -> {
    if(c < 10) return true;
    if(c > 20) return false;
    return c == 15;
}, "计算结果验证");
```

对于更加复杂的组合结果判断，我们还可以使用`assertAll`来包含多个判断操作：

```java
Assertions.assertAll("整体测试",
        () -> Assertions.assertTrue(c == 14),
        () -> Assertions.assertTrue(c > 10),
        () -> Assertions.assertTrue(c < 20)
);
```

进行整体测试时，所有的测试结果将合并到一起输出。

除了我们上面提到的真假判断外，还有很多不同类型的结果判断，比如异常判断，我们希望这个案例抛出指定的异常：

```java
Assertions.assertThrows(IOException.class, () -> {
    System.out.println(1/0);
}, "此测试案例并未抛出指定异常");
```

由于此时抛出的是一个ArithmeticException并不是我们需要的IOException或是其子类，所以说断言失败：

![image-20241115174848264](https://s2.loli.net/2024/11/15/VwkAoeGIzChBtq2.png)

除了上述例子中出现的断言方法之外，JUnit还提供了上百种断言方法供大家使用，这里就不挨个介绍了。

除了断言工具外，对于一些不影响结果的测试，我们可以使用“假设”工具来实现对结果的判断但不作为测试结果的判断依据，它通常在执行给定测试没有意义时使用。

```java
public void test1(){
    Assumptions.assumeTrue(1 == 3);
}
```

测试结果中会将其显示为已忽略，而不是失败：

![image-20241115222449539](https://s2.loli.net/2024/11/15/urBZYCMxmNGKP8v.png)

### 条件测试和执行

有些时候我们可能需要配置各种条件来执行某些测试案例，比如某些测试案例必须在指定JDK版本执行，或是某些案例只需要在某个特定操作系统执行，Junit支持我们就为测试案例设置条件来实现这些功能。

比如，我们要限制某个测试案例只在指定操作系统下进行，那么就可以使用`@EnabledOnOs`来指定：

```java
@Test
@EnabledOnOs(OS.MAC)
public void test1(){
    System.out.println("我是只在Mac下执行的测试案例");
}

@Test
@EnabledOnOs(OS.WINDOWS)
//@DisabledOnOs(OS.MAC)  或是使用相反注解来为指定操作系统关闭此用例
public void test2(){
    System.out.println("我是只在Windows下执行的测试案例");
}
```

这样，当我们在指定操作系统下执行时，此测试案例才会启动，否则会直接忽略：

![image-20241115224429055](https://s2.loli.net/2024/11/15/Mh6eIo28tYkCc5A.png)

同样的，如果我们要指定在某个JDK版本执行测试案例，也可以使用`@EnabledOnJre`来进行指定：

```java
@Test
@EnabledOnJre(JRE.JAVA_8)
//@DisabledOnJre(JRE.JAVA_8) 或是使用相反的注解来为指定JDK关闭
public void test1(){
    System.out.println("我是只在Java8下执行的测试案例");
}

@Test
@EnabledOnJre(JRE.JAVA_17)
public void test2(){
    System.out.println("我是只在Java17下执行的测试案例");
}
```

或是一个指定的JDK版本范围：

```java
@Test
@EnabledForJreRange(min = JRE.JAVA_8, max = JRE.JAVA_17)
public void test1(){
    System.out.println("我是只在Java8-17下执行的测试案例");
}
```

除了这种简单判断外，我们还可以直接从系统属性中获取我们需要的参数来进行判断。

> 使用`System.getProperties()`来获取所有的系统属性，包括系统的架构、版本、名称等信息。

使用`@EnabledIfSystemProperty`来对系统属性进行判断：

```java
@Test
@EnabledIfSystemProperty(named = "os.arch", matches = "aarch64")
//其中matches参数支持正则表达式
public void test1(){
    System.out.println("我是只在arm64架构下做的测试");
}
```

当然，有时候为了方便，我们也可以直接读取环境变量：

```java
@Test
@EnabledIfEnvironmentVariable(named = "TEST_STR", matches = "666")
public void test1(){
    System.out.println("我是只在环境变量: TEST_STR = 666");
}
```

如果你认为这还不够灵活，你还可以直接声明一个自定义方法来进行判断：

```java
@Test
@EnabledIf("testCondition")
public void test1(){
    System.out.println("我是自定义的测试条件");
}

public boolean testCondition() {
    return 1 > 0;
}
```

> 条件方法可以位于测试类之外。在这种情况下，它必须用其*完全限定的名称*来引用
>
> ```java
> @EnabledIf("example.ExternalCondition#customCondition")
> ```
>
> ```java
> class ExternalCondition {
>   	/**
>      * 在几种情况下，条件方法需要static：
>      * 当@EnabledIf或@DisabledIf在类上使用时
>      * 当@EnabledIf或@DisabledIf用于@ParameterizedTest或@TestTemplate方法时
>      * 当条件方法位于外部类中时
>      */
>     static boolean customCondition() {
>         return true;
>     }
> }
> ```

### 生命周期、顺序控制和嵌套测试

我们可以自由设定某些操作在测试开始之前或之后执行，比如测试前的准备工作或是测试后的收尾工作：

```java
@Test
public void test1() {
    System.out.println("我是测试方法1");
}

@BeforeAll  //使用BeforeAll必须为static方法
public static void start() {
    System.out.println("我是测试前必须要执行的准备工作");
}
```

其中，`@BeforeAll`表示此准备工作在所有测试用例执行之前执行，这样，当测试开始前，会优先进行指定的准备工作，防止准备不足导致的测试失败。相反的，`@AfterAll`则会在所有测试用例完成之后执行。

除了在所有方法执行前后插入准备工作，我们也可以为所有的方法单个插入准备工作：

```java
@BeforeEach  //使用BeforeEach不能为static方法
public void start() {
    System.out.println("我是测试前必须要执行的准备工作");
}
```

这样，在每个测试用例执行之前，都会执行一次这里的准备工作：

![image-20241116004216666](https://s2.loli.net/2024/11/16/Kw2S1lxy3AbOI5n.png)

我们接着来了解一下测试类的生命周期。默认情况下，执行测试实际上也会对类进行实例化，并通过实例化对象来调用其中的测试方法，并且，每一个测试用例执行之前，都会创建一个新的对象，而不是直接执行：

```java
public class MainTest {

    public MainTest() {
        System.out.println("AAA");
    }
  
  ...
```

像这样，我们可以得到这样的输出结果：

![image-20241116004839643](https://s2.loli.net/2024/11/16/yGfK7EvemLFwpCt.png)

每次执行测试用例都会创建一个新的对象来执行，这在某些场景下可能会显得不太方便，比如初始化类需要花费大量时间或是执行非常费时的IO操作时，这会导致我们要花费大量时间来等待每次测试用例的初始化操作。我们也可以手动修改测试类的初始化行为，默认情况下为`PER_METHOD`模式：

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MainTest {
```

将其修改为`PER_CLASS`模式后，初始化操作只会执行一次，因为现在是以类为单位：

![image-20241116005150384](https://s2.loli.net/2024/11/16/8SyYQUWC2lLeEnB.png)

当然，如果您现在依然对测试用例执行前后有其他准备工作需求，也可以使用之前的`@BeforeEach`和`@AfterEach`来实现灵活控制。

有些时候我们可能需要控制某些测试案例的顺序，默认情况下，所有的测试案例都是按照方法的名称顺序来进行的，比如：

```java
@Test
public void test3() {  //按照名称顺序，虽然这里是第一个定义的，但是它是第三个
    System.out.println("我是测试用例3");
}

@Test
public void test1() {
    System.out.println("我是测试用例1");
}

@Test
public void test2() {
    System.out.println("我是测试用例2");
}
```

除了默认的名称顺序之外，JUnit提供了以下顺序：

- `MethodOrderer.DisplayName`：根据显示名称对测试方法进行*字母数字*排序（请参阅[显示名称生成优先级规则](https://junit.org/junit5/docs/current/user-guide/#writing-tests-display-name-generator-precedence-rules)）
- `MethodOrderer.MethodName`：根据测试方法的名称和形式参数列表，*以字母数字*排序
- `MethodOrderer.OrderAnnotation`：根据通过`@Order`注释指定的值对测试方法*进行数值*排序
- `MethodOrderer.Random`：*伪随机*排序测试方法，并支持自定义*种子*的配置

其中，注解顺序可以由我们自己通过注解来手动定义执行顺序：

```java
@Test
@Order(1)
void nullValues() {
    // perform assertions against null values
}
```

有些时候我们可能需要对测试用例进行进一步的分层，比如用户相关的测试全部归为一个组，而管理相关的测试全部归为一个组，此时我们可以使用嵌套测试，通过在类中定义多个内部类来完成：

```java
public class MainTest {

    @Test
    public void test() {
        System.out.println("我是外部测试类型");
    }

    @Nested
    class Test1 {

        @Test
        public void test1_1() {
            System.out.println("我是内部测试类型1-1");
        }

        @Test
        public void test1_2() {
            System.out.println("我是内部测试类型1-2");
        }
    }

    @Nested
    class Test2 {
        @Test
        public void test2_1() {
            System.out.println("我是内部测试类型2-1");
        }

        @Test
        public void test2_2() {
            System.out.println("我是内部测试类型2-2");
        }
    }
}
```

此时测试的结果展示也是嵌套的样式：

![image-20241116001214709](https://s2.loli.net/2024/11/16/35vkn4qQzlbVrif.png)

注意，当我们在嵌套测试中使用诸如`@BeforeEach`、`@BeforeAll`这种注解时，它仅会作用于所属内部类中的所有测试用例，而不是包含外部类中和其他内部类中的全部测试用例。

嵌套类的执行同样可以通过`@TestClassOrder`来控制嵌套类的执行顺序。

### 重复和参数化测试

对于某些存在随机性的测试案例，我们可能需要多次执行才能确定其是否存在某些问题，比如某个案例存在一个BUG，导致其10次里面会有1次出现错误，现在我们想要保证其10次都不会出现问题才算通过，此时我们就可以使用重复测试案例来使其多次执行：

```java
@RepeatedTest(10)
public void test1() {
    Random random = new Random();
    if (random.nextInt(10) == 0) {
        throw new IllegalStateException();
    }
}
```

此时会重复执行10次此案例，并且当每一次执行都没有出现问题时，才会正常通过：

![image-20241116010355502](https://s2.loli.net/2024/11/16/iLSas1TzdHtOgUI.png)

某些测试可能并不是固定单个输入参数，有时我们可能也需要对多个输入参数进行测试，来做到全方面的问题排查。它与重复测试比较类似，但是参数可以由我们自己决定：

```java
@ParameterizedTest  //使用此注解来表示此测试是一个参数化测试
@ValueSource(strings = { "aa", "bb", "ccc" })   //指定参数列表
public void test1(String str) {  //需要添加一个参数
    if (str.length() == 3) {
        throw new IllegalStateException();
    }
}
```

这里我们使用`@ValueSource`来进行参数来源设定，也就是需要进行测试的参数列表，接着下面会根据参数挨个执行此测试用例，保证每一种情况都正常执行：

![image-20241116021604397](https://s2.loli.net/2024/11/16/iT3UvfHc2Ns4koS.png)

这里的`@ValueSource`是最简单的一种参数设定，我们可以直接设置一系列值，支持以下类型：`short`、`byte`、`int`、`long`、`float`、`double`、`char`、`boolean`、`java.lang.String`、`java.lang.Class`

除了直接设置指定类型常量值，我们也可以传入空值或是一些为空的字符串、数组等：

```java
@ParameterizedTest
@NullSource  //将值设置为null进行测试
public void test1(String str) {
```

```java
@ParameterizedTest
@EmptySource  //将值设置为空进行测试，如空字符串、空数组、空集合等
public void test1(int[] arr) {
```

> `@NullAndEmptySource`：结合了`@NullSource`和`@EmptySource`两个注解的功能。

我们也可以使用枚举值来进行测试，比如我们希望测试某个枚举类型下所有的枚举作为参数进行测试：

```java
enum Type {
    SMALL, MEDIUM, LARGE
}

@ParameterizedTest
@EnumSource(Type.class)  //这将依次测试枚举类中的所有枚举
public void test1(Type type) {
    System.out.println(type);
}
```

或是指定某些枚举常量：

```java
@ParameterizedTest
//模式默认为INCLUDE，即使用指定的枚举常量进行测试
@EnumSource(mode = EnumSource.Mode.INCLUDE, names = { "SMALL", "LARGE" })
public void test1(Type type) {
    System.out.println(type);
}
```

除了以上方式获取参数，我们也可以使用特定的方法来生成我们需要的测试参数，只需要添加`@MethodSource`注解即可指定方法：

```java
@ParameterizedTest
@MethodSource("stringProvider")
public void test1(String str) {
    System.out.println(str);
}

static List<String> stringProvider() {
    return List.of("apple", "banana");
}
```

方法的返回值可以是任何可迭代（Iterable）内容，如数组、集合类、Stream等。同样的，对于其他类中的方法，需要和之前一样使用*完全限定的方法名称*来引用。

和方法一样，字段同样可以作为参数的来源，但它必须是静态的：

```java
static List<String> list = List.of("AAA", "BBB");

@ParameterizedTest
@FieldSource("list")
public void test1(String str) {
    System.out.println(str);
}
```

不仅仅是一个普通的集合或是数组可以作为字段参数来源，如Supplier这种懒加载的数据，也可以作为参数来源：

```java
static Supplier<List<String>> list = () -> List.of("AAA", "BBB");

@ParameterizedTest
@FieldSource("list")
public void test1(String str) {
    System.out.println(str);
}
```

当然，JUnit还支持从CSV表格中导入或自定义参数提供器等，这里就不做详细介绍了，官方文档：https://junit.org/junit5/docs/current/user-guide/#writing-tests-parameterized-tests-sources-ArgumentsSource

## 实战：图书管理系统

**注意：** 在开始之前，请先完成Maven视频课程，本次实战以及后续内容统一采用Maven进行依赖管理，不再使用之前的Jar包导入方式。

项目需求：

* 在线录入学生信息和书籍信息
* 查询书籍信息列表
* 查询学生信息列表
* 查询借阅信息列表
* 完整的日志系统

MySQL数据库JDBC驱动依赖：

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

Mybatis框架依赖：

```xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.16</version>
</dependency>
```

JUnit5依赖：

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.8.1</version>
    <scope>test</scope>
</dependency>
```

Lombok依赖：

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.36</version>
</dependency>
```

————————————————
版权声明：本文为柏码知识库版权所有，禁止一切未经授权的转载、发布、出售等行为，违者将被追究法律责任。
原文链接：https://www.itbaima.cn/zh-CN/document/pgevws6w2krkffa4