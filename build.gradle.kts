fun getVersionDetails(): com.palantir.gradle.gitversion.VersionDetails =
    (extra["versionDetails"] as groovy.lang.Closure<*>)() as com.palantir.gradle.gitversion.VersionDetails

val gitInfo = getVersionDetails()
version = gitInfo.version

repositories {
    mavenCentral()
}

plugins {
    id("java")
    kotlin("jvm") version "1.5.0"
    id("org.jetbrains.intellij") version "1.1.2"
    id("com.palantir.git-version") version "0.12.3"
}

dependencies {
    implementation(kotlin("reflect"))
}

intellij {
    version.set(properties["idea-version"] as String)
    pluginName.set( "Ansible Vault Integration")
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

    patchPluginXml {
        setVersion(project.version)
    }

    publishPlugin {
        dependsOn("patchPluginXml")
        token.set(System.getenv("JB_TOKEN"))
    }
}
