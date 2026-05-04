import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
	java
	id("org.springframework.boot") version "4.0.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "pl.krystianbeduch"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(26)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-h2console")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.mapstruct:mapstruct:1.6.3")

	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.h2database:h2")
	annotationProcessor("org.projectlombok:lombok")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testCompileOnly("org.projectlombok:lombok")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.register<BootRun>("bootRunDev") {
    group = "application"
    description = "Runs the application with dev profile"
    mainClass.set("pl.krystianbeduch.gymmembership.GymMembershipApplication")
    classpath = sourceSets.main.get().runtimeClasspath
    systemProperty("spring.profiles.active", "dev")
}