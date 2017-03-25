<?php

$configData = file_get_contents('config.json');
$configData = json_decode($configData);

$config['database-host'] = $configData->database_host;
$config['database-user'] = $configData->database_user;
$config['database-pass'] = $configData->database_pass;
$config['database-name'] = $configData->database_name;

$mysqli = new mysqli($config['database-host'], $config['database-user'], $config['database-pass'], $config['database-name']);
