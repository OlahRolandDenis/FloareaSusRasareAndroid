<?php
// Report all PHP errors
error_reporting(E_ALL);
ini_set("display_errors", 1);
include("db_connect.php");

// set default values for not-set params
if(!isset($_GET['opt'])) $_GET['opt'] = 'add';
if(!isset($_POST['id'])) $_POST['id'] = 0;
if(!isset($_POST['leds_intensity'])) $_POST['leds_intensity'] = 0;
if(!isset($_POST['water_level'])) $_POST['water_level'] = 0;
if(!isset($_POST['temperature'])) $_POST['temperature'] = 0;
if(!isset($_POST['moist'])) $_POST['moist'] = 0;
if(!isset($_POST['sunlight'])) $_POST['sunlight'] = 0;
if(!isset($_POST['pump_1'])) $_POST['pump_1'] = 0;
if(!isset($_POST['pump_2'])) $_POST['pump_2'] = 0;
if(!isset($_POST['pump_3'])) $_POST['pump_3'] = 0;
if(!isset($_POST['pump_4'])) $_POST['pump_4'] = 0;

/*
    leds_intensity	
    water_level	
    temperature	
    moist	
    sunlight	
    pump_1	
    pump_2	
    pump_3	
    pump_4	
*/

// get $opt from URL parameter
$opt = $_GET['opt'];
$opt=htmlspecialchars($opt);

// Takes raw data from the request
$json = file_get_contents('php://input');

// Converts it into a PHP object
$data = json_decode(json_encode($json));

$dataType = gettype(json_decode($data));
echo "data variable has the type: {$dataType}";

$status = "GOOD";

if ( gettype(json_decode($data)) == "string" || $dataType == 'NULL' ) {
    $status = "BAD";
    echo nl2br("\n\nError occured: CHECK THE PARAMETERS SENT OR THE JSON FORMAT");
} else {
    $status = "GOOD";

    $leds_intensity = json_decode($data)->leds_intensity;
    $leds_intensity=htmlspecialchars($leds_intensity);

    $water_level = json_decode($data)->water_level;
    $water_level=htmlspecialchars($water_level);

    $temperature = json_decode($data)->temperature;
    $temperature=htmlspecialchars($temperature);

    $moist = json_decode($data)->moist;
    $moist=htmlspecialchars($moist);

    $sunlight = json_decode($data)->sunlight;
    $sunlight=htmlspecialchars($sunlight);

    $pump_1 = json_decode($data)->pump_1;
    $pump_1=htmlspecialchars($pump_1);

    $pump_2 = json_decode($data)->pump_2;
    $pump_2=htmlspecialchars($pump_2);

    $pump_3 = json_decode($data)->pump_3;
    $pump_3=htmlspecialchars($pump_3);

    $pump_4 = json_decode($data)->pump_4;
    $pump_4=htmlspecialchars($pump_4);
}

if ( $status == "GOOD" ) {
    switch ( $opt ) {
        case "add":
          $sql = "UPDATE `Plant 1`
                SET leds_intensity = $leds_intensity,
                    water_level = $water_level,
                    temperature = $temperature,
                    moist = $moist,
                    sunlight = $sunlight,
                    pump_1 = $pump_1,
                    pump_2 = $pump_2,
                    pump_3 = $pump_3,
                    pump_4 = $pump_4
                WHERE id = 48;";
    
            echo nl2br("\n {$sql}");
    
            if ( mysqli_query($conn, $sql) ) {
                    print "DATA SENT SUCCESSFULLY!";
            } else {
                    echo "Error: " . $sql . "<br>" . mysqli_error($conn);
                    echo "Error: ". mysqli_error($conn);
            }
    
        break;
    
        default:
            echo 'entered default case';
            break;
    }
}

?>