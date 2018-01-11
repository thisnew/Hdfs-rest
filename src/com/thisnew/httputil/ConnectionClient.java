package com.thisnew.httputil;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionClient {

    private CloseableHttpClient httpclient;
    private String HOST_URL;
    private ObjectMapper om = new ObjectMapper();


    public ConnectionClient(String url) {
        this.httpclient = HttpClients.createDefault();
        this.HOST_URL = url;
    }

    //http://118.190.85.52:14000/webhdfs/v1/alidata1?op=LISTSTATUS&user.name=admin
     String executeQuary(String wayLink, String type) {
        HttpGet httpget = new HttpGet(HOST_URL + "/webhdfs/v1" + wayLink + "?op=" + type + "&user.name=admin");
        CloseableHttpResponse execute = null;
        try {
            execute = this.httpclient.execute(httpget);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseStatus(execute);
    }



    public String responseStatus(CloseableHttpResponse response) {
        String content = null;
        if (response.getStatusLine().getStatusCode() == 200) {
            try {
                content = EntityUtils.toString(response.getEntity(), "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println(response.getStatusLine().getStatusCode());
        }
        return content;
    }

    public BlockInfo jsonToObject(String json) {
        JsonNode jsonNodes = null;
        JsonNode jsonNodemap = null;
        BlockInfo blockInfo = null;
        try {
            jsonNodes = om.readValue(json, JsonNode.class);
            jsonNodemap = jsonNodes.get("FileStatus");
            blockInfo = om.readValue(jsonNodemap.toString(), BlockInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blockInfo;
    }

    public List<BlockInfo> jsonToList(String json) {

        JsonNode jsonNodes = null;
        List<BlockInfo> beanList = null;
        JsonNode jsonNodemap = null;
        JsonNode jon = null;
        try {
            jsonNodes = this.om.readValue(json, JsonNode.class);
            jsonNodemap = jsonNodes.get("FileStatuses");
            jon = jsonNodemap.get("FileStatus");
            beanList = this.om.readValue(jon.toString(), new TypeReference<List<BlockInfo>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return beanList;
    }


    public void start() {
    }

    public void close() {
        try {
            httpclient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void delete(String getpath) {
        System.out.println(getpath);
    }

    public void create(String getpath) {
        System.out.println(getpath);
    }


    public List<String> getChildren(String filePath) {
        //test
        System.out.println("getChildren 的path: " + filePath);

        String queryRes = executeQuary(filePath, "LISTSTATUS");
        List<BlockInfo> blockInfo=jsonToList(queryRes);

        List<String> list = new ArrayList<String>();
        for (BlockInfo bi : blockInfo) {
            list.add(bi.getPathSuffix());
            System.out.println("getChildren 的:"+bi.getPathSuffix());
        }

        return list;
    }


    public BlockInfo forpath(String path) {

        System.out.println("forpath 的path : " + path);


        String queryResSim = executeQuary(path, "GETFILESTATUS");
        BlockInfo blockInfo = jsonToObject(queryResSim);
        blockInfo.setPathSuffix(path);

        String queryResLis = executeQuary(path, "LISTSTATUS");
        List<BlockInfo> beanList = jsonToList(queryResLis);
        blockInfo.setNumChildren(beanList.size());

        System.out.println("forpath 的 : " + beanList.toString());
        return blockInfo;
    }

    public String ForTestExecuteQuary() {
        String statusNum= null;
        HttpGet httpget = new HttpGet(HOST_URL + "/webhdfs/v1/?op=LISTSTATUS&user.name=admin");
        CloseableHttpResponse execute = null;
        try {
            execute = this.httpclient.execute(httpget);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getCause().getMessage();
        }
        statusNum =""+execute.getStatusLine().getStatusCode();
        return statusNum;
    }

    public boolean isKerberos(){
        return false;
    }
    public boolean isConnection(){
        return false;
    }
    public boolean isLegal(){
        return false;

    }

}
