package com.example.contactadmin;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    private final AmqpTemplate amqpTemplate;

    public ContactService(AmqpTemplate amqpTemplate)
    {
        this.amqpTemplate=amqpTemplate;


    }

    public Contact saveContact(Contact contact) {

        Contact c =contactRepository.save(contact);
        /*Map<String, String> message = new HashMap<>();
        message.put("outEmail", contact.getEmail());
        message.put("subject", contact.getSubject());
        message.put("body", contact.getMessage());

        amqpTemplate.convertAndSend("email_exchange", "email.generalized", message);
        System.out.println("Email published to RabbitMQ.");
*/
        return c;
    }


    public Contact updateContactStatus(String id, String message) {
        Optional<Contact> optionalContact = contactRepository.findById(id);

        if (optionalContact.isPresent()) {
            Contact contact = optionalContact.get();
            contact.setStatus("Processed");
            Map<String, String> m = new HashMap<>();
            m.put("outEmail", contact.getEmail());
            m.put("subject", "reply"+contact.getSubject());
            m.put("body", message);

            amqpTemplate.convertAndSend("email_exchange", "email.generalized", m);
            System.out.println("Email published to RabbitMQ.");


            return contactRepository.save(contact);
        } else {
            throw new IllegalArgumentException("Contact with ID " + id + " not found");
        }
    }


    public List<Contact> getContactsNotProcessed() {
        return contactRepository.findByStatusNot("Processed");
    }
}

