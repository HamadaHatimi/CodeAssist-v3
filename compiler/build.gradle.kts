plugins {
    id("java-library")
}

group = "com.tyron.code"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(project(":project"))
    implementation("com.google.guava:guava:31.0.1-android")
}

tasks.test {
    useJUnitPlatform()
}