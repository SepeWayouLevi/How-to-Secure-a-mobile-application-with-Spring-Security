package org.example.accessbasededonnees.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.accessbasededonnees.model.Forms;
import org.example.accessbasededonnees.repository.FormsRepository;
import org.example.accessbasededonnees.util.SanitizeHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ServicePurchase {
    private FormsRepository formsRepository;

    @Autowired
    public ServicePurchase(FormsRepository formsRepository) {
        this.formsRepository = formsRepository;
    }
    public List<Forms> getRequestsForPurchase(String email, String typeOfReference, HttpServletRequest theRequest) {
        SanitizeHeader sanitizeHeader = new SanitizeHeader();
        sanitizeHeader.sanitizeHeader(email, theRequest);
        if(!"D".equals(typeOfReference)){
            throw new IllegalArgumentException("only reference type D is allowed here") ;
        }
        return formsRepository.findUserRequestsForPurchase(email, typeOfReference);
    }

}
