<?php
include ‘db_connect.php’;

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$sql = "SELECT id, name, phone_number FROM emergency_contacts";
$result = $conn->query($sql);

$contacts = array();
if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $contacts[] = $row;
    }
}

echo json_encode($contacts);

$conn->close();
?>
