package com.validate.brokenlinks;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class TriggerBLC {

	public static void main(String[] args) {
		// System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir")
		// + "//chromedriver.exe");
		// WebDriver dr = new ChromeDriver();
		WebDriver dr = new HtmlUnitDriver();
		dr.get("http://www.google.com");

		BLCDriver bl = new BLCDriver(dr);
		bl.setBaseURL("google");
		bl.setNavigationDepth(2);
		bl.start();

		dr.close();
		System.out.println("done crawling");

	}

}
