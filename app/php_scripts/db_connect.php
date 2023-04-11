<?php
$servername = "localhost";
$username = "oxygenieuser";
$password = "12345678";
$dbname = "oxygenie";

// Create connection
$conn = mysqli_connect($servername, $username, $password, $dbname);
// Check connection
if (!$conn) {
  die("Connection failed: " . mysqli_connect_error());
}

/*fisierul se conecteaza la baza de date dinainte creata, numita "dictionar", care
cuprinde cateva tabele : cuvinte, domenii, useri, etc.*/
?>