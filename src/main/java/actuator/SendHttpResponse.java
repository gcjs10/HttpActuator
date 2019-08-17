package actuator;
import net.dongliu.requests.Response;
import org.json.JSONArray;

import java.util.List;

public class SendHttpResponse {
    private String error="";
    private String iresponse="";
    private boolean issuccess=true;
    private Response<String> response=null;
    private JSONArray cookies=null;

    public String geterror() {
        return error;
    }

    public void seterror(String error) {
        this.error =error;
    }

    public String getiresponse() {
        return iresponse;
    }

    public void setiresponse(String iresponse) {
        this.iresponse =iresponse;
    }

    public boolean getIsSuccess(){
        return issuccess;
    }

    public void setIsSuccess(boolean issuccess){
        this.issuccess =issuccess;
    }

    public Response<String> getResponse() {
        return response;
    }

    public void setResponse(Response<String> response) {
        this.response =response;
    }

    public void setCookies(JSONArray Cookies) {
        this.cookies = Cookies;
    }

    public JSONArray getCookies() {
        return cookies;
    }

}