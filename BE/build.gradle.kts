plugins {
    id("java")
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // Spring boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    implementation("org.apache.poi:poi:5.2.5")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Security & JWT
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Spring dev tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Thymeleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    // MSSQL JDBC driver
    implementation("org.postgresql:postgresql:42.7.3")
    
    // Flyway (Database Migration)
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    // JBcrypt
    implementation("org.mindrot:jbcrypt:0.4")

    //javaFX
    val javafxVersion = "21"
    implementation("org.openjfx:javafx-controls:$javafxVersion")
    implementation("org.openjfx:javafx-fxml:$javafxVersion")

    // Mail
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
}

tasks.test {
    useJUnitPlatform()
}

// Enable annotation processing
tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.bootRun {
    sourceResources(sourceSets["main"])
}