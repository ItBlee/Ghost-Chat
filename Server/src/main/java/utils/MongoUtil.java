package utils;

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

public class MongoUtil {
    private final String host;
    private final Integer port;
    private final String dbName;
    private final String username;
    private final String password;

    private static MongoClient client;
    private static MongoDatabase database;

    public MongoUtil(String dbName) {
        this(null, null, dbName, null, null);
    }

    public MongoUtil(String host, Integer port, String dbName) {
        this(host, port, dbName, null, null);
    }

    public MongoUtil(String host, Integer port, String dbName, String username, String password) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
    }

    private MongoClientSettings clientSettings() {
        String uri = System.getProperty("mongodb.uri");
        if (host != null && port != null)
            uri = "mongodb://" + host + ":" + port;
        if (username != null && password != null)
            uri = "mongodb://" + username + ":" + password + "@" + host + ":" + port;
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
            database = getClient().getDatabase(dbName);
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
