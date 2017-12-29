package com.thisnew.ui;


import com.thisnew.httputil.BlockInfo;

import java.util.Arrays;


/**
 * ZooKeeper Node
 *
 * @author linux_china
 */
public class DFSNode {
    private static java.util.List<String> binaryExtNames = Arrays.asList("pb", "bin", "msgpack");

    public static String ROOT_NAME = "/";
    private String path;
    private String name;
    // private Stat stat;
    private BlockInfo blockInfo;


    public DFSNode(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public boolean isLeaf() {
        return blockInfo.getType().equalsIgnoreCase("FILE") ? true : false;
    }

    public boolean isRoot() {
        return path.equals("/") && name == null;
    }

//    public boolean isEphemeral() {
//        return stat != null && stat.getEphemeralOwner() > 0;
//    }

    public int getChildrenCount() {
        return blockInfo != null ? blockInfo.getNumChildren() : 0;
    }

    public boolean isFilled() {
        return blockInfo != null;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        if (name == null) {
            return path;
        } else {
            if (path.endsWith("/")) {
                return path + name;
            } else {
                return path + "/" + name;
            }
        }
    }

    public DFSNode getSubNode(String subNodeName) {
        return new DFSNode(getFilePath(), subNodeName);
    }

    public BlockInfo getBlockInfo() {
        return blockInfo;
    }

    public void setBlockInfo(BlockInfo blockInfo) {
        this.blockInfo = blockInfo;
    }

    @Override
    public String toString() {
        return name == null ? ROOT_NAME : name;
    }

    public boolean isBinary() {
        String extName = null;
        if (name.contains(".")) {
            extName = name.substring(name.lastIndexOf(".") + 1);
        }
        return extName != null && binaryExtNames.contains(extName.toLowerCase());
    }

//    public String getTooltip() {
//        return "cZxid = " + stat.getCzxid() + "\n" +
//                "ctime = " + new Date(stat.getCtime()) + "\n" +
//                "mZxid = " + stat.getMzxid() + "\n" +
//                "mtime = " + new Date(stat.getMtime()) + "\n" +
//                "pZxid = " + stat.getPzxid() + "\n" +
//                "cversion = " + stat.getCversion() + "\n" +
//                "dataVersion = " + stat.getVersion() + "\n" +
//                "aclVersion = " + stat.getAversion() + "\n" +
//                "ephemeralOwner = " + stat.getEphemeralOwner() + "\n" +
//                "dataLength =" + stat.getDataLength() + " \n" +
//                "numChildren = " + stat.getNumChildren();
//    }
}
