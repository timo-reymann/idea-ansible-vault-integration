fun getVersionDetails(): com.palantir.gradle.gitversion.VersionDetails =
    (extra["versionDetails"] as groovy.lang.Closure<*>)() as com.palantir.gradle.gitversion.VersionDetails

val gitInfo = getVersionDetails()
version = gitInfo.version

repositories {
    mavenCentral()
}

plugins {
    id("java")
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.intellij") version "1.7.0"
    id("com.palantir.git-version") version "0.15.0"
    id("com.adarshr.test-logger") version "3.0.0"
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation("junit", "junit", "4.12")
    implementation("org.ini4j", "ini4j", "0.5.4")
}

intellij {
    version.set(properties["idea-version"] as String)
    pluginName.set("Ansible Vault Integration")
    updateSinceUntilBuild.set(false)
    downloadSources.set(true)
    plugins.set(
        listOf("yaml")
    )
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    compileJava {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }

    test {
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }

        useJUnit()

        // Prevent "File access outside allowed roots" in multi module tests, because modules each have an .iml
        environment("NO_FS_ROOTS_ACCESS_CHECK", "1")
    }

    patchPluginXml {
        setVersion(project.version)
    }

    publishPlugin {
        dependsOn("patchPluginXml")
        token.set(System.getenv("JB_TOKEN"))
    }

    runPluginVerifier {
        ideVersions.set(
            // Top 3 used IDEs with latest 5 versions
            // Generated with https://github.com/timo-reymann/script-shelve/blob/master/jetbrains/query_ide_versions_for_verifier.py
            listOf(
                // PY - PyCharm Professional
                "PCP-212.5080.64", // 2021.2.1
                "PCP-212.4746.96", // 2021.2
                "PCP-211.7628.24", // 2021.1.3
                "PCP-211.7442.45", // 2021.1.2
                "PCP-211.7142.13", // 2021.1.1

                // IU - IntelliJ IDEA Ultimate
                "IU-212.5080.55", // 2021.2.1
                "IU-212.4746.92", // 2021.2
                "IU-211.7628.21", // 2021.1.3
                "IU-211.7442.40", // 2021.1.2
                "IU-211.7142.45", // 2021.1.1

                // IC - IntelliJ IDEA Community Edition
                "IC-212.5080.55", // 2021.2.1
                "IC-212.4746.92", // 2021.2
                "IC-211.7628.21", // 2021.1.3
                "IC-211.7442.40", // 2021.1.2
                "IC-211.7142.45" // 2021.1.1
            )
        )
        failureLevel.set(
            listOf(
                org.jetbrains.intellij.tasks.RunPluginVerifierTask.FailureLevel.INVALID_PLUGIN
            )
        )
    }
}
