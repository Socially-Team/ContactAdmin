package com.example.contactadmin;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ContactRepository extends MongoRepository<Contact, String> {
    // Additional query methods can be defined here, if needed
    List<Contact> findByStatusNot(String status);
}
