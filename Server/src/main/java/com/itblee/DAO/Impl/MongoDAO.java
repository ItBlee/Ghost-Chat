package com.itblee.DAO.Impl;

import com.itblee.DAO.GenericDAO;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.itblee.DAO.entity.BaseEntity;
import com.itblee.DAO.helper.MongoHelper;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MongoDAO<T extends BaseEntity> implements GenericDAO<T> {
    protected final MongoCollection<T> collection;

    private final Class<T> entityClass;

    @SuppressWarnings("ALL")
    public MongoDAO(String collectionName) {
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
        this.collection = MongoHelper.getInstance().getCollection(collectionName, entityClass);
    }

    @Override
    public T findById(String id) {
        return collection.find(Filters.eq(new ObjectId(id)), entityClass).limit(1).first();
    }

    @Override
    public Iterable<T> findByCondition(Bson condition) {
        return findByCondition(condition, 0, null);
    }

    @Override
    public Iterable<T> findByCondition(Bson condition, int limit) {
        return findByCondition(condition, limit, null);
    }

    @Override
    public Iterable<T> findByCondition(Bson condition, int limit, Bson sort) {
        return collection.find(condition, entityClass).limit(limit).sort(sort);
    }

    @Override
    public Iterable<T> getAll() {
        return getAll(0, null);
    }

    @Override
    public Iterable<T> getAll(int limit) {
        return getAll(limit, null);
    }

    @Override
    public Iterable<T> getAll(int limit, Bson sort) {
        return collection.find(entityClass).limit(limit).sort(sort);
    }

    @Override
    public String insertOne(T entity) {
        try {
            InsertOneResult result = collection.insertOne(entity);
            if (result.wasAcknowledged() && result.getInsertedId() != null)
                return result.getInsertedId().asString().getValue();
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<Integer, String> insertMany(List<T> entities) {
        Map<Integer, String> map = new HashMap<Integer, String>();
        try {
            InsertManyResult results = collection.insertMany(entities);;
            if (results.wasAcknowledged()) {
                for (Map.Entry<Integer, BsonValue> entry: results.getInsertedIds().entrySet())
                    map.put(entry.getKey(), entry.getValue().asString().getValue());
                return map;
            }
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateOne(String id, List<Bson> updates) {
        try {
            updates.add(Updates.currentDate("modifiedDate"));
            UpdateResult result = collection.updateOne(Filters.eq(new ObjectId(id)), updates);
            return result.wasAcknowledged();
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public UpdateResult updateMany(Bson condition, List<Bson> updates) {
        try {
            updates.add(Updates.currentDate("modifiedDate"));
            return collection.updateMany(condition, updates);
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteOne(String id) {
        try {
            DeleteResult result = collection.deleteOne(Filters.eq(new ObjectId(id)));
            return result.wasAcknowledged();
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public DeleteResult deleteMany(Bson condition) {
        try {
            return collection.deleteMany(condition);
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return null;
    }
}
