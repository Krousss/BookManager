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
import util.StringUtil;

import com.alibaba.fastjson.JSONObject;

import dao.BookCategoryDao;
import dao.UserDao;
import entity.BookCategory;
import entity.User;

/**
 * Servlet implementation class BookCategoryServlet
 */
@WebServlet("/BookCategoryServlet")
public class BookCategoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BookCategoryServlet() {
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
		if("addBookCategory".equals(method)){
			addBookCategory(request,response);
			return;
		}else if("getBookCategoryList".equals(method)){
			getBookCategoryList(request,response);
			return;
		}else if("deleteBookCategory".equals(method)){
			deleteBookCategory(request,response);
			return;
		}else if("editBookCategory".equals(method)){
			editBookCategory(request,response);
			return;
		}
	}
	//�޸ķ���
	private void editBookCategory(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		//��������
		BookCategoryDao bookCategoryDao=new BookCategoryDao();
		String name=request.getParameter("name");
		int id=Integer.parseInt(request.getParameter("id"));
		Map<String, String> ret=new HashMap<String, String>();
		//�жϸ�ʽ
		if(StringUtil.isEmpty(name)){
			ret.put("type", "error");
			ret.put("msg", "����������Ϊ��");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(isExitCategory(name,id)){
			ret.put("type", "error");
			ret.put("msg", "�������Ѵ��ڣ��뻻һ����");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		//�����û�
		BookCategory bookCategory=new BookCategory();
		bookCategory.setName(name);
		bookCategory.setId(id);
		if(!bookCategoryDao.update(bookCategory)){
			ret.put("type", "error");
			ret.put("msg", "����ʧ��");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		ret.put("type", "success");
		ret.put("msg", "���³ɹ�");
		writrToPage(response, JSONObject.toJSONString(ret));
		return;
	}

	private boolean isExitCategory(String name, int id) {
		// TODO Auto-generated method stub
		Page<BookCategory> page = new Page<BookCategory>(1, 10);
		BookCategoryDao bookCategoryDao=new BookCategoryDao();
		page.getSearchProporties().add(new SearchProperty("name", name, Operator.EQ));
		page = bookCategoryDao.findList(page);
		if(page.getContent().size() > 0){
			BookCategory bookCategory = page.getContent().get(0);
			if(bookCategory.getId() != id)return true;
		}
		return false;
	}

	//ɾ���û�
	private void deleteBookCategory(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		Map<String, String> ret = new HashMap<String, String>();
		BookCategoryDao bookCategoryDao=new BookCategoryDao();
		String[] ids=request.getParameterValues("ids[]");
		if(ids==null||ids.length==0){
			ret.put("type", "error");
			ret.put("msg", "��ѡ��Ҫɾ���ķ���");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		int[] idArr=new int[ids.length];
		for(int i=0;i<ids.length;i++){
			idArr[i]=Integer.parseInt(ids[i]);
		}
		if(!bookCategoryDao.delete(idArr)){
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

	//��ȡ�����б�
	private void getBookCategoryList(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("UTF-8");
		BookCategoryDao bookCategoryDao=new BookCategoryDao();
		String name = request.getParameter("name");
		if(name == null){
			name = "";
		}
		int pageNumber = Integer.parseInt(request.getParameter("page"));
		int pageSize = Integer.parseInt(request.getParameter("rows"));
		Page<BookCategory> page = new Page<BookCategory>(pageNumber, pageSize);
		page.getSearchProporties().add(new SearchProperty("name", "%"+name+"%", Operator.LIKE));
		page = bookCategoryDao.findList(page);
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("total", page.getTotal());
		ret.put("rows", page.getContent());
		response.getWriter().write(JSONObject.toJSONString(ret));
	}
	//���ӷ���
	private void addBookCategory(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		Map<String, String> ret = new HashMap<String, String>();
		String name = request.getParameter("name");
		BookCategoryDao bookCategoryDao=new BookCategoryDao();
		if(StringUtil.isEmpty(name)){
			ret.put("type", "error");
			ret.put("msg", "����������Ϊ��!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(isExitCategory(name)){
			ret.put("type", "error");
			ret.put("msg", "�������Ѿ�����!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		BookCategory bookCategory=new BookCategory();
		bookCategory.setName(name);
		if(!bookCategoryDao.add(bookCategory)){
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
	//�ж������Ƿ����
	private boolean isExitCategory(String name) {
		// TODO Auto-generated method stub
		BookCategoryDao bookCategoryDao=new BookCategoryDao();
		Page<BookCategory> page = new Page<BookCategory>(1, 10);
		page.getSearchProporties().add(new SearchProperty("name", name, Operator.EQ));
		page = bookCategoryDao.findList(page);
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

}
