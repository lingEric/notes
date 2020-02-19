# mybatis传参方式和动态拼接SQL集锦











```xml
pay_channel_code in
<foreach item="channel_code" collection="payChannelCode.split(',')" open="(" separator="," close=")">
    #{channel_code}
</foreach>
```

