package com.parser.processors;

import com.parser.bo.NewsFeedItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsFeedProcessor extends Processor {


    @Override
    public void process(InputStream stream) throws Exception{
        //html parsing is awful
        String html = getStringResponse(stream);

    }

    private String getStringResponse(InputStream stream) throws  Exception{
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        stream));
        String s;
        while ((s = in.readLine()) != null){
            sb.append(s);
        }
        return sb.toString();
    }

    private void parseResponse(String response){
        //[&hellip;]
        Pattern notePattern = Pattern.compile("\"item entry\".*?hellip;");
        Pattern headerPattern = Pattern.compile("<h3>.*?</h3>");
        Pattern titlePattern = Pattern.compile("\"bookmark\">.*?</a>");
        Pattern linkPattern = Pattern.compile("href=\".*?\"");
        Pattern textPattern = Pattern.compile("class=\"itemtext\">.*?</div>");
        Pattern datePattern = Pattern.compile("</strong>.*?<br");
        Pattern authorPattern = Pattern.compile("Автор:.*?</strong>");

        Matcher noteMatcher = notePattern.matcher(response);
        while (noteMatcher.find()){
            NewsFeedItem newsNote = new NewsFeedItem();
            String note = noteMatcher.group();

            Matcher headerMatcher = headerPattern.matcher(note);
            if (headerMatcher.find()){
                String header = headerMatcher.group();
                Matcher titleMatcher = titlePattern.matcher(header);
                if (titleMatcher.find()){
                    newsNote.setTitle(titleMatcher.group());
                }
                Matcher linkMatcher =  linkPattern.matcher(header);
                if (linkMatcher.find()){
                    newsNote.setUrl(linkMatcher.group());
                }
            }



            newsNote.setAuthor();

        }
    }
}
