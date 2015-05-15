# Stocks_WebServices
A project to find the company in the Dow Jones with maximum standard deviation for a day by running it on AWS for  a day


STOCK VALUES BASED WEB SERVICES PROJECT: 
*****************************************

Requirements:
**************
Build client for Stock Price Service by using the dynamic web project.
Service definition is in the wsdl <http://www.webservicex.net/stockquote.asmx?WSDL>
Build a program that find the maximum fluctuated stock within a day ( Maximum Standard Deviation) for all the company in Dow Jones Industrials Average 
Call service every 10 minutes
For each stock price, write it to a file in the same folder the program.


Following are the steps and the information about the files being created. 
***************************************************************************

*******
Steps:
*******
1. The runnable jar files should be created with all the libraries included along with the jar.
2. To connect to the EC2 instance of AWS we can use putty if you are using windows following the link below:
	<http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/putty.html>
   WinSCP can be use to upload the required files and folders in the similar manner.
3. The whole folder of StockProj with the jar file and folder named Constant should be placed in the server.
4. Now the jar file can be executed with the appropriate command. For example : <nohup java -jar myjar.jar 2>&1 >> logfile.log &>
5. Allow the executable to run for one whole day. And at the end of one day the executable generates the Stock 
   values in a txt file and the maximum deviation of all companies in another file.


***************************************************************************
Output after running it for one day will be caputured in following files:
***************************************************************************
1. The StockValues.txt has the stock values of all the companies.
2. The Standard_deviation.txt has the deviation calculated for all the companies at the end of 
   the day along with the company with maximum vaule with its value.
