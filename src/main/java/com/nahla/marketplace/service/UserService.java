package com.nahla.marketplace.service;

import com.nahla.marketplace.model.User;
import com.nahla.marketplace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User createUser(User user) {
        return repo.save(user);
    }
}