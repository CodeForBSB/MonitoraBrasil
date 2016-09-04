'use strict';

var Parse = require('parse/node');
var format=require('format-number');


var Monitora = {
	/*
	Build element for project
	*/
	buildElementProject: function(project){
		var mReturn = {
            title: project.get("tx_nome"),
            subtitle: project.get("nome_autor")+"\n"+project.get("txt_ementa"),
            //item_url: "https://www.oculus.com/en-us/rift/",
            //image_url: "http://www.camara.gov.br/internet/deputado/bandep/"+congressman.get("idCadastro")+".jpg",
            buttons: [ {
              type: "postback",
              title: "Ver ementa",
              payload: "{\"id_project\": \""+project.id+"\",\"nome\": \""+project.get("tx_nome")+"\", \"type\": \"project_ementa\"}",
            }],
          };
		  return mReturn;
	},

	/*
	  Build element for congressman with options: Spends, assiduity and projects
	*/
	buildElements:  function(congressman){
		var btns = [ {
			type: "postback",
			title: "Gastos - cota parlamentar",
			payload: "{\"id_politico\": \""+congressman.id+"\",\"casa\": \""+congressman.get('tipo')+"\",\"sexo\": \""+congressman.get('sexo')+"\",\"nome\": \""+congressman.get("nome")+"\", \"type\": \"gastos_cota\"}",
		},{
			type: "postback",
			title: "Projetos",
			payload: "{\"id_politico\": \""+congressman.id+"\",\"nome\": \""+congressman.get("nome")+"\", \"type\": \"projetos\"}",
		}];
		var titulo = "Senador";
		if(congressman.get('tipo').toLocaleLowerCase() === 'feminino')
			titulo = "Senadora";
		var urlPhoto = 'http://www.senado.gov.br/senadores/img/fotos-oficiais/senador'+congressman.get("idCadastro")+'.jpg';
		if(congressman.get('tipo') === 'c'){
			urlPhoto = "http://www.camara.gov.br/internet/deputado/bandep/"+congressman.get("idCadastro")+".jpg";
			btns.push({
				type: "postback",
				title: "Presença",
				payload: "{\"id_politico\": \""+congressman.id+"\",\"nome\": \""+congressman.get("nome")+"\", \"type\": \"presenca\"}",
			})
			titulo = "Deputado";

			if(congressman.get('tipo').toLocaleLowerCase() === 'feminino')
				titulo = "Deputada";
		}

		var mReturn = {
				title: titulo +" "+ congressman.get("nome"),
				subtitle: congressman.get("siglaPartido")+" - "+congressman.get("uf")+"\n"+congressman.get("telefone"),
				//item_url: "https://www.oculus.com/en-us/rift/",
				image_url: urlPhoto,
				buttons: btns,
			  };




			  return mReturn;
	},

	/*
	Search a congressman by name
	*/
	getPolitico: function(params, callback){
		var Politico = Parse.Object.extend("Politico");
		var query = new Parse.Query(Politico);
		//query.equalTo("tipo", "c");
		query.limit(10);
		if(params.firstName){
			query.startsWith("nome", params.firstName);
			if(params.fullName)
				query.startsWith("nome", params.fullName);
		}
		else{
			if(params.uf){
				query.equalTo("uf", params.uf);
			}
		}
		query.find({
		  success: function(objects) {
			// Successfully retrieved the object.
			var ret = [];
			  var json = {};
			  if(objects.length === 0){

				json.success = false;
				json.message = "Não encontrei nenhum parlamentar com esse nome";
			  }else{
					var cards = [];
					for (var i = 0; i < objects.length; i++) {
						  var object = objects[i];
						  cards.push(Monitora.buildElements(object));
					}
					json.success = true;
					json.qtd = objects.length;
					json.cards = cards;
				}
				ret.push(json);
				console.log (ret);
			  callback( json);

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
		senderID - sender id
	*/
	getRanking: function(params, callback){
		var Politico = Parse.Object.extend("Politico");
		var query = new Parse.Query(Politico);
		query.equalTo("tipo", "c");
		query.limit(3);
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
			var mensagem = "Ranking dos 3 que mais gastaram:\n"
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
		var CotaPorAno = Parse.Object.extend("CotaPorAno");
		var query = new Parse.Query(CotaPorAno);
		query.equalTo("politico", "Politico$"+json.politicoId);
		query.find({
		  success: function(objects) {
			// Successfully retrieved the object.
			var tt = "o";
			var senador = "senador";
			if(json.sexo.toLocaleLowerCase() === 'feminino'){
				tt = "a";
				senador = "senadora";
			}

			var mensagem = tt.toLocaleUpperCase()+" "+senador+" "+json.nome+" gastou:\n";
			if(json.casa === 'c')
		 		mensagem = tt.toLocaleUpperCase()+" deputad"+tt+" "+json.nome+" gastou:\n";
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
		Get congressman´s presence
		params: politicoId, nome
	 */

	getPresenca : function (params, callback){

		var json = params[0];
		var Presenca = Parse.Object.extend("Presenca");
		var query = new Parse.Query(Presenca);
		query.equalTo("politico", "Politico$"+json.politicoId);
		//console.log(qPolitico);
		query.find({
		  success: function(objects) {
			// Successfully retrieved the object.


			var mensagem = "Presença do(a) deputado(a) "+json.nome+" :\n";

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
	}
};

module.exports = Monitora;
