# REST Monitora, Brasil!


O REST do Monitora, Brasil! permite a consulta de dados contidos na plataforma. 

##Serviços disponíveis

###getPoliticos
 Parâmetros:
- casa (c ou s)
- uf (uf do Brasil) - opcional
- partido (partidos do Brasil) - opcional
- limit (limite de resultados)
- pg (paginação)

Ex: [https://monitorabrasil.com/monitorarest/getPoliticos/s?limit=2&pg=0](https://monitorabrasil.com/monitorarest/getPoliticos/s?limit=2&pg=0)

###getPolitico
Parâmetros:
- id (id do político)


Ex: [https://monitorabrasil.com/monitorarest/getpolitico?id=H1YjgTdI54](https://monitorabrasil.com/monitorarest/getpolitico?id=H1YjgTdI54)


###getRanking
Parâmetros:
- casa (c ou s)
- uf (uf do Brasil)
- partido (partidos do Brasil)
- limit (limite de resultados)
- pg (paginação)

Ex: [https://monitorabrasil.com/monitorarest/getRanking/c?limit=10&pg=0&uf=sp](https://monitorabrasil.com/monitorarest/getRanking/c?limit=10&pg=0&uf=sp)

[https://monitorabrasil.com/monitorarest/getRanking/c?limit=10&pg=0&uf=sp&partido=PSDB](https://monitorabrasil.com/monitorarest/getRanking/c?limit=10&pg=0&uf=sp&partido=PSDB)

[https://monitorabrasil.com/monitorarest/getRanking/c?limit=10&pg=0](https://monitorabrasil.com/monitorarest/getRanking/c?limit=10&pg=0)

###searchProjects
Parâmetros:
- keys (palavras chaves separado por virgula)


Ex: [https://monitorabrasil.com/monitorarest/searchprojeto?keys=moradia](https://monitorabrasil.com/monitorarest/searchprojeto?keys=moradia)

###getpartidos
Parâmetros:
- partido (sigla do partido)(opcional)


Ex: [https://monitorabrasil.com/monitorarest/getpartidos](https://monitorabrasil.com/monitorarest/getpartidos)

[https://monitorabrasil.com/monitorarest/getpartidos?partido=PTN](https://monitorabrasil.com/monitorarest/getpartidos?partido=PTN)
	
###getgastos
Parâmetros:
- idPolitico (id do político)
- ano


Ex: [https://monitorabrasil.com/monitorarest/getgastos/?idPolitico=160653&ano=2016](https://monitorabrasil.com/monitorarest/getgastos/?idPolitico=160653&ano=2016)

	
###getgastoscategoria
Parâmetros:
- idPolitico (id do político)
- ano (opcional)
- mes (opcional)


Ex: [https://monitorabrasil.com/monitorarest/getgastoscategoria/?idPolitico=74847&ano=2015&mes=4](https://monitorabrasil.com/monitorarest/getgastoscategoria/?idPolitico=74847&ano=2015&mes=4)

[https://monitorabrasil.com/monitorarest/getgastoscategoria/?idPolitico=74847&ano=2015](https://monitorabrasil.com/monitorarest/getgastoscategoria/?idPolitico=74847&ano=2015)

[https://monitorabrasil.com/monitorarest/getgastoscategoria/?idPolitico=74847](https://monitorabrasil.com/monitorarest/getgastoscategoria/?idPolitico=74847)


	
###getlistgastoscategoria
Parâmetros:
- idPolitico (id do político)
- subcota (SERVIÇOS POSTAIS, FORNECIMENTO DE ALIMENTAÇÃO DO PARLAMENTAR, DIVULGAÇÃO DA ATIVIDADE PARLAMENTAR.,COMBUSTÍVEIS E LUBRIFICANTES., MANUTENÇÃO DE ESCRITÓRIO DE APOIO À ATIVIDADE PARLAMENTAR, TELEFONIA, Emissão Bilhete Aéreo)
- ano (opcional)
- mes (opcional)


Ex: [https://monitorabrasil.com/monitorarest/getlistgastoscategoria/?idPolitico=74847&subcota=COMBUSTÍVEIS E LUBRIFICANTES.&ano=2015&mes=4](https://monitorabrasil.com/monitorarest/getlistgastoscategoria/?idPolitico=74847&subcota=COMBUST%C3%8DVEIS%20E%20LUBRIFICANTES.&ano=2015&mes=4)

[https://monitorabrasil.com/monitorarest/getlistgastoscategoria/?idPolitico=74847&subcota=DIVULGAÇÃO DA ATIVIDADE PARLAMENTAR.&ano=2015&mes=4](https://monitorabrasil.com/monitorarest/getlistgastoscategoria/?idPolitico=74847&subcota=DIVULGAÇÃO DA ATIVIDADE PARLAMENTAR.&ano=2015&mes=4)






# Copyright
Copyright 2013 de Geraldo Augusto de Morais Figueiredo<br>
Este arquivo é parte do programa Monitora, Brasil!. O Monitora, Brasil! é um software livre; você pode redistribuí-lo e/ou modificá-lo dentro dos termos da GNU Affero General Public License como publicada pela Fundação do Software Livre (FSF); na versão 3 da Licença. <br>
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a licença para maiores detalhes. 
Link para a cópia da GNU Affero General Public License: http://www.gnu.org/licenses/ 
