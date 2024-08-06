<?php
include 'db_connect.php';
session_start(); // Start the session to access session variables

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Check if user is logged in and has a valid user ID in the session
    if (!isset($_SESSION['user_id']) || intval($_SESSION['user_id']) <= 0) {
        echo json_encode(["error" => "Invalid or missing user ID"]);
        exit();
    }

    $user_id = intval($_SESSION['user_id']);

    $stmt = $conn->prepare("SELECT * FROM safety_zones WHERE user_id = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();

    $safety_zones = [];
    while ($row = $result->fetch_assoc()) {
        $safety_zones[] = $row;
    }

    header('Content-Type: application/json');
    echo json_encode($safety_zones);

    $stmt->close();
    $conn->close();
} else {
    echo json_encode(["error" => "Invalid request method"]);
}
?>
