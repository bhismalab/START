<?php

namespace app\controllers;

use app\helpers\app;
use tb\start_cms\base\widget;
use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class breadcrumb_widget_controller extends widget {
	/* See tb\start_cms\helpers\view class for function widget($widget_name, $options=[]) information */

	public static function run() {
		return self::render('breadcrumbs', [
			'links' => self::$options['links']
		]);
	}
}

?>