package de.hhu.ausgabenverwaltung;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packagesOf = AusgabenVerwaltungApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchUnitTests {

    @ArchTest
    static final ArchRule useOnionArchitecture = onionArchitecture()
            .domainModels("..domain..")
            .domainServices("..service..")
            .applicationServices("..service..")
            .adapter("web", "..web..")
            .adapter("database", "..database..")
            .adapter("api", "..api..");
}
