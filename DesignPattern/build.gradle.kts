plugins {
    java
}

group = "fr.uge.poo"
version = "1.0-SNAPSHOT"
val jvmVersion = "17"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = jvmVersion
    targetCompatibility = jvmVersion
    options.compilerArgs.add("--enable-preview")
    options.compilerArgs.add("-Xlint:all")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("--enable-preview")
}