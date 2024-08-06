<?php
include 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $id = intval($_POST['id']);

    $sql = "DELETE FROM auto_notifications WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('i', $id);

    if ($stmt->execute()) {
        echo "Notification deleted successfully";
    } else {
        echo "Error: " . $stmt->error;
    }
} else {
    http_response_code(405);
}
?>
