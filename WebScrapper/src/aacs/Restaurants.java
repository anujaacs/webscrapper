package aacs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Restaurants {
	
	static String cityLocContent = "";
	//static JSONArray cityLocArray ;
 

	public static void main(String[] args) throws JSONException {
		// TODO Auto-generated method stub
		
		Logger logger = Logger.getLogger("MyLog");  
	    FileHandler fh;  

	    try {  

	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler("appender.log");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  

	        // the following statement is used to log any messages  
	        logger.info("My first log");  

	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  

	    //logger.info("Hi How r u?");  
		JSONArray locResArray = new JSONArray();
		JsonParser parser = new JsonParser();
		JsonArray cityLocgArray = new JsonArray();
		String chromeDriverPath = "/usr/local/bin/chromedriver";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless");//, "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");  
        WebDriver driver = new ChromeDriver(options);
		try {
			cityLocContent = FileUtils.readFileToString(new File("CityLocality.json"));
			//cityLocArray = new JSONArray(cityLocContent);
			cityLocgArray = parser.parse(cityLocContent).getAsJsonArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			cityLocContent = "NA";
		} 
		System.out.println(cityLocgArray.toString());
		//int localityCount = 0;
		int breaker = 0;
		int matcher = 5;
		String filename = "resArray"+matcher+".json";
		for (JsonElement cityLocElement : cityLocgArray) {
			breaker++;
			if (breaker != matcher)
				continue;
			JsonObject cityLocObj = cityLocElement.getAsJsonObject();
			JsonArray localitiesArray = cityLocObj.get("localities").getAsJsonArray();
			
			for (JsonElement loclityElement : localitiesArray) {
				JSONObject locResObj = new JSONObject();
				JsonObject localityObj = loclityElement.getAsJsonObject();
				String localityId = localityObj.get("lid").getAsString();
				locResObj.put("lid", localityId);
				logger.info(localityId);
				String localityUrl = localityObj.get("lurl").getAsString();
				driver.get(localityUrl + "?all=1");
				Document restaurantsDoc = Jsoup.parse(driver.getPageSource());
		        Element pagesTotal = restaurantsDoc.select("div.pagination-number div b").last();
		        int totalPages;
		        try {
		        	totalPages = Integer.parseInt(pagesTotal.html());
		        } catch (NullPointerException ne) {
		        	String tempUrl = driver.getCurrentUrl();
		        	if (tempUrl.contains("15005")) {
		        		driver.get(localityUrl.split(":15005")[0]+localityUrl.split(":15005")[1]+"?all=1");
		        		restaurantsDoc = Jsoup.parse(driver.getPageSource());
		        		pagesTotal = restaurantsDoc.select("div.pagination-number div b").last();
		        		totalPages = Integer.parseInt(pagesTotal.html());
		        	}else
		        		totalPages = 10;
		        }
		        JSONArray restaurantsArray = new JSONArray();
		        for (int i = 0; i < totalPages; i++) {
		        	Elements resNames = restaurantsDoc.select("a.result-title");
		            Elements resRatings = restaurantsDoc.select("div.rating-popup");
		            Elements resAddresses = restaurantsDoc.select("div.search-result-address");
		            Elements resCosts = restaurantsDoc.select("div.res-cost span.pl0");
		            //resNames.get(1).html();
		            int namesSize = resNames.size();
		            for (int a = 0; a < namesSize; a++) {
		                JSONObject restaurantObj = new JSONObject();
		                String resId = localityId + "R" + (a+1);
		                try {
		                restaurantObj.put("resName", resNames.get(a).html());
		                restaurantObj.put("resUrl", resNames.get(a).attr("href"));
		                if (a < resRatings.size())
		                	restaurantObj.put("resRating", resRatings.get(a).html());
		                else
		                	restaurantObj.put("resRating", "NA");
		                if (a < resAddresses.size())
		                	restaurantObj.put("resAddress", resAddresses.get(a).html());
		                else
		                	restaurantObj.put("resAddress", "NA");
		                if (a < resCosts.size())
		                	restaurantObj.put("resCostForTwo", resCosts.get(a).html());
		                else
		                	restaurantObj.put("resCostForTwo", "NA");
		                restaurantObj.put("resId", resId);
		                } catch (JSONException je) {
		                    System.out.println("[ERROR RESTAURANT]JSON exception ocuured " + je.getMessage() +"\n "+ resNames.get(a).attr("href")+"\n PAGE No"+(i+1));
		                    //screenshotUrlAdd(driver, "ERRORRESTAURANT.png");
		                }
		                restaurantsArray.put(restaurantObj);
		             }
		            //System.out.println(restaurantsArray);
		            driver.get(localityObj.get("lurl").getAsString() + "?all=1&page=" + (i + 1));
	                restaurantsDoc = Jsoup.parse(driver.getPageSource());
		        }
		        locResObj.put("lrestaurants", restaurantsArray); 
		        System.out.println(locResObj);
		        logger.info(locResObj.toString());
		        locResArray.put(locResObj);
			}
			//locResArray.put(locResObj);
			//if (breaker == 0)
			//	break;
			//breaker++;
			//localityCount = localityCount + localitiesArray.size();
		}
		driver.quit();
		try {
		FileWriter fileWriter = new FileWriter(filename);
		fileWriter.write(locResArray.toString());
		fileWriter.flush();
		}catch (IOException ie) {
			System.out.println(ie.getMessage());
		}
		System.out.println(locResArray);
	}

	
	
	public void comments() {
		/*[{"lid":"C1L1", "lrestaurants":[{"resName": "XYZ", "resUrl": "xyz.com", "resRatings": "4.4", "resCost":"1200"}]}]
		driver.get(loaclityElement.attr("href") + "?all=1");
        Document restaurantsDoc = Jsoup.parse(driver.getPageSource());
        Element pagesTotal = restaurantsDoc.select("div.pagination-number div b").last();
        int totalPages = Integer.parseInt(pagesTotal.html());
        JSONArray restaurantsArray = new JSONArray();
        for (int i = 0; i < totalPages; i++) {
            Elements resNames = restaurantsDoc.select("a.result-title");
            Elements resRatings = restaurantsDoc.select("div.rating-popup");
            Elements resAddresses = restaurantsDoc.select("div.search-result-address");
            Elements resCosts = restaurantsDoc.select("div.res-cost span.pl0");
            //resNames.get(1).html();
            int namesSize = resNames.size();
            for (int a = 0; a < namesSize; a++) {
                JSONObject restaurantObj = new JSONObject();
                String resId = localityId + "R" + (a+1);
                try {
                restaurantObj.put("resName", resNames.get(a).html());
                restaurantObj.put("resUrl", resNames.get(a).attr("href"));
                if (a < resRatings.size())
                	restaurantObj.put("resRating", resRatings.get(a).html());
                else
                	restaurantObj.put("resRating", "NA");
                if (a < resAddresses.size())
                	restaurantObj.put("resAddress", resAddresses.get(a).html());
                else
                	restaurantObj.put("resAddress", "NA");
                if (a < resCosts.size())
                	restaurantObj.put("resCostForTwo", resCosts.get(a).html());
                else
                	restaurantObj.put("resCostForTwo", "NA");
                restaurantObj.put("resId", resId);
                } catch (JSONException je) {
                    System.out.println("[ERROR RESTAURANT]JSON exception ocuured " + je.getMessage() +"\n "+ resNames.get(a).attr("href")+"\n PAGE No"+(i+1));
                    screenshotUrlAdd(driver, "ERRORRESTAURANT.png");
                }
                driver.get(resNames.get(a).attr("href"));
                restaurantsDoc = Jsoup.parse(driver.getPageSource());
                for (Element menuPage: restaurantsDoc.select("script")) {
                    for (DataNode node: menuPage.dataNodes()) {
                        if (node.getWholeData().contains("zomato.menuPages")) {
                            //System.out.println(node.getWholeData());
                            String pages = node.getWholeData().split("zomato.menuPages = ")[1];
                            try {
                            JSONArray menuArray = new JSONArray(pages.split(";")[0]);
                            restaurantObj.put("resMenuPages", menuArray);
                            }catch (JSONException je) {
                                System.out.println("[ERROR MENU PAGE]JSON exception ocuured " + je.getMessage() +"\n "+ resNames.get(a).attr("href")+"\n PAGE No"+(i+1));
                                screenshotUrlAdd(driver, "ERRORMENUPAGE.png");
                            }
                        }
                    }
                }
                restaurantsArray.put(restaurantObj);
            }

            //System.out.println(names.html());
            driver.get(loaclityElement.attr("href") + "?all=1&page=" + (i + 1));
            restaurantsDoc = Jsoup.parse(driver.getPageSource());
        }
		*/
	}
}
