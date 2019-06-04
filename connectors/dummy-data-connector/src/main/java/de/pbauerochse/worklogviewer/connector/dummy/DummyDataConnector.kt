package de.pbauerochse.worklogviewer.connector.dummy

import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.connector.workitem.AddWorkItemRequest
import de.pbauerochse.worklogviewer.connector.workitem.AddWorkItemResult
import de.pbauerochse.worklogviewer.report.*
import de.pbauerochse.worklogviewer.tasks.Progress
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random

class DummyDataConnector(username: String) : YouTrackConnector {

    private val ownUser = User(username, "Yourself ($username)")

    override fun getTimeReport(parameters: TimeReportParameters, progress: Progress): TimeReport {
        val issues = generateRandomIssues(parameters)
        return TimeReport(parameters, issues)
    }

    override fun addWorkItem(request: AddWorkItemRequest): AddWorkItemResult {
        LOGGER.info("Adding Workitem $request")
        return AddWorkItemResult(
            request.issueId,
            ownUser,
            request.date,
            request.durationInMinutes,
            request.description,
            null
        )
    }

    override fun searchIssues(query: String, offset: Int, progress: Progress): List<Issue> {
        TODO("not implemented")
    }

    private fun generateRandomIssues(parameters: TimeReportParameters): List<Issue> {
        LOGGER.info("Generating Random Issues for ${parameters.timerange}")
        val users = generateRandomUsers()
        val issues = generateRandomIssues()

        LOGGER.info("Generated ${issues.size} issues")

        var currentDate = parameters.timerange.start
        while (currentDate <= parameters.timerange.end) {

            val numerOfWorkedOnIssuesThisDay = Random.nextInt(1, issues.size + 1)
            repeat(numerOfWorkedOnIssuesThisDay) {
                val issue = issues.random()
                fillWithTimeEntries(issue, currentDate, users)
            }

            currentDate = currentDate.plusDays(1)
        }

        return issues
    }

    private fun generateRandomUsers(): List<User> {
        val amount = Random.nextInt(3, 12)
        LOGGER.info("Generating $amount Users")
        val genereatedUsers = (1..amount).map {
            val firstName = DummyNames.firstNames.random()
            val lastName = DummyNames.lastNames.random()

            User("User_$it", "$firstName $lastName")
        }

        return genereatedUsers + ownUser
    }

    private fun generateRandomIssues(): List<Issue> {
        val projects = generateRandomProjects()
        val amount = Random.nextInt(1, 30)
        return (1..amount).map {
            val project = projects.random()
            val issueId = "$project-$it"
            val issueDescription = DummyNames.issues.random()
            val resolved = Random.nextBoolean()
            val resolveDate = if (resolved) LocalDateTime.now() else null
            val issueFields = fieldsWithValues(project.possibleFields)
            Issue(issueId, issueDescription, issueDescription, issueFields, resolveDate)
        }
    }

    private fun fillWithTimeEntries(issue: Issue, currentDate: LocalDate, users: List<User>) {
        LOGGER.info("Generating TimeEntries for Issue ${issue.id} and date $currentDate")
        val maxTimebookings = Random.nextInt(0, 10)

        repeat(maxTimebookings) {
            val user = users.random()
            val durationInQuarterHours = Random.nextLong(1, 5)
            val durationInMinutes = durationInQuarterHours * 15
            val workType = getRandomWorkType()

            val worklogItem = WorklogItem(issue, user, currentDate, durationInMinutes, "Working for $durationInMinutes minutes", workType)
            issue.worklogItems.add(worklogItem)
        }
    }

    private fun getRandomWorkType(): String? = listOf(null, "Development", "Testing", "Analysis", "Communication").random()

    private fun fieldsWithValues(possibleFields: List<DummyNames.ProjectField>): List<Field> = possibleFields.map { field ->
        val isMultiValueField = Random.nextBoolean()
        val upperBound = if (isMultiValueField) 3 else 2

        val numValues = Random.nextInt(0, upperBound)

        val values = (0 until numValues).map {
            field.possibleValues.random()
        }.distinct()

        Field(field.name, values)
    }

    private fun generateRandomProjects(): List<DummyProject> {
        val amount = Random.nextInt(1, 15)
        LOGGER.info("Generating $amount Projects")

        return (1..amount).map {
            val projectName = DummyNames.projects.random()
            val amountOfFieldValues = Random.nextInt(3, DummyNames.fields.size)
            LOGGER.info("Selecting $amountOfFieldValues Fields out of ${DummyNames.fields.size} for Project $it")
            val projectFields = (1..amountOfFieldValues).map { DummyNames.fields.random() }.distinct()
            DummyProject(projectName, projectFields)
        }
    }

    internal data class DummyProject(
        val name: String,
        val possibleFields: List<DummyNames.ProjectField> = emptyList()
    ) {
        override fun toString(): String = name
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DummyDataConnector::class.java)
    }

}
