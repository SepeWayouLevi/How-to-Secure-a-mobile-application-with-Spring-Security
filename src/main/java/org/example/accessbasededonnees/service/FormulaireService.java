package org.example.accessbasededonnees.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.accessbasededonnees.model.Forms;
import org.example.accessbasededonnees.repository.FormsRepository;
import org.example.accessbasededonnees.util.SanitizeHeader;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormulaireService {
    private final FormsRepository formsRepository;

    public List<Forms> getFormsByEmail(String email, HttpServletRequest theRequest)  {
        SanitizeHeader sanitizeHeader = new SanitizeHeader() ;
        sanitizeHeader.verifyHeader(email, theRequest);
        return formsRepository.findFormsByEmail(email);
    }

}
