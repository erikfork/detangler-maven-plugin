# detangler-gradle-plugin
Simple plugin to generate [detangler](https://github.com/SeanShubin/detangler) reports with gradle.

Usage:

In `settings.gradle` add a repository that has the plugin:

    pluginManagement {
        repositories {
            maven {
                url="https://some.maven.repo/repository/snapshots"
            }
        }
    }

In `build.gradle` add the plugin coordinates:

    plugins {
        id "com.impact.gradle.detangler" version "1.0-SNAPSHOT"
    }

Also add the parameters. The `basePackages` parameter is a list of prefixes to the classes 
you want to include in the report. The rest of the parameters are optional:

    detangler.basePackages=["com.myorg"]
    detangler.allowedInCycle=[ ]
    detangler.includeTests=true
    detangler.level=2

Now you should be able to generate a report using:

    gradle detangler

To see the effective configuration, set the `detangler.debug` system property:

    gradle -Ddetangler.debug=true detangler
