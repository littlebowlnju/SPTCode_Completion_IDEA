import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementWeigher;

public class SPTWeigher extends LookupElementWeigher {
    SPTWeigher() {
        super("SPTWeigher", false, false);
    }

    @Override
    public Integer weigh(LookupElement element) {
        if (element instanceof SPTLookupElement) {
            return ((SPTLookupElement) element).index;
        }
        return Integer.MAX_VALUE;
    }
}
