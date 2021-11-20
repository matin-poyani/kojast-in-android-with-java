<?php
require_once __DIR__ . '/DB.php';

$db = new DB();

$response = [
    'error' => 'عملیات نامشخص',
    'data' => NULL,
    'ts' => time(),
];

if (isset($_GET['action'])) {
    $action = strtolower($_GET['action']);
    switch ($action) {
        case 'cities':
        case 'points':
        case 'states':
        case 'types':
            $response['error'] = '';
            require_once __DIR__ . '/actions/' . $action . '.php';
            break;
        default:
            $response['error'] = 'عملیات اشتباه';
    }
}

header('Content-Type: application/json');
echo json_encode($response, JSON_UNESCAPED_UNICODE);