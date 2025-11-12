-- Limpiar datos previos
DELETE FROM Matricula;
DELETE FROM FacturaP;
DELETE FROM CuotaActividad;
DELETE FROM Cuota;
DELETE FROM Actividad;
DELETE FROM Alumno;
DELETE FROM Profesor;
DELETE FROM Administrador;

-- Administradores
INSERT INTO Administrador(nombre, email, password) VALUES
('Rosa', 'admin@escuela.com', 'admin123');

-- Profesores
INSERT INTO Profesor(nombre, apellido, email, telefono) VALUES
('Juan', 'Perez', 'juan.perez@escuela.com', '654321123'),
('Ana', 'Gomez', 'ana.gomez@escuela.com', '456321123');

-- Alumnos
INSERT INTO Alumno(nombre, apellido, email, telefono, es_interno) VALUES
('Carlos', 'Lopez', 'carlos.lopez@gmail.com', '123456789', 0),
('Lucia', 'Martinez', 'lucia.martinez@gmail.com', '234567891', 0),
('Pedro', 'Sanchez', 'pedro.sanchez@gmail.com', '345678912', 1),
('Marcos', 'Arias', 'marcos.arias@gmail.com', '456789123', 1),
('Ana', 'Torres', 'ana.torres@gmail.com', '567891234', 0),
('Luis', 'Fernandez', 'luis.fernandez@gmail.com', '678912345', 0);

-- Cuotas
INSERT INTO Cuota(categoria) VALUES
('Normal'),
('Colegiado'),
('Alumno de Uniovi');

-- Actividades
INSERT INTO Actividad(nombre, objetivos, contenidos, espacio, inicio_inscripcion, fin_inscripcion, fecha_inicio, fecha_fin, es_gratuita, total_plazas, empresa, isClosed) VALUES
('Principiantes Python', 'Introducir a los alumnos a Python', 'Sintaxis básica y funciones', 'L-31', '2025-10-10', '2025-11-09', '2025-11-05', '2025-11-10', 0, 3, 'TechGroup', 0),
('Java Avanzado', 'Profundizar en Java', 'Colecciones, excepciones y patrones', 'A-S-02', '2025-11-10', '2025-11-30', '2025-12-01', '2025-12-10', 0, 12, 'CodeCorp', 0),
('Fundamentos de SQL', 'Aprender SQL desde cero', 'Select, Insert, Update, Delete', 'B-10', '2025-10-01', '2025-10-31', '2025-11-20', '2025-11-25', 0, 15, 'DataSchool', 0),
('Diseño Web Básico', 'HTML, CSS y JS', 'Construcción de páginas web', 'C-05', '2025-10-01', '2025-10-31', '2025-11-25', '2025-11-30', 0, 20, 'DesignHub', 0),
('React Avanzado', 'React avanzado', 'Hooks, Context, Redux', 'D-01', '2025-11-01', '2025-12-05', '2025-12-10', '2025-12-15', 0, 15, 'FrontTech', 0),
('Node.js Intermedio', 'Backend Node', 'Express, API REST', 'E-02', '2025-10-01', '2025-10-20', '2025-11-22', '2025-11-27', 0, 15, 'CodeWorks', 0);

-- Cuotas por Actividad
INSERT INTO CuotaActividad(id_cuota, id_actividad, valor) VALUES
(1, 1, 100.00),
(2, 1, 80.00),
(3, 1, 60.00),
(1, 2, 150.00),
(2, 2, 120.00),
(3, 2, 90.00),
(1, 3, 80.00),
(2, 3, 65.00),
(3, 3, 50.00),
(1, 4, 90.00),
(2, 4, 70.00),
(3, 4, 55.00),
(1, 5, 120.00),
(2, 5, 100.00),
(3, 5, 80.00),
(1, 6, 100.00),
(2, 6, 85.00),
(3, 6, 65.00);

-- Matrículas
INSERT INTO Matricula(id_cuota_actividad, id_alumno, id_actividad, fecha_matricula, monto_pagado, esta_pagado) VALUES
(1, 1, 1, '2025-10-08', 100.00, 1),
(2, 2, 1, '2025-10-09', 0.00, 0),
(3, 3, 1, '2025-10-09', 0.00, 0),
(1, 3, 3, '2025-10-10', 80.00, 1),
(2, 4, 4, '2025-10-11', 0.00, 0),
(1, 5, 5, '2025-10-12', 0.00, 0),
(1, 6, 6, '2025-10-13', 100.00, 1),
(3, 1, 3, '2025-10-14', 0.00, 0),
(1, 2, 2, '2025-10-15', 150.00, 1),
(3, 3, 5, '2025-10-16', 0.00, 0);

-- Facturas profesores
INSERT INTO FacturaP(id_profesor, id_actividad, numero_factura, fecha_factura, cantidad, emisor_nombre, emisor_nif, emisor_direccion, esta_pagado) VALUES
(1, 1, 'F001', '2025-11-06', 250.00, 'Juan Perez', '12345678A', 'Calle Falsa 123', 0),
(2, 2, 'F002', '2025-12-02', 300.00, 'Ana Gomez', '87654321B', 'Avenida Siempre Viva 45', 1),
(1, 3, 'F003', '2025-11-21', 200.00, 'Juan Perez', '12345678A', 'Calle Falsa 123', 0),
(2, 4, 'F004', '2025-11-26', 220.00, 'Ana Gomez', '87654321B', 'Avenida Siempre Viva 45', 1),
(1, 5, 'F005', '2025-12-11', 250.00, 'Juan Perez', '12345678A', 'Calle Falsa 123', 0),
(2, 6, 'F006', '2025-11-23', 230.00, 'Ana Gomez', '87654321B', 'Avenida Siempre Viva 45', 1);

