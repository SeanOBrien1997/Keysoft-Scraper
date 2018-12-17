package sob.group.web.keysoft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.ListUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 
 *
 */
public class App {
	static final Scanner sc = new Scanner(System.in);

	public static void main(String[] args) throws Exception {
		int[] sections = getSectionsInput();
		WebDriver driver = setUp(true);
		login(driver);
		List<String> list = loopSectionsList(driver, sections);
		List<Product> pList = Product.makeProductList(list);
		searchLists(pList);
	}

	public static int[] getSectionsInput() {
		String s = sc.nextLine().toUpperCase();
		Set<String> sectionSet = new HashSet<String>();
		while (!s.equals("STOP")) {
			sectionSet.add(s);
			s = sc.nextLine().toUpperCase();
		}

		List<String> sect = new ArrayList<String>(sectionSet);
		return getSections(sect);
	}

	public static void searchLists(List<Product> pList) {
		System.out.println("Enter search: ");
		String search = sc.nextLine();
		while (!search.equals("stop search")) {
			for (Product p : pList) {
				String s = p.getName().toLowerCase();
				if (s.contains(search)) {
					System.out.println(p.toString());
				} else if (search.equals("flush screen")) {
					System.out.println("\033[H\033[2J");
				}
			}
			System.out.println("Enter search: ");
			search = sc.nextLine().toLowerCase();
		}

	}

	public static int[] getSections(List<String> list) {
		int[] ar = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			switch (s) {
			case "BATTERIES":
				ar[i] = 0;
				break;
			case "CARDS":
				ar[i] = 1;
				break;
			case "DUMPBINS":
				ar[i] = 2;
				break;
			case "ELECTRICAL & AUDIO":
				ar[i] = 3;
				break;
			case "FANCY GOODS":
				ar[i] = 4;
				break;
			case "HAIR FASHION":
				ar[i] = 5;
				break;
			case "HATS&SCARVES&GLOVES":
				ar[i] = 6;
				break;
			case "HOUSEHOLD":
				ar[i] = 7;
				break;
			case "PAPER BAGS":
				ar[i] = 8;
				break;
			case "PRICE STICKERS":
				ar[i] = 9;
				break;
			case "SEASON":
				ar[i] = 10;
				break;
			case "STATIONERY":
				ar[i] = 11;
				break;
			case "SWEETS":
				ar[i] = 12;
				break;
			case "TISSUES":
				ar[i] = 13;
				break;
			case "TOILETRIES":
				ar[i] = 14;
				break;
			case "TOYS":
				ar[i] = 15;
				break;
			case "WRAP":
				ar[i] = 16;
				break;
			default:
				System.out.println("Error: Invalid entry for section");
			}
		}
		return ar;
	}

	public static List<String> loopSectionsList(WebDriver driver, int[] sections) throws IOException {
		List<String> list = new ArrayList<String>();
		for (int index : sections) {
			goToSection(driver, index);
			list = ListUtils.union(list, sectionList(driver));
		}
		return list;
	}

	public static int findItem(WebDriver driver, List<String> list, String find) {
		int index = -1;
		int pagenum = -1;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(find)) {
				index = i;
			}
		}
		if (index == -1) {
			goToMainMenu(driver);
			System.out.println("Not found");
		} else {
			index++;
			pagenum = (index / 21) + 1;
			for (int i = 0; i < pagenum; i++) {
				nextPage(driver);
			}
		}
		return pagenum;
	}

	public static List<String> findNewStocks(List<String> oldList, List<String> newList) {
		List<String> newStocks = new ArrayList<String>(newList);
		newStocks.removeAll(oldList);
		return newStocks;
	}

	public static List<String> findOutOfStocks(List<String> oldList, List<String> newList) {
		List<String> outOfStocks = new ArrayList<String>(oldList);
		outOfStocks.removeAll(newList);
		return outOfStocks;
	}

	public static List<String> sectionList(WebDriver driver) {
		List<String> list = new ArrayList<String>();
		boolean check = false;
		while (!check) {
			list = ListUtils.union(list, tableEntries(getSource(driver)));
			check = checkSamePage(driver);
		}
		goToMainMenu(driver);
		return list;
	}

	public static int numberOfPages(List<String> sectionList) {
		int result;
		if (sectionList.size() % 21 == 0) {
			result = sectionList.size() / 21;
		} else {
			result = (sectionList.size() / 21) + 1;
		}
		return result;
	}

	public static boolean checkSamePage(WebDriver driver) {
		boolean samePage = false;
		String a = getSource(driver).toString();
		nextPage(driver);
		String b = getSource(driver).toString();
		if (a.equals(b)) {
			samePage = true;
		}
		return samePage;
	}

	public static void nextPage(WebDriver driver) {
		String xpath = ("//*[@id=\"200\"]/div[3]/table/tbody/tr/td[3]/button");
		driver.findElement(By.xpath(xpath)).click();
	}

	public static void goToMainMenu(WebDriver driver) {
		String xpath = ("//*[@id=\"BtnBack.0\"]");
		driver.findElement(By.xpath(xpath)).click();
	}

	public static Document getSource(WebDriver driver) {
		return Jsoup.parse(driver.getPageSource());
	}

	public static void goToSection(WebDriver driver, int section) throws IOException {
		String xPaths = "C:\\\\Users\\\\Sean\\\\Desktop\\\\xPaths\\\\test.txt";
		List<String> sectionsXpaths = readTextFile(xPaths);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.findElement(By.xpath(sectionsXpaths.get(section))).click();
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#\\30 > table")));
	}

	public static void login(WebDriver driver) {
		WebDriverWait wait = new WebDriverWait(driver, 20);
		String username = "";
		String password = "";
		String baseUrl = "http://server1.keysoft.ie/keysoft/#!";
		String xpathNewOrder = "//*[@id=\"BtnNew.0\"]";
		driver.get(baseUrl);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Data_UserName_0")));
		driver.findElement(By.id("Data_UserName_0")).sendKeys(username);
		driver.findElement(By.id("Data_Password_0")).sendKeys(password);
		driver.findElement(By.name("Login")).sendKeys(Keys.ENTER);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.findElement(By.xpath(xpathNewOrder)).click();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	public static WebDriver setUp(boolean headless) {
		System.setProperty("webdriver.chrome.driver", "A:\\Webscraping\\chromedriver\\chromedriver.exe");
		WebDriver driver;
		if (headless) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--headless");
			driver = new ChromeDriver(options);
		} else {
			driver = new ChromeDriver();
		}
		return driver;
	}

	@SuppressWarnings("unused")
	public static List<String> tableEntries(Document pageSource) {
		final Document productTable = Jsoup.parse(pageSource.getElementsByClass("Table1").get(1).toString());
		int index = 1;
		List<String> entries = new ArrayList<String>();
		for (Element row : productTable.select("table.Table1 tr")) {
			List<String> ids = tableIDValues(index);
			String result = "";
			for (String s : ids) {
				Element e = productTable.getElementById(s);
				result += (e.attr("value") + " ");
			}
			entries.add(result);
			index++;
		}
		return entries;
	}

	public static List<String> tableIDValues(int i) {
		List<String> list = new ArrayList<String>();
		list.add("Data301_Name1_" + i);
		list.add("Data301_Code_" + i);
		list.add("Data301_Name_" + i);
		list.add("Data301_Name3_" + i);
		list.add("Data301_Name4_" + i);
		list.add("Data301_Name5_" + i);
		return list;
	}

	public static List<String> readTextFile(String path) throws IOException {
		List<String> list = new ArrayList<String>();
		File file = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String s;
		while ((s = br.readLine()) != null) {
			list.add(s);
		}
		br.close();
		return list;
	}
}
