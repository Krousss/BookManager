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

import com.alibaba.fastjson.JSONObject;

import dao.UserDao;
import page.Operator;
import page.Page;
import page.SearchProperty;
import util.StringUtil;
import entity.User;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserServlet() {
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
		String method=request.getParameter("method");
		if("toMyUserListView".equals(method)){
			toMyUserListView(request,response);
			return;
		}else if("UserListView".equals(method)){
			UserListView(request,response);
			return;
		}else if("getUserList".equals(method)){
			getUserList(request,response);
			return;
		}else if("toAllUserListView".equals(method)){
			toAllUserListView(request,response);
			return;
		}else if("addUser".equals(method)){
			addUser(request,response);
			return;
		}else if("editUserByself".equals(method)){
			editUserByself(request,response);
			return;
		}else if("editUserBymanager".equals(method)){
			editUserBymanager(request,response);
		}else if("deleteUser".equals(method)){
			deleteUser(request,response);
		}else if("toBookCategoryListView".equals(method)){
			toBookCategoryListView(request,response);
			return;
		}else if("toBookListView".equals(method)){
			toBookListView(request,response);
			return;
		}else if("toBorrowListView".equals(method)){
			toBorrowListView(request,response);
			return;
		}else if("toCountListView".equals(method)){
			toCountListView(request,response);
			return;
		}
	}
	//跳转到统计页面
	private void toCountListView(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("view/borrow_stats.jsp").forward(request, response);
	}

	private void toBorrowListView(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("view/borrow.jsp").forward(request, response);
	}

	//跳转到图书列表
	private void toBookListView(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("view/bookList.jsp").forward(request, response);
	}

	//跳转到图书分类列表
	private void toBookCategoryListView(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("view/bookCategoryList.jsp").forward(request, response);
	}

	//删除用户
	private void deleteUser(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		Map<String, String> ret = new HashMap<String, String>();
		UserDao userDao=new UserDao();
		String[] ids=request.getParameterValues("ids[]");
		if(ids==null||ids.length==0){
			ret.put("type", "error");
			ret.put("msg", "请选择要删除的用户");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		int[] idArr=new int[ids.length];
		for(int i=0;i<ids.length;i++){
			idArr[i]=Integer.parseInt(ids[i]);
		}
		if(!userDao.delete(idArr)){
			ret.put("type", "error");
			ret.put("msg", "删除失败");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		ret.put("type", "success");
		ret.put("msg", "删除成功");
		writrToPage(response, JSONObject.toJSONString(ret));
		return;
	}

	//管理员与超管修改用户
	private void editUserBymanager(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		Map<String, String> ret = new HashMap<String, String>();
		int id = Integer.parseInt(request.getParameter("id"));
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		int type = Integer.parseInt(request.getParameter("type"));
		int status = Integer.parseInt(request.getParameter("status"));
		UserDao userDao=new UserDao();
		
		if(StringUtil.isEmpty(username)){
			ret.put("type", "error");
			ret.put("msg", "用户名不能为空!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(StringUtil.isEmpty(password)){
			ret.put("type", "error");
			ret.put("msg", "密码不能为空!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(isExitUser(username,id)){
			ret.put("type", "error");
			ret.put("msg", "该用户名已经存在!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		
		User user = new User();
		user.setId(id);
		user.setUsername(username);
		user.setPassword(password);
		user.setType(type);
		user.setStatus(status);
		
		if(!userDao.update(user)){
			ret.put("type", "error");
			ret.put("msg", "更新失败，请联系管理员!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		
		ret.put("type", "success");
		ret.put("msg", "更新成功!");
		writrToPage(response, JSONObject.toJSONString(ret));
		return;
	}

	//修改用户
	private void editUserByself(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		Map<String, String> ret = new HashMap<String, String>();
		int id = Integer.parseInt(request.getParameter("id"));
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		int type = Integer.parseInt(request.getParameter("type"));
		UserDao userDao=new UserDao();
		if(StringUtil.isEmpty(username)){
			ret.put("type", "error");
			ret.put("msg", "用户名不能为空!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(StringUtil.isEmpty(password)){
			ret.put("type", "error");
			ret.put("msg", "密码不能为空!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(isExitUser(username,id)){
			ret.put("type", "error");
			ret.put("msg", "该用户名已经存在!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		User user = new User();
		user.setId(id);
		user.setUsername(username);
		user.setPassword(password);
		user.setType(type);
		if(!userDao.update(user)){
			ret.put("type", "error");
			ret.put("msg", "更新失败，请联系管理员!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		ret.put("type", "success");
		ret.put("msg", "更新成功!");
		writrToPage(response, JSONObject.toJSONString(ret));
		return;
	}
	//判断修改后用户名是否重复
	private boolean isExitUser(String username, int id) {
		// TODO Auto-generated method stub
		Page<User> page = new Page<User>(1, 10);
		UserDao userDao=new UserDao();
		page.getSearchProporties().add(new SearchProperty("username", username, Operator.EQ));
		page = userDao.findList(page);
		if(page.getContent().size() > 0){
			User user = page.getContent().get(0);
			if(user.getId() != id)return true;
		}
		return false;
	}

	//添加用户
	private void addUser(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		Map<String, String> ret = new HashMap<String, String>();
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		int type = Integer.parseInt(request.getParameter("type"));
		int status = Integer.parseInt(request.getParameter("status"));
		UserDao userDao=new UserDao();
		if(StringUtil.isEmpty(username)){
			ret.put("type", "error");
			ret.put("msg", "用户名不能为空!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(StringUtil.isEmpty(password)){
			ret.put("type", "error");
			ret.put("msg", "密码不能为空!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(isExitUser(username)){
			ret.put("type", "error");
			ret.put("msg", "该用户名已经存在!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setStatus(status);
		user.setType(type);
		if(!userDao.add(user)){
			ret.put("type", "error");
			ret.put("msg", "添加失败，请联系管理员!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		ret.put("type", "success");
		ret.put("msg", "添加成功!");
		writrToPage(response, JSONObject.toJSONString(ret));
		return;
	}
	//判断是否存在用户名相同用户
	private boolean isExitUser(String username) {
		// TODO Auto-generated method stub
		UserDao userDao=new UserDao();
		Page<User> page = new Page<User>(1, 10);
		page.getSearchProporties().add(new SearchProperty("username", username, Operator.EQ));
		page = userDao.findList(page);
		if(page.getContent().size() > 0){
			return true;
		}
		return false;
	}

	private void writrToPage(HttpServletResponse response, String jsonString) {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(jsonString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//超管获取所有用户列表
	private void toAllUserListView(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("view/admin/allUserList.jsp").forward(request, response);
	}

	//获取用户列表
	private void getUserList(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("UTF-8");
		UserDao userDao=new UserDao();
		String username = request.getParameter("username");
		if(username == null){
			username = "";
		}
		int pageNumber = Integer.parseInt(request.getParameter("page"));
		int pageSize = Integer.parseInt(request.getParameter("rows"));
		Page<User> page = new Page<User>(pageNumber, pageSize);
		page.getSearchProporties().add(new SearchProperty("username", "%"+username+"%", Operator.LIKE));
		HttpSession session = request.getSession();
		User user =(User) session.getAttribute("user");
		//用户查询自身
		if(user.getType()==2){
			page.getSearchProporties().add(new SearchProperty("id", user.getId(), Operator.EQ));
		}
		//图书管理员查询用户
		if(user.getType()==3){
			page.getSearchProporties().add(new SearchProperty("type", 2, Operator.EQ));
		}
		page = userDao.findList(page);
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("total", page.getTotal());
		ret.put("rows", page.getContent());
		response.getWriter().write(JSONObject.toJSONString(ret));
	}

	//图书管理员查看用户信息
	private void UserListView(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("view/manager/userList.jsp").forward(request, response);
	}

	//用户查看个人信息
	private void toMyUserListView(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("view/user/myUserList.jsp").forward(request, response);
	}

}
