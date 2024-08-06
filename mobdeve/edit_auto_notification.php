<?php
include 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $id = intval($_POST['id']);
    $notificationText = $_POST['notificationText'];
    $interval = intval($_POST['interval']);
    $timeUnit = $_POST['timeUnit'];

    $sql = "UPDATE auto_notifications SET notification_text = ?, interval = ?, time_unit = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('ssis', $notificationText, $interval, $timeUnit, $id);

    if ($stmt->execute()) {
        echo "Notification edited successfully";
    } else {
        echo "Error: " . $stmt->error;
    }
} else {
    http_response_code(405);
}
?>
