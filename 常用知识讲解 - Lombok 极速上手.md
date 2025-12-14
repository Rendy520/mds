![QQ_1722849668567](https://s2.loli.net/2024/08/05/8rIxAC7PGvXFq4c.png)

# Lombok极速上手

**注意：** 开始学习Lombok前至少需要保证完成JavaSE课程中的**注解**部分，本课程采用的版本为Java17。

我们发现，在以往编写项目时，尤其是在类进行类内部成员字段封装时，需要编写大量的get/set方法，这不仅使得我们类定义中充满了get和set方法，同时如果字段名称发生改变，又要挨个进行修改，甚至当字段变得很多时，构造方法的编写会非常麻烦：

```java
public class Account {
    private int id;
    private String name;
    private int age;
    private String gender;
    private String password;
    private String description;
    ...
}
```

依次编写类中所有字段的Getter和Setter还有构造方法简直是场灾难，后期字段名字变了甚至还得一个一个修改！

只不过这种问题在Java17之后得到的一定程度的解决，我们可以使用记录类型来快速得到一个自带构造方法、Getter以及重写ToString等方法的类：

```java
public record Account(int id, String name, int age, String gender, String password, String description) {
}
```

只不过，虽然记录类型不需要我们额外动手编写一部分代码了，但是它依然不够灵活，同时最关键的Setter也并未生成，所以说它依然没有大规模使用。

那么有没有一种更加完美的方案来处理这种问题呢？通过使用Lombok（小辣椒）就可以做到，它就是专门用于简化 Java 编程中的样板代码的，它通过注解的方式，能够自动生成常见的代码，比如构造函数、getter 和 setter 方法、toString 方法、equals 和 hashCode 方法等，从而使开发者能够专注于业务逻辑，而不必重复编写冗长的代码。官网地址：https://projectlombok.org

![img](https://s2.loli.net/2024/08/05/6rnhfZEKVXGN7C1.jpg)

使用Lombok后，你的代码就会变成这样：

```java
@Getter
@Setter
@AllArgsConstructor
public class Student {
    private Integer sid;
    private String name;
    private String sex;
}
```

使用Lombok提供的注解，即可一步到位，直接生成对应的Getter和Setter方法，包括构造方法、toString等都可以全包。

## 安装Lombok

首先我们需要导入Lombok的jar依赖，和jdbc依赖是一样的，放在项目目录下直接导入就行了。可以在这里进行下载：https://projectlombok.org/download，如果你已经学完了Maven，那么也可以直接使用Maven导入：

```xml
<dependency>
		<groupId>org.projectlombok</groupId>
		<artifactId>lombok</artifactId>
		<version>1.18.34</version>
		<scope>provided</scope>
</dependency>
```

然后我们要安装一下Lombok插件，由于IDEA终极版默认都安装了Lombok的插件，因此直接导入依赖后就可以使用了。

现在我们在需要测试的实体类上添加`@Data`注解试试看：

```java
import lombok.Data;

@Data
public class Account {
    private int id;
    private String name;
    private int age;
    private String gender;
    private String password;
    private String description;
}
```

接着测试一下是否可以直接使用，`@Data`会为我们的类自动生成Getter和Setter方法，我们可以直接调用：

```java
public static void main(String[] args) {
    Account account = new Account();
    account.setId(10);
}
```

如果运行后出现要求启用Lombok注解处理，请务必开启，否则会出现错误：

![QQ_1722851796836](https://s2.loli.net/2024/08/05/acAOFUE2sC9LuVH.png)

如果在启用注解处理后依然在运行时存在找不到符号问题，建议重启IDEA或是重启电脑后再试。

那么Lombok是如何做到一个注解就包揽了代码生成工作的呢？这里又要说到我们Java的编译过程，它可以分成三个阶段：

![img](https://s2.loli.net/2024/08/05/6HwkaOh9SJ7XCRm.png)

1. 所有源文件会被解析成语法树。
2. 调用注解处理器。如果注解处理器产生了新的源文件，新文件也要进行编译。
3. 最后，语法树会被分析并转化成类文件。

Lombok会在上述的第二阶段，执行*[lombok.core.AnnotationProcessor](https://github.com/rzwitserloot/lombok/blob/master/src/core/lombok/core/AnnotationProcessor.java)*，它所做的工作就是我们上面所说的，修改语法树，并将注解对应需要生成的内容全部添加到类文件中，这样，我们即使没有在源代码中编写的内容，也会存在于生成出来的class文件中。

## 使用Lombok

我们接着来为大家介绍Lombok提供的主要注解。

### 类属性相关

我们还是从类属性相关注解开始介绍，首先是`@Getter`，它用于自动生成Getter方法，定义如下：

```java
@Target({ElementType.FIELD, ElementType.TYPE})   //此注解可以添加在字段或是类型上
@Retention(RetentionPolicy.SOURCE)
public @interface Getter {
    AccessLevel value() default AccessLevel.PUBLIC;  //自动生成的Getter的访问权限级别
    AnyAnnotation[] onMethod() default {};  //用于添加额外的注解
    boolean lazy() default false;   //懒加载功能
    ...
}
```

它最简单的用法，就是直接添加到类上或是字段上：

```java
@Getter   //添加到类上时，将为类中所有字段添加Getter方法
public class Account {
    private int id;
    @Getter  //当添加到字段上时，仅对此字段生效
    private String name;
    private int age;
}
```

假设我们这里将`@Getter`编写在类上，那么生成得到的代码为：

```java
import lombok.Generated;

public class Account {
    private int id;
    private String name;
    private int age;

    public Account() {}

    @Generated
    public int getId() {   //自动为所有字段生成了对应的Getter方法
        return this.id;
    }

    ...省略
}
```

是不是感觉非常方便？而且使用起来也很灵活。注意它存在一定的命名规则，如果该字段名为`foo`，则将其直接按照字段名称命名为`getFoo`，但是注意，如果字段的类型为`boolean`，则会命名`isFoo`，这是比较特殊的地方。

我们接着来看Getter注解的其他属性，首先是访问权限，默认情况下为public，但是有时候可能我们只希望生成一个private的get方法，此时我们可以对其进行修改：

* PUBLIC - 对应public关键字
* PACKAGE - 相当于不添加任何访问权限关键字
* PRIVATE - 对应private关键字
* PROTECTED  -  对应protected关键字
* MODULE - 仅限模块内使用，与PACKAGE类似，相当于不添加任何访问权限关键字
* NONE - 表示不生成对应的方法，这很适合对类中不需要生成的字段进行排除

这里我们尝试将其更改为：

```java
@Getter(AccessLevel.PRIVATE)   //为所有字段生成private的Getter方法
public class Account {
    private int id;
    @Getter(AccessLevel.NONE)   //不为name生成Getter方法，字段上的注解优先级更高
    private String name;
    private int age;
}
```

得到的结果就是：

```java
public class Account {
    private int id;
    private String name;
    private int age;

    public Account() {
    }

    private int getId() {   //得到的就是private的Getter方法
        return this.id;
    }

    ...
}
```

我们接着来看它的`onMethod`属性，这个属性用于添加一些额外的注解到生成的方法上，比如我们要为Getter方法添加一个额外的`@Deprecated`表示它不推荐使用，那么：

```java
@Getter
public class Account {
    private int id;
    @Getter(onMethod_ = { @Deprecated })
    private String name;
    private int age;
}
```

此时得到的代码为：

```java
public class Account {
    ...

    /** @deprecated */
    @Deprecated   //由Lombok额外添加的注解
    public String getName() {
        return this.name;
    }
}
```

最后我们再来看看它的`lazy`属性，这是用于控制懒加载

> 懒加载就是在一开始的时候此字段没有值，当我们需要的时候再将值添加到此处。

只不过它有一些要求，我们的字段必须是private且final的：

```java
public class Account {
    @Getter(lazy = true)
    private final String name = "你干嘛";
}
```

生成的代码如下：

```java
public class Account {
  	//这里会自动将我们的字段修改为AtomicReference原子类型，以防止多线程环境下出现的问题
    private final AtomicReference<Object> name = new AtomicReference();

    ...

    //当我们调用getName才会去初始化字段的值，为了保证初始化只进行一次，整个过程与懒汉式单例模式一致
    public String getName() {
        Object $value = this.name.get();
        if ($value == null) {   //判断值是否为null，如果是则需要进行懒初始化
            synchronized(this.name) {    //对我们的字段加锁，保证同时只能进一个
                $value = this.name.get();
                if ($value == null) {    //再次进行一次判断，因为有可能其他线程后进入
                    String actualValue = "你干嘛";
                    $value = "你干嘛" == null ? this.name : "你干嘛";
                    this.name.set($value);
                }
            }
        }
				//返回得到的结果
        return (String)($value == this.name ? null : $value);
    }
}
```

有关原子类相关知识点，可以在JUC篇视频教程中进行学习，有关单例模式相关知识点，可以在Java设计模式篇视频教程中学习，这里不再赘述。我们作为使用者来说，只需要知道懒加载其实就是将字段的值延迟赋值给它了。比如下面这种场景就很适合：

```java
public class Account {
    @Getter(lazy = true)
    private final String name = initValue();

    private String initValue() {
        System.out.println("我不希望在对象创建时就执行");
        return "666";
    }
}
```

至此，有关`@Getter`注解相关的内容我们就介绍完毕了，我们接着来看`@Setter`注解，它与`@Getter`非常相似，用于生成字段对应的Setter方法：

```java
public class Account {
    @Setter
    private String name;
}
```

得到结果为：

```java
public class Account {
    private String name;

   	...
      
    public void setName(String name) {  //自动生成一个setter方法
        this.name = name;
    }
}
```

可以看到它同样会根据字段名称来生成Setter方法，其他参数与`@Getter`用法一致，这里就不重复介绍了。唯一一个不一样的参数为`onParam`，它可以在形参上的额外添加的自定义注解。

最后需要注意的是，如果我们手动编写了对应字段的Getter或是Setter方法（按照上述命名规则进行判断）那么Lombok提供的注解将不会生效，也不会覆盖我们自己编写的方法：

```java
public class Account {
    @Setter
    private String name;

    public void setName(int name) {   //即使上面添加Setter注解，这里也不会被覆盖，但是仅限于同名不同参的情况
        System.out.println("我是自定义的");
        this.name = name;
    }
}
```

如果出现同名不同参数的情况导致误判，我们也可以使用`@Tolerate`注解使Lombok忽略它的存在，继续生成。

### 构造方法相关

Lombok也可以为我们自动生成对应的构造方法，它提供了三个用于处理构造方法的注解，我们来依次认识一下它们。首先是最简单的`@AllArgsConstructor`，它用于为类中所有字段生成一个构造方法：

```java
@AllArgsConstructor
public class Account {
    private int id;
    private String name;
    private int age;
}
```

它只能添加到类上，之后生成：

```java
public class Account {
    private int id;
    private String name;
    private int age;

    public Account(int id, String name, int age) {   //自动生成一个携带所有参数的构造方法
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
```

我们接着来看它的一些属性：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface AllArgsConstructor {
    //用于生成一个静态构造方法
    String staticName() default "";
    
    //用于在构造方法上添加额外的注解
    AnyAnnotation[] onConstructor() default {};
    
  	//设置构造方法的访问权限级别
  	AccessLevel access() default lombok.AccessLevel.PUBLIC;
    ...
}
```

其中`onConstructor`和`access`和我们上一节介绍的内容差不多，这里就不再多说了，我们直接来看它的`staticName`属性，它主要用于生成静态构造方法，现在很多类都包含一些静态构造方法，比如：

```java
List<String> strings = List.of("A", "B", "C", "D");
```

这里的`List.of()`其实就是一种静态构造方法，通常用于快速构造对应的类对象，我们也可以像这样去编写，只需要将`staticName`设置一个名字即可：

```java
@AllArgsConstructor(staticName = "with")
public class Account {
    ...
}
```

此时得到结果为：

```java
public class Account {
    ...

    //强制生成一个带全部参数的private方法，不可修改
    private Account(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public static Account with(int id, String name, int age) {
        return new Account(id, name, age);
    }
}
```

我们在使用时，需要调用此静态构造方法来创建对象：

```java
Account account = Account.with(1, "小明", 18);
```

官方说这种方式非常适合用作泛型的类型推断，简化代码，比如 `MapEntry.of("foo", 5)` 而不是更长的`new MapEntry<String, Integer>("foo", 5)`

现在有了全参构造，但是此时我们又需要一个无参构造怎么办呢，Lombok早就为我们准备好了，我们只需要再添加一个`@NoArgsConstructor`注解即可：

```java
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    ...
}
```

这样我们就可以得到一个既有全参构造又有无参构造的类：

```java
public class Account {
    ...

    public Account() {}

    public Account(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
```

但是注意，由于这里会生成一个无参构造，当我们使用`@NoArgsConstructor`时类中不允许存在final类型的字段，否则会出现错误：

![QQ_1722937534148](https://s2.loli.net/2024/08/06/PiYOwgRElzM2yb1.png)

只不过，`@NoArgsConstructor`有一个`force`属性，它可以在创建无参构造时，为final类型的字段给一个默认值，这样就可以同时存在了：

```java
@NoArgsConstructor(force = true)   //强制开启
@AllArgsConstructor
public class Account {
    private final int id;   //字段必须初始化
    private String name;
    private int age;
}
```

得到的结果为：

```java
public class Account {
    ...

    public Account() {
        this.id = 0;   //强行生成一个无参构造，但是这里也会为属性设置一个默认值，不然编译会报错
    }

    ...
}
```

我们来看最后一个构造相关的注解，`@RequiredArgsConstructor`用于生成那些需要初始化的参数的构造方法，也就是说类中哪些字段为final，它就只针对这些字段生成对应的构造方法，比如：

```java
@RequiredArgsConstructor
public class Account {
    private final int id;
    private String name;
    private final int age;
}
```

生成的结果为：

```java
public class Account {
    ...

    public Account(int id, int age) {   //只为fianl字段id和age生成了对应的构造方法
        this.id = id;
        this.age = age;
    }
}
```

有关构造函数相关内容我们就介绍到这里。

### 打印对象

我们也可以使用Lombok为类生成toString、equals以及hashCode等方法，我们首先来看最简单的toString方法生成，只需要在类上添加一个`@ToString`注解即可：

```java
@ToString
@AllArgsConstructor
public class Account {
    private int id;
    private String name;
    private int age;
}
```

```java
public static void main(String[] args) {
    Account account = new Account(1, "小明", 18);
    System.out.println(account);   //尝试直接打印
}
```

这样就可以直接得到一个格式化好的字符串：

![QQ_1722938247064](https://s2.loli.net/2024/08/06/12yk6qGSsIM5B7p.png)

我们来看看生成出来的内容：

```java
public class Account {
    ...

    public String toString() {   //自动为每个字段都生成打印
        return "Account(id=" + this.id + ", name=" + this.name + ", age=" + this.age + ")";
    }
  
    ...
}
```

是不是感觉非常方便？一个注解就搞定了这么繁杂的代码。我们来看看它有哪些参数：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ToString {
    //是否在打印的内容中带上对应字段的名字
    boolean includeFieldNames() default true;
    
    //用于排除不需要打印的字段（这种用法很快会被移除，不建议使用）
    String[] exclude() default {};
    
    //和上面相反，设置哪些字段需要打印，默认打印所有（这种用法很快会被移除，不建议使用）
    String[] of() default {};
    
    //不仅为当前类中所有字段生成，同时还调用父类toString进行拼接
    boolean callSuper() default false;
    
    //默认情况下生成的toString会尽可能使用get方法获取字段值，我们也可以手段关闭这个功能
    boolean doNotUseGetters() default false;
    
    //开启后将只为字段或get方法上添加了@ToString.Includ注解的内容生成toString方法，白名单模式
    boolean onlyExplicitlyIncluded() default false;
    
    /**
     * 用于排除toString中的字段
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Exclude {}
    
    /**
     * 用于手动包含toString中的字段
     */
    @Target({ElementType.FIELD, ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Include {
			 //配置字段打印顺序的优先级
       int rank() default 0;
       
       //配置一个自定义的字段名称进行打印
       String name() default "";
    }
}
```

我们首先来看`@ToString`对于get方法的特殊机制，它会尽可能使用我们自定义的get方法获取字段的值，命名规则判定和之前一样：

```java
@ToString
@AllArgsConstructor
public class Account {
    ...

    public String getName() {
        return name + "同学";   //编写了一个自定义的getName方法
    }
}
```

打印得到的结果为：

![QQ_1722939058554](https://s2.loli.net/2024/08/06/tZrsA52R3pyWbqc.png)

有时候可能我们希望的是直接打印字段原本的值，所以为了避免这种情况，我们可以手动为`@ToString`配置doNotUseGetters属性：

```java
@ToString(doNotUseGetters = true)
@AllArgsConstructor
public class Account {
```

这样生成的代码中就不会采用getter方法了。

我们接着来看字段的包含和排除，首先当我们在类上添加`@ToString`后，默认会为所有字段生成对应的ToString操作，但是如果我们需要排除某些字段，我们可以使用`@ToString.Exclude`注解，将其添加到对应的字段上时，就不会打印了：

```java
@ToString
@AllArgsConstructor
public class Account {
    @ToString.Exclude
    private int id;
    private String name;
    private int age;
}
```

![QQ_1722939596344](https://s2.loli.net/2024/08/06/dGH7vLc5IgsbZP4.png)

这种黑名单模式虽然用起来很方便，但是白名单模式就会很麻烦，也就是我们需要指定哪些字段打印，哪些字段才打印，考虑到这个问题，Lombok也为我们提供了白名单相关的注解，要开启白名单模式，需要将onlyExplicitlyIncluded设置为真，接着为我们需要打印的字段添加`@ToString.Include`注解：

```java
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Account {
    @ToString.Include
    private int id;
    @ToString.Include
    private String name;
    private int age;
}
```

![QQ_1722940029901](https://s2.loli.net/2024/08/06/3qT52QNPhtiRyg4.png)

不过值得注意的是，`@ToString.Include`不仅可以对字段生效，还可以对方法生效，它可以将某些方法执行后的结果也包含在toString中：

```java
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Account {
    ...

    @ToString.Include
    public String test() {
        return "你干嘛";
    }
}
```

![QQ_1722940194659](https://s2.loli.net/2024/08/06/WaQtwJXVbPHSoT4.png)

我们来看看`@ToString.Include`可以设置的一些参数，比如我们可以手动为字段起一个用于打印的名字：

```java
@ToString.Include(name = "编号")
private int id;
@ToString.Include(name = "名字")
private String name;
```

![QQ_1722940310376](https://s2.loli.net/2024/08/06/tPcuIzwgUW8kOmN.png)

默认情况下toString打印的字段属性是按照声明顺序进行的，我们也可以手动为其指定顺序：

```java
@ToString.Include(name = "编号")
private int id;
@ToString.Include(name = "名字", rank = 1)   //rank越大，越靠前，默认为0
private String name;
```

![QQ_1722940433118](https://s2.loli.net/2024/08/06/W48mcTetgYGZlFs.png)

### 比较相关

Lombok可以为我们自动生成类属性的比较方法以及对应的HashCode计算。我们只需要为类添加`@EqualsAndHashCode`注解，即可开启：

```java
@EqualsAndHashCode
@AllArgsConstructor
public class Account {
    private int id;
    private String name;
    private int age;
}
```

此时生成的类：

```java
public class Account {
    ...

    public boolean equals(Object o) {   //自动生成的equals重写方法，包含所有参数的比较
        ... 
    }

    protected boolean canEqual(Object other) {
        return other instanceof Account;
    }

    public int hashCode() {   //自动生成的hashCode重写方法
        ...
    }

    ...
}
```

它自动为我们所有的参数生成了对应的比较方法，我们可以来测试一下生成的代码是否可以正常运行：

```java
public static void main(String[] args) {
    Account a1 = new Account(1, "小明", 18);
    Account a2 = new Account(1, "小明", 18);
    System.out.println(a1.equals(a2));   //结果为true
}
```

`@EqualsAndHashCode`注解的参数大部分与我们上节讲解的`@ToString`类似，比如`exclude`、`of`、`callSuper`（默认关闭，开启后调用equals比较前先调用父类的equals进行一次比较）、`doNotUseGetters`以及生成的方法中需要额外携带的注解`onParam`属性等。

它同样可以使用`onlyExplicitlyIncluded`属性来开启白名单模式，我们可以使用以下注解来自由控制哪些属性会作为比较的目标，哪些需要排除：

* `@EqualsAndHashCode.Exclude`  -  用于排除不需要参与比较的字段。
* `@EqualsAndHashCode.Include`  -  开启白名单模式后，用于标明哪些字段需要参与比较。

它与前面讲解的`@ToString`一样，我们可以来试试看：

```java
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Account {
    @EqualsAndHashCode.Include
    private int id;   //此时只对id字段进行比较
    private String name;
    private int age;
}
```

```java
public static void main(String[] args) {
    Account a1 = new Account(1, "小明", 18);
    Account a2 = new Account(1, "小红", 17);
    System.out.println(a1.equals(a2));   //由于只比较id字段，因此结果为true
}
```

和`@ToString.Include`一样，我们也可以将其添加到一个方法上，使其结果也参与到比较中。

```java
@EqualsAndHashCode.Include
public int test() {
    return 1;
}
```

不过，`@EqualsAndHashCode.Include`有一个`replaces`属性，它可以用于将当前方法的结果替代目标字段进行比较，比如我们像这样编写：

```java
@EqualsAndHashCode.Include(replaces = "id")   //此时id字段的比较不会直接比较其本身，而是改为调用此方法获取对应结果进行比较
public int test() {
    return 1;
}
```

这样，在生成的equals方法中，需要比较id字段时，会直接比较两个对象调用`test`方法的结果。

最后我们来看看它独特的hashCode缓存机制，我们可以通过设置`cacheStrategy`属性来控制hashCode结果的缓存，这有助于优化程序的性能，默认情况下缓存为NEVER也就是不启用，我们也可以开启LAZY模式：

```java
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
@AllArgsConstructor
public class Account {
    private int id;
    private String name;
    private int age;
}
```

此时，生成的hashCode方法在第一次生成结果后，后续将一直使用同样的结果直接返回，避免二次计算：

```java
public int hashCode() {
    if (this.$hashCodeCache != 0) {  //判断是否生成过hashCode，直接使用缓存
        return this.$hashCodeCache;
    } else {
        ...
        this.$hashCodeCache = result;  //设置缓存
        return result;
    }
}
```

这对于那些属性值不会发生变化的实体类来说，是一个很好的选择，但是如果后续使用中字段值可能会发生变化从而影响HashCode的结果，则不建议使用此功能。

至此，我们已经介绍了`@ToString`，`@EqualsAndHashCode`，`@Getter`和`@Setter`以及构造方法相关注解，很多情况下我们可能需要在用作数据传递的实体类上将它们一并用上，我们可以直接使用`@Data`注解，它等价于我们在类上添加这些注解：`@Getter` `@Setter` `@RequiredArgsConstructor` `@ToString` `@EqualsAndHashCode`

```java
@Data   //一个注解直接把get和set方法、构造方法、toString、equals全包了
public class Account {
    private int id;
    private String name;
    private int age;
}
```

当然，如果我们希望某个类只作为结果，里面的数据不可进行修改，我们也可以使用`@Data`的只读版本`@Value`，它等价于添加注解：`@Getter` `@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)` `@AllArgsConstructor` `@ToString` `@EqualsAndHashCode`

```java
@Value
public class Account {  //类会自动变成final类型
    int id;   //在`@FieldDefaults`配置下，会自动将类属性变为private和final的状态，这里我们无需手动编写
    String name;
    int age;
}
```

只不过在Java17之后，这种类完全可以被记录类型平替，因此使用时IDEA会提示我们直接使用记录类型。

### 建造者模式

**注意：** 开始之前建议先了解Java设计模式篇中讲解的**建造者模式**。

Lombok也可以快速将一个类转换为建造者模式，只需添加`@Builder`注解即可：

```java
@Builder
@ToString
public class Account {
    int id;
    String name;
    int age;
}
```

接着，我们就可以直接使用Lombok生成的builder来创建对象：

```java
public static void main(String[] args) {
    Account account = Account.builder()
            .id(1)
            .name("小明")
            .age(18)
            .build();
    System.out.println(account);
}
```

我们可以来看一下类中生成的Builder内部类，它就是整个建造者模式的核心了：

```java
public static class AccountBuilder {
    private int id;
    private String name;
    private int age;

    AccountBuilder() {
    }

    public AccountBuilder id(int id) {
        this.id = id;
        return this;
    }

    public AccountBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AccountBuilder age(int age) {
        this.age = age;
        return this;
    }

    public Account build() {
        return new Account(this.id, this.name, this.age);
    }

    public String toString() {
        return "Account.AccountBuilder(id=" + this.id + ", name=" + this.name + ", age=" + this.age + ")";
    }
}
```

我们接着来看Builder注解相关参数：

```java
public @interface Builder {
    
  	//用于设置默认值
    @Target(FIELD)
    @Retention(SOURCE)
    public @interface Default {}

    //用于获取builder对象的静态方法，默认情况下名称为builder()
    String builderMethodName() default "builder";
    
    //Builder对象最终构建的方法名称，默认情况下名称为build()
    String buildMethodName() default "build";
    
    //内部生成的Builder类名称，默认为 类名称+Builder
    String builderClassName() default "";
    
    //生成一个用于将对象转回Builder的方法，默认情况下不启用
    boolean toBuilder() default false;
    
    //生成的Builder极其相关方法的访问权限级别
    AccessLevel access() default lombok.AccessLevel.PUBLIC;

    //Builder中各个属性设置器的前缀，默认没有
    String setterPrefix() default "";
    
    //用于配合toBuilder方法使用，指明如何获取指定字段的值
    @Target({FIELD, PARAMETER})
    @Retention(SOURCE)
    public @interface ObtainVia {
       //通过其他字段获取
       String field() default "";
       //通过方法获取
       String method() default "";
       //如果上面通过方法获取，这里可以指明是否为静态方法
       boolean isStatic() default false;
    }
}
```

这里我们先从`@Builder.Default`注解开始介绍，这个注解是用于设定默认值的：

```java
@Builder
@ToString
public class Account {
    int id;
    @Builder.Default   //默认值必须进行初始化
    String name = "小米";
    int age;
}
```

注意，如果我们只是单纯为name字段初始化而不添加`@Builder.Default`注解，那么设置的初始值并不会作为Builder采用的默认值。

接着我们来看`toBuilder`参数，开启后，它会为我们的类生成一个`toBuilder`方法，能够使得一个构建好的实体类回炉重造：

```java
public static void main(String[] args) {
    Account account = Account.builder()
            .id(1)
            .name("小黑")
            .age(18)
            .build();
    Account.AccountBuilder builder = account.toBuilder();
}
```

它还有一个`@Builder.ObtainVia`配合使用，它用于告诉Lombok将实体类转换为Builder时，指定的字段值应该如何获取，正常情况下，如果不使用它，那么会直接获取当前实体类的对应属性值。我们也可以修改：

```java
@Builder(toBuilder = true)
@ToString
public class Account {
    int id;
    String name;
    @Builder.ObtainVia(field = "id")   //当从实体类转换回Builder时，age的值从id字段获取
    int age;
}
```

### 变量相关

很多语言都有`var`关键字，它用于声明一个变量，但是我们无需手动确定其类型。在Java10之后，同样支持了`var`关键字，它可以自动推断类型，而不需要像之前那样手动指明变量的类型：

```java
var a = "";   //自动推断变量a的类型为String
```

而在Lombok中，也为我们提供了对应的注解类型，使得我们可以像其他语言那样使用：

* `var` - Java10之前可用，Java10之后会出现无法导入的问题，功能完全一样。
* `val` - 与var类似，但是它是不可变的，等价于final变量。

```java
public static void main(String[] args) {
    val a = "Hello World";   //等价于final变量
    System.out.println(a);
}
```

只不过对于Java来说，这种玩法似乎很少有人愿意接受，大家还是更喜欢直接明确类型。

### 资源释放和异常处理

有些时候，我们可能会用到一些需要释放资源的对象，比如Stream这类，在Java7的时候推出了try-with-resource语法，它大大减少了我们编写资源释放语句的成本：

```java
public static void main(String[] args) throws IOException {
  	//使用try-with-resource会自动生成close相关操作的代码
    try (FileInputStream in = new FileInputStream("test.exe")){
        byte[] bytes = in.readAllBytes();
        System.out.println(new String(bytes));
    }
}
```

虽然这种方式已经能够很大程度减少我们的工作，但是它依然不够山里灵活，Lombok为我们提供了一个更加简单方便的注解，我们只需要在需要释放资源的变量前添加`@Cleanup`即可：

```java
public static void main(String[] args) throws IOException {
  	//添加即可自动释放资源
    @Cleanup FileInputStream in = new FileInputStream("test.py");
    byte[] bytes = in.readAllBytes();
    System.out.println(new String(bytes));
}
```

生成的代码为：

```java
public static void main(String[] args) throws IOException {
    FileInputStream in = new FileInputStream("test.py");
    try {
        byte[] bytes = in.readAllBytes();   //自动将后续操作添加到try语句块中
        System.out.println(new String(bytes));
    } finally {   //在finally中对资源进行释放
        if (Collections.singletonList(in).get(0) != null) {
            in.close();
        }
    }
}
```

当然，我们也可以自定义需要关闭资源的方法：

```java
static class Test {
    public void end() {
        System.out.println("关闭资源");
    }
}

public static void main(String[] args) throws IOException {
    @Cleanup("end") Test test = new Test();
}
```

我们接着来看下一个更好用的注解，各位小伙伴在使用Steam时多多少少都会遇到手动处理IOException的问题，这就导致我们很多地方都要去写`throws`来标明方法会抛出的异常，这实在是太不方便了。

Lombok为我们提供了一个`@SneakyThrows`注解，就像它的名字一样悄悄咪咪地抛出，不需要我们手动指定：

```java
@SneakyThrows
public static void main(String[] args) {
    @Cleanup FileInputStream in = new FileInputStream("test.py");
    byte[] bytes = in.readAllBytes();
    System.out.println(new String(bytes));
}
```

实际上在编译后会生成：

```java
public static void main(String[] args) {
    try {   //自动使用trycatch包裹内部，出现问题直接原样抛出
        ...
    } catch (Throwable var7) {
        Throwable $ex = var7;
        throw $ex;
    }
}
```

官方解释这个注解是为了避免 javac 的异常捕获检查，减少手动编写异常抛出的代码。

### 非空判断

由于Java除了基本类型之外其他类型都有可能存在`null`的情况，因此空指针问题一直困扰着所有人，很多情况下我们不得不在传递参数时进行校验，以确保传入的参数不是`null`才能安全地进行后续操作。最简单的方法就是开始的时候直接对其进行判定：

```java
/***
 * 测试方法
 * @param text 要求不为null的情况下打印
 */
public static void test(String text){
    if(text == null) return;
    System.out.println(text);
}
```

在Java8之后我们也可以利用Optional将其写的更加优雅：

```java
//利用Optional和Lambda表达式一行搞定
public static void test(String text){
    Optional.ofNullable(text).ifPresent(System.out::println);
}
```

不过，虽然我们可以像这样进行一次预先判断，但是它依然不够方便，如果我们需要对所有参数都进行校验，这简直就是地狱，Lombok为我们提供了一种更好的方式进行空判断，我们只需要在参数的前面添加`@NonNull`则会自动为其生成对应的判空代码：

```java
public static void test(@NonNull String text){
    System.out.println(text);
}
```

生成结果为：

```java
public static void test(@NonNull String text) {
    if (text == null) {
        throw new NullPointerException("text is marked non-null but is null");
    } else {
        System.out.println(text);
    }
}
```

除了方法的形式参数外，`@NonNull`也可以添加到局部变量上 ，但是只会有一个警告效果。

### 锁处理

**注意：** 本版块涉及到JUC相关知识点。

很多时候我们可能需要为方法加锁，这可以防止多线程环境下导致的一些并发问题。比如：

```java
private static int a = 0;  //如果多个线程同时为a做自增，由于底层指令并不是原子的，可能会出现问题

@SneakyThrows
public static void main(String[] args) {
    for (int i = 0; i < 10; i++) {   //创建10个线程同时为a做自增操作
        new Thread(Main::counter).start();
    }
    Thread.sleep(3000);
    System.out.println(a);
}

public static synchronized void counter() {  //添加synchronized来保证同一时间只有一个线程运行
    for (int i = 0; i < 1000000; i++) a++;
}
```

只不过直接在方法上添加`synchronized`关键字，会导致不同的方法使用同一把锁：

```java
public synchronized void test1(){ }
public synchronized void test2(){ }
```

这里`test1`和`test2`都是类的成员方法，并且添加了`synchronized`，默认情况下它们会采用当前对象作为锁，但是有些时候我们可能并不希望这两个方法是使用同一把锁，让它们的执行互不影响。这个时候我们可能会采用：

```java
final Object lock1 = new Object(); 
final Object lock2 = new Object();

public void test(){
    synchronized (lock1) {
        
    }
}

public void test2(){
    synchronized (lock2) {

    }
}
```

此时我们为这两个方法分别分配一个锁对象，然后采用同步代码块的方式进行加锁，这样就可以解决我们上面的问题了，但是这样写着实在是太麻烦了，不仅要写一大堆`synchronized`代码块，还要建很多锁对象。Lombok为我们提供了一个`@Synchronized`注解，它可以自动生成同步代码块：

```java
private final Object lock1 = new Object();

@Synchronized("lock1")  //直接指定作为锁的变量名称
public void test() {  }
```

它会自动生成对应的同步代码块：

```java
public void test() {
    synchronized(this.lock1) {
        ;
    }
}
```

如果我们不填写锁名称，那么它会按照我们的方法性质添加一把默认的锁：

* 成员方法：统一使用一个名称为`$lock`的锁作为对象锁。
* 静态方法：统一使用一个名称为`$LOCK`的锁作为类锁。

除了`@Synchronized`之外，Lombok也为我们提供了一个JUC版本，它采用ReentrantLock作为锁，注解名称为`@Locked`：

```java
@Locked
public void test() {

}
```

它采用ReentrantLock，同样在进入方法前加锁，执行完成后解锁：

```java
public class Main {
  	//和上面一样，直接添加注解会根据方法使用统一的锁
    private final Lock $lock = new ReentrantLock();

    ...

    public void test() {
        this.$lock.lock();
        this.$lock.unlock();
    }
}
```

它还可以进一步细分为读写锁，我们可以单独为某个方法添加读锁或是写锁。

### 日志相关

我们在项目中可能需要使用到各种日志，Lombok为不同的日志框架提供了一个快速注解。我们在项目中常常需要将日志对象创建在类中：

```java
private static Logger log = Logger.getLogger(Main.class.getName());

public static void main(String[] args) {
    log.info("我是日志");
}
```

而有了Lombok之后，我们可以直接在类上添加`@Log`注解：

```java
@Log
public class Main {
    public static void main(String[] args) {
        log.info("我是日志");  //自动生成一个log对象，我们可以直接使用
    }
}
```

针对于不同的日志框架，我们可以使用不同的注解来创建：

```
@CommonsLog
```

创造`private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(LogExample.class);`

```
@Flogger
```

创造`private static final com.google.common.flogger.FluentLogger log = com.google.common.flogger.FluentLogger.forEnclosingClass();`

```
@JBossLog
```

创造`private static final org.jboss.logging.Logger log = org.jboss.logging.Logger.getLogger(LogExample.class);`

```
@Log
```

创造`private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(LogExample.class.getName());`

```
@Log4j
```

创造`private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LogExample.class);`

```
@Log4j2
```

创造`private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(LogExample.class);`

```
@Slf4j
```

创造`private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogExample.class);`

```
@XSlf4j
```

创造`private static final org.slf4j.ext.XLogger log = org.slf4j.ext.XLoggerFactory.getXLogger(LogExample.class);`

## 试验性功能

以下功能为Lombok提供的试验性功能，不保证在后续版本中会作为正式功能推出，有可能会删除。

### 强化Getter和Setter

虽然Lombok已经为我们提供了`@Getter`和`@Setter`注解，方便我们快速生成对应的get和set方法，但是我们还可以使其变得更加强大。Lombok为我们提供了一个`@Accessors`注解，用于配置lombok如何生成和查找getters和setters。

比如现在非常流行的链式设置属性，set方法直接返回当前对象，这样就可以一直set下去，很爽：

```java
public static void main(String[] args) {
    Account account = new Account();
    account.setId(1).setAge(18).setName("小明");
}
```

要实现这种方式也非常简单，我们只需要添加`@Accessors`注解即可：

```java
@Accessors(chain = true)  //将chain设置为true开启链式
@ToString
@Setter
public class Account {
    int id;
    String name;
    int age;
}
```

我们接着来看看`@Accessors`的其他属性，首先是`fluent`，它可以直接去掉Getter或Setter的前缀，直接使用字段名称作为方法名称：

```java
public static void main(String[] args) {
    Account account = new Account();
    account.id(1).age(18).name("小明");    //大幅度简化代码
}
```

注意，开启`fluent`后默认也会启用`chain`属性。

我们接着来看`makeFinal`属性，它用于将所有生成的方法设置为final，防止子类进行修改：

```java
public final void setId(int id) {
    this.id = id;
}
```

最后还有一个`prefix`属性，这个功能比较特殊，它可以在你有一些特殊命名的情况下使用，比如：

```java
public class Account {
    int userId;   //在命名时，有些人总爱添加点前缀
    String userName;
    int userAge;
}
```

这种情况下生成出来的Getter也会按照这样进行命名，非常不好用：

```java
public void setUserId(int userId) {
    this.userId = userId;
}
```

通过对`@Accessors`添加`prefix`属性，可以指示在生成Getter或Setter时去掉前缀，比如：

```java
@Accessors(prefix = "user")
@ToString
@Setter
public class Account {
    int userId;
    String userName;
    int userAge;
}
```

这样生成的方法就会直接去掉对应的前缀了。只不过，一旦设置了前缀，那么所有不是以此前缀开头的字段，会直接不生成对应的方法。如果各位小伙伴觉得`@Accessors`加到类上直接作用于全部字段，控制得不是很灵活，我们也可以将其单独放到某个字段上进行控制：

```java
@ToString
@Setter
public class Account {
    int userId;
    @Accessors(prefix = "user")
    String userName;
    int userAge;
}
```

### 添加新方法到现有的类

如果各位小伙伴编写过Kotlin语言，其中有一个非常有意思的扩展函数功能，它可以做到无需修改原本的类代码，直接为对应的类添加一些额外的函数，这对于我们开发者来说是一个非常方便的功能：

```java
//为官方的String类添加一个新的test函数，使其返回自定义内容
fun String.test() = "666"

fun main() {
    val text = "Hello World"
    println(text.test())  //就好像String类中真的有这个函数一样
}
```

而在Java语言中，Lombok使得这一切成为了可能。Lombok为我们添加了一个`@ExtensionMethod`注解，它可以实现类似的效果，比如现在我们要给String添加一个扩展的方法，使其根据空格进行划分，变成字符串数组，我们创建一个新的类来编写：

```java
public class ExtensionObject {
    //这个类用于我们编写额外的扩展方法
}
```

扩展方法是一种特殊的**静态方法**，它的参数和返回值是有要求的，比如现在我们想要为String类型添加一个扩展方法，就好像是String所具有的成员方法那样，实际上就是对一个String对象进行操作，所以说，编写对应的静态方法，也需要一个对应类型的参数才可以，先写一下：

```java
public static void splitByBlank(String text) {
    
}
```

接着，这个方法调用完后会生成一个字符串数组，所以说我们需要将其返回值改成我们需要的类型，然后就可以编写对应的处理逻辑了：

```java
public static String[] splitByBlank(String text) {
    return text.split("\\s+");
}
```

好了，现在这个方法已经完全具备成为一个扩展方法的资格了，它实际上就是一个参数完全对应我们使用方式的静态方法，那我们来看看如何使用它：

```java
@ExtensionMethod({ ExtensionObject.class })   //首先在需要使用这种扩展方法的类上添加我们编写扩展方法所在类的名字
public class Main {
```

接着我们就可以直接使用了：

```java
@ExtensionMethod({ ExtensionObject.class })
public class Main {
    public static void main(String[] args) {
        String[] words = "Hello World!".splitByBlank();   //就好像真的是String的方法那样
        System.out.println(Arrays.toString(words));
    }
}
```

扩展方法不仅能为其添加新的方法，甚至还可以直接覆盖已存在的方法：

```java
public static int length(String text) {
    return 0;
}
```

当我们添加这种方法后，它刚好对应的就是String类的length方法，这会导致原本的方法被覆盖：

```java
public static void main(String[] args) {
    System.out.println("Hello World!".length());   //结果为0
}
```

为了避免这种情况，我们要么不要去编写这种会覆盖原方法的方法，要么就需要在接口上添加`suppressBaseMethods`参数来手动阻止覆盖行为：

```java
@ExtensionMethod(value = { ExtensionObject.class }, suppressBaseMethods = false)
```

利用这种特性，实际上JDK中提供的很多工具类都可以作为扩展方法进行使用，比如Arrays就提供了大量工具类，我们完全可以利用它来实现各种高级操作：

```java
@ExtensionMethod({ Arrays.class })
public class Main {
    public static void main(String[] args) {
        int[] arr = { 1, 3, 5, 6, 7 };
        System.out.println(arr.toString());   //正常情况下数组是没有重写toString的，打印出来就是依托答辩，但是现在我们使用扩展方法将Arrays的全部引入，就不一样了
    }
}
```

注意，如果我们编写的扩展方法的参数列表超过1个了，那么后续的参数会作为扩展方法的参数：

```java
public static int length(String text, int x) {  //这里x是多出来的参数
    return 0 + x;
}
```

实际调用就会变成这样：

```java
public static void main(String[] args) {
    System.out.println("Hello World!".length(1));
}
```

官方还有一个非常有趣的例子，结合泛型，我们还可以编写一个更加牛逼的判空操作：

```java
public static <T> T or(T target, T other) {
    return target == null ? other : target;
}
```

下面这种情况用着是真舒服：

```java
//打印传入字符串长度，如果为null也要打印0
public static void test(String str) {
    int length = str.or("").length();
    System.out.println("传入的字符串长度为: " + length);
}
```

### 默认字段修饰

默认字段修饰可以为类中字段快速生成对应的修饰符，避免我们手动进行编写。只需添加`@FieldDefaults`即可：

```java
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account {
    int id;
    String name;
    int age;
}
```

这样生成的代码中所有字段就自动变成private了：

```java
public class Account {
    private int id;
    private String name;
    private int age;
  	...
}
```

这个注解比较简单，无需多说。

### 委托属性

**注意：** 本节课需要掌握Java设计模式课程中的**代理模式**才能继续学习。

代理类需要保证客户端使用的透明性，也就是说操作起来需要与原本的真实对象相同，比如我们访问Github只需要输入网址即可访问，而添加代理之后，也是使用同样的方式去访问Github，所以操作起来是一样的。包括Spring框架其实也是依靠代理模式去实现的AOP记录日志等。

```java
public class UtilImplProxy implements Util {   //代理类
    Util util = new UtilImpl();

    @Override
    public int test(String text) {
        return util.test(text);
    }
}

public class UtilImpl implements Util {   //真正的实现类
    @Override
    public int test(String text) {
        return text.length();
    }
}

public interface Util {   //接口定义
    int test(String text);
}
```

可以看到，无论是真正实现的类还是代理类，都实现了`Util`接口，但是代理类完全没有靠自己去实现，而是内部维护了一个真正的实现类，依靠这个实现类去完成接口定义的方法，就像是替身攻击一样。

Lombok为我们提供了一个`@Delegate`注解，可以自动为代理类生成委托相关的代码：

```java
public class UtilImplProxy implements Util {
    @Delegate   //添加此注解后，会自动根据接口或类定义生成对应的方法，完成委托
    Util util = new UtilImpl();
}

public class UtilImpl implements Util {
    @Override
    public int test(String text) {
        return text.length();
    }
}

public interface Util {
    int test(String text);
}
```

当然，如果我们直接在某个类中使用`@Delegate`它也会直接生成指定类型所有方法到类中：

```java
public class Account {
    @Delegate  //此时会为当前类生成所有ArrayList的List接口实现方法委托
    List<String> list = new ArrayList<>();
}
```

所以我们的类甚至可以直接作为接口的实现，变成彻底的代理类：

```java
public class Account implements List<String> {
    @Delegate
    List<String> list = new ArrayList<>();
}
```

我们接着来看这个注解的参数，我们在生成委托方法时可能并不需要将目标所有的方法全部都实现，这时我们可以手动指定哪些方法需要实现：

```java
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface Delegate {
    //只有给定类中的方法会被委托
    Class<?>[] types() default {};
    
    //给定类中的方法都不会被委托
    Class<?>[] excludes() default {};
}
```

比如：

```java
public class DelegationExample {
  private interface SimpleCollection {   //此时接口只定义了add和remove
    boolean add(String item);
    boolean remove(Object item);
  }
  
  @Delegate(types=SimpleCollection.class)  //即使委托一个ArrayList也只会实现指定接口定义的两个方法
  private final Collection<String> collection = new ArrayList<String>();
}
```

### 工具类

Lombok为我们提供了一个注解用于快速构建工具类。有些时候我们编写工具类要写一大堆static方法，那有没有更方便的形式呢？

我们只需要在类上添加`@UtilityClass`，然后Lombok将自动生成一个私有构造函数来禁止构造，并且所有方法都会自动变成静态方法：

```java
@UtilityClass
public class UtilityClassExample {
  private final int CONSTANT = 5;

  public int addSomething(int in) {
    return in + CONSTANT;
  }
}
```

生成的结果为：

```java
public final class UtilityClassExample {
  private static final int CONSTANT = 5;

  private UtilityClassExample() {
    throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static int addSomething(int in) {
    return in + CONSTANT;
  }
}
```

### 标准异常

我们常常需要在自己的项目中创建各种各样的自定义类型，便于我们快速定位问题原因。

只不过编写一个异常实际上很多情况下都是固定形式，比如：

```java
public class CustomException extends RuntimeException {
    public CustomException(String message) {   //往往都要实现父类的一些构造方法
        super(message);
    }
}
```

Lombok为了简化这些繁杂的过程，为我们提供了一个`@StandardException`注解，这个注解会在编译时自动为我们生成对应的构造方法实现：

```java
@StandardException
public class CustomException extends Exception {
}
```

生成的结果为：

```java
public class CustomException extends Exception {
    public CustomException() {
        this((String)null, (Throwable)null);
    }

    public CustomException(String message) {
        this(message, (Throwable)null);
    }

    public CustomException(Throwable cause) {
        this(cause != null ? cause.getMessage() : null, cause);
    }

    public CustomException(String message, Throwable cause) {
        super(message);
        if (cause != null) {
            super.initCause(cause);
        }

    }
}
```

————————————————
版权声明：本文为柏码知识库版权所有，禁止一切未经授权的转载、发布、出售等行为，违者将被追究法律责任。
原文链接：https://www.itbaima.cn/zh-CN/document/iqbc2haub31bwqtz?segment=2#%E5%BB%BA%E9%80%A0%E8%80%85%E6%A8%A1%E5%BC%8F