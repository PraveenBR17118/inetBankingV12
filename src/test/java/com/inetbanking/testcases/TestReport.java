package com.inetbanking.testcases;


import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.inetbanking.pageObjects.LoginPage;
import com.inetbanking.utilities.ReadConfig;

public class TestReport
{
	ReadConfig readconfig=new ReadConfig();

	public String baseURL=readconfig.getApplicationURL();
	public String username=readconfig.getUsername();
	public String password=readconfig.getPassword();
	public static WebDriver driver;

	public static Logger logger;

	public ExtentHtmlReporter htmlReporter;
	public ExtentReports extent;
	public ExtentTest test;


	@BeforeSuite
	public void setup() 
	{
		// start reporters
		htmlReporter = new ExtentHtmlReporter("extent1.html");

		// create ExtentReports and attach reporter(s)
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);


	}

	@Parameters("browser")
	@BeforeClass
	public void setup(String br)
	{			
		logger = Logger.getLogger("ebanking");
		PropertyConfigurator.configure("Log4j.properties");

		if(br.equals("chrome"))
		{
			System.setProperty("webdriver.chrome.driver",readconfig.getChromePath());
			driver=new ChromeDriver();
		}
		else if(br.equals("firefox"))
		{
			System.setProperty("webdriver.gecko.driver",readconfig.getFirefoxPath());
			driver = new FirefoxDriver();
		}


		driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
		driver.get(baseURL);
	}

	@Test
	public void test1() throws Exception 
	{
		// creates a toggle for the given test, adds all log events under it    
		test = extent.createTest("MyFirstTest", "Sample description");

		// log(Status, details)
		test.log(Status.INFO, "This step shows usage of log(status, details)");

		// info(details)
		test.info("This step shows usage of info(details)");

		// log with snapshot
		test.fail("details", MediaEntityBuilder.createScreenCaptureFromPath("screenshot.png").build());

		// test with snapshot
		test.addScreenCaptureFromPath("screenshot.png");

	}

	@Test
	public void loginTest() throws IOException 
	{
		// creates a toggle for the given test, adds all log events under it    
		test = extent.createTest("MyLoginTest", "Sample description");

		logger.info("URL is opened");

		LoginPage lp=new LoginPage(driver);
		lp.setUserName(username);
		logger.info("Entered username");

		lp.setPassword(password);
		logger.info("Entered password");

		lp.clickSubmit();

		System.out.println(driver.getTitle());

//		// log(Status, details)
//		test.log(Status.INFO, "This step shows usage of log(status, details)");

		//GTPL Bank Manager HomePage
		
		if(driver.getTitle().equals("GTPL Bank Manager HomePage"))
		{
			Assert.assertTrue(true);

			test=extent.createTest(driver.getTitle()); // create new entry in the report
			test.log(Status.PASS,MarkupHelper.createLabel(driver.getTitle(),ExtentColor.GREEN)); // send the passed information to the report with GREEN color highlighted

			logger.info("Login test passed");
		}
		
		else 
		{
			//captureScreen(driver,"loginTest");
			Assert.assertTrue(false);
			
			test=extent.createTest(driver.getTitle()); // create new entry in the report
			test.log(Status.FAIL,MarkupHelper.createLabel(driver.getTitle(),ExtentColor.RED)); // send the passed information to the report with GREEN color highlighted
			
			String screenshotPath=System.getProperty("user.dir")+"\\Screenshots\\"+driver.getTitle()+".png";
			
			File f = new File(screenshotPath); 
			
			if(f.exists())
			{
			try {
				test.fail("Screenshot is below:" + test.addScreenCaptureFromPath(screenshotPath));
				} 
			catch (Exception e) 
					{
					e.printStackTrace();
					}
			}

			

			logger.info("Login test failed");
		}

	}

	@AfterClass
	public void tearDown()
	{
		driver.quit();
	}

	@AfterSuite
	public void taeDown() 
	{
		// calling flush writes everything to the log file
		extent.flush();
	}

}
