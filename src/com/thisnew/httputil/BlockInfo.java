package com.thisnew.httputil;

public class BlockInfo {

    private String pathSuffix;         //: alidata1,
    private String type;               //: DIRECTORY,
    private int     length;             //: 0,
    private String owner;              //: hdfs,
    private String group;              //: supergroup,
    private int     permission;         //: 755,
    private long    accessTime;         //: 0,
    private long    modificationTime;   //: 1510207042941,
    private int     blockSize;          //: 0,
    private int     replication;        //: 0
    private int     childrenSize;


    @Override
    public String toString() {
        return "BlockInfo{" +
                "pathSuffix='" + pathSuffix + '\'' +
                ", type='" + type + '\'' +
                ", length=" + length +
                ", owner='" + owner + '\'' +
                ", group='" + group + '\'' +
                ", permission=" + permission +
                ", accessTime=" + accessTime +
                ", modificationTime=" + modificationTime +
                ", blockSize=" + blockSize +
                ", replication=" + replication +
                '}';
    }
    public int getNumChildren(){



        return childrenSize;
    }

    public void setNumChildren(int size) {
        this.childrenSize=size;
    }






    public String getPathSuffix() {
        return pathSuffix;
    }

    public void setPathSuffix(String pathSuffix) {
        this.pathSuffix = pathSuffix;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public long getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(long accessTime) {
        this.accessTime = accessTime;
    }

    public long getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(long modificationTime) {
        this.modificationTime = modificationTime;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public int getReplication() {
        return replication;
    }

    public void setReplication(int replication) {
        this.replication = replication;
    }


}
