import com.braveinnov.Reflector
import org.jetbrains.kotlin.cli.jvm.main

plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.6.20-1.0.5"
    id("org.springframework.boot").version("2.7.8")
    application
}

configurations {
    create("customKotlinCompiler") {
        extendsFrom(configurations.implementation.get())
        defaultDependencies {
            val kotlinVersion = "1.6.21"
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

    val kotlinVersion = "1.6.21" // adjust the version as needed
    add("customKotlinCompiler", "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    add("customKotlinCompiler", "org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion")
    add("customKotlinCompiler", "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    add("customKotlinCompiler", "org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")
//    ksp(project(":processor"))
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

kotlin.sourceSets.main {
    kotlin.srcDirs(
//        file("$buildDir/generated/ksp/main/kotlin"),
        file("$buildDir/generated/tg/main/kotlin")
    )
}

tasks.clean {
    delete("build/generated")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        incremental = true
    }
}

tasks.named("bootJar") {
    dependsOn("compileKotlinManually")
}

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

tasks.register<JavaExec>("runScriptAndGenerateFiles"){
    main = "com.braveinnov.Reflector"
    classpath = files(sourceSets.main.get().runtimeClasspath)
    args("com.braveinnov.AppController", "definedTypes")
    outputs.upToDateWhen { false }
    outputs.dir("$buildDir/generated/tg/main/kotlin")
}