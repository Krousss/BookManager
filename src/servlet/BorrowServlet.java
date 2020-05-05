package servlet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import page.Operator;
import page.Page;
import page.SearchProperty;
import util.StringUtil;

import com.alibaba.fastjson.JSONObject;

import dao.BaseDao;
import dao.BookDao;
import dao.BorrowDao;
import dao.UserDao;
import entity.Book;
import entity.Borrow;
import entity.User;

/**
 * Servlet implementation class BorrowServlet
 * ���Ĺ���servlet
 */
@WebServlet("/BorrowServlet")
public class BorrowServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private BookDao bookDao;
    private BorrowDao borrowDao;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BorrowServlet() {
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
		if("addBorrow".equals(method)){
			addBorrow(request,response);
			return;
		}else if("borrowList".equals(method)){
			borrowList(request,response);
			return;
		}else if("bookReturn".equals(method)){
			bookReturn(request,response);
			return;
		}
	}
	//����
	private void bookReturn(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		int id = Integer.parseInt(request.getParameter("id"));
		int realReturnNumber = Integer.parseInt(request.getParameter("realReturnNumber"));
		Map<String, Object> ret = new HashMap<String, Object>();
		borrowDao=new BorrowDao();
		bookDao=new BookDao();
		Borrow borrow = borrowDao.find(id);
		if(borrow == null){
			ret.put("type", "error");
			ret.put("msg", "��ѡ��Ҫ���ĵ���Ϣ!");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(borrow.getNumber() < realReturnNumber){
			ret.put("type", "error");
			ret.put("msg", "�黹�������ڽ������");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		
		//�ж���ȫ���黹���ǲ��ֹ黹
		if(realReturnNumber == borrow.getNumber()){
			//ȫ���黹
			//���ȸ��¹黹״̬����ʾ�Ѿ��黹
			borrow.setStatus(2);
			borrow.setReturnTime(new Timestamp(System.currentTimeMillis()));
			if(!borrowDao.update(borrow)){
				ret.put("type", "error");
				ret.put("msg", "������Ϣ����ʧ��!");
				StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
				return;
			}
		}
		
		//�ж��Ƿ��ǲ��ֹ黹
		if(borrow.getNumber() > realReturnNumber){
			//���ֹ黹
			borrow.setNumber(borrow.getNumber() - realReturnNumber);
			if(!borrowDao.update(borrow)){
				ret.put("type", "error");
				ret.put("msg", "������Ϣ����ʧ��!");
				StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
				return;
			}
		}
		
		//����ͼ����Ϣ
		Book book = bookDao.find(borrow.getBookId());
		book.setFree_number(book.getFree_number() + realReturnNumber);
		if(book.getStatus() == 2){
			//��ͼ��״̬��ȫ�����
			book.setStatus(1);//���ó�״̬Ϊ�ɽ�
		}
		if(!bookDao.update(book)){
			ret.put("type", "error");
			ret.put("msg", "ͼ����Ϣ����ʧ��!");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		
		ret.put("type", "success");
		ret.put("msg", "ͼ����³ɹ�!");
		StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
	}

	//�õ������б�
	private void borrowList(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		borrowDao=new BorrowDao();
		bookDao=new BookDao();
		String username = request.getParameter("username");
		String bookName = request.getParameter("bookName");
		int status = Integer.parseInt(StringUtil.isEmpty(request.getParameter("status")) ? "-1" : request.getParameter("status"));
		int pageNumber = Integer.parseInt(request.getParameter("page"));
		int pageSize = Integer.parseInt(request.getParameter("rows"));
		Page<Borrow> page = new Page<Borrow>(pageNumber, pageSize);
		User loginedUser = (User)request.getSession().getAttribute("user");
		//����Ա
		if(loginedUser.getType() == 3){
			if(!StringUtil.isEmpty(username)){
				Page<User> userPage = new Page<User>(1, 10);
				userPage.getSearchProporties().add(new SearchProperty("username", "%"+username + "%", Operator.LIKE));
				UserDao userDao=new UserDao();
				userPage = userDao.findList(userPage);
				if(userPage.getContent().size() > 0){
					List<String> ids = new ArrayList<String>();
					for(User u:userPage.getContent()){
						ids.add(u.getId()+"");
					}
					page.getSearchProporties().add(new SearchProperty("user_id", StringUtil.join(ids, ","), Operator.IN));
				}
			}
		}
		//��ͨ�û�
		if(loginedUser.getType() != 3){
			page.getSearchProporties().add(new SearchProperty("user_id", loginedUser.getId(), Operator.EQ));
		}
		//����ͼ����������
		if(!StringUtil.isEmpty(bookName)){
			Page<Book> bookPage = new Page<Book>(1, 10);
			bookPage.getSearchProporties().add(new SearchProperty("name", "%"+bookName+"%", Operator.LIKE));
			bookPage = bookDao.findList(bookPage);
			List<String> ids = new ArrayList<String>();
			for(Book b:bookPage.getContent()){
				ids.add(b.getId()+"");
			}
			if(bookPage.getContent().size() > 0){
				page.getSearchProporties().add(new SearchProperty("book_id", StringUtil.join(ids, ","), Operator.IN));
			}
		}
		
		//����״̬����
		if(status != -1){
			page.getSearchProporties().add(new SearchProperty("status", status, Operator.EQ));
		}
		page=borrowDao.findList(page);
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put("total", page.getTotal());
		ret.put("rows", page.getContent());
		StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
	}

	//��ӽ��ļ�¼
	private void addBorrow(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		//��ȡ����
		Map<String, String> ret=new HashMap<String, String>();
		int id=Integer.parseInt(request.getParameter("bookId"));
		int borrow_number=Integer.parseInt(request.getParameter("realBorrowNumber"));
		bookDao=new BookDao();
		borrowDao=new BorrowDao();
		Book book = bookDao.find(id);
		if(borrow_number<=0){
			ret.put("type", "error");
			ret.put("msg", "����ȷ��д��������");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		//�������ݸ�ʽ���
		if(book.getFree_number()<borrow_number){
			ret.put("type", "error");
			ret.put("msg", "�����������ɽ���!");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		User user=(User)request.getSession().getAttribute("user");
		//������û��и���Ľ����¼
		if(isExit(book,user)){
			ret.put("type", "error");
			ret.put("msg", "���й黹�����ٴν���");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		Borrow borrow=new Borrow();
		borrow.setUser(user);
		borrow.setBook(book);
		borrow.setNumber(borrow_number);
		borrow.setBookId(id);
		borrow.setUserId(user.getId());
		borrow.setBorrowTime(new Timestamp(System.currentTimeMillis()));
		if(!borrowDao.add(borrow)){
			ret.put("type", "error");
			ret.put("msg", "����ʧ�ܣ�����ϵ����Ա");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		book.setFree_number(book.getFree_number()-borrow_number);
		if(book.getFree_number() == 0){
			book.setStatus(2);
		}
		if(!bookDao.update(book)){
			ret.put("type", "error");
			ret.put("msg", "����ͼ��ʧ��");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		ret.put("type", "success");
		ret.put("msg", "���ĳɹ�");
		StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
		return;
	}
	//�жϸ��û��Ƿ��и���Ľ����¼
	private boolean isExit(Book book, User user) {
		// TODO Auto-generated method stub
		Page<Borrow> page = new Page<Borrow>(1, 999);
		page.getSearchProporties().add(new SearchProperty("user_id", user.getId(), Operator.EQ));
		page.getSearchProporties().add(new SearchProperty("status", 1, Operator.EQ));
		page = borrowDao.findList(page);
		if(page.getContent().size() >0){
			for(Borrow b:page.getContent()){
				if(b.getBookId() == book.getId())return true;
			}
		}
		return false;
	}
}
