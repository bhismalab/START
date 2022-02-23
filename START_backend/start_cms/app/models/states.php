<?php

namespace app\models;

use tb\start_cms\CMS;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class states {
	public static $tbl = 'states';

	public static function getStatesList() {
		$list = CMS::$db->selectAll('*', self::$tbl);

		return $list;
	}

	public static function getById($id) {
		return CMS::$db->selectRow('*', self::$tbl, ("id='".intval($id)."'"));
	}

	public static function addState() { // 2016-10-18
		$response = ['success' => false, 'message' => 'insert_err', 'errors' => []];

		if (empty($response['errors'])) {
			$state = [];
			$state['name'] = $_POST['state'];
			$state['name_hindi'] = $_POST['state_hindi'];

			$state_id = CMS::$db->add(self::$tbl, $state);

			if ($state_id) {
				CMS::log([
					'subj_table' => self::$tbl,
					'subj_id' => $state_id,
					'action' => 'add',
					'descr' => 'State '.$state['name'].' added by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
				]);

				$response['success'] = true;
				$response['message'] = 'insert_suc';
			}
		}

		return $response;
	}

	public static function updateState($id) { // 2016-10-18
		$response = ['success' => false, 'message' => 'update_err'];

		$upd = [];
		$upd['name'] = $_POST['state'];
		$upd['name_hindi'] = $_POST['state_hindi'];

		//if (empty($response['errors'])) {
			$updated = CMS::$db->mod(self::$tbl.'#'.$id, $upd);
			
			if ($id) {
				CMS::log([
					'subj_table' => self::$tbl,
					'subj_id' => $id,
					'action' => 'edit',
					'descr' => 'State #'.$id.' was modified by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
				]);
			}

			$response['success'] = true;
			$response['message'] = 'update_suc';
		//}

		return $response;
	}

	public static function deleteState($id) {
		$deleted = CMS::$db->exec('DELETE FROM `states` WHERE `id`=:id', [':id' => $id]);

		if ($deleted) {
			CMS::log([
				'subj_table' => self::$tbl,
				'subj_id' => (int)$id,
				'action' => 'erase',
				'descr' => 'State was deleted by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
			]);
		}

		return $deleted;
	}
}

?>