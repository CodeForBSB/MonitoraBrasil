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
			if(params.uf)
			query.equalTo("uf", params.uf);
			if(params.siglaPartido){
				if(params.siglaPartido === "PCDOB")
					params.siglaPartido = "PCdoB";
				query.equalTo("siglaPartido", params.siglaPartido);
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
				json.qtd = objects.length;
				json.politicos = objects;
				
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
	Search a congressman by id
	*/
	getPolitico: function(params, callback){
		var Politico = Parse.Object.extend("Politico");
		var query = new Parse.Query(Politico);
		var ret = [];
		query.get(params.objectId, {
		  success: function(politico) {
			ret.push(politico);
			callback(ret);
		  },
		  error: function(object, error) {
			ret.push(error);
			callback(ret);
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
		query.equalTo("tipo", params.casa);
		query.limit(parseInt(params.limit));
		query.skip(parseInt(params.pg));
		if(params.uf)
			query.equalTo("uf", params.uf);
		if(params.siglaPartido){
			if(params.siglaPartido === "PCDOB")
				params.siglaPartido = "PCdoB";
			query.equalTo("siglaPartido", params.siglaPartido);
		}
		query.descending("gastos");
		var ret = [];
		var json = {};
		query.find({
		  success: function(objects) {
			// Successfully retrieved the object.
			
			if(objects.length === 0){		
				json.success = false;
				json.message = "Sem resultado";	
			  }else{
				// Successfully retrieved the object.										
				json.success = true;
				json.qtd = objects.length;
				json.politicos = objects;
			  }
			  ret.push(json);
			  callback( ret);			
		  },
		  error: function(error) {
			json.success = false;
			json.message = "Sem resultado";	
			ret.push(json);
			callback( ret);
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
		console.log(params.keys);
		query.containsAll("words", params.keys);	
		//query.limit(10);	
		var ret = [];
		var json = {};
		query.find({
		  success: function(objects) {
			if(objects.length === 0){		
				json.success = false;
				json.message = "Sem resultado";	
			  }else{
				// Successfully retrieved the object.										
				json.success = true;
				json.qtd = objects.length;
				json.projetos = [];
				for (var i = 0; i < objects.length; i++) {
					var projeto = objects[i];
					
					json.projetos.push({"dt_apresentacao":projeto.get("dt_apresentacao"),
							"id_proposicao":projeto.get("id_proposicao"),
							"tx_ultimo_despacho":projeto.get("tx_ultimo_despacho"),
							"updatedAt":projeto.get("updatedAt"),
							"tx_orgao":projeto.get("tx_orgao"),
							"id_autor":projeto.get("id_autor"),
							"tp_casa":projeto.get("tp_casa"),
							"tx_orgao_estado":projeto.get("tx_orgao_estado"),
							"tx_link":projeto.get("tx_link"),
							"id_situacao":projeto.get("id_situacao"),
							"nr_ano":projeto.get("nr_ano"),
							"bl_votou":projeto.get("bl_votou"),
							"tp_proposicao":projeto.get("tp_proposicao"),
							"tx_nome":projeto.get("tx_nome"),
							"txt_ementa":projeto.get("txt_ementa"),
							"dt_ultimo_despacho":projeto.get("dt_ultimo_despacho"),
							"nome_autor":projeto.get("nome_autor"),
							"objectId":projeto.get("objectId")})
				}				
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