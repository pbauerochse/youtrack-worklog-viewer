package de.pbauerochse.worklogviewer.fx.tablecolumns;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Patrick Bauerochse
 * @since 08.07.15
 */
public class CellStyleClasses {

    public static final String SUMMARY_COLUMN_OR_CELL_CSS_CLASS = "summary";
    public static final String GROUP_COLUMN_OR_CELL_CSS_CLASS = "group";
    public static final String HIGHLIGHT_COLUMN_CSS_CLASS = "highlight";
    public static final String TODAY_COLUMN_OR_CELL_CSS_CLASS = "today";
    public static final String ISSUE_CELL_CSS_CLASS = "issue-cell";

    public static final Set<String> ALL = new HashSet<>();
    static {
        ALL.add(SUMMARY_COLUMN_OR_CELL_CSS_CLASS);
        ALL.add(GROUP_COLUMN_OR_CELL_CSS_CLASS);
        ALL.add(HIGHLIGHT_COLUMN_CSS_CLASS);
        ALL.add(TODAY_COLUMN_OR_CELL_CSS_CLASS);
        ALL.add(ISSUE_CELL_CSS_CLASS);
    }

}
