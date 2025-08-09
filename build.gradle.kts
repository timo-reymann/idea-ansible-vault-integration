import org.jetbrains.intellij.platform.gradle.TestFrameworkType

fun getVersionDetails(): com.palantir.gradle.gitversion.VersionDetails =
    (extra["versionDetails"] as groovy.lang.Closure<*>)() as com.palantir.gradle.gitversion.VersionDetails

val gitInfo = getVersionDetails()
version = gitInfo.version

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

plugins {
    id("java")
    kotlin("jvm") version "2.2.0"
    id("org.jetbrains.intellij.platform") version "2.7.1"
    id("com.palantir.git-version") version "4.0.0"
    id("com.adarshr.test-logger") version "4.0.0"
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation("junit", "junit", "4.13.2")
    implementation("org.ini4j", "ini4j", "0.5.4")

    intellijPlatform {
        intellijIdeaUltimate(providers.gradleProperty("idea-version"))
        pluginVerifier()
        zipSigner()
        instrumentationTools()
        bundledPlugins(
            listOf(
                "org.jetbrains.plugins.yaml",
            )
        )
        testFramework(TestFrameworkType.Platform)
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "Ansible Vault Integration"

        ideaVersion {
            untilBuild = provider { null }
        }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }

    publishing {
        token = System.getenv("JB_TOKEN")
    }
}

kotlin {
    jvmToolchain(17)
}

tasks {
    test {
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }

        useJUnit()

        // Prevent "File access outside allowed roots" in multi module tests, because modules each have an .iml
        environment("NO_FS_ROOTS_ACCESS_CHECK", "1")
    }
}
