package webcrawler;



import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) throws Exception {
        // Initialize crawler configuration
        CrawlConfig config = new CrawlConfig();
        // Set the folder where intermediate crawl data is stored (e.g. list of urls that are extracted from previously
        // fetched pages and need to be crawled later).
        config.setCrawlStorageFolder("data");
        // Be polite: Make sure that we don't send more than 1 request per second (1000 milliseconds between requests).
        // Otherwise it may overload the target servers.
        config.setPolitenessDelay(50);
        // You can set the maximum crawl depth here. The default value is -1 for unlimited depth.
        config.setMaxDepthOfCrawling(6);
        // You can set the maximum number of pages to crawl. The default value is -1 for unlimited number of pages.
        config.setMaxPagesToFetch(50);
        // Should binary data should also be crawled? example: the contents of pdf, or the metadata of images etc
        config.setIncludeBinaryContentInCrawling(true);
        // This config parameter can be used to set your crawl to be resumable
        // (meaning that you can resume the crawl from a previously
        // interrupted/crashed crawl). Note: if you enable resuming feature and
        // want to start a fresh crawl, you need to delete the contents of
        // rootFolder manually.
        config.setResumableCrawling(false);
        // Set user agent for HTTP requests
        // Set this to true if you want crawling to stop whenever an unexpected error
        // occurs. You'll probably want this set to true when you first start testing
        // your crawler, and then set to false once you're ready to let the crawler run
        // for a long time.
        //config.setHaltOnError(true);
        config.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");

        // Initialize crawl controller
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

      /*
       * For each crawl, you need to add some seed urls. These are the first
       * URLs that are fetched and then the crawler starts following links
       * which are found in these pages
       */
        controller.addSeed("https://www.latimes.com");

        // Number of threads to use during crawling. Increasing this typically makes crawling faster. But crawling
        // speed depends on many other factors as well. You can experiment with this to figure out what number of
        // threads works best for you.
        int numberofcrawlers = 4;
      /*
       * Start the crawl. This is a blocking operation, meaning that your code
       * will reach the line after this only when crawling is finished.
       */
        controller.start(BasicCrawler.class, numberofcrawlers);
        // controller.startNonBlocking(BasicCrawler.class, numberofcrawlers);

        //controller.waitUntilComplete();
        // Write results to CSV files
        StringBuilder fetchResult = new StringBuilder("URL,Status\n");
        StringBuilder visitResult = new StringBuilder("URL,Size,Outgoing Links,Content Type\n");
        StringBuilder urlsResult = new StringBuilder("URL,Status\n");

        for (Object localData : controller.getCrawlersLocalData()) {
            String[] tasks = (String[]) localData;
            fetchResult.append(tasks[0]);
            visitResult.append(tasks[1]);
            urlsResult.append(tasks[2]);
        }

        writeCSV(fetchResult, "test_fetch_latimes.csv");
        writeCSV(visitResult, "test_visit_latimes.csv");
        writeCSV(urlsResult, "test_urls_latimes.csv");

        System.out.println("==============================");
        System.out.println("Crawling Complete");
    }

    private static void writeCSV(StringBuilder output, String fileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(fileName, StandardCharsets.UTF_8)) {
            writer.println(output.toString().trim());
            writer.flush();
            writer.close();
        }
    }
}
