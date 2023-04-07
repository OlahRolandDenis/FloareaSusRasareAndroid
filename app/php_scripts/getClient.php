<?php
// Report all PHP errors
error_reporting(E_ALL);
ini_set("display_errors", 1);
include("db_connect.php");

if(!isset($_GET['opt'])) $_GET['opt'] = '';
if(!isset($_GET['key'])) $_GET['key'] = '';
if(!isset($_GET['mac'])) $_GET['mac'] = '';

/*
$_POST['key'] = '3d449c6640760504dde5ba63f9e719bf';ec36008f7ec61284151bc7ce461b6b2a
$_POST['mac'] = '9494093d-0f75-4fe4-9f12-30f764f2105a';
*/

$opt = $_GET['opt'];
$opt=htmlspecialchars($opt);

$mac = $_GET['mac'];
$mac=htmlspecialchars($mac);

$key = $_GET['key'];
$key=htmlspecialchars($key);
//print "key=".$key;
//print "<br>";
//print "mac=".$mac;

$auth_key=$mac."taxiineu";
if($key!=md5($auth_key))
    {print "Error auth";}
    else{
        switch ($opt){
            case 'tot':
                        //id	latitudine	longitudine	nume	tel	data	link
                

                $stmt ->execute();
                //$stmt -> bind_result($id, $latitudine, $longitudine, $nume, $tel, $data, $link);
                $stmt -> bind_result($id, $id_sofer, $id_client, $nume, $tel, $nr_masina, $mac, $lat, $lon, $adresa, $link, $status, $obs, $obs_sofer, $obs_sofer1, $data_comanda, $data_start, $data_client, $data_stop, $banned, $del);
                //id 	id_sofer 	id_client 	nume 	tel 	nr_masina 	mac 	lat 	lon 	adresa 	link 	status 	obs data_comanda    data_start 	data_client 	data_stop   banned  
                $products = array();

                while($stmt ->fetch()){

                    $temp = array();
                    
                    $temp['id'] = $id;
                    $temp['id_sofer'] = $id_sofer;
                    $temp['id_client'] = $id_client;
                    $temp['nume'] = $nume;
                    $temp['tel'] = $tel;
                    $temp['nr_masina'] = $nr_masina;
                    $temp['mac'] = $mac;
                    $temp['lat'] = $lat;
                    $temp['lon'] = $lon;
                    $temp['adresa'] = $adresa;
                    $temp['link'] = $link;
                    $temp['status'] = $status;
                    $temp['obs'] = $obs;
                    $temp['obs_sofer'] = $obs_sofer;
                    $temp['obs_sofer1'] = $obs_sofer1;
                    $temp['data_comanda'] = $data_comanda;
                    $temp['data_start'] = $data_start;
                    $temp['data_client'] = $data_client;
                    $temp['data_stop'] = $data_stop;
                    $temp['banned'] = $banned;
                    $temp['del'] = $del;

                    array_push($products,$temp);
                    }

                    echo json_encode($products);
                
            break;
            
            case 'rate':
               
                $sql = "SELECT `data_stop`, `nota` FROM `log_comenzi` where `mac`='".$mac."' ORDER BY `data_stop` DESC LIMIT 1";
                $stmt = $conn->prepare($sql);
                $stmt ->execute();
                //$stmt -> bind_result($id, $latitudine, $longitudine, $nume, $tel, $data, $link);
                $stmt -> bind_result($data_stop, $nota);
                 
                $products = array();

                while($stmt ->fetch()){

                    $temp = array();
                    
                    $temp['data_stop'] = $data_stop;
                    $temp['nota'] = $nota;
                    
                    array_push($products,$temp);
                    }

                    echo json_encode($products);

            break;
            }
        
        }//Error auth
    $conn->close();

$_POST['opt'] = '';
$_POST['key'] = '';
$_POST['mac'] = '';
?>