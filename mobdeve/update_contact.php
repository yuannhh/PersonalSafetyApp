<?php
include 'db_connect.php';

header('Content-Type: application/json');

$id = $_POST['id'] ?? 0;
$contact_name = $_POST['contact_name'] ?? '';
$contact_phone = $_POST['contact_phone'] ?? '';

if ($id == 0 || empty($contact_name) || empty($contact_phone)) {
    echo json_encode(['success' => false, 'message' => 'Invalid input']);
    exit;
}

$sql = "UPDATE emergency_contacts SET contact_name = ?, contact_phone = ? WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ssi", $contact_name, $contact_phone, $id);

if ($stmt->execute()) {
    echo json_encode(['success' => true]);
} else {
    echo json_encode(['success' => false, 'message' => 'Failed to update contact']);
}

$conn->close();
?>

