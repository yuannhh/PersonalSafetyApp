<?php
include 'db_connect.php';

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get data from POST request
$user_id = $_POST['user_id'];
$incident_type = $_POST['incident_type'];
$incident_details = $_POST['incident_details'];
$location = $_POST['location'];
$status = $_POST['status'];
$timestamp = $_POST['timestamp'];

// Insert data into the database
$sql = "INSERT INTO incidents (user_id, incident_type, incident_details, location, timestamp, status)
VALUES ('$user_id', '$incident_type', '$incident_details', '$location', '$timestamp', '$status')";

if ($conn->query($sql) === TRUE) {
    echo "New record created successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

// Close connection
$conn->close();
?>
