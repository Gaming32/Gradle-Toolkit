package xyz.unifycraft.gradle

import org.gradle.api.Project
import xyz.unifycraft.gradle.utils.propertyOr
import java.io.ByteArrayOutputStream

data class GitHubData(
    val branch: String,
    val commit: String,
    val url: String
) {
    companion object {
        @JvmStatic
        fun from(project: Project): GitHubData {
            val extension = project.extensions.findByName("githubData") as GitHubData?
            if (extension != null)
                return extension

            val branch = project.propertyOr("GITHUB_REF_NAME", fetchCurrentBranch(project) ?: "LOCAL")!!
            val commit = project.propertyOr("GITHUB_SHA", fetchCurrentCommit(project) ?: "LOCAL")!!
            val url = (fetchCurrentUrl(project) ?: "NONE").replace(".git", "")
            val data = GitHubData(branch, commit, url)
            project.extensions.add("githubData", data)
            return data
        }

        @JvmStatic
        fun fetchCurrentBranch(project: Project): String? {
            val output = ByteArrayOutputStream()
            project.exec {
                commandLine("git", "rev-parse", "--abbrev-ref", "HEAD")
                standardOutput = output
            }
            val string = output.toString().trim()
            return if (string.isEmpty() || string.startsWith("fatal")) null else string
        }

        @JvmStatic
        fun fetchCurrentCommit(project: Project): String? {
            val output = ByteArrayOutputStream()
            project.exec {
                commandLine("git", "rev-parse", "HEAD")
                standardOutput = output
            }
            val string = output.toString().trim()
            return if (string.isEmpty() || string.startsWith("fatal")) null else string.substring(0, 7)
        }

        @JvmStatic
        fun fetchCurrentUrl(project: Project): String? {
            val output = ByteArrayOutputStream()
            project.exec {
                commandLine("git", "config", "--get", "remote.origin.url")
                standardOutput = output
            }
            val string = output.toString().trim()
            return if (string.isEmpty() || string.startsWith("fatal")) null else string
        }
    }
}
