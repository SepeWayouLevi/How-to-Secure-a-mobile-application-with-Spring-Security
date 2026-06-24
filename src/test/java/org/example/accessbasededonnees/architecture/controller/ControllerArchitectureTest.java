package org.example.accessbasededonnees.architecture.controller;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
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
