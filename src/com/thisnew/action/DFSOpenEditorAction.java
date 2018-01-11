package com.thisnew.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.ui.treeStructure.Tree;
import com.thisnew.HdfsProjectComponent;
import com.thisnew.ui.DFSNode;
import com.thisnew.vsf.DFSNodeVirtualFile;

import javax.swing.tree.TreePath;

public class DFSOpenEditorAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        HdfsProjectComponent hdfsProjectComponent = HdfsProjectComponent.getInstance(anActionEvent.getProject());
        Tree dfsTree = hdfsProjectComponent.getDFSTree();
        TreePath treePath = dfsTree.getSelectionPath();
        DFSNode dfsNode= (DFSNode) treePath.getLastPathComponent();
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(anActionEvent.getProject());
        DFSNodeVirtualFile dfsNodeVirtualFile = (DFSNodeVirtualFile) hdfsProjectComponent.getFileSystem().findFileByPath(dfsNode.getFilePath());
        if(dfsNodeVirtualFile != null){
            dfsNodeVirtualFile.setLeaf();
            fileEditorManager.openFile(dfsNodeVirtualFile,true);
        }


    }
}
