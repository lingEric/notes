package com.ling.servlet;

import com.ling.util.AuthCodeUtil;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ImgServlet",urlPatterns = "/img-code")
public class ImgServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        String authCode = AuthCodeUtil.getAuthCode();
        request.getSession().setAttribute("authcode", authCode.toLowerCase());
        //把图片返回
        ImageIO.write(AuthCodeUtil.getAuthImg(authCode), "JPEG", response.getOutputStream());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
