plugins {
    id("java-library")
    id("maven-publish")

    id("io.papermc.paperweight.userdev") version "1.5.4" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

tasks["jar"].enabled = false

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    group = "de.pianoman911"
    version = "1.4.4"

    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.compilerArgs.add("-Xlint:deprecation")
    }

    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }

    java {
        withSourcesJar()
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

subprojects {
    publishing {
        publications.create<MavenPublication>("maven${project.name}") {
            artifactId = "${rootProject.name}-${project.name}".lowercase()
            from(components["java"])
        }
        repositories.maven("https://repo.thejocraft.net/releases/") {
            name = "tjcserver"
            authentication { create<BasicAuthentication>("basic") }
            credentials(PasswordCredentials::class)
        }
    }
}
