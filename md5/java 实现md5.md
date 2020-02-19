1. 使用java提供的类库

```java
@Test
public void givenPassword_whenHashing_thenVerifying() 
  throws NoSuchAlgorithmException {
    String hash = "35454B055CC325EA1AF2126E27707052";
    String password = "ILoveJava";
         
    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update(password.getBytes());
    byte[] digest = md.digest();
    String myHash = DatatypeConverter
      .printHexBinary(digest).toUpperCase();
         
    assertThat(myHash.equals(hash)).isTrue();
}
```

我们也可以校验文件

```java
@Test
public void givenFile_generatingChecksum_thenVerifying() 
  throws NoSuchAlgorithmException, IOException {
    String filename = "src/test/resources/test_md5.txt";
    String checksum = "5EB63BBBE01EEED093CB22BB8F5ACDC3";
         
    MessageDigest md = MessageDigest.getInstance("MD5");
    md.update(Files.readAllBytes(Paths.get(filename)));
    byte[] digest = md.digest();
    String myChecksum = DatatypeConverter
      .printHexBinary(digest).toUpperCase();
         
    assertThat(myChecksum.equals(checksum)).isTrue();
}
```

1. 使用Apache commons 的`org.apache.commons.codec.digest.DigestUtils` 

```java
@Test
public void givenPassword_whenHashingUsingCommons_thenVerifying()  {
    String hash = "35454B055CC325EA1AF2126E27707052";
    String password = "ILoveJava";
    String md5Hex = DigestUtils.md5Hex(password).toUpperCase();
    assertThat(md5Hex.equals(hash)).isTrue();
}
```

1. 使用spring提供的和apache commons差不多的`org.springframework.util.DigestUtils` 

```java
DigestUtils.md5DigestAsHex(str.getBytes())
```

1. 使用guava的`com.google.common.io.Files.hash` 

```java
@Test
public void givenFile_whenChecksumUsingGuava_thenVerifying() 
  throws IOException {
    String filename = "src/test/resources/test_md5.txt";
    String checksum = "5EB63BBBE01EEED093CB22BB8F5ACDC3";
    HashCode hash = com.google.common.io.Files.hash(new File(filename), Hashing.md5());
    String myChecksum = hash.toString().toUpperCase();
    assertThat(myChecksum.equals(checksum)).isTrue();
}
```