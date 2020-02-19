# mybatis 动态拼接sql

1. 模糊查询使用bind标签

   ```xml
   <!-- bind:可以将OGNL表达式的值绑定到一个变量中，方便后来引用这个变量的值 -->
   <bind name="bindeName" value="'%'+eName+'%'"/>
   
   <!-- 示例 -->
   <if test="serviceCode != null and serviceCode != ''">
       <bind name="serviceCodeLike" value="'%'+serviceCode+'%'"/>
       and aas.service_code like #{serviceCodeLike}
   </if>
   ```

2. 多个where条件对and进行处理

   ```xml
   <!-- 方案1：使用1=1 -->
   select
   xxx
   from xxx
   where 1=1
   <if test="clientCode !=null and clientCode !=''">
       and c.client_code=#{clientCode}
   </if>
   <if test="clientName !=null and clientName !=''">
       and c.client_name=#{clientName}
   </if>
   <if test="enableFlag !=null and enableFlag !=''">
       and c.enable_flag=#{enableFlag}
   </if>
   ...
   
   <!-- 方案2：使用where标签，每个条件都需要加上and，where标签自动去掉第一个条件前面的and -->
   <where>
       <if test="productLineCode != null and productLineCode != ''">
           <bind name="productLineCodeLike" value="'%'+productLineCode+'%'"/>
           and apl.product_line_code like #{productLineCodeLike}
       </if>
       <if test="productLineName != null and productLineName != ''">
           <bind name="productLineNameLike" value="'%'+productLineName+'%'"/>
           and apl.product_line_name like #{productLineNameLike}
       </if>
       <if test="versionNumber != null and versionNumber != ''">
           and apl.version_number = #{versionNumber}
       </if>
       ...
   </where>
   ```

3. 