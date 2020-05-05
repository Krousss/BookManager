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
	//��ת��ͳ��ҳ��
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

	//��ת��ͼ���б�
	private void toBookListView(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("view/bookList.jsp").forward(request, response);
	}

	//��ת��ͼ������б�
	private void toBookCategoryListView(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("view/bookCategoryList.jsp").forward(request, response);
	}

	//ɾ���û�
	private void deleteUser(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		Map<String, String> ret = new HashMap<String, String>();
		UserDao userDao=new UserDao();
		String[] ids=request.getParameterValues("ids[]");
		if(ids==null||ids.length==0){
			ret.put("type", "error");
			ret.put("msg", "��ѡ��Ҫɾ�����û�");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		int[] idArr=new int[ids.length];
		for(int i=0;i<ids.length;i++){
			idArr[i]=Integer.parseInt(ids[i]);
		}
		if(!userDao.delete(idArr)){
			ret.put("type", "error");
			ret.put("msg", "ɾ��ʧ��");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		ret.put("type", "success");
		ret.put("msg", "ɾ���ɹ�");
		writrToPage(response, JSONObject.toJSONString(ret));
		return;
	}

	//����Ա�볬���޸��û�
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
			ret.put("msg", "�û�������Ϊ��!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(StringUtil.isEmpty(password)){
			ret.put("type", "error");
			ret.put("msg", "���벻��Ϊ��!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(isExitUser(username,id)){
			ret.put("type", "error");
			ret.put("msg", "���û����Ѿ�����!");
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
			ret.put("msg", "����ʧ�ܣ�����ϵ����Ա!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		
		ret.put("type", "success");
		ret.put("msg", "���³ɹ�!");
		writrToPage(response, JSONObject.toJSONString(ret));
		return;
	}

	//�޸��û�
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
			ret.put("msg", "�û�������Ϊ��!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(StringUtil.isEmpty(password)){
			ret.put("type", "error");
			ret.put("msg", "���벻��Ϊ��!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(isExitUser(username,id)){
			ret.put("type", "error");
			ret.put("msg", "���û����Ѿ�����!");
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
			ret.put("msg", "����ʧ�ܣ�����ϵ����Ա!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		ret.put("type", "success");
		ret.put("msg", "���³ɹ�!");
		writrToPage(response, JSONObject.toJSONString(ret));
		return;
	}
	//�ж��޸ĺ��û����Ƿ��ظ�
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

	//����û�
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
			ret.put("msg", "�û�������Ϊ��!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(StringUtil.isEmpty(password)){
			ret.put("type", "error");
			ret.put("msg", "���벻��Ϊ��!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(isExitUser(username)){
			ret.put("type", "error");
			ret.put("msg", "���û����Ѿ�����!");
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
			ret.put("msg", "���ʧ�ܣ�����ϵ����Ա!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		ret.put("type", "success");
		ret.put("msg", "��ӳɹ�!");
		writrToPage(response, JSONObject.toJSONString(ret));
		return;
	}
	//�ж��Ƿ�����û�����ͬ�û�
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

	//���ܻ�ȡ�����û��б�
	private void toAllUserListView(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("view/admin/allUserList.jsp").forward(request, response);
	}

	//��ȡ�û��б�
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
		//�û���ѯ����
		if(user.getType()==2){
			page.getSearchProporties().add(new SearchProperty("id", user.getId(), Operator.EQ));
		}
		//ͼ�����Ա��ѯ�û�
		if(user.getType()==3){
			page.getSearchProporties().add(new SearchProperty("type", 2, Operator.EQ));
		}
		page = userDao.findList(page);
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("total", page.getTotal());
		ret.put("rows", page.getContent());
		response.getWriter().write(JSONObject.toJSONString(ret));
	}

	//ͼ�����Ա�鿴�û���Ϣ
	private void UserListView(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("view/manager/userList.jsp").forward(request, response);
	}

	//�û��鿴������Ϣ
	private void toMyUserListView(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		// TODO Auto-generated method stub
		request.getRequestDispatcher("view/user/myUserList.jsp").forward(request, response);
	}

}
