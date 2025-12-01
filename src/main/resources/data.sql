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
INSERT INTO Profesor(nombre, apellido, email, telefono,nif,direccion,isEmpresa) VALUES
('Claudio', 'Perez', 'c.perez@escuela.com','1','684321123', 'avenida',0),
('Raquel', 'Perez', 'r.perez@escuela.com','1','694321123', 'avenida', 0);


-- Alumnos
INSERT INTO Alumno(nombre, apellido, email, telefono, es_interno) VALUES
('Alicia', 'Torres', 'al.torres@gmail.com', '567891234', 0),
('Juan', 'Torres', 'jt.torres@gmail.com', '567891234', 0),
('Fanjul', 'García', 'fg.garcia@gmail.com', '567891234', 0),
('Marta', 'Maria', 'mm.maria@gmail.com', '567891234', 0),
('Mateo', 'Juarez', 'mj.juarez@gmail.com', '567891234', 0);

-- Cuotas
INSERT INTO Cuota(categoria) VALUES
('Normal'),
('Colegiados');

-- Actividades
INSERT INTO Actividad(nombre, objetivos, contenidos, espacio, inicio_inscripcion, fin_inscripcion, fecha_inicio, fecha_fin, es_gratuita, total_plazas, empresa, isClosed) VALUES
('Nuevas técnicas de Prueba','Técnicas nuevas de prueba','Tecnicas modernas para hacer test','C-02','2025-07-01','2025-07-31','2025-09-01','2025-09-03',0,2,'CodeCorp', 0),
('Administración de BBDD Neo4J','BDD Neo4J','Manejo de BD','C-03','2025-12-01','2025-12-21','2025-12-26','2025-12-30',0,5,'VinCorp', 0);
-- Cuotas por Actividad
INSERT INTO CuotaActividad(id_cuota, id_actividad, valor) VALUES
(1, 1, 400.00),
(2, 1, 200.00),
(1, 2, 400.00),
(2, 2, 200.00);

-- Matrículas
INSERT INTO Matricula(id_alumno, id_cuota_actividad, id_actividad, fecha_matricula, monto_pagado, esta_pagado, numero_matriculados, integrantes_ids,monto_total) VALUES
(1, 2, 1, '2025-07-15', 200.00, 1,1,1,200.00),
(2, 1, 1, '2025-07-20', 400.00, 1,1,2,400.00),
(4, 3, 2, '2025-12-01', 300.00, 1,1,4,400.00),
(3, 4, 2, '2025-12-01', 300.00, 1,1,3,200.00),
(5, 4, 2, '2025-12-02', 0.00, 0,1,5,200.00);

-- Facturas profesores
INSERT INTO FacturaP(id_profesor, id_actividad, remuneracion, numero_factura, fecha_factura, cantidad, emisor_nombre, emisor_nif, emisor_direccion, esta_pagado) VALUES
(1, 1, 250.00, 'F001', '2025-09-04', 250.00, 'Claudio Perez', '12345678A', 'Calle Falsa 123', 1),
(2, 2, 750.00, '-1', '', 0, '', '', '', 0);

INSERT INTO PagoAlumno (id_matricula,fecha_pago,cantidad,metodo_pago) VALUES
(1,'2025-07-16',200.00,'Transferencia'),
(2,'2025-07-20',400.00,'Transferencia'),
(4,'2025-12-02',300.00,'Transferencia'),
(3,'2025-12-01',100.00,'Transferencia'),
(3,'2025-12-02',100.00,'Transferencia');  

INSERT INTO PagoProfesor(id_profesor,id_factura,id_actividad,fecha_pago,cantidad,estado_pago) VALUES
(1,1,1,'2025-09-05',250.00,'Completado');