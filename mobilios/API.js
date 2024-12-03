const express = require('express');
const { Sequelize, DataTypes } = require('sequelize');

// Initialize Express app
const app = express();
const PORT = 3000;

// Database configuration
const sequelize = new Sequelize('LDB', 'stud', 'vLXCDmSG6EpEnhXX', {
    host: 'seklys.ila.lt',
    dialect: 'mysql',
    dialectModule: require('mysql2'),
});

// Define the `Stiprumai` model
const Stiprumai = sequelize.define('Stiprumai', {
    id: {
        type: DataTypes.INTEGER,
        primaryKey: true,
        autoIncrement: true,
    },
    matavimas: {
        type: DataTypes.INTEGER,
        allowNull: false,
    },
    sensorius: {
        type: DataTypes.STRING(50),
        allowNull: false,
    },
    stiprumas: {
        type: DataTypes.INTEGER,
        allowNull: false,
    },
}, {
    tableName: 'stiprumai',
    timestamps: false,
});

// Define the `Matavimai` model
const Matavimai = sequelize.define('Matavimai', {
    matavimas: {
        type: DataTypes.INTEGER,
        primaryKey: true,
    },
    x: {
        type: DataTypes.INTEGER,
        allowNull: false,
    },
    y: {
        type: DataTypes.INTEGER,
        allowNull: false,
    },
    atstumas: {
        type: DataTypes.FLOAT,
        allowNull: false,
    },
}, {
    tableName: 'matavimai',
    timestamps: false,
});

// Route to test database connection
app.get('/test_db', async (req, res) => {
    try {
        await sequelize.authenticate();
        res.status(200).send('Database connection is working!');
    } catch (error) {
        res.status(500).send(`Database connection error: ${error.message}`);
    }
});

// Route to get `stiprumai` data
app.get('/api/stiprumai', async (req, res) => {
    try {
        const data = await Stiprumai.findAll();
        res.json(data.map((item) => ({
            id: item.id,
            reading: item.matavimas,
            sensor: item.sensorius,
            intensity: item.stiprumas,
        })));
    } catch (error) {
        res.status(500).send(`Error fetching stiprumai: ${error.message}`);
    }
});

// Route to get `matavimai` data
app.get('/api/matavimai', async (req, res) => {
    try {
        const data = await Matavimai.findAll();
        res.json(data.map((item) => ({
            reading: item.matavimas,
            x_coordinate: item.x,
            y_coordinate: item.y,
            distance: item.atstumas,
        })));
    } catch (error) {
        res.status(500).send(`Error fetching matavimai: ${error.message}`);
    }
});

// Sync models and start the server
(async () => {
    try {
        await sequelize.sync();
        console.log('Database synchronized!');
        app.listen(PORT, () => {
            console.log(`Server is running on http://localhost:${PORT}`);
        });
    } catch (error) {
        console.error('Unable to start the server:', error);
    }
})();
