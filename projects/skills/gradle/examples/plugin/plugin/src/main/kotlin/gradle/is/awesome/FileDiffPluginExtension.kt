package gradle.`is`.awesome

import org.gradle.api.file.RegularFileProperty

interface FileDiffPluginExtension {
    val file1: RegularFileProperty
    val file2: RegularFileProperty
}
