package actuator;

import com.google.gson.*;
import net.dongliu.requests.Cookie;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

class MyRuns implements Runnable {
    private boolean isLastRequest;
    private boolean needResponse;
    private String runtimeCaseId;
    private String runtimeStepId;
    private String method;
    private String url;
    private String retry;
    private String caseId="";
    private String stepId="";
    private String subStepId="";
    private String body;
    private String taskId;
    private JsonObject jo1;
    private JsonParser jp = new JsonParser();
    KafkaProducer<String, String> producer;
    SendHttpResponse resultstr =new SendHttpResponse();
    private Map<String, String> map = new HashMap<String, String>();
    //private Map<String, String> cookies = new HashMap<String, String>();
    private Map<String, String> cookiesmap = new HashMap<String, String>();
    private Long totalConsumedTime = null;
    private String resultStarget="";
    JsonArray assertkey;


    JSONObject jr = new JSONObject();

    public MyRuns(String jj, KafkaProducer<String, String> producer) {

        Log4jUtil.info("request" + jj);

        try {
            jo1 = jp.parse(jj).getAsJsonObject();
        }catch (Exception e){
            resultstr.setIsSuccess(false);
            resultstr.seterror(e.getLocalizedMessage());
            e.printStackTrace();
            return;
        }

        if (!jo1.get("body").isJsonNull()){
            body = jo1.get("body").getAsJsonObject().toString();
            Log4jUtil.info("body" + body);

        }

        url = jo1.get("url").getAsString();
        method = jo1.get("method").getAsString();
        retry = jo1.get("retry").getAsString();
        caseId = jo1.get("caseId").getAsString();
        stepId = jo1.get("stepId").getAsString();

        try {
            subStepId = jo1.get("subStepId").getAsString();
        }
        catch (Exception e){
            subStepId =null;
        }

        taskId = jo1.get("taskId").getAsString();
        runtimeCaseId = jo1.get("runtimeCaseId").getAsString();
        runtimeStepId = jo1.get("runtimeStepId").getAsString();
        isLastRequest = jo1.get("isLastRequest").getAsBoolean();
        needResponse=jo1.get("needResponse").getAsBoolean();
        taskId = jo1.get("taskId").getAsString();

        Gson gson = new Gson();
        this.map = gson.fromJson(jo1.getAsJsonObject("headers"), HashMap.class);

        JsonArray cookies = jo1.get("cookies").getAsJsonArray();
        //JsonArray cookies = new JsonArray();

        for(int i=0;i<cookies.size();i++){
            cookiesmap.put(cookies.get(i).getAsJsonObject().get("name").getAsString(), cookies.get(i).getAsJsonObject().get("value").getAsString());

            Log4jUtil.info(cookies.get(i).getAsJsonObject().get("name").getAsString());
            Log4jUtil.info(cookies.get(i).getAsJsonObject().get("value").getAsString());
        }

            //request方法不允许map为空
        if(map.isEmpty()){
            map.put("test","aikucun");
        }

        if(cookiesmap.isEmpty()){
            cookiesmap.put("test","aikucun");
        }

        Log4jUtil.info("you====" + this.map.toString());
        this.producer=producer;
    }

    public void run() {

        int forretry=Integer.parseInt(retry) + 1;

            for(int i=0;i<forretry;i++) {

                    try {
                        sHttp();
                    } catch (Exception e) {
                        resultstr.setIsSuccess(false);
                        resultstr.seterror(e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                    if (resultstr.getIsSuccess() == true) {
                        try {
                            Getparams();
                        } catch (Exception e) {
                            resultStarget="";
                            resultstr.setIsSuccess(false);
                            resultstr.seterror(e.getLocalizedMessage());
                            e.printStackTrace();
                        }

                        if (!resultStarget.equals("") || jo1.getAsJsonObject("vars").keySet().isEmpty()){
                            break;
                        }
                    }

            }



        try {

            if (resultstr.getResponse() != null) {
                if (needResponse == true) {
                    resultstr.setiresponse(resultstr.getResponse().getBody());
                } else {
                    resultstr.setiresponse("");
                }

                if (resultstr.getResponse().getStatusCode() != 200) {
                    resultstr.setIsSuccess(false);
                }
            }else {
                resultstr.setIsSuccess(false);
            }

            //断言
            assertkey = jo1.get("asserts").getAsJsonArray();
            //Log4jUtil.info("断言:" + assertkey.getAsString());
            if (assertkey.size() > 0) {
                for (int i = 0; i < assertkey.size(); i++) {
                    if (!resultstr.getResponse().getBody().contains(assertkey.get(i).getAsString())) {
                        resultstr.setIsSuccess(false);
                        resultstr.seterror("断言返回报文无此key:" + assertkey.get(i).getAsString());
                        break;
                    }
                }
            }

            //打印http状态码
            Log4jUtil.info(String.valueOf(resultstr.getResponse().getStatusCode()));
            //如果http状态码是400就没有任何返回提示错误信息
            if(resultstr.getResponse().getStatusCode() == 400){
                resultstr.seterror("http 400");
            }

        }catch (Exception e){
            resultStarget="";
            resultstr.setIsSuccess(false);
            //resultstr.seterror(e.getLocalizedMessage());
            e.printStackTrace();
        }

        //请求结束清空map以便下次使用
        //map.clear();
        JSONObject json = new JSONObject();
        try {
            json.put("caseId",caseId);
            json.put("isLastRequest",isLastRequest);
            json.put("stepId",stepId);
            json.put("subStepId",subStepId);
            if (totalConsumedTime== null){
                json.put("responseTime",0);
            }else{
                json.put("responseTime",Integer.parseInt(totalConsumedTime.toString()));
            }
            json.put("taskId",taskId);
            json.put("isSuccess",resultstr.getIsSuccess());
            json.put("runtimeCaseId",runtimeCaseId);
            json.put("runtimeStepId",runtimeStepId);
            json.put("error",resultstr.geterror());
            json.put("response",resultstr.getiresponse());
            json.put("vars",jr);
            if (resultstr.getCookies()==null){
                json.put("cookies",new JSONArray());
            }
            else{
                json.put("cookies",resultstr.getCookies());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        producer.send(new ProducerRecord<String, String>("responsehttp", "zhongwen", json.toString()));
        //producer.flush();
        Log4jUtil.info(json.toString());
    }

    public void sHttp() throws Exception{
        Long beginTime = new Date().getTime();
        Log4jUtil.info("you sHttp====" + this.map.toString());
        resultstr = SendHttp.SendHttp(this.body,this.url, this.method,this.map,this.cookiesmap);

        //存储cookies
        if (resultstr.getResponse().getCookies().size()!=0){

            List<Cookie> result = resultstr.getResponse().getCookies();
//            List<JSONObject> CookiesList=new ArrayList();
            JSONArray CookiesList = new JSONArray();
            for(Cookie c : result) {
                JSONObject json = new JSONObject();
                json.put("domain", c.getDomain());
                json.put("path", c.getPath());
                json.put("name", c.getName());
                json.put("value", c.getValue());
                json.put("expiry", c.getExpiry());
                json.put("secure", c.isSecure());
                json.put("hostOnly", c.isHostOnly());
//                CookiesList.add(json);
                CookiesList.put(json);
            }
            resultstr.setCookies(CookiesList);
        }
        //返回接口响应时间
        totalConsumedTime=new Date().getTime()-beginTime;
    }

    public void Getparams() throws Exception{
        GetAnalysisParameter  GetParameter  = new GetAnalysisParameter();

        if (!jo1.getAsJsonObject("vars").keySet().isEmpty()) {
            for (Map.Entry<String, JsonElement> varkey : jo1.getAsJsonObject("vars").entrySet()) {

                //打印解析表达式
                Log4jUtil.debug(varkey.getValue().toString());

                resultStarget = GetParameter.GetJson(resultstr.getResponse().getBody(), varkey.getValue().getAsString());
                //打印解析结果
                Log4jUtil.debug(resultStarget);

                //重试解析不到内容提示错误信息
                if (!resultstr.getResponse().getBody().contains(resultStarget) || resultStarget.equals("")) {
                    resultstr.setIsSuccess(false);
                    resultstr.seterror("提取变量返回报文无此key:" + varkey.getValue());
                    break;
                }

                //要返回的解析内容如果是对象直接返回,如果是字符串替换双引号
                if (GetParameter.isExits(resultStarget,"{") == "1"){
                    jr.put(varkey.getKey(), resultStarget);
                }else{
                    jr.put(varkey.getKey(), resultStarget.replace("\"",""));
                }

            }
        }

    }

}
