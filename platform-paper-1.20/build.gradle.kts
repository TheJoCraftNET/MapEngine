plugins {
    id("io.papermc.paperweight.userdev")
}

dependencies {
    api(project(":platform-common"))
    paperweight.paperDevBundle("1.20-R0.1-SNAPSHOT")
}

tasks {
    reobfJar {
        inputJar.set(jar.get().outputs.files.singleFile)
        outputJar.set(File.createTempFile("reobfOut", ".jar"))

        doLast {
            val inputFile = inputJar.get().asFile
            inputFile.delete()

            val outputFile = outputJar.get().asFile
            outputFile.copyTo(inputFile)
            outputFile.delete()
        }
    }

    assemble {
        dependsOn(reobfJar)
    }
}