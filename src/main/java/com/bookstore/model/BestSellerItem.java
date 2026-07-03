package com.bookstore.model;

public class BestSellerItem {
	private int bookId;
	private String title;
	private int soldQty;
	private int rentedQty;

	public int getTotalQty() {
		return soldQty + rentedQty;
	}

	public BestSellerItem(int bookId, String title, int soldQty, int rentedQty) {
		super();
		this.bookId = bookId;
		this.title = title;
		this.soldQty = soldQty;
		this.rentedQty = rentedQty;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSoldQty() {
		return soldQty;
	}

	public void setSoldQty(int soldQty) {
		this.soldQty = soldQty;
	}

	public int getRentedQty() {
		return rentedQty;
	}

	public void setRentedQty(int rentedQty) {
		this.rentedQty = rentedQty;
	}

}
