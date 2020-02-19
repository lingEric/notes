package util;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class ImageUploadUtil {
	/**
	 * 单张图片上传默认方法，返回图片在服务器中保存的路径
	 * 
	 */
	public static String imgUpload(HttpServletRequest request, String path) throws Exception {
		// 图片保存路径
		String imgPath = "";
		// 1. 创建工厂对象
		FileItemFactory factory = new DiskFileItemFactory();
		// 2. 文件上传核心工具类
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 设置大小限制参数
		upload.setFileSizeMax(20 * 1024 * 1024); // 单个文件大小限制
		upload.setSizeMax(50 * 1024 * 1024); // 总文件大小限制
		upload.setHeaderEncoding("UTF-8"); // 对中文文件编码处理

		// 判断
		if (ServletFileUpload.isMultipartContent(request)) {
			// 3. 把请求数据转换为list集合
			List<FileItem> list = upload.parseRequest(request);
			// 遍历
			for (FileItem item : list) {
				// 判断：是否为文件表单项
				if (!item.isFormField()) {
					/******** 文件上传 ***********/
					// a. 获取文件名称
					String name = item.getName();// E:其他文件\私人\头像.png
					name = name.substring(name.lastIndexOf("."));// 在windows操作系统中，文件名不能包括':'字符，同时必须去掉\符号
					// ----处理上传文件名重名问题----
					// a1. 先得到唯一标记
					String id = StringUtil.getRandomString(8);
					// a2. 拼接文件名
					name = id + name;

					// 图片URL
					imgPath = request.getContextPath() + "/../appData/ive/" + name;

					// b. 得到上传目录
					String basePath = path;
					// c. 创建要上传的文件对象
					File file = new File(basePath, name);
					// d. 上传
					item.write(file);
					item.delete(); // 删除组件运行时产生的临时文件
				}
			}
		}

		return imgPath;
	}

	/**
	 * ckeditor单张图片上传方法，写回图片的URL
	 * 
	 */
	public static void imgUpload2(HttpServletRequest request, String path, HttpServletResponse response)
			throws Exception {
		String imageContextPath = imgUpload(request, path);
		response.setContentType("text/html;charset=UTF-8");
		String callback = request.getParameter("CKEditorFuncNum");
		PrintWriter out;
		out = response.getWriter();
		out.println("<script type=\"text/javascript\">");
		out.println("window.parent.CKEDITOR.tools.callFunction(" + callback + ",'" + imageContextPath + "',''" + ")");
		out.println("</script>");
		out.flush();
		out.close();
	}
}
