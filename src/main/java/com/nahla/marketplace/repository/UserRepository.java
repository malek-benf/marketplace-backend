package com.nahla.marketplace.repository;
import com.nahla.marketplace.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByPhone(String phone);
    boolean existsByPhone(String phone);
    List<User> findByRole(String role);
    List<User> findByRoleAndVerifiedTrue(String role);
    List<User> findByVerifiedTrue();
    List<User> findByGovernorate(String governorate);
}
