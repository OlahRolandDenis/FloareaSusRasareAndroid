<?php
// Report all PHP errors
error_reporting(E_ALL);
ini_set("display_errors", 1);
include("db_connect.php");

$stmt = $conn->prepare("SELECT id, water_level, temperature, moist FROM `Plant 1`");

$stmt ->execute();

$stmt -> bind_result($id, $water_level, $temperature, $moist);

$products = array();

while($stmt ->fetch()){

    $temp = array();
    
    $temp['id'] = $id;
    $temp['water_level'] = $water_level;
    $temp['temperature'] = $temperature;
    $temp['moist'] = $moist;
   

    array_push($products,$temp);
}

echo json_encode($products);

?>