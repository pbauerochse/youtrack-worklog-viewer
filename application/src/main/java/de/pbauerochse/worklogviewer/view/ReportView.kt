package de.pbauerochse.worklogviewer.view

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.TimeReportParameters

class ReportView(val groups: List<ReportGroup>, val issues: List<Issue>, val reportParameters: TimeReportParameters)