<?php

/* @var $db DB */

if (isset($_POST['state_id'])) {
    $stateId = $db->escape($_POST['state_id']);
    $response['data'] = $db->arrayQuery("SELECT `id`,`name` FROM `cities` WHERE (`state_id`={$stateId}) ORDER BY `center` DESC, `name`;");
    foreach ($response['data'] as $key => $value) {
        $response['data'][$key]->id = intval($value->id);
    }
} else {
    $response['error'] = 'استان مشخص نشده است';
}