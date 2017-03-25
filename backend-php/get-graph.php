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
