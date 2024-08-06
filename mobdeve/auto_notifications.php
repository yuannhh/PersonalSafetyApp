<?php
include 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $userId = intval($_POST['user_id']);

    $sql = "SELECT * FROM auto_notifications WHERE user_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param('i', $userId);
    $stmt->execute();
    $result = $stmt->get_result();

    $notifications = array();
    while ($row = $result->fetch_assoc()) {
        $notifications[] = $row;
    }

    echo json_encode($notifications);
} else {
    http_response_code(405);
}
?>
