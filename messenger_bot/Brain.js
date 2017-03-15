'use strict';

var Parse = require('parse/node');
var format=require('format-number');
var Monitora = require('./Monitora');

var Brain = {
	
	analiseQuickReply: function(params, callback){
		var brain = this;
		var retMensagens = []		
		switch (params.tipo) {
			case "PASSO_4":
				switch ( params.escolha ){
					case "sim":
						this.saveInteraction(params.senderID, "passo4_nome")
						retMensagens.push({mensagem:"Digite o nome do(a) político(a):", tipo:"text"})
						callback(retMensagens)
					break;
					
					case "nao":
						this.saveInteraction(params.senderID, "passo4_uf")
						retMensagens.push({
							mensagem:brain.getQuickReply4(
								params.senderID,
								"Quer saber dos políticos de sua uf?",
								"PASSO_5"), 
							tipo:"quickReply"})				
						callback(retMensagens)
					break;
					
					case "menu":
						retMensagens.push({
							mensagem:brain.getQuickReplyMenuPrincipal(
								params.senderID,
								"O que quer saber?"), 
							tipo:"quickReply"})				
						callback(retMensagens)
					break;
					
					default:
					/*	brain.buscarPolitico(params,function(retPolitico){
							callback(retPolitico);
						})*/
					break;
				}				
			break;
			
			//passo 5, escolha de uf
			case "PASSO_5":
				if(params.escolha === "sim"){
					this.saveInteraction(params.senderID, "passo5_uf")					
					retMensagens.push({mensagem:"Qual sua uf?", tipo:"text"})
					callback(retMensagens)
				}else{
					this.saveInteraction(params.senderID, "passo5_uf")
					retMensagens.push({
						mensagem:brain.getQuickReply4(
							params.senderID,
							unescape('Tenho uma coisa interessante para te mostrar. Quer vem qual político gasta mais?'),
							"PASSO_6"), 
						tipo:"quickReply"})				
					callback(retMensagens)
				}
			break;
			
			//passo 6, RANK
			case "PASSO_6":
				if(params.escolha === "sim"){
					this.saveInteraction(params.senderID, "passo6_rank")
					//enviar rank
					retMensagens.push({
						mensagem:brain.getQuickReplyCasa(
							params.senderID,
							unescape('Qual casa?'),
							"PASSO_6_1"), 
						tipo:"quickReply"})	
					callback(retMensagens)
				}else{
					this.saveInteraction(params.senderID, "passo6_rank")
					retMensagens.push({
						mensagem:brain.getQuickReply4(
							params.senderID,
							unescape('Quer pesquisar projetos?'),
							"PASSO_7"), 
						tipo:"quickReply"})				
					callback(retMensagens)
				}
			break;
			
			//passo 6_1, RANK
			case "PASSO_6_1":
				this.saveInteraction(params.senderID, "passo61_rank")
				Monitora.getRanking({casa: params.escolha}, function(ret){
					retMensagens.push({mensagem:ret[0], tipo:"text"})
					//enviar mensagem com opcao para uf ou partido
					/*retMensagens.push({
						mensagem:brain.getQuickReplyRank(
							params.senderID,
							unescape('Quer ver o ranking por uf ou partido?'),
							"PASSO_6_2",params.escolha), 
						tipo:"quickReply"})*/
					retMensagens.push({
							mensagem:brain.getQuickReplyMenuPrincipal(
								params.senderID,
								"Mais alguma coisa? Gosto muito de dar informações desses políticos!! ;)"), 
							tipo:"quickReply"})		
					callback(retMensagens)
				})
			break;
			
			//passo 6_2, RANK
			case "PASSO_6_2":
				this.saveInteraction(params.senderID, "passo62_rank")
				switch (params.escolha){
					case 'uf':
						retMensagens.push({mensagem:"Qual uf?", tipo:"text"})
						callback(retMensagens)
					break;
					case 'partido':
						retMensagens.push({mensagem:"Qual partido?", tipo:"text"})
						callback(retMensagens)
					break;				
				}
			break;
			
			//passo 6_1, RANK
			case "MENU_PRINCIPAL":
				switch (params.escolha){
					case 'rank':
						this.saveInteraction(params.senderID, "passo6_rank")
						retMensagens.push({
						mensagem:brain.getQuickReplyCasa(
							params.senderID,
							unescape('Qual casa?'),
							"PASSO_6_1"), 
						tipo:"quickReply"})	
						callback(retMensagens)
					break;
					
					case 'politicos':
						this.saveInteraction(params.senderID, "passo4_nome")
						retMensagens.push({mensagem:"Digite o nome do(a) político(a):", tipo:"text"})
						callback(retMensagens)
					break;
					
					case 'inscricoes':						
						this.saveInteraction(params.senderID, "passo4_inscricoes")
						Monitora.getUserPoliticos({userId:params.senderID}, function(ret){
							var mensagem = ""
							if(ret.length > 0){
								mensagem = "Políticos monitorados:\n"
								for(var i=0; i < ret.length; i++){
									var politico = ret[i].get("politico");
									mensagem = mensagem + politico.get("nome")+"\n";
								}
							}else{
								mensagem = "Você não está monitorando nenhum político ainda. "
							}
							
							retMensagens.push({mensagem:mensagem, tipo:"text"})
							retMensagens.push({
							mensagem:brain.getQuickReplyMenuPrincipal(
								params.senderID,
								"Mais alguma coisa? Tenho vários outros políticos no meu cérebro :)"), 
							tipo:"quickReply"})	
							callback(retMensagens)
						})
					break;
					
					getUserPoliticos
				}
				
			break;
			
			case "MONITORAR_POLITICO":				
				switch (params.escolha){
					case 'sim':
						//salvar o monitoramento
						Monitora.saveUserPolitico({userId: params.senderID, idPolitico: params.payload.idPolitico})
						retMensagens.push({mensagem:"Ok, vc receberá informações do que ele(a) está fazendo. ;)", tipo:"text"})
						retMensagens.push({
							mensagem:brain.getQuickReplyMenuPrincipal(
								params.senderID,
								"O que mais deseja?"), 
							tipo:"quickReply"})	
						callback(retMensagens)
					break;
				}
			break;
		}		
	},
	
	analiseMensagem: function(params, callback){
		//verifica se ja existe o usuario, se nao existir cria um
		var user = {}
		var retMensagens =[];
		var brain = this;
		var mMensagem = params.mensagem
		this.saveUser(params.senderID, function(ret){
			user = ret.user
			//se usuario novo, enviar primeira mensagem e salvar iteração
			if(ret.isNew){				
				retMensagens.push({mensagem:"Eu sou um robô. Vou te ajudar a acompanhar o que os políticos estão fazendo.", tipo:"text"})				
				retMensagens.push({
					mensagem:brain.getQuickReply4(
						params.senderID,
						"Você quer saber alguma coisa de um(a) político(a) específico(a)?",
						"PASSO_4"
						), 
					tipo:"quickReply"})				
				//salvar interação
				brain.saveInteraction(params.senderID, "passo2")
				callback(retMensagens)				
			}else{
				//verificar se é a primeira interação do dia				
				brain.getInteraction(params.senderID, function(ret2){
					//console.log(ret2)
					if(ret2.num === 0){
						brain.saveInteraction(params.senderID, "volta_user")
						retMensagens.push({mensagem:"Você está de volta, o que posso ajudar?", tipo:"text"})
						retMensagens.push({mensagem:brain.getQuickReply4(params.senderID,"Você quer saber alguma coisa de um(a) político(a) específico(a)?",
						"PASSO_4"), tipo:"quickReply"})				
						
						callback(retMensagens)
					}else{
						//verificar qual passo da interação
						switch (ret2.descricaoInt) {
							case 'passo4_nome'://buscar politico								
								brain.saveInteraction(params.senderID, "busca_politico")
								brain.buscarPolitico(mMensagem,function(retPolitico){									
									callback(retPolitico.retMensagens);
								})
							break;
								
							case 'passo5_uf'://buscar politicos de ufs
								brain.saveInteraction(params.senderID, "busca_politico_uf")
								var uf = mMensagem.toLocaleUpperCase().trim();
								Monitora.getPolitico({uf:uf}, function(ret3){
																	
									if(ret3.success){
										retMensagens.push({mensagem:"Encontrei! ;)", tipo:"text"})
										retMensagens.push({mensagem:ret3.cards, tipo:"cards"})
													
										//sendFileCongressman(ret.cards, senderID);
									}else{
										brain.saveInteraction(params.senderID, "passo5_uf")	
										retMensagens.push({mensagem:"Não encontrei :( Informe a sigla. Exemplo: SP", tipo:"text"})
									}
									callback(retMensagens)
								});		
								break;								
							
								
							default:
								brain.saveInteraction(params.senderID, "busca_politico")
								brain.buscarPolitico(mMensagem,function(retPolitico){									
									if(retPolitico.success){
										callback(retPolitico.retMensagens);
									}else{
										//verificar se tem alguma palavra que quer finalizar a conversa
										if(mMensagem.toLocaleUpperCase().trim() === "TCHAU"){
											retMensagens.push({mensagem:"Volte sempre e fique informado sobre o que os políticos estão fazendo ;)", tipo:"text"})
										}else{
											retMensagens.push(
											{mensagem:brain.getQuickReply4(
												params.senderID,"Quer saber algo de outro político?",
												"PASSO_4"), 
											tipo:"quickReply"})
										}														
										callback(retMensagens)
									}
									
								})
							break;
						}
						
					}
					
				})
			}
		})
	},
	
	buscarPolitico: function(mMensagem, callback){
		var retMensagens = []
		var ret ={}
		var par = mMensagem.split(" ");

		var firstName = par[0].toLocaleUpperCase().trim();
		var fullName = null;
		if(par.length > 1){
		  var fullName = par[0].toLocaleUpperCase().trim()+" "+par[1].toLocaleUpperCase().trim();
		}				
		Monitora.getPolitico({firstName:firstName, fullName:fullName}, function(ret3){
									
			if(ret3.success){
				ret.success = true;
				retMensagens.push({mensagem:"Encontrei! ;)", tipo:"text"})
				retMensagens.push({mensagem:ret3.cards, tipo:"cards"})
				//se tiver encontrado um mostrar a presenca			
				//sendFileCongressman(ret.cards, senderID);
			}else{
				ret.success = false;
				retMensagens.push({mensagem:"Não encontrei :( Tem certeza que é esse o nome? Tenho as fichas de deputados federais e senadores.", tipo:"text"})
				
			}
			ret.retMensagens = retMensagens;
			callback(ret)
		});		
	},
	
	enviaMensagemMonitorar:function(params,callback){
		var brain = this;
		//verificar se ja esta monitorando
		Monitora.hasUserPolitico({userId: params.senderID, idPolitico: params.idPolitico}, function(ret){			
			if(!ret){
				//verificar se ja enviou hoje a mensagem de monitorar
		
				callback(brain.getQuickReplyGeral(params.senderID, 
					"Você quer monitorar "+params.nome+"?",
					"MONITORAR_POLITICO", 
					",\"idPolitico\":\""+params.idPolitico+"\""))	
			}
		})
		
		
	},
	
	getQuickReplyMenuPrincipal: function (senderID,texto){
		var messageData = {
			recipient: {
				id: senderID
			},
			message: {
				text: texto,
				metadata: "MENU_PRINCIPAL",
				quick_replies: [
					{
						"content_type":"text",
						"title":"Políticos",
						"payload":"{\"escolha\":\"politicos\",\"type\":\"MENU_PRINCIPAL\"}"
					},
					{
						"content_type":"text",
						"title":"Ranking",
						"payload":"{\"escolha\":\"rank\",\"type\":\"MENU_PRINCIPAL\"}"
					},
					{
						"content_type":"text",
						"title":"Minhas inscrições",
						"payload":"{\"escolha\":\"inscricoes\",\"type\":\"MENU_PRINCIPAL\"}"
					}
				]
			}
		};
		return messageData;
	},
	
	
	getQuickReplyRank: function (senderID,texto, flag,casa){
		var messageData = {
			recipient: {
				id: senderID
			},
			message: {
				text: texto,
				metadata: flag,
				quick_replies: [
					{
						"content_type":"text",
						"title":"Por UF",
						"payload":"{\"escolha\":\"uf\",\"casa\":\""+casa+"\",\"type\":\""+flag+"\"}"
					},
					{
						"content_type":"text",
						"title":"Por Partido",
						"payload":"{\"escolha\":\"partido\",\"casa\":\""+casa+"\",\"type\":\""+flag+"\"}"
					}
				]
			}
		};
		return messageData;
	},
	
	getQuickReplyCasa: function (senderID,texto, flag){
		var messageData = {
			recipient: {
				id: senderID
			},
			message: {
				text: texto,
				metadata: flag,
				quick_replies: [
					{
						"content_type":"text",
						"title":"Câmara",
						"payload":"{\"escolha\":\"c\",\"type\":\""+flag+"\"}"
					},
					{
						"content_type":"text",
						"title":"Senado",
						"payload":"{\"escolha\":\"s\",\"type\":\""+flag+"\"}"
					}
				]
			}
		};
		return messageData;
	},
	
	getQuickReply4: function (senderID,texto, flag){
		var messageData = {
			recipient: {
				id: senderID
			},
			message: {
				text: texto,
				metadata: flag,
				quick_replies: [
					{
						"content_type":"text",
						"title":"Sim",
						"payload":"{\"escolha\":\"sim\",\"type\":\""+flag+"\"}"
					},
					{
						"content_type":"text",
						"title":"Não",
						"payload":"{\"escolha\":\"nao\",\"type\":\""+flag+"\"}"
					},
					{
						"content_type":"text",
						"title":"Menu Geral",
						"payload":"{\"escolha\":\"menu\",\"type\":\""+flag+"\"}"
					}
				]
			}
		};
		return messageData;
	},
	
	
	getQuickReplyGeral: function (senderID,texto, flag, payload){
		var messageData = {
			recipient: {
				id: senderID
			},
			message: {
				text: texto,
				metadata: flag,
				quick_replies: [
					{
						"content_type":"text",
						"title":"Sim",
						"payload":"{\"escolha\":\"sim\",\"type\":\""+flag+"\""+payload+"}"
					},
					{
						"content_type":"text",
						"title":"Não",
						"payload":"{\"escolha\":\"nao\",\"type\":\""+flag+"\""+payload+"}"
					},
					{
						"content_type":"text",
						"title":"Menu Geral",
						"payload":"{\"escolha\":\"menu\",\"type\":\""+flag+"\"}"
					}
				]
			}
		};
		return messageData;
	},
	
	saveInteraction: function (senderID, descricaoInt){
		Monitora.saveInteraction({userId:senderID, descricaoInt:descricaoInt})
	},
	
	getInteraction: function (senderID, callback){
		Monitora.getInteraction({userId:senderID},function(ret){
			callback(ret)
		})
		
	},
	
	
	saveUser: function(senderID,callback){  
		var params = {};
		params.userId = senderID;
		Monitora.searchUser(params, function(ret){
			if(!ret.encontrou){
				Monitora.saveUser(params, function(ret){					
					callback({isNew: true, user: ret})
				})
			}else{
				callback({isNew: false, user: ret.usuario})
			}
		});
	}
}

module.exports = Brain;