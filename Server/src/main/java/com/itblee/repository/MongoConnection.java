package com.itblee.repository;

import com.itblee.utils.PropertyUtil;
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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MongoConnection {
    private static MongoConnection INSTANCE;

    private static MongoClient client;
    private static MongoDatabase database;

    public static synchronized MongoConnection getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MongoConnection();
        return INSTANCE;
    }

    private MongoClientSettings clientSettings() {
        String uri = PropertyUtil.getString("db.uri");
        ConnectionString connectionString = new ConnectionString(uri);
        PojoCodecProvider provider = PojoCodecProvider.builder()
                .automatic(true)
                .build();
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromProviders(provider);
        CodecRegistry defaultCodecRegistry = MongoClientSettings.getDefaultCodecRegistry();
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(defaultCodecRegistry, pojoCodecRegistry);
        return MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                //.applyToSslSettings(sslBuilder -> sslBuilder.enabled(true))
                .applyToConnectionPoolSettings(connPoolBuilder ->
                        connPoolBuilder.maxWaitTime(PropertyUtil.getInt("pool.alive"),
                                        TimeUnit.valueOf(PropertyUtil.getString("pool.time.unit")))
                                .minSize(PropertyUtil.getInt("pool.min"))
                                .maxSize(PropertyUtil.getInt("pool.max")))
                .build();
    }

    public MongoClient getClient() {
        if (client == null)
            client = MongoClients.create(clientSettings());
        return client;
    }

    public MongoDatabase getDatabase() {
        if (database == null) {
            String dbName = PropertyUtil.getString("db.name");
            database = getClient().getDatabase(dbName);
        }
        return database;
    }

    public MongoCollection<Document> getCollection(String name) {
        MongoDatabase database = getDatabase();
        if (!isCollectionExist(name))
            database.createCollection(name);
        return database.getCollection(name);
    }

    public <T> MongoCollection<T> getCollection(String name, Class<T> clazz) {
        MongoDatabase database = getDatabase();
        if (!isCollectionExist(name))
            database.createCollection(name);
        return database.getCollection(name, clazz);
    }

    public boolean isCollectionExist(String name) {
        return getDatabase().listCollectionNames().into(new ArrayList<>()).contains(name);
    }

}
