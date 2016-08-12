/*
 * Copyright 2016-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */

/* jshint node: true, devel: true */
'use strict';

const 
  bodyParser = require('body-parser'),
  config = require('config'),
  crypto = require('crypto'),
  express = require('express'),
  https = require('https'),  
  request = require('request');

var Parse = require('parse/node');

Parse.initialize('JMzpiMhkL1z5hvuGzLhYPppNJPJpoaTAdIp3oNmh', 'mtyHx7hxS1zvPz5FnWq94w4GHzchvb44HJiZOZj2');
Parse.serverURL = 'http://localhost:1339/monitoraserver/'

var app = express();
app.set('port', process.env.PORT || 5001);
app.set('view engine', 'ejs');
app.use(bodyParser.json({ verify: verifyRequestSignature }));
app.use(express.static('public'));

/*
 * Be sure to setup your config values before running this code. You can 
 * set them using environment variables or modifying the config file in /config.
 *
 */

// App Secret can be retrieved from the App Dashboard
const APP_SECRET = (process.env.MESSENGER_APP_SECRET) ? 
  process.env.MESSENGER_APP_SECRET :
  config.get('appSecret');

// Arbitrary value used to validate a webhook
const VALIDATION_TOKEN = (process.env.MESSENGER_VALIDATION_TOKEN) ?
  (process.env.MESSENGER_VALIDATION_TOKEN) :
  config.get('validationToken');

// Generate a page access token for your page from the App Dashboard
const PAGE_ACCESS_TOKEN = (process.env.MESSENGER_PAGE_ACCESS_TOKEN) ?
  (process.env.MESSENGER_PAGE_ACCESS_TOKEN) :
  config.get('pageAccessToken');

// URL where the app is running (include protocol). Used to point to scripts and 
// assets located at this address. 
const SERVER_URL = (process.env.SERVER_URL) ?
  (process.env.SERVER_URL) :
  config.get('serverURL');

if (!(APP_SECRET && VALIDATION_TOKEN && PAGE_ACCESS_TOKEN && SERVER_URL)) {
  console.error("Missing config values");
  process.exit(1);
}

/*
 * Use your own validation token. Check that the token used in the Webhook 
 * setup is the same token used here.
 *
 */
app.get('/messengerbot/webhook', function(req, res) {
  if (req.query['hub.mode'] === 'subscribe' &&
      req.query['hub.verify_token'] === VALIDATION_TOKEN) {
    console.log("Validating webhook");
    res.status(200).send(req.query['hub.challenge']);
  } else {
    console.error("Failed validation. Make sure the validation tokens match.");
    res.sendStatus(403);          
  }  
});


/*
 * All callbacks for Messenger are POST-ed. They will be sent to the same
 * webhook. Be sure to subscribe your app to your page to receive callbacks
 * for your page. 
 * https://developers.facebook.com/docs/messenger-platform/product-overview/setup#subscribe_app
 *
 */
app.post('/messengerbot/webhook', function (req, res) {
  var data = req.body;

  // Make sure this is a page subscription
  if (data.object == 'page') {
    // Iterate over each entry
    // There may be multiple if batched
    data.entry.forEach(function(pageEntry) {
      var pageID = pageEntry.id;
      var timeOfEvent = pageEntry.time;

      // Iterate over each messaging event
      pageEntry.messaging.forEach(function(messagingEvent) {
        if (messagingEvent.optin) {
          receivedAuthentication(messagingEvent);
        } else if (messagingEvent.message) {
          receivedMessage(messagingEvent);
        } else if (messagingEvent.delivery) {
          receivedDeliveryConfirmation(messagingEvent);
        } else if (messagingEvent.postback) {
          receivedPostback(messagingEvent);
        } else if (messagingEvent.read) {
          receivedMessageRead(messagingEvent);
        } else if (messagingEvent.account_linking) {
          receivedAccountLink(messagingEvent);
        } else {
          console.log("Webhook received unknown messagingEvent: ", messagingEvent);
        }
      });
    });

    // Assume all went well.
    //
    // You must send back a 200, within 20 seconds, to let us know you've 
    // successfully received the callback. Otherwise, the request will time out.
    res.sendStatus(200);
  }
});

/*
 * This path is used for account linking. The account linking call-to-action
 * (sendAccountLinking) is pointed to this URL. 
 * 
 */
app.get('/messengerbot/authorize', function(req, res) {
  var accountLinkingToken = req.query['account_linking_token'];
  var redirectURI = req.query['redirect_uri'];
  console.log('autorizando...');
  // Authorization Code should be generated per user by the developer. This will 
  // be passed to the Account Linking callback.
  var authCode = "1234567890";

  // Redirect users to this URI on successful login
  var redirectURISuccess = redirectURI + "&authorization_code=" + authCode;

  res.render('authorize', {
    accountLinkingToken: accountLinkingToken,
    redirectURI: redirectURI,
    redirectURISuccess: redirectURISuccess
  });
});

/*
 * Verify that the callback came from Facebook. Using the App Secret from 
 * the App Dashboard, we can verify the signature that is sent with each 
 * callback in the x-hub-signature field, located in the header.
 *
 * https://developers.facebook.com/docs/graph-api/webhooks#setup
 *
 */
function verifyRequestSignature(req, res, buf) {
  var signature = req.headers["x-hub-signature"];
//  console.log(signature);
  if (!signature) {
    // For testing, let's log an error. In production, you should throw an 
    // error.
    console.error("Couldn't validate the signature.");
  } else {
    var elements = signature.split('=');
    var method = elements[0];
    var signatureHash = elements[1];
  //  console.log(APP_SECRET);
    var expectedHash = crypto.createHmac('sha1', APP_SECRET)
                        .update(buf)
                        .digest('hex');
    //console.log(signatureHash + " = "+ expectedHash);
    if (signatureHash != expectedHash) {
      throw new Error("Couldn't validate the request signature.");
    }
  }
}

/*
 * Authorization Event
 *
 * The value for 'optin.ref' is defined in the entry point. For the "Send to 
 * Messenger" plugin, it is the 'data-ref' field. Read more at 
 * https://developers.facebook.com/docs/messenger-platform/webhook-reference/authentication
 *
 */
function receivedAuthentication(event) {
  var senderID = event.sender.id;
  var recipientID = event.recipient.id;
  var timeOfAuth = event.timestamp;

  // The 'ref' field is set in the 'Send to Messenger' plugin, in the 'data-ref'
  // The developer can set this to an arbitrary value to associate the 
  // authentication callback with the 'Send to Messenger' click event. This is
  // a way to do account linking when the user clicks the 'Send to Messenger' 
  // plugin.
  var passThroughParam = event.optin.ref;

  console.log("Received authentication for user %d and page %d with pass " +
    "through param '%s' at %d", senderID, recipientID, passThroughParam, 
    timeOfAuth);

  // When an authentication is received, we'll send a message back to the sender
  // to let them know it was successful.
  sendTextMessage(senderID, "Authentication successful");
}

/*
 * Message Event
 *
 * This event is called when a message is sent to your page. The 'message' 
 * object format can vary depending on the kind of message that was received.
 * Read more at https://developers.facebook.com/docs/messenger-platform/webhook-reference/message-received
 *
 * For this example, we're going to echo any text that we get. If we get some 
 * special keywords ('button', 'generic', 'receipt'), then we'll send back
 * examples of those bubbles to illustrate the special message bubbles we've 
 * created. If we receive a message with an attachment (image, video, audio), 
 * then we'll simply confirm that we've received the attachment.
 * 
 */
function receivedMessage(event) {
  var senderID = event.sender.id;
  var recipientID = event.recipient.id;
  var timeOfMessage = event.timestamp;
  var message = event.message;
  //console.log("");
  //console.log("Received message for user %d and page %d at %d with message:", 
    //senderID, recipientID, timeOfMessage);
  //console.log(JSON.stringify(message));
  //console.log("");
  var isEcho = message.is_echo;
  var messageId = message.mid;
  var appId = message.app_id;
  var metadata = message.metadata;

  // You may get a text or attachment but not both
  var messageText = message.text;
  var messageAttachments = message.attachments;
  var quickReply = message.quick_reply;
  
  if (isEcho) {
	 // console.log("---isEcho---");
    // Just logging message echoes to console
   // console.log("Received echo for message %s and app %d with metadata %s", 
    //  messageId, appId, metadata);
	//  console.log("-----");
    return;
  } else if (quickReply) {
	 // console.log("---quickReply---");
    var quickReplyPayload = quickReply.payload;
   // console.log("Quick reply for message %s with payload %s",
    //  messageId, quickReplyPayload);
	//  console.log("-----");

    sendTextMessage(senderID, "Voto registrado!");
    return;
  }

  if (messageText) {
	//console.log("---messageText---");
	var isFunction = false;
	var par = messageText.split(" ");
	switch (par[0].substring(0,3).toLocaleLowerCase()) {
		case 'pol':
			//seach congressman message
			
			getPolitico(messageText.substring(3,messageText.length).toLocaleUpperCase().trim(), null, senderID)
			isFunction = true;
			break;
		case 'pro':
			var keys = [];
			for(var i =1; i < par.length; i++){
				keys.push(par[i]);				
			}
			searchProjects(keys, senderID);
			isFunction = true;
        break;
	}
	
	if (messageText.substring(0,2).toLocaleLowerCase() === "uf"){
		getPolitico(null,messageText.substring(2,messageText.length).toLocaleUpperCase().trim(), senderID)
		isFunction = true;
	}
	
	if(!isFunction){
		// If we receive a text message, check to see if it matches any special
		// keywords and send back the corresponding example. Otherwise, just echo
		// the text we received.	
		switch (messageText) {	  	
		  default:
			messageText = "Olá! Quer saber informações de algum deputado? \n\n"+
			"Digite o pol <nome de um político> \nEx: pol Tiririca\n\n"+ 
			"Ou vc pode digitar uf <estado> para ver todos os deputados de um estado\nEx: uf SP\n\n"+
			"Vc tbm pode pesquisar projetos por palavra chave. \nEx: pro corrupção";
			sendTextMessage(senderID, messageText);
		}
	}
    
  } else if (messageAttachments) {
    sendTextMessage(senderID, "Message with attachment received");
  }
}


/*
 * Delivery Confirmation Event
 *
 * This event is sent to confirm the delivery of a message. Read more about 
 * these fields at https://developers.facebook.com/docs/messenger-platform/webhook-reference/message-delivered
 *
 */
function receivedDeliveryConfirmation(event) {
  var senderID = event.sender.id;
  var recipientID = event.recipient.id;
  var delivery = event.delivery;
  var messageIDs = delivery.mids;
  var watermark = delivery.watermark;
  var sequenceNumber = delivery.seq;

  if (messageIDs) {
    messageIDs.forEach(function(messageID) {
     // console.log("Received delivery confirmation for message ID: %s", 
    //    messageID);
    });
  }

  //console.log("All message before %d were delivered.", watermark);
}


/*
 * Postback Event
 *
 * This event is called when a postback is tapped on a Structured Message. 
 * https://developers.facebook.com/docs/messenger-platform/webhook-reference/postback-received
 * 
 */
function receivedPostback(event) {
  var senderID = event.sender.id;
  var recipientID = event.recipient.id;
  var timeOfPostback = event.timestamp;

  // The 'payload' param is a developer-defined field which is set in a postback 
  // button for Structured Messages. 
  var payload = event.postback.payload;
  
  var json = JSON.parse(payload);
  var idPolitico = json.id_politico;
  var nome = json.nome;
  var politico = Parse.Object.extend("Politico");
  politico.id = idPolitico;
  //type of action
  var type = json.type;
  
  switch (type) {
	  case "gastos_cota":
		//send spend´s information
			getGastos(politico, nome, senderID);
		break;
	   case "presenca":	
			getPresenca(politico, nome, senderID);
		break;
	   case "projetos":	
			getProjects(politico, senderID);
		break;
	   case "project_ementa":	
			var PP = Parse.Object.extend("Proposicao");
			var query = new Parse.Query(PP);
			query.get(json.id_project, {
			  success: function(project) {
				var message = 
							project.get("tx_nome")+"\nAutor: "+
							project.get("nome_autor")+"\n"+project.get("txt_ementa").replace(/["]/g,'\'');
				if(project.get("tx_ultimo_despacho").trim().length > 0){
					setTimeout(function(){ sendTextMessage(senderID, "Último despacho: "+
							project.get("tx_ultimo_despacho")); }, 2000);
					
				}
				if(project.get("tx_link").trim().length > 0){
					setTimeout(function(){ sendTextMessage(senderID, "Link para o projeto: "+
							project.get("tx_link")); }, 2000);
				}
				sendTextMessage(senderID, message);
				setTimeout(function(){ sendQuickReply(senderID); }, 3000);
			  },
			  error: function(object, error) {
				
			  }
			});
			
			
		break;
		
		
  }
  

}

/*
 * Message Read Event
 *
 * This event is called when a previously-sent message has been read.
 * https://developers.facebook.com/docs/messenger-platform/webhook-reference/message-read
 * 
 */
function receivedMessageRead(event) {
  var senderID = event.sender.id;
  var recipientID = event.recipient.id;

  // All messages before watermark (a timestamp) or sequence have been seen.
  var watermark = event.read.watermark;
  var sequenceNumber = event.read.seq;
 // console.log("");
//  console.log("Received message read event for watermark %d and sequence " +
 //   "number %d", watermark, sequenceNumber);
 // console.log("");
	
}

/*
 * Account Link Event
 *
 * This event is called when the Link Account or UnLink Account action has been
 * tapped.
 * https://developers.facebook.com/docs/messenger-platform/webhook-reference/account-linking
 * 
 */
function receivedAccountLink(event) {
  var senderID = event.sender.id;
  var recipientID = event.recipient.id;

  var status = event.account_linking.status;
  var authCode = event.account_linking.authorization_code;

  //console.log("Received account link event with for user %d with status %s " +
  //  "and auth code %s ", senderID, status, authCode);
}


/*
 * Send a text message using the Send API.
 *
 */
function sendTextMessage(recipientId, messageText) {
 var messageData = {
    recipient: {
      id: recipientId
    },
    message: {
      text: messageText,
      metadata: "DEVELOPER_DEFINED_METADATA"
    }
  };
  callSendAPI(messageData);
}

/*
 * Send a Structured Message (Generic Message type) using the Send API.
 *
 */
function sendFileCongressman(cards, recipientId) {
  var messageData = {
    recipient: {
      id: recipientId
    },
    message: {
      attachment: {
        type: "template",
        payload: {
          template_type: "generic",
          elements: cards
        }
      }
    }
  };  

  callSendAPI(messageData);
}



/*
 * Send a message with Quick Reply buttons.
 *
 */
function sendQuickReply(recipientId) {
  var messageData = {
    recipient: {
      id: recipientId
    },
    message: {
      text: "Como vc votaria nesse projeto?",
      metadata: "DEVELOPER_DEFINED_METADATA",
      quick_replies: [
        {
          "content_type":"text",
          "title":"Favorável",
          "payload":"Pro_favoravel"
        },
        {
          "content_type":"text",
          "title":"Contra",
          "payload":"Pro_contra"
        }
      ]
    }
  };

  callSendAPI(messageData);
}

/*
 * Send a read receipt to indicate the message has been read
 *
 */
function sendReadReceipt(recipientId) {
  console.log("Sending a read receipt to mark message as seen");

  var messageData = {
    recipient: {
      id: recipientId
    },
    sender_action: "mark_seen"
  };

  callSendAPI(messageData);
}

/*
 * Turn typing indicator on
 *
 */
function sendTypingOn(recipientId) {
 // console.log("Turning typing indicator on");

  var messageData = {
    recipient: {
      id: recipientId
    },
    sender_action: "typing_on"
  };

  callSendAPI(messageData);
}

/*
 * Turn typing indicator off
 *
 */
function sendTypingOff(recipientId) {
  console.log("Turning typing indicator off");

  var messageData = {
    recipient: {
      id: recipientId
    },
    sender_action: "typing_off"
  };

  callSendAPI(messageData);
}

/*
 * Send a message with the account linking call-to-action
 *
 */
function sendAccountLinking(recipientId) {
  var messageData = {
    recipient: {
      id: recipientId
    },
    message: {
      attachment: {
        type: "template",
        payload: {
          template_type: "button",
          text: "Welcome. Link your account.",
          buttons:[{
            type: "account_link",
            url: SERVER_URL + "/authorize"
          }]
        }
      }
    }
  };  

  callSendAPI(messageData);
}

/*
 * Call the Send API. The message data goes in the body. If successful, we'll 
 * get the message id in a response 
 *
 */
function callSendAPI(messageData) {
  request({
    uri: 'https://graph.facebook.com/v2.6/me/messages',
    qs: { access_token: PAGE_ACCESS_TOKEN },
    method: 'POST',
    json: messageData

  }, function (error, response, body) {
    if (!error && response.statusCode == 200) {
      var recipientId = body.recipient_id;
      var messageId = body.message_id;

    /*  if (messageId) {
        console.log("Successfully sent message with id %s to recipient %s", 
          messageId, recipientId);
      } else {
      console.log("Successfully called Send API for recipient %s", 
        recipientId);
      }*/
    } else {
     
      console.error(response.body.error.message);
    }
  });  
}

/*
  Build element for congressman with options: Spends, assiduity and projects
*/
function buildElements (congressman){
	var mReturn = {
            title: congressman.get("nome"),
            subtitle: congressman.get("siglaPartido")+" - "+congressman.get("uf")+"\n"+congressman.get("telefone"),
            //item_url: "https://www.oculus.com/en-us/rift/",               
            image_url: "http://www.camara.gov.br/internet/deputado/bandep/"+congressman.get("idCadastro")+".jpg",
            buttons: [ {
              type: "postback",
              title: "Gastos - cota parlamentar",
              payload: "{\"id_politico\": \""+congressman.id+"\",\"nome\": \""+congressman.get("nome")+"\", \"type\": \"gastos_cota\"}",
            },{
              type: "postback",
              title: "Presença",
              payload: "{\"id_politico\": \""+congressman.id+"\",\"nome\": \""+congressman.get("nome")+"\", \"type\": \"presenca\"}",
            },{
              type: "postback",
              title: "Projetos",
              payload: "{\"id_politico\": \""+congressman.id+"\",\"nome\": \""+congressman.get("nome")+"\", \"type\": \"projetos\"}",
            }],
          };
		  return mReturn;
}

/*
  Build element for project
*/
function buildElementProject (project){
	
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
}

/*

Search a congressman by name

*/

function getPolitico (nome, uf, senderID){
	sendTypingOn(senderID);
	console.log("Buscando ... "+ nome);
	var Politico = Parse.Object.extend("Politico");
	var query = new Parse.Query(Politico);
	query.equalTo("tipo", "c");
	query.limit(10);
	if(nome)
		query.startsWith("nome", nome);
	else{
		if(uf){
			query.equalTo("uf", uf);
		}
	}
	query.find({
	  success: function(objects) {
		// Successfully retrieved the object.
		var cards = [];
		for (var i = 0; i < objects.length; i++) {
			  var object = objects[i];
			  cards.push(buildElements(object));
		}
		//console.log(cards);
		sendFileCongressman(cards, senderID);
	  },
	  error: function(error) {
		console.log("Error: " + error.code + " " + error.message);
	  }
	});
	
	
}

/*

Search congressman projects

*/

function getProjects (qPolitico, senderID){
	//console.log(qPolitico);
	sendTypingOn(senderID);
	var Politico = Parse.Object.extend("Proposicao");
	var query = new Parse.Query(Politico);
	query.equalTo("autor", "Politico$"+qPolitico.id);	
	query.limit(10);	
	query.find({
	  success: function(objects) {
		  if(objects.length == 0){
			  sendTextMessage(senderID, "Nenhum projeto encontrado!");
		  }else{
			  // Successfully retrieved the object.
				var cards = [];
				
				for (var i = 0; i < objects.length; i++) {
					  var object = objects[i];
					  cards.push(buildElementProject(object));
				}
				//console.log(cards);
				sendFileCongressman(cards, senderID);
		  }
		
	  },
	  error: function(error) {
		console.log("Error: " + error.code + " " + error.message);
	  }
	});
	
	
}


Number.prototype.formatMoney = function(c, d, t){
var n = this, 
    c = isNaN(c = Math.abs(c)) ? 2 : c, 
    d = d == undefined ? "." : d, 
    t = t == undefined ? "," : t, 
    s = n < 0 ? "-" : "", 
    i = parseInt(n = Math.abs(+n || 0).toFixed(c)) + "", 
    j = (j = i.length) > 3 ? j % 3 : 0;
   return s + (j ? i.substr(0, j) + t : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + t) + (c ? d + Math.abs(n - i).toFixed(c).slice(2) : "");
 };

 /*
 
 Get congressman´s spending
 */
function getGastos(qPolitico, nome, senderID){
	sendTypingOn(senderID);
	var Politico = Parse.Object.extend("CotaPorAno");
	var query = new Parse.Query(Politico);
	query.equalTo("politico", "Politico$"+qPolitico.id);	
	//console.log(qPolitico);
	query.find({
	  success: function(objects) {
		// Successfully retrieved the object.
		var mensagem = "O deputado "+nome+" gastou:\n";
		//console.log(objects.length);
		for (var i = 0; i < objects.length; i++) {
			  var object = objects[i];
			 mensagem = mensagem + "Ano "+object.get("ano") +": R$ "+object.get("total").formatMoney(2, ',', '.')+"\n";
		}
		sendTextMessage(senderID, mensagem);
		
	  },
	  error: function(error) {
		console.log("Error: " + error.code + " " + error.message);
	  }
	});
}

 /*
 
 Get congressman´s presence
 */
function getPresenca(qPolitico, nome, senderID){
	sendTypingOn(senderID);
	var Politico = Parse.Object.extend("Presenca");
	var query = new Parse.Query(Politico);
	query.equalTo("politico", "Politico$"+qPolitico.id);	
	//console.log(qPolitico);
	query.find({
	  success: function(objects) {
		// Successfully retrieved the object.
		var mensagem = "Presença do(a) deputado(a) "+nome+" :\n";
		//console.log(objects.length);
		for (var i = 0; i < objects.length; i++) {
			  var object = objects[i];
			 mensagem = mensagem + "Ano: "+object.get("nr_ano") +"\n     ->Faltas "+
			 (object.get("nr_ausencia_justificada")+object.get("nr_ausencia_nao_justificada"))+"\n"+
			 "     ->Presença:"+object.get("nr_presenca")+"\n";
		}
		sendTextMessage(senderID, mensagem);
		
	  },
	  error: function(error) {
		console.log("Error: " + error.code + " " + error.message);
	  }
	});
}


 /*
 
 Get projects with keywords
 */
function searchProjects(keys,  senderID){
	sendTypingOn(senderID);
	var Politico = Parse.Object.extend("Proposicao");
	var query = new Parse.Query(Politico);
	query.containsAll("words", keys);	
	query.limit(10);	
	//console.log(qPolitico);
	query.find({
	  success: function(objects) {
		if(objects.length == 0){
			  sendTextMessage(senderID, "Nenhum projeto encontrado!");
		  }else{
			  // Successfully retrieved the object.
				var cards = [];
				
				for (var i = 0; i < objects.length; i++) {
					  var object = objects[i];
					  cards.push(buildElementProject(object));
				}
				//console.log(cards);
				sendFileCongressman(cards, senderID);
		  }
		
	  },
	  error: function(error) {
		console.log("Error: " + error.code + " " + error.message);
	  }
	});
}
// Start server
// Webhooks must be available via SSL with a certificate signed by a valid 
// certificate authority.
app.listen(app.get('port'), function() {
  console.log('Node app is running on port', app.get('port'));
});

module.exports = app;

