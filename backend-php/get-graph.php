<?php
/**
 * Created by PhpStorm.
 * User: Andi Muqsith Ashari
 * Date: 3/25/2017
 * Time: 11:32 AM
 */
/*
include "config.php";
include "helpers/lineCircle.php";

$parentId = $mysqli->real_escape_string($_GET['parent_id']);

$parent = $mysqli->query("SELECT * FROM map_point WHERE id='" . $parentId . "' LIMIT 1");
$parent = $parent->fetch_assoc();

$eventList = file_get_contents("https://mars.aashari.id/api/get-events.php?location_x=" . $parent['location_x'] . "&location_y=" . $parent['location_y']);
$eventList = json_decode($eventList);

$parent['relation'] = visitChild($mysqli, $parent, $eventList);

header('Content-Type: application/json');
echo json_encode($parent);

function visitChild($mysqli, $parent, $eventList, $visitedPoint = [])
{
    $childList = $mysqli->query("SELECT mp2.*,mv.type as type FROM map_vector mv JOIN map_point mp1 ON mp1.id=mv.point_1 JOIN map_point mp2 ON mp2.id=mv.point_2 WHERE mp1.id = '" . $parent['id'] . "'");
    $relationList = [];
    while ($child = $childList->fetch_assoc()) {
        $key = $parent['id'] . "-" . $child['id'];
        if (!@$visitedPoint[$key]) {
            $pathEvent = [];
            foreach ($eventList as $event) {
                $el = $event->location;
                if (is_array($el)) {
                    foreach ($el as $e) {
                        if (lineCircle($parent['location_x'], $parent['location_y'], $child['location_x'], $child['location_y'], $e->x, $e->y)) {
                            $event->distance = distance($parent['location_x'], $parent['location_y'], $e->x, $e->y);
                            $pathEvent[] = $event;
                            break;
                        }
                    }
                } else {
                    if (lineCircle($parent['location_x'], $parent['location_y'], $child['location_x'], $child['location_y'], $el->x, $el->y)) {
                        $event->distance = distance($parent['location_x'], $parent['location_y'], $el->x, $el->y);
                        $pathEvent[] = $event;
                    }
                }
            }
            $visitedPoint[$key] = true;
            $child['events'] = $pathEvent;
            $child['relations'] = visitChild($mysqli, $child, $eventList, $visitedPoint);
            $relationList[] = $child;
        }
    }
    return $relationList;
}

<<<<<<< HEAD

function isCollide($lx1, $ly1, $lx2, $ly2, $x0, $y0){
      $A1 = $ly2 - $ly1;
      $B1 = $lx1 - $lx2;
      $C1 = ($ly2 - $ly1)*$lx1 + ($lx1 - $lx2)*$ly1;
      $C2 = -$B1*$x0 + $A1*$y0;
      $det = $A1*$A1 - -$B1*$B1;
      $cx = 0;
      $cy = 0;
      if($det != 0){
            $cx = (($A1*$C1 - $B1*$C2)/$det);
            $cy = (($A1*$C2 - -$B1*$C1)/$det);
      }else{
            $cx = $x0;
            $cy = $y0;
      }

      $distance = sqrt(($cx - $x0) * ($cx - $x0) + ($cy - $y0) * ($cy - $y0));
      if($distance < 0.000015){
        return "true";
      }else {
        return "false";
      }
}

echo (isCollide(-6.183247,106.823646, -6.183198, 106.824720, -6.183235, 106.824170));
echo (isCollide(-6.183247,106.823646, -6.183198, 106.824720, -6.183158, -106.825275));
echo (isCollide(-6.183247,106.823646, -6.183198, 106.824720, -6.182539, 106.823121));
echo (isCollide(-6.183247,106.823646, -6.183198, 106.824720, -6.182539, 106.823351));
*/

$a=-6.180698;
$b=106.824451;
$c=-6.18081;
$d=106.823367;

$m=($d-$b)/($c-$a);
echo $m."\n";

$x=-6.181141;
$y=106.823034;
//echo $y-($m*$x)-$b+($m*$a)."\n";
$distance=abs($y-($m*$x)-$b+($m*$a))/sqrt(1+($m*$m));
echo "Distance : ".$distance;
=======
function distance($lat1, $lon1, $lat2, $lon2, $unit = "K")
{
    $theta = $lon1 - $lon2;
    $dist = sin(deg2rad($lat1)) * sin(deg2rad($lat2)) + cos(deg2rad($lat1)) * cos(deg2rad($lat2)) * cos(deg2rad($theta));
    $dist = acos($dist);
    $dist = rad2deg($dist);
    $miles = $dist * 60 * 1.1515;
    $unit = strtoupper($unit);
    if ($unit == "K") {
        return ($miles * 1.609344);
    } else if ($unit == "N") {
        return ($miles * 0.8684);
    } else {
        return $miles;
    }
}
>>>>>>> 3f8ff461831fd7f634053727104b523c1fe59b2c
