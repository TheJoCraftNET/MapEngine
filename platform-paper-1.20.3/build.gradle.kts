plugins {
    id("io.papermc.paperweight.userdev")
}

dependencies {
    api(project(":platform-common"))
    paperweight.paperDevBundle("1.20.3-R0.1-SNAPSHOT")
}

tasks.assemble {
    dependsOn(tasks.reobfJar)
}
