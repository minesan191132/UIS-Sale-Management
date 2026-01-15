plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.14"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

javafx {
    version = "21.0.4"
    modules = listOf(
        "javafx.controls",
        "javafx.fxml"
    )
}

application {
    mainClass.set("org.example.test")
}
