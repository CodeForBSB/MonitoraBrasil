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

var Monitora = require('./Monitora');
var Brain = require('./Brain');


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
	console.log(req.query['hub.verify_token']);
	console.log(VALIDATION_TOKEN);
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
  //console.log(data)

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

function IsJsonString(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
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
  //console.log(JSON.stringify(event));
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
    //verify if is a rank quickReply

    //console.log(quickReplyPayload);
	if(IsJsonString(quickReplyPayload)){
		var j = JSON.parse(quickReplyPayload);
		
		var pQr = {tipo: j.type, senderID: senderID, escolha: j.escolha, payload: j}
		Brain.analiseQuickReply(pQr, function(ret){			
			for(var i=0; i < ret.length; i++){
				var m = ret[i]
				if(m.tipo === "text"){
					if(i == 0){
						sendTextMessage(senderID, m.mensagem);
					}else{
						setTimeout( 
							sendTextMessage(senderID, m.mensagem)
						, 2000);
					}
				}
				if(m.tipo === "quickReply"){
					sendTypingOn(senderID)
					setTimeout( function(){callSendAPI(m.mensagem)}, 3000);
				}
				if(m.tipo === "cards"){
					setTimeout( function(){sendFileCongressman(m.mensagem, senderID)}, 3000);
					
				}
			}
			
		})	
			
		 if(j.type === "HOUSE_CHOOSE"){
		   getRanking(quickReplyPayload, senderID);
		   return;
		 }
	}else{
      sendTextMessage(senderID, "Voto registrado!");
      return;
     }
  }else{
	  if (messageText || messageAttachments) {
		// console.log("---messageText--- "+messageText);
		var p ={mensagem: messageText, senderID: senderID}
		Brain.analiseMensagem(p, function(ret){
			for(var i=0; i < ret.length; i++){
				var m = ret[i]
				if(m.tipo === "text"){
					if(i == 0){
						sendTextMessage(senderID, m.mensagem);
					}else{
						setTimeout( 
							sendTextMessage(senderID, m.mensagem)
						, 2000);
					}
				}
				if(m.tipo === "quickReply"){
					sendTypingOn(senderID)
					setTimeout( function(){callSendAPI(m.mensagem)}, 3000);
				}
				if(m.tipo === "cards"){
					setTimeout( function(){sendFileCongressman(m.mensagem, senderID)}, 3000);
					
				}
				
			}
			
		})
		return;
	
	
	
	
	
	//verifica se ja existe o usuario, se nao existir cria um
	saveUser(senderID)
    var isFunction = false;
    var par = messageText.split(" ");
    switch (par[0].substring(0,3).toLocaleLowerCase()) {
		case 'gut':
			sendText4all();
			break;
		case 'pol':
			if(par.length > 1){
				//seach congressman message
				var firstName = par[1].toLocaleUpperCase().trim();
				var fullName = null;
				if(par.length > 2){
				  var fullName = par[1].toLocaleUpperCase().trim()+" "+par[2].toLocaleUpperCase().trim();
				}
				getPolitico(firstName, fullName, null, senderID)
				isFunction = true;

			}else{
				sendTextMessage(senderID, "Digite Pol nome_do_politico");
			}
			break;


		case 'pro':
			var keys = [];
			for(var i =1; i < par.length; i++){
				keys.push(par[i]);
			}
			searchProjects(keys, senderID);
			isFunction = true;
			break;


		case 'ran':
			var uf = null;
			var siglaPartido = null;
			if(par[1] && par[2]){
			var param = par[1].trim().toLocaleLowerCase();
			var value =  par[2].trim().toLocaleUpperCase();
			if(param === "uf"){
			  uf = value;
			}else{
			  if(param === "partido"){
				siglaPartido = value;
			  }
			}

		  }
		  sendHouseChoose(uf, siglaPartido, senderID);
		  //getRanking(uf, siglaPartido, senderID);
		  isFunction = true;
			break;

    }

    if (par[0].toLocaleLowerCase() === "uf"){
		isFunction = true;
		if(par[1]){
			getPolitico(null,null,par[1].toLocaleUpperCase().trim(), senderID)
		}else{
			sendTextMessage(senderID, "Qual uf vc quer? Ex: uf sp");
		}

    }

    if(!isFunction){
      // If we receive a text message, check to see if it matches any special
      // keywords and send back the corresponding example. Otherwise, just echo
      // the text we received.
      switch (messageText) {
        default:
        messageText = "Olá! Quer saber informações de algum deputado federal ou senador(a)? Tem as seguintes opções \n"+
        "pol <nome de um político> \nEx: pol Tiririca\n\n"+
        "uf <UF>\nEx: uf SP\n\n"+
        "rank \n"+
        "rank uf <UF> \n"+
        "rank partido <PARTIDO>\n\n"+
        "pro <palavra-chave> - para pesquisar projetos";
        sendTextMessage(senderID, messageText);
      }
    }

  } else if (messageAttachments) {	  
    //sendTextMessage(senderID, "Message with attachment received");
  }
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
  if(json.type === 'MENU_PRINCIPAL'){
	 var pQr = {tipo: json.type, senderID: senderID, escolha: json.escolha, payload: json}
		Brain.analiseQuickReply(pQr, function(ret){			
			for(var i=0; i < ret.length; i++){
				var m = ret[i]
				if(m.tipo === "text"){
					if(i == 0){
						sendTextMessage(senderID, m.mensagem);
					}else{
						setTimeout( 
							sendTextMessage(senderID, m.mensagem)
						, 2000);
					}
				}
				if(m.tipo === "quickReply"){
					sendTypingOn(senderID)
					setTimeout( function(){callSendAPI(m.mensagem)}, 3000);
				}
				if(m.tipo === "cards"){
					setTimeout( function(){sendFileCongressman(m.mensagem, senderID)}, 3000);
					
				}
			}
			
		})	
  }else{
	  var idPolitico = json.id_politico;
	  var nome = json.nome;
	  var politico = Parse.Object.extend("Politico");
	  politico.id = idPolitico;
	  if(json.casa){
		var casa = json.casa;
	  }
	  if(json.sexo){
		var sexo = json.sexo;
	  }
	  //type of action
	  var type = json.type;
	  //verificar se envia a opcao de monitorar o politico
	  Brain.enviaMensagemMonitorar({senderID:senderID,idPolitico:idPolitico,nome:nome}, function(ret){
		  setTimeout( function(){callSendAPI(ret)}, 4000);
	  });

	  switch (type) {
		case "gastos_cota":
			//send spend´s information
			getGastos(politico, nome, casa, sexo, senderID);
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
				  project.get("tx_link")); }, 2500);
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
  


}

/*
* Send a message with Quick Reply buttons to choose Camara ou Senado for Rank.
*
*/
function sendHouseChoose(uf, siglaPartido, senderID) {
  var messageData = {
    recipient: {
      id: senderID
    },
    message: {
      text: "Qual casa?",
      metadata: "HOUSE_CHOOSE",
      quick_replies: [
	  
        {
          "content_type":"text",
          "title":"Câmara",
          "payload":"{\"uf\":\""+uf+"\",\"siglaPartido\":\""+siglaPartido+"\",\"casa\":\"c\",\"type\":\"HOUSE_CHOOSE\"}"
        },
        {
          "content_type":"text",
          "title":"Senado",
          "payload":"{\"uf\":\""+uf+"\",\"siglaPartido\":\""+siglaPartido+"\",\"casa\":\"s\",\"type\":\"HOUSE_CHOOSE\"}"
        }
      ]
    }
  };

  callSendAPI(messageData);
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

Search a congressman by name

*/

function getPolitico (firstName, fullName, uf, senderID){
  sendTypingOn(senderID);

  var params = {};
  if(firstName)
	params.firstName = firstName;
  if(fullName)
	params.fullName = fullName;
  if(uf)
	params.uf = uf;
  Monitora.getPolitico(params, function(ret){
    //console.log(ret);
    if(ret.success){
      sendFileCongressman(ret.cards, senderID);
    }else{
      sendTextMessage(senderID,ret.message);
    }

  });
}

/*

get congressman spending´s rank
params
uf - state of congressman
siglaPartido - parties
senderID - sender id

*/

function getRanking ( payload, senderID){

  sendTypingOn(senderID);
  // {uf:'"+uf+"',siglaPartido:'"+siglaPartido+"',casa:'c'}
  var params = JSON.parse(payload);
  // var params = {};
  // params.uf = uf;
  // params.siglaPartido = siglaPartido;
  Monitora.getRanking(params, function(ret){
    sendTextMessage(senderID, ret[0]);
  });
}

/*

Get congressman projects

*/

function getProjects (qPolitico, senderID){
  //console.log(qPolitico);
  sendTypingOn(senderID);
  var params = {};
  params.politicoId = qPolitico.id;
  Monitora.getProjects(params, function(ret){
    if(ret.length == 0){
      sendTextMessage(senderID, "Nenhum projeto encontrado!");
    }else{
      sendFileCongressman(ret, senderID);
    }
  });


}


/*

Get congressman´s spending
*/
function getGastos(qPolitico, nome,  casa, sexo, senderID){
  sendTypingOn(senderID);
  var json = {};
  json.politicoId = qPolitico.id;
  json.nome=nome;
  json.casa = casa;
  json.sexo = sexo;
  var params = [json];
  //console.log(params);
  Monitora.getGastos(params, function(ret){
    sendTextMessage(senderID,ret[0]);
	if(ret.length > 1){
		setTimeout(function(){ sendTextMessage(senderID,ret[1]) }, 3000);
	}
  });
}

/*

Get congressman´s presence
*/
function getPresenca(qPolitico, nome, senderID){
  sendTypingOn(senderID);
  var json = {};
  json.politicoId = qPolitico.id;
  json.nome=nome;
  var params = [json];
  //console.log(params);
  Monitora.getPresenca(params, function(ret){
    sendTextMessage(senderID,ret[0]);
  });
}


/*

Get projects with keywords
*/
function searchProjects(keys,  senderID){
  sendTypingOn(senderID);
  var params = {};
  params.keys = keys;
  Monitora.searchProjects(params, function(ret){
    if(ret.length == 0){
      sendTextMessage(senderID, "Nenhum projeto encontrado!");
    }else{
      sendFileCongressman(ret, senderID);
    }
  });
}


/*

Save user
// 1173239032747500
*/
function saveUser(senderID){  
  var params = {};
  params.userId = senderID;
  Monitora.searchUser(params, function(ret){
    if(ret[0] === "false"){
      Monitora.saveUser(params, function(ret){
		  
	  })
    }
  });
}


function sendImage(senderId){
	var messageData = {
    recipient: {
      id: senderId
    },
    message: {
      attachment: {
        type: "image",
        payload: {
          url: "http://monitorabrasil.com/content/images/2017/FB_IMG_1487156331243.jpg"
        }
      }
    }
  };
  callSendAPI(messageData);
}


function shareContent(senderID){
	 var messageData = {
    recipient: {
      id: senderID
    },
    message: {
      attachment: {
        type: "template",
        payload: {
          template_type: "generic",
          elements: [{
			   "title":"Meme do dia",
				"subtitle":"Seis por meia dúzia?",
				"image_url":"http://monitorabrasil.com/content/images/2017/FB_IMG_1487156331243.jpg",
				"buttons":[
				  {
					"type":"element_share"
				  }              
				]}]
        }
      }
    }
  };
  
	callSendAPI(messageData);

}

function sendText4all(){
	var params = {};
	
  
	
	 Monitora.sendText4all(params, function(ret){
		 for (var i = 0; i < ret.length; i++) {
			// sendImage(ret[i])
			 //shareContent(ret[i])
			//sendTextMessage(ret[i], "Vi que você gosta do tema tal, veja os projetos da semana:");
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
