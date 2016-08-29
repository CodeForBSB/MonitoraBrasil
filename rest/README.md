# REST Monitora, Brasil!


O REST do Monitora, Brasil! permite a consulta de dados contidos na plataforma. 

##Serviços disponíveis

###getPoliticos
 Parâmetros:
- casa (c ou s)
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


# Copyright
Copyright 2013 de Geraldo Augusto de Morais Figueiredo<br>
Este arquivo é parte do programa Monitora, Brasil!. O Monitora, Brasil! é um software livre; você pode redistribuí-lo e/ou modificá-lo dentro dos termos da GNU Affero General Public License como publicada pela Fundação do Software Livre (FSF); na versão 3 da Licença. <br>
Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a licença para maiores detalhes. 
Link para a cópia da GNU Affero General Public License: http://www.gnu.org/licenses/ 
