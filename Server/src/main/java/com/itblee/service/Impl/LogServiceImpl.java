package com.itblee.service.Impl;

import com.itblee.repository.LogRepository;
import com.itblee.repository.Impl.LogRepositoryImpl;
import com.itblee.repository.document.Log;
import com.itblee.service.LogService;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LogServiceImpl implements LogService {

    private final LogRepository logRepository = new LogRepositoryImpl();

    @Override
    public List<Log> findByUsernameDescending(String username) {
        Collection<Bson> filters = new ArrayList<>();
        filters.add(Filters.eq("username", username));
        Bson sort = Sorts.descending("createdDate");
        FindIterable<Log> results = logRepository.findByCondition(Filters.and(filters), sort);
        return results.into(new ArrayList<>());
    }

    @Override
    public List<Log> findMessageHistoryAscending(String... names) {
        Collection<Bson> filters = new ArrayList<>();
        filters.add(Filters.in("username", names));
        filters.add(Filters.in("contact", names));
        filters.add(Filters.eq("action", "Send message"));
        Bson sort = Sorts.ascending("createdDate");
        FindIterable<Log> results = logRepository.findByCondition(Filters.and(filters), sort);
        return results.into(new ArrayList<>());
    }

    @Override
    public void record(List<Log> logs) {
        logRepository.insertMany(logs);
    }

}
