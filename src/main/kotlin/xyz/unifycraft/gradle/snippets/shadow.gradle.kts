package xyz.unifycraft.gradle.snippets

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import gradle.kotlin.dsl.accessors._06e55093d2b2796fa8ca19eb1df48cd4.implementation
import net.fabricmc.loom.LoomGradlePlugin
import org.gradle.jvm.tasks.Jar

plugins {
    id("com.github.johnrengelman.shadow")
    java
}

val unishade by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

val unishadowJar = tasks.register<ShadowJar>("unishadowJar") {
    group = "unishadow"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    configurations = listOf(project.configurations["unishade"])

    val javaPlugin = project.convention.getPlugin(JavaPluginConvention::class.java)
    val jarTask = project.tasks.getByName("jar") as Jar

    manifest.inheritFrom(jarTask.manifest)
    val libsProvider = project.provider { listOf(jarTask.manifest.attributes["Class-Path"]) }
    val files = project.objects.fileCollection().from(project.configurations["unishade"])
    doFirst {
        if (!files.isEmpty) {
            val libs = libsProvider.get().toMutableList()
            libs.addAll(files.map { it.name })
            manifest.attributes(mapOf("Class-Path" to libs.filterNotNull().joinToString(" ")))
        }
    }

    from(javaPlugin.sourceSets.getByName("main").output)
    exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")

    project.artifacts.add("unishade", project.tasks.named("unishadowJar"))
}

pluginManager.withPlugin("java") {
    tasks["assemble"].dependsOn(unishadowJar)
}

pluginManager.withPlugin("gg.essential.loom") {
    tasks["shadowJar"].doFirst {
        throw GradleException("Incorrect task! You're looking for unishadowJar.")
    }

    val remapJar = project.tasks["remapJar"] as Jar
    val unishadowJar = tasks["unishadowJar"] as ShadowJar
    remapJar.dependsOn(unishadowJar)
    remapJar.from(unishadowJar.archiveFile.get())
}