plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

fun getReobfJarFile(project: Project): RegularFile {
    return project.layout.buildDirectory.dir("libs").get()
        .file("${project.name}-${project.version}.jar")
}

dependencies {
    runtimeOnly(project(":platform-paper-1.19.3"))
    runtimeOnly(project(":platform-paper-1.19.4"))

    api(project(":platform-common"))
    api("org.bstats:bstats-bukkit:3.0.2")
}

tasks {
    shadowJar {
        destinationDirectory.set(rootProject.buildDir.resolve("libs"))
        archiveBaseName.set(rootProject.name)

        relocate("org.bstats", "de.pianoman911.mapengine.bstats")
    }

    assemble {
        dependsOn(shadowJar)
    }
}

bukkit {
    main = "$group.mapengine.core.MapEnginePlugin"
    apiVersion = "1.19"
    authors = listOf("pianoman911")
    name = rootProject.name
}
