import org.gradle.internal.extensions.core.extra

plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.11.3"
    id("org.asciidoctor.jvm.convert") version "4.0.5"
    kotlin("plugin.noarg") version "2.3.0"
}


group = "com.github.jingshouyan"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    all {
        exclude("org.springframework.boot", "spring-boot-starter-logging")
    }
}

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")
extra["nettyVersion"] = "4.2.10.Final"

dependencyManagement {
    imports {
        mavenBom("io.netty:netty-bom:${project.extra["nettyVersion"]}")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.baomidou:mybatis-plus-spring-boot4-starter:3.5.15")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")
    implementation("com.corundumstudio.socketio:netty-socketio:2.0.13")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework:spring-context-support")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("com.lmax:disruptor:4.0.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("io.micrometer:micrometer-registry-otlp")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-restdocs")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")

    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

allOpen {
    annotation("com.baomidou.mybatisplus.annotation.TableName")
}

noArg {
    annotation("com.baomidou.mybatisplus.annotation.TableName")
}

tasks.withType<Test> {
    useJUnitPlatform()
}



tasks.withType<JavaCompile> {
    doFirst {
        println(
            "AnnotationProcessorPath for '$name' is ${
                options.annotationProcessorPath?.joinToString(
                    prefix = "\n",
                    separator = "\n",
                    transform = { it.toString() })
            }"
        )
    }
}

tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}
