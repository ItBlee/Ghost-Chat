package com.itblee.repository.Impl;

import com.itblee.repository.UserRepository;
import com.itblee.repository.document.UserDetail;

public class UserRepositoryImpl extends MongoRepository<UserDetail> implements UserRepository {

    public UserRepositoryImpl() {
        super("userdetail", UserDetail.class);
    }

}
