package actuator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dongliu.requests.Proxies;
import net.dongliu.requests.Requests;
import net.dongliu.requests.Response;
import net.dongliu.requests.body.Part;
import org.apache.commons.lang.ObjectUtils;

import java.io.*;
import java.util.*;


public class SendHttp {

    public static SendHttpResponse SendHttp(String body, String url, String method, Map<String, String> map, Map<String, String> cookies) throws Exception{

        SendHttpResponse response = new SendHttpResponse();
        String isupload="";

        JsonParser fjson = new JsonParser();
        try {
            isupload = fjson.parse(body).getAsJsonObject().get("type").getAsString();
        }catch (Exception e){
            isupload="json";
        }


        body = fjson.parse(body).getAsJsonObject().toString();

        //上传文件
        if(isupload.equals("form")){

            int size = fjson.parse(body).getAsJsonObject().get("val").getAsJsonArray().size();
            ArrayList<Part<?>> list = new ArrayList();
            //循环上传多个附件
            for (int f=0;f<size;f++){
                JsonObject fjsonbody = fjson.parse(body).getAsJsonObject().get("val").getAsJsonArray().get(f).getAsJsonObject();

                if(fjsonbody.get("type").getAsString().equals("file")){
                   String file = fjsonbody.get("val").getAsString();
                    file = file.substring(file.lastIndexOf("/")+1);
                    InputStream ss = new BufferedInputStream(Requests.get(fjsonbody.get("val").getAsString()).send().getInput());
                    list.add(Part.file(fjsonbody.get("key").getAsString(),file,ss));
                }else{
                    list.add(Part.text(fjsonbody.get("key").getAsString(),fjsonbody.get("val").getAsString()));
                }
            }

            response.setResponse(SendHttp.UploadHttp(url,list,method,map,cookies));
            Log4jUtil.info("upload");
            Log4jUtil.info(response.getResponse().toString());
        }


        if (method.equals("POST") && isupload.equals("json"))
        {
            response.setResponse(SendHttp.SendPostHttp(fjson.parse(body).getAsJsonObject().get("val").getAsString(),url,map,cookies));
            Log4jUtil.info("post");
            Log4jUtil.info("response.getResponse()");
            //Log4jUtil.info(fjson.parse(body).getAsJsonObject().get("val").getAsString());
        }

        if (method.equals("GET") && isupload.equals("json"))
        {
            response.setResponse(SendHttp.SendGetHttp(url,map,cookies));
            Log4jUtil.info("get");
            Log4jUtil.info("response.getResponse()");

        }

        if (method.equals("PUT") && isupload.equals("json"))
        {
            response.setResponse(SendHttp.SendPutHttp(fjson.parse(body).getAsJsonObject().get("val").getAsString(),url,map,cookies));
            Log4jUtil.info("PUT");
            Log4jUtil.info("response.getResponse()");
            Log4jUtil.info(fjson.parse(body).getAsJsonObject().get("val").getAsString());
        }

        if (method.equals("DElETE"))
        {
            response.setResponse(SendHttp.SendDeleteHttp(fjson.parse(body).getAsJsonObject().get("val").getAsString(),url,map,cookies));
            Log4jUtil.info("DElETE");
            Log4jUtil.info("response.getResponse()");
            Log4jUtil.info(response.getResponse().toString());
        }

        if (method.equals("HEAD"))
        {
            response.setResponse(SendHttp.SendHeadHttp(url,map,cookies));
            Log4jUtil.info("HEAD");
            Log4jUtil.info("response.getResponse()");
            Log4jUtil.info(response.getResponse().toString());
        }

        //返回接口响应时间
        return response;
    }

    public static Response<String> UploadHttp(String url, ArrayList<Part<?>> filelist,String method,Map<String, String> map,Map<String, String> cookies) throws Exception{

        if(method.equals("POST")) {
            //Response<String> resp2 = Requests.post(url).multiPartBody(Part.file("file",new File("F:\\test.csv")), Part.text("scriptid","5cef3a2b2aeada3eeaa8d592"), Part.text("size","3")).send().toTextResponse();
            Response<String> resp = Requests.post(url).multiPartBody(filelist).headers(map).cookies(cookies).send().toTextResponse();
            return resp;
        }

        if(method.equals("PUT")) {
            //Response<String> resp2 = Requests.post(url).multiPartBody(Part.file("file",new File("F:\\test.csv")), Part.text("scriptid","5cef3a2b2aeada3eeaa8d592"), Part.text("size","3")).send().toTextResponse();
            Response<String> resp = Requests.put(url).multiPartBody(filelist).headers(map).cookies(cookies).send().toTextResponse();
            return resp;
        }
        return null;
    }

    public static Response<String> SendPostHttp(String body, String url, Map<String, String> map, Map<String, String> cookies) throws Exception{
        Log4jUtil.info("map");
        Log4jUtil.info(map.toString());
        Response<String> resp = Requests.post(url).headers(map).cookies(cookies).body(body).send().toTextResponse();
        //Response<String> resp = Requests.post(url).headers(map).cookies(cookies).body(body).proxy(Proxies.httpProxy("127.0.0.1", 8889)).send().toTextResponse();
        //Log4jUtil.info(resp.getBody());
        return resp;
    }

    public static Response<String> SendGetHttp(String url, Map<String, String> map,Map<String, String> cookies) throws Exception{
        Response<String> resp = Requests.get(url).headers(map).cookies(cookies).send().toTextResponse();
        System.out.println(resp.getCookies());
        return resp;
    }

    public static Response<String> SendPutHttp(String body, String url, Map<String, String> map ,Map<String, String> cookies) throws Exception{
        //Response<String> resp = Requests.put(url).headers(map).body(body).proxy(Proxies.httpProxy("127.0.0.1", 8888)).send().toTextResponse();
        Response<String> resp = Requests.put(url).headers(map).cookies(cookies).body(body).send().toTextResponse();
        return resp;
    }

    public static Response<String> SendHeadHttp(String url, Map<String, String> map,Map<String, String> cookies) throws Exception{
        Response<String> resp = Requests.head(url).headers(map).cookies(cookies).send().toTextResponse();
        return resp;
    }

    public static Response<String> SendDeleteHttp(String body, String url, Map<String, String> map, Map<String, String> cookies) throws Exception{
        Response<String> resp = Requests.delete(url).headers(map).cookies(cookies).body(body).send().toTextResponse();
        return resp;
    }

}

