package org.example.accessbasededonnees.architecture.model;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import jakarta.persistence.Entity;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "org.example.accessbasededonnees")
public class ModelArchitectureTest {


    @ArchTest
    static final ArchRule entitiesMustResideInModelPackage =
            classes().that().areAnnotatedWith(Entity.class).should().resideInAPackage("..model..")
                    .as("Entities should reside in a package '..model..'");
}
