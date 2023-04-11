<?php

// Report all PHP errors
error_reporting(E_ALL);
ini_set("display_errors", 1);
include("db_connect.php");

// set default values for not-set params
if(!isset($_GET['opt'])) $_GET['opt'] = 'delete_command';
if(!isset($_POST['parameter_name'])) $_POST['parameter_name'] = '';
if(!isset($_POST['value'])) $_POST['value'] = -1;

// get $opt from URL parameter
$opt = $_GET['opt'];
$opt=htmlspecialchars($opt);

$status = "GOOD";

switch ( $opt ) {
    case "delete_command":
        $sql = "DELETE FROM `Commands` WHERE `Commands`.`id` = 1";
    
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

?>