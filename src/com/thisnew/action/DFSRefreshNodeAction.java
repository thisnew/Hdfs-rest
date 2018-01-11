package com.thisnew.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.treeStructure.Tree;
import com.thisnew.HdfsProjectComponent;

import javax.swing.tree.TreePath;


/**
 * Created by NTable on 2017/6/4.
 */
public class DFSRefreshNodeAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        HdfsProjectComponent hdfsProjectComponent= HdfsProjectComponent.getInstance(anActionEvent.getProject());
        Tree hdfsTree = hdfsProjectComponent.getDFSTree();
        TreePath treePath = hdfsTree.getSelectionPath();
        hdfsTree.updateUI();
        if(treePath != null){
            hdfsTree.expandPath(treePath);
        }
    }
}
