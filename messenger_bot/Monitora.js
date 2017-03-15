'use strict';

var Parse = require('parse/node');
var format=require('format-number');
const moment = require('moment');
var _ = require("underscore");

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
            buttons: [ 
			/*{
                "type":"web_url",
                "url":"https://monitorabrasil.com",
                "title":"Abrir site",
                "webview_height_ratio": "tall"
              },*/
			  {
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
		/*},{
			type: "postback",
			title: "Monitorar",
			payload: "{\"id_politico\": \""+congressman.id+"\",\"nome\": \""+congressman.get("nome")+"\", \"type\": \"monitorar\"}",
		*/}];
		var titulo = "Senador";
		if(congressman.get('sexo').toLocaleLowerCase() === 'feminino')
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

			if(congressman.get('sexo').toLocaleLowerCase() === 'feminino')
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
		console.log(params)
		if(params.firstName){
			query.startsWith("nome", params.firstName);
			if(params.fullName)
				query.startsWith("nome", params.fullName);
		}else{
			if(params.uf){
				query.equalTo("uf", params.uf);
			}
		}
		query.find({
		  success: function(objects) {
			// Successfully retrieved the object.
			  var json = {};
			  if(objects.length === 0){

				json.success = false;
				json.message = "Não encontrei nenhum parlamentar com esse nome";
			  }else{
					var cards = [];
					var politicos = []
					for (var i = 0; i < objects.length; i++) {
						  var object = objects[i];
						  politicos.push({idPolitico: object.id, nome: object.get('nome')})
						  cards.push(Monitora.buildElements(object));
					}
					json.success = true;
					json.qtd = objects.length;
					json.cards = cards;
					json.politicos = politicos
				}
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
		//console.log(params);
		var Politico = Parse.Object.extend("Politico");
		var query = new Parse.Query(Politico);
		query.equalTo("tipo", params.casa);
		query.limit(3);
		if(params.uf){
			if(params.uf != 'null')
				query.equalTo("uf", params.uf);
		}

		if(params.siglaPartido){
			if(params.siglaPartido != 'null'){
				if(params.siglaPartido === "PCDOB")
					params.siglaPartido = "PCdoB";
				query.equalTo("siglaPartido", params.siglaPartido);
			}

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
			if(objects.length == 0){
				 mensagem = mensagem + " Aparentemente não gastou nada!!! Parabéns!\n"
			}
			var total = 0;
			for (var i = 0; i < objects.length; i++) {
				  var object = objects[i];
				  var formattedNumber = format({prefix: 'R$ ',integerSeparator : '.', decimal: ',', round : 2})
					(object.get("total"), {noSeparator: false});
				 mensagem = mensagem + "Ano "+object.get("ano") +": "+formattedNumber+"\n";
				 total = total + object.get("total");
			}
			var fTotal = format({prefix: 'R$ ',integerSeparator : '.', decimal: ',', round : 2})
					(total, {noSeparator: false});
			mensagem = mensagem + "Total: "+fTotal;
			var ret = [];
			ret.push(mensagem);
			
			if(total > 0){
				var compare = Monitora.getCompare(total, function(comp){
					//console.log(comp);
					var compareText = "Esse valor equivale a "+comp.valueItem+" "+comp.descriptionItem;
					ret.push(compareText);
					callback( ret);
				});
			}	else{
				callback( ret);
			}		
			
			

		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
	},
	
	/*
		Compare congressman´s spending
		params: total
	 */

	getCompare : function (total, callback){
		var ReferenciaCusto = Parse.Object.extend("ReferenciaCusto");
		var query = new Parse.Query(ReferenciaCusto);
		query.find({
		  success: function(objects) {
			var item = objects[Math.floor(Math.random()*objects.length)];
			var valueItem = item.get('valor');
			var compareValue = total/valueItem;
			var descriptionItem = item.get('Produto');
			var fCompareValue = format({prefix: '',integerSeparator : '.', decimal: ',', round : 0})
					(compareValue, {noSeparator: false});
			var ret = {};
			ret.descriptionItem = descriptionItem;
			ret.valueItem = fCompareValue;
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
	}, 
	/*
		Search user´s that interact with bot
		params: userId
	 */

	searchUser : function (params, callback){

		
		var UserBot = Parse.Object.extend("UserBot");
		var query = new Parse.Query(UserBot);
		query.equalTo("userId", params.userId);
		//console.log(qPolitico);
		query.find({
		  success: function(objects) {
			// Successfully retrieved the object.
			
			var mensagem = {};
			if(objects.length == 0){
				mensagem.encontrou = false
			}else{
				mensagem.encontrou = true
				mensagem.usuario = objects[0]
			}
			callback( mensagem);

		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
	}, 
	
	/*
		Save user´s that interact with bot
		params: userId
	 */

	saveUser : function (params, callback){

		var UserBot = Parse.Object.extend("UserBot");
		var userBot = new UserBot();
		userBot.set("userId",params.userId);

		userBot.save(null, {
		  success: function(userBot) {			  
			// Execute any logic that should take place after the object is saved.
			console.log('New object created with objectId: ' + userBot.id);
			callback(userBot)
		  },
		  error: function(userBot, error) {
			// Execute any logic that should take place if the save fails.
			// error is a Parse.Error with an error code and message.
			console.log('Failed to create new object, with error code: ' + error.message);
		  }
		});		
	},
	
	/*
		Save user´s that interact with facebook´s page
		params: userId
	 */

	saveUsersPagina : function (users){
		var mb = this;
		_.map(users, function(user){
			mb.hasUserPagina(user.userId, function(ret){
				
				if(!ret){					
					var UserPagina = Parse.Object.extend("UserPagina");
					var userPagina = new UserPagina();
					userPagina.set("userId",user.userId);
					userPagina.set("nome",user.nome);					
					userPagina.set("likes",user.likes);
					userPagina.set("nrlikes",user.nrlikes);
					userPagina.set("comments",user.comments);
					userPagina.set("nrcomments",user.nrcomments);
					userPagina.set("shares",user.shares);
					userPagina.set("nrshares",user.nrshares);
					
					userPagina.save();		
				}else{					
					_.map(user.likes, function(like){
						if(ret.get('likes').indexOf(like) < 0){
							ret.addUnique("likes",like);	
							ret.increment("nrlikes");
						}
					})
					
					_.map(user.comments, function(comment){
						if(ret.get('comments').indexOf(comment) < 0){
							ret.addUnique("comments",comment);	
							ret.increment("nrcomments");
						}
					})	
						
					_.map(user.shares, function(share){
						if(ret.get('shares').indexOf(share) < 0){
							ret.addUnique("shares",share);	
							ret.increment("nrshares");
						}
					})	
					
					ret.save();
				}
			})
		})
		
			
	},
	
	saveUsersPaginaSemana : function (users, semana){
		var mb = this;
		_.map(users, function(user){
			mb.hasUserPaginaSemana(user.userId, function(ret){
				
				if(!ret){					
					var UserPagina = Parse.Object.extend("UserPaginaSemana");
					var userPagina = new UserPagina();
					userPagina.set("userId",user.userId);
					userPagina.set("nome",user.nome);					
					userPagina.set("likes",user.likes);
					userPagina.set("nrlikes",user.nrlikes);
					userPagina.set("comments",user.comments);
					userPagina.set("nrcomments",user.nrcomments);
					userPagina.set("shares",user.shares);
					userPagina.set("nrshares",user.nrshares);
					userPagina.set("semana",semana);
					
					userPagina.save();		
				}else{					
					_.map(user.likes, function(like){
						if(ret.get('likes').indexOf(like) < 0){
							ret.addUnique("likes",like);	
							ret.increment("nrlikes");
						}
					})
					
					_.map(user.comments, function(comment){
						if(ret.get('comments').indexOf(comment) < 0){
							ret.addUnique("comments",comment);	
							ret.increment("nrcomments");
						}
					})	
						
					_.map(user.shares, function(share){
						if(ret.get('shares').indexOf(share) < 0){
							ret.addUnique("shares",share);	
							ret.increment("nrshares");
						}
					})	
					
					ret.save();
				}
			})
		})
		
			
	},
	
	/*DEPRECATED
		Save user´s that interact with facebook´s page
		params: userId 
	 */

	saveUserPagina : function (params){
		this.hasUserPagina(params, function(ret){
			//console.log(params)
			if(!ret){
				var UserPagina = Parse.Object.extend("UserPagina");
				var userPagina = new UserPagina();
				userPagina.set("userId",params.userId);
				userPagina.set("nome",params.nome);
				if(params.likes)
					userPagina.add("likes",params.postId);
					userPagina.set("nrlikes",1);
				if(params.comments)
					userPagina.add("comments",params.postId);
					userPagina.set("nrcomments",1);
				userPagina.save();	
			}else{
				console.log("ja existe "+params.nome)
				if(params.likes){
					if(ret.get('likes').indexOf(params.postId) < 0){
						ret.addUnique("likes",params.postId);	
						ret.increment("nrlikes");
						
					}
					
				}
				if(params.comments){
					if(ret.get('likes').indexOf(params.postId) < 0){
						ret.addUnique("comments",params.postId);		
						ret.increment("nrcomments");
					}
					
				}
				ret.save();
			}
		})
			
	},
	/*
		Search if a user facebook´s page
		params: userId
	 */
	hasUserPagina: function (userId, callback){
		var UserPagina = Parse.Object.extend("UserPagina");
		var query = new Parse.Query(UserPagina);
		query.equalTo("userId", userId);
		query.first({
		  success: function(object) {
			if(object){
				callback(object)
			}else{
				callback(false)
			}
		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
	},
	
	hasUserPaginaSemana: function (userId, callback){
		var UserPagina = Parse.Object.extend("UserPaginaSemana");
		var query = new Parse.Query(UserPagina);
		query.equalTo("userId", userId);
		query.first({
		  success: function(object) {
			if(object){
				callback(object)
			}else{
				callback(false)
			}
		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
	},
	
	getInteraction : function (params, callback){

		
		var UserBot = Parse.Object.extend("UserBotInteraction");
		var query = new Parse.Query(UserBot);
		query.equalTo("userId", params.userId);
		query.equalTo("data", moment().format('YYYYMMDD'));
		query.find({
		  success: function(objects) {
			// Successfully retrieved the object.
			
			var mensagem = {};
			if(objects.length == 0){
				mensagem.num = 0
			}else{
				
				mensagem.num = objects[0].get("interacao")
				mensagem.descricaoInt = objects[0].get("descricao")
			}
			callback( mensagem);

		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
	}, 
	
	/*
		Save user to a politian
		params: userId, idPolitico, nome
	 */
	saveUserPolitico: function (params){
		var UserBotMonitoraPolitico = Parse.Object.extend("UserBotMonitoraPolitico");
		var userBotMonitoraPolitico = new UserBotMonitoraPolitico();
		userBotMonitoraPolitico.set("userId",params.userId);
		userBotMonitoraPolitico.set("idPolitico",params.idPolitico);
		var Politico = Parse.Object.extend("Politico");
		var politico = Politico.createWithoutData(params.idPolitico);
		userBotMonitoraPolitico.set("politico",politico);
		userBotMonitoraPolitico.save()
	},
	
	/*
		Search if a user has a poltitian
		params: userId, idPolitico
	 */
	hasUserPolitico: function (params, callback){
		var UserBotMonitoraPolitico = Parse.Object.extend("UserBotMonitoraPolitico");
		var query = new Parse.Query(UserBotMonitoraPolitico);
		query.equalTo("userId", params.userId);
		query.equalTo("idPolitico", params.idPolitico);
		query.first({
		  success: function(object) {
			if(object){
				callback(true)
			}else{
				callback(false)
			}
			

		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
	},
	
	/*
		Search and return all politicians
		params: userId
	 */
	getUserPoliticos: function (params, callback){
		var UserBotMonitoraPolitico = Parse.Object.extend("UserBotMonitoraPolitico");
		var query = new Parse.Query(UserBotMonitoraPolitico);
		query.equalTo("userId", params.userId);
		query.include("politico");
		query.find({
		  success: function(objects) {			
			callback(objects)

		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
	},
	
	/*
		Save Interaction with bot
		params: userId
	 */

	saveInteraction : function (params, callback){
		
		var UserBot = Parse.Object.extend("UserBotInteraction");
		var query = new Parse.Query(UserBot);
		query.equalTo("userId", params.userId);
		query.equalTo("data", moment().format('YYYYMMDD'));
		//console.log(qPolitico);
		query.find({
		  success: function(objects) {
			// Successfully retrieved the object.
			//console.log(objects)
			var mensagem = {};
			if(objects.length > 0){
				objects[0].increment("interacao");
				objects[0].set("descricao",params.descricaoInt);
				objects[0].save();
			}else{
				var UserBotInteraction = Parse.Object.extend("UserBotInteraction");
				var userBotInteraction = new UserBotInteraction();
				userBotInteraction.set("userId",params.userId);
				userBotInteraction.set("descricao",params.descricaoInt);
				userBotInteraction.set("interacao",1);
				userBotInteraction.set("data",moment().format('YYYYMMDD'))
				userBotInteraction.save(null, {
					  success: function(userBot2) {			  
						// Execute any logic that should take place after the object is saved.
						//console.log('New object created with objectId: ' + userBot2.id);
						
					  },
					  error: function(userBot, error) {
						// Execute any logic that should take place if the save fails.
						// error is a Parse.Error with an error code and message.
						console.log('Failed to create new object, with error code: ' + error.message);
					  }
					});		
			}
			//callback( mensagem);

		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
		
		
		
	},
	
	/*
		ADMIN
	 */
	getPoliticoMonitorados: function (callback){
		var UserBotMonitoraPolitico = Parse.Object.extend("UserBotMonitoraPolitico");
		var query = new Parse.Query(UserBotMonitoraPolitico);
		query.include("politico");
		query.find({
		  success: function(objects) {			
			callback(objects)

		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
	},
	
	/*
		Save post´s Monitora
		params: userId, idPolitico, nome
	 */
	saveFacebookPost: function (params){
		this.hasPostFacebook(params, function(ret){
			if(!ret){
				params.facebookPost.save()
			}
		})
		
	},
	
	/*
		Search if a user has a poltitian
		params: userId, idPolitico
	 */
	hasPostFacebook: function (params, callback){
		var FacebookPost = Parse.Object.extend("FacebookPost");
		var query = new Parse.Query(FacebookPost);
		query.equalTo("idPost", params.facebookPost.get('idPost'));
		query.first({
		  success: function(object) {
			if(object){
				callback(true)
			}else{
				callback(false)
			}
		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
	},
	
	/*
		Search if a user has a poltitian
		params: userId, idPolitico
	 */
	getPostsFacebook: function (params, callback){
		var FacebookPost = Parse.Object.extend("FacebookPost");
		var query = new Parse.Query(FacebookPost);		
		query.descending('createdAt');
		query.find({
		  success: function(objects) {
			 callback(objects);
		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
	},
	
		/*
		Search if a user has a poltitian
		params: userId, idPolitico
	 */
	getUsuariosPagina: function (params, callback){
		var UserPagina = Parse.Object.extend(params);
		var query = new Parse.Query(UserPagina);		
		query.limit(1000);
		query.find({
		  success: function(objects) {
			 callback(objects);
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

	sendText4all : function (params, callback){		
		var UserBot = Parse.Object.extend("UserBot");
		var query = new Parse.Query(UserBot);
		query.find({
		  success: function(objects) {
			var ret = [];
			for (var i = 0; i < objects.length; i++) {
				 var object = objects[i];
				 ret.push(object.get("userId"))
			}			
			callback( ret);

		  },
		  error: function(error) {
			console.log("Error: " + error.code + " " + error.message);
		  }
		});
	}
	
};

module.exports = Monitora;
