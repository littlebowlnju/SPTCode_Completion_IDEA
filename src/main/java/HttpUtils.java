import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpUtils {
    static final String url = "http://127.0.0.1:8000";
    static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static JSONObject doPost(String source) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        // post source code
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setEntity(new StringEntity(source, "utf-8"));
//        httpPost.addHeader("Accept", "application/json");
        // 使用HttpClient发起请求，返回response
        HttpResponse response = httpClient.execute(httpPost);
        System.out.println(response.getStatusLine());
        int statusCode = response.getStatusLine().getStatusCode();
        if(statusCode >= 200 && statusCode < 300){
            HttpEntity entity = response.getEntity();
            String results = EntityUtils.toString(entity,"UTF-8");
            try {
                return JSONObject.parseObject(results);
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
        System.out.println(object);
    }
}
