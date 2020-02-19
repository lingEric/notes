# PasswordEncoder
首先要理解DelegatingPasswordEncoder的作用和存在意义,明白官方为什么要使用它来取代原先的NoOpPasswordEncoder

DelegatingPasswordEncoder和NoOpPasswordEncoder都是PasswordEncoder接口的实现类,根据官方的定义,Spring Security的PasswordEncoder接口用于执行密码的单向转换，以便安全地存储密码。

关于密码存储的演变历史这里我不多做介绍,简单来说就是现在数据库存储的密码基本都是经过编码的,而决定如何编码以及判断未编码的字符序列和编码后的字符串是否匹配就是PassswordEncoder的责任.

这里我们可以看一下PasswordEncoder接口的源码

```java
public interface PasswordEncoder {

    /**
     * Encode the raw password. Generally, a good encoding algorithm applies a SHA-1 or
     * greater hash combined with an 8-byte or greater randomly generated salt.
     */
    String encode(CharSequence rawPassword);

    /**
     * Verify the encoded password obtained from storage matches the submitted raw
     * password after it too is encoded. Returns true if the passwords match, false if
     * they do not. The stored password itself is never decoded.
     *
     * @param rawPassword the raw password to encode and match
     * @param encodedPassword the encoded password from storage to compare with
     * @return true if the raw password, after encoding, matches the encoded password from
     * storage
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);

}
```



理解了PasswordEncoder的作用后我们来Spring Security 5.0之前默认的PasswordEncoder实现类NoOpPasswordEncoder,这个类已经被标记为过时了,因为不安全,下面就让我们来看看它是如何地不安全的

## NoOpPasswordEncoder
事实上,NoOpPasswordEncoder就是没有编码的编码器,源码如下:

```java
@Deprecated
public final class NoOpPasswordEncoder implements PasswordEncoder {

    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }
    
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword);
    }
    
    /**
     * Get the singleton {@link NoOpPasswordEncoder}.
     */
    public static PasswordEncoder getInstance() {
        return INSTANCE;
    }
    
    private static final PasswordEncoder INSTANCE = new NoOpPasswordEncoder();
    
    private NoOpPasswordEncoder() {
    }

}
```
## DelegatingPasswordEncoder
DelegatingPasswordEncoder并不是传统意义上的编码器,它并不使用某一特定算法进行编码,顾名思义,它是一个委派密码编码器,它将具体编码的实现根据要求委派给不同的算法,以此来实现不同编码算法之间的兼容和变化协调。

### 构造方法
下面我们来看看DelegatingPasswordEncoder的构造方法

```java
public DelegatingPasswordEncoder(String idForEncode,
    Map<String, PasswordEncoder> idToPasswordEncoder) {
    if(idForEncode == null) {
        throw new IllegalArgumentException("idForEncode cannot be null");
    }
    if(!idToPasswordEncoder.containsKey(idForEncode)) {
        throw new IllegalArgumentException("idForEncode " + idForEncode + "is not found in idToPasswordEncoder " + idToPasswordEncoder);
    }
    for(String id : idToPasswordEncoder.keySet()) {
        if(id == null) {
            continue;
        }
        if(id.contains(PREFIX)) {
            throw new IllegalArgumentException("id " + id + " cannot contain " + PREFIX);
        }
        if(id.contains(SUFFIX)) {
            throw new IllegalArgumentException("id " + id + " cannot contain " + SUFFIX);
        }
    }
    this.idForEncode = idForEncode;
    this.passwordEncoderForEncode = idToPasswordEncoder.get(idForEncode);
    this.idToPasswordEncoder = new HashMap<>(idToPasswordEncoder);
}
```
* idForEncode决定密码编码器的类型

* idToPasswordEncoder决定判断匹配时兼容的类型 
  而且idToPasswordEncoder必须包含idForEncode

围绕这个构造方法通常有两种创建思路,如下:

### 工厂构造【推荐】

```java
PasswordEncoder passwordEncoder =
    PasswordEncoderFactories.createDelegatingPasswordEncoder();
```

其具体实现如下:

```java
public static PasswordEncoder createDelegatingPasswordEncoder() {
    String encodingId = "bcrypt";
    Map<String, PasswordEncoder> encoders = new HashMap<>();
    encoders.put(encodingId, new BCryptPasswordEncoder());
    encoders.put("ldap", new LdapShaPasswordEncoder());
    encoders.put("MD4", new Md4PasswordEncoder());
    encoders.put("MD5", new MessageDigestPasswordEncoder("MD5"));
    encoders.put("noop", NoOpPasswordEncoder.getInstance());
    encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
    encoders.put("scrypt", new SCryptPasswordEncoder());
    encoders.put("SHA-1", new MessageDigestPasswordEncoder("SHA-1"));
    encoders.put("SHA-256", new MessageDigestPasswordEncoder("SHA-256"));
    encoders.put("sha256", new StandardPasswordEncoder());

    return new DelegatingPasswordEncoder(encodingId, encoders);
}
```

这个可以简单地理解为,遇到新密码,DelegatingPasswordEncoder会委托给BCryptPasswordEncoder()进行加密。

同时,对历史上使用ldap,MD4,MD5等等加密算法的密码认证保持兼容(如果数据库里的密码使用的是MD5算法,那使用matches方法认证仍可以通过,但新密码会使用bcrypt进行储存)

### 定制构造
接下来是定制构造,其实和工厂方法是一样的,一般情况下推荐直接使用工厂方法,这里给一个小例子

```java
String idForEncode = "bcrypt";
Map encoders = new HashMap<>();
encoders.put(idForEncode, new BCryptPasswordEncoder());
encoders.put("noop", NoOpPasswordEncoder.getInstance());
encoders.put("pbkdf2", new Pbkdf2PasswordEncoder());
encoders.put("scrypt", new SCryptPasswordEncoder());
encoders.put("sha256", new StandardPasswordEncoder());

PasswordEncoder passwordEncoder =
    new DelegatingPasswordEncoder(idForEncode, encoders);
```



## 密码存储格式
密码的标准存储格式是:

`{id}encodedPassword`

其中,id标识使用PaswordEncoder的种类 
encodedPassword是原密码被编码后的密码

例如**rawPassword**为**password**在使用不同编码算法的情况下在数据库的存储如下:

```
{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG 
{noop}password 
{pbkdf2}5d923b44a6d129f3ddf3e3c8d29412723dcbde72445e8ef6bf3b508fbf17fa4ed4d6b99ca763d8dc 
{scrypt}$e0801$8bWJaSu2IKSn9Z9kM+TPXfOc/9bdYSrN1oD9qfVThWEwdRTnO7re7Ei+fUZRJ68k9lTyuTeUp4of4g24hHnazw==$OAOec05+bXxvuu/1qZ6NUR+xQYvYv7BeL1QxwRpY5Pc=  
{sha256}97cde38028ad898ebc02e690819fa220e88c62e0699403e94fff291cfffaf8410849f27605abcbc0 
```

注意：

>  密码的可靠性并不依赖于加密算法的保密,即密码的可靠在于就算你知道我使用的是什么算法你也无法还原出原密码(当然,对于本身就可逆的编码算法来说就不是这样了,但这样的算法我们通常不会认为是可靠的),而且,即使没有标明使用的是什么算法,攻击者也很容易根据一些规律从编码后的密码字符串中推测出编码算法,如bcrypt算法通常是以$2a$开头的

## 密码编码与匹配
从上文可知,idForEncode这个构造参数决定使用哪个PasswordEncoder进行密码的编码,编码的方法如下:

```java
private static final String PREFIX = "{";
private static final String SUFFIX = "}";

@Override
public String encode(CharSequence rawPassword) {
    return PREFIX + this.idForEncode + SUFFIX + this.passwordEncoderForEncode.encode(rawPassword);
}
```

所以用上文构造的DelegatingPasswordEncoder默认使用BCryptPasswordEncoder,结果格式如

`{bcrypt}2a2a10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG`

密码编码方法比较简单,重点在于匹配.匹配方法源码如下:

```java
@Override
public boolean matches(CharSequence rawPassword, String prefixEncodedPassword) {
    if(rawPassword == null && prefixEncodedPassword == null) {
        return true;
    }
    //取出编码算法的id
    String id = extractId(prefixEncodedPassword);
    //根据编码算法的id从支持的密码编码器Map(构造时传入)中取出对应编码器
    PasswordEncoder delegate = this.idToPasswordEncoder.get(id);
    if(delegate == null) {
    //如果找不到对应的密码编码器则使用默认密码编码器进行匹配判断,此时比较的密码字符串是 prefixEncodedPassword
        return this.defaultPasswordEncoderForMatches
            .matches(rawPassword, prefixEncodedPassword);
    }
    //从 prefixEncodedPassword 中提取获得 encodedPassword 
    String encodedPassword = extractEncodedPassword(prefixEncodedPassword);
    //使用对应编码器进行匹配判断,此时比较的密码字符串是 encodedPassword ,不携带编码算法id头
    return delegate.matches(rawPassword, encodedPassword);
}
```

这个匹配方法其实也挺好理解的,唯一需要特别注意的就是找不到对应密码编码器时使用的默认密码编码器,

我们来看看defaultPasswordEncoderForMatches是一个什么东西

### defaultPasswordEncoderForMatches

defaultPasswordEncoderForMatches在DelegatingPasswordEncoder的源码里对应内容如下

```java
private static final String PREFIX = "{";
private static final String SUFFIX = "}";
private final String idForEncode;
private final PasswordEncoder passwordEncoderForEncode;
private final Map<String, PasswordEncoder> idToPasswordEncoder;

private PasswordEncoder defaultPasswordEncoderForMatches = new UnmappedIdPasswordEncoder();

public void setDefaultPasswordEncoderForMatches(
    PasswordEncoder defaultPasswordEncoderForMatches) {
    if(defaultPasswordEncoderForMatches == null) {
        throw new IllegalArgumentException("defaultPasswordEncoderForMatches cannot be null");
    }
    this.defaultPasswordEncoderForMatches = defaultPasswordEncoderForMatches;
}

private class UnmappedIdPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        throw new UnsupportedOperationException("encode is not supported");
    }

    @Override
    public boolean matches(CharSequence rawPassword,
        String prefixEncodedPassword) {
        String id = extractId(prefixEncodedPassword);
        throw new IllegalArgumentException("There is no PasswordEncoder mapped for the id \"" + id + "\"");
    }
}
```
如果没有设置默认的编码器，它就会调用UnmappedIdPasswordEncoder，而`UnmappedIdPasswordEncoder`其实本质不是编码器，只是在这里抛出异常

遇到这个异常,最简单的做法就是明确提供一个PasswordEncoder对密码进行编码,如果是从Spring Security 5.0 之前迁移而来的,由于之前默认使用的是NoOpPasswordEncoder并且数据库的密码保存格式不带有加密算法id头,会报id为null异常,所以应该明确提供一个NoOpPasswordEncoder密码编码器.

这里有两种思路,其一就是使用NoOpPasswordEncoder取代DelegatingPasswordEncoder,以恢复到之前版本的状态,这也是笔者在其他博客上看得比较多的一种解决方法.另外就是使用DelegatingPasswordEncoder的setDefaultPasswordEncoderForMatches方法指定默认的密码编码器为NoOpPasswordEncoder,这两种方法孰优孰劣自然不言而喻,官方文档是这么说的

Reverting to NoOpPasswordEncoder is not considered to be secure. You should instead migrate to using DelegatingPasswordEncoder to support secure password encoding. 
恢复到NoOpPasswordEncoder不被认为是安全的。您应该转而使用DelegatingPasswordEncoder支持安全密码编码

当然,你也可以将数据库保存的密码都加上一个{noop}前缀,这样DelegatingPasswordEncoder就知道要使用NoOpPasswordEncoder了,这确实是一种方法,但没必要,这里我们来看一下前面的两种解决方法的实现

1. 使用NoOpPasswordEncoder取代DelegatingPasswordEncoder

```java
@Bean
 public PasswordEncoder passwordEncoder（）{
     return NoOpPasswordEncoder.getInstance（）;
}
```



2. 使用DelegatingPasswordEncoder指定defaultPasswordEncoderForMatches

```java
    @Bean
    public  static PasswordEncoder passwordEncoder( ){
        DelegatingPasswordEncoder delegatingPasswordEncoder =
                (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
//设置defaultPasswordEncoderForMatches为NoOpPasswordEncoder
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
        return  delegatingPasswordEncoder;
    }
```

