import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import org.jetbrains.annotations.NotNull;

public class SPTLookupElement extends LookupElement {
    public final String code;
    public final String score;
    public final int index;

    public SPTLookupElement(int index, String code, String score) {
        this.index = index;
        this.code = code;
        this.score = score;
    }

    @NotNull
    @Override
    public String getLookupString() {
        return this.code;
    }

    @Override
    public void handleInsert(InsertionContext context) {
        int begin = context.getStartOffset();
        // delete trigger word "??"
        context.getDocument().deleteString(begin-2, begin);
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        presentation.setItemText(this.code);
        presentation.setItemTextBold(true);
        presentation.setTypeText(this.score);
    }
}
