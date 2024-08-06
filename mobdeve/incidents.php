<?php
include "db_connect.php";

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get user_id from POST request
$user_id = $_POST['user_id'] ?? 0;

$sql = "SELECT * FROM incidents WHERE user_id = ? ORDER BY timestamp DESC";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$incidents = array();
while ($row = $result->fetch_assoc()) {
    $incidents[] = $row;
}

echo json_encode($incidents);

$stmt->close();
$conn->close();
?>
