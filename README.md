AniMate - Software Colaborativo de Animação

Este projeto possui backend (Java/Spring Boot) e frontend (JavaScript/Node.js). A conexão é local no momento.

REQUISITOS:
- Java 17 ou superior
- Maven
- Node.js e npm
- Docker e Docker Compose
- IntelliJ IDEA (ou outro IDE Java)

PASSO 1: CONFIGURAR O DOCKER

1. Certifique-se de que Docker e Docker Compose estão instalados e rodando.
2. Na raiz do projeto backend, localize o arquivo docker-compose.yml.
3. O arquivo já está configurado para subir um banco PostgreSQL. Se necessário, ajuste usuário, senha e porta conforme sua preferência.
4. Execute no CMD:
   docker-compose up -d
5. Para verificar se o container está rodando:
   docker ps

PASSO 2: CONFIGURAR O BACKEND

1. No arquivo application.properties do backend, verifique se as propriedades de conexão com o banco estão corretas (URL, usuário, senha).
2. Certifique-se de que existe a linha:
   spring.jpa.hibernate.ddl-auto=update
3. Isso garante que a tabela users será criada automaticamente ao rodar a aplicação.

PASSO 3: RODAR O BACKEND

1. Abra o projeto no IntelliJ IDEA.
2. Navegue até backend/backend/src/main/java/com/animate/backend/BackendApplication.java.
3. Clique com o botão direito no arquivo e selecione "Run 'BackendApplication.main()'".
4. O backend estará disponível em http://localhost:8080.

PASSO 4: RODAR O FRONTEND

1. Clone o repositório do frontend:
   git clone https://github.com/Lazy-ghosthunter/AniMate.git
2. Acesse a pasta do frontend:
   cd AniMate
3. Instale as dependências do projeto:
   npm install
4. Para rodar o frontend via CMD, execute:
   npm start
5. O frontend estará disponível em http://localhost:3000.

SOBRE:

AniMate é um software de animação colaborativa, permitindo que múltiplos usuários criem juntos em tempo real (atualmente apenas em rede local).

Repositório do frontend: https://github.com/Lazy-ghosthunter/AniMate
