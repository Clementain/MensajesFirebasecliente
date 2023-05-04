const express = require('express')
const app = express()
const cors = require('cors');
const bodyParser = require('body-parser');
const admin = require('firebase-admin');
// Configurar la aplicación de Firebase
const serviceAccount = require('./clave.json');

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(cors());

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
});

app.get('/', async (req, res) => {
    res.sendStatus(status)
});

// Ruta para recibir un token de un cliente Android
app.post('/registerToken', (req) => {
    enviarNotificacion(req.body.token, req.body.titulo, req.body.cuerpo);

});

const enviarNotificacion = async (token, titulo, cuerpo) => {
    try {
        const message = {
            notification: {
                title: titulo,
                body: cuerpo
            },
            token: token
        };

        admin.messaging().send(message)
            .then((response) => {
                console.log('Notificación enviada exitosamente:', response);
            })
            .catch((error) => {
                console.log('Error al enviar la notificación:', error);
            });
    } catch (error) {
        console.log(error);

    }
}
app.listen(3000, () => {
    console.log('La aplicación está escuchando en el puerto 3000.')
})

