package servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import page.Operator;
import page.Page;
import page.SearchProperty;
import util.StringUtil;
import dao.BookCategoryDao;
import dao.BookDao;
import dao.UserDao;
import entity.Book;
import entity.BookCategory;
import entity.User;

/**
 * Servlet implementation class BookServlet
 * ͼ��servlet
 */
@WebServlet("/BookServlet")
public class BookServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BookServlet() {
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
		if("addBook".equals(method)){
			addBook(request,response);
			return;
		}else if("getBookCategoryComboxData".equals(method)){
			getBookCategoryComboxData(request,response);
			return;
		}else if("getBookList".equals(method)){
			getBookList(request,response);
			return;
		}else if("deleteBook".equals(method)){
			deleteBook(request,response);
			return;
		}else if("editBook".equals(method)){
			editBook(request,response);
			return;
		}
	}
	//�޸�ͼ����Ϣ
	private void editBook(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		String name = request.getParameter("name");
		int id = Integer.parseInt(request.getParameter("id"));
		int bookCategoryId = Integer.parseInt(request.getParameter("bookCategoryId"));
		int status = Integer.parseInt(request.getParameter("status"));
		int number = Integer.parseInt(request.getParameter("number"));
		String info = request.getParameter("info");
		BookDao bookDao=new BookDao();
		BookCategoryDao bookCategoryDao=new BookCategoryDao();
		Map<String, Object> ret = new HashMap<String, Object>();
		if(StringUtil.isEmpty(name)){
			ret.put("type", "error");
			ret.put("msg", "ͼ�����Ʋ���Ϊ��!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		BookCategory bookCategory = bookCategoryDao.find(bookCategoryId);
		if(bookCategory == null){
			ret.put("type", "error");
			ret.put("msg", "ͼ����಻��Ϊ��!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		//���ͼ�������Ƿ���ڽ������
		Book oldBook = bookDao.find(id);
		//�Ѿ����������
		int borrowedNumber = oldBook.getNumber() - oldBook.getFree_number();
		if(number < borrowedNumber){
			ret.put("type", "error");
			ret.put("msg", "��������С���Ѿ����������!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		
		Book book = new Book();
		book.setId(id);
		book.setBookCategory(bookCategory);
		book.setName(name);
		book.setStatus(status);
		book.setNumber(number);
		book.setFree_number(number-borrowedNumber);
		book.setInfo(info);
		if(book.getFree_number() == 0){
			book.setStatus(2);
		}
		
		if(!bookDao.update(book)){
			ret.put("type", "error");
			ret.put("msg", "ͼ�����ʧ�ܣ�����ϵ����Ա!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		ret.put("type", "success");
		ret.put("msg", "ͼ����³ɹ�!");
		writrToPage(response, JSONObject.toJSONString(ret));
	}

	//ɾ��ͼ��
	private void deleteBook(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		Map<String, String> ret = new HashMap<String, String>();
		BookDao bookDao=new BookDao();
		String[] ids=request.getParameterValues("ids[]");
		if(ids==null||ids.length==0){
			ret.put("type", "error");
			ret.put("msg", "��ѡ��Ҫɾ����ͼ��");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		int[] idArr=new int[ids.length];
		for(int i=0;i<ids.length;i++){
			idArr[i]=Integer.parseInt(ids[i]);
		}
		if(!bookDao.delete(idArr)){
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

	//��ȡͼ���б�
	private void getBookList(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		String name = request.getParameter("name");
		String bbcid = request.getParameter("bookCategoryId");
		BookDao bookDao=new BookDao();
		BookCategoryDao bookCategoryDao=new BookCategoryDao();
		if(name == null){
			name = "";
		}
		int pageNumber = Integer.parseInt(request.getParameter("page"));
		int pageSize = Integer.parseInt(request.getParameter("rows"));
		Page<Book> page = new Page<Book>(pageNumber, pageSize);
		page.getSearchProporties().add(new SearchProperty("name", "%"+name+"%", Operator.LIKE));
		if(!StringUtil.isEmpty(bbcid) && !"0".equals(bbcid)){
			page.getSearchProporties().add(new SearchProperty("book_category", bookCategoryDao.find(Integer.parseInt(bbcid)), Operator.EQ));
		}
		page = bookDao.findList(page);
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("total", page.getTotal());
		ret.put("rows", page.getContent());
		writrToPage(response, JSONObject.toJSONString(ret));
	}

	//�������ȡ����
	private void getBookCategoryComboxData(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		Page<BookCategory> page = new Page<BookCategory>(1, 999);
		BookCategoryDao bookCategoryDao=new BookCategoryDao();
		page = bookCategoryDao.findList(page);
		Map<String, Object> ret = new HashMap<String, Object>();
		BookCategory bookCategory = new BookCategory();
		bookCategory.setId(0);
		bookCategory.setName("ȫ��");
		page.getContent().add(bookCategory);
		ret.put("type", "success");
		ret.put("values", page.getContent());
		writrToPage(response, JSONObject.toJSONString(ret));
	}

	//���ͼ��
	private void addBook(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		//��������
		Map<String, String> ret = new HashMap<String, String>();
		String name = request.getParameter("name");
		int bookCategoryId = Integer.parseInt(request.getParameter("bookCategoryId"));
		int status=Integer.parseInt(request.getParameter("status"));
		int number = Integer.parseInt(request.getParameter("number"));
		String info = request.getParameter("info");
		//�жϸ�ʽ��ȷ���
		if(StringUtil.isEmpty(name)){
			ret.put("type", "error");
			ret.put("msg", "��������Ϊ��!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(isExitUser(name)){
			ret.put("type", "error");
			ret.put("msg", "�������Ѿ�����!");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		BookCategoryDao bookCategoryDao=new BookCategoryDao();
		BookCategory bookCategory=bookCategoryDao.find(bookCategoryId);
		if(bookCategory==null){
			ret.put("type", "error");
			ret.put("msg", "����д�鼮����");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		BookDao bookDao=new BookDao();
		Book book=new Book();
		book.setName(name);
		book.setBookCategory(bookCategory);
		book.setNumber(number);
		book.setStatus(status);
		book.setInfo(info);
		book.setFree_number(number);
		if(!bookDao.add(book)){
			ret.put("type", "error");
			ret.put("msg", "���ʧ��");
			writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		ret.put("type", "success");
		ret.put("msg", "��ӳɹ�!");
		writrToPage(response, JSONObject.toJSONString(ret));
		return;
		
	}
	//�ж��޸ĺ������Ƿ��ظ�
		private boolean isExitUser(String name, int id) {
			// TODO Auto-generated method stub
			Page<Book> page = new Page<Book>(1, 10);
			BookDao bookDao=new BookDao();
			page.getSearchProporties().add(new SearchProperty("name", name, Operator.EQ));
			page = bookDao.findList(page);
			if(page.getContent().size() > 0){
				Book book = page.getContent().get(0);
				if(book.getId() != id)return true;
			}
			return false;
		}
		//�ж��Ƿ������ͬ����
		private boolean isExitUser(String name) {
			// TODO Auto-generated method stub
			BookDao bookDao=new BookDao();
			Page<Book> page = new Page<Book>(1, 10);
			page.getSearchProporties().add(new SearchProperty("name", name, Operator.EQ));
			page = bookDao.findList(page);
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
