plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jetbrains.compose") version "1.5.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.material:material-icons-extended:1.5.0")
    implementation("org.xerial:sqlite-jdbc:3.45.2.0")
    implementation("com.github.librepdf:openpdf:1.3.30")
    
    // Ensure SQLite native libraries are included
    runtimeOnly("org.xerial:sqlite-jdbc:3.45.2.0")
}

tasks.test {
    useJUnitPlatform()
}
compose.desktop {
    application {
        mainClass = "org.example.MainKt"
        
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Exe
            )
            packageName = "LT POS"
            packageVersion = "1.0.0"
            description = "Lakshmi Multiplex Theatre Canteen Billing Application"
            vendor = "Lakshmi Multiplex"
            
            // Include all runtime dependencies
            includeAllModules = true
            
            modules(
                "java.base",
                "java.desktop",
                "java.sql",
                "java.naming"
            )
            
            windows {
                console = false
                dirChooser = true
                perUserInstall = false
                menuGroup = "Lakshmi Multiplex"
                upgradeUuid = "61DAB35E-17CB-43B8-A18C-05CFCF57B6B6"
                iconFile.set(project.file("src/main/resources/Logo.ico"))
            }
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
kotlin {
    jvmToolchain(17)
}