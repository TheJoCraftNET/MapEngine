plugins {
    id("io.papermc.paperweight.userdev")
}

dependencies {
    api(project(":platform-common"))
    paperweight.paperDevBundle("1.20.5-R0.1-SNAPSHOT")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}
