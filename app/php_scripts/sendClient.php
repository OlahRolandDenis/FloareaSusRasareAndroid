<?php
// Report all PHP errors
error_reporting(E_ALL);
ini_set("display_errors", 1);
include("db_connect.php");
//id 	id_sofer 	id_client 	nume 	tel 	nr_masina 	mac 	lat 	lon 	adresa 	link 	status 	obs 	data_start 	data_client 	data_stop 
if(!isset($_POST['opt'])) $_POST['opt'] = '';
if(!isset($_POST['key'])) $_POST['key'] = '';
if(!isset($_POST['mac'])) $_POST['mac'] = '';
if(!isset($_POST['nume'])) $_POST['nume'] = '';
if(!isset($_POST['tel'])) $_POST['tel'] = '';
if(!isset($_POST['lon'])) $_POST['lon'] = '';
if(!isset($_POST['lat'])) $_POST['lat'] = '';
if(!isset($_POST['adresa'])) $_POST['adresa'] = '';
if(!isset($_POST['link'])) $_POST['link'] = '';
if(!isset($_POST['obs'])) $_POST['obs'] = '';
if(!isset($_POST['nota'])) $_POST['nota'] = '';

/*
$_POST['key'] = '3d449c6640760504dde5ba63f9e719bf';
$_POST['mac'] = '9494093d-0f75-4fe4-9f12-30f764f2105a';
$_POST['nume'] = 'nu';
$_POST['tel'] = 'te';
$_POST['lon'] = 'lo';
$_POST['lat'] = 'la';
$_POST['adresa'] = 'adr';
$_POST['link'] = 'lk';
$_POST['obs'] = 'ob';
*/
$opt = $_POST['opt'];
$opt=htmlspecialchars($opt);

$mac = $_POST['mac'];
$mac=htmlspecialchars($mac);

$key = $_POST['key'];
$key=htmlspecialchars($key);

$nume = $_POST['nume'];
$nume=htmlspecialchars($nume);

$tel = $_POST['tel'];
$tel=htmlspecialchars($tel);

$lat = $_POST['lat'];
$lat=htmlspecialchars($lat);

$lon = $_POST['lon'];
$lon=htmlspecialchars($lon);

$adresa = $_POST['adresa'];
$adresa=htmlspecialchars($adresa);

$link = $_POST['link'];
$link=htmlspecialchars($link);

$obs = $_POST['obs'];
$obs=htmlspecialchars($obs);

$nota = $_POST['nota'];
$nota=htmlspecialchars($nota);

$auth_key=$mac."TaxiIneu";

switch ($opt){
    case 'send':
        if($key!=md5($auth_key))
            {print "Error auth";}
        else{
            $sql = "SELECT * FROM banned_mac where mac='".$mac."'";
            $result = $conn->query($sql);

            if ($result->num_rows > 0) {
                print "Banned id";
            }else{
                $sql = "SELECT * FROM comenzi where mac='".$mac."'";
                $result = $conn->query($sql);
                $status=0;
                if ($result->num_rows > 0) {
                    while($row = $result->fetch_assoc()) {
                        $status=$row["status"];
                      }
                }
                if($status==0){
                    //id 	id_sofer 	id_client 	nume 	tel 	nr_masina 	mac 	lat 	lon 	adresa 	link 	status 	obs 	data_start 	data_client 	data_stop
                    $sql = "INSERT INTO comenzi (lat, lon, nume, tel, mac, adresa, link, obs) VALUES ('".$lat."', '".$lon."', '".$nume."', '".$tel."', '".$mac."', '".$adresa."', '".$link."', '".$obs."') ON DUPLICATE KEY UPDATE "."lat='".$lat."', lon='".$lon."', nume='".$nume."', tel='".$tel."', mac='".$mac."', adresa='".$adresa."', link='".$link."', obs='".$obs."'";
                    //print $sql;

                    if (mysqli_query($conn, $sql)) {
                        echo "Order send successfully";
                    } else {
                        //echo "Error: " . $sql . "<br>" . mysqli_error($conn);
                        echo "Error: ". mysqli_error($conn);
                    }

                }else {print "Order taken";}
            }

        }//if auth key
    break;
    
    
    case 'del':
        if($key!=md5($auth_key))
        {print "Error auth";}
    else{
            $sql = "SELECT * FROM comenzi where mac='".$mac."'";
            $result = $conn->query($sql);
            if ($result->num_rows > 0) {
                
                $sql = "SELECT * FROM comenzi where mac='".$mac."' and status ='0'";
                $result = $conn->query($sql);
                    if ($result->num_rows > 0) {
                        //id 	id_sofer 	id_client 	nume 	tel 	nr_masina 	mac 	lat 	lon 	adresa 	link 	status 	obs 	data_start 	data_client 	data_stop
                        $sql = "DELETE FROM comenzi where mac='".$mac."'";
                        //print $sql;

                        if (mysqli_query($conn, $sql)) {
                            echo "Delete record successfully";
                        } else {
                            //echo "Error: " . $sql . "<br>" . mysqli_error($conn);
                            echo "Error: ". mysqli_error($conn);
                        }

                    }else {print "Order taken";}
            }else {print "Order doesn't exist";}

        }//if auth key
    break;

    case 'rate':
        if($key!=md5($auth_key))
        {print "Error auth";}
    else{

        $sql = "SELECT * FROM `log_comenzi` where `mac`='".$mac."' ORDER BY `data_stop` DESC LIMIT 1";
        $result = $conn->query($sql);
        if ($result->num_rows > 0) {
            while($row = $result->fetch_assoc()) {
                $id=$row["id"];
              }

                        //id 	id_sofer 	id_client 	nume 	tel 	nr_masina 	mac 	lat 	lon 	adresa 	link 	status 	obs 	data_start 	data_client 	data_stop
                        $sql = "UPDATE log_comenzi SET nota='".$nota."' where mac='".$mac."' and id='".$id."'";
                        //print $sql;

                        if (mysqli_query($conn, $sql)) {
                            echo "Rate add successfully";
                        } else {
                            //echo "Error: " . $sql . "<br>" . mysqli_error($conn);
                            echo "Error: ". mysqli_error($conn);
                        }
                  
            }else {print "Order doesn't exist";}

        }//if auth key
    break;

}

$_POST['opt'] = '';
$_POST['key'] = '';
$_POST['mac'] = '';
$_POST['nume'] = '';
$_POST['tel'] = '';
$_POST['lon'] = '';
$_POST['lat'] = '';
$_POST['adresa'] = '';
$_POST['link'] = '';
$_POST['obs'] = '';

$conn->close();

/*
$sql = "SELECT id, firstname, lastname FROM MyGuests";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
  echo "<table><tr><th>ID</th><th>Name</th></tr>";
  // output data of each row
  while($row = $result->fetch_assoc()) {
    echo "<tr><td>".$row["id"]."</td><td>".$row["firstname"]." ".$row["lastname"]."</td></tr>";
  }
  echo "</table>";
} else {
  echo "0 results";
}
$conn->close();

*/

?>