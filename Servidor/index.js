const express = require('express');
const app = express();
const cors = require('cors');
const bodyParser = require('body-parser');
const admin = require('firebase-admin');
const sqlite3 = require('sqlite3').verbose();

// Configurar la aplicación de Firebase
const serviceAccount = require('./clave.json');

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());
app.use(cors());

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
});

// Conectar con la base de datos SQLite
const db = new sqlite3.Database('tokens.db', (err) => {
    if (err) {
        console.error(err.message);
    } else {
        console.log('Conectado a la base de datos tokens.db.');
        db.serialize(() => {
            db.run("CREATE TABLE IF NOT EXISTS tokens (token TEXT)");
        });
    }
});

app.get('/', async (req, res) => {
    res.send("Servidor funcionando");
});

// Ruta para recibir un token de un cliente Android
app.post('/registerToken', (req, res) => {
    const { token, titulo, cuerpo } = req.body;

    // Insertar el token en la tabla de la base de datos
    db.run('INSERT INTO tokens (token) VALUES (?)', [token], (err) => {
        if (err) {
            console.error(err);
            res.sendStatus(500);
        } else {
            // Enviar la notificación al nuevo token y a todos los tokens almacenados en la base de datos
            enviarNotificacion(token, titulo, cuerpo);
            db.all('SELECT token FROM tokens WHERE token != ?', [token], (err, rows) => {
                if (err) {
                    console.error(err);
                } else {
                    rows.forEach(({ token }) => {
                        enviarNotificacion(token, titulo, cuerpo);
                    });
                }
            });
            res.sendStatus(200);
        }
    });
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
            .then(() => {
                console.log('Notificación enviada exitosamente:');
            })
            .catch(() => {
                console.log('Error al enviar la notificación');
            });
    } catch (error) {
        console.log(error);
    }
}

app.listen(3000, () => {
    console.log('La aplicación está escuchando en el puerto 3000.');
});
