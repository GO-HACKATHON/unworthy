<?php

// LINE/CIRCLE
$lineCircle($x1, $y1, $x2, $y2, $cx, $cy, $r) {

  // is either end INSIDE the circle?
  // if so, return true immediately
  $inside1 = pointCircle($x1,$y1, $cx,$cy,$r);
  $inside2 = pointCircle($x2,$y2, $cx,$cy,$r);
  if ($inside1 || $inside2) return true;

  // get length of the line
  $distX = $x1 - $x2;
  $distY = $y1 - $y2;
  $len = sqrt( ($distX*$distX) + ($distY*$distY) );

  // get dot product of the line and circle
  $dot = ( (($cx-$x1)*($x2-$x1)) + (($cy-$y1)*($y2-$y1)) ) / pow($len,2);

  // find the closest point on the line
  $closestX = $x1 + ($dot * ($x2-$x1));
  $closestY = $y1 + ($dot * ($y2-$y1));

  // is this point actually on the line segment?
  // if so keep going, but if not, return false
  $onSegment = linePoint($x1,$y1,$x2,$y2, $closestX,$closestY);
  if (!$onSegment) return false;

  // get distance to closest point
  $distX = $closestX - $cx;
  $distY = $closestY - $cy;
  $distance = sqrt( ($distX*$distX) + ($distY*$distY) );

  if ($distance <= $r) {
    return true;
  }
  return false;
}


function linePoint($x1, $y1, $x2, $y2, $px, $py) {

  $d1 = dist($px,$py, $x1,$y1);
  $d2 = dist($px,$py, $x2,$y2);

  $lineLen = dist($x1,$y1, $x2,$y2);

  $buffer = 0.001;

  if ($d1+$d2 >= $lineLen-$buffer && $d1+$d2 <= $lineLen+$buffer) {
    return true;
  }
  return false;
}

function pointCircle($px, $py, $cx, $cy, $r) {

  $distX = $px - $cx;
  $distY = $py - $cy;
  $distance = sqrt( ($distX*$distX) + ($distY*$distY) );

  if (distance <= r) {
    return true;
  }
  return false;
}


echo (lineCircle(-6.183247,106.823646, -6.183198, 106.824720, -6.183235, 106.824170,0.000015));
echo (lineCircle(-6.183247,106.823646, -6.183198, 106.824720, -6.183158, -106.825275,0.000015));
echo (lineCircle(-6.183247,106.823646, -6.183198, 106.824720, -6.182539, 106.823121,0.000015));
echo (lineCircle(-6.183247,106.823646, -6.183198, 106.824720, -6.182539, 106.823351,0.000015));
