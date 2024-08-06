<?php
include 'db_connect.php';

// Check connection
if ($conn->connect_error) {
  die("Connection failed: " . $conn->connect_error);
}

$id = $_POST['id'];
$name = $_POST['name'];
$addressLine = $_POST['addressLine'];
$city = $_POST['city'];
$state = $_POST['state'];
$country = $_POST['country'];

$sql = "UPDATE safety_zones SET name='$name', addressLine='$addressLine', city='$city', state='$state', country='$country' WHERE id='$id'";

if ($conn->query($sql) === TRUE) {
  echo "Record updated successfully";
} else {
  echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>
