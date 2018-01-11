package com.thisnew.vsf;


import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.ex.dummy.DummyFileSystem;
import com.thisnew.httputil.ConnectionClient;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by NTable on 2017/6/5.
 */
public class DFSVirturalFileSystem extends DummyFileSystem {

    public static final String PROTOCOL = "DFS";
    private ConnectionClient cw;
    private Charset charset = Charset.forName("utf-8");

    public DFSVirturalFileSystem(ConnectionClient cw, String charset){
        this.cw=cw;
        if(StringUtil.isEmpty(charset)){
            charset="utf-8";
        }
        this.charset=Charset.forName(charset);
    }


    @NotNull
    public String getProtocol() {
        return PROTOCOL;
    }

    public Charset getCharset() {
        return this.charset;
    }

    @Nullable
    public VirtualFile findFileByPath(@NotNull @NonNls String path) {
        return new DFSNodeVirtualFile(this, path);


    }

    public void refresh(boolean b) {

    }

    @Nullable
    public VirtualFile refreshAndFindFileByPath(@NotNull String path) {
        return findFileByPath(path);
    }

    public void addVirtualFileListener(@NotNull VirtualFileListener virtualFileListener) {

    }

    public void removeVirtualFileListener(@NotNull VirtualFileListener virtualFileListener) {

    }

    public void deleteFile(Object o, @NotNull VirtualFile virtualFile) throws IOException {
//        try {
//            // getCurator().delete().forPath(virtualFile.getPath());
//            getCw().delete(virtualFile.getPath());
//            //todo
//        } catch (Exception ignore) {
//
//        }
    }

    public void moveFile(Object o, @NotNull VirtualFile virtualFile, @NotNull VirtualFile virtualFile2) throws IOException {
//        try {
//            byte[] content = getCw().getData().forPath(virtualFile.getPath());
//            getCw().create().forPath(virtualFile2.getPath(), content);
//            getCw().delete().forPath(virtualFile.getPath());
//        } catch (Exception ignore) {
//
//        }
    }

    public void renameFile(Object o, @NotNull VirtualFile virtualFile, @NotNull String name) throws IOException {
//        String newFilePath = virtualFile.getPath().substring(0, virtualFile.getPath().indexOf("/")) + "/" + name;
//        moveFile(o, virtualFile, new ZkNodeVirtualFile(this, newFilePath));
    }

    public VirtualFile createChildFile(Object o, @NotNull VirtualFile virtualFile, @NotNull String fileName) throws IOException {
        String filePath = virtualFile.getPath() + "/" + fileName;
//        try {
//            getCurator().create().forPath(filePath);
//        } catch (Exception ignore) {
//
//        }
        return new DFSNodeVirtualFile(this, filePath);
    }

    @NotNull
    public VirtualFile createChildDirectory(Object o, @NotNull VirtualFile virtualFile, @NotNull String directory) throws IOException {
        String filePath = virtualFile.getPath() + "/" + directory;
//        try {
//            getCurator().create().forPath(filePath);
//            getCw().create(filePath);
//        } catch (Exception ignore) {
//
//        }
        return new DFSNodeVirtualFile(this, filePath);
    }

    public VirtualFile copyFile(Object o, @NotNull VirtualFile virtualFile, @NotNull VirtualFile virtualFile2, @NotNull String s) throws IOException {
        try {
            //todo
        } catch (Exception ignore) {

        }
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }

    public ConnectionClient getCw() {
        return this.cw;
    }
}
