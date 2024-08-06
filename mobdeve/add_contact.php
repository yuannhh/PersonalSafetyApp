<?php
include 'db_connect.php';

$user_id = $_POST['user_id'] ?? '';
$name = $_POST['name'] ?? '';
$phone_number = $_POST['phone_number'] ?? '';

$sql = "INSERT INTO contacts (user_id, contact_name, contact_phone) VALUES (?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("iss", $user_id, $name, $phone_number);

if ($stmt->execute()) {
    echo "success";
} else {
    echo "failure";
}
?>
