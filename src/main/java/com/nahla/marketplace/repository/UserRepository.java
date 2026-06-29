package com.nahla.marketplace.repository;

import com.nahla.marketplace.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {


    
}