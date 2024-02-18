import com.braveinnov.Reflector
import org.jetbrains.kotlin.cli.jvm.main

plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
    id("org.springframework.boot").version("2.7.8")
    kotlin("plugin.spring") version "1.6.10"
    application
}

configurations {
    create("customKotlinCompiler") {
        extendsFrom(configurations.implementation.get())
        defaultDependencies {
            val kotlinVersion = properties["kotlinVersion"]
            add(project.dependencies.create("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion"))
            add(project.dependencies.create("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"))
        }
    }
}


dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-web:2.7.8")
    implementation("org.springframework.boot:spring-boot-starter:2.7.8")
    implementation("com.squareup:kotlinpoet:1.12.0")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.8.10")
    implementation(project(":annotation"))
    implementation(kotlin("stdlib-jdk8"))

    val kotlinVersion = properties["kotlinVersion"]
    add("customKotlinCompiler", "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    add("customKotlinCompiler", "org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion")
    add("customKotlinCompiler", "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    add("customKotlinCompiler", "org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")
    ksp(project(":processor"))
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest()
        }
    }
}

application {
    mainClass.set("com.braveinnov.AppKt")
}

/**
 * Including the generated folder in the source list.
 * This way it will be included in the final jar.
 */
kotlin.sourceSets.main {
    kotlin.srcDirs(
        file("$buildDir/generated/ksp/main/kotlin"),
        file("$buildDir/generated/tg/main/kotlin")
    )
}

tasks.clean {
    delete("build/generated")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        incremental = false //Heads up! It makes the KSP fail when true.
    }
}

/**
 * Defining the bootJar task depends on the compileKotlinManually.
 * This way the jar will be packed after the generated classes compile and will include it in the
 * final package..
 */
tasks.named("bootJar") {
    dependsOn("compileKotlinManually")
}

/**
 * Task responsible for compiling the generated files manually.
 * I tried different approaches before ending with it.
 *
 * It's working perfectly.
 */
tasks.register<JavaExec>("compileKotlinManually") {
    mainClass.set("org.jetbrains.kotlin.cli.jvm.K2JVMCompiler")
    classpath = configurations.getByName("customKotlinCompiler")

    val internalClasspath = configurations.getByName("customKotlinCompiler").asPath

    val sourceLocation = "$buildDir/generated/tg/main/kotlin"
    val outputLocation = "$buildDir/classes/kotlin/main"

    args = listOf(
        "-no-stdlib", "-no-reflect",
        "-d", outputLocation,
        "-classpath", internalClasspath,
        sourceLocation
    )
    dependsOn("runScriptAndGenerateFiles")
}

/**
 * Task responsible for calling the Reflector.
 *
 * In my final project the Reflector will be responsible for identify
 * all the classes with the @TransitionalService annotation and invoke
 * all the methods with the @TransitionalTypes. It will generate new files
 * based on the method returns.
 */
tasks.register<JavaExec>("runScriptAndGenerateFiles"){
    main = "com.braveinnov.Reflector"
    classpath = files(sourceSets.main.get().runtimeClasspath)
    args("com.braveinnov.AppController", "definedTypes")
    outputs.upToDateWhen { false }
    outputs.dir("$buildDir/generated/tg/main/kotlin")
}