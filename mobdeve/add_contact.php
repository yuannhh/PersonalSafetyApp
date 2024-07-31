<?php
include ‘db_connect.php’;

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$name = $_POST['name'];
$phone_number = $_POST['phone_number'];

$sql = "INSERT INTO emergency_contacts (name, phone_number) VALUES ('$name', '$phone_number')";

if ($conn->query($sql) === TRUE) {
    echo "New record created successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>