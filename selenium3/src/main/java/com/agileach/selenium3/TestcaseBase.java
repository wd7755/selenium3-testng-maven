package com.agileach.selenium3;

import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestcaseBase {
	protected static WebDriver driver;
	protected static Logger logger = LoggerFactory.getLogger(TestcaseBase.class);
	protected String excelPath;
	private static EventFiringWebDriver eventDriver;	
	
	public static WebDriver getDriver(String browser) {
		if (driver == null) {
			synchronized (WebDriver.class) {
				try {				
					if(browser.equalsIgnoreCase("chrome")) {
						System.setProperty("webdriver.chrome.driver", "D:\\jdk\\chromedriver.exe");
						ChromeOptions option = new ChromeOptions();		
						//通过ChromeOptions的setExperimentalOption方法，传下面两个参数来禁止掉谷歌受自动化控制的信息栏
						option.setExperimentalOption("useAutomationExtension", false); 
						option.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));	
						option.setCapability("acceptSslCerts", true);
						driver = new ChromeDriver(option);		
					}else if(browser.equalsIgnoreCase("firefox")) {
						System.setProperty("webdriver.gecko.driver", "D:\\jdk\\geckodriver.exe");    	
						FirefoxOptions option = new FirefoxOptions();		
						option.setCapability("acceptSslCerts", true);
						driver = new FirefoxDriver(option);		
					}else if(browser.equalsIgnoreCase("ie")) {
						InternetExplorerOptions option = new InternetExplorerOptions();		
						option.setCapability("acceptSslCerts", true);
						driver = new InternetExplorerDriver(option);		
					}
					logger.info("instance an new webdriver...");					
					eventDriver = new EventFiringWebDriver(driver);
			        //注册事件
			        eventDriver.register(new MyWebDriverListener());	
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
			eventDriver.manage().window().maximize();
			eventDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		}		
		return eventDriver;
	}

	@BeforeTest
	public void setUp() {		
		// 数据流的形式读取配置文件
		Properties prop = new Properties();
		try {
			InputStream fis = this.getClass().getClassLoader().getResourceAsStream("config.properties");	
			prop.load(fis);
			String browser = prop.getProperty("Browser");	
			excelPath = prop.getProperty("TestDataPath");		
			driver = getDriver(browser);				
			logger.info("Start {0}，get webdriver...",browser);
			Util.setWebDriver(driver);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	@AfterTest
	public void tearDown() {
		driver.quit();
		driver = null;
		logger.info("quit webdriver...");
	}
}
