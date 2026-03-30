package it.marconi.rubrica.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.marconi.rubrica.domain.Contact;
import it.marconi.rubrica.domain.ContactForm;
import it.marconi.rubrica.repositories.ContactRepository;

// un service si interfaccia tra Controller e Repository
// sceglie come e quali dati leggere, salvare, eliminare...
@Service
public class ContactService {
    
    // dependency injection
    @Autowired
    private ContactRepository contactRepo;

    public Contact save(ContactForm contactForm) {

        Contact c = mapContact(contactForm);
        return contactRepo.save(c);
    }

    private Contact mapContact(ContactForm form) {

        Contact c = new Contact();
        c.setName(form.getName());
        c.setSurname(form.getSurname());
        c.setPhone(form.getPhone());
        c.setEmail(form.getEmail());
        
        return c;
    }

    public List<Contact> findAll() {
        return contactRepo.findAll();
    }

    public Optional<Contact> get(UUID id) {
        return contactRepo.findById(id);
    }

    public void deleteById(UUID id) {
        contactRepo.deleteById(id);
    }
}
