<?php
/**
 * Created by PhpStorm.
 * User: Andi Muqsith Ashari
 * Date: 3/25/2017
 * Time: 11:32 AM
 */

include "config.php";

$parentId = $mysqli->real_escape_string($_GET['parent_id']);

$parent = $mysqli->query("SELECT * FROM map_point WHERE id='" . $parentId . "' LIMIT 1");
$parent = $parent->fetch_assoc();
$parent['relation'] = visitChild($mysqli, $parent);

header('Content-Type: application/json');
echo json_encode($parent);

function visitChild($mysqli, $parent, $visitedPoint = [])
{
    $childList = $mysqli->query("SELECT mp2.*,mv.type as type FROM map_vector mv JOIN map_point mp1 ON mp1.id=mv.point_1 JOIN map_point mp2 ON mp2.id=mv.point_2 WHERE mp1.id = '" . $parent['id'] . "'");
    $relationList = [];
    while ($child = $childList->fetch_assoc()) {
        $key = $parent['id'] . "-" . $child['id'];
        if (!@$visitedPoint[$key]) {
            $visitedPoint[$key] = true;
            $child['relation'] = visitChild($mysqli, $child, $visitedPoint);
            $relationList[] = $child;
        }
    }
    return $relationList;
}