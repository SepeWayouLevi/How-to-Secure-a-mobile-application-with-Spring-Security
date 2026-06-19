package org.example.accessbasededonnees.architecture.controller;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import org.springframework.stereotype.Service;

import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "org.example.accessbasededonnees")
public class ControllerArchitectureTest {

    @ArchTest
    static final ArchRule controllersShouldResideTnControllerPackage =
            classes().that().haveSimpleNameContaining("Controller").should().resideInAPackage("..controller..");



    @ArchTest
    static final ArchRule authenticationControllerShouldCallServices =
            classes()
                    .that().resideInAPackage("..controller..")
                    .and().haveSimpleName("AuthenticationController")
                    .should().dependOnClassesThat()
                    .resideInAPackage(
                            "..service.."
                    );




}
