package com.example.contactadmin;

import com.example.contactadmin.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping
    public Contact saveContact(@RequestBody Contact contact) {
        contact.setStatus("Unprocessed");
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

    
    @GetMapping
    public List<Contact> getContactsNotProcessed() {
        UserPrinciple principal = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Check if the user has the ADMIN role
        if ("ADMIN".equals(principal.getRole())) {
            return contactService.getContactsNotProcessed();
        }
        return null;
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }
}
