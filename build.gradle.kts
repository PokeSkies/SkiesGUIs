@file:Suppress("UnstableApiUsage")

plugins {
    java
    idea
    id("quiet-fabric-loom") version ("1.6-SNAPSHOT")
    kotlin("jvm") version ("1.9.22")
}
val modId = project.properties["mod_id"].toString()
version = project.properties["mod_version"].toString()
group = project.properties["mod_group"].toString()

val modName = project.properties["mod_name"].toString()
base.archivesName.set(modName)

val minecraftVersion = project.properties["minecraft_version"].toString()

repositories {
    mavenCentral()
    maven( "https://jitpack.io")
    maven("https://maven.parchmentmc.org")
    maven {
        name = "Modrinth"
        url = uri("https://api.modrinth.com/maven")
        content {
            includeGroup("maven.modrinth")
        }
    }
    maven("https://maven.nucleoid.xyz/") { name = "Nucleoid" }
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots1"
        mavenContent { snapshotsOnly() }
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.impactdev.net/repository/development/")
}

loom {
    splitEnvironmentSourceSets()
    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

val modImplementationInclude by configurations.register("modImplementationInclude")

configurations {
    modImplementationInclude
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-$minecraftVersion:${project.properties["parchment_version"]}")
    })

    modImplementation("net.fabricmc:fabric-loader:${project.properties["loader_version"].toString()}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.properties["fabric_kotlin_version"].toString()}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.properties["fabric_version"].toString()}")

    modImplementation(include("net.kyori:adventure-platform-fabric:5.12.0")!!)

    modImplementation("me.lucko:fabric-permissions-api:0.2-SNAPSHOT")

    modImplementation("eu.pb4:placeholder-api:2.4.0-pre.1+1.20.5")

    modImplementation("io.github.miniplaceholders:miniplaceholders-api:2.2.3")
    modImplementation("io.github.miniplaceholders:miniplaceholders-kotlin-ext:2.2.3")

    modImplementation("net.impactdev.impactor:common:5.2.4+1.20.1-SNAPSHOT")
    modImplementation("net.impactdev.impactor.api:economy:5.2.4-SNAPSHOT")
    modImplementation("net.impactdev.impactor.api:text:5.2.4-SNAPSHOT")

    implementation(include("org.graalvm.sdk:graal-sdk:22.3.0")!!)
    implementation(include("org.graalvm.truffle:truffle-api:22.3.0")!!)

    modImplementation("com.github.plan-player-analytics:Plan:5.6.2614")

    modImplementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

tasks.processResources {
    inputs.property("version", version)

    filesMatching("fabric.mod.json") {
        expand("id" to modId, "version" to version)
    }

    filesMatching("**/lang/*.json") {
        expand("id" to modId)
    }
}

tasks.remapJar {
    archiveFileName.set("${project.name}-fabric-${project.properties["minecraft_version"]}-${project.version}.jar")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

tasks.withType<AbstractArchiveTask> {
    from("LICENSE") {
        rename { "${it}_${modId}" }
    }
}