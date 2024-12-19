package com.example.contactadmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/api/contacts")
    public Contact saveContact(@RequestBody Contact contact) {
        return contactService.saveContact(contact); // Save and return the contact
    }

    @PutMapping("/{id}/status")
    public Contact updateStatus(
            @PathVariable String id,

            @RequestBody replyRequest request
    ) {
        // You can now use `request.getMessage()` in your logic

        return contactService.updateContactStatus(id, request.getMessage());
    }


    @GetMapping("/contacts")
    public List<Contact> getContactsNotProcessed() {
        return contactService.getContactsNotProcessed();
    }
}
