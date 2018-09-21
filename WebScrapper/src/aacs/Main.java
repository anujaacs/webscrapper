package aacs;

import java.io.File;
import java.io.FileWriter;
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

public class Main {

    //System.out.println(driver.getPageSource());
    //https://www.zomato.com/india
    //https://www.zomato.com/pune/tjs-brew-works-hadapsar/menu#tabtop

    public static String URL = "https://www.zomato.com/india";

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        JSONArray finalArray = new JSONArray();

        String chromeDriverPath = "/usr/local/bin/chromedriver";
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless");//, "--disable-gpu", "--window-size=1920,1200","--ignore-certificate-errors");  
        WebDriver driver = new ChromeDriver(options);

        //Load Zomato India Page
        driver.get(URL);
        Document countryDoc = Jsoup.parse(driver.getPageSource());
        Elements citiesElements = countryDoc.select("div.flex a");
        String cityCase = "C";
        int id = 1;
        int brk = 0;
        //try {
            for (Element cityElement: citiesElements) {
            	brk++;
            	if (brk == 2)
            		break;
                JSONObject cityObject = new JSONObject();
                String cityId = cityCase + id;
                try {
                cityObject.put("cityId", cityId);
                cityObject.put("cityName", cityElement.html().split(" Restaurants")[0]);
                cityObject.put("cityUrl", cityElement.attr("href"));
                } catch (JSONException je) {
                    System.out.println("[ERROR CITY]JSON exception ocuured " + je.getMessage() +"\n "+ cityElement.attr("href"));
                    screenshotUrlAdd(driver, "ERRORCITY.png");
                }
                //Loading city url
                driver.get(cityElement.attr("href"));
                Document cityDoc = Jsoup.parse(driver.getPageSource());
                Elements localitiesElements = cityDoc.select("a.pbot0");
                JSONArray localitiesArray = new JSONArray();
                int lid = 1;
                for (Element loaclityElement: localitiesElements) {
                    JSONObject localityObject = new JSONObject();
                    String localityId = cityId + "L" + lid;
                    try {
                    localityObject.put("lid", localityId);
                    localityObject.put("lname", loaclityElement.attr("title").split("Restaurants in ")[1]);
                    localityObject.put("lurl", loaclityElement.attr("href"));
                    } catch (JSONException je) {
                        System.out.println("[ERROR LOCALITY]JSON exception ocuured " + je.getMessage() +"\n "+ loaclityElement.attr("href"));
                        screenshotUrlAdd(driver, "ERRORLOCALITY.png");
                    }
                    //Loading locality url
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
                    //System.out.println(restaurantsArray.toString());
                    try {
                    localityObject.put("lhotels", restaurantsArray);
                    localitiesArray.put(localityObject);
                    }catch (JSONException je) {
                        System.out.println("[ERROR localityObject, restaurantsArray]JSON exception ocuured " + je.getMessage());
                        screenshotUrlAdd(driver, "ERRORlocalityObject.png");
                    }
                    lid++;
                }
                try {
                cityObject.put("localities", localitiesArray);
                }catch (JSONException je) {
                    System.out.println("[ERROR localitiesArray]JSON exception ocuured " + je.getMessage());
                    screenshotUrlAdd(driver, "ERRORlocalitiesArray.png");
                }
                finalArray.put(cityObject);
                id++;
            }
        /*} catch (JSONException je) {
            System.out.println("JSON exception ocuured" + je.getMessage());
        } */
        try {
			FileWriter fileWriter = new FileWriter("finalArrayAllIndia.json");
			fileWriter.write(finalArray.toString());
			fileWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
        System.out.println(finalArray.toString());
        /*
	    Document document = Jsoup.parse(driver.getPageSource());
	      Elements eles = document.getElementsByTag("script");
	      
	      for (Element ele : eles) {
	    	  //if (ele.html().contains("zomato.menuPages"))
	    	  for (DataNode node : ele.dataNodes()) {
	    		  if (node.getWholeData().contains("zomato.menuPages")) {
	                //System.out.println(node.getWholeData());
	    			  String pages = node.getWholeData().split("zomato.menuPages = ")[1];
	    			  System.out.print(pages.split(";")[0]);
	                
	    		  }
	          }  
	      }
	      */
        //System.out.println(eles.html());
        //System.out.println("Process End");
        /*Elements els = document.select("div.flex a");
        for (Element el: els) {
          System.out.println(el.attr("href"));
          
        }*/

        // Search for username / password input and fill the inputs
        /* driver.findElement(By.xpath("//input[@name='acct']")).sendKeys(userName);
	      driver.findElement(By.xpath("//input[@type='password']")).sendKeys(password);

	      // Locate the login button and click on it
	      driver.findElement(By.xpath("//input[@value='login']")).click();

	      if(driver.getCurrentUrl().equals("https://news.ycombinator.com/login")){
	           System.out.println("Incorrect credentials");
	           driver.quit();
	           System.exit(1);
	      }else{
	           System.out.println("Successfuly logged in");
	      }

	        // Take a screenshot of the current page
	        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
	        try {
				FileUtils.copyFile(screenshot, new File("screenshot.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	        // Logout
	        driver.findElement(By.id("logout")).click();*/
        driver.quit();

    }

    public static void screenshotUrlAdd(WebDriver driver, String fileName) {
    	File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
			FileUtils.copyFile(screenshot, new File(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


}