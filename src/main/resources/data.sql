-- Limpieza de datos
DELETE FROM Matricula;
DELETE FROM Actividad;
DELETE FROM Alumno;
DELETE FROM Profesor;
DELETE FROM Administrador;

--Datos para carga inicial de la base de datos
INSERT INTO Administrador(nombre, email, password) VALUES
('Rosa', 'admin@escuela.com', 'admin123');

INSERT INTO Profesor(nombre, apellido, email, telefono) VALUES
('Juan', 'Perez', 'juan.perez@escuela.com', '654321123'),
('Ana', 'Gomez', 'ana.gomez@escuela.com', '456321123');

INSERT INTO Alumno(nombre, apellido, email, telefono, es_interno) VALUES
('Carlos', 'Lopez', 'carlos.lopez@gmail.com', '123456789', 0),
('Lucia', 'Martinez', 'lucia.martinez@gmail.com', '234567891', 0),
('Pedro', 'Sanchez', 'pedro.sanchez@gmail.com', '345678912', 1),
('Marcos', 'Arias', 'marcos.arias@gmail.com', '456789123', 1);

INSERT INTO Actividad (nombre, objetivos, contenidos, id_profesor, remuneracion, espacio, plazas, fecha, hora_inicio, hora_fin, inicio_inscripcion, fin_inscripcion, cuota, es_gratuita) VALUES
('Principiantes Python', 'Introducir a los alumnos a Python', 'Sintaxis basica y funciones', 1, 250.00, 'L-31', 20, '2025-11-05', '10:00', '13:00', '2025-10-19', '2025-11-09', 100.00, 0),
('Java Avanzado', 'Profundizar en conceptos avanzados de Java', 'Colecciones, excepciones y patrones', 2, 300.00, 'A-S-02', 40, '2025-12-01', '09:00', '14:00', '2025-11-10', '2025-11-30', 150.00, 0);

INSERT INTO Matricula(id_alumno, id_actividad, fecha_matricula, monto_pagado, esta_pagado) VALUES
(1, 2, '2025-10-08', 100.00, 1),
(2, 1, '2025-10-08', 200.00, 1);
