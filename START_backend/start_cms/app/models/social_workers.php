<?php

namespace app\models;

use abeautifulsite\simple_image\SimpleImage;
use tb\start_cms\CMS;
use tb\start_cms\helpers\security;
use tb\start_cms\helpers\utils;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class social_workers {
	public static $curr_pg = 1;
	public static $pp = 10;
	public static $pages_amount = 0;
	public static $items_amount = 0;

	public static $users_tbl = 'social_workers';
	public static $allowed_avatar_ext = ['jpg', 'jpeg', 'jpe', 'png'];
	public static $avatar_size = ['width' => 256, 'height' => 256];
	public static $colors = [/*'white','gray','black',*/ 'red', 'orange', 'yellow', 'lime', 'green', 'olive', 'cyan', 'blue', 'violet', 'purple', 'saddlebrown'];

	const AVATAR_UPL_DIR = 'avatars/social_workers/';

	public static function getUsersList() { // 2017-04-14
		$list = [];

		$where = [];
		$where[] = "is_deleted='0'";
		if (!empty($_GET['q'])) {
			$where[] = "(full_name LIKE '%".utils::makeSearchable($_GET['q'])."%' OR email LIKE '%".utils::makeSearchable($_GET['q'])."%')";
		}
		if (in_array(@$_GET['filter']['is_blocked'], ['0', '1'])) {
			$where[] = "is_blocked=".CMS::$db->escape($_GET['filter']['is_blocked']);
		}

		$c = CMS::$db->select('COUNT(id)', self::$users_tbl, $where);
		self::$items_amount = $c;
		// print "<pre>RESULT:\n{$c}\n\nQUERIES:\n".var_export(CMS::$db->queries, 1)."\n\nERRORS:\n".var_export(CMS::$db->errors, 1)."\n</pre>";
		$pages_amount = ceil($c/self::$pp);

		if ($pages_amount>0) {
			self::$pages_amount = $pages_amount;
			self::$curr_pg = ((self::$curr_pg>self::$pages_amount)? self::$pages_amount: self::$curr_pg);
			$start_from = (self::$curr_pg-1)*self::$pp;

			$list = CMS::$db->selectAll('*', self::$users_tbl, $where, 'email', $start_from, self::$pp);
			// print "<pre>RESULT:\n".var_export($list, 1)."\n\nQUERIES:\n".var_export(CMS::$db->queries, 1)."\n\nERRORS:\n".var_export(CMS::$db->errors, 1)."\n</pre>";
		}

		return $list;
	}

	public static function countWorkers() { // 2017-04-18
		return CMS::$db->get("SELECT COUNT(id) FROM `".self::$users_tbl."` WHERE is_deleted='0'");
	}
	
	public static function addWorker() { // 2017-04-18
		$response = ['success' => false, 'message' => 'insert_err'];

		$user = [];

		if (empty($_POST['email'])) {
			$response['errors'][] = 'Username cannot be empty.';
		} else if (!utils::checkLogin($_POST['email'])) {
			$response['errors'][] = 'Username is invalid.';
		} else if (self::isDuplicateLogin($_POST['email'])) {
			$response['errors'][] = 'Such username already registered.';
		} else {
			$user['email'] = $_POST['email'];
		}

		if (!utils::checkPass($_POST['pwd'])) {
			$response['errors'][] = 'cms_user_pwd_err';
		} else {
			$user['password'] = security::generatePasswordHash($_POST['pwd'], CMS::$salt);
		}

		$name = trim(@(string)$_POST['full_name']);
		if (empty($name)) {
			$response['errors'][] = 'cms_user_name_err';
		} else {
			$user['full_name'] = $name;
		}

		/*if (empty($_POST['color']) || !in_array($_POST['color'], self::$colors)) {
			$response['errors'][] = 'social_workers_color_invalid_err';
		} else {
			$user['color'] = $_POST['color'];
		}*/

		/*if (empty($_POST['gender']) || !in_array($_POST['gender'], ['M', 'F'])) {
			$response['errors'][] = 'social_workers_gender_invalid_err';
		} else {
			$user['gender'] = $_POST['gender'];
		}*/

		if (empty($_POST['birth_date'])) {
			$response['errors'][] = 'social_workers_birth_date_empty_err';
		} else if (!utils::valid_date($_POST['birth_date'])) {
			$response['errors'][] = 'social_workers_birth_date_invalid_err';
		} else {
			$user['birth_date'] = utils::formatPlainDate('Y-m-d', $_POST['birth_date']);
		}

		if (empty($response['errors'])) {
			$user['reg_by'] = $_SESSION[CMS::$sess_hash]['ses_adm_id'];
			$user['reg_date'] = date('Y-m-d H:i:s');

			$user_id = CMS::$db->add(self::$users_tbl, $user);

			if ($user_id) {
				CMS::log([
					'subj_table' => self::$users_tbl,
					'subj_id' => $user_id,
					'action' => 'add',
					'descr' => 'Social worker '.$user['full_name'].' added by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
				]);

				$response['success'] = true;
				$response['message'] = 'insert_suc';
			} else {
				print "<pre>\n".var_export($user, 1)."\n\n".var_export(CMS::$db->queries, 1)."\n\n".var_export(CMS::$db->errors, 1)."\n</pre>";
			}
		}

		return $response;
	}

	private static function isDuplicateLogin($login, $self_id=0) { // 2017-04-19
		$sql = "SELECT `id` FROM ".self::$users_tbl." WHERE `email`=:email".(empty($self_id)? '': ' AND id!=:exclude_id')." LIMIT 1";
		$params = [':email' => $login];
		if (!empty($self_id)) {$params[':exclude_id'] = $self_id;}

		return CMS::$db->getRow($sql, $params);
	}

	public static function getWorker($id) { // 2017-04-19
		return CMS::$db->selectRow('*', self::$users_tbl, ("id='".intval($id)."' AND is_deleted='0'"), '', '', 1);
	}

	public static function editWorker($id) { // 2017-04-19
		$response = ['success' => false, 'message' => 'update_err'];

		$user = self::getWorker($id);
		if (empty($user['id'])) {
			$response['message'] = 'cms_user_edit_err_not_found';
			return $response;
		}

		$upd = [];

		if (empty($_POST['email'])) {
			$response['errors'][] = 'Username cannot be empty.';
		} else if (!utils::checkLogin($_POST['email'])) {
			$response['errors'][] = 'Username is invalid.';
		} else if (self::isDuplicateLogin($_POST['email'], $id)) {
			$response['errors'][] = 'Such username already registered.';
		} else {
			$upd['email'] = $_POST['email'];
		}

		$name = trim(@(string)$_POST['full_name']);
		if (empty($name)) {
			$response['errors'][] = 'cms_user_name_err';
		} else {
			$upd['full_name'] = $name;
		}

		if (!empty($_POST['pwd'])) {
			if (!utils::checkPass($_POST['pwd'])) {
				$response['errors'][] = 'cms_user_pwd_err';
			} else {
				$upd['password'] = security::generatePasswordHash($_POST['pwd'], CMS::$salt);
			}
		}

		/*if (empty($_POST['color']) || !in_array($_POST['color'], self::$colors)) {
			$response['errors'][] = 'social_workers_color_invalid_err';
		} else {
			$upd['color'] = $_POST['color'];
		}

		if (empty($_POST['gender']) || !in_array($_POST['gender'], ['M', 'F'])) {
			$response['errors'][] = 'social_workers_gender_invalid_err';
		} else {
			$upd['gender'] = $_POST['gender'];
		}*/

		if (empty($_POST['birth_date'])) {
			$response['errors'][] = 'social_workers_birth_date_empty_err';
		} else if (!utils::valid_date($_POST['birth_date'])) {
			$response['errors'][] = 'social_workers_birth_date_invalid_err';
		} else {
			$upd['birth_date'] = utils::formatPlainDate('Y-m-d', $_POST['birth_date']);
		}

		if (empty($response['errors'])) {
			$updated = CMS::$db->mod(self::$users_tbl.'#'.$id, $upd);

			if ($updated) {
				CMS::log([
					'subj_table' => self::$users_tbl,
					'subj_id' => $id,
					'action' => 'edit',
					'descr' => 'Social worker '.$upd['full_name'].' was modified by '.ADMIN_TYPE.' '.ADMIN_INFO,
				]);
			}

			$response['success'] = true;
			$response['message'] = 'update_suc';
		}

		return $response;
	}

	public static function setWorkerStatus($id, $status) { // 2017-04-19
		$upd = ['is_blocked' => '1'];
		if ($status=='on') {
			$upd['is_blocked'] = '0';
			$upd['login_attempts'] = '0';
		}
		$updated = CMS::$db->mod(self::$users_tbl.'#'.(int)$id, $upd);

		if ($updated) {
			CMS::log([
				'subj_table' => self::$users_tbl,
				'subj_id' => (int)$id,
				'action' => 'edit',
				'descr' => 'Social worker was '.(($status=='on')? 'un': '').'blocked by '.ADMIN_TYPE.' '.ADMIN_INFO,
			]);
		}

		return $updated;
	}

	public static function deleteWorker($id) { // 2017-04-19
		$deleted = CMS::$db->mod(self::$users_tbl.'#'.(int)$id, [
			'is_deleted' => '1',
		]);

		if ($deleted) {
			CMS::log([
				'subj_table' => self::$users_tbl,
				'subj_id' => $id,
				'action' => 'delete',
				'descr' => 'Social worker was moved to recycle bin by '.ADMIN_TYPE.' '.ADMIN_INFO,
			]);
		}

		return $deleted;
	}

	public static function getList() { // 2017-05-02
		return CMS::$db->getAll("SELECT id, full_name FROM `".self::$users_tbl."` WHERE is_deleted='0'");
	}
}

?>