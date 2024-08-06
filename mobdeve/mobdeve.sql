-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Aug 01, 2024 at 09:17 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `mobdeve`
--

-- --------------------------------------------------------

--
-- Table structure for table `alerts`
--

CREATE TABLE `alerts` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `type` enum('Panic Alert','Weather Alert') NOT NULL,
  `user_id` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `alerts`
--

INSERT INTO `alerts` (`id`, `name`, `type`, `user_id`, `timestamp`) VALUES
(1, 'Panic Button - Immediate Alert', 'Panic Alert', 1, '2024-07-31 18:00:00'),
(2, 'Weather Alert - Safety Planning', 'Weather Alert', 1, '2024-07-31 18:15:00'),
(3, 'Panic Button - Immediate Alert', 'Panic Alert', 2, '2024-07-31 18:30:00'),
(4, 'Weather Alert - Safety Planning', 'Weather Alert', 2, '2024-07-31 18:45:00'),
(5, 'Panic Button - Immediate Alert', 'Panic Alert', 3, '2024-07-31 19:00:00'),
(6, 'Weather Alert - Safety Planning', 'Weather Alert', 3, '2024-07-31 19:15:00'),
(7, 'Panic Button - Immediate Alert', 'Panic Alert', 3, '2024-07-31 19:30:00'),
(8, 'Weather Alert - Safety Planning', 'Weather Alert', 3, '2024-07-31 19:45:00');

-- --------------------------------------------------------

--
-- Table structure for table `auto_notifications`
--

CREATE TABLE `auto_notifications` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `notification_text` varchar(255) NOT NULL,
  `interval` bigint(20) NOT NULL,
  `time_unit` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `auto_notifications`
--

INSERT INTO `auto_notifications` (`id`, `user_id`, `notification_text`, `interval`, `time_unit`) VALUES
(1, 1, 'Reminder to check in', 30, 'Minutes'),
(2, 2, 'Emergency alert', 2, 'Hours'),
(3, 3, 'Daily safety check', 1, 'Days');

-- --------------------------------------------------------

--
-- Table structure for table `emergency_contacts`
--

CREATE TABLE `emergency_contacts` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `contact_name` varchar(255) NOT NULL,
  `contact_phone` varchar(15) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `emergency_contacts`
--

INSERT INTO `emergency_contacts` (`id`, `user_id`, `contact_name`, `contact_phone`) VALUES
(1, 1, 'Maria Santos', '+63 912 345 678'),
(2, 2, 'Jose Reyes', '+63 923 456 789'),
(3, 3, 'Ana Cruz', '+63 934 567 890');

-- --------------------------------------------------------

--
-- Table structure for table `incidents`
--

CREATE TABLE `incidents` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `incident_type` varchar(255) NOT NULL,
  `incident_details` text NOT NULL,
  `location` varchar(255) NOT NULL,
  `timestamp` datetime NOT NULL,
  `status` varchar(50) DEFAULT 'reported'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `incidents`
--

INSERT INTO `incidents` (`id`, `user_id`, `incident_type`, `incident_details`, `location`, `timestamp`, `status`) VALUES
(1, 1, 'Accident', 'Car accident on the highway.', 'Highway 101, Manila', '2024-07-31 10:15:00', 'reported'),
(2, 2, 'Theft', 'Wallet stolen from the backpack.', 'Ayala Mall, Cebu City', '2024-07-31 14:30:00', 'in progress'),
(3, 3, 'Emergency', 'Medical emergency at home.', '1234 Elm Street, Davao City', '2024-07-31 16:00:00', 'resolved'),
(4, 1, 'Accident', 'Bicycle accident at the park.', 'Rizal Park, Manila', '2024-07-31 18:45:00', 'reported'),
(5, 2, 'Theft', 'Phone snatched on the street.', 'J. Center Mall, Cebu City', '2024-07-31 20:00:00', 'reported');

-- --------------------------------------------------------

--
-- Table structure for table `safety_zones`
--

CREATE TABLE `safety_zones` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `addressLine` varchar(255) NOT NULL,
  `city` varchar(100) NOT NULL,
  `state` varchar(100) NOT NULL,
  `country` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `safety_zones`
--

INSERT INTO `safety_zones` (`id`, `user_id`, `name`, `addressLine`, `city`, `state`, `country`) VALUES
(1, 1, 'Safe House', '123 Safety St', 'Manila', 'Metro Manila', 'Philippines'),
(2, 2, 'Emergency Center', '456 Secure Ave', 'Cebu City', 'Cebu', 'Philippines'),
(3, 3, 'Protective Shelter', '789 Shield Rd', 'Davao City', 'Davao', 'Philippines'),
(4, 3, 'Safe Haven', '321 Guardian Way', 'Quezon City', 'Metro Manila', 'Philippines'),
(5, 3, 'Rescue Hub', '654 Refuge Ln', 'Baguio', 'Benguet', 'Philippines'),
(6, 3, 'Secure Point', '987 Secure Pl', 'Makati', 'Metro Manila', 'Philippines');

-- --------------------------------------------------------

--
-- Table structure for table `session_tokens`
--

CREATE TABLE `session_tokens` (
  `token` varchar(255) NOT NULL,
  `user_id` int(11) NOT NULL,
  `expiry` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `date_of_birth` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `email`, `password`, `full_name`, `date_of_birth`) VALUES
(1, 'ian.jones@gmail.com', 'password606', 'Ian Jones', '1994-10-20'),
(2, 'julia.martin@gmail.com', 'password707', 'Julia Martin', '1987-08-07'),
(3, 'test@gmail.com', 'password', 'test', '2000-07-20');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `auto_notifications`
--
ALTER TABLE `auto_notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `emergency_contacts`
--
ALTER TABLE `emergency_contacts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `incidents`
--
ALTER TABLE `incidents`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `safety_zones`
--
ALTER TABLE `safety_zones`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `session_tokens`
--
ALTER TABLE `session_tokens`
  ADD PRIMARY KEY (`token`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `auto_notifications`
--
ALTER TABLE `auto_notifications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `emergency_contacts`
--
ALTER TABLE `emergency_contacts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `incidents`
--
ALTER TABLE `incidents`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `safety_zones`
--
ALTER TABLE `safety_zones`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `auto_notifications`
--
ALTER TABLE `auto_notifications`
  ADD CONSTRAINT `auto_notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `emergency_contacts`
--
ALTER TABLE `emergency_contacts`
  ADD CONSTRAINT `emergency_contacts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `incidents`
--
ALTER TABLE `incidents`
  ADD CONSTRAINT `incidents_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `safety_zones`
--
ALTER TABLE `safety_zones`
  ADD CONSTRAINT `safety_zones_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `session_tokens`
--
ALTER TABLE `session_tokens`
  ADD CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
