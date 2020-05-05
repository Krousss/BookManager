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
 * 借阅管理servlet
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
	//还书
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
			ret.put("msg", "请选择要借阅的信息!");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		if(borrow.getNumber() < realReturnNumber){
			ret.put("type", "error");
			ret.put("msg", "归还数量大于借出数量");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		
		//判断是全部归还还是部分归还
		if(realReturnNumber == borrow.getNumber()){
			//全部归还
			//首先更新归还状态，表示已经归还
			borrow.setStatus(2);
			borrow.setReturnTime(new Timestamp(System.currentTimeMillis()));
			if(!borrowDao.update(borrow)){
				ret.put("type", "error");
				ret.put("msg", "借阅信息更新失败!");
				StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
				return;
			}
		}
		
		//判断是否是部分归还
		if(borrow.getNumber() > realReturnNumber){
			//部分归还
			borrow.setNumber(borrow.getNumber() - realReturnNumber);
			if(!borrowDao.update(borrow)){
				ret.put("type", "error");
				ret.put("msg", "借阅信息更新失败!");
				StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
				return;
			}
		}
		
		//更新图书信息
		Book book = bookDao.find(borrow.getBookId());
		book.setFree_number(book.getFree_number() + realReturnNumber);
		if(book.getStatus() == 2){
			//若图书状态是全部借出
			book.setStatus(1);//设置成状态为可借
		}
		if(!bookDao.update(book)){
			ret.put("type", "error");
			ret.put("msg", "图书信息更新失败!");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		
		ret.put("type", "success");
		ret.put("msg", "图书更新成功!");
		StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
	}

	//得到借阅列表
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
		//管理员
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
		//普通用户
		if(loginedUser.getType() != 3){
			page.getSearchProporties().add(new SearchProperty("user_id", loginedUser.getId(), Operator.EQ));
		}
		//根据图书名称搜索
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
		
		//根据状态搜索
		if(status != -1){
			page.getSearchProporties().add(new SearchProperty("status", status, Operator.EQ));
		}
		page=borrowDao.findList(page);
		Map<String,Object> ret = new HashMap<String,Object>();
		ret.put("total", page.getTotal());
		ret.put("rows", page.getContent());
		StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
	}

	//添加借阅记录
	private void addBorrow(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		//获取数据
		Map<String, String> ret=new HashMap<String, String>();
		int id=Integer.parseInt(request.getParameter("bookId"));
		int borrow_number=Integer.parseInt(request.getParameter("realBorrowNumber"));
		bookDao=new BookDao();
		borrowDao=new BorrowDao();
		Book book = bookDao.find(id);
		if(borrow_number<=0){
			ret.put("type", "error");
			ret.put("msg", "请正确填写借阅数量");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		//进行数据格式检查
		if(book.getFree_number()<borrow_number){
			ret.put("type", "error");
			ret.put("msg", "数量超过最大可借数!");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		User user=(User)request.getSession().getAttribute("user");
		//如果该用户有该书的借书记录
		if(isExit(book,user)){
			ret.put("type", "error");
			ret.put("msg", "先行归还方可再次借阅");
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
			ret.put("msg", "借阅失败，请联系管理员");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		book.setFree_number(book.getFree_number()-borrow_number);
		if(book.getFree_number() == 0){
			book.setStatus(2);
		}
		if(!bookDao.update(book)){
			ret.put("type", "error");
			ret.put("msg", "更新图书失败");
			StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
			return;
		}
		ret.put("type", "success");
		ret.put("msg", "借阅成功");
		StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
		return;
	}
	//判断该用户是否有该书的借书记录
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
