<?php
// Report all PHP errors
error_reporting(E_ALL);
ini_set("display_errors", 1);
include("db_connect.php");
//id 	id_sofer 	id_client 	nume 	tel 	nr_masina 	mac 	lat 	lon 	adresa 	link 	status 	obs obs_sofer	obs_sofer1	data_start 	data_client 	data_stop 
if(!isset($_POST['opt'])) $_POST['opt'] = '';
if(!isset($_POST['key'])) $_POST['key'] = '';
if(!isset($_POST['auth_key'])) $_POST['auth_key'] = '';
if(!isset($_POST['id'])) $_POST['id'] = '';
if(!isset($_POST['order_id'])) $_POST['order_id'] = '';
if(!isset($_POST['parola'])) $_POST['parola'] = '';
if(!isset($_POST['nr_masina'])) $_POST['nr_masina'] = '';
if(!isset($_POST['status'])) $_POST['status'] = '';
if(!isset($_POST['obs_sofer'])) $_POST['obs_sofer'] = '';
if(!isset($_POST['obs_sofer1'])) $_POST['obs_sofer1'] = '';


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

$id = $_POST['id'];
$id=htmlspecialchars($id);

$order_id = $_POST['order_id'];
$order_id=htmlspecialchars($order_id);

$key = $_POST['key'];
$key=htmlspecialchars($key);

$auth_key = $_POST['auth_key'];
$auth_key=htmlspecialchars($auth_key);

$parola = $_POST['parola'];
$parola=htmlspecialchars($parola);

$nr_masina = $_POST['nr_masina'];
$nr_masina=htmlspecialchars($nr_masina);

$status = $_POST['status'];
$status=htmlspecialchars($status);

$obs_sofer = $_POST['obs_sofer'];
$obs_sofer=htmlspecialchars($obs_sofer);

$obs_sofer1 = $_POST['obs_sofer1'];
$obs_sofer1=htmlspecialchars($obs_sofer1);



$key2=$key."TaxiIneu";

switch ($opt){
    case 'login':
        if($auth_key!=md5($key2))
            {print "Error auth";}
        else{
            
                $sql = "SELECT * FROM soferi where activ='1' and id='".$id."'";
                $result = $conn->query($sql);
                $pass2="";
                if ($result->num_rows > 0) {
                    while($row = $result->fetch_assoc()) {
                        $pass2=$row["parola"];
                        
                      }
                    if((md5($pass2))==$parola){
                        $pass_ok=md5("pass_ok".$key);
                        print $pass_ok;
                    }else{
                        $pass_bad=md5("pass_bad".$key);
                        print $pass_bad;
                    }
                }else{
                    $bad_user=md5("bad_user".$key);
                        print $bad_user;
                }
                

        }//if auth key
    break;

    case 'logout':
        if($auth_key!=md5($key2))
            {print "Error auth";}
        else{
            
                $sql = "SELECT * FROM soferi where activ='1' and id='".$id."'";
                $result = $conn->query($sql);
                $pass2="";
                if ($result->num_rows > 0) {
                    while($row = $result->fetch_assoc()) {
                        $pass2=$row["parola"];
                        
                      }
                    if((md5($pass2))==$parola){
                        $sql = "UPDATE soferi SET login='0', nr_masina=NULL where id='".$id."'";
                        //print $sql;

                        if (mysqli_query($conn, $sql)) {
                            $pass_ok=md5("pass_ok".$key);
                            print $pass_ok;
                        } else {
                            //echo "Error: " . $sql . "<br>" . mysqli_error($conn);
                            echo "Error: ". mysqli_error($conn);
                        }
                        //update masini
                        
                    }else{
                        $pass_bad=md5("pass_bad".$key);
                        print $pass_bad;
                    }
                }else{
                    $bad_user=md5("bad_user".$key);
                        print $bad_user;
                }
                

        }//if auth key
    break;
    
    
    case 'getcar':
        if($auth_key!=md5($key2))
        {print "Error auth";}
    else{
        $sql = "SELECT * FROM masini where activ='1'";
                $result = $conn->query($sql);
                $pass2="";
                $products = array();
                if ($result->num_rows > 0) {
                    while($row = $result->fetch_assoc()) {
                        $temp = array();
                    
                        $temp['id'] = $row["id"];
                        $temp['numar'] = $row["numar"];
                        
                        array_push($products,$temp);
                        
                      }
                      echo json_encode($products);
                    
                }else{
                                        
                }

        }//if auth key
    break;

    case 'sendcar':
        if($auth_key!=md5($key2))
        {print "Error auth";}
    else{

        $sql = "SELECT * FROM soferi where activ='1' and id='".$id."'";
        $result = $conn->query($sql);
        $pass2="";
        $nr_masina2="";
        $id2="";
        if ($result->num_rows > 0) {
            while($row = $result->fetch_assoc()) {
                $pass2=$row["parola"];
                $nr_masina2=$row["nr_masina"];
                $id2=$row["id"];
                
              }
            if((md5($pass2))==$parola){
                $sql = "SELECT * FROM soferi where nr_masina='".$nr_masina."' and id<>'".$id."'";
                $result = $conn->query($sql);

                if ($result->num_rows > 0) {
                    $products = array();
                    while($row = $result->fetch_assoc()) {
                        
                        $temp = array();
                
                        $temp['id'] = $row["id"];
                        $temp['nume'] = $row["nume"];
                        $temp['prenume'] = $row["prenume"];
                        $temp['nr_masina'] = $row["nr_masina"];
                                
                        array_push($products,$temp);

                        //print "User care au nr_masina=".$nr_masina."\n id=".$row["id"]." ".$row["nume"]." ".$row["prenume"]."\n";
                    
                    }
                    echo json_encode($products);

                }else{ 

                
                        $sql = "UPDATE soferi SET login='1', nr_masina='".$nr_masina."' where id='".$id."'";
                        //print $sql;

                        if (mysqli_query($conn, $sql)) {
                            $pass_ok=md5("pass_ok".$key);
                            print $pass_ok;
                        } else {
                            //echo "Error: " . $sql . "<br>" . mysqli_error($conn);
                            echo "Error: ". mysqli_error($conn);
                        }
                        //update masini
                    }
                
            }else{
                $pass_bad=md5("pass_bad".$key);
                print $pass_bad;
            }
        }else{
            $bad_user=md5("bad_user".$key);
                print $bad_user;
        }

        }//if auth key
    break;

    case 'autologin':
        if($auth_key!=md5($key2))
        {print "Error auth";}
    else{

        $sql = "SELECT * FROM soferi where activ='1' and id='".$id."'";
        $result = $conn->query($sql);
        $pass2="";
        if ($result->num_rows > 0) {
            $products = array();
            while($row = $result->fetch_assoc()) {
                $pass2=$row["parola"];
                
                $temp = array();
                
                $temp['id'] = $row["id"];
                $temp['nr_masina'] = $row["nr_masina"];
                $temp['login'] = $row["login"];
                
                $pass_ok=md5("pass_ok".$key);
                $temp['pass_ok'] = $pass_ok;

                array_push($products,$temp);
                
              }
            if((md5($pass2))==$parola){
                               
                echo json_encode($products);
                
            }else{
                $pass_bad=md5("pass_bad".$key);
                print $pass_bad;
            }
        }else{
            $bad_user=md5("bad_user".$key);
                print $bad_user;
        }

        }//if auth key
    break;


    case 'getorders':
        if($auth_key!=md5($key2))
        {print "Error auth";}
    else{

        $sql = "SELECT * FROM soferi where activ='1' and id='".$id."'";
        $result = $conn->query($sql);
        $pass2="";
        $nr_masina2="";
        $id2="";
        if ($result->num_rows > 0) {
            while($row = $result->fetch_assoc()) {
                $pass2=$row["parola"];
                $nr_masina2=$row["nr_masina"];
                $id2=$row["id"];
                
              }
            if((md5($pass2))==$parola){
                $sql = "SELECT CURRENT_TIMESTAMP,CURRENT_TIME,id,id_sofer,id_client,nume,tel,nr_masina,mac,lat,lon,adresa,link,status,obs,obs_sofer,obs_sofer1,data_comanda,data_start,data_client,data_stop,banned,del 
                FROM comenzi";
                //$sql = "SELECT * FROM comenzi where status='0'";
                $result = $conn->query($sql);

                if ($result->num_rows > 0) {
                    $products = array();
                    while($row = $result->fetch_assoc()) {
                        
                        $temp = array();
                
                        $temp['id'] = $row["id"];
                        $temp['nume'] = $row["nume"];
                        $temp['tel'] = $row["tel"];
                        $temp['mac'] = $row["mac"];
                        $temp['lat'] = $row["lat"];
                        $temp['lon'] = $row["lon"];
                        $temp['adresa'] = $row["adresa"];
                        $temp['link'] = $row["link"];
                        $temp['status'] = $row["status"];
                        $temp['obs'] = $row["obs"];

                        $difference = date_diff(date_create($row['CURRENT_TIMESTAMP']), date_create($row['data_comanda']));
                        $minutes = $difference->days * 24 * 60;
                        $minutes += $difference->h * 60;
                        $minutes += $difference->i;

                        $temp['CURRENT_TIMESTAMP'] = $row["CURRENT_TIMESTAMP"];
                        $temp['CURRENT_TIME'] = $row["CURRENT_TIME"];
                        $temp['minutes'] = $minutes;
                        
                                
                        array_push($products,$temp);

                        //print "User care au nr_masina=".$nr_masina."\n id=".$row["id"]." ".$row["nume"]." ".$row["prenume"]."\n";
                    
                    }
                    echo json_encode($products);

                }
                
            }else{
                $pass_bad=md5("pass_bad".$key);
                print $pass_bad;
            }
        }else{
            $bad_user=md5("bad_user".$key);
                print $bad_user;
        }

        }//if auth key
    break;

    case 'getorderdetail':
        if($auth_key!=md5($key2))
        {print "Error auth";}
    else{

        $sql = "SELECT * FROM soferi where activ='1' and id='".$id."'";
        $result = $conn->query($sql);
        $pass2="";
        $nr_masina2="";
        $id2="";
        if ($result->num_rows > 0) {
            while($row = $result->fetch_assoc()) {
                $pass2=$row["parola"];
                $nr_masina2=$row["nr_masina"];
                $id2=$row["id"];
                
              }
            if((md5($pass2))==$parola){
                $sql = "SELECT CURRENT_TIMESTAMP,CURRENT_TIME,id,id_sofer,id_client,nume,tel,nr_masina,mac,lat,lon,adresa,link,status,obs,obs_sofer,obs_sofer1,data_comanda,data_start,data_client,data_stop,banned,del 
                FROM comenzi where id=".$order_id;
                //$sql = "SELECT * FROM comenzi where status='0'";
                $result = $conn->query($sql);

                if ($result->num_rows > 0) {
                    $products = array();
                    while($row = $result->fetch_assoc()) {
                        
                        $temp = array();
                
                        $temp['id'] = $row["id"];
                        $temp['nume'] = $row["nume"];
                        $temp['tel'] = $row["tel"];
                        $temp['mac'] = $row["mac"];
                        $temp['lat'] = $row["lat"];
                        $temp['lon'] = $row["lon"];
                        $temp['adresa'] = $row["adresa"];
                        $temp['link'] = $row["link"];
                        $temp['status'] = $row["status"];
                        $temp['obs'] = $row["obs"];

                        $difference = date_diff(date_create($row['CURRENT_TIMESTAMP']), date_create($row['data_comanda']));
                        $minutes = $difference->days * 24 * 60;
                        $minutes += $difference->h * 60;
                        $minutes += $difference->i;

                        $temp['CURRENT_TIMESTAMP'] = $row["CURRENT_TIMESTAMP"];
                        $temp['CURRENT_TIME'] = $row["CURRENT_TIME"];
                        $temp['minutes'] = $minutes;
                        
                                
                        array_push($products,$temp);

                        //print "User care au nr_masina=".$nr_masina."\n id=".$row["id"]." ".$row["nume"]." ".$row["prenume"]."\n";
                    
                    }
                    echo json_encode($products);

                }
                
            }else{
                $pass_bad=md5("pass_bad".$key);
                print $pass_bad;
            }
        }else{
            $bad_user=md5("bad_user".$key);
                print $bad_user;
        }

        }//if auth key
    break;

    case 'start':
        if($auth_key!=md5($key2))
            {print "Error auth";}
        else{
            
                $sql = "SELECT * FROM soferi where activ='1' and id='".$id."'";
                $result = $conn->query($sql);
                $pass2="";
                if ($result->num_rows > 0) {
                    while($row = $result->fetch_assoc()) {
                        $pass2=$row["parola"];
                        
                      }
                    if((md5($pass2))==$parola){
                        $sql = "UPDATE comenzi SET status='1', id_sofer='".$id."', nr_masina='".$nr_masina."', obs_sofer='".$obs_sofer."' where id='".$order_id."'";
                        //print $sql;

                        if (mysqli_query($conn, $sql)) {
                            $pass_ok=md5("pass_ok".$key);
                            print $pass_ok;
                        } else {
                            //echo "Error: " . $sql . "<br>" . mysqli_error($conn);
                            echo "Error: ". mysqli_error($conn);
                        }
                        //update masini
                        
                    }else{
                        $pass_bad=md5("pass_bad".$key);
                        print $pass_bad;
                    }
                }else{
                    $bad_user=md5("bad_user".$key);
                        print $bad_user;
                }
                

        }//if auth key
    break;

    case 'client':
        if($auth_key!=md5($key2))
            {print "Error auth";}
        else{
            
                $sql = "SELECT * FROM soferi where activ='1' and id='".$id."'";
                $result = $conn->query($sql);
                $pass2="";
                if ($result->num_rows > 0) {
                    while($row = $result->fetch_assoc()) {
                        $pass2=$row["parola"];
                        
                      }
                    if((md5($pass2))==$parola){
                        $sql = "UPDATE comenzi SET status='2', obs_sofer1='".$obs_sofer1."' where id='".$order_id."'";
                        //print $sql;

                        if (mysqli_query($conn, $sql)) {
                            $pass_ok=md5("pass_ok".$key);
                            print $pass_ok;
                        } else {
                            //echo "Error: " . $sql . "<br>" . mysqli_error($conn);
                            echo "Error: ". mysqli_error($conn);
                        }
                        //update masini
                        
                    }else{
                        $pass_bad=md5("pass_bad".$key);
                        print $pass_bad;
                    }
                }else{
                    $bad_user=md5("bad_user".$key);
                        print $bad_user;
                }
                

        }//if auth key
    break;
    case 'stop':
        if($auth_key!=md5($key2))
            {print "Error auth";}
        else{
            
                $sql = "SELECT * FROM soferi where activ='1' and id='".$id."'";
                $result = $conn->query($sql);
                $pass2="";
                if ($result->num_rows > 0) {
                    while($row = $result->fetch_assoc()) {
                        $pass2=$row["parola"];
                        
                      }
                    if((md5($pass2))==$parola){
                        
                        $sql = "SELECT * FROM comenzi where status>='2' and id='".$order_id."'";
                        $result = $conn->query($sql);
                        $pass2="";
                        if ($result->num_rows > 0) {
                        
                                    $sql = "DELETE from comenzi where id='".$order_id."' and status>='2'";
                                    //print $sql;

                                    if (mysqli_query($conn, $sql)) {
                                        $pass_ok=md5("pass_ok".$key);
                                        print $pass_ok;
                                    } else {
                                        //echo "Error: " . $sql . "<br>" . mysqli_error($conn);
                                        echo "Error: ". mysqli_error($conn);
                                    }
                                    //update masini
                                }else{$buff123=md5("comanda_in_derulare".$key);print $buff123;}
                        
                    }else{//md5
                        $pass_bad=md5("pass_bad".$key);
                        print $pass_bad;
                    }
                }else{
                    $bad_user=md5("bad_user".$key);
                        print $bad_user;
                }
                

        }//if auth key
    break;

}

$_POST['opt'] = '';
$_POST['key'] = '';
$_POST['id'] = '';
$_POST['order_id'] = '';
$_POST['parola'] = '';
$_POST['nr_masina'] = '';
$_POST['status'] = '';
$_POST['obs_sofer'] = '';
$_POST['obs_sofer1'] = '';

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