package de.pbauerochse.worklogviewer.datasource.dummy

import de.pbauerochse.worklogviewer.datasource.AddWorkItemRequest
import de.pbauerochse.worklogviewer.datasource.AddWorkItemResult
import de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSource
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.timereport.*
import org.slf4j.LoggerFactory
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.random.Random

/**
 * A [TimeTrackingDataSource] that generates random example data. Can be used during
 * application development.
 */
class DummyDataSource(username: String) : TimeTrackingDataSource {

    private val ownUser = User(username, "Yourself ($username)")

    override fun getTimeReport(parameters: TimeReportParameters, progress: Progress): TimeReport {
        val issues = generateRandomIssues(parameters.timerange.start, parameters.timerange.end, progress)
        return TimeReport(parameters, issues)
    }

    override fun addWorkItem(request: AddWorkItemRequest, progress: Progress): AddWorkItemResult {
        LOGGER.info("Adding Workitem $request")
        val issue = generateRandomIssues(1).first()
        val workItem = generateWorkItems(issue, LocalDate.now(), listOf(ownUser), progress).first()
        return AddWorkItemResult(
            issue = issue,
            addedWorkItem = workItem
        )
    }

    override fun loadIssuesByIds(issueIds: Set<String>, progress: Progress): List<Issue> {
        return generateRandomIssues(issueIds.size)
    }

    override fun searchIssues(query: String, offset: Int, maxResults: Int, progress: Progress): List<Issue> {
        return generateRandomIssues(Random.nextInt(1, maxResults))
    }

    override fun loadIssue(id: String, progress: Progress): IssueWithWorkItems {
        val issue = generateRandomIssues(1).first()
        val workItems = generateWorkItems(issue, LocalDate.now(), generateRandomUsers(), progress)
        return IssueWithWorkItems(issue, workItems)
    }

    override fun loadWorkItems(issue: Issue, timeRange: TimeRange?, progress: Progress): IssueWithWorkItems {
        var currentDate = LocalDate.now().minusDays(30)
        val workItems = mutableListOf<WorkItem>()
        val users = generateRandomUsers()

        while (currentDate <= LocalDate.now()) {
            workItems.addAll(generateWorkItems(issue, currentDate, users, progress))
            currentDate = currentDate.plusDays(1)
        }

        return IssueWithWorkItems(issue, workItems)
    }

    override fun getWorkItemTypes(projectId: String, progress: Progress): List<WorkItemType> {
        return listOf(
            WorkItemType("ID1", "Development"),
            WorkItemType("ID2", "Project management"),
            WorkItemType("ID3", "Documentation")
        )
    }

    private fun generateRandomIssues(startDate: LocalDate, endDate: LocalDate, progress: Progress): List<IssueWithWorkItems> {
        LOGGER.info("Generating Random Issues between $startDate and $endDate")
        val users = generateRandomUsers()
        val issues = generateRandomIssues(Random.nextInt(10, 50))

        LOGGER.info("Generated ${issues.size} issues")

        return issues.map { issue ->
            val workItems = mutableListOf<WorkItem>()

            var currentDate = startDate
            while (currentDate <= endDate) {
                repeat(Random.nextInt(1, issues.size + 1)) {
                    workItems.addAll(generateWorkItems(issue, currentDate, users, progress))
                }

                currentDate = currentDate.plusDays(1)
            }

            return@map IssueWithWorkItems(issue, workItems)
        }
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

    private fun generateRandomIssues(amount: Int): List<Issue> {
        val projects = generateRandomProjects(Random.nextInt(1, 10))
        return (1..amount).map {
            val project = projects.random()
            val issueId = "$project-$it"
            val issueDescription = DummyNames.issues.random()
            val resolved = Random.nextBoolean()
            val resolveDate = if (resolved) LocalDateTime.now() else null
            val issueFields = fieldsWithValues(project.possibleFields)
            val tags = randomTags(Random.nextInt(3, 10))
            return@map object : Issue {
                override val id: String = issueId
                override val issueNumber: Long = it.toLong()
                override val humanReadableId: String = issueId
                override val externalUrl: URL = URL("http://localhost/issue/$issueId")
                override val title: String = issueDescription
                override val description: String = issueDescription
                override val project: Project = project
                override val resolutionDate: ZonedDateTime? = resolveDate?.atZone(ZoneId.systemDefault())
                override val fields: List<Field> = issueFields
                override val tags: List<Tag> = tags
            }
        }
    }

    private fun generateWorkItems(issue: Issue, currentDate: LocalDate, users: List<User>, progress: Progress): List<WorkItem> {
        LOGGER.info("Generating TimeEntries for Issue ${issue.id} and date $currentDate")
        val maxTimebookings = Random.nextInt(0, 10)

        return (0 until maxTimebookings).map {
            val user = users.random()
            val durationInQuarterHours = Random.nextLong(1, 5)
            val durationInMinutes = durationInQuarterHours * 15
            val workType = (getWorkItemTypes(issue.project.id, progress) + listOf(null)).random()
            return@map object : WorkItem {
                override val id: String = "${issue.id}-$it"
                override val owner: User = user
                override val workDate: ZonedDateTime = currentDate.atStartOfDay(ZoneId.systemDefault())
                override val durationInMinutes: Long = durationInMinutes
                override val description: String = ""
                override val workType: WorkItemType? = workType
                override val belongsToCurrentUser: Boolean = user.id == ownUser.id
            }
        }
    }

    private fun fieldsWithValues(possibleFields: List<DummyNames.ProjectField>): List<Field> = possibleFields.map { field ->
        val isMultiValueField = Random.nextBoolean()
        val upperBound = if (isMultiValueField) 3 else 2

        val numValues = Random.nextInt(0, upperBound)

        val values = (0 until numValues).map {
            field.possibleValues.random()
        }.distinct()

        Field(field.name, values)
    }

    private fun generateRandomProjects(amount: Int): List<DummyProject> {
//        val amount = Random.nextInt(1, 15)
        LOGGER.info("Generating $amount Projects")

        return (1..amount).map {
            val projectName = DummyNames.projects.random()
            val amountOfFieldValues = Random.nextInt(3, DummyNames.fields.size)
            LOGGER.info("Selecting $amountOfFieldValues Fields out of ${DummyNames.fields.size} for Project $it")
            val projectFields = (1..amountOfFieldValues).map { DummyNames.fields.random() }.distinct()
            DummyProject("P$it", projectName, projectFields)
        }
    }

    internal class DummyProject(
        id: String,
        name: String,
        val possibleFields: List<DummyNames.ProjectField> = emptyList()
    ): Project(id, name, name) {
        override fun toString(): String = name
    }

    private fun randomTags(count: Int): List<Tag> {
        return (1..count).map { DummyNames.tags.random() }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DummyDataSource::class.java)
    }

}
