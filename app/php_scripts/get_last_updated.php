<?php
// Report all PHP errors
error_reporting(E_ALL);
ini_set("display_errors", 1);
include("db_connect.php");

//$stmt = $conn->prepare("SELECT id, water_level, temperature, moist FROM `Plant 1`");

$stmt = $conn->prepare("SELECT `parameter_name`, `last_updated` FROM `Last Updated`");

$stmt -> execute();

$stmt -> bind_result($parameter_name, $last_updated);

$products = array();

while($stmt ->fetch()){

    $temp = array();
    
    $temp['parameter_name'] = $parameter_name;
    $temp['last_updated'] = $last_updated;   

    array_push($products,$temp);
}

if ( sizeof($products) == 0 )
    echo "no command";
else
    echo json_encode($products);

?>