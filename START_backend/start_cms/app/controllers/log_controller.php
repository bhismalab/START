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

class log_controller extends controller {
	/*
		This contoller contains actions to display and export CMS user's events log.
	*/

	private static $runtime = [];

	public static function action_list() { // 2017-07-14
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_log_list');

		$params = [];

		$page = intval(@$_GET['page']);
		cms_users::$curr_pg = (empty($page)? 1: $page);

		$params['link_sc'] = utils::trueLink(['controller', 'action', 'q']);
		$params['link_return'] = urlencode(SITE.CMS_DIR.utils::trueLink(['controller', 'action', 'q', 'page']));

		$params['log'] = log::getLog();
		$params['users'] = log::getUsersAreInLogs();
		//echo "<pre>"; var_dump($params['filter']); die(":a");
        //var_dump($_GET['filter']); die();

		$params['canWrite'] = CMS::hasAccessTo('log/list', 'write');

		return self::render('log_list', $params);
	}

	public static function action_download_log() { // 2017-09-04
		$log = log::getLog(['limit' => 'no']);
		log::exportLog($log);
	}
}

?>