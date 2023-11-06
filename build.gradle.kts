import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.5"
	id("io.spring.dependency-management") version "1.1.3"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
}

group = "com.trading"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

fun isAppleSilicon() = System.getProperty("os.name") == "Mac OS X" && System.getProperty("os.arch") == "aarch64"

dependencies {
	if (isAppleSilicon()) {
		runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.68.Final:osx-aarch_64")
	}
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework:spring-webflux")
	implementation("com.natpryce:hamkrest:1.8.0.1")
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.squareup.okhttp3:mockwebserver:4.9.2")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("com.squareup.okhttp3:okhttp:4.9.2")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
