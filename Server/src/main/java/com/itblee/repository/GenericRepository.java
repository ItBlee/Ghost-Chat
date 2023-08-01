package com.itblee.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GenericRepository<T> {
	Optional<T> findById(ObjectId id);

	FindIterable<T> findByCondition(Bson condition);

	FindIterable<T> findByCondition(Bson condition, int limit);

	FindIterable<T> findByCondition(Bson condition, Bson sort);

	FindIterable<T> findByCondition(Bson condition, int limit, Bson sort);

	FindIterable<T> findAll();

	FindIterable<T> findAll(String... fields);

	FindIterable<T> findAll(int limit);

	FindIterable<T> findAll(Bson sort);

	FindIterable<T> findAll(int limit, Bson sort);

	Optional<ObjectId> insertOne(T document);

	Map<Integer, ObjectId> insertMany(List<T> documents);

	boolean updateOne(ObjectId id, List<Bson> updates);

	UpdateResult updateMany(Bson condition, List<Bson> updates);

	boolean deleteOne(ObjectId id);

	DeleteResult deleteMany(Bson condition);
}
