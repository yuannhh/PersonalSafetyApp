<?php
include 'db_connect.php';

header('Content-Type: application/json');

$user_id = $_POST['user_id'] ?? '';
$name = $_POST['name'] ?? '';
$phone_number = $_POST['phone_number'] ?? '';

if (empty($user_id) || empty($name) || empty($phone_number)) {
    echo json_encode(['success' => false, 'message' => 'Invalid input']);
    exit;
}

// Check if user_id exists in the users table
$sql = "SELECT id FROM users WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows == 0) {
    echo json_encode(['success' => false, 'message' => 'User ID does not exist']);
    exit;
}

$stmt->close();

$sql = "INSERT INTO emergency_contacts (user_id, contact_name, contact_phone) VALUES (?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("iss", $user_id, $name, $phone_number);

if ($stmt->execute()) {
    echo json_encode(['success' => true]);
} else {
    echo json_encode(['success' => false, 'message' => 'Failed to add contact']);
}

$stmt->close();
$conn->close();
?>

