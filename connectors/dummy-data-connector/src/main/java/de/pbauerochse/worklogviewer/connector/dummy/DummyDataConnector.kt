package de.pbauerochse.worklogviewer.connector.dummy

import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.report.*
import de.pbauerochse.worklogviewer.tasks.Progress
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

class DummyDataConnector : YouTrackConnector {

    override fun getTimeReport(parameters: TimeReportParameters, progress: Progress): TimeReport {
        val issues = generateRandomIssues(parameters)
        return TimeReport(parameters, issues)
    }

    private fun generateRandomIssues(parameters: TimeReportParameters): List<Issue> {
        LOGGER.info("Generating Random Issues for ${parameters.timerange}")
        val users = generateRandomUsers()
        val issues = generateRandomIssues()

        LOGGER.info("Generated ${issues.size} issues")

        var currentDate = parameters.timerange.start
        while (currentDate <= parameters.timerange.end) {

            val numerOfWorkedOnIssuesThisDay = Random.nextInt(20, 50)
            repeat(numerOfWorkedOnIssuesThisDay) {
                val issue = issues.random()
                fillWithTimeEntries(issue, currentDate, users)
            }

            currentDate = currentDate.plusDays(1)
        }

        return issues
    }

    private fun generateRandomUsers(): List<User> {
        val amount = Random.nextInt(5, 25)
        LOGGER.info("Generating $amount Users")
        return (1 .. amount).map {
            User("User $it", "Firstname Lastname #$it")
        }
    }

    private fun generateRandomIssues() : List<Issue> {
        val fields = generateRandomFields()
        val projects = generateRandomProjects(fields)
        val amount = Random.nextInt(1, 30)
        return (1 .. amount).map {
            val project = projects.random()
            val issueId = "$project-$it"
            val resolved = Random.nextBoolean()
            val resolveDate = if (resolved) LocalDateTime.now() else null
            val issueFields = fieldsWithValues(project.possibleFields)
            Issue(issueId, "Description for $issueId", resolveDate, issueFields)
        }
    }

    private fun generateRandomFields(): List<DummyProjectField> {
        val amount = Random.nextInt(20, 40)
        LOGGER.info("Generating $amount Fields")
        return (1 .. amount).map { fieldIndex ->
            val amountOfValues = Random.nextInt(1, 10)
            LOGGER.info("Generating $amountOfValues Values for Field $fieldIndex")
            val possibleValues = (1 .. amountOfValues).map {
                "Value #$it for Field $fieldIndex"
            }

            DummyProjectField("Field #$fieldIndex", possibleValues)
        }
    }

    private fun fillWithTimeEntries(issue: Issue, currentDate: LocalDate, users: List<User>) {
        LOGGER.info("Generating TimeEntries for Issue ${issue.id} and date $currentDate")
        val maxTimebookings = Random.nextInt(3, users.size)
        repeat(maxTimebookings) {
            val user = users.random()
            val durationInQuarterHours = Random.nextLong(1, 8)
            val durationInMinutes = durationInQuarterHours * 15
            val workType = getRandomWorkType()

            val worklogItem = WorklogItem(issue, user, currentDate, durationInMinutes, "Working for $durationInMinutes minutes", workType)
            issue.worklogItems.add(worklogItem)
        }
    }

    private fun getRandomWorkType(): String? = listOf(null, "Development", "Testing", "Analysis", "Communication").random()

    private fun fieldsWithValues(possibleFields: List<DummyProjectField>): List<Field> = possibleFields.map { field ->
        val isMultiValueField = Random.nextBoolean()
        val numValues = if (isMultiValueField) Random.nextInt(0, field.possibleValues.size) else Random.nextInt(0, 1)

        val values = (0 until numValues).map {
            field.possibleValues.random()
        }

        Field(field.name, values)
    }

    private fun generateRandomProjects(allFields : List<DummyProjectField>): List<DummyProject> {
        val amount = Random.nextInt(1, 15)
        LOGGER.info("Generating $amount Projects")

        return (1 .. amount).map {
            val amountOfFields = Random.nextInt(3, allFields.size)
            LOGGER.info("Selecting $amountOfFields Fields out of ${allFields.size} for Project $it")
            val projectFields = (1 .. amountOfFields).map { allFields.random() }.distinct()
            DummyProject("PROJECT $it", projectFields)
        }
    }

    internal data class DummyProject(
        val name: String,
        val possibleFields: List<DummyProjectField> = emptyList()
    ) {
        override fun toString(): String = name
    }

    internal data class DummyProjectField(
        val name: String,
        val possibleValues: List<String>
    )

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DummyDataConnector::class.java)
    }

}