package com.thisnew;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

/**
 * Created by NZY-PC on 2017/6/7.
 */

@State(name = "DFSConfigurable", storages = {@Storage(id = "HDFS", file = StoragePathMacros.PROJECT_FILE)})
public class DFSConfigPersistence implements PersistentStateComponent<DFSConfigPersistence> {
    public String host;
    public Integer port;
    public String path;
    public String whitePaths;
    public boolean enabled;
    public String charset;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static DFSConfigPersistence getInstance(Project project) {
        DFSConfigPersistence dfsConfigPersistence = ServiceManager.getService(project, DFSConfigPersistence.class);
        return dfsConfigPersistence;
    }


    @Nullable
    @Override
    public DFSConfigPersistence getState() {
        return this;
    }


    @Override
    public void loadState(DFSConfigPersistence dfsConfigPersistence) {
        XmlSerializerUtil.copyBean(dfsConfigPersistence, this);
    }

    public boolean isAvailable() {
        return enabled && StringUtil.isNotEmpty(host);
    }

    public String getUrl() {
        if (host.contains(":")) {
            return host;
        } else {
            return host + ":" + port;
        }
    }

    public String getFirstServer() {
        return getUrl();
    }


    public String getTitle() {

        return getUrl();
    }

}
