package com.nahla.marketplace.repository;
import com.nahla.marketplace.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByThreadIdOrderByCreatedAtAsc(String threadId);
}