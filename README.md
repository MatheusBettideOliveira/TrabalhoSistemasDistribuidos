1 INTRODUÇÃO
Este documento apresenta a documentação técnica da implementação de um cliente Java que consome a API REST GeoDB Cities por meio da plataforma RapidAPI. O projeto foi desenvolvido na IDE NetBeans e visa demonstrar o acesso e o consumo de serviços Web via protocolo HTTP, utilizando o estilo arquitetural REST.

2 DESCRIÇÃO GERAL DA API
A API GeoDB Cities fornece informações geográficas sobre cidades ao redor do mundo. As funcionalidades oferecidas permitem:
Buscar cidades por nome (prefixo);

Listar as maiores cidades de um país;

Encontrar cidades próximas a coordenadas geográficas específicas.

A API é acessível por meio do serviço RapidAPI, exigindo chave de autenticação nos headers das requisições.

3 TECNOLOGIAS UTILIZADAS
Linguagem: Java
IDE: NetBeans
Protocolos: HTTP/HTTPS

Bibliotecas:
HttpClient (requisições HTTP)
JSON.simple ou Gson (parsing JSON)
API REST: GeoDB Cities (RapidAPI)

4 CHAMADAS REALIZADAS À API
4.1 Buscar Cidades por Nome
URL:
 https://geodb-cities-api.wirefreethought.com/v1/geo/cities?namePrefix={nome}


Método: GET


Headers:
x-rapidapi-key: {sua_chave_api}
x-rapidapi-host: geodb-cities-api.wirefreethought.com

Exemplo de resposta:


{
  "data": [
    {
      "city": "Rio de Janeiro",
      "country": "Brazil"
    }
  ]
}


4.2 Listar as Maiores Cidades de um País
URL:
 https://geodb-cities-api.wirefreethought.com/v1/geo/countries/{codigo_pais}/cities
Método: GET
Headers:
x-rapidapi-key: {sua_chave_api}
x-rapidapi-host: geodb-cities-api.wirefreethought.com

Exemplo de resposta:

{
  "data": [
    {
      "city": "São Paulo",
      "population": 12000000
    }
  ]
}
4.3 Cidades Próximas a Coordenadas Geográficas
URL:
 https://geodb-cities-api.wirefreethought.com/v1/geo/cities/nearby?latitude={lat}&longitude={long}
Método: GET
Headers:
x-rapidapi-key: {sua_chave_api}
x-rapidapi-host: geodb-cities-api.wirefreethought.com

Exemplo de resposta:
{
  "data": [
    {
      "city": "São Bernardo do Campo",
      "distance": 15.6
    }
  ]
}
5 INSTRUÇÕES DE EXECUÇÃO NO NETBEANS
5.1 Instalação
Baixar o NetBeans IDE no site oficial: https://netbeans.apache.org
Instalar o JDK 11 ou superior

5.2 Criação e Importação do Projeto
Abrir o NetBeans
Selecionar: File > New Project > Java > Java Application
Nomear o projeto e finalizar

5.3 Adição de Bibliotecas
Adicionar as bibliotecas necessárias (HttpClient, JSON.simple ou Gson) via “Libraries > Add JAR/Folder” no projeto

5.4 Execução
Implementar as chamadas REST nos arquivos ApiClient.java e Main.java
Rodar o projeto clicando em Run

6 ESTRUTURA DO PROJETO
src/
—-- main/
    —-- java/
        —-- com/
            —-- exemplo/
                —-- Main.java
                —-- ApiClient.java
                —-- City.java
    —-- resources/
        —-- config.properties
        
7 CONSIDERAÇÕES FINAIS
A aplicação Java demonstra o consumo de serviços RESTful com autenticação via cabeçalhos. A utilização da API GeoDB Cities permite a exploração de dados geográficos com chamadas simples e resposta em formato JSON. O projeto pode ser expandido para interfaces gráficas mais completas ou integração com mapas.


Aluno: Matheus Betti de Oliveira
Professor: Udo Fritzke Junior
