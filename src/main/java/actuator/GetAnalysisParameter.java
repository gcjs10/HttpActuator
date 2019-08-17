package actuator;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONObject;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GetAnalysisParameter {


    public ArrayList result = new ArrayList<String>();

    public  String GetJson(String jsonstr, String ii) throws Exception {
        String resrt ="";
        JsonParser json = new JsonParser();
        JsonObject jsonbody = json.parse(jsonstr).getAsJsonObject();
        Log4jUtil.debug(ii);
        String[] strings = ii.split("\\.");
        result = new ArrayList<String>(Arrays.asList(strings));
        try {
            resrt = GetJsonString(jsonbody,result);
        }catch (Exception e){
            throw new Exception(ii + " is not Exit");
        }
        return resrt;
    }


    public String GetJsonString(JsonObject jsonbody, ArrayList jj) throws Exception{

        JsonObject displaystr=null;
        String currentstr=jj.get(0).toString();
        String bracket="";

        if(result.size()>1) {

            if (currentstr.contains("]")){
                bracket = currentstr.substring(0, currentstr.indexOf("["));
            }

            if (!jsonbody.has(bracket)) {
                throw new Exception();
            }

            if (!jsonbody.has(bracket) && !currentstr.contains("]")) {
                throw new Exception();
            }

            //对象的处理方式
            if (isAraryList(currentstr) == "0") {
                displaystr = jsonbody.get(currentstr).getAsJsonObject();
            }

            //获取数组的处理方式
            if (isAraryList(currentstr) == "1") {
                //获取数组序号
                String item=currentstr.substring(currentstr.indexOf("[")+1,currentstr.indexOf("]"));
                //获取数组名称
                String istr = currentstr.substring(0, currentstr.indexOf("["));
                displaystr = jsonbody.get(istr).getAsJsonArray().get(Integer.parseInt(item)).getAsJsonObject();
            }


            //根据条件查询对象
            if (isAraryList(currentstr) =="2") {
                //获取数组序号
                //String item = GetParameter.getMatcher("\\[(.+?)\\]",currentstr,1);
                String item=currentstr.substring(currentstr.indexOf("[")+1,currentstr.indexOf("]"));
                //获取数组名称
                bracket = currentstr.substring(0, currentstr.indexOf("["));

                for(int j=0;j<jsonbody.size();j++){
                    displaystr = jsonbody.get(bracket).getAsJsonArray().get(j).getAsJsonObject();
                    if (displaystr.getAsString().replace(" ","").contains(item.replace(" ","").replace("=",":"))){
                        break;
                    }
                }
            }

            result.remove(0);
            return GetJsonString(displaystr, result);
        }else if (result.size()==1 && isAraryList(currentstr) == "1") {
            //获取数组序号
            String item=currentstr.substring(currentstr.indexOf("[")+1,currentstr.indexOf("]"));
            //获取数组名称

            String istr = currentstr.substring(0, currentstr.indexOf("["));

            if (jsonbody.get(istr).getAsJsonArray().get(Integer.parseInt(item)).toString().contains("}")){
                displaystr = jsonbody.get(istr).getAsJsonArray().get(Integer.parseInt(item)).getAsJsonObject();
            }else{
                return jsonbody.get(istr).getAsJsonArray().get(Integer.parseInt(item)).getAsString();
            }
        }
        else if (result.size()==1) {
            Log4jUtil.info("GetJsonString:"+jsonbody.get(currentstr).toString());
            return jsonbody.get(currentstr).toString();
        }

        Log4jUtil.info(displaystr.toString());
        return displaystr.toString();
    }

    //判断需要解析的对象类型
    public String isAraryList(String isArary){
        //判断顺序不可调换否则逻辑出错
       if(isArary.indexOf("=")!=-1){
            return "2";
        }else if(isArary.indexOf("[")!=-1){
            return "1";
        }else{
            return "0";
        }
    }

    //判断是否存在该字符
    public static String isExits(String ExitsStr,String targetStr){
        if(ExitsStr.indexOf(targetStr)!=-1){
            return "1";
        }else{
            return "0";
        }
    }

        /**
      获取字符串中被两个字符（串）包含的所有数据
      str 处理字符串
      start 起始字符（串）
      end 结束字符（串）
      isSpecial 起始和结束字符是否是特殊字符
      Set<String>
      */
        //遍历对象
      public static Set<String> getStrContainData(String str, String start, String end, boolean isSpecial){
          Set<String> result = new HashSet<String>();
          if(isSpecial){
                  start = "\\\\" + start;
                  end = "\\\\" + end;
          }
          String regex = start + "(.*?)" + end;
          Pattern pattern = Pattern.compile(regex);
          Matcher matcher = pattern.matcher(str);
          while(matcher.find()){
                 String key = matcher.group(1);
                 if(!key.contains(start) && !key.contains(end)){
                        result.add(key);
                   }
          }
          return result;
      }

}