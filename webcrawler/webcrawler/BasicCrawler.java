package webcrawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

// -need- to implement visit() and shouldVisit()
public class BasicCrawler extends WebCrawler {

  /*
    private static final Pattern IMAGE_EXTENSIONS = Pattern.compile(".*\\.(bmp|gif|jpg|png)$");
    private static final Pattern CSS_EXTENSIONS = Pattern.compile(".*\\.(css|less|scss)$");
    private static final Pattern JS_EXTENSIONS = Pattern.compile(".*\\.(js|json|xml)$");
  */
    private static final Pattern ALL_EXTENSIONS = Pattern.compile(
    ".*(\\.(" + "css|js|json|webmanifest|ttf|svg|wav|avi|mov|mpeg|mpg|ram|m4v|wma|wmv|mid|txt|mp2|mp3|mp4|zip|rar|gz|exe|ico))$");

  /*********************************************************
    TO - DOs 
  #1 a two column spreadsheet, column 1 containing the URL and
  column 2 containing the HTTP/HTTPS status code received

  #2 column 1 containing the URLs successfully downloaded, 
  column 2 containing the size of the downloaded file 
  column 3 containing the # of outlinks found
  column 4 containing the resulting content-type

  #3 all of the URLs (including repeats) that were discovered
  # a two column spreadsheet where column 1 contains the encountered URL and column two an
  indicator of whether the URL a. resides in the website (OK), or b. points outside of the website
  (N_OK).
  * *********************************************************/

    private String fetchLATimes = ""; 
    private String visitLATimes = ""; 
    private String urlsLATimes = "";  
    private HashSet<String> visitedUrls = new HashSet<>(); 
    private int countVal = 0; 

    @Override
    public Object getMyLocalData() {
      return new String[]{fetchLATimes, visitLATimes, urlsLATimes};
    }

    @Override
    protected void handlePageStatusCode(WebURL url, int statusCode, String statusDescription) {
      String href = url.getURL().toLowerCase().replaceAll(",", "_"); 
      fetchLATimes += href + ", " + statusCode + "\n";
      visitedUrls.add(href);
    }

    /**
     * You should implement this function to specify whether the given url
     * should be crawled or not (based on your crawling logic).
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
      // #3 all of the URLs (including repeats) that were discovered
        String href = url.getURL().toLowerCase().replaceAll(",", "_");
        boolean validDomain = href.startsWith("http://www.latimes.com/") || href.startsWith("https://www.latimes.com/");

        if (validDomain) {
          urlsLATimes += href + ", OK\n";
        }  
        else {
          urlsLATimes += href + ", N_OK\n";
          return false;
        }
        // Ignore the url if it has an extension that matches our defined set of image extensions.
        if (ALL_EXTENSIONS.matcher(href).matches()) {
            //numSeenImages.incrementAndGet();
            return false;
        }

        if (visitedUrls.contains(href)) {
          return false;
        }
        // starts with https://www.latimes.com/ or           http://www.latimes.com/
        // is not a repeating url
        // is not a filter page type
        // is not a page that is not a link
        return true;

    }

    /**
     * This function is called when a page is fetched and ready to be processed
     * by our program.
     */
    @Override
    public void visit(Page page) {
      String url = page.getWebURL().getURL().toLowerCase().replaceAll(",", "_");
      int outboundLinks = 0; 
      int size = page.getContentData().length;
      String contentType = page.getContentType().split(";")[0];
      

      boolean correctFormat = contentType.contains("text") || contentType.contains("html") || contentType.contains("doc") || contentType.contains("pdf") || contentType.contains("image");

      if (!correctFormat)
        return; 

      if (page.getParseData() instanceof HtmlParseData) {
        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        Set<WebURL> links = htmlParseData.getOutgoingUrls();
        outboundLinks += links.size();
      }

      visitLATimes += url + "," + size + "," + outboundLinks + "," + contentType + "\n";

        //countVal++; Need to find a way to count cumulative crawls for each unique crawl
        
        System.out.println("Found: " + page.getWebURL());
        //System.out.println("\n Pages Crawled: " + countVal);
        System.out.println("\n==========================\n");
    }
}
