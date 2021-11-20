<?php

/* @var $db DB */

if (isset($_POST['city_id'], $_POST['type_id'])) {
    $cityID = $db->escape($_POST['city_id']);
    $typeID = $db->escape($_POST['type_id']);
    $response['data'] = $db->arrayQuery("SELECT `id`,`name`,`description`,`lat`,`lng` FROM `points` WHERE (`city_id`={$cityID} AND `type_id`={$typeID}) ORDER BY `name`;");
    foreach ($response['data'] as $key => $value) {
        $response['data'][$key]->id = intval($value->id);
        $response['data'][$key]->lat = doubleval($value->lat);
        $response['data'][$key]->lng = doubleval($value->lng);
    }
} else {
    $response['error'] = 'شهر و نوع نقاط مشخص نشده است';
}