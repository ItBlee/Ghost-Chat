package com.itblee.core;

import com.itblee.core.Impl.UserSession;
import com.itblee.core.helper.ServerHelper;
import com.itblee.repository.document.BaseDocument;
import com.itblee.repository.document.Log;
import com.itblee.service.Impl.LogServiceImpl;
import com.itblee.service.LogService;

import java.util.*;
import java.util.stream.Collectors;

public class Logger {

    private final LogService logService = new LogServiceImpl();

    public void log(List<Log> logs) {
        logService.record(logs);
    }

    public void log(UserSession session) {
        List<Log> sessionLogs = session.getLogs().values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        session.getLogs().clear();
        log(sessionLogs);
    }

    public Collection<Log> getUserLogs(String username) {
        Collection<Log> logsInDb = logService.findByUsernameDescending(username);
        Collection<Log> results = new ArrayList<>(logsInDb);
        ServerHelper.getUserManager().getSessions().forEach(session -> {
            List<Log> logsInSession = session.getLogs(username);
            List<Log> shallowCopy = logsInSession.subList(0, logsInSession.size());
            Collections.reverse(shallowCopy);
            results.addAll(shallowCopy);
        });
        return results;
    }

    public Collection<Log> getUserHistoryChat(String... names) {
        Collection<Log> logsInDb = logService.findMessageHistoryAscending(names);
        Collection<Log> results = new ArrayList<>(logsInDb);
        List<String> nameList = Arrays.asList(names);
        List<Log> logsInSession = new ArrayList<>();
        ServerHelper.getUserManager().getSessions().forEach(session -> {
            for (String name : nameList)
                if (session.getLogs().containsKey(name))
                    logsInSession.addAll(session.getLogs(name));
        });
        logsInSession.stream()
                .filter(log -> nameList.contains(log.getUsername())
                        && nameList.contains(log.getContact())
                        && log.getAction().equals("Send message"))
                .sorted(Comparator.comparing(BaseDocument::getCreatedDate))
                .forEachOrdered(results::add);
        return results;
    }

}
