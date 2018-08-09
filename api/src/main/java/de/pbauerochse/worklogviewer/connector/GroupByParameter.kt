package de.pbauerochse.worklogviewer.connector

interface GroupByParameter {
    val id : String
    fun getLabel() : String
}