fun getVersionDetails(): com.palantir.gradle.gitversion.VersionDetails =
    (extra["versionDetails"] as groovy.lang.Closure<*>)() as com.palantir.gradle.gitversion.VersionDetails

val gitInfo = getVersionDetails()
version = gitInfo.version

repositories {
    mavenCentral()
}

plugins {
    id("java")
    kotlin("jvm") version "1.5.30"
    id("org.jetbrains.intellij") version "1.1.6"
    id("com.palantir.git-version") version "0.12.3"
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
}
