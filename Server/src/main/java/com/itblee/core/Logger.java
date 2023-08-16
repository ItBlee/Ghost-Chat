package com.itblee.core;

import com.itblee.repository.document.BaseDocument;
import com.itblee.repository.document.Log;
import com.itblee.security.Session;
import com.itblee.service.Impl.LogServiceImpl;
import com.itblee.service.LogService;

import java.util.*;
import java.util.stream.Collectors;

public class Logger {

    private final LogService logService;

    private final Map<UUID, Map<String, List<Log>>> sessionLogMap;

    public Logger() {
        logService = new LogServiceImpl();
        sessionLogMap = Collections.synchronizedMap(new HashMap<>());
    }

    public void log(User user, Log log) {
        String filter = user.getUsername() != null ? user.getUsername() : user.getUid().toString();
        sessionLogMap.computeIfAbsent(user.getUid(), uuid -> new HashMap<>())
                .computeIfAbsent(filter, username -> new ArrayList<>())
                .add(log);
    }

    public void log(Session session, Log log) {
        sessionLogMap.computeIfAbsent(session.getUid(), uuid -> new HashMap<>())
                .computeIfAbsent(session.getUid().toString(), username -> new ArrayList<>())
                .add(log);
    }

    public void record(List<Log> logs) {
        logService.record(logs);
    }

    public void record(Session session) {
        Map<String, List<Log>> found = sessionLogMap.get(session.getUid());
        if (found == null)
            throw new IllegalArgumentException();
        List<Log> sessionLogs = found.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        record(sessionLogs);
        sessionLogMap.get(session.getUid()).clear();
    }

    public Collection<Log> getUserLogs(String username) {
        Collection<Log> logsInDb = logService.findByUsernameDescending(username);
        Collection<Log> logsInCache = new ArrayList<>();
        sessionLogMap.values().forEach(logsByUser -> {
            if (logsByUser.containsKey(username)) {
                List<Log> logsInSession = logsByUser.get(username);
                List<Log> shallowCopy = logsInSession.subList(0, logsInSession.size());
                logsInCache.addAll(shallowCopy);
            }
        });
        Collection<Log> results = new ArrayList<>();
        results.addAll(logsInDb);
        results.addAll(logsInCache);
        return results.stream()
                .sorted(Comparator.comparing(BaseDocument::getCreatedDate)
                        .reversed())
                .collect(Collectors.toList());
    }

    public Collection<Log> getUserHistoryChat(String... names) {
        List<String> nameList = Arrays.asList(names);
        Collection<Log> logsInDb = logService.findMessageHistoryAscending(names);
        Collection<Log> logsInCache = new ArrayList<>();
        sessionLogMap.values().forEach(logsByUser -> {
            for (String name : nameList)
                if (logsByUser.containsKey(name)) {
                    List<Log> logsInSession = logsByUser.get(name);
                    List<Log> shallowCopy = logsInSession.subList(0, logsInSession.size());
                    logsInCache.addAll(shallowCopy);
                }
        });
        Collection<Log> results = new ArrayList<>(logsInDb);
        logsInCache.stream()
                .filter(log -> nameList.contains(log.getUsername())
                        && nameList.contains(log.getContact())
                        && log.getAction().equals("Send message"))
                .forEach(results::add);
        return results.stream()
                .sorted(Comparator.comparing(BaseDocument::getCreatedDate))
                .collect(Collectors.toList());
    }

}
