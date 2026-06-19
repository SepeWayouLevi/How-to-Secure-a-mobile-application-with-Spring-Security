package org.example.accessbasededonnees.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.accessbasededonnees.model.Forms;
import org.example.accessbasededonnees.repository.FormsRepository;
import org.example.accessbasededonnees.util.SanitizeHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicePricing {
    private FormsRepository formsRepository;

    @Autowired
    public ServicePricing(FormsRepository formsRepository) {
        this.formsRepository = formsRepository;
    }

    public List<Forms> getRequestsForPricing(String email, HttpServletRequest theRequest)  {
        SanitizeHeader sanitizeHeader = new SanitizeHeader();
        sanitizeHeader.verifyHeader(email, theRequest);
        return formsRepository.formsForPricing(email);
    }



}
