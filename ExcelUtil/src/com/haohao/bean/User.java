package com.haohao.bean;

import java.util.Date;

public class User {

	private String name;
	private Integer id;
	private Float age;
	private Date date;
	private Child child;

	public Child getChild() {
		return child;
	}

	public void setChild(Child child) {
		this.child = child;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Float getAge() {
		return age;
	}

	public void setAge(Float age) {
		this.age = age;
	}

	/**
	 * 
	 */
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 * @param id
	 * @param age
	 * @param date
	 * @param child
	 */
	public User(String name, Integer id, Float age, Date date, Child child) {
		super();
		this.name = name;
		this.id = id;
		this.age = age;
		this.date = date;
		this.child = child;
	}

	/**
	 * @param name
	 * @param id
	 * @param age
	 * @param date
	 */
	public User(String name, Integer id, Float age, Date date) {
		super();
		this.name = name;
		this.id = id;
		this.age = age;
		this.date = date;
	}

}
