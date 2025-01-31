package xyz.deftu.gradle.tools

import xyz.deftu.gradle.utils.propertyBoolOr

repositories {
    mavenCentral()
    mavenLocal()

    optionalMaven("repo.deftu.releases", "Deftu Releases", "https://maven.deftu.xyz/releases/")
    optionalMaven("repo.jitpack", "JitPack", "https://jitpack.io/")
    optionalMaven("repo.essential", "Essential", "https://repo.essential.gg/repository/maven-public/")
    optionalMaven("repo.sponge", "SpongePowered", "https://repo.spongepowered.org/maven/")
    optionalMaven("repo.deftu.snapshots", "Deftu Snapshots", "https://maven.deftu.xyz/snapshots/")
}

fun RepositoryHandler.optionalMaven(propertyName: String, name: String? = null, url: String) {
    if (project.propertyBoolOr(propertyName, false)) return
    maven(url) {
        name?.let { name ->
            setName(name)
        }
    }
}
