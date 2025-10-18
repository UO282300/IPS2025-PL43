--Primero se deben borrar todas las tablas
DROP TABLE IF EXISTS Matricula;
DROP TABLE IF EXISTS Actividad;
DROP TABLE IF EXISTS Alumno;
DROP TABLE IF EXISTS Profesor;
DROP TABLE IF EXISTS Administrador;
DROP TABLE IF EXISTS FacturaP;
DROP TABLE IF EXISTS PagoProfesor;

--Luego se anyaden las nuevas
CREATE TABLE Administrador (
    id_admin INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE Profesor (
    id_profesor INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(20)
);

CREATE TABLE Alumno (
    id_alumno INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    es_interno BOOLEAN NOT NULL DEFAULT 1
);

CREATE TABLE Actividad (
    id_actividad INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre VARCHAR(150) NOT NULL,
    objetivos TEXT,
    contenidos TEXT,
    id_profesor INTEGER,
    remuneracion DECIMAL(10,2),
    espacio VARCHAR(100),
    total_plazas INTEGER,
    fecha DATE,
    hora_inicio TIME,
    hora_fin TIME,
    inicio_inscripcion DATE,
    fin_inscripcion DATE,
    cuota DECIMAL(10,2) DEFAULT 0,
    es_gratuita BOOLEAN DEFAULT 0,
    FOREIGN KEY (id_profesor) REFERENCES Profesor(id_profesor)
);


CREATE TABLE Matricula (
    id_matricula INTEGER PRIMARY KEY AUTOINCREMENT,
    id_alumno INTEGER NOT NULL,
    id_actividad INTEGER NOT NULL,
    fecha_matricula DATE NOT NULL,
    monto_pagado DECIMAL(10,2) DEFAULT 0,
    esta_pagado BOOLEAN NOT NULL DEFAULT 0,
    FOREIGN KEY (id_alumno) REFERENCES Alumno(id_alumno),
    FOREIGN KEY (id_actividad) REFERENCES Actividad(id_actividad)
    FOREIGN KEY (id_actividad) REFERENCES Curso(id_actividad)
);

CREATE TABLE FacturaP (
    id_factura INTEGER PRIMARY KEY AUTOINCREMENT,
    id_profesor INTEGER NOT NULL,
    numero_factura VARCHAR(50) NOT NULL,
    fecha_factura DATE NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL,
    emisor_nombre VARCHAR(100) NOT NULL,
    emisor_nif VARCHAR(20) NOT NULL,
    emisor_direccion VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_profesor) REFERENCES Profesor(id_profesor)
);

CREATE TABLE PagoProfesor (
    id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
    id_profesor INTEGER NOT NULL,
    id_factura INTEGER NOT NULL,
    fecha_pago DATE NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL,
    estado_pago VARCHAR(20) DEFAULT 'Pendiente',
    FOREIGN KEY (id_profesor) REFERENCES Profesor(id_profesor),
    FOREIGN KEY (id_factura) REFERENCES Factura(id_factura)
);