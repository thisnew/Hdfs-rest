package com.thisnew;

import com.intellij.ide.ui.customization.CustomizationUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.treeStructure.Tree;
import com.thisnew.httputil.ConnectionWork;
import com.thisnew.ui.DFSNode;
import com.thisnew.ui.DFSTreeModel;
import com.thisnew.vsf.DFSVirturalFileSystem;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.WrappingIconPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.net.Socket;


/**
 * Created by NZY-PC on 2017/6/3.
 */
public class HdfsProjectComponent extends DoubleClickListener implements ProjectComponent {

    private Project project;
    private Tree DFSTree;
    private final Icon rootIcon = IconLoader.findIcon("hadoop.png");
    private ConnectionWork cw;

    private DFSVirturalFileSystem dvfs;



    public HdfsProjectComponent(Project project) {
        this.project=project;
    }

    public static HdfsProjectComponent getInstance(Project project) {
        return project.getComponent(HdfsProjectComponent.class);
    }

    @Override
    public void projectOpened() {
        initDFS();
        if (this.cw != null) {
            initWindowsTools();
        }
    }


    public void initWindowsTools() {
        // 设定配置文件
        final DFSConfigPersistence config=DFSConfigPersistence.getInstance(project);
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).registerToolWindow("HDFS-Rest", false, ToolWindowAnchor.LEFT);
        toolWindow.setTitle("HDFS-Rest");
        toolWindow.setIcon(rootIcon);

        //获取root节点  "/" 的名称
        DFSNode.ROOT_NAME=config.getTitle();
        //获取tree

        DFSTree=new Tree(new DFSTreeModel(cw,config.whitePaths));
        //todo 设置tree类 curator, config.whitePaths 构造
        //插件提示注册
        ToolTipManager.sharedInstance().registerComponent(DFSTree);
        //选择模式
        DFSTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.installOn(DFSTree);

        //DFSTreeModel
        CustomizationUtil.installPopupHandler(DFSTree, "DFS.OperationMenu", ActionPlaces.UNKNOWN);

        DFSTree.setCellRenderer(new DefaultTreeRenderer(){
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
                if (value instanceof DFSNode && component instanceof WrappingIconPanel) {
                    DFSNode node = (DFSNode) value;
                    WrappingIconPanel wrappingPanel = (WrappingIconPanel) component;
                    if (node.isRoot()) {
                        wrappingPanel.setIcon(rootIcon);
                    } else if (node.isLeaf()) {
                        FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName(node.getName());
                        Icon icon = fileType.getIcon();
                        //node.isBinary()
                        if (false) {
                            icon = FileTypes.ARCHIVE.getIcon();
                        } else if (fileType.getName().equalsIgnoreCase(FileTypes.UNKNOWN.getName())) {
                            icon = FileTypes.PLAIN_TEXT.getIcon();
                        }
                        else {
                            icon = IconLoader.getTransparentIcon(icon);
                        }
                        wrappingPanel.setIcon(icon);
                    }
//                    if (config.tooltip) {
//                        wrappingPanel.setToolTipText(node.getTooltip());
//                    }
                }
                return component;
            }
        });
        final ContentManager contentManager = toolWindow.getContentManager();
        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(true);
        JBScrollPane jbScrollPane = new JBScrollPane(DFSTree);
        panel.add(jbScrollPane);
        panel.setToolbar(createToolBar());
        final Content content = contentManager.getFactory().createContent(panel, null, false);
        contentManager.addContent(content);
    }

    private JComponent createToolBar() {
        ActionGroup actionGroup = (ActionGroup) ActionManager.getInstance().getAction("DFS.Toolbar");
        String place = ActionPlaces.EDITOR_TOOLBAR;
        JPanel toolBarPanel = new JPanel(new GridLayout());
        toolBarPanel.add(ActionManager.getInstance().createActionToolbar(place, actionGroup, true).getComponent());
        return toolBarPanel;
    }


    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "DFSComponent";
    }


    @Override
    protected boolean onDoubleClick(MouseEvent mouseEvent) {
        Tree source = (Tree) mouseEvent.getSource();
        TreePath treePath = source.getSelectionPath();
        DFSNode selectedNode = (DFSNode) treePath.getLastPathComponent();
        if (selectedNode.isLeaf()) {
            VirtualFile file = dvfs.findFileByPath(selectedNode.getFilePath());
            if (file != null && project != null) {
                new OpenFileDescriptor(project, file).navigate(true);
            }
        }
       return true;

    }


    public DFSVirturalFileSystem getFileSystem() {
        return dvfs;
    }

    public Tree getDFSTree() {
        return DFSTree;
    }

    public void reloadDFSTree() {
        DFSNode.ROOT_NAME = DFSConfigPersistence.getInstance(project).getTitle();
        if (cw == null) {
            initDFS();
        }
        if (cw != null) {
            DFSTree.setModel(new DFSTreeModel(cw, DFSConfigPersistence.getInstance(project).whitePaths));
            DFSTree.updateUI();
        }
    }

    public void initDFS() {
        if(this.cw!=null){
            this.cw.close();
        }
        DFSConfigPersistence config= DFSConfigPersistence.getInstance(project);
        if(config.isAvailable()){
            this.cw=new ConnectionWork(config.getUrl());
            cw.start();
            this.dvfs= new DFSVirturalFileSystem(cw,DFSConfigPersistence.getInstance(project).charset);
        }
    }

    public boolean ruok(String server) {
        try {
            String[] parts = server.split(":");
            Socket sock = new Socket(parts[0], Integer.valueOf(parts[1]));
            sock.getOutputStream().write("ruok".getBytes());

            return true;
        } catch (Exception e) {
            return false;
        }
    }



}
