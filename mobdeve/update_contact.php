<?php
include ‘db_connect.php’;

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$id = $_POST['id'];
$name = $_POST['name'];
$phone_number = $_POST['phone_number'];

$sql = "UPDATE emergency_contacts SET name='$name', phone_number='$phone_number' WHERE id=$id";

if ($conn->query($sql) === TRUE) {
    echo "Record updated successfully";
} else {
    echo "Error updating record: " . $conn->error;
}

$conn->close();
?>
