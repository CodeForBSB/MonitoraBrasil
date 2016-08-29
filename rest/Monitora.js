'use strict';

var Parse = require('parse/node');
var format=require('format-number');


var Monitora = {
	
	/*
	Search a congressman by name
	*/
	getPoliticos: function(params, callback){
		var Politico = Parse.Object.extend("Politico");
		var query = new Parse.Query(Politico);
		query.equalTo("tipo", params.casa);
		query.limit(parseInt(params.limit));
		query.skip(parseInt(params.pg));
		if(params.fullName){
			query.equalTo("nome", params.fullName);
			query.startsWith("nome", params.firstName);
		}			
		else{
			if(params.uf){
				query.equalTo("uf", params.uf);
			}
		}
		query.find({
		  success: function(objects) {
			  var ret = [];
			  var json = {};
			  if(objects.length === 0){		
				
				json.success = false;
				json.message = "Não encontrei nenhum parlamentar com esse nome";
				
				
			  }else{
				// Successfully retrieved the object.										
				json.success = true;
				json.objects = objects;
				
			  }
			  ret.push(json);
			  callback( ret);
			
		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
	},
	
	/*
	get congressman spending´s rank 
	params 
		uf - state of congressman
		siglaPartido - parties
		limit - quantity of return´s itens
	*/
	getRanking: function(params, callback){
		var Politico = Parse.Object.extend("Politico");
		var query = new Parse.Query(Politico);
		query.equalTo("tipo", "c");
		query.limit(params.limit);
		if(params.uf)
			query.equalTo("uf", params.uf);
		if(params.siglaPartido){
			if(params.siglaPartido === "PCDOB")
				params.siglaPartido = "PCdoB";
			query.equalTo("siglaPartido", params.siglaPartido);
		}
		query.descending("gastos");
		query.find({
		  success: function(objects) {
			// Successfully retrieved the object.
			//var cards = [];
			var mensagem = "Ranking dos "+params.limit+" que mais gastaram:\n"
			for (var i = 0; i < objects.length; i++) {
				var object = objects[i];
				var formattedNumber = format({prefix: 'R$ ',integerSeparator : '.', decimal: ',', round : 2})
					(object.get("gastos"), {noSeparator: false});
				mensagem = mensagem + (i+1)+"º "+object.get("nome")+" ("+object.get("siglaPartido")+"-"+
										object.get("uf")+") "+formattedNumber+"\n";
			}
			var ret = [];
			ret.push(mensagem);
			callback( ret);
		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
	},
	
	 /*
		Get projects filter by keywords
		params: keywords
	 */
	
	searchProjects : function (params, callback){
		var Politico = Parse.Object.extend("Proposicao");
		var query = new Parse.Query(Politico);
		query.containsAll("words", params.keys);	
		query.limit(10);	
		query.find({
		  success: function(objects) {
			var cards = [];
			if(objects.length > 0){
				  // Successfully retrieved the object.
					for (var i = 0; i < objects.length; i++) {
						  var object = objects[i];
						  cards.push(Monitora.buildElementProject(object));
					}
					
					//console.log(cards);
					
			  }
			  callback(cards);
			
		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
	},
	
	/*
		 Get congressman projects
		params: politicoId, nome
	 */
	
	getProjects : function (params, callback){		
		
		//var json = params[0];
		var Proposicao = Parse.Object.extend("Proposicao");
		var query = new Parse.Query(Proposicao);
		query.equalTo("autor", "Politico$"+params.politicoId);	
		query.limit(10);	
		query.find({
		  success: function(objects) {
			// Successfully retrieved the object.
			var cards = [];
			for (var i = 0; i < objects.length; i++) {
				var object = objects[i];
				cards.push(Monitora.buildElementProject(object));
			}
			callback(cards);
			
		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});		
	},
	
	/*
		 Get congressman´s spending
		params: politicoId, nome
	 */
	
	getGastos : function (params, callback){		
		
		var json = params[0];
		var gender = Monitora.getGender(json.gender);
		var CotaPorAno = Parse.Object.extend("CotaPorAno");
		var query = new Parse.Query(CotaPorAno);
		query.equalTo("politico", "Politico$"+json.politicoId);	
		query.find({
		  success: function(objects) {
			// Successfully retrieved the object.
			// Successfully retrieved the object.
			var mensagem = gender.toUpperCase()+" deputad"+gender+" "+json.nome+" gastou:\n";
			//console.log(objects.length);
			for (var i = 0; i < objects.length; i++) {
				  var object = objects[i];
				  var formattedNumber = format({prefix: 'R$ ',integerSeparator : '.', decimal: ',', round : 2})
					(object.get("total"), {noSeparator: false});
				 mensagem = mensagem + "Ano "+object.get("ano") +": "+formattedNumber+"\n";
			}
			var ret = [];
			ret.push(mensagem);
			callback( ret);
			
		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});		
	},
	
	/*
		 Get congressman´s spending by categories
		params: politicoId, nome,limit
	 */
	
	getSpendingCategories : function (params, callback){		
		
		
		var gender = Monitora.getGender(params.gender);
		var CotaXCategoria = Parse.Object.extend("CotaXCategoria");
		var query = new Parse.Query(CotaXCategoria);
		query.equalTo("politico", "Politico$"+params.politicoId);	
		query.limit(params.limit);
		query.descending("total");
		query.find({
		  success: function(objects) {
			  //console.log(objects);
			// Successfully retrieved the object.
			var mensagem = "Gastou mais em: \n";
			//console.log(objects.length);
			for (var i = 0; i < objects.length; i++) {
				  var object = objects[i];
				  var formattedNumber = format({prefix: 'R$ ',integerSeparator : '.', decimal: ',', round : 2})
					(object.get("total"), {noSeparator: false});
					
				 mensagem = mensagem + "Ano "+object.get("ano") +": "+formattedNumber+" em "+
				 Monitora.cutCategory(object.get("tpCota"))+"\n";
			}
			
			//if(message.length > 320){
			//	message = message.substring(0, 319)					
			//}
			var ret = [];
			ret.push(mensagem);
			callback( ret);
			
		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});		
	},
	
	
	/*
		Get congressman´s presence
		params: politicoId, nome
	 */
	
	getPresenca : function (params, callback){		
		
		var json = params[0];
		var gender = Monitora.getGender(json.gender);
		var Presenca = Parse.Object.extend("Presenca");
		var query = new Parse.Query(Presenca);
		query.equalTo("politico", "Politico$"+json.politicoId);	
		//console.log(qPolitico);
		query.find({
		  success: function(objects) {
			// Successfully retrieved the object.
			
			
			var mensagem = "Presença d"+gender+" deputad"+gender+" "+json.nome+" :\n";
			
			for (var i = 0; i < objects.length; i++) {
				  var object = objects[i];
				 mensagem = mensagem + "Ano: "+object.get("nr_ano") +"\n     ->Faltas "+
				 (object.get("nr_ausencia_justificada")+object.get("nr_ausencia_nao_justificada"))+"\n"+
				 "     ->Presença:"+object.get("nr_presenca")+"\n";
			}
			//console.log(mensagem);
			var ret = [];
			ret.push(mensagem);
			callback( ret);
			
		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});		
	},
	
	/* 
		get congressman´s gender 
		can be (o) -> male
		or (a) -> female
	*/
	
	getGender: function (gender){
		if(gender === "masculino")
			return "o";
		return "a";
	},
	
	/* 
		get congressman´s gender 
		can be (o) -> male
		or (a) -> female
	*/
	
	cutCategory: function (cat){
		cat = cat.replace("MANUTENÇÃO DE ESCRITÓRIO DE APOIO À ATIVIDADE PARLAMENTAR",'Manutenção de escritório');
		cat = cat.replace("CONSULTORIAS PESQUISAS E TRABALHOS TÉCNICOS",'Consultorias, pesquisas e trab. técn.');
		cat = cat.replace("DIVULGAÇÃO DA ATIVIDADE PARLAMENTAR",'Divulgação de ativid. parlam.');
		return cat;
	}
};

module.exports = Monitora;