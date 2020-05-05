package servlet;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import util.CpachaUtil;

/**
 * Servlet implementation class CpachaServlet
 * 验证码servlet
 */
@WebServlet("/CpachaServlet")
public class CpachaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CpachaServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String method = request.getParameter("method");
		if("GetLoginVCode".equals(method)){
			getLoginVCode(request,response);
			return;
		}else if("GetRegisterVCode".equals(method)){
			GetRegisterVCode(request,response);
		}
	}
	//生成注册验证码
	private void GetRegisterVCode(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		CpachaUtil cpachaUtil=new CpachaUtil();
		HttpSession session = request.getSession();
		String generatorVCode = cpachaUtil.generatorVCode();
		session.setAttribute("registerCode", generatorVCode.toUpperCase());
		BufferedImage generatorRotateVCodeImage = cpachaUtil.generatorRotateVCodeImage(generatorVCode, true);
		try {
			ServletOutputStream outputStream = response.getOutputStream();
			ImageIO.write(generatorRotateVCodeImage, "gif", outputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//生成登录验证码
	private void getLoginVCode(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		CpachaUtil cpachaUtil=new CpachaUtil();
		HttpSession session = request.getSession();
		String generatorVCode = cpachaUtil.generatorVCode();
		session.setAttribute("loginCode", generatorVCode.toUpperCase());
		BufferedImage generatorRotateVCodeImage = cpachaUtil.generatorRotateVCodeImage(generatorVCode, true);
		try {
			ServletOutputStream outputStream = response.getOutputStream();
			ImageIO.write(generatorRotateVCodeImage, "gif", outputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
