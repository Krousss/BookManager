/**
 * 
 */
package entity;

import java.sql.Timestamp;

import annotation.Column;

/**
 * @author Yph
 *�û����ļ�¼ʵ����
 */
public class Borrow extends BaseEntity {
	//�����û�
	@Column(name="user",isForeignEntity=true)
	private User user;
	private int userId;
	//�����鼮
	@Column(name="book",isForeignEntity=true)
	private Book book;
	private int bookId;
	//��������
	private int number;
	//����״̬,1Ϊ������,2Ϊ�ѹ黹
	private int status=1;
	private Timestamp borrowTime;//���ʱ��
	private Timestamp returnTime;//�黹ʱ��
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public Timestamp getBorrowTime() {
		return borrowTime;
	}
	public void setBorrowTime(Timestamp borrowTime) {
		this.borrowTime = borrowTime;
	}
	public Timestamp getReturnTime() {
		return returnTime;
	}
	public void setReturnTime(Timestamp returnTime) {
		this.returnTime = returnTime;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	
	
}
