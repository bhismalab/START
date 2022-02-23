<?php

namespace app\controllers;

use app\helpers\app;
use app\models\social_workers;
use tb\start_cms\CMS;
use tb\start_cms\base\controller;
use tb\start_cms\helpers\security;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class social_workers_controller extends controller {
	/*
		This contoller contains actions to manage social workers (mobile application users).
	*/

	private static $runtime = [];

	public static function action_list() { // 2017-04-14
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_block_social_workers');

		$params = [];

		$page = intval(@$_GET['page']);
		social_workers::$curr_pg = (empty($page)? 1: $page);

		$params['users'] = social_workers::getUsersList();
		$params['count'] = social_workers::$items_amount;
		$params['total'] = social_workers::$pages_amount;
		$params['current'] = social_workers::$curr_pg;

		$params['link_sc'] = utils::trueLink(['controller', 'action', 'q']);
		$params['link_return'] = urlencode(SITE.CMS_DIR.utils::trueLink(['controller', 'action', 'q', 'page']));

		$params['canWrite'] = CMS::hasAccessTo('social_workers/list', 'write');

		$params['uploadUrl'] = SITE.utils::dirCanonicalPath(CMS_DIR.UPLOADS_DIR);
		$params['avatarDirUrl'] = $params['uploadUrl'].social_workers::AVATAR_UPL_DIR;

		return self::render('social_workers_list', $params);
	}

	public static function action_add() { // 2017-04-19
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_social_workers_add');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('social_workers/add', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=social_workers&action=list': $_GET['return']);

		if (isset($_POST['add'])) {
			$params['op'] = social_workers::addWorker();
			if ($params['op']['success']) {
				utils::delayedRedirect($params['link_back']);
			}
		}

		$params['colors'] = social_workers::$colors;

		return self::render('social_workers_add', $params);
	}

	public static function action_edit() { // 2017-04-19
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_social_workers_edit');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('social_workers/edit', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=social_workers&action=list': $_GET['return']);

		$id = @(int)$_GET['id'];
		$params['user'] = social_workers::getWorker($id);

		if (empty($params['user']['id'])) {
			return CMS::resolve('base/404');
		}

		if (isset($_POST['save']) || isset($_POST['apply'])) {
			$params['op'] = social_workers::editWorker($id);
			if ($params['op']['success']) {
				$params['user'] = social_workers::getWorker($id);

				if (isset($_POST['save'])) {
					utils::delayedRedirect($params['link_back']);
				}
			}
		}

		$params['colors'] = social_workers::$colors;
		//$params['is_valid_password'] = security::validatePassword($params['user']['password'], @(string)$_GET['pwd'], CMS::$salt);

		return self::render('social_workers_edit', $params);
	}

	public static function action_delete() { // 2017-04-19
		self::$layout = 'common_layout';
		view::$title = CMS::t('delete');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('social_workers/delete', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=social_workers&action=list': $_GET['return']);

		$deleted = false;
		if ($params['canWrite']) {
			$deleted = social_workers::deleteWorker(@$_POST['delete']);
		}
		$params['op']['success'] = $deleted;
		$params['op']['message'] = 'del_'.($deleted? 'suc': 'err');

		return self::render('social_workers_delete', $params);
	}

	public static function action_ajax_set_ban() { // 2017-04-19
		header('Content-type: application/json; charset=utf-8');

		$response = ['success' => false, 'message' => 'ajax_invalid_request'];

		if (!empty($_SERVER['HTTP_X_REQUESTED_WITH']) && strtolower($_SERVER['HTTP_X_REQUESTED_WITH'])=='xmlhttprequest') {
			$id = @(int)$_POST['user_id'];
			$status = @(string)$_POST['turn'];
			$updated = social_workers::setWorkerStatus($id, $status);
			if ($updated) {
				$response['success'] = true;
				$response['message'] = 'update_suc';
				$response['data']['action'] = $status;
			}
		}

		return json_encode($response);
	}
}

?>