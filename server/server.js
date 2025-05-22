//importação das bibliotecas
const express = require('express');
const http = require('http');
const cors = require('cors');
const { Server } = require('socket.io');

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

    //Para a conexão com a canvas
    socket.on('draw', (data) => {
        socket.broadcast.emit('draw', data);
    });

    //Usuário desconectado
    socket.on('disconnect', () => {
        console.log('Usuário desconectado: ', socket.id);
    });
});

//HTTP SERVER
const PORT = process.env.PORT || 3000;
server.listen(PORT, () => {
    console.log(`Servidor rodando na porta: ${PORT}`);
});