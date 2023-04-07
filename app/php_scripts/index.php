<?php
error_reporting(E_ALL);
ini_set("display_errors", 1);
include("db_connect.php");
//print 'android';
//$fetch = curl_request("https://www.google.com/maps?ftid=$ftid");
  //  preg_match("/\"$ftid\",.*?(\d+\.\d+),(\d+\.\d+)/", $fetch, $geo);
   // print $geo[1]."  ".$geo[2];

//$address = Space+Needle;
$url = "https://maps.app.goo.gl/ub7JjtmWAeuBXNWDA";
//$url="https://goo.gl/maps/yxcx1dGBfTvf2pDh7";
//$url="https://maps.app.goo.gl/sEEJWtWK4GJ6eYs37";

//$url = "https://www.google.com/maps/place/46.422447,21.839384/data=!4m6!3m5!1s0!7e2!8m2!3d46.422447!4d21.839384?utm_source=mstt_1&entry=gps&g_ep=CAESCTExLjQzLjUwMxgAIP___________wEqAA%3D%3D";
//$url="https://www.google.com";
/*
$fetch = curl_exec($url);
print "AA".$fetch;
$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
$response = curl_exec($ch);
curl_close($ch);
$result = json_decode($response);
print $response;
$ltlg = array($result->results[0]->geometry->location->lat, $result->results[0]->geometry->location->lng);
print "coord:".$ltlg['lat']."  ".$ltlg[1];


$ch = curl_init();
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, false);
    curl_setopt($ch, CURLOPT_HEADER, 0);
    curl_setopt($ch, CURLOPT_URL,$url);
    $result=curl_exec($ch);
    curl_close($ch);
    print $result;
*/
/*
$url2="";
$headers = get_headers($url, 1);
	$url = $headers['Location'];
    //print "url0=".$url[2];
	if (is_array($url)) {
		foreach ($url as $url) {
			//echo "url2=".$url . "\n";
            $url2=$url;
		}
	} else {
		//echo "url1=".$url;
	    $url2=$url;
    }

    print $url;
    */

    $sql = "SELECT CURRENT_TIMESTAMP,CURRENT_TIME,id,id_sofer,id_client,nume,tel,nr_masina,mac,lat,lon,adresa,link,status,obs,obs_sofer,obs_sofer1,data_comanda,data_start,data_client,data_stop,banned,del 
    FROM comenzi";
    //$sql = "SELECT * FROM comenzi where status='0'";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        $products = array();
        while($row = $result->fetch_assoc()) {
            
            $temp = array();
            /*
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
            */
            print $row["id"]."   ".$row['CURRENT_TIMESTAMP']."  ".$row['data_comanda']." ";
            $difference = date_diff(date_create($row['CURRENT_TIMESTAMP']), date_create($row['data_comanda']));
            $minutes = $difference->days * 24 * 60;
            $minutes += $difference->h * 60;
            $minutes += $difference->i;
            
            print $minutes;
            print "<br>";
                    
            array_push($products,$temp);

            //print "User care au nr_masina=".$nr_masina."\n id=".$row["id"]." ".$row["nume"]." ".$row["prenume"]."\n";
        
        }
        //echo json_encode($products);
    }

?>
