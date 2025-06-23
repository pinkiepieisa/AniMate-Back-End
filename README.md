AniMate - Software Colaborativo de Animação
SOBRE:

AniMate é um software de animação colaborativa, permitindo que múltiplos usuários criem juntos em tempo real (atualmente apenas em rede local).

Repositório do frontend: https://github.com/Lazy-ghosthunter/AniMate

Este projeto possui backend (Java/Spring Boot) e frontend (JavaScript/Node.js). A conexão é local no momento.

REQUISITOS:
- Java 17 ou superior
- Maven
- Node.js
- Docker e Docker Compose
- Um IDE Java

PASSO 1: CONFIGURAR O DOCKER

1. Certifique-se de que Docker e Docker Compose estão instalados e rodando.
2. Na raiz do projeto backend, localize o arquivo docker-compose.yml.
3. O arquivo já está configurado para subir um banco PostgreSQL. Se necessário, ajuste usuário, senha e porta conforme sua preferência.
4. Execute no CMD:
   docker-compose up -d
5. Para verificar se o container está rodando:
   docker ps


PASSO 2: RODAR O BACKEND

1. Abra o projeto no IDE.
2. Navegue até backend/backend/src/main/java/com/animate/backend/BackendApplication.java.
3. Clique com o botão direito no arquivo e selecione "Run 'BackendApplication.main()'".
   

PASSO 3: RODAR O SERVER (canvas e chat)

O frontend estará disponível em http://localhost:3000.

1. Navegue até o arquivo server
2. Abra o cmd no mesmo local
3. Rode o comando:
   "node server.js"
4. acesse o modo membro acessando a página em uma nova guia, copiando e alterando o url do canvas adm para o canvas membro.
   
