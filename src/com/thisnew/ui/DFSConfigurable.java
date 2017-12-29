package com.thisnew.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.thisnew.DFSConfigPersistence;
import com.thisnew.HdfsProjectComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DFSConfigurable implements Configurable {
    private JTextField textField1;      //url
    private JTextField textField2;      //port
    private JTextField textField3;      //path
    private JTextField textField4;     //chat
    private JCheckBox checkBox1;
    private JPanel root;
    private JTextArea TextArea;

    private Project project;
    private DFSConfigPersistence config;

    public DFSConfigurable(Project pj){
        this.project=pj;
        this.config=DFSConfigPersistence.getInstance(pj);
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "HDFS-fs";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return root;
    }

    @Override
    public boolean isModified() {
        String newHost = textField1.getText().trim();
        String newPort = textField2.getText().trim();
        String newPath = textField3.getText().trim();
        String newCharset = textField4.getText().trim();
        if (newPath == null) {
            newPath = "/";
        } else {
            newPath = newPath.trim();
        }
        return !(newHost.equals(config.host)
                && Integer.valueOf(newPort).equals(config.port)
                && newCharset.equals(config.charset)
                && config.enabled == checkBox1.isSelected()
                && (newPath.equals(config.whitePaths)));

    }


    @Override
    public void apply() throws ConfigurationException {
        String oldHost = config.host;
        config.host = textField1.getText().trim();
        config.port = Integer.valueOf(textField2.getText().trim());
        boolean oldEnabled = config.enabled;
        config.enabled = checkBox1.isSelected();
        HdfsProjectComponent hdfsProjectComponent = HdfsProjectComponent.getInstance(project);
        if (!oldEnabled && config.enabled) {
            hdfsProjectComponent.initDFS();
            if(ToolWindowManager.getInstance(project).getToolWindow("HDFS") ==null){
                hdfsProjectComponent.initWindowsTools();
            }

        }
        // host changed to init zk again
        if (oldHost != null && !oldHost.equals(config.host)) {
            hdfsProjectComponent.initDFS();

        }
        config.path = textField3.getText();
        if (config.isAvailable()) {
            hdfsProjectComponent.reloadDFSTree();
        }


    }

    @Override
    public void reset() {
        textField1.setText(config.host);
        textField2.setText(config.port == null ? "14000" : String.valueOf(config.port));
        checkBox1.setSelected(config.enabled);
        textField3.setText(config.path);
    }

    @Override
    public void disposeUIResources() {

    }
}
