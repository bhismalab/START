<?php

namespace app\controllers;

use app\helpers\app;
use app\models\cms_users;
use tb\start_cms\CMS;
use tb\start_cms\base\controller;
use tb\start_cms\helpers\security;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class cms_users_controller extends controller {
	/*
		This contoller contains actions to manage CMS users.
	*/

	private static $runtime = [];

	public static function action_list() { // 2016-09-12
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_cms_users_list');

		$params = [];

		$page = intval(@$_GET['page']);
		cms_users::$curr_pg = (empty($page)? 1: $page);

		$params['users'] = cms_users::getUsersList();
		$params['count'] = cms_users::$items_amount;
		$params['total'] = cms_users::$pages_amount;
		$params['current'] = cms_users::$curr_pg;

		$params['link_sc'] = utils::trueLink(['controller', 'action', 'q']);
		$params['link_return'] = urlencode(SITE.CMS_DIR.utils::trueLink(['controller', 'action', 'q', 'page']));

		$params['canWrite'] = CMS::hasAccessTo('cms_users/list', 'write');

		$params['uploadUrl'] = SITE.utils::dirCanonicalPath(CMS_DIR.UPLOADS_DIR);
		$params['avatarDirUrl'] = $params['uploadUrl'].cms_users::AVATAR_UPL_DIR;

		return self::render('cms_users_list', $params);
	}

	public static function action_add() { // 2016-09-13
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_cms_users_add');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('cms_users/add', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=cms_users&action=list': $_GET['return']);

		if (isset($_POST['add'])) {
			$params['op'] = cms_users::addUserValidate();
			if ($params['op']['success']) {
				self::$layout = 'simple_layout';
				$params['approvement_key'] = 'approvable_new_user';
				view::$title = CMS::t('password_approve_title');

				return self::render('lockscreen', $params);
			}
		}

		if (isset($_POST['approvement_key']) && ($_POST['approvement_key']=='approvable_new_user')) {
			/*
				This section verifyes user credential entered at the lockscreen to enshure that new user was added by authorized user,
				not anonimus who taked control over computer with active session.
			*/

			$user = cms_users::getUserByLogin($_SESSION[CMS::$sess_hash]['ses_adm_login']);
			if (empty($user['id'])) {
				CMS::logout();
				return '';
			}
			if (!security::validatePassword($user['password'], @$_POST['password'], CMS::$salt)) {
				CMS::logout();
				return '';
			}
			$params['op'] = cms_users::addUserApprove();
			utils::delayedRedirect($params['link_back']);
		}

		$params['langs'] = CMS::getCMSLangsRegistered();

		return self::render('cms_user_add', $params);
	}

	public static function action_delete() { // 2016-10-18
		self::$layout = 'common_layout';
		view::$title = CMS::t('delete');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('cms_users/delete', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=cms_users&action=list': $_GET['return']);

		$deleted = false;
		if ($params['canWrite']) {
			$deleted = cms_users::deleteUser(@$_POST['delete']);
		}
		$params['op']['success'] = $deleted;
		$params['op']['message'] = 'del_'.($deleted? 'suc': 'err');

		return self::render('cms_user_delete', $params);
	}

	public static function action_edit() { // 2016-11-15
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_cms_users_edit');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('cms_users/edit', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=cms_users&action=list': $_GET['return']);

		$id = @(int)$_GET['id'];
		$params['user'] = cms_users::getUser($id);

		if (empty($params['user']['id'])) {
			return CMS::resolve('base/404');
		}

		if (isset($_POST['save']) || isset($_POST['apply'])) {
			$params['op'] = cms_users::validateUserData($id);
			if ($params['op']['success']) {
				if (empty($params['op']['data']['is_approve_required'])) {
					$params['op'] = cms_users::saveUserData($id, $params['op']['data']['validated']);
					$params['user'] = cms_users::getUser($id);

					if ($params['op']['success'] && isset($_POST['save'])) {
						utils::delayedRedirect($params['link_back']);
					}
				} else {
					$_SESSION['approvable_user_modifications'] = $params['op']['data']['validated'];
					self::$layout = 'simple_layout';
					$params['approvement_key'] = 'approvable_user_modifications';
					view::$title = CMS::t('password_approve_title');

					return self::render('lockscreen', $params);
				}
			}
		}

		if (isset($_POST['approvement_key']) && ($_POST['approvement_key']=='approvable_user_modifications')) {
			/*
				This section verifyes user credential entered at the lockscreen to enshure that editable user was edited by authorized user,
				not anonimus who taked control over computer with active session.
			*/

			$user = cms_users::getUserByLogin($_SESSION[CMS::$sess_hash]['ses_adm_login']);
			if (empty($user['id'])) {
				CMS::logout();
				return '';
			}
			if (!security::validatePassword($user['password'], @$_POST['password'], CMS::$salt)) {
				CMS::logout();
				return '';
			}

			$params['op'] = cms_users::saveUserData($id, @$_SESSION['approvable_user_modifications']);
			$params['user'] = cms_users::getUser($id);

			if ($params['op']['success']) {
				unset($_SESSION['approvable_user_modifications']);

				if (isset($_POST['save'])) {
					utils::delayedRedirect($params['link_back']);
				}
			}
		}

		$params['langs'] = CMS::getCMSLangsRegistered();
		$params['uploadUrl'] = SITE.utils::dirCanonicalPath(CMS_DIR.UPLOADS_DIR);
		$params['avatarDirUrl'] = $params['uploadUrl'].cms_users::AVATAR_UPL_DIR;

		return self::render('cms_user_edit', $params);
	}

	public static function action_ajax_set_ban() { // 2016-11-16
		header('Content-type: application/json; charset=utf-8');

		$response = ['success' => false, 'message' => 'ajax_invalid_request'];

		if (!empty($_SERVER['HTTP_X_REQUESTED_WITH']) && strtolower($_SERVER['HTTP_X_REQUESTED_WITH'])=='xmlhttprequest') {
			$id = @(int)$_POST['user_id'];
			$status = @(string)$_POST['turn'];
			if ($id=='1') {
				$response['message'] = 'access_prohibited_cant_block_root_user';
			} else {
				$updated = cms_users::setUserStatus($id, $status);
				if ($updated) {
					$response['success'] = true;
					$response['message'] = 'update_suc';
					$response['data']['action'] = $status;
				}
			}
		}

		return json_encode($response);
	}

	public static function action_change_my_password() { // 2017-09-04
		self::$layout = 'common_layout';

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('cms_users/change_my_password', 'write');

		$id = @(int)$_GET['id'];
		$params['user'] = cms_users::getUser($id);

		if (empty($params['user']['id']) || ($id!=$_SESSION[CMS::$sess_hash]['ses_adm_id'])) {
			return CMS::resolve('base/404');
		}

		if (isset($_POST['save']) || isset($_POST['apply'])) {
			$params['op'] = cms_users::validatePasswordChange($id);
			if ($params['op']['success']) {
				if (empty($params['op']['data']['is_approve_required'])) {
					$params['op'] = cms_users::savePasswordData($id, $params['op']['data']['validated']);
					$params['user'] = cms_users::getUser($id);
				} else {
					$_SESSION['approvable_user_modifications'] = $params['op']['data']['validated'];
					self::$layout = 'simple_layout';
					$params['approvement_key'] = 'approvable_user_modifications';
					view::$title = CMS::t('password_approve_title');

					return self::render('lockscreen', $params);
				}
			}
		}

		if (isset($_POST['approvement_key']) && ($_POST['approvement_key']=='approvable_user_modifications')) {
			/*
				This section verifyes user credential entered at the lockscreen to enshure that editable user was edited by authorized user,
				not anonimus who taked control over computer with active session.
			*/

			$user = cms_users::getUserByLogin($_SESSION[CMS::$sess_hash]['ses_adm_login']);
			if (empty($user['id'])) {
				CMS::logout();
				return '';
			}
			if (!security::validatePassword($user['password'], @$_POST['password'], CMS::$salt)) {
				CMS::logout();
				return '';
			}

			$params['op'] = cms_users::savePasswordData($id, @$_SESSION['approvable_user_modifications']);
			$params['user'] = cms_users::getUser($id);

			if ($params['op']['success']) {
				unset($_SESSION['approvable_user_modifications']);
			}
		}

		return self::render('change_my_password', $params);
	}
}

?>