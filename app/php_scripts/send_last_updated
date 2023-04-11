<?php

// Report all PHP errors
error_reporting(E_ALL);
ini_set("display_errors", 1);
include("db_connect.php");

// set default values for not-set params
if(!isset($_GET['opt'])) $_GET['opt'] = 'send_last_updated';
if(!isset($_POST['parameter_name'])) $_POST['parameter_name'] = '';
if(!isset($_POST['last_updated'])) $_POST['last_updated'] = '';

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

if ( gettype(json_decode($data)) == "string" ) {
    $status = "BAD";
    echo nl2br("\n\nError occured: CHECK THE PARAMETERS SENT OR THE JSON FORMAT");
} else {
    $status = "GOOD";

    $parameter_name = json_decode($data)->parameter_name;
    $parameter_name = htmlspecialchars($parameter_name);

    $last_updated = json_decode($data)->last_updated;
    $last_updated=htmlspecialchars($last_updated);

}

if ( $status == "GOOD" ) {
    switch ( $opt ) {
        case "send_last_updated":
            $sql = "INSERT INTO `Last Updated` (`parameter_name`, `send_last_updated`) VALUES (NULL, '".$parameter_name."', '".$send_last_updated."');";
    
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