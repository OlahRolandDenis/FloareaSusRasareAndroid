<?php
// Report all PHP errors
error_reporting(E_ALL);
ini_set("display_errors", 1);
include("db_connect.php");

//$stmt = $conn->prepare("SELECT id, water_level, temperature, moist FROM `Plant 1`");

$stmt = $conn->prepare("SELECT * FROM `Plant 1`");

$stmt -> execute();

$stmt -> bind_result(
    $id, 
    $leds_intensity,
    $water_level,
    $temperature,
    $moist,
    $sunlight,
    $pump_1,
    $pump_2,
    $pump_3,
    $pump_4
);

$products = array();

while($stmt ->fetch()){

    $temp = array();
    
    $temp['leds_intensity'] = $leds_intensity;
    $temp['water_level'] = $water_level;   
    $temp['temperature'] = $temperature;  
    $temp['moist'] = $moist;  
    $temp['sunlight'] = $sunlight;  
    $temp['pump_1'] = $pump_1;  
    $temp['pump_2'] = $pump_2;  
    $temp['pump_3'] = $pump_3;
    $temp['pump_4'] = $pump_4;  

    array_push($products,$temp);
}

echo json_encode($products);

?>