package com.dao;

import java.util.List;

public interface DAO<T> {
	
	void add(T object);
	List<T> getAll();
	T findBy(int id);
	void delete(int id);
	

}
