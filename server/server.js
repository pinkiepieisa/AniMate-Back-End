//importação das bibliotecas
const express = require('express');
const http = require('http');
const cors = require('cors');
const { Server } = require('socket.io');
const client = require('prom-client');

//Permite a conexão externa
const app = express();
app.use(cors());

const server = http.createServer(app);
const io = new Server(server, {
    cors: {
        origin: "*",
        //Aceita as conexões de qualquer origem
        methods: ["GET", "POST"]
    }
});

//Usuário conectado
io.on('connection', (socket) => {
    console.log('Usuário conectado: ', socket.id);

    //Usuário desconectado
    socket.on('disconnect', () => {
        console.log('Usuário desconectado: ', socket.id);
    });

    //Para a conexão do desenho
    socket.on('draw', (data) => {
        socket.broadcast.emit('draw', data);
    });

    //Para a conexão do chat
    socket.on('message', (text) => {
        console.log(`Mensagem do usuário: ${socket.id}`);

        // Envia a mensagem para todos
        io.emit('receive_message', {
            text,
            authorId: socket.id
        });
    });

});

// Ativa a coleta de métricas padrão do Node.js (CPU, Memória, Garbage Collection)
const collectDefaultMetrics = client.collectDefaultMetrics;
collectDefaultMetrics({ register: client.register });

// Cria o endpoint que o Prometheus vai ler
app.get('/metrics', async (req, res) => {
    res.set('Content-Type', client.register.contentType);
    res.end(await client.register.metrics());
});

//HTTP SERVER
const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
    console.log(`Servidor rodando na porta: ${PORT}`);
});