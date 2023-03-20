import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.serialization") version "1.8.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    java
    `maven-publish`
}

group = "net.azisaba"
version = "6.4.1"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))

    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
    maven {
        name = "azisaba-repo"
        url = uri("https://repo.azisaba.net/repository/maven-public/")
    }
    maven {
        name = "lumine"
        url = uri("https://mvn.lumine.io/repository/maven-public/")
    }
    maven {
        name = "mypet"
        url = uri("https://repo.mypet-plugin.de/")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.charleskorn.kaml:kaml:0.52.0")
    //noinspection GradlePackageUpdate
    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.0.6")
    implementation("org.yaml:snakeyaml:2.0")
    compileOnly("net.azisaba.ballotbox:receiver:1.0.1")
    compileOnly("net.azisaba.azipluginmessaging:api:4.0.3")
    compileOnly("net.azisaba:RyuZUPluginChat:4.2.0")
    compileOnly("net.azisaba.rarity:api:1.0.1-SNAPSHOT")
    compileOnly("net.azisaba:ItemStash:1.0.0-SNAPSHOT")
    //noinspection VulnerableLibrariesLocal
    compileOnly("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.15.2-R0.1-SNAPSHOT")
    compileOnly("io.lumine:Mythic-Dist:4.13.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.MyPetORG.MyPet:mypet-api:5c8ceeac6a")
    compileOnly("de.keyle:knbt:0.0.5")
}

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
        }
    }
}

tasks {
    javadoc {
        options.encoding = "UTF-8"
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
        }
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        from(sourceSets.main.get().resources.srcDirs) {
            filter(ReplaceTokens::class, mapOf("tokens" to mapOf("version" to project.version.toString())))
            filteringCharset = "UTF-8"
        }
    }

    shadowJar {
        exclude("org/slf4j/**")
        relocate("org.mariadb", "com.github.mori01231.lifecore.lib.org.mariadb")
        relocate("com.zaxxer.hikari", "com.github.mori01231.lifecore.lib.com.zaxxer.hikari")
        relocate("kotlin", "com.github.mori01231.lifecore.lib.kotlin")
        relocate("org.jetbrains", "com.github.mori01231.lifecore.lib.org.jetbrains")
        relocate("com.charleskorn.kaml", "com.github.mori01231.lifecore.lib.com.charleskorn.kaml")
        relocate("org.yaml", "com.github.mori01231.lifecore.lib.org.yaml")
        relocate("org.snakeyaml", "com.github.mori01231.lifecore.lib.org.snakeyaml")
        relocate("org.intellij", "com.github.mori01231.lifecore.lib.org.intellij")
    }
}
