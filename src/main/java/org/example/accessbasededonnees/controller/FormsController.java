package org.example.accessbasededonnees.controller;
import jakarta.servlet.http.HttpServletRequest;
import org.example.accessbasededonnees.model.Forms;
import org.example.accessbasededonnees.repository.FormsRepository;
import org.example.accessbasededonnees.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
public class FormsController {
    private final FormulaireService formService;

    private final ServiceRegulatoryAffairs serviceRegulatoryAffairs;

    private final ServicePurchase servicePurchase;

    private final FormsRepository formsRepository;

    private final ServicePricing servicePricing;



    public FormsController(FormsRepository formsRepository,
                           FormulaireService formService,
                           ServiceRegulatoryAffairs serviceRegulatoryAffairs,
                           ServicePurchase servicePurchase,
                           ServicePricing servicePricing
    ) {
        this.formsRepository = formsRepository;
        this.formService = formService;
        this.serviceRegulatoryAffairs = serviceRegulatoryAffairs;
        this.servicePurchase = servicePurchase;
        this.servicePricing = servicePricing;
    }

    // get forms by with marquage
    @GetMapping("/withMarking")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_LAW')")
    @ResponseStatus(HttpStatus.OK)
    public List<Forms> formsWithMarking(@RequestHeader("User-Email") String email, HttpServletRequest theRequest) {
        return serviceRegulatoryAffairs.getFormsWithMarking(email, theRequest);
    }

    //get forms with ref D
    @GetMapping("/WithRefD")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PURCHASE')")
    @ResponseStatus(HttpStatus.OK)
    public List<Forms> formsWithRefD(@RequestHeader("User-Email") String email,
                                     @RequestParam("typeofreference") String typeOfReference,
                                     HttpServletRequest theRequest) {
        return servicePurchase.getRequestsForPurchase(email, typeOfReference, theRequest);
    }


    @GetMapping("/Pricing")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PRICING')")
    @ResponseStatus(HttpStatus.OK)
    public List<Forms> formsWithAPrice(@RequestHeader("User-Email") String email, HttpServletRequest theRequest) {
        return servicePricing.getRequestsForPricing(email, theRequest);
    }


    @GetMapping("/userForms")
    @PreAuthorize("hasAnyRole('ROLE_GENERAL' , 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public List<Forms> findFormsByEmail(@RequestHeader("User-Email") String email, HttpServletRequest theRequest) {
        return formService.getFormsByEmail(email, theRequest);
    }


    //create
    @PostMapping("/createUserForms")
    @PreAuthorize("hasAnyRole('ROLE_GENERAL', 'ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Forms createForms(@RequestBody Forms forms) {
        return formsRepository.save(forms);
    }

    //get all
    @GetMapping("/byCountry")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN'," +
            " 'ROLE_PRICING'," +
            " 'ROLE_GENERAL', " +
            "'ROLE_LAW'," +
            " 'ROLE_PURCHASE', " +
            "'ROLE_STOCK')")
    @ResponseStatus(HttpStatus.OK)
    public List<Forms> getAllForms(@RequestHeader("User-Email") String email) {
        return formsRepository.findFormsByCountry(email);
    }

    //read one
    @GetMapping("/id/{idDemand}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Forms> getFormsById(@PathVariable Long idDemand) {
        return formsRepository.findById(idDemand).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    //Update
    @PutMapping("/{idDemand}/editRequest")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GENERAL')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Forms> updateForms(@PathVariable Long idDemand, @RequestBody Forms formsDetails) {
        return formsRepository.findById(idDemand).map(
                forms -> {
                    forms.setProductLine(formsDetails.getProductLine());
                    forms.setTypeOfReference(formsDetails.getTypeOfReference());
                    forms.setRequesterName(formsDetails.getRequesterName());
                    forms.setTypeOfArticle(formsDetails.getTypeOfArticle());
                    forms.setPricingValidation(formsDetails.getPricingValidation());
                    forms.setPurchaseValidation(formsDetails.getPurchaseValidation());
                    forms.setRegulatoryAffairsValidation(formsDetails.getRegulatoryAffairsValidation());
                    forms.setCatalogPrice(formsDetails.getCatalogPrice());
                    forms.setProductClassificationDescription(formsDetails.getProductClassificationDescription());
                    forms.setMarking(formsDetails.isMarking());
                    forms.setIdMarking(formsDetails.getIdMarking());
                    forms.setTypeOfMarking(formsDetails.getTypeOfMarking());
                    forms.setEmail(formsDetails.getEmail());
                    forms.setStatusId(formsDetails.getStatusId());
                    Forms updated = formsRepository.save(forms);
                    return ResponseEntity.ok(updated);
                }).orElse(ResponseEntity.notFound().build());
    }


    // PATCH spécifique pour validation_pricing
    @PatchMapping("/{idDemand}/pricingValidation")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GENERAL', 'ROLE_PRICING')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Forms> updatePricingValidation(@PathVariable Long idDemand, @RequestBody String pricingValidation) {
        return formsRepository.findById(idDemand).map(forms -> {
            forms.setPricingValidation(pricingValidation.replace("\"", "")); // si le body est "A", ça retire les guillemets
            Forms updated = formsRepository.save(forms);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{idDemand}/regulatoryAffairsValidation")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GENERAL', 'ROLE_LAW')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Forms> updateRegAffairsValidation(@PathVariable Long idDemand, @RequestBody String regAffValidation) {
        return formsRepository.findById(idDemand).map(forms -> {
            forms.setRegulatoryAffairsValidation(regAffValidation.replace("\"", "")); // si le body est "A", ça retire les guillemets
            Forms updated = formsRepository.save(forms);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{idDemand}/purchaseValidation")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_GENERAL' , 'ROLE_PURCHASE')")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Forms> updatePurchaseValidation(@PathVariable Long idDemand, @RequestBody String purchaseValidation) {
        return formsRepository.findById(idDemand).map(forms -> {
            forms.setPurchaseValidation(purchaseValidation.replace("\"", "")); // si le body est "A", ça retire les guillemets
            Forms updated = formsRepository.save(forms);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

}



