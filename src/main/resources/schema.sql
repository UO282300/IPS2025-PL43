--Primero se deben borrar todas las tablas
DROP TABLE IF EXISTS Matricula;
DROP TABLE IF EXISTS Actividad;
DROP TABLE IF EXISTS Alumno;
DROP TABLE IF EXISTS Profesor;
DROP TABLE IF EXISTS Administrador;
DROP TABLE IF EXISTS Devoluciones;
DROP TABLE IF EXISTS FacturaP;
DROP TABLE IF EXISTS PagoProfesor;
DROP TABLE IF EXISTS PagoAlumno;
DROP TABLE IF EXISTS DevolucionProfesor;
DROP TABLE IF EXISTS CuotaActividad;
DROP TABLE IF EXISTS Cuota;

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
    telefono VARCHAR(20),
    nif VARCHAR(20) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    isEmpresa BOOLEAN DEFAULT 0
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
    espacio VARCHAR(100),
    inicio_inscripcion DATE,
    fin_inscripcion DATE,
    fecha_inicio DATE,
    fecha_fin DATE,
    es_gratuita BOOLEAN DEFAULT 0,
    total_plazas INTEGER,
    empresa VARCHAR(100),
    isClosed BOOLEAN DEFAULT 0,
    isCancelada BOOLEAN DEFAULT 0
);

CREATE TABLE Matricula (
    id_matricula INTEGER PRIMARY KEY AUTOINCREMENT,
    id_cuota_actividad INTEGER NOT NULL,
    id_alumno INTEGER NOT NULL,
    id_actividad INTEGER NOT NULL,
    fecha_matricula DATE NOT NULL,
    monto_pagado DECIMAL(10,2) DEFAULT 0,
    esta_pagado BOOLEAN NOT NULL DEFAULT 0,
    isCancelada BOOLEAN DEFAULT 0,
    numero_matriculados INTEGER NOT NULL,
    integrantes_ids TEXT,
    isRetrasada BOOLEAN DEFAULT 0,
    monto_total DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (id_cuota_actividad) REFERENCES CuotaActividad(id_cuota_actividad),
    
    FOREIGN KEY (id_alumno) REFERENCES Alumno(id_alumno),
    FOREIGN KEY (id_actividad) REFERENCES Actividad(id_actividad)
);

CREATE TABLE PagoAlumno (
    id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
    id_matricula INTEGER NOT NULL,
    fecha_pago DATE NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(50) DEFAULT 'Transferencia',
    FOREIGN KEY (id_matricula) REFERENCES Matricula(id_matricula)
);


CREATE TABLE FacturaP (
    id_factura INTEGER PRIMARY KEY AUTOINCREMENT,
    id_profesor INTEGER NOT NULL,
    id_actividad INTEGER NOT NULL,
    numero_factura VARCHAR(50) NOT NULL,
    fecha_factura DATE NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL,
    emisor_nombre VARCHAR(100) NOT NULL,
    emisor_nif VARCHAR(20) NOT NULL,
    emisor_direccion VARCHAR(255) NOT NULL,
    esta_pagado BOOLEAN NOT NULL DEFAULT 0,
    FOREIGN KEY (id_profesor) REFERENCES Profesor(id_profesor),
    FOREIGN KEY (id_actividad) REFERENCES Actividad(id_actividad)
);

CREATE TABLE PagoProfesor (
    id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
    id_profesor INTEGER NOT NULL,
    id_factura INTEGER NOT NULL,
    id_actividad INTEGER NOT NULL,
    fecha_pago DATE NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL,
    estado_pago VARCHAR(20) DEFAULT 'Pendiente',
    FOREIGN KEY (id_profesor) REFERENCES Profesor(id_profesor),
    FOREIGN KEY (id_factura) REFERENCES FacturaP(id_factura),
    FOREIGN KEY (id_actividad) REFERENCES Actividad(id_actividad)
);

CREATE TABLE Devoluciones (
    id_devolucion INTEGER PRIMARY KEY AUTOINCREMENT,
    id_matricula INTEGER NOT NULL,
    id_alumno INTEGER NOT NULL,
    id_actividad INTEGER NOT NULL,
    fecha_solicitada DATE NOT NULL,
    fecha_enviada DATE NOT NULL,
    monto_devuelto DECIMAL(10,2) DEFAULT 0,
    metodo_pago VARCHAR(50) DEFAULT 'Transferencia',
    FOREIGN KEY (id_matricula) REFERENCES Matricula(id_matricula),
    FOREIGN KEY (id_alumno) REFERENCES Alumno(id_alumno),
    FOREIGN KEY (id_actividad) REFERENCES Actividad(id_actividad)

);

CREATE TABLE DevolucionProfesor (
    id_devolucion INTEGER PRIMARY KEY AUTOINCREMENT,
    id_profesor INTEGER NOT NULL,
    id_factura INTEGER NOT NULL,
    id_actividad INTEGER NOT NULL,
    fecha_devolucion DATE NOT NULL,
    cantidad DECIMAL(10,2) NOT NULL,
    motivo TEXT,
    esta_pagado BOOLEAN NOT NULL DEFAULT 1,

    FOREIGN KEY (id_profesor) REFERENCES Profesor(id_profesor),
    FOREIGN KEY (id_factura) REFERENCES FacturaP(id_factura),
    FOREIGN KEY (id_actividad) REFERENCES Actividad(id_actividad)
);

CREATE TABLE CuotaActividad (
	id_cuota_actividad INTEGER PRIMARY KEY AUTOINCREMENT,
	id_cuota INTEGER NOT NULL,
	id_actividad INTEGER NOT NULL,
	valor DECIMAL(10,2) DEFAULT 0,
	
	FOREIGN KEY (id_cuota) REFERENCES Cuota(id_cuota),
	FOREIGN KEY (id_actividad) REFERENCES Actividad(id_actividad)
	
);

CREATE TABLE Cuota (
	id_cuota INTEGER PRIMARY KEY AUTOINCREMENT,
	categoria VARCHAR(100) NOT NULL
);
