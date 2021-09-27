plugins {
    java
}

group = "fr.umlv"
version = "1.0-SNAPSHOT"
val jvmVersion = "17"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.0")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = jvmVersion
    targetCompatibility = jvmVersion
    options.compilerArgs.add("--enable-preview")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}