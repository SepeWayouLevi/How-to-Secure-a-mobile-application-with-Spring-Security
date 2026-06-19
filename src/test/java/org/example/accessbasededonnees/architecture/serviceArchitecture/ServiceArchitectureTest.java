package org.example.accessbasededonnees.architecture.serviceArchitecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "org.example.accessbasededonnees")
public class ServiceArchitectureTest {
    @ArchTest
    static final ArchRule service_should_reside_in_service_package =
            classes().that().areAnnotatedWith(Service.class).should().resideInAPackage("..service..");


    @ArchTest
    static final ArchRule service_should_be_annotated_with_service_annotation =
            classes().that().resideInAPackage("..service..").should().beAnnotatedWith(Service.class);

}
