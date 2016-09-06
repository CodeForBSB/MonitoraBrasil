 'use strict';

const 
  bodyParser = require('body-parser'),
  config = require('config'),
  crypto = require('crypto'),
  express = require('express'),
  https = require('https'),  
  request = require('request');
  
  
  var Monitora = require('monitora-brasil');
  


var app = express();
app.set('port',  config.get('port'));
app.set('view engine', 'ejs');
//app.use(bodyParser.json({ verify: verifyRequestSignature }));


// Serve the Parse API on the /parse URL prefix
//var parseApiPath = '/monitorarest';
//app.use(parseApiPath, api);

var path = config.get('path');

app.get(path+'/getPoliticos/:casa', function(req, res) {	
	var pg = req.param('pg'); 
	var limit = req.param('limit');
	var uf = req.param('uf');
	var partido = req.param('partido');
	req.params.pg=pg;
	req.params.limit=limit;
	if(uf)
		req.params.uf=uf.toUpperCase();
	if(partido)
		req.params.siglaPartido=partido.toUpperCase();
	Monitora.getPoliticos(req.params, function(ret){	
		res.send(ret);
		
	});	
});

app.get(path+'/getRanking/:casa', function(req, res) {	
	var pg = req.param('pg'); 
	var limit = req.param('limit');
	var uf = req.param('uf');
	var partido = req.param('partido');
	req.params.pg=pg;
	req.params.limit=limit;
	if(uf)
		req.params.uf=uf.toUpperCase();
	if(partido)
		req.params.siglaPartido=partido.toUpperCase();
	Monitora.getRanking(req.params, function(ret){	
		res.send(ret);
		
	});	
});

app.get(path+'/searchprojeto/', function(req, res) {	
	var keys = req.param('keys'); 
	if(keys){
		req.params.keys= keys.split(',');
	}
	Monitora.searchProjects(req.params, function(ret){	
		res.send(ret);
		
	});	
});

app.get(path+'/getpolitico/', function(req, res) {	
	var objectId = req.param('id'); 
	if(objectId){
		req.params.objectId = objectId;
	}
	Monitora.getPolitico(req.params, function(ret){	
		res.send(ret);
		
	});	
});

app.get(path+'/getpartidos/', function(req, res) {	
	var partido = req.param('partido');	
	if(partido)
		req.params.siglaPartido=partido.toUpperCase();
	Monitora.getPartidos(req.params, function(ret){	
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