import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.*

plugins {
	id("org.springframework.boot") version "2.7.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("com.google.protobuf") version "0.8.15"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	idea
}

group = "me.sssaang"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

val protobufVersion = "3.15.6"
val grpcVersion = "1.36.0"
val grpcKotlinVersion = "1.0.0"

dependencies {
	// spring init default
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("io.github.lognet:grpc-spring-boot-starter:4.0.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")

	// grpc
	implementation("io.grpc:grpc-kotlin-stub:$grpcKotlinVersion")
	implementation("io.grpc:grpc-protobuf:${grpcVersion}")
	implementation("io.grpc:grpc-stub:1.40.1")
	compileOnly("jakarta.annotation:jakarta.annotation-api:1.3.5") // Java 9+ compatibility - Do NOT update to 2.0.0
	implementation("com.google.protobuf:protobuf-java:$protobufVersion")

	// test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:$protobufVersion"
	}
	plugins {
		id("grpc") {
			artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
		}
		id("grpckt") {
			artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion:jdk7@jar"
		}
	}
	generateProtoTasks {
		all().forEach { generateProtoTask ->
			generateProtoTask.plugins {
				id("grpc")
				id("grpckt")
			}
		}
	}
}

sourceSets{
	getByName("main"){
		java {
			srcDirs(
				"build/generated/source/proto/main/java",
				"build/generated/source/proto/main/kotlin"
			)
		}
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

