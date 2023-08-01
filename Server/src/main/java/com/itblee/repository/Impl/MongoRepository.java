package com.itblee.repository.Impl;

import com.itblee.repository.GenericRepository;
import com.itblee.repository.document.BaseDocument;
import com.itblee.repository.MongoConnection;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;

public abstract class MongoRepository<T extends BaseDocument> implements GenericRepository<T> {
    protected final MongoCollection<T> collection;

    private final Class<T> entityClass;

    public MongoRepository(String collectionName, Class<T> entityClass) {
        this.entityClass = entityClass;
        this.collection = MongoConnection.getInstance().getCollection(collectionName, entityClass);
    }

    @Override
    public Optional<T> findById(ObjectId id) {
        Bson filter = Filters.eq(id);
        return Optional.ofNullable(collection.find(filter, entityClass)
                .limit(1)
                .first());
    }

    @Override
    public FindIterable<T> findByCondition(Bson condition) {
        return findByCondition(condition, 0, null);
    }

    @Override
    public FindIterable<T> findByCondition(Bson condition, int limit) {
        return findByCondition(condition, limit, null);
    }

    @Override
    public FindIterable<T> findByCondition(Bson condition, Bson sort) {
        return findByCondition(condition, 0, sort);
    }

    @Override
    public FindIterable<T> findByCondition(Bson condition, int limit, Bson sort) {
        return collection.find(condition, entityClass)
                .limit(limit)
                .sort(sort);
    }

    @Override
    public FindIterable<T> findAll() {
        return findAll(0, null);
    }

    @Override
    public FindIterable<T> findAll(String... fields) {
        Bson[] fieldsBson = Arrays.stream(fields)
                .map(Projections::include)
                .toArray(Bson[]::new);
        Bson projection = Projections.fields(fieldsBson);
        return collection.find(entityClass)
                .limit(0)
                .sort(null)
                .projection(projection);
    }

    @Override
    public FindIterable<T> findAll(int limit) {
        return findAll(limit, null);
    }

    @Override
    public FindIterable<T> findAll(Bson sort) {
        return findAll(0, sort);
    }

    @Override
    public FindIterable<T> findAll(int limit, Bson sort) {
        return collection.find(entityClass)
                .limit(limit)
                .sort(sort);
    }

    @Override
    public Optional<ObjectId> insertOne(T document) {
        if (document.getCreatedDate() == null)
            document.setCreatedDate(new Date());
        try {
            InsertOneResult result = collection.insertOne(document);
            if (result.wasAcknowledged() && result.getInsertedId() != null)
                return Optional.ofNullable(result.getInsertedId().asObjectId().getValue());
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Map<Integer, ObjectId> insertMany(List<T> documents) {
        Map<Integer, ObjectId> map = new HashMap<>();
        for (T document : documents) {
            if (document.getCreatedDate() == null)
                document.setCreatedDate(new Date());
        }
        try {
            InsertManyResult results = collection.insertMany(documents);
            if (results.wasAcknowledged()) {
                for (Map.Entry<Integer, BsonValue> entry: results.getInsertedIds().entrySet())
                    map.put(entry.getKey(), entry.getValue().asObjectId().getValue());
            }
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public boolean updateOne(ObjectId id, List<Bson> updates) {
        try {
            updates.add(Updates.set("modifiedDate", new Date()));
            Bson filter = Filters.eq(id);
            UpdateResult result = collection.updateOne(filter, updates);
            return result.wasAcknowledged();
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public UpdateResult updateMany(Bson condition, List<Bson> updates) {
        try {
            updates.add(Updates.set("modifiedDate", new Date()));
            return collection.updateMany(condition, updates);
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteOne(ObjectId id) {
        try {
            Bson filter = Filters.eq(id);
            DeleteResult result = collection.deleteOne(filter);
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
