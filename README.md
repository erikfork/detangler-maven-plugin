# detangler-gradle-plugin
Simple plugin to generate [detangler](https://github.com/SeanShubin/detangler) reports with gradle.

Usage:

In `settings.gradle` add a repository that has the plugin:

    pluginManagement {
        repositories {
            maven { url "https://plugins.gradle.org/m2/" }
            maven { url "https://ir-nexus.impactradius.net/repository/snapshots" }
        }
    }


In `build.gradle` add the plugin coordinates:

    plugins {
        id "com.impact.gradle.detangler" version "1.0-SNAPSHOT"
    }

Also add the parameters.

- `basePackages` (required) a list of prefixes to the classes 
you want to include in the report.
- `allowedInCycle` (default `[]`) a list of elements that are expected to be
in a cycle and should not cause a build failure. (TODO: The syntax is a bit weird,
so make it more standard json)
- `includeTest` (default `true`) controls whether test classes are included in
the analysis/report
- `level` (default `2`) passed directly through -- see docs in "detangler" project

Example:

    detangler.basePackages=["com.myorg"]
    detangler.allowedInCycle=[ ]
    detangler.includeTests=true
    detangler.level=2

Now you should be able to generate a report using:

    gradle detangler

To see the effective configuration, set the `detangler.debug` system property:

    gradle -Ddetangler.debug=true detangler

### Kotlin

When using the Kotlin DSL, an example `settings.gradle.kts` could looks like:

    pluginManagement {
        repositories {
            gradlePluginPortal()
            maven("https://ir-nexus.impactradius.net/repository/snapshots")
        }
    }

and an example `build.gradle.kts` could look like:

    plugins {
        id("com.impact.gradle.detangler") version "1.0-SNAPSHOT"
    }

    detangler.basePackages=listOf("estalea","impact","com.impact")

