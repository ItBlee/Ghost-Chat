package com.itblee.repository.Impl;

import com.itblee.repository.LogRepository;
import com.itblee.repository.document.Log;

public class LogRepositoryImpl extends MongoRepository<Log> implements LogRepository {

    public LogRepositoryImpl() {
        super("log", Log.class);
    }

}
