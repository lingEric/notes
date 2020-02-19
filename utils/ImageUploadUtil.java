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
	 * ����ͼƬ�ϴ�Ĭ�Ϸ���������ͼƬ�ڷ������б����·��
	 * 
	 */
	public static String imgUpload(HttpServletRequest request, String path) throws Exception {
		// ͼƬ����·��
		String imgPath = "";
		// 1. ������������
		FileItemFactory factory = new DiskFileItemFactory();
		// 2. �ļ��ϴ����Ĺ�����
		ServletFileUpload upload = new ServletFileUpload(factory);
		// ���ô�С���Ʋ���
		upload.setFileSizeMax(20 * 1024 * 1024); // �����ļ���С����
		upload.setSizeMax(50 * 1024 * 1024); // ���ļ���С����
		upload.setHeaderEncoding("UTF-8"); // �������ļ����봦��

		// �ж�
		if (ServletFileUpload.isMultipartContent(request)) {
			// 3. ����������ת��Ϊlist����
			List<FileItem> list = upload.parseRequest(request);
			// ����
			for (FileItem item : list) {
				// �жϣ��Ƿ�Ϊ�ļ�����
				if (!item.isFormField()) {
					/******** �ļ��ϴ� ***********/
					// a. ��ȡ�ļ�����
					String name = item.getName();// E:�����ļ�\˽��\ͷ��.png
					name = name.substring(name.lastIndexOf("."));// ��windows����ϵͳ�У��ļ������ܰ���':'�ַ���ͬʱ����ȥ��\����
					// ----�����ϴ��ļ�����������----
					// a1. �ȵõ�Ψһ���
					String id = StringUtil.getRandomString(8);
					// a2. ƴ���ļ���
					name = id + name;

					// ͼƬURL
					imgPath = request.getContextPath() + "/../appData/ive/" + name;

					// b. �õ��ϴ�Ŀ¼
					String basePath = path;
					// c. ����Ҫ�ϴ����ļ�����
					File file = new File(basePath, name);
					// d. �ϴ�
					item.write(file);
					item.delete(); // ɾ���������ʱ��������ʱ�ļ�
				}
			}
		}

		return imgPath;
	}

	/**
	 * ckeditor����ͼƬ�ϴ�������д��ͼƬ��URL
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
