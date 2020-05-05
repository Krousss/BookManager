package servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import page.Operator;
import page.Page;
import page.SearchProperty;

import com.alibaba.fastjson.JSONObject;

import dao.UserDao;
import entity.User;

/**
 * Servlet implementation class LoginServlet
 * 登录注册servlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private UserDao userDao=new UserDao();  
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
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
		if("toRegister".equals(method)){
			toRegister(request,response);
			return;
		}else if("toLogin".equals(method)){
			toLogin(request,response);
			return;
		}else if("registerAction".equals(method)){
			registerAction(request,response);
			return;
		}else if("LoginAction".equals(method)){
			loginAction(request,response);
			return;
		}else if("LoginOut".equals(method)){
			LoginOut(request,response);
			return;
		}
	}
	private void LoginOut(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		session.setAttribute("user", null);
		request.getRequestDispatcher("view/login.jsp").forward(request, response);
	}

	private void loginAction(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		Map<String, String> ret = new HashMap<String, String>();
		//设置字符编码
		response.setCharacterEncoding("UTF-8");
		//获取前端数据
		String username = request.getParameter("username");
		String password=request.getParameter("password");
		String vcode=request.getParameter("vcode");
		String type = request.getParameter("type");
		HttpSession session = request.getSession();
		Object loginCode = session.getAttribute("loginCode");
		//二次判断数据是否为空
		if(util.StringUtil.isEmpty(username)){
			ret.put("type", "error");
			ret.put("msg", "用户名不能为空！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(util.StringUtil.isEmpty(password)){
			ret.put("type", "error");
			ret.put("msg", "密码不能为空！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(util.StringUtil.isEmpty(vcode)){
			ret.put("type", "error");
			ret.put("msg", "验证码不能为空！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(loginCode==null){
			ret.put("type", "error");
			ret.put("msg", "长时间未操作验证码已失效");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(!vcode.toUpperCase().equals(loginCode.toString())){
			ret.put("type", "error");
			ret.put("msg", "验证码错误");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		Page<User> page = new Page<User>(1, 10);
		page.getSearchProporties().add(new SearchProperty("username", username, Operator.EQ));
		page.getSearchProporties().add(new SearchProperty("type", type, Operator.EQ));
		page = userDao.findList(page);
		if(page.getContent().size() == 0){
			ret.put("type", "error");
			ret.put("msg", "该类型的用户名不存在！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		User user = page.getContent().get(0);
		if(!password.equals(user.getPassword())){
			ret.put("type", "error");
			ret.put("msg", "密码错误！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(user.getStatus() == 0){
			ret.put("type", "error");
			ret.put("msg", "用户账号已因逾期行为被冻结，请联系管理员！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		session.setAttribute("loginCode",null);
		session.setAttribute("user", user);
		ret.put("type", "success");
		ret.put("msg", "登录成功！");
		response.getWriter().write(JSONObject.toJSONString(ret));
	}

	//注册，只有普通用户能进行注册，图书管理员由超级管理员进行分配
	private void registerAction(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		//返回消息容器
		Map<String, String> ret = new HashMap<String, String>();
		//设置字符编码
		response.setCharacterEncoding("UTF-8");
		//获取前端数据
		String username = request.getParameter("username");
		String password=request.getParameter("password");
		String repassword = request.getParameter("repassword");
		String vcode=request.getParameter("vcode");
		HttpSession session = request.getSession();
		Object registerCode = session.getAttribute("registerCode");
		//二次判断数据是否为空
		if(util.StringUtil.isEmpty(username)){
			ret.put("type", "error");
			ret.put("msg", "用户名不能为空！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(util.StringUtil.isEmpty(password)){
			ret.put("type", "error");
			ret.put("msg", "密码不能为空！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(util.StringUtil.isEmpty(repassword)){
			ret.put("type", "error");
			ret.put("msg", "确认密码不能为空！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(util.StringUtil.isEmpty(vcode)){
			ret.put("type", "error");
			ret.put("msg", "验证码不能为空！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(registerCode==null){
			ret.put("type", "error");
			ret.put("msg", "长时间未操作验证码已失效");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		if(!vcode.toUpperCase().equals(registerCode.toString())){
			ret.put("type", "error");
			ret.put("msg", "验证码错误");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		//添加yonghu
		if(isExitUsername(username)){
			ret.put("type", "error");
			ret.put("msg", "该用户名已存在");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		User user=new User();
		user.setUsername(username);
		user.setPassword(password);
		if(!userDao.add(user)){
			ret.put("type", "error");
			ret.put("msg", "注册失败，请联系管理员！");
			response.getWriter().write(JSONObject.toJSONString(ret));
			return;
		}
		request.getSession().setAttribute("registerCode",null);
		ret.put("type", "success");
		ret.put("msg", "注册成功！");
		response.getWriter().write(JSONObject.toJSONString(ret));
	}
	//判断是否存在用户
	private boolean isExitUsername(String username) {
		// TODO Auto-generated method stub
		Page<User> page = new Page<User>(1, 10);
		page.getSearchProporties().add(new SearchProperty("username", username, Operator.EQ));
		page = userDao.findList(page);
		if(page.getContent().size() > 0){
			return true;
		}
		return false;
	}

	private void toLogin(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("view/login.jsp").forward(request, response);
	}

	//跳转到注册界面
	private void toRegister(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("view/register.jsp").forward(request, response);
	}

}
