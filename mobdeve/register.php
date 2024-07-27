<?php
include 'db_connect.php';

// Retrieve POST data
$fullName = isset($_POST['full_name']) ? trim($_POST['full_name']) : '';
$dob = isset($_POST['date_of_birth']) ? trim($_POST['date_of_birth']) : '';
$email = isset($_POST['email']) ? trim($_POST['email']) : '';
$password = isset($_POST['password']) ? trim($_POST['password']) : '';

// Validate input data
if (empty($fullName) || empty($dob) || empty($email) || empty($password)) {
    echo json_encode(["message" => "All fields are required"]);
    exit();
}

// Check if email already exists
$sql = "SELECT * FROM users WHERE email = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode(["message" => "Email already exists"]);
    $stmt->close();
    $conn->close();
    exit();
}

// Insert new user into the database
$sql = "INSERT INTO users (email, password, full_name, date_of_birth) VALUES (?, ?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ssss", $email, $password, $fullName, $dob);

if ($stmt->execute()) {
    echo json_encode(["message" => "Account created successfully"]);
} else {
    echo json_encode(["message" => "Error: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>
