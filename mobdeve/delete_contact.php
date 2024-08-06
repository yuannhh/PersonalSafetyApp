<?php
include 'db_connect.php';

$id = $_POST['id'] ?? '';

$sql = "DELETE FROM contacts WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $id);

if ($stmt->execute()) {
    echo "success";
} else {
    echo "failure";
}
?>
