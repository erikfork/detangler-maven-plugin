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

            File allowedFile = null
            File argsFile

            doFirst {
                boolean debug = Boolean.getBoolean("detangler.debug")
                if (debug) {
                    println "generating detangler report"
                }
                def startsWith = spec.basePackages.collect { "[" + it.replace('.', ' ') + "]" }.join(" ")

                String allowedFilePath
                if (spec.allowedInCycle.size() == 1 && new File(spec.allowedInCycle.get(0)).canRead()) {
                    allowedFilePath = spec.allowedInCycle.get(0)
                } else {
                    allowedFile = File.createTempFile("detangler-allowed-in-cycle", ".txt")
                    allowedFile.write(spec.allowedInCycle.collect { "[" + it.replace('.', ' ') + "]" }.join(" "))
                    allowedFilePath = allowedFile.getAbsolutePath()
                }

                if (debug) {
                    project.sourceSets.main.output.classesDirs.each { println it.getPath() + " has " + (it.isDirectory() ? it.list().length : 0) + " files" }
                    project.sourceSets.test.output.classesDirs.each { println it.getPath() + " has " + (it.isDirectory() ? it.list().length : 0) + " files" }
                }

                String classesDir = project.sourceSets.main.output.classesDirs.findAll { it.isDirectory() && it.list().length > 0 }.join(" ")
                if (spec.includeTests) {
                    classesDir += " " + project.sourceSets.test.output.classesDirs.findAll { it.isDirectory() && it.list().length > 0 }.join(" ")
                }

                String config = String.join(
                        "\n",
                        "{",
                        "  reportDir build/report/detangler",
                        "  searchPaths [ " + classesDir + " ]",
                        "  level " + spec.level,
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
                        "  allowedInCycle " + allowedFilePath,
                        "  pathsRelativeToCurrentDirectory true",
                        "  pathsRelativeToConfigurationDirectory false",
                        "}")

                argsFile = File.createTempFile("detangler", ".txt")
                argsFile.write(config)
                args(argsFile.path)

                if (debug) {
                    println "detangler config is:"
                    println config
                }
            }

            doLast {
                if (allowedFile != null) {
                    allowedFile.delete()
                }
                argsFile.delete()
            }
        }
    }
}

