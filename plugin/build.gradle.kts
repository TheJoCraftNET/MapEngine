import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.util.stream.Stream

plugins {
    id("com.gradleup.shadow")
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

val reobfPlatforms = listOf("1.19.3", "1.19.4", "1.20", "1.20.2", "1.20.3")
val mojangPlatforms = listOf("1.20.5", "1.21.2", "1.21.6")
val allPlatforms: List<String> = reobfPlatforms + mojangPlatforms

dependencies {
    allPlatforms.forEach {
        val configuration = if (reobfPlatforms.contains(it)) "reobf" else "runtimeElements"
        runtimeOnly(project(":platform-paper-$it", configuration))
    }

    api(project(":platform-common"))
    api("org.bstats:bstats-bukkit:3.0.2")

    compileOnlyApi("com.google.code.gson:gson:2.10.1")
}

val gitHash = git("rev-parse --short HEAD")
val gitBranch = git("rev-parse --abbrev-ref HEAD")
val gitTag = git("describe --tags --abbrev=0")

fun git(git: String): Provider<String> {
    return providers.exec {
        commandLine(Stream.concat(Stream.of("git"), git.split(" ").stream()).toList())
    }.standardOutput.asText.map { it.trim() }
}

val compileTime: Temporal = ZonedDateTime.now(ZoneOffset.UTC)
val compileDate: String = DateTimeFormatter.ISO_DATE_TIME.format(compileTime)

tasks {
    shadowJar {
        destinationDirectory = rootProject.layout.buildDirectory.map { it.dir("libs") }
        archiveBaseName = rootProject.name

        relocate("org.bstats", "de.pianoman911.mapengine.bstats")
    }

    assemble {
        dependsOn(shadowJar)
    }

    jar {
        manifest.attributes(
            "Implementation-Title" to rootProject.name,
            "Implementation-Vendor" to "pianoman911",
            "Implementation-Version" to project.version,
            "License" to "AGPL-3.0",

            "Build-Date" to compileDate,
            "Build-Timestamp" to compileTime.toString(),
            "Platforms" to allPlatforms.joinToString(", "),

            "Git-Commit" to gitHash.get(),
            "Git-Branch" to gitBranch.get(),
            "Git-Tag" to gitTag.get(),

            // starting with 1.20.5, paper runtime jars are only provided with mojang mappings
            // mapengine uses mojang mappings and disables reobfuscation for 1.20.5+
            "paperweight-mappings-namespace" to "mojang",
        )
    }

    runServer {
        runDirectory = rootProject.layout.projectDirectory.dir("run")
        minecraftVersion("1.21.6")
    }
}

bukkit {
    main = "$group.mapengine.core.MapEnginePlugin"
    apiVersion = "1.19"
    authors = listOf("pianoman911")

    name = rootProject.name
    description = "${gitHash.get()}/${gitBranch.get()} (${gitTag.get()}), $compileDate"
}
