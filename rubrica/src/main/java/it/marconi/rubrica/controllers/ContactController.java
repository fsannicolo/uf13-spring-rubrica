package it.marconi.rubrica.controllers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.marconi.rubrica.domain.Contact;
import it.marconi.rubrica.domain.ContactForm;
import it.marconi.rubrica.services.ContactService;
import jakarta.validation.Valid;

@Controller
public class ContactController {
    
    // dependency injection
    @Autowired
    private ContactService contactService;

    @GetMapping
    public ModelAndView showContactList() {
        // passo alla webpage la lista dei contatti letta dal db
        return new ModelAndView("contact-list")
            .addObject("contacts", contactService.findAll());
    }

    // endpoint per la richiesta GET, mi deve mostrare il form di inserimento
    @GetMapping("/new")
    public ModelAndView newContactForm() {
        // passo una istanza vuota del form alla pagina web          // contactForm
        return new ModelAndView("contact-form").addObject(new ContactForm());
    }

    // endpoint per la richiesta POST, deve salvare il contatto nel DB
    @PostMapping("/new") 
    public ModelAndView handleNewContact(
        @ModelAttribute @Valid ContactForm contactForm,
        BindingResult br,        // esito della validazione (subito dopo parametro da validare)
        RedirectAttributes attr
    ) {

        // controllo l'esito della validazione
        if (br.hasErrors())
            return new ModelAndView("contact-form");

        Contact c = contactService.save(contactForm);

        // aggiungo un parametro speciale che sopravviva al redirect
        attr.addFlashAttribute("newContact", true);

        return new ModelAndView("redirect:/contact?id=" + c.getId()); 
    }

    // pattern PRG
    @GetMapping(path = "contact", params = "id")
    public ModelAndView showContact(@RequestParam("id") UUID contactId) {

        Optional<Contact> opContact = contactService.get(contactId);

        // controllo se il dato è presente
        if (opContact.isPresent()) {
            return new ModelAndView("contact-detail")
                .addObject("contact", opContact.get());
        }
        else 
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contatto non trovato");
    }

    @GetMapping("contact/delete/{id}")
    public ModelAndView deleteContact(
        @PathVariable("id") UUID contactId,
        RedirectAttributes attr
    ) {

        contactService.deleteById(contactId);

        // attributo per feedback eliminazione
        attr.addFlashAttribute("deleted", true);
        return new ModelAndView("redirect:/");
    }
}
