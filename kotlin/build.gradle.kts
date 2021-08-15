plugins {
    kotlin("jvm") version "1.3.70"
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.junit.jupiter:junit-jupiter:5.7.0")
    implementation("org.testng:testng:7.1.0")
    implementation("junit:junit:4.13.2")
}