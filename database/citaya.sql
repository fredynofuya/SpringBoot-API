-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: localhost
-- Tiempo de generación: 30-09-2026 a las 00:35:52
-- Versión del servidor: 10.4.28-MariaDB
-- Versión de PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `citaya`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `citas`
--

CREATE TABLE `citas` (
  `id_cita` int(11) NOT NULL,
  `id_paciente` int(11) NOT NULL,
  `id_medico` int(11) DEFAULT NULL,
  `fecha` date DEFAULT NULL,
  `hora` time DEFAULT NULL,
  `estado` enum('SOLICITADA','CONFIRMADA','REPROGRAMADA','CANCELADA','ATENDIDA') DEFAULT 'SOLICITADA',
  `motivo` varchar(255) DEFAULT NULL,
  `observaciones` text DEFAULT NULL,
  `fecha_creacion` datetime DEFAULT current_timestamp(),
  `fecha_actualizacion` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Volcado de datos para la tabla `citas`
--

INSERT INTO `citas` (`id_cita`, `id_paciente`, `id_medico`, `fecha`, `hora`, `estado`, `motivo`, `observaciones`, `fecha_creacion`, `fecha_actualizacion`) VALUES
(11, 6, 1, '2026-09-25', '09:00:00', 'CONFIRMADA', 'Odontología', NULL, '2026-09-25 04:21:22', '2026-09-28 21:05:17'),
(12, 5, 1, '2026-09-26', '10:30:00', 'CONFIRMADA', 'general', NULL, '2026-09-25 13:05:37', '2026-09-28 20:51:43'),
(17, 1, 2, '2026-09-30', '10:00:00', 'CONFIRMADA', 'general', NULL, '2026-09-28 22:35:13', '2026-09-28 22:35:13');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `consultorios`
--

CREATE TABLE `consultorios` (
  `id_consultorio` int(11) NOT NULL,
  `nombre` varchar(50) DEFAULT NULL,
  `ubicacion` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Volcado de datos para la tabla `consultorios`
--

INSERT INTO `consultorios` (`id_consultorio`, `nombre`, `ubicacion`) VALUES
(1, 'Consultorio 101', 'Piso 1 - Torre A'),
(2, 'Consultorio 102', 'Piso 1 - Torre A'),
(3, 'Consultorio 201', 'Piso 2 - Torre B'),
(4, 'Consultorio 202', 'Piso 2 - Torre B');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `especialidades`
--

CREATE TABLE `especialidades` (
  `id_especialidad` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Volcado de datos para la tabla `especialidades`
--

INSERT INTO `especialidades` (`id_especialidad`, `nombre`, `descripcion`) VALUES
(1, 'Medicina General', 'Consulta médica general y chequeos de rutina'),
(2, 'Pediatría', 'Atención médica para niños y adolescentes'),
(3, 'Cardiología', 'Diagnóstico y tratamiento de enfermedades del corazón'),
(4, 'Dermatología', 'Diagnóstico y tratamiento de enfermedades de la piel');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medicos`
--

CREATE TABLE `medicos` (
  `id_medico` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `id_especialidad` int(11) NOT NULL,
  `id_consultorio` int(11) NOT NULL,
  `registro_profesional` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Volcado de datos para la tabla `medicos`
--

INSERT INTO `medicos` (`id_medico`, `id_usuario`, `id_especialidad`, `id_consultorio`, `registro_profesional`) VALUES
(1, 2, 1, 1, 'RM-000123'),
(2, 9, 2, 2, 'RM-000456');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pacientes`
--

CREATE TABLE `pacientes` (
  `id_paciente` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `tipo_documento` enum('CC','TI','PASAPORTE') NOT NULL,
  `documento` varchar(30) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `eps` enum('SURA','SANITAS','SAVIASALUD') NOT NULL,
  `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

--
-- Volcado de datos para la tabla `pacientes`
--

INSERT INTO `pacientes` (`id_paciente`, `nombre`, `email`, `tipo_documento`, `documento`, `telefono`, `eps`, `fecha_registro`) VALUES
(1, 'Mariana López', 'mariana.lopez@gmail.com', 'CC', '1000234567', '3001112233', 'SURA', '2026-09-24 05:09:58'),
(2, 'Andrés Torres', 'andres.torres@gmail.com', 'CC', '1000345678', '3002223344', 'SANITAS', '2026-09-24 05:09:58'),
(3, 'Valentina Ruiz', 'valentina.ruiz@gmail.com', 'TI', '1000456789', '3003334455', 'SAVIASALUD', '2026-09-24 05:09:58'),
(4, 'Santiago Castro', 'santiago.castro@gmail.com', 'PASAPORTE', '1000567890', '3004445566', 'SURA', '2026-09-24 05:09:58'),
(5, 'Fredy Alexander Nofuya Garreta ', 'fredhyalexander@gmail.com', 'PASAPORTE', '1020472509', '3232919776', 'SANITAS', '2026-09-24 06:08:48'),
(6, 'Fredy Alexander Nofuya Garreta ', 'fredhyalexander@gmail.com', 'CC', '1033455509', '3232944766', 'SURA', '2026-09-25 04:21:22'),
(7, 'Fredy Alexander Nofuya Garreta ', 'fredhyalexander@gmail.com', 'CC', '1033477509', '3232919776', 'SANITAS', '2026-09-28 02:46:03'),
(8, 'Fredy Alexander Nofuya Garreta ', 'fredy.nofuya@udea.edu.co', 'TI', '1034355509', '3232919776', 'SANITAS', '2026-09-28 02:49:19'),
(9, 'Fredy Alexander Nofuya Garreta ', 'fredhyalexander@gmail.com', 'PASAPORTE', '10276452309', '3232919776', 'SANITAS', '2026-09-28 02:52:19');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `email` varchar(120) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `rol` enum('ADMIN','MEDICO') NOT NULL,
  `activo` tinyint(1) DEFAULT 1,
  `fecha_creacion` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`id_usuario`, `nombre`, `email`, `password`, `rol`, `activo`, `fecha_creacion`) VALUES
(1, 'Juan Perez', 'juan.perez@email.com', '123456', 'MEDICO', 1, '2026-03-13 12:36:08'),
(2, 'Maria Gomez', 'maria.gomez@email.com', '123456', 'MEDICO', 1, '2026-03-13 12:36:08'),
(3, 'Carlos Ramirez', 'carlos.ramirez@email.com', '123456', 'MEDICO', 1, '2026-03-13 12:36:08'),
(4, 'Laura Martinez', 'laura.martinez@email.com', '123456', 'MEDICO', 1, '2026-03-13 12:36:08'),
(5, 'Andres Torres', 'andres.torres@email.com', '123456', 'MEDICO', 1, '2026-03-13 12:36:08'),
(6, 'Laura Gómez', 'laura.gomez@citaya.com', '$2a$10$examplehashadmin01', 'ADMIN', 1, '2026-09-21 22:07:41'),
(7, 'Carlos Pérez', 'carlos.perez@citaya.com', '$2a$10$examplehashadmin02', 'ADMIN', 1, '2026-09-21 22:07:41'),
(8, 'Ana Martínez', 'ana.martinez@citaya.com', '$2a$10$examplehashmed01', 'MEDICO', 1, '2026-09-21 22:07:41'),
(9, 'Jorge Ramírez', 'jorge.ramirez@citaya.com', '$2a$10$examplehashmed02', 'MEDICO', 1, '2026-09-21 22:07:41');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `citas`
--
ALTER TABLE `citas`
  ADD PRIMARY KEY (`id_cita`),
  ADD KEY `id_paciente` (`id_paciente`),
  ADD KEY `id_medico` (`id_medico`);

--
-- Indices de la tabla `consultorios`
--
ALTER TABLE `consultorios`
  ADD PRIMARY KEY (`id_consultorio`);

--
-- Indices de la tabla `especialidades`
--
ALTER TABLE `especialidades`
  ADD PRIMARY KEY (`id_especialidad`);

--
-- Indices de la tabla `medicos`
--
ALTER TABLE `medicos`
  ADD PRIMARY KEY (`id_medico`),
  ADD KEY `id_usuario` (`id_usuario`),
  ADD KEY `id_especialidad` (`id_especialidad`),
  ADD KEY `id_consultorio` (`id_consultorio`);

--
-- Indices de la tabla `pacientes`
--
ALTER TABLE `pacientes`
  ADD PRIMARY KEY (`id_paciente`),
  ADD UNIQUE KEY `documento` (`documento`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `citas`
--
ALTER TABLE `citas`
  MODIFY `id_cita` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT de la tabla `consultorios`
--
ALTER TABLE `consultorios`
  MODIFY `id_consultorio` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `especialidades`
--
ALTER TABLE `especialidades`
  MODIFY `id_especialidad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `medicos`
--
ALTER TABLE `medicos`
  MODIFY `id_medico` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `pacientes`
--
ALTER TABLE `pacientes`
  MODIFY `id_paciente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `citas`
--
ALTER TABLE `citas`
  ADD CONSTRAINT `citas_ibfk_1` FOREIGN KEY (`id_paciente`) REFERENCES `pacientes` (`id_paciente`),
  ADD CONSTRAINT `citas_ibfk_2` FOREIGN KEY (`id_medico`) REFERENCES `medicos` (`id_medico`);

--
-- Filtros para la tabla `medicos`
--
ALTER TABLE `medicos`
  ADD CONSTRAINT `medicos_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  ADD CONSTRAINT `medicos_ibfk_2` FOREIGN KEY (`id_especialidad`) REFERENCES `especialidades` (`id_especialidad`),
  ADD CONSTRAINT `medicos_ibfk_3` FOREIGN KEY (`id_consultorio`) REFERENCES `consultorios` (`id_consultorio`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
