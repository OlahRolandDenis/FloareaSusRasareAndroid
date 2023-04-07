<?php
error_reporting(E_ALL);
ini_set("display_errors", 1);

//id 	id_sofer 	id_client
$url2 = "Dropped pin
https://maps.app.goo.gl/sEEJWtWK4GJ6eYs37";
$url2 = "Piata Ineu
https://maps.app.goo.gl/k59N237ijJdVxQe77";

//$url2 = "https://maps.app.goo.gl/ub7JjtmWAeuBXNWDA";

//$url2="https://maps.app.goo.gl/ub7JjtmWAeuBXNWDA";

//$url3=explode("http",$url2);
$url3=strstr($url2,"https://");
print "url3=".$url3;
$url3="https://www.google.com/maps/place/Piata+Ineu,+Strada+Rahovei+30,+Ineu+315300/data=!4m2!3m1!1s0x4745f7f9545de1c9:0x679313c0a9062165?utm_source=mstt_1&entry=gps&g_ep=CAESCTExLjQzLjUwMxgAIP___________wEqAA%3D%3D&ucbcb=1";

$headers = get_headers($url3, 1);
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

    //$ll=explode(,,substr(strstr(strstr($url,?ll=),&,true),4));
    //$long=$ll[0];
    //$lat=$ll[1];
    //print $long." ".$lat;
?>