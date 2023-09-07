package com.impact.gradle.detangler.plugin

import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.api.plugins.*


class DetanglerPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        project.pluginManager.apply(ApplicationPlugin)
        project.pluginManager.apply(JavaPlugin)

        project.configurations.create("detanglerConfig")
        project.dependencies.add("detanglerConfig", "com.seanshubin.detangler:detangler-console:0.9.4")

        DetanglerPluginExtension spec = project.extensions.create('detangler', DetanglerPluginExtension)

        project.tasks.create("detangler", JavaExec) {
            mainClass 'com.seanshubin.detangler.console.ConsoleApplication'
            classpath project.configurations.detanglerConfig
            dependsOn 'compileJava'
            dependsOn 'compileTestJava'

            File allowedFile
            File argsFile

            doFirst {
                def startsWith = spec.basePackages.collect {"[" + it.replace('.', ' ') + "]"}.join(" ")

                allowedFile = File.createTempFile("detangler-allowed-in-cycle", ".txt")
                allowedFile.write(spec.allowedInCycle.collect {"[" + it.replace('.', ' ') + "]"}.join(" "))

                String classesDir = project.sourceSets.main.output.classesDirs.join(" ")
                String testClassesDir = project.sourceSets.test.output.classesDirs.join(" ")

                argsFile = File.createTempFile("detangler", ".txt")
                argsFile.write(String.join(
                        "\n",
                        "{",
                        "  reportDir build/report/detangler",
                        "  searchPaths [ " + classesDir + " " + testClassesDir + " ]",
                        "  level 2",
                        "  startsWith {",
                        "    include [" + startsWith + "]",
                        "    drop [" + startsWith + "]",
                        "    exclude []",
                        "  }",
                        "  ignoreFiles []",
                        "  canFailBuild true",
                        "  ignoreJavadoc true",
                        "  logTiming false",
                        "  logEffectiveConfiguration false",
                        "  allowedInCycle " + allowedFile.getAbsolutePath(),
                        "  pathsRelativeToCurrentDirectory true",
                        "  pathsRelativeToConfigurationDirectory false",
                        "}"))

                args(argsFile.path)
            }

            doLast {
                allowedFile.delete()
                argsFile.delete()
            }
        }
    }
}

