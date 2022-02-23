<?php

namespace tb\start_cms\base;

use tb\start_cms\base\controller;
use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class widget extends controller {
	public static $options;

	public static function init() {}

	public static function run() {
		return '';
	}

	public static function render($widget_view, $data=[]) {
		$tpl = view::render(WIDGET_DIR.$widget_view.'.php', $data);
		return $tpl;
	}
}

?>