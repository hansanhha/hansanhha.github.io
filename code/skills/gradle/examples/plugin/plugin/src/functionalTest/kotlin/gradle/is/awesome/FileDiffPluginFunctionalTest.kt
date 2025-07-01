package gradle.`is`.awesome

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class FileDiffPluginFunctionalTest {

    @TempDir
    lateinit var tempFolder: File

    private lateinit var testFile1: File
    private lateinit var testFile2: File

    @BeforeEach
    fun setup() {
        testFile1 = File(tempFolder, "testFile1.txt")
        testFile2 = File(tempFolder, "testFile2.txt")

        val buildScript = File(tempFolder, "build.gradle.kts")
        buildScript.writeText(
            """
                import gradle.is.awesome.FileDiffPluginExtension
                
                plugins {
                    id("gradle.is.awesome.file-diff")
                }
                configure<FileDiffPluginExtension> {
                    file1.set(file("testFile1.txt"))
                    file2.set(file("testFile2.txt"))
                }
            """.trimIndent()
        )
    }

    @Test
    fun `can diff 2 files of same length`() {
        testFile1.writeText("")
        testFile2.writeText("")
    }
}