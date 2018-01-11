package com.thisnew.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowManager;
import com.thisnew.DFSConfigPersistence;
import com.thisnew.HdfsProjectComponent;
import com.thisnew.httputil.ConnectionClient;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.InetAddress;

public class DFSConfigurable implements Configurable {
    private JTextField textField1;      //url
    private JTextField textField2;      //port
    private JTextField textField3;      //path
    private JTextField textField4;     //chat
    private JCheckBox checkBox1;
    private JPanel root;
    private JTextArea TextArea;
    private JButton testConnectionButton;
    private JTextArea DisplayConStatus;
    private JLabel portLabel;
    private JLabel urlLabel;
    private Project project;
    private DFSConfigPersistence config;
    public DFSConfigurable(Project pj){
        this.project=pj;
        this.config=DFSConfigPersistence.getInstance(pj);

        testConnectionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                    DisplayConStatus.setText(" ");
                    String reg="^(http|https|ftp)\\://(((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])|([a-zA-Z0-9_\\-\\.])+\\.(com|net|org|edu|int|mil|gov|arpa|biz|aero|name|coop|info|pro|museum|uk|me))((:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&%\\$#\\=~])*)$*";
                    String url=textField1.getText().trim()+":"+textField2.getText().trim();
                    if(!url.matches(reg)){
                        DisplayConStatus.setForeground(Color.red);
                        DisplayConStatus.setText("Error url: " + url);
                    }else {
                        String status = new ConnectionClient(url).ForTestExecuteQuary();
                        if (status.equals("200")) {
                            DisplayConStatus.setForeground(Color.green);
                            DisplayConStatus.setText("Successful");
                        } else {
                            DisplayConStatus.setForeground(Color.red);
                            DisplayConStatus.setText("Error code: " + status);
                        }
                        super.mouseClicked(e);
                    }
                }
        });
        textField1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                urlLabel.setText(" ");
                String reg="^(http|https|ftp)\\://(((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])|([a-zA-Z0-9_\\-\\.])+\\.(com|net|org|edu|int|mil|gov|arpa|biz|aero|name|coop|info|pro|museum|uk|me))((:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\\-\\._\\?\\,\\'/\\\\\\+&%\\$#\\=~])*)$*";
                if(!textField1.getText().trim().matches(reg)){
                    urlLabel.setForeground(Color.red);
                    urlLabel.setText("Mismatch");
                }
                super.focusLost(e);
            }
        });
        textField2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                portLabel.setText(" ");
                String regport="^([0-9]|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$";
                if(!textField2.getText().trim().matches(regport)){
                    portLabel.setForeground(Color.red);
                    portLabel.setText("Mismatch");
                }
                super.focusLost(e);
            }
        });


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
    public void apply() {
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
