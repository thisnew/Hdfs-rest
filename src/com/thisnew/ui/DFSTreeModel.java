package com.thisnew.ui;

import com.intellij.openapi.util.text.StringUtil;
import com.thisnew.httputil.BlockInfo;
import com.thisnew.httputil.ConnectionWork;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.util.*;

/**
 * zoo keeper tree model
 *
 * @author linux_china
 */
public class DFSTreeModel implements TreeModel {
    private DFSNode root = new DFSNode("/", null);
    private ConnectionWork cw;
    private List<String> whitePaths;

    public DFSTreeModel(ConnectionWork cw, String whitePaths) {
        this.cw = cw;
        if (StringUtil.isNotEmpty(whitePaths)) {
            this.whitePaths = Arrays.asList(whitePaths.trim().split("[\\s;]+"));
        }
    }

    public Object getRoot() {
        return root;
    }

    public Object getChild(Object parent, int i) {
        List<DFSNode> children = getChildren((DFSNode) parent);
        return children.get(i);
    }

    public int getChildCount(Object parent) {
        DFSNode dfsNode = (DFSNode) parent;
        if (!dfsNode.isFilled()) {
            fillZkNode(dfsNode);
        }
        if (!dfsNode.isLeaf() && whitePaths != null && !whitePaths.isEmpty()) {
            return getChildren(dfsNode).size();
        }
        return dfsNode.getChildrenCount();
    }

    public boolean isLeaf(Object node) {
        DFSNode dfsNode = (DFSNode) node;
        if (!dfsNode.isFilled()) {
            fillZkNode(dfsNode);
        }
        return dfsNode.isLeaf();
    }

    private void fillZkNode(DFSNode dfsNode) {
        try {
            BlockInfo bi = cw.forpath(dfsNode.getFilePath());
            System.out.println("fillzknoew : "+dfsNode.getFilePath());
            System.out.println("bi : "+ bi.toString() );
            if (bi != null) {
                dfsNode.setBlockInfo(bi);
            }
        } catch (Exception ignore) {

        }
    }

    public void valueForPathChanged(TreePath treePath, Object o) {

    }

    public int getIndexOfChild(Object parent, Object node) {
        List<DFSNode> children = getChildren((DFSNode) parent);
        for (int i = 0; i < children.size(); i++) {
            if (((DFSNode) node).getFilePath().equals(children.get(i).getFilePath())) {
                return i;
            }
        }
        return -1;
    }

    public void addTreeModelListener(TreeModelListener treeModelListener) {

    }

    public void removeTreeModelListener(TreeModelListener treeModelListener) {

    }

    public List<DFSNode> getChildren(DFSNode node) {
        List<DFSNode> children = new ArrayList<DFSNode>();
        if (!node.isFilled()) {
            fillZkNode(node);
        }
        if (node.isLeaf()) {
            return children;
        }
        try {
            List<String> nodescw = cw.getChildren(node.getFilePath());


            Collections.sort(nodescw, new Comparator<String>() {
                public int compare(String s, String s2) {
                    return s.compareTo(s2);
                }
            });

            for (int i = 0; i < nodescw.size() && i < 100; i++) {
                DFSNode zkNode = new DFSNode(node.getFilePath(), nodescw.get(i));
                if (isWhitePath(zkNode.getFilePath())) {
                    children.add(zkNode);
                }
            }
        } catch (Exception ignore) {
        }


        return children;
    }


    private boolean isWhitePath(String filePath) {
        if (this.whitePaths != null) {
            boolean legal = false;
            for (String whitePath : whitePaths) {
                if (filePath.startsWith(whitePath)) {
                    legal = true;
                    break;
                } else if (whitePath.lastIndexOf("/") > 1 && whitePath.startsWith(filePath)) {
                    legal = true;
                    break;
                }
            }
            return legal;
        }
        return true;
    }
}