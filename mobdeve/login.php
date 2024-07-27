<?php
include 'db_connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $email = isset($_POST['email']) ? trim($_POST['email']) : '';
    $password = isset($_POST['password']) ? trim($_POST['password']) : '';

    // Validate input
    if (empty($email) || empty($password)) {
        echo "Email and password are required";
        exit();
    }

    // Check if email exists
    $stmt = $conn->prepare("SELECT * FROM users WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        // Email exists, check password
        $stmt = $conn->prepare("SELECT * FROM users WHERE email = ? AND password = ?");
        $stmt->bind_param("ss", $email, $password);
        $stmt->execute();
        $result = $stmt->get_result();

        if ($result->num_rows > 0) {
            echo "Login successful";
        } else {
            echo "Invalid email or password";
        }
    } else {
        echo "Email does not exist";
    }

    $stmt->close();
    $conn->close();
}
?>
