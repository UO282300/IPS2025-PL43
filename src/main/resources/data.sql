-- Limpieza de datos
DELETE FROM Matricula;
DELETE FROM Curso;
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

INSERT INTO Curso(id_curso, nombre, descripcion, precio, id_profesor) VALUES
(1,'Principantes python', 'Curso basico de python', 100.00, 1),
(2,'Java avanzado', 'Curso avanzado de Java', 150.00, 2);

INSERT INTO Matricula(id_alumno, id_curso, fecha_matricula, monto_pagado, esta_pagado) VALUES
(1, 2, '2025-10-01', 100.00, 1),
(2, 1, '2025-10-02', 200.00, 1);
