package de.hhu.ausgabenverwaltung;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

@AnalyzeClasses(packagesOf = AusgabenVerwaltungApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchUnitTests {


    // No Field should be injected with @Autowired, to keep up the Testability
    @ArchTest
    static final ArchRule noMembersShouldBeAutowired =
            GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION;

    // No Class should throw just an Exception, better throw something like NoSuchElementException
    @ArchTest
    static final ArchRule noGenericException =
            GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    // No Class should use something like System.out..
    @ArchTest
    static final ArchRule noAccessToStandardStreams =
            GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    // In modern projects it is common to use Log4j or Logback
    @ArchTest
    static final ArchRule noJavaUtilLogging =
            GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    // In modern projects it is common to use Java.time
    @ArchTest
    static final ArchRule noJodaTime = GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME;


    // Onion architecture preset
    // ----------------------------------------------------------------------------------------------
    @ArchTest
    static final ArchRule useOnionArchitecture =
            onionArchitecture()
                    .domainModels("..domain..")
                    .domainServices("..application..")
                    .applicationServices("..application.service..")
                    .adapter("controller", "..adapters.controller.web..","..adapters.controller.api..")
                    .adapter("database", "..adapters.database..");

    // Custom rules
    // ----------------------------------------------------------------------------------------------

    @ArchTest
    static final ArchRule daosMustResideInDataaccessPackage =
            classes().that().haveNameMatching(".*Dao").should().resideInAPackage("..dao..")
                    .as("DataAccessObjects sollten sich in einem Paket „..dataaccess.'");


    @ArchTest
    static final ArchRule interfacesShouldNotHaveSimpleClassNamesContainingTheWordInterface =
            noClasses().that().areInterfaces().should().haveSimpleNameContaining("Interface")
                    .as("Interface Namen sollten nicht den String Interface haben");


    @ArchTest
    static final ArchRule fieldsShouldBePrivate =
            fields()
                    .that()
                    .areDeclaredInClassesThat()
                    .areAnnotatedWith(Controller.class)
                    .should()
                    .bePrivate()
                    .as("Controller-Felder sollen für kein anderes Objekt zugänglich sein");


    @ArchTest
    static final ArchRule serviceClassAnnotation =
            classes()
                    .that()
                    .areTopLevelClasses()
                    .and()
                    .areNotInterfaces()
                    .and()
                    .resideInAPackage("..application.service..")
                    .should()
                    .beMetaAnnotatedWith(Service.class)
                    .as("Services müssen mit @Service annotiert werden um in den Kontext geladen zu werden");


    @ArchTest
    static final ArchRule serviceClassSuffix =
            classes()
                    .that().resideInAPackage("..application.service..")
                    .and().areAnnotatedWith(Service.class)
                    .should().haveSimpleNameEndingWith("Service")
                    .as("Das Ende des Namens einer Service Klasse  sollte Service sein");


    @ArchTest
    static final ArchRule controllerClassAnnotation =
            classes()
                    .that().haveSimpleNameEndingWith("Controller")
                    .and().areAnnotatedWith(Controller.class)
                    .and().resideInAnyPackage("..adapters..")
                    .should().beMetaAnnotatedWith(Controller.class);


    @ArchTest
    static final ArchRule controllerClassSuffix =
            classes()
                    .that().resideInAPackage("..adapters..")
                    .and().areAnnotatedWith(Controller.class)
                    .should().haveSimpleNameEndingWith("Controller")
                    .as("Das Ende des Names einer Controller Klasse sollte Controller sein");


    @ArchTest
    static final ArchRule configurationClassesAnnotation =
            classes()
                    .that()
                    .areTopLevelClasses()
                    .and()
                    .areNotInterfaces()
                    .and()
                    .resideInAPackage("..config..")
                    .should()
                    .beMetaAnnotatedWith(Configuration.class);

    @ArchTest
    static final ArchRule configurationClassSuffix =
            classes()
                    .that().resideInAPackage("..config..")
                    .and().areAnnotatedWith(Configuration.class)
                    .should().haveSimpleNameEndingWith("Configuration")
                    .as("Das Ende des Namens einer Konfigurationsklasse sollte Configuration sein");


    @ArchTest
    static final ArchRule shouldBeInterfaces =
        classes()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..application.repo..")
            .should()
            .beInterfaces();

    @ArchTest
    static final ArchRule implementingInterfaces =
        classes()
            .that()
            .areTopLevelClasses()
            .and()
            .resideInAPackage("..adapters.database.implementation")
            .should()
            .implement(JavaClass.Predicates.resideInAPackage("..application.repo.."));
}
