<?php

namespace app\controllers;

use app\helpers\app;
use app\models\cms_users;
use app\models\log;
use tb\start_cms\CMS;
use tb\start_cms\base\controller;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class consent_form_controller extends controller {

	private static $runtime = [];

	public static function action_edit() { // 2017-10-16
		if ($_SERVER['REQUEST_METHOD'] === 'POST') {
			$allowedTags = '<p><a><img><em><strong><s><br>';

			file_put_contents(CONSENT_FORM_FILE_ENGLISH, strip_tags(
				$_POST['consent_text_english'], $allowedTags
			));

			file_put_contents(CONSENT_FORM_FILE_HINDI, strip_tags(
				$_POST['consent_text_hindi'], $allowedTags
			));
		}

		utils::redirect(
			'?controller=exercises&action=list'
		);
	}
}

?>