fun getVersionDetails(): com.palantir.gradle.gitversion.VersionDetails =
    (extra["versionDetails"] as groovy.lang.Closure<*>)() as com.palantir.gradle.gitversion.VersionDetails

val gitInfo = getVersionDetails()
version = gitInfo.version

repositories {
    mavenCentral()
}

plugins {
    id("java")
    kotlin("jvm") version "1.9.10"
    id("org.jetbrains.intellij") version "1.12.0"
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
        kotlinOptions.jvmTarget = "17"
    }

    compileJava {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
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
                "PCP-223.8617.48", // 2022.3.2
                "PCP-223.8214.51", // 2022.3.1
                "PCP-223.7571.203", // 2022.3
                "PCP-222.4459.20", // 2022.2.4
                "PCP-222.4345.23", // 2022.2.3

                // IU - IntelliJ IDEA Ultimate
                "IU-223.8617.56", // 2022.3.2
                "IU-223.8214.52", // 2022.3.1
                "IU-223.7571.182", // 2022.3
                "IU-222.4459.24", // 2022.2.4
                "IU-222.4345.14", // 2022.2.3

                // IC - IntelliJ IDEA Community Edition
                "IC-223.8617.56", // 2022.3.2
                "IC-223.8214.52", // 2022.3.1
                "IC-223.7571.182", // 2022.3
                "IC-222.4459.24", // 2022.2.4
                "IC-222.4345.14", // 2022.2.3
            )
        )
        failureLevel.set(
            listOf(
                org.jetbrains.intellij.tasks.RunPluginVerifierTask.FailureLevel.INVALID_PLUGIN
            )
        )
    }
}
