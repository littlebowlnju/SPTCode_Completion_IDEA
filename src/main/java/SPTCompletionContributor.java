import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class SPTCompletionContributor extends CompletionContributor{

    public ArrayList<LookupElement> retrieveCompletions(CompletionParameters parameters, int maxCompletions) throws Exception {
        ArrayList<LookupElement> completions = new ArrayList<>();
        PsiElement element = parameters.getPosition();
        PsiMethod containingMethod = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        String source = containingMethod.getText();
        source = source.replace("??IntellijIdeaRulezzz", "PRED");
        System.out.println(source);
        JSONObject predictionObject = HttpUtils.doPost(source);
        JSONArray predictions = (JSONArray) predictionObject.get("predictions");
        JSONArray predictionScores = (JSONArray) predictionObject.get("prediction_scores");
//        String[] predictions = new String[] {
//                "int a = 1",
//                "a = a + b",
//                "a = a + a",
//                "int b = 1",
//                "b = b - 1"
//        };
//        String[] prediction_scores = new String[]{
//                "44.96%", "25.78%", "19.99%", "17.22%", "11.56%"
//        };
        for (int i = 0; i < Math.max(5, maxCompletions); ++i) {
            SPTLookupElement completion = new SPTLookupElement(
                    i,
                    (String)predictions.get(i),
                    (String)predictionScores.get(i)
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

        if (!trigger) {
            return;
        }

        int baseMaxCompletions = 5;
        ArrayList<LookupElement> completions = null;
        try {
            completions = this.retrieveCompletions(parameters, baseMaxCompletions);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
