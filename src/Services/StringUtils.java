package Services;

import org.davidmoten.text.utils.WordWrap;

public class StringUtils {
    public static String wrapText(String text){
        return WordWrap.from(text).maxWidth(40).insertHyphens(true).wrap();
    }

    public static String applyWrapForButton(String text) {
        return "<html>" + wrapText(text).replace("\n","<br/>") + "</html>";
    }
}
