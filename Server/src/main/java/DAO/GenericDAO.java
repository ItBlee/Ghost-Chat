package DAO;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;

public interface GenericDAO<T> {
	T findById(String id);

	Iterable<T> findByCondition(Bson condition);

	Iterable<T> findByCondition(Bson condition, int limit);

	Iterable<T> findByCondition(Bson condition, int limit, Bson sort);

	Iterable<T> getAll();

	Iterable<T> getAll(int limit);

	Iterable<T> getAll(int limit, Bson sort);

	String insertOne(T entity);

	Map<Integer, String> insertMany(List<T> entities);

	boolean updateOne(String id, List<Bson> updates);

	UpdateResult updateMany(Bson condition, List<Bson> updates);

	boolean deleteOne(String id);

	DeleteResult deleteMany(Bson condition);
}
