<?php
/**
 * Created by PhpStorm.
 * User: Andi Muqsith Ashari
 * Date: 3/25/2017
 * Time: 6:29 PM
 */

include "config.php";
include "helpers/lineCircle.php";

$eventList = file_get_contents("https://mars.aashari.id/api/get-events.json");
$eventList = json_decode($eventList);

$driverPassList = [];
$driverPassData = $mysqli->query("SELECT * FROM tracker ORDER BY location_x");
while ($driverPass = $driverPassData->fetch_assoc()) {
    $driverPassList[] = $driverPass;
}

$response = [];

$graphList = $mysqli->query("SELECT * FROM map_point ORDER BY location_x");
while ($graph = $graphList->fetch_assoc()) {
    $graph['relation'] = [];
    $relationList = $mysqli->query("SELECT mp2.*,mv.type as type FROM map_vector mv JOIN map_point mp1 ON mp1.id=mv.point_1 JOIN map_point mp2 ON mp2.id=mv.point_2 WHERE mp1.id = '" . $graph['id'] . "'");
    while ($relation = $relationList->fetch_assoc()) {
        $pathEvent = [];
        $passList = [];
        foreach ($driverPassList as $dp) {
            if (lineCircle($graph['location_x'], $graph['location_y'], $relation['location_x'], $relation['location_y'], $dp['location_x'], $dp['location_y'])) {
                $passList[] = $dp;
            }
        }
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
        $relation['distance'] = distance($graph['location_x'], $graph['location_y'], $relation['location_x'], $relation['location_y']);
        $relation['events'] = $pathEvent;
        $relation['pass_list'] = $passList;
        $graph['relation'][] = $relation;
    }
    $response[] = $graph;
}

echo json_encode($response);