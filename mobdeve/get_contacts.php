<?php
include 'db_connect.php';

header('Content-Type: application/json');

$user_id = $_GET['user_id'] ?? 0;

$sql = "SELECT id, contact_name, contact_phone FROM emergency_contacts WHERE user_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$contacts = [];
while ($row = $result->fetch_assoc()) {
    $contacts[] = $row;
}

echo json_encode($contacts);

$conn->close();
?>



