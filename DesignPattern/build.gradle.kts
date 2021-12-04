plugins {
    java
}

group = "fr.uge.poo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks {
    compileJava {
        options.compilerArgs.add("--enable-preview")
        options.release.set(17)
    }

    withType<JavaExec> {
        jvmArgs("--enable-preview")
    }

    test {
        useJUnitPlatform()
        jvmArgs("--enable-preview")
    }
}