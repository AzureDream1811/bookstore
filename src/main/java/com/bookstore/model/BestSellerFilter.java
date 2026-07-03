package com.bookstore.model;

import java.time.LocalDate;

public class BestSellerFilter {
	public enum Type {
		ALL, SOLD, RENTED
	}

	public enum SortOrder {
		ASC, DESC
	}

	private LocalDate fromDate; // null → mặc định 30 ngày gần nhất
	private LocalDate toDate; // null → mặc định hôm nay
	private Type type;
	private SortOrder sortOrder;

	public BestSellerFilter(LocalDate fromDate, LocalDate toDate, Type type, SortOrder sortOrder) {
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.type = type;
		this.sortOrder = sortOrder;
	}

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}

}
