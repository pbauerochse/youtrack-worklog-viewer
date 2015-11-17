package de.pbauerochse.worklogviewer.fx.tabs.domain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import javafx.scene.control.TreeItem;

import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 09.07.15
 */
public class DisplayData {

    private List<TreeItem<DisplayRow>> treeRows = Lists.newArrayList();

    public ImmutableList<TreeItem<DisplayRow>> getTreeRows() {
        return ImmutableList.copyOf(treeRows);
    }

    public List<DisplayRow> getStatistics() {
        return Lists.newArrayList();
    }

    public void addRow(TreeItem<DisplayRow> rowTreeItem) {
        treeRows.add(rowTreeItem);
    }

}
