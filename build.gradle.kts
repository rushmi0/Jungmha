import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.2.0"
    id("io.micronaut.test-resources") version "4.2.0"
    id("io.micronaut.aot") version "4.2.0"
    id("nu.studer.jooq") version "8.2"
    id("com.google.devtools.ksp") version "1.9.21-1.0.16"
}

version = "0.1"
group = "org.jungmha"

val kotlinVersion = project.properties["kotlinVersion"]

repositories {
    mavenCentral()
}

dependencies {

    compileOnly("io.micronaut:micronaut-http-client")
    compileOnly("io.micronaut.openapi:micronaut-openapi-annotations")

    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("org.postgresql:postgresql")

    jooqGenerator("org.postgresql:postgresql:42.5.1")

    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.openapi:micronaut-openapi:6.4.0")
    ksp("io.micronaut.serde:micronaut-serde-processor")

    annotationProcessor("io.micronaut.openapi:micronaut-openapi:2.1.1")

    testImplementation("io.micronaut:micronaut-http-client")

    implementation("io.swagger.core.v3:swagger-annotations")
    implementation("io.micronaut:micronaut-websocket")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.sql:micronaut-jooq")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    // https://mvnrepository.com/artifact/io.reactivex.rxjava2/rxjava
    //implementation("io.reactivex.rxjava2:rxjava:2.2.21")

    // https://mvnrepository.com/artifact/io.micronaut.rxjava2/micronaut-rxjava2
    implementation("io.micronaut.rxjava2:micronaut-rxjava2:2.2.1")


    // https://mvnrepository.com/artifact/com.google.guava/guava
    implementation("com.google.guava:guava:32.1.2-jre")

}


java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}

tasks {

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

}

kotlin {

    sourceSets.all {
        languageSettings {
            version = 2.0
        }
    }

}

application {
    mainClass.set("org.jungmha.ApplicationKt")
}


graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("org.jungmha.*")
    }
    testResources {
        additionalModules.add("jdbc-postgresql")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading.set(false)
        convertYamlToJava.set(false)
        precomputeOperations.set(true)
        cacheEnvironment.set(true)
        optimizeClassLoading.set(true)
        deduceEnvironment.set(true)
        optimizeNetty.set(true)
    }
}


jooq {
    version.set("3.18.7")  // default (can be omitted)
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)  // default (can be omitted)

    configurations {
        create("main") {  // name of the jOOQ configuration
            generateSchemaSourceOnCompilation.set(true)  // default (can be omitted)

            jooqConfiguration.apply {
                logging = Logging.WARN
                //logging = Logging.DEBUG
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    //url = "jdbc:postgresql://localhost:5432/postgres"
                    url = "jdbc:postgresql://jungmha-postgres:5432/postgres"
                    user = "postgres"
                    password = "sql@min"
                    properties.add(Property().apply {
                        key = "ssl"
                        value = "false"
                    })
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "org.jungmha.infra.database"
                        directory = "target/infra/jooq/main"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

