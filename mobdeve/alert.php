<?php
include "db_connect.php";

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get user_id from POST request
$user_id = $_POST['user_id'] ?? 0;

$sql = "SELECT * FROM alarms WHERE user_id = ? ORDER BY id DESC";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$alarms = array();
while ($row = $result->fetch_assoc()) {
    $alarms[] = $row;
}

echo json_encode($alarms);

$stmt->close();
$conn->close();
?>
