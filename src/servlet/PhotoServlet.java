package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import dao.BookDao;
import entity.Book;

/**
 * Servlet implementation class PhotoServlet
 */
@WebServlet("/PhotoServlet")
public class PhotoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PhotoServlet() {
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
		if("setPhoto".equals(method)){
			setPhoto(request,response);
			return;
		}else if("getPhoto".equals(method)){
			getPhoto(request,response);
		}
	}

	private void getPhoto(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// TODO Auto-generated method stub
		int sid=Integer.parseInt(request.getParameter("sid"));
		BookDao bookDao=new BookDao();
		Book book = bookDao.find(sid);
		String photo = book.getPhoto();
		File file=new File(photo);
		try {
			FileInputStream fileInputStream=new FileInputStream(file);
			byte[] b=new byte[fileInputStream.available()];
			fileInputStream.read(b);
			response.getOutputStream().write(b,0,b.length);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setPhoto(HttpServletRequest request,
			HttpServletResponse response) {
		// TODO Auto-generated method stub
		//创建一个解析器工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //文件上传解析器
        ServletFileUpload upload = new ServletFileUpload(factory);
        // 判断enctype属性是否为multipart/form-data 
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            try {
                //解析请求，将表单中每个输入项封装成一个FileItem对象
                List<FileItem> fileItems = upload.parseRequest(request);
                // 迭代表单数据
                for (FileItem fileItem : fileItems) {
                    //判断输入的类型是 普通输入项 还是文件
                    if (fileItem.isFormField()) {
                        //普通输入项 ,得到input中的name属性的值,fileItem.getFieldName()
                        ////得到输入项中的值,fileItem.getString("UTF-8"),"UTF-8"防止中文乱码
                        System.out.println(fileItem.getFieldName()+"\t"+fileItem.getString("UTF-8"));
                    } else {
                        //上传的是文件，获得文件上传字段中的文件名
                        //注意IE或FireFox中获取的文件名是不一样的，IE中是绝对路径，FireFox中只是文件名
                        String fileName = fileItem.getName();
                        System.out.println(fileName);
                        //Substring是字符串截取，返回值是一个截取后的字符串
                        //lastIndexOf(".")是从右向左查,获取.之后的字符串
                        String ext = fileName.substring(fileName.lastIndexOf("."));
                        //UUID.randomUUID().toString()是javaJDK提供的一个自动生成主键的方法, UUID的唯一缺陷在于生成的结果串会比较长
                        String name = UUID.randomUUID()+ext;
                        //将FileItem对象中保存的主体内容保存到某个指定的文件中
                        String path = request.getSession().getServletContext().getRealPath("");
                        System.out.println(path);
                        int id=Integer.parseInt(request.getParameter("pid"));
                        BookDao bookDao=new BookDao();
                        Book book = bookDao.find(id);
                        String oldPhoto = book.getPhoto();
                        if(oldPhoto!=null){
                        	File oldFile=new File(oldPhoto);
                        	if(oldFile.exists()){
                        		oldFile.delete();
                        	}
                        }
                        File file = new File(path+"\\file\\"+name);
                        book.setPhoto(path+"\\file\\"+name);
                        if(bookDao.update(book)){
                        	response.setCharacterEncoding("UTF-8");
                        	response.getWriter().write("<script>alert('修改成功');window.location=view/bookList.jsp</script>");
                        }else{
                        	response.setCharacterEncoding("UTF-8");
                        	response.getWriter().write("<script>alert('修改失败');window.location=view/bookList.jsp</script>");
                        }
                        fileItem.write(file);
                    }
                }
            } catch (FileUploadException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }else{
            System.out.println("普通表单");
        }
	}

}
