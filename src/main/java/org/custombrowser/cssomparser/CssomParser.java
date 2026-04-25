package org.custombrowser.cssomparser;

import com.helger.css.ECSSVersion;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.decl.CSSStyleRule;
import com.helger.css.reader.CSSReader;
import com.helger.css.writer.CSSWriterSettings;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class CssomParser {

    public List<CascadingStyleSheet> parse(Document dom) {
        if (dom == null) {
            System.err.println("Error: Document is empty.");
            return List.of(); // return empty list, not null (standard Java convention)
        }

        System.out.println("Building the CSSOM tree...");
        List<CascadingStyleSheet> cssom = new ArrayList<>();
        
        Elements styleElements = dom.select("style"); // selects all the <style> blocks in the html & returns an arraylist

        System.out.println("Found " + styleElements.size() + " style blocks in this page");

        CSSWriterSettings writerSettings = new CSSWriterSettings(ECSSVersion.CSS30);

        for (Element styE : styleElements) {
            String cssContent = styE.html();
            CascadingStyleSheet css = CSSReader.readFromString(cssContent, ECSSVersion.CSS30); // parses the raw css string & builds the CSSOM tree
            
            if (css != null) {
                cssom.add(css);
                
                // optional code for testing (prints all CSS rule to terminal)
                // System.out.println("Found " + css.getStyleRuleCount() + " number of CSS rules in this block");
                // System.out.println("Listing them below...");
                
                // for (CSSStyleRule rule : css.getAllStyleRules()) { // iterates over every CSS rule inside the block
                //     System.out.println(rule.getAsCSSString(writerSettings, 0));
                // }
            }
        }
        
        return cssom;
    }

}
