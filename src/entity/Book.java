package entity;

import java.io.InputStream;

import annotation.Column;

//图书实体
public class Book extends BaseEntity {
	//图书名称
	private String name;
	//图书分类，引用外部实体
	@Column(name="book_category",isForeignEntity=true)
	private BookCategory bookCategory;
	//图书状态,可借与不可借
	private int status;
	//图书数量
	private int number;
	//可借数量
	private int free_number;
	//图书介绍
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
