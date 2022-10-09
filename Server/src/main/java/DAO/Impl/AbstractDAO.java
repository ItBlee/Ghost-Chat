package DAO.Impl;

import DAO.GenericDAO;

import java.util.List;

public abstract class AbstractDAO<T> implements GenericDAO<T> {
    @Override
    public T findById(Object id) {
        return null;
    }

    @Override
    public List<T> findAll() {
        return null;
    }

    @Override
    public void save(T model) {

    }

    @Override
    public T update(T model) {
        return null;
    }

    @Override
    public void remove(Object id) {

    }

    @Override
    public long countAll() {
        return 0;
    }
}
