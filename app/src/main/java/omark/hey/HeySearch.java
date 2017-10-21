package omark.hey;

public class HeySearch {
    static String DEFAULT = "https://bing.com/search?q=";
    
    public static String getSearch(String body) {
        return S.get("search" , HeySearch.DEFAULT) + body;
    }
}
