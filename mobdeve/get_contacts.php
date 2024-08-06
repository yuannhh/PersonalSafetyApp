<?php
include 'db_connect.php';

$user_id = $_GET['user_id'] ?? 0;

$sql = "SELECT * FROM contacts WHERE user_id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$contacts = [];
while ($row = $result->fetch_assoc()) {
    $contacts[] = $row;
}

$output = array_map(function($contact) {
    return implode('|', [$contact['id'], $contact['contact_name'], $contact['contact_phone']]);
}, $contacts);

echo implode("\n", $output);
?>
