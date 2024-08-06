<?php
include 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $userId = intval($_POST['user_id']);
    $notificationText = $_POST['notificationText'];
    $interval = intval($_POST['interval']);
    $timeUnit = $_POST['timeUnit'];

    $sql = "INSERT INTO auto_notifications (user_id, notification_text, interval, time_unit) VALUES (?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('ssis', $userId, $notificationText, $interval, $timeUnit);

    if ($stmt->execute()) {
        echo "Notification added successfully";
    } else {
        echo "Error: " . $stmt->error;
    }
} else {
    http_response_code(405);
}
?>
