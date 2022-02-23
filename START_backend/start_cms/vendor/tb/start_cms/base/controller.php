<?php

namespace tb\start_cms\base;

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class controller {
	public static $layout = '';

	public static function render($action_view, $data=[]) {
		$tpl = view::render(TPL_DIR.$action_view.'.php', $data);
		if (!empty(self::$layout)) {
			$tpl = view::render(VIEW_DIR.self::$layout.'.php', [
				'content' => $tpl
			]);
		}
		return $tpl;
	}
}

?>