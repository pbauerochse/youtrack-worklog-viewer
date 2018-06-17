package de.pbauerochse.worklogviewer.excel

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet

/**
 * Creates the next available
 * row
 */
fun Sheet.createNextRow() : Row {
    val lastRowNum = this.lastRowNum
    return createRow(lastRowNum + 1)
}
