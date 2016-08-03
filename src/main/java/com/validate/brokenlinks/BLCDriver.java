package com.validate.brokenlinks;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class BLCDriver {

	private static String BaseURL = "";
	private static WebDriver driver;
	private static List<String> verifiedPages = new ArrayList<String>();
	private static List<String> brokenLinks = new ArrayList<String>();
	private static int MaxLevel;

	private List<String> allLinksInCurrentPage = new ArrayList<String>();;
	private List<String> brokenLinksInCurrentPage = new ArrayList<String>();;
	private String currentUrl;
	private boolean pageDidntOpen = false;
	private int level;

	public BLCDriver(WebDriver driver) {
		BLCDriver.driver = driver;
		currentUrl = driver.getCurrentUrl();
	}

	private BLCDriver(WebDriver driver, String url) {
		BLCDriver.driver = driver;
		currentUrl = url;
	}

	public void start(int MaxLevel) {
		level = 1;
		BLCDriver.MaxLevel = MaxLevel;
		System.out.println("Current Level --> " + level);
		startBrokenLinksCheck();
	}

	public void start() {
		level = 1;
		MaxLevel = 10;
		System.out.println("Current Level --> " + level);
		startBrokenLinksCheck();
	}

	private void startNext() {
		System.out.println("Current Level --> " + level);
		startBrokenLinksCheck();
	}

	private void startBrokenLinksCheck() {
		System.out.println("\nVerifying links in page --> " + currentUrl);
		getAllLinksInPage(currentUrl); // adds all links to the
										// 'allLinksInCurrentPage' variable.
		getBrokenLinksInPage(); // get response from all links and keeps broken
								// links in 'brokenlinksincurrentpage' and
								// 'brokenlinks' variables.
		startBrokenLinkCheckNextLevel();
	}

	private void startBrokenLinkCheckNextLevel() {

		int nextLevel = level + 1;
		if (nextLevel <= MaxLevel) {
			for (String link : allLinksInCurrentPage) {
				if (!BaseURL.equalsIgnoreCase("")) {

					if (link.contains(BaseURL)) {
						if (!brokenLinksInCurrentPage.contains(link)) {
							BLCDriver blcdriver = new BLCDriver(driver, link);
							blcdriver.level = nextLevel;
							blcdriver.startNext();
						}
					}

				} else {

					if (!brokenLinksInCurrentPage.contains(link)) {
						BLCDriver blcdriver = new BLCDriver(driver, link);
						blcdriver.level = nextLevel;
						blcdriver.startNext();

					}

				}

			}

		}

	}

	private int getResponseOfLink(String url) {
		try {
			URL u = new URL(url);
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setReadTimeout(120000);
			huc.setRequestMethod("GET");
			huc.connect();
			return huc.getResponseCode();
		} catch (Exception e) {
			System.out.println("\t\tBad URL - " + url);
			System.out.println("\t\t" + e.getMessage());
			// brokenLinks.add(url); //an url for which unable to get response
			// to handle this
			return 0;
		}
	}

	private void getAllLinksInPage(String url) {

		if (!url.contains("logout")) {

			try {

				driver.get(url);
			} catch (Exception e) {
				pageDidntOpen = true;
				System.out.println("\t\tError opening url " + url);
				System.out.println("\t\t" + e.getMessage());
			}
			if (!pageDidntOpen) {

				List<WebElement> links = driver.findElements(By.tagName("a"));
				for (int i = 0; i < links.size(); i++) {
					if (!(links.get(i).getAttribute("href") == null)
							&& !(links.get(i).getAttribute("href").equals(""))) {

						if (!links.get(i).getAttribute("href")
								.equalsIgnoreCase(url)) {
							if (!verifiedPages.contains(links.get(i)
									.getAttribute("href"))) {
								if (links.get(i).getAttribute("href")
										.contains("http")) {
									allLinksInCurrentPage.add(links.get(i)
											.getAttribute("href"));
								}
							} else {
								System.out
										.println("While collecting links in the page found Below link available in Already Verified Pages List");
								System.out.println(links.get(i).getAttribute(
										"href"));
							}
						}
					}
				}

				System.out.println("No.of links in the page --> "
						+ allLinksInCurrentPage.size());
			} else {
				System.out.println("Page Didnt Open --> " + url);
			}
		}
	}

	private void getBrokenLinksInPage() {
		int statusCode;
		if (allLinksInCurrentPage.size() == 0) {
			System.out.println("End point reached on a branch");
		}
		for (int i = 0; i < allLinksInCurrentPage.size(); i++) {
			statusCode = getResponseOfLink(allLinksInCurrentPage.get(i).trim());

			if (statusCode != 200) {
				System.out.println("\n Status code --> " + statusCode
						+ "  url --> " + allLinksInCurrentPage.get(i));
			}
			if (statusCode == 404) {
				brokenLinks.add(allLinksInCurrentPage.get(i));
				brokenLinksInCurrentPage.add(allLinksInCurrentPage.get(i));
			}
		}
		System.out.println("No.of broken links in current page --> "
				+ brokenLinksInCurrentPage.size());
	}

	public void printBrokenLinks() {
		brokenLinks.get(1).toString();
	}

	public void setBaseURL(String brul) {
		BaseURL = brul;
	}

	public void setNavigationDepth(int _level) {
		MaxLevel = _level;

	}

}
