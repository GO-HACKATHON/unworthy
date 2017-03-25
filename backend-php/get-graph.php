<?php
/**
 * Created by PhpStorm.
 * User: Andi Muqsith Ashari
 * Date: 3/25/2017
 * Time: 11:32 AM
 */

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