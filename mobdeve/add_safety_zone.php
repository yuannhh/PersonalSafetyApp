<?php
include 'db_connect.php';

// Check connection
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}

$user_id = $_POST['user_id'];
$name = $_POST['name'];
$addressLine = $_POST['addressLine'];
$city = $_POST['city'];
$state = $_POST['state'];
$country = $_POST['country'];

$sql = "INSERT INTO safety_zones (user_id, name, addressLine, city, state, country) VALUES ('$user_id', '$name', '$addressLine', '$city', '$state', '$country')";

if ($conn->query($sql) === TRUE) {
  echo "New record created successfully";
} else {
  echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>
