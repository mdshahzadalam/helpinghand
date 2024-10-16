package com.ecom.util;

public enum OrderStatus {

	IN_PROGRESS(1, "In Progress"), BOOKING_RECEIVED(2, "Booking Received"),CANCEL(3,"Cancelled"),SUCCESS(4,"Success");
	
	//, PRODUCT_PACKED(3, "Product Packed"),
	//OUT_FOR_DELIVERY(4, "Out for Delivery"), DELIVERED(5, "Delivered"),

	private Integer id;

	private String name;

	private OrderStatus(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}