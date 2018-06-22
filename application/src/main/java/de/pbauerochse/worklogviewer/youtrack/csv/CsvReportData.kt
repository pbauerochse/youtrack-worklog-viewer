package de.pbauerochse.worklogviewer.youtrack.csv

import de.pbauerochse.worklogviewer.youtrack.domain.Project

/**
 * Data structure that contains the parsed csv report
 * entries
 */
@Deprecated("")
data class CsvReportData(val projects : List<Project>) {

    fun getProject(id : String) : Project? {
        return projects.find { it.id == id }
    }

}