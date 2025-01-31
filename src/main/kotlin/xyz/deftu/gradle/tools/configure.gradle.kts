package xyz.deftu.gradle.tools

import xyz.deftu.gradle.ModData
import xyz.deftu.gradle.ProjectData
import xyz.deftu.gradle.utils.isLoomPresent
import xyz.deftu.gradle.utils.propertyBoolOr

plugins {
    java
}

val modData = ModData.from(project)
val projectData = ProjectData.from(project)

if (modData.present) {
    if (propertyBoolOr("mod.version.setup", true))
        version = modData.version
    if (propertyBoolOr("mod.group.setup", true))
        group = modData.group
    if (propertyBoolOr("mod.name.setup", true)) {
        base.archivesName.set(modData.name)
        tasks {
            if (isLoomPresent()) {
                named<org.gradle.jvm.tasks.Jar>("remapJar") {
                    archiveBaseName.set(modData.name)
                }
            } else {
                named<Jar>("jar") {
                    archiveBaseName.set(modData.name)
                }
            }
        }
    }
}

if (projectData.present) {
    if (propertyBoolOr("project.version.setup", true))
        version = projectData.version
    if (propertyBoolOr("project.group.setup", true))
        group = projectData.group
    if (propertyBoolOr("project.name.setup", true)) {
        base.archivesName.set(projectData.name)
        tasks {
            named<Jar>("jar") {
                archiveBaseName.set(projectData.name)
            }
        }
    }
}
