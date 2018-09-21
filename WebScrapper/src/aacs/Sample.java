package aacs;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public class Sample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
       JSONArray finalArray = new JSONArray();
		
		String chromeDriverPath = "/usr/local/bin/chromedriver" ;  
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);  
		ChromeOptions options = new ChromeOptions();  
		//options.addArguments("--headless", "--disable-gpu");  
		WebDriver driver = new ChromeDriver(options);  
		
		//Load Zomato India Page   ?all=1&page=2
		try {
	    driver.get("https://www.zomato.com/pune/baner-restaurants?all=1");
	    Document doc = Jsoup.parse(driver.getPageSource());
	    Element pagesTotal = doc.select("div.pagination-number div b").last();
	    int totalPages = Integer.parseInt(pagesTotal.html());
	    JSONArray restaurantsArray = new JSONArray();
	    for (int i=0; i<totalPages; i++) {
	    	Elements resNames = doc.select("a.result-title");
	    	Elements resRatings = doc.select("div.rating-popup");
	    	Elements resAddresses = doc.select("div.search-result-address");
	    	Elements resCosts = doc.select("div.res-cost span.pl0");
	    	//resNames.get(1).html();
	    	int namesSize = resNames.size();
	    	System.out.println(namesSize);
	    	/*for (int a = 0; a < namesSize; a++) {
	    		JSONObject restaurantObj = new JSONObject();
	    		restaurantObj.put("resName", resNames.get(i).html());
	    		restaurantObj.put("resUrl", resNames.get(i).attr("href"));
	    		restaurantObj.put("resRating", resRatings.get(i).html());
	    		restaurantObj.put("resAddress", resAddresses.get(i).html());
	    		restaurantObj.put("resCostForTwo", resCosts.get(i).html());
	    		driver.get(resNames.get(i).attr("href"));
	    		doc = Jsoup.parse(driver.getPageSource());
	    		for (Element menuPage : doc.select("script")) {
	    			for (DataNode node : menuPage.dataNodes()) {
	  	    		  if (node.getWholeData().contains("zomato.menuPages")) {
	  	                //System.out.println(node.getWholeData());
	  	    			  String pages = node.getWholeData().split("zomato.menuPages = ")[1];
	  	    			  JSONArray menuArray = new JSONArray(pages.split(";")[0]);
	  	    			  restaurantObj.put("resMenuPages", menuArray);
	  	    		  }
	  	          } 
	    		}
	    		restaurantsArray.put(restaurantObj);
	    	}*/
	    	
	    	//System.out.println(names.html());
	    	driver.get("https://www.zomato.com/pune/baner-restaurants?all=1&page="+(i+1));
	    	doc = Jsoup.parse(driver.getPageSource());
	    }
	    System.out.println(restaurantsArray.toString());
		}catch(Exception je) {
			System.out.println("JSON Exception");
		}
	    //Elements addresses = doc.select("div.flex a");
	    //Elements names = doc.select("div.flex a");
	    driver.quit();
	    
	    	
	}
	
	public void sample() {
		//Total Count of restaurant in locality
		/*
		Elements names = doc.select("h2.search_subtitle");
	    System.out.println(names.html().split("Showing ")[1].split(" restaurants")[0]); */
		
		//Title of rest
		/*
		Elements names = doc.select("a.result-title");
	    System.out.println(names.html());
		 */
		
		//Rating 
		/*
		Elements names = doc.select("div.rating-popup");
	    System.out.println(names.html());
		 */
		
		//Res Address
		/*
		Elements names = doc.select("div.search-result-address");
	    System.out.println(names.html());
		 */
		
		//Cost For Two
		/*
		Elements names = doc.select("div.res-cost span.pl0");
	    System.out.println(names.html()); 
		 */
		//Total Pages
		/*
		Element pagesTotal = doc.select("div.pagination-number div b").last();
	    System.out.println(names.html());
		 */
		/*
		 * Starting ChromeDriver 2.42.591059 (a3d9684d10d61aa0c45f6723b327283be1ebaad8) on port 32498
Only local connections are allowed.
Sep 19, 2018 12:26:09 AM org.openqa.selenium.remote.ProtocolHandshake createSession
INFO: Detected dialect: OSS
[1537298167.902][SEVERE]: Timed out receiving message from renderer: 299.536
[1537298167.905][SEVERE]: Timed out receiving message from renderer: -0.007
Exception in thread "main" org.openqa.selenium.TimeoutException: timeout
  (Session info: chrome=69.0.3497.100)
  (Driver info: chromedriver=2.42.591059 (a3d9684d10d61aa0c45f6723b327283be1ebaad8),platform=Mac OS X 10.13.6 x86_64) (WARNING: The server did not provide any stacktrace information)
Command duration or timeout: 0 milliseconds
Build info: version: '3.14.0', revision: 'aacccce0', time: '2018-08-02T20:13:22.693Z'
System info: host: 'Anujs-MacBook-Pro.local', ip: 'fe80:0:0:0:144d:e6d2:421b:f2f4%en0', os.name: 'Mac OS X', os.arch: 'x86_64', os.version: '10.13.6', java.version: '1.8.0_131'
Driver info: org.openqa.selenium.chrome.ChromeDriver
Capabilities {acceptInsecureCerts: false, acceptSslCerts: false, applicationCacheEnabled: false, browserConnectionEnabled: false, browserName: chrome, chrome: {chromedriverVersion: 2.42.591059 (a3d9684d10d61a..., userDataDir: /var/folders/gp/s8m2922j6tj...}, cssSelectorsEnabled: true, databaseEnabled: false, goog:chromeOptions: {debuggerAddress: localhost:53848}, handlesAlerts: true, hasTouchScreen: false, javascriptEnabled: true, locationContextEnabled: true, mobileEmulationEnabled: false, nativeEvents: true, networkConnectionEnabled: false, pageLoadStrategy: normal, platform: MAC, platformName: MAC, rotatable: false, setWindowRect: true, takesHeapSnapshot: true, takesScreenshot: true, unexpectedAlertBehaviour: , unhandledPromptBehavior: , version: 69.0.3497.100, webStorageEnabled: true}
Session ID: b0002c4c3890e69250b9f96a854ece4a
	at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
	at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
	at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
	at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
	at org.openqa.selenium.remote.ErrorHandler.createThrowable(ErrorHandler.java:214)
	at org.openqa.selenium.remote.ErrorHandler.throwIfResponseFailed(ErrorHandler.java:166)
	at org.openqa.selenium.remote.http.JsonHttpResponseCodec.reconstructValue(JsonHttpResponseCodec.java:40)
	at org.openqa.selenium.remote.http.AbstractHttpResponseCodec.decode(AbstractHttpResponseCodec.java:80)
	at org.openqa.selenium.remote.http.AbstractHttpResponseCodec.decode(AbstractHttpResponseCodec.java:44)
	at org.openqa.selenium.remote.HttpCommandExecutor.execute(HttpCommandExecutor.java:158)
	at org.openqa.selenium.remote.service.DriverCommandExecutor.execute(DriverCommandExecutor.java:83)
	at org.openqa.selenium.remote.RemoteWebDriver.execute(RemoteWebDriver.java:548)
	at org.openqa.selenium.remote.RemoteWebDriver.get(RemoteWebDriver.java:276)
	at aacs.Main.main(Main.java:112)

		 */
		
	}

}
