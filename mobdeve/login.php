<?php
include 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email = isset($_POST['email']) ? trim($_POST['email']) : '';
    $password = isset($_POST['password']) ? trim($_POST['password']) : '';

    // Validate input
    if (empty($email) || empty($password)) {
        echo "error: Email and password are required";
        exit();
    }

    // Check if email exists
    $stmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        // Email exists, check password
        $stmt = $conn->prepare("SELECT id FROM users WHERE email = ? AND password = ?");
        $stmt->bind_param("ss", $email, $password);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            // Login successful
            $userId = $result->fetch_assoc()['id'];
            echo "success:" . $userId;
        } else {
            echo "error: Invalid email or password";
        }
    } else {
        echo "error: Email does not exist";
    }

    $stmt->close();
    $conn->close();
}
?>