package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.StringUtil;

import com.alibaba.fastjson.JSONObject;

import dao.BorrowDao;

/**
 * Servlet implementation class BorrowStatsServlet
 */
@WebServlet("/BorrowStatsServlet")
public class BorrowStatsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BorrowStatsServlet() {
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
		if("borrowStatsList".equals(method)){
			borrowStatsList(request,response);
		}
	}

	private void borrowStatsList(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		BorrowDao borrowDao=new BorrowDao();
		String type = request.getParameter("type");
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("type", "success");
		if("user".equals(type)){
			ret.put("title", "”√ªßΩË‘ƒ≈≈––∞Ò");
		}
		if("book".equals(type)){
			ret.put("title", "Õº ÈΩË‘ƒ≈≈––∞Ò");
		}
		List<Map<String, Object>> stats = borrowDao.getStats(type);
		List<String> nameList = new ArrayList<String>();
		List<Integer> numList = new ArrayList<Integer>();
		for(Map<String, Object> m:stats){
			nameList.add(m.get("name").toString());
			numList.add(Integer.parseInt(m.get("num")+""));
		}
		ret.put("nameList", nameList);
		ret.put("numList", numList);
		StringUtil.writrToPage(response, JSONObject.toJSONString(ret));
	}

}
