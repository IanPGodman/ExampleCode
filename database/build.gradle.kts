plugins {
    id("java")
    id("idea")
}

group = "org.ian.godman"
version = "0.1.0"

repositories {
    mavenCentral()
}

val springBootVersion = "3.2.4"
val lombokVersion = "1.18.30"
val mapStructVersion = "1.5.5.Final"

fun DependencyHandler.springBoot(module: String) =
    implementation("org.springframework.boot:spring-boot-starter-$module:$springBootVersion")

dependencies {
    implementation(project(":app-config"))

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")

    springBoot("security")
    springBoot("jdbc")
    springBoot("data-jpa")


    implementation( "org.liquibase:liquibase-core:4.27.0")
    implementation( "jakarta.transaction:jakarta.transaction-api:2.0.1")
    implementation( "jakarta.annotation:jakarta.annotation-api:2.1.1")
    implementation( "org.postgresql:postgresql:42.7.2")
    implementation( "com.zaxxer:HikariCP:5.1.0")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    implementation("org.mapstruct:mapstruct-processor:1.5.5.Final")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}