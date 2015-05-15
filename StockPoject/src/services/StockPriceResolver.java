package services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;














import NET.webserviceX.www.StockQuoteSoapProxy;

public class StockPriceResolver {
	
	static HashMap<String, ArrayList<Double>> ValTable = new HashMap<String, ArrayList<Double>>();
	
	static final ArrayList<String> CompanyList = new ArrayList<String>();
	
	static ArrayList<Double> lastPriceList;
	static StockQuoteSoapProxy proxy = new StockQuoteSoapProxy();
	static HashMap<String,Double>StdDevCalc = new HashMap<String,Double>();
	static double maxStdDev = 0.00;
	static String maxStdDevComp = null;
	static final int timeConst = 144;

	
	public static void main(String args[]) throws IOException, InterruptedException{
			String value = null;
			ReadCompanyList();
			int count = 0;
			try {
				while(true){
					for(int numComp = 0; numComp < CompanyList.size(); numComp++ ){
						value = proxy.getQuote(CompanyList.get(numComp));
						//System.out.println(value);
						ParseAndAdd(value);
					}
					System.out.println(ValTable);
					PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("StockValues.txt"))); 
	
				    for (int windex = 0; windex < CompanyList.size(); windex++) {
				        out.println(CompanyList.get(windex) + " " + ValTable.get(CompanyList.get(windex)));
				    }
				    out.close();
				    count++;
				    System.out.println("Count = "+count);
				    if(count < timeConst){
				    		TimeUnit.MINUTES.sleep(10);
				    }
				    	//continue;
				    else
				    	break;
				}
				StDeviationCalc();
				PrintWriter outbuf = new PrintWriter(new BufferedWriter(new FileWriter("Standard_Deviation.txt")));
				for (int windex = 0; windex < CompanyList.size(); windex++) {
					outbuf.println(CompanyList.get(windex) + " " + StdDevCalc.get(CompanyList.get(windex)));
				}
				outbuf.println("Company with Maximum Deviation is: "+ maxStdDevComp + " with the Maximum Deviation of: " + maxStdDev);
				outbuf.close();
		} 
		catch (Exception e) {
			System.out.println(e.toString());
		}

	}
	
	private static void StDeviationCalc() {
		int numOfElements=lastPriceList.size();
		
		//System.out.println("number of elements"+numOfElements);
		//Mean calculation
		for(int cnt= 0; cnt < CompanyList.size(); cnt++) {
			ArrayList<Double>Calculate = ValTable.get(CompanyList.get(cnt));
			double Sum = 0.00;
			for(int avgcnt = 0; avgcnt < Calculate.size(); avgcnt++){
				Sum = Sum + Calculate.get(avgcnt);
			}
			Double Mean = Sum/numOfElements;
			StdDevCalc.put(CompanyList.get(cnt),Mean);
		}
		System.out.println(StdDevCalc);
		
		//Standard deviation for each company.
		for(int cnt=0; cnt< CompanyList.size(); cnt++) {
			ArrayList<Double>StdCalculate = ValTable.get(CompanyList.get(cnt));
			double diffsum = 0.00;
			for(int stdcnt = 0; stdcnt < StdCalculate.size(); stdcnt++){
				double diffsqr = Math.pow(StdCalculate.get(stdcnt) - StdDevCalc.get(CompanyList.get(cnt)), 2);
				diffsum = diffsum + diffsqr;
			}
			diffsum = diffsum/StdCalculate.size();
			double stddev = Math.sqrt(diffsum);
			StdDevCalc.put(CompanyList.get(cnt),stddev);
			if(stddev > maxStdDev){
				maxStdDev = stddev;
				maxStdDevComp = CompanyList.get(cnt);
			}
		}
		System.out.println(StdDevCalc);
	}


	private static void ReadCompanyList() throws IOException {
		BufferedReader buf = null;
		String companySym = null;
		final String dir;
        dir = System.getProperty("user.dir");
        String path = dir + "/" +"Constant" +  "/" + "CompanySymbol.txt";
		buf = new BufferedReader (new FileReader(path));
		while((companySym = buf.readLine())!= null){
			CompanyList.add(companySym);
			ValTable.put(companySym, null);
		}
		buf.close();
	}

	private static void ParseAndAdd(String perStockVal) {
		try {
			String symbol;
			
			String lastprice; 
			if(perStockVal.indexOf('&') != -1){
				int index = perStockVal.indexOf('&');
				perStockVal = perStockVal.substring(0, index) + "&amp;" + perStockVal.substring(index+1, perStockVal.length());
				//System.out.println(perStockVal);
			}
			org.w3c.dom.Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
			        .parse(new InputSource(new StringReader(perStockVal)));
			
			NodeList sym = doc.getElementsByTagName("Symbol");
			symbol = sym.item(0).getTextContent();
			
			NodeList lastval = doc.getElementsByTagName("Last");
			lastprice = lastval.item(0).getTextContent();
			updateTable(symbol, lastprice);
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		
	}

	private static void updateTable(String symbol, String lastprice) {
		
	    // if list does not exist create it
	    if(ValTable.get(symbol) == null) {
	    	lastPriceList = new ArrayList<Double>();
	    	lastPriceList.add(Double.parseDouble(lastprice));
	        ValTable.put(symbol, lastPriceList);
	    } 
	    
	    else {
	        // add if item is not already in list
	    	lastPriceList = ValTable.get(symbol);
	    	lastPriceList.add(Double.parseDouble(lastprice));
	        ValTable.put(symbol, lastPriceList);
	    }
	}
}