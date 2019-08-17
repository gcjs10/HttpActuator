package actuator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetParameter {

    public static  String getMatcher(String regex, String source,int sindex) {
        String result = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        for (int i=0;i<sindex;i++){
            matcher.find();
            result = matcher.group();
            //result = matcher.group(1);  可打印具体的值
        }
        return result;
    }

}