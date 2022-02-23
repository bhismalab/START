<?php

namespace app\models;

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class parents {
	public static $tbl = 'parents';

	public static $signatures_dir = 'parent_signatures/';

	public static function getParentsByChildId($child_id) { // 2017-05-12
		return CMS::$db->getAll("SELECT p.id, p.child_relationship AS rel, ".CMS::$db->aes('p.surname', 'surname').", ".CMS::$db->aes('p.name', 'name').",
				".CMS::$db->aes('p.patronymic', 'patronymic').", ".CMS::$db->aes('p.birth_date', 'birth_date').", ".CMS::$db->aes('p.spoken_language', 'lang').",
				".CMS::$db->aes('p.gender', 'gender').", ".CMS::$db->aes('p.state', 'state').", ".CMS::$db->aes('p.address', 'address').",
				".CMS::$db->aes('p.phone', 'phone').", ".CMS::$db->aes('p.email', 'email').", ".CMS::$db->aes('p.preferable_contact', 'preferable_contact').",
				p.signature_scan AS signature
			FROM `".self::$tbl."` p
			WHERE p.child_id=:child_id AND p.is_deleted='0'
			ORDER BY p.id ASC",
			[':child_id' => $child_id]
		);
	}

	public static function getParentById($parent_id) { // 2017-05-15
		return CMS::$db->getRow("SELECT p.id, ".CMS::$db->aes('p.surname', 'surname').", ".CMS::$db->aes('p.name', 'name').",
				".CMS::$db->aes('p.patronymic', 'patronymic').", ".CMS::$db->aes('p.birth_date', 'birth_date').",
				".CMS::$db->aes('p.gender', 'gender').", ".CMS::$db->aes('p.state', 'state').", ".CMS::$db->aes('p.address', 'address').",
				".CMS::$db->aes('p.preferable_contact', 'preferable_contact').", p.add_datetime, p.add_by, p.signature_scan AS signature
			FROM `".self::$tbl."` p
			WHERE p.id=:id AND p.is_deleted='0'
			LIMIT 1",
			[':id' => $parent_id]
		);
	}
}

?>