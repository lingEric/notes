1.导入相应jar包
<!--文件上传-->
<!-- https://mvnrepository.com/artifact/commons-fileupload/commons-fileupload -->
<dependency>
	<groupId>commons-fileupload</groupId>
	<artifactId>commons-fileupload</artifactId>
	<version>1.3.2</version>
</dependency>

<dependency>
	<groupId>commons-io</groupId>
	<artifactId>commons-io</artifactId>
	<version>2.4</version>
</dependency>

2.springMvc配置文件中配置multipartResolver
<!--配置图片上传-->
<bean id="multipartResolver"
	  class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
	<!-- set the max upload size10MB -->
	<property name="maxUploadSize">
		<value>10485760</value>
	</property>
	<property name="maxInMemorySize">
		<value>4096</value>
	</property>
	<property name="defaultEncoding">
		<value>utf-8</value>
	</property>
</bean>

3.页面表单
<form action="${APP_PATH}/filesUpload" method="post" enctype="multipart/form-data">
	<input type="file" name="files" accept="image/*">
	<button type="submit">上传</button>
</form>

4.controller

package com.ling.controller;

import com.ling.bean.Msg;
import com.ling.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * MultipartFile类常用的一些方法：
 * 1.String getContentType()//获取文件MIME类型
 * 2.InputStream getInputStream()//获取文件流
 * 3.String getName() //获取表单中文件组件的名字
 * 4.String getOriginalFilename() //获取上传文件的原名
 * 5.long getSize()  //获取文件的字节大小，单位byte
 * 6.boolean isEmpty() //是否为空
 * 7.void transferTo(File dest)//保存到一个目标文件中
 */
@Controller
public class ImageController {

    private final HttpServletRequest request;

    @Autowired
    public ImageController(HttpServletRequest request) {
        this.request = request;
    }

    @RequestMapping(value = "/filesUpload")
    @ResponseBody
    public Msg filesUpload(@RequestParam("files") MultipartFile[] files) {
        return Msg.success().add("fileList", filesUploadUtil(files));
    }

    /**
     * @return 上传的文件的路径
     */
    private Map<String, String> filesUploadUtil(MultipartFile[] files) {
        //判断file数组不能为空并且长度大于0
        if (files != null && files.length > 0) {
            Map<String, String> map = new HashMap<>();
            //循环获取file数组中得文件
            int i = 0;
            for (MultipartFile file : files) {
                //保存文件
                map.put("index" + i, saveFile(file));
                ++i;
            }
            return map;
        }
        // 重定向
        return null;
    }


    /***
     * 抽取的保存文件方法
     */
    private String saveFile(MultipartFile file) {
        String uuid = UUID.randomUUID().toString();
        String upDir = DateUtil.getYearMonthString();
        // 判断文件是否为空
        if (!file.isEmpty()) {
            try {
                // 文件保存路径
                String basePath = request.getServletContext().getRealPath("/images") + "/upload/" + upDir;
                File file_temp = new File(basePath);
                if (!file_temp.exists()) {
                    //创建目录
                    file_temp.mkdir();
                }
                //获取文件的初始名称，如：aa.txt
                String originalName = file.getOriginalFilename();
                //获取文件的扩展名，如.txt
                String fileSuffix = originalName.substring(originalName.lastIndexOf("."));
                //获取文件的路径，uuid确保该文件不会重名
                String filePath = basePath + "/" + uuid + fileSuffix;
                // 转存文件
                file.transferTo(new File(filePath));
                return request.getContextPath() + "/images/upload/" + upDir + "/" + uuid + fileSuffix;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}
