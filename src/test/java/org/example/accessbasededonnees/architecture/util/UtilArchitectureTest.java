package org.example.accessbasededonnees.architecture.util;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

@AnalyzeClasses(packages = "org.example.accessbasededonnees")
public class UtilArchitectureTest {

    @ArchTest
    static final ArchRule authenticationControllerShouldCallServices =
            noClasses()
                    .that().resideInAPackage("..util..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "..repository..",
                            "..controller..",
                            "model",
                            "service");
}
