package DAO;

import java.util.List;

public interface GenericDAO<T> {
	T findById(Object id);
	List<T> findAll();
	void save(T model);
	T update (T model);
	void remove(Object id);
	long countAll();
}
