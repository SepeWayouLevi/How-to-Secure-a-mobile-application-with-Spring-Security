package org.example.accessbasededonnees.architecture.repository;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "org.example.accessbasededonnees")
public class RepositoryArchitectureTest {


    @ArchTest
    static final ArchRule repositoriesShouldBeInRepositoryPackage =
            classes()
                    .that().haveSimpleNameContaining("Repository")
                    .should().resideInAPackage("..repository..");




}
