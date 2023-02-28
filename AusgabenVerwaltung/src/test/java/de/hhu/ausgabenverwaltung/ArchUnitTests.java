package de.hhu.ausgabenverwaltung;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

@AnalyzeClasses(packagesOf = AusgabenVerwaltungApplication.class)
public class ArchUnitTests {

    @ArchTest
    static final ArchRule useOnionArchitecture = onionArchitecture()
            .domainModels("..domain..")
            .domainServices("..service..")
            .applicationServices("..service..")
            .adapter("web", "..web..");
}
