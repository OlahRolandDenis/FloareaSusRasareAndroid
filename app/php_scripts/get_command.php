<?php
// Report all PHP errors
error_reporting(E_ALL);
ini_set("display_errors", 1);
include("db_connect.php");

//$stmt = $conn->prepare("SELECT id, water_level, temperature, moist FROM `Plant 1`");

$stmt = $conn->prepare("SELECT `id`, `parameter_name`, `value` FROM `Commands`");

$stmt -> execute();

$stmt -> bind_result($id, $parameter_name, $value);

$products = array();

while($stmt ->fetch()){

    $temp = array();
    
    $temp['id'] = $id;
    $temp['parameter_name'] = $parameter_name;
    $temp['value'] = $value;   

    array_push($products,$temp);
}

if ( sizeof($products) == 0 )
    echo "no command";
else
    echo json_encode($products);

?>