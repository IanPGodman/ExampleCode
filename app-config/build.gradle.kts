plugins {
    id("java")
    id("idea")
}

group = "org.ian.godman"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    val springVersion = "3.2.4"
    val lombokVersion = "1.18.30"

    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

    implementation( "org.springframework.boot:spring-boot-starter-web:$springVersion")
    implementation( "org.springframework.boot:spring-boot-starter-security:$springVersion")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}