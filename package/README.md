# Package Monitora, Brasil!


O pacote do Monitora, Brasil! permite a consulta de dados contidos na plataforma. 


##Utilização
Para instalar o pacote em um projeto node, execute:

`npm install --save monitora-brasil`

Depois, declare no projeto 

```javascript
var Monitora = require('monitora-brasil');
	
	var req = {}
	req.params.pg=0;
	req.params.limit=2;
	req.params.uf = 'SP';
	Monitora.getPoliticos(req.params, function(ret){	
		console.log(ret);
		
	});	
```


# Copyright
Copyright 2013 de Geraldo Augusto de Morais Figueiredo<br>
Este arquivo é parte do programa Monitora, Brasil!. O Monitora, Brasil! é um software livre; você pode redistribuí-lo e/ou modificá-lo dentro dos termos da GNU Affero General Public License como publicada pela Fundação do Software Livre (FSF); na versão 3 da Licença. <br>
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a licença para maiores detalhes. 
Link para a cópia da GNU Affero General Public License: http://www.gnu.org/licenses/ 
