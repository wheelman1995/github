package ru.wheelman.github

import io.mockk.every
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Test
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.text.Charsets.UTF_8

class Test {

    private val githubResponse =
        "[{\"login\":\"mojombo\",\"id\":1,\"node_id\":\"MDQ6VXNlcjE=\",\"avatar_url\":\"https://avatars0.githubusercontent.com/u/1?v=4\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/mojombo\",\"html_url\":\"https://github.com/mojombo\",\"followers_url\":\"https://api.github.com/users/mojombo/followers\",\"following_url\":\"https://api.github.com/users/mojombo/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/mojombo/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/mojombo/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/mojombo/subscriptions\",\"organizations_url\":\"https://api.github.com/users/mojombo/orgs\",\"repos_url\":\"https://api.github.com/users/mojombo/repos\",\"events_url\":\"https://api.github.com/users/mojombo/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/mojombo/received_events\",\"type\":\"User\",\"site_admin\":false}]"
    private val path = Paths.get("test.txt")

    @Test
    fun testGithubRequest() {
        assertEquals(
            githubResponse,
            GithubRequest().getUsersJson()
        )
    }

    @Test
    fun testWithMock() {
        val mock = mockk<FileReader>()
        every { mock.readStringFromFile(any()) } returns "123"
        assertEquals("123", mock.readStringFromFile(Paths.get("***")))
    }

    @Test
    fun testFileWriter() {
        val fileWriter = FileWriter()
        fileWriter.writeStringToFile(path, githubResponse)
        assertEquals(
            githubResponse,
            Files.newBufferedReader(path).readText()
        )
    }

    @Test
    fun testFileReader() {
        Files.newBufferedWriter(path).run {
            write(githubResponse)
            flush()
        }
        val fileReader = FileReader()
        assertEquals(
            githubResponse,
            fileReader.readStringFromFile(path)
        )
    }

    @After
    fun tearDown() {
        Files.deleteIfExists(path)
    }

    private class GithubRequest {

        fun getUsersJson(): String {
            val urlConnection = URL("https://api.github.com/users?per_page=1").openConnection()
            urlConnection.connect()
            urlConnection.getInputStream().use {
                return it.bufferedReader().readText()
            }
        }
    }

    private class FileWriter {

        fun writeStringToFile(path: Path, data: String) {
            Files.write(path, data.toByteArray())
        }
    }

    private class FileReader {

        fun readStringFromFile(path: Path) =
            Files.readAllBytes(path).toString(UTF_8)

    }
}