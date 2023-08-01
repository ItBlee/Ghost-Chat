package com.itblee.service;

import com.itblee.repository.document.Log;

import java.util.Collection;
import java.util.List;

public interface LogService {
    Collection<Log> findByUsernameDescending(String username);
    Collection<Log> findMessageHistoryAscending(String... names);
    void record(List<Log> logs);
}
