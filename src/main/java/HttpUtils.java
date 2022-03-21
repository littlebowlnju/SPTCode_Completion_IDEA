import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtils {
    static final String url = "http://127.0.0.1:8000/";
    static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static JSONObject doPost(String source) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        // post source code
//        if (source!=null && source != "") {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("data", source));
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "UTF-8");
        httpPost.setEntity(formEntity);
//        System.out.println(formEntity);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
//        }
//        if (map != null) {
//            // 声明存放参数的List集合
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//
//            // 遍历map，设置参数到list中
//            for (Map.Entry<String, Object> entry : map.entrySet()) {
//                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
//            }
//
//            // 创建form表单对象
//            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "utf-8");
//            // 把表单对象设置到httpPost中
//            httpPost.setEntity(formEntity);
//        }
//        httpPost.setEntity(new StringEntity(source));
        System.out.println(httpPost.getConfig());
        System.out.println(httpPost.getURI());
        // 使用HttpClient发起请求，返回response
        HttpResponse response = httpClient.execute(httpPost);
        System.out.println(response.getStatusLine());
        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode >= 200 && statusCode < 300){
            HttpEntity entity = response.getEntity();
            String results = EntityUtils.toString(entity,"UTF-8");
            System.out.println(results);
            try {
                JSONObject jsonObject = JSONObject.parseObject(results);
                return jsonObject;
            }catch (Exception e){
                return null;
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        String testSource = "public int[] twoSum(int[] nums, int target) {" +
        "PRED " +
        "for (int i = 0; i < n; ++i) {" +
        "for (int j = i + 1; j < n; ++j) {" +
        "if (nums[i] + nums[j] == target) {" +
        "return new int[]{i, j};}}}" +
        "return new int[0];}";
        JSONObject object = doPost(testSource);
    }
}
