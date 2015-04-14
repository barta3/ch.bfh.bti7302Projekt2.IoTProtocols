// server.js - NodeJS server for the ERFA Project

// Load node modules
var fs = require('fs');
var sys = require('sys');
var http = require('http');
var sqlite3 = require('sqlite3');

// Use node-static module to server chart for client-side dynamic graph
var nodestatic = require('node-static');

// Setup static server for current directory
var staticServer = new nodestatic.Server(".");

// Setup database connection for logging
var db = new sqlite3.Database('./erfa.db');
//var db = new sqlite3.Database('./sample_database.db');

var mqtt    = require('mqtt');
var client  = mqtt.connect('mqtt://127.0.0.1');

client.subscribe('abaertschi2');

client.on('message', function (topic, message) {
  // message is Buffer
  console.log(message.toString());
  var statement = db.prepare("INSERT INTO temperature_records VALUES (?, ?)");
   // Insert values into prepared statement
   statement.run(Date.now(), +(message.toString()));
   // Execute the statement
   statement.finalize();
});

// Get temperature records from database
function selectTemp(num_records, start_date, callback){
   // - Num records is an SQL filter from latest record back trough time series, 
   // - start_date is the first date in the time-series required, 
   // - callback is the output function
   var current_temp = db.all("SELECT * FROM (SELECT * FROM temperature_records WHERE unix_time > (strftime('%s',?)*1000) ORDER BY unix_time DESC LIMIT ?) ORDER BY unix_time;", start_date, num_records,
      function(err, rows){
         if (err){
			   response.writeHead(500, { "Content-type": "text/html" });
			   response.end(err + "\n");
			   console.log('Error serving querying database. ' + err);
			   return;
				      }
         data = {temperature_record:[rows]}
         callback(data);
   });
};

// Setup node http server
var server = http.createServer(
	// Our main server function
	function(request, response)
	{
		// Grab the URL requested by the client and parse any query options
		var url = require('url').parse(request.url, true);
		var pathfile = url.pathname;
      var query = url.query;

		// Test to see if it's a database query
		if (pathfile == '/temperature_query.json'){
         // Test to see if number of observations was specified as url query
         if (query.num_obs){
            var num_obs = parseInt(query.num_obs);
         }
         else{
         // If not specified default to 20. Note use -1 in query string to get all.
            var num_obs = -1;
         }
         if (query.start_date){
            var start_date = query.start_date;
         }
         else{
            var start_date = '1970-01-01T00:00';
         }   
         // Send a message to console log
         console.log('Database query request from '+ request.connection.remoteAddress +' for ' + num_obs + ' records from ' + start_date+'.');
         // call selectTemp function to get data from database
         selectTemp(num_obs, start_date, function(data){
            response.writeHead(200, { "Content-type": "application/json" });		
	         response.end(JSON.stringify(data), "ascii");
         });
      return;
      }
      
      // Handler for favicon.ico requests
		if (pathfile == '/favicon.ico'){
			response.writeHead(200, {'Content-Type': 'image/x-icon'});
			response.end();

			// Optionally log favicon requests.
			//console.log('favicon requested');
			return;
		}


		else {
			// Print requested file to terminal
			console.log('Request from '+ request.connection.remoteAddress +' for: ' + pathfile);

			// Serve file using node-static			
			staticServer.serve(request, response, function (err, result) {
					if (err){
						// Log the error
						sys.error("Error serving " + request.url + " - " + err.message);
						
						// Respond to the client
						response.writeHead(err.status, err.headers);
						response.end('Error 404 - file not found');
						return;
						}
					return;	
					})
		}
});

// Enable server
server.listen(8000);
// Log message
console.log('Server running at http://localhost:8000');
