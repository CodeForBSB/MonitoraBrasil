 'use strict';

const 
  bodyParser = require('body-parser'),
  config = require('config'),
  crypto = require('crypto'),
  express = require('express'),
  https = require('https'),  
  request = require('request');
  
var Parse = require('parse/node');
var Monitora = require('./Monitora');

Parse.initialize(config.get('parse_id_app'), config.get('parse_js_key'));
Parse.serverURL = config.get('parse_url');

var app = express();
app.set('port',  config.get('port'));
app.set('view engine', 'ejs');
//app.use(bodyParser.json({ verify: verifyRequestSignature }));


// Serve the Parse API on the /parse URL prefix
//var parseApiPath = '/monitorarest';
//app.use(parseApiPath, api);


app.get('/monitorarest/getPoliticos/:casa', function(req, res) {	
	var pg = req.param('pg'); 
	var limit = req.param('limit');
	req.params.pg=pg;
	req.params.limit=limit;
	Monitora.getPoliticos(req.params, function(ret){	
		res.send(ret);
		
	});	
});



// Start server
// Webhooks must be available via SSL with a certificate signed by a valid 
// certificate authority.
app.listen(app.get('port'), function() {
  console.log('Node app is running on port', app.get('port'));
});

module.exports = app;