<?php

/* @var $db DB */

$response['data'] = $db->arrayQuery("SELECT `id`,`name` FROM `states` ORDER BY `name`;");
foreach ($response['data'] as $key => $value) {
    $response['data'][$key]->id = intval($value->id);
}
