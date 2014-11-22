package core;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class ScreenScraping {

	private static WebDriver driver = null;

	public static void main(String[] args) {

		ScreenScraping f = new ScreenScraping();
		int tE = 0;
		Element urls2 = null;
		
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get("http://www.airasia.com");

		String pageSource = driver.getPageSource();
		Document doc = Jsoup.parse(pageSource);

		Elements div = doc.select("div#ContentPlaceHolder2_T3120FD4E011_Col00");
		Elements urls = div.select("div.column");
		Iterator itrUrls = urls.iterator();

		while (itrUrls.hasNext()){
			if (tE != 1){
				urls2 = (Element) itrUrls.next();
			}
			Elements url = urls2.select("a.book-now"); 
			String url2 = url.attr("abs:href");
			tE = f.getInfoFlight(url2);
		}
		System.out.println("last page");
		driver.quit();
		System.out.println("the end");
	}

	public void info(Elements dept_ret)	{
		Elements row1 = dept_ret.select("div.row1");
		Elements row2 = dept_ret.select("div.row2");
		Element[] info1 = new Element[10];
		Element[] info2 = new Element[3];

		for (int i=0;i<10;i++){
			info1[i] = row1.select("span").get(i);
		}

		for (int i=0;i<3;i+=2){
			info2[i] = row2.select("div.left").get(i);

		}

		Element numFlight = info1[0];
		Element fr = info2[0];
		Element to = info2[2];
		Element tmDep = info1[1];
		Element tmAr = info1[2];
		Element fareLbl = info1[3];
		Element sumFare = info1[4];
		Element psg = info1[5];
		Element tx1Lbl = info1[6];
		Element sumTx = info1[7];
		Element tx2Lbl = info1[8];
		Element sum1Trp = info1[9];

		System.out.print("Flight Number : "+numFlight.text() +
				"\nFrom : " + fr.text() + " " + tmDep.text() + " to : " + to.text() +" "+ tmAr.text() +
				"\n"+fareLbl.text()+ " : " + psg.text() + " " + sumFare.text() + 
				"\n"+tx1Lbl.text() + " :\n" + tx2Lbl.text() + " : " + sumTx.text() +   
				"\nTotal 1 Way Trip : " + sum1Trp.text()
				);
	}

	public int getInfoFlight(String url){
		boolean refPage = false;
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get(url);  
		WebDriverWait wait = new WebDriverWait(driver, 5);

		try {
			wait.until(ExpectedConditions.elementToBeClickable(By.className("bookingna")));
			System.out.println("NOT FOUND");
			driver.close();
			return 0;
		}catch (TimeoutException tENA){
			do {
				try {
					refPage = false;
					wait.until(ExpectedConditions.elementToBeClickable(By.className("total-amount-bg")));
				}catch (TimeoutException notOP){
					System.out.println("OBJECT XMLDOCUMENT NOT OPENING, refreshing page");
					refPage = true;
					driver.navigate().to(driver.getCurrentUrl());
				}
			} while (refPage == true);
		}

		String sPageSource = driver.getPageSource();
		Document doc = Jsoup.parse(sPageSource);

		Element div = doc.getElementById("taxAndFeeResult");
		Elements fhtCrs = div.select("div.depart-return-msg");
		Element depLbl = div.select("a").get(0);
		Element arLbl = div.select("a").get(1);
		Elements dep = div.select("div.flightDisplay_1");
		Elements ar = div.select("div.flightDisplay_2");
		Elements sumAll = doc.select("div.total-amount-bg-last");

		ScreenScraping f = new ScreenScraping();

		System.out.println("Filght Course : " + fhtCrs.text() + "\n" + depLbl.text());
		f.info(dep);
		System.out.println( "\n" + arLbl.text());
		f.info(ar);
		System.out.println("\n"+sumAll.text());

		driver.close();
		return 0;
	}
}