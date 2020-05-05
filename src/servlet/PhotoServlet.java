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
		//����һ������������
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //�ļ��ϴ�������
        ServletFileUpload upload = new ServletFileUpload(factory);
        // �ж�enctype�����Ƿ�Ϊmultipart/form-data 
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            try {
                //�������󣬽�����ÿ���������װ��һ��FileItem����
                List<FileItem> fileItems = upload.parseRequest(request);
                // ����������
                for (FileItem fileItem : fileItems) {
                    //�ж������������ ��ͨ������ �����ļ�
                    if (fileItem.isFormField()) {
                        //��ͨ������ ,�õ�input�е�name���Ե�ֵ,fileItem.getFieldName()
                        ////�õ��������е�ֵ,fileItem.getString("UTF-8"),"UTF-8"��ֹ��������
                        System.out.println(fileItem.getFieldName()+"\t"+fileItem.getString("UTF-8"));
                    } else {
                        //�ϴ������ļ�������ļ��ϴ��ֶ��е��ļ���
                        //ע��IE��FireFox�л�ȡ���ļ����ǲ�һ���ģ�IE���Ǿ���·����FireFox��ֻ���ļ���
                        String fileName = fileItem.getName();
                        System.out.println(fileName);
                        //Substring���ַ�����ȡ������ֵ��һ����ȡ����ַ���
                        //lastIndexOf(".")�Ǵ��������,��ȡ.֮����ַ���
                        String ext = fileName.substring(fileName.lastIndexOf("."));
                        //UUID.randomUUID().toString()��javaJDK�ṩ��һ���Զ����������ķ���, UUID��Ψһȱ���������ɵĽ������Ƚϳ�
                        String name = UUID.randomUUID()+ext;
                        //��FileItem�����б�����������ݱ��浽ĳ��ָ�����ļ���
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
                        	response.getWriter().write("<script>alert('�޸ĳɹ�');window.location=view/bookList.jsp</script>");
                        }else{
                        	response.setCharacterEncoding("UTF-8");
                        	response.getWriter().write("<script>alert('�޸�ʧ��');window.location=view/bookList.jsp</script>");
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
            System.out.println("��ͨ��");
        }
	}

}
