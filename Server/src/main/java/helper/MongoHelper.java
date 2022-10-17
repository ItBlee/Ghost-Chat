package helper;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static constant.ServerConstant.*;

public class MongoHelper {
    private static MongoHelper MONGO_HELPER;

    private static MongoClient client;
    private static MongoDatabase database;

    public static synchronized MongoHelper getInstance() {
        if (MONGO_HELPER == null)
            MONGO_HELPER = new MongoHelper();
        return MONGO_HELPER;
    }

    private MongoClientSettings clientSettings() {
        String uri = System.getProperty("mongodb.uri");
        if (DB_HOST != null && DB_PORT != null)
            uri = "mongodb://" + DB_HOST + ":" + DB_PORT;
        if (DB_USERNAME != null && DB_PASSWORD != null)
            uri = "mongodb://" + DB_USERNAME + ":" + DB_PASSWORD + "@" + DB_HOST + ":" + DB_PORT;
        ConnectionString connectionString = new ConnectionString(uri);
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        return MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();
    }

    public MongoClient getClient() {
        if (client == null)
            client = MongoClients.create(clientSettings());
        return client;
    }

    public MongoDatabase getDatabase() {
        if (database == null)
            database = getClient().getDatabase(DB_NAME);
        return database;
    }

    public MongoCollection<Document> getCollection(String name) {
        MongoDatabase database = getDatabase();
        return database.getCollection(name);
    }

    public <T> MongoCollection<T> getCollection(String name, Class<T> clazz) {
        MongoDatabase database = getDatabase();
        return database.getCollection(name, clazz);
    }

    public void close() {
        if (client != null)
            client.close();
    }
}
