import org.apache.tools.ant.filters.ReplaceTokens
import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    alias(libs.plugins.paperweight.userdev)
    java
    `maven-publish`
}

val minecraftVersion: String by project
val paperDevBundle: String by project
val javaVersion: String by project
val pluginVersion: String by project

group = "net.azisaba"
version = "$pluginVersion+$minecraftVersion"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))

    withJavadocJar()
    withSourcesJar()
}

kotlin {
    jvmToolchain(javaVersion.toInt())
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
    // net.azisaba plugins
    maven("https://maven.azisaba.net/releases") {
        name = "azisaba"
        mavenContent { includeGroupAndSubgroups("net.azisaba") }
    }
    // xyz.acrylicstyle and other legacy artifacts
    maven("https://repo.azisaba.net/repository/maven-public/") {
        name = "azisabaLegacy"
    }
    maven("https://mvn.lumine.io/repository/maven-public/") {
        name = "lumine"
        mavenContent { includeGroup("io.lumine") }
    }
    maven("https://jitpack.io") {
        name = "jitpack"
        mavenContent { includeGroupAndSubgroups("com.github") }
    }
}

dependencies {
    paperweight.paperDevBundle(paperDevBundle)
    compileOnly(platform(libs.adventure.bom))

    implementation(kotlin("stdlib"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kaml)
    implementation(libs.hikaricp)
    implementation(libs.mariadb)
    implementation(libs.snakeyaml)
    implementation(libs.acrylicstyle.expression)

    // net.azisaba plugins
    compileOnly(libs.lifepvelevel)
    compileOnly(libs.joinfilter)
    compileOnly(libs.ballotbox.receiver)
    compileOnly(libs.azipluginmessaging.api)
    compileOnly(libs.ryuzupluginchat) {
        exclude("co.aikar", "taskchain-core")
    }
    compileOnly(libs.rarity.api) {
        exclude("org.spigotmc", "spigot-api")
    }
    compileOnly(libs.itemstash)
    compileOnly(libs.townia)

    // third-party plugins
    compileOnly(libs.mythic.dist)
    compileOnly(libs.vault.api) {
        exclude("org.bukkit", "bukkit")
    }
    compileOnly(libs.mypet.api)
    compileOnly(libs.autorank)
    compileOnly(libs.statz) {
        exclude("nl.lolmewn.stats", "Stats")
        exclude("me.staartvin", "PluginLibrary")
        exclude("org.spigotmc", "spigot-api")
        exclude("org.bukkit", "bukkit")
    }
}

// Paper runs Mojang-mapped since 1.20.5 and Minecraft 26.x is no longer obfuscated,
// so the plugin is shipped Mojang-mapped (no reobfuscation step).
paperweight.reobfArtifactConfiguration.set(ReobfArtifactConfiguration.MOJANG_PRODUCTION)

publishing {
    repositories {
        maven {
            name = "repo"
            credentials(PasswordCredentials::class)
            url = uri(
                if (project.version.toString().endsWith("SNAPSHOT"))
                    project.findProperty("deploySnapshotURL") ?: System.getProperty("deploySnapshotURL", "https://repo.azisaba.net/repository/maven-snapshots/")
                else
                    project.findProperty("deployReleasesURL") ?: System.getProperty("deployReleasesURL", "https://repo.azisaba.net/repository/maven-releases/")
            )
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks.shadowJar)
        }
    }
}

tasks {
    javadoc {
        options.encoding = "UTF-8"
        (options as? StandardJavadocDocletOptions)?.apply {
            addStringOption("Xdoclint:none", "-quiet")
        }
        isFailOnError = false
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    compileKotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-opt-in=kotlin.RequiresOptIn", "-Xskip-metadata-version-check")
        }
    }

    processResources {
        doNotTrackState("plugin.yml should be updated every time")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        from(sourceSets.main.get().resources.srcDirs) {
            filter(
                ReplaceTokens::class,
                mapOf("tokens" to mapOf("version" to project.version.toString(), "apiVersion" to minecraftVersion)),
            )
            filteringCharset = "UTF-8"
        }
    }

    shadowJar {
        archiveClassifier.set("")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        exclude("org/slf4j/**")
        relocate("org.mariadb", "com.github.mori01231.lifecore.lib.org.mariadb")
        relocate("com.zaxxer.hikari", "com.github.mori01231.lifecore.lib.com.zaxxer.hikari")
        relocate("kotlin", "com.github.mori01231.lifecore.lib.kotlin")
        relocate("org.jetbrains", "com.github.mori01231.lifecore.lib.org.jetbrains")
        relocate("com.charleskorn.kaml", "com.github.mori01231.lifecore.lib.com.charleskorn.kaml")
        relocate("org.yaml", "com.github.mori01231.lifecore.lib.org.yaml")
        relocate("org.snakeyaml", "com.github.mori01231.lifecore.lib.org.snakeyaml")
        relocate("org.intellij", "com.github.mori01231.lifecore.lib.org.intellij")
        relocate("xyz.acrylicstyle.util", "com.github.mori01231.lifecore.lib.xyz.acrylicstyle.util")
    }

    jar {
        archiveClassifier.set("plain")
    }

    assemble {
        dependsOn(shadowJar)
    }
}
