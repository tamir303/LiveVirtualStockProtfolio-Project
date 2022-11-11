Welcom To StockMarket Project.
Project By Yuval Mastey and Tamir Spilberg

Stock Market:
With the use of live information the program offers the user a unique opportunity to experiment with stocks and virtual currency in real-time. 
Portfolio:
The program constantly updates prices, showing stocks behavioral change and user's profit/loss with each update, making sure the user can change his strategy in any given situation in accordance with his balance.

Design Patterns:
Command Pattern (Broker) 
Singleton Pattern (Broker , Api)
MVC 

Threads:
Stocks Real Time Update ( Every 30 Seconds)
Stock Data Update (user's Choice)
Main Live Update

Local DataBase:
MYSQL-  save and load Multi portfolio's current states
Live stock price updates
Stock Data up to 1 year

GUI:
Live Stock Updates
Stock data on an Area chart (Please Check out the image)
portfolio base functions

API:
Stock Updates From Twelve Data API please make sure to subscribe the Api first.
(if you wish to use another API please make sure to update the API Requests in APiRequest.class)
https://rapidapi.com/twelvedata/api/twelve-data1/

 Installtion steps:
1. Please go to RapidAPI.com subscribe to TewlfeData API for api KEY
2. Run MySQL query and build local database
3. export JAVA files make sure you have the right jars.
4. Go to ApiRequest Package and edit the API KEYS, and username and password for MySQL (JDBC)
5. run program
