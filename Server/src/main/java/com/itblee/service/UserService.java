package com.itblee.service;

import com.itblee.exception.BadRequestException;
import com.itblee.exception.ForbiddenException;
import com.itblee.exception.NotFoundException;
import com.itblee.exception.UserExistException;
import com.itblee.repository.document.UserDetail;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserDetail> findAllWithLimitFields();
    Optional<UserDetail> findByUsername(String username);
    UserDetail login(String username, String password) throws NotFoundException, ForbiddenException, BadRequestException;
    UserDetail register(String username, String password) throws BadRequestException, UserExistException;
    void changePassword(String username,String hash, String salt) throws NotFoundException;
    void resetPassword(String username) throws NotFoundException;
    void banUser(String username) throws NotFoundException;
    void activeUser(String username) throws NotFoundException;
    void changeStatus(String username, int status) throws NotFoundException;
}
