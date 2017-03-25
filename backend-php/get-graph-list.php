<?php
/**
 * Created by PhpStorm.
 * User: Andi Muqsith Ashari
 * Date: 3/25/2017
 * Time: 4:09 PM
 */


include "config.php";
include "helpers/lineCircle.php";

$eventList = file_get_contents("https://mars.aashari.id/api/get-events.php");
$eventList = json_decode($eventList);

$response = [];

$graphList = $mysqli->query("SELECT * FROM map_point ORDER BY location_x");
while ($graph = $graphList->fetch_assoc()) {
    $graph['relation'] = [];
    $relationList = $mysqli->query("SELECT mp2.*,mv.type as type FROM map_vector mv JOIN map_point mp1 ON mp1.id=mv.point_1 JOIN map_point mp2 ON mp2.id=mv.point_2 WHERE mp1.id = '" . $graph['id'] . "'");
    while ($relation = $relationList->fetch_assoc()) {
        $pathEvent = [];
        foreach ($eventList as $event) {
            $el = $event->location;
            if (is_array($el)) {
                foreach ($el as $e) {
                    if (lineCircle($graph['location_x'], $graph['location_y'], $relation['location_x'], $relation['location_y'], $e->x, $e->y)) {
                        $event->distance = distance($graph['location_x'], $graph['location_y'], $e->x, $e->y);
                        $pathEvent[] = $event;
                        break;
                    }
                }
            } else {
                if (lineCircle($graph['location_x'], $graph['location_y'], $relation['location_x'], $relation['location_y'], $el->x, $el->y)) {
                    $event->distance = distance($graph['location_x'], $graph['location_y'], $el->x, $el->y);
                    $pathEvent[] = $event;
                }
            }
        }
        $relation['events'] = $pathEvent;
        $graph['relation'][] = $relation;
    }
    $response[] = $graph;
}

header('Content-Type: application/json');
echo json_encode($response);

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