package entity;

import java.io.InputStream;

import annotation.Column;

//ͼ��ʵ��
public class Book extends BaseEntity {
	//ͼ������
	private String name;
	//ͼ����࣬�����ⲿʵ��
	@Column(name="book_category",isForeignEntity=true)
	private BookCategory bookCategory;
	//ͼ��״̬,�ɽ��벻�ɽ�
	private int status;
	//ͼ������
	private int number;
	//�ɽ�����
	private int free_number;
	//ͼ�����
	private String info;
	private String photo;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BookCategory getBookCategory() {
		return bookCategory;
	}
	public void setBookCategory(BookCategory bookCategory) {
		this.bookCategory = bookCategory;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getFree_number() {
		return free_number;
	}
	public void setFree_number(int free_number) {
		this.free_number = free_number;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
}
