plugins {
    java
    idea
    id("org.springframework.boot") version "3.2.4"
}

group = "org.ian.godman"
version = "0.1.0"

val springBootVersion = "3.2.4"
val lombokVersion = "1.18.30"
val mapStructVersion = "1.5.5.Final"
val jakartaVersion = "6.0.0"
val cucumberVersion = "7.16.1"

val springProfile: String = findProperty("springProfile") as? String ?: "jpa"
var buildImageBeforeTest: Boolean = findProperty("buildImageBeforeTest") as? String == "true"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.bootJar {
    archiveFileName = "coding.example.jar"
    mainClass = "coding.example.Application"
}

repositories {
    mavenCentral()
}

fun DependencyHandler.springBoot(module: String) =
    implementation("org.springframework.boot:spring-boot-starter-$module:$springBootVersion")

dependencies {
    implementation(project(":database"))
    implementation(project(":app-config"))

    compileOnly("jakarta.servlet:jakarta.servlet-api:$jakartaVersion")
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapStructVersion")

    springBoot("web")
    springBoot("jdbc")
    springBoot("thymeleaf")
    springBoot("log4j2")
    springBoot("security")
    springBoot("webflux")
    springBoot("data-jpa")
    springBoot("actuator")
    implementation("org.liquibase:liquibase-core:4.27.0")
    implementation("com.h2database:h2:2.2.224")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")

    implementation("io.cucumber:cucumber-junit:$cucumberVersion")
    implementation("io.cucumber:cucumber-java:$cucumberVersion")
    implementation("commons-codec:commons-codec:1.16.1")

    implementation("org.mapstruct:mapstruct:$mapStructVersion")
    implementation("org.mapstruct:mapstruct-processor:$mapStructVersion")
    implementation("org.imgscalr:imgscalr-lib:4.2")
    implementation("org.apache.tika:tika-core:2.9.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.10.2")

    testImplementation("org.junit.platform:junit-platform-launcher:1.10.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("com.codeborne:selenide:7.2.3")
    testImplementation("io.github.bonigarcia:webdrivermanager:5.7.0")
    testImplementation("org.springframework.security:spring-security-test:6.2.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.testcontainers:testcontainers:1.19.6")
    testImplementation("org.testcontainers:junit-jupiter:1.19.6")
    testImplementation("io.github.bonigarcia:webdrivermanager:5.6.4")

    configurations {
        all {
            exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("buildDockerImage") {
    if (buildImageBeforeTest) {
        println("Building docker image ...")
        dependsOn(tasks.named("bootJar"))
        doFirst {
            exec {
                commandLine("docker", "build", "-t", "coding.example", ".")
            }

        }
    }
    else{
        println("Not building docker image")
    }
}

tasks.named("test") {
    dependsOn("buildDockerImage")
}

