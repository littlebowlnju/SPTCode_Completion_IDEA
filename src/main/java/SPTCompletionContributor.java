import code_service.Code2APISeq;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.ide.ui.EditorOptionsTopHitProvider;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class SPTCompletionContributor extends CompletionContributor{

    private static final int ADVERTISEMENT_MAX_LENGTH = 100;
    private static int index = 0;

//    public AutocompleteResponse  retrieveCompletions(CompletionParameters parameters, int max_num_results){
//        try {
//            final int MAX_OFFSET = 100000; // 100 KB
//            Document doc = parameters.getEditor().getDocument();
//            int middle = parameters.getOffset();
//            // find the method block
//            int begin = Integer.max(0, middle - MAX_OFFSET);
//            int end = Integer.min(doc.getTextLength(), middle + MAX_OFFSET);
//            AutocompleteRequest req = new AutocompleteRequest();
//            req.before = doc.getText(new TextRange(begin, middle));
//            req.after = doc.getText(new TextRange(middle, end));
//            req.filename = parameters.getOriginalFile().getVirtualFile().getPath();
//            req.max_num_results = max_num_results;
//            req.region_includes_beginning = (begin == 0);
//            req.region_includes_end = (end == doc.getTextLength());
//
//            AutocompleteResponse autocompleteResponse = new AutocompleteResponse();
//            autocompleteResponse.old_prefix = "";
//            autocompleteResponse.user_message = new String[0];
//
//            String source = "";
//
////            String code = "";
////            if( req.after.startsWith("\n")){
////                code = req.before + "HOLE();" + req.after;
////            }else{
////                code = req.before + "HOLE()" + req.after;
////            }
////            List<String> apis = Code2APISeq.deal_with_code_file(code);
////
////            String un_complete_func = "";
////            for(int i=0; i<apis.size(); i++){
////                if (apis.get(i).contains("HOLE")){
////                    un_complete_func = apis.get(i);
////                    break;
////                }
////            }
////
////            Map<String,Object> map = new HashMap<>();
////            map.put("username", un_complete_func);
////            JSONObject response = doPost("http://127.0.0.1:5000/complete/", map);
////            if(response == null){
////                autocompleteResponse.results = new ResultEntry[0];
////                return autocompleteResponse;
////            }
//            List<String> codes = null;
//            List<Float> scores = null;
//            TestEntity testEntity = ExampleTest.getExamples(index);
//            int seconds = new Random().nextInt(600 - 400) + 401;
//            Thread.sleep(seconds);
//            codes = testEntity.getCodes();
//            scores = testEntity.getScores();
//
//            index += 1;
//            if(index > 13){index = 0;}
//
//            //JSONArray results = response.getJSONArray("results");
//
//            List<ResultEntry> methods = new ArrayList<>();
//
//            for(int i=0; i<codes.size(); i++){
////                JSONObject result = code.getJSONObject(i);
////                String methodName = result.getString("API");
////                Float score = result.getFloat("score");
////
//                ResultEntry resultEntry = new ResultEntry();
//                resultEntry.new_prefix = i + 1+ ".  " +codes.get(i);
//                resultEntry.detail = String.format("%.0f", (scores.get(i) * 100)) + "%";
//                resultEntry.old_suffix = "";
//                resultEntry.new_suffix = "";
//                resultEntry.score = scores.get(i);
//
//                methods.add(resultEntry);
//            }
//
//            autocompleteResponse.results = methods.toArray(new ResultEntry[methods.size()]);
//
//            return autocompleteResponse;
//        }catch (Exception e){
//            System.out.println(e);
//            return null;
//        }
//    }

    public ArrayList<LookupElement> retrieveCompletions(CompletionParameters parameters, int maxCompletions) {
        ArrayList<LookupElement> completions = new ArrayList<>();
        PsiElement element = parameters.getPosition();
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        String source = containingMethod.getText();
        source = source.replace("??IntellijIdeaRulezzz", "PRED");
        System.out.println(source);

        String[] predictions = new String[] {
                "int a = 1",
                "a = a + b",
                "a = a + a",
                "int b = 1",
                "b = b - 1"
        };
        String[] prediction_scores = new String[]{
                "44.96%", "25.78%", "19.99%", "17.22%", "11.56%"
        };
        for (int i = 0; i < Math.max(5, maxCompletions); ++i) {
            SPTLookupElement completion = new SPTLookupElement(
                    i,
                    predictions[i],
                    prediction_scores[i]
            );
            completions.add(completion);
        }
        return completions;
    }


    @Override
    public void fillCompletionVariants(CompletionParameters parameters, CompletionResultSet result) {
        System.out.println(parameters.getOffset());
        boolean trigger = endsWith(
                parameters.getEditor().getDocument(),
                parameters.getOffset() - result.getPrefixMatcher().getPrefix().length(),
                "??");

//        System.out.println(trigger);

        if (!trigger) {
            return;
        }

        int baseMaxCompletions = 5;
//        AutocompleteResponse completions = this.retrieveCompletions(parameters, baseMaxCompletions);
        ArrayList<LookupElement> completions = this.retrieveCompletions(parameters, baseMaxCompletions);
        PrefixMatcher originalMatcher = result.getPrefixMatcher();
        if (completions != null) {
            result.withRelevanceSorter(CompletionSorter.defaultSorter(parameters, originalMatcher).weigh(new SPTWeigher()));
//            result = result.withPrefixMatcher(new MyPrefixMatcher(originalMatcher.cloneWithPrefix("")));
//            result = result.withRelevanceSorter(CompletionSorter.defaultSorter(parameters, originalMatcher).weigh(new MyWeigher()));
            //result.restartCompletionOnAnyPrefixChange();

//            if (completions.user_message.length >= 1) {
//                String firstMsg = completions.user_message[0];
//                if (firstMsg.length() <= ADVERTISEMENT_MAX_LENGTH) {
//                    result.addLookupAdvertisement(firstMsg);
//                }
//            }
//            if (originalMatcher.getPrefix().length() == 0 && completions.results.length == 0) {
//                result.stopHere();
//                return;
//            }
//            ArrayList<LookupElement> elements = new ArrayList<>();
//            int maxCompletions = trigger ? 10 : baseMaxCompletions;
//            for (int i = 0; i < completions.results.length && i < maxCompletions; i++) {
//                MyLookUpElement elt = new MyLookUpElement(
//                        i,
//                        completions.old_prefix,
//                        completions.results[i].new_prefix,
//                        completions.results[i].old_suffix,
//                        completions.results[i].new_suffix,
//                        completions.results[i].score
//                );
//                elt.copyLspFrom(completions.results[i]);
//
//                if (result.getPrefixMatcher().prefixMatches(elt)) {
//                    elements.add(elt);
//                }
//            }
            result.addAllElements(completions);
        }
    }

    static boolean endsWith(Document doc, int pos, String s) {
        int begin = pos - s.length();
        if (begin < 0 || pos > doc.getTextLength()) {
            return false;
        } else {
            String tail = doc.getText(new TextRange(begin, pos));
            return tail.equals(s);
        }
    }



    @Override
    public boolean invokeAutoPopup(@NotNull PsiElement position, char typeChar) {
        return true;
    }
}
