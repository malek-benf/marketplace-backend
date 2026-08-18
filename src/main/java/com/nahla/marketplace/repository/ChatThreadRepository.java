package com.nahla.marketplace.repository;
import com.nahla.marketplace.model.ChatThread;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ChatThreadRepository extends MongoRepository<ChatThread, String> {
    List<ChatThread> findByParticipantIdsContainingOrderByLastMessageAtDesc(String userId);
    
    @Query("{ 'participantIds': { $all: [?0, ?1] }, 'listingId': ?2 }")
    Optional<ChatThread> findByParticipantsAndListing(String user1, String user2, String listingId);
}