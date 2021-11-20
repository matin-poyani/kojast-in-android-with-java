<?php

/* @var $db DB */

$response['data'] = $db->arrayQuery("SELECT * FROM `types` ORDER BY `title`;");
foreach ($response['data'] as $key => $value) {
    $response['data'][$key]->id = intval($value->id);
}
