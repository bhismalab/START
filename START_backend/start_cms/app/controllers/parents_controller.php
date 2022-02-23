<?php

namespace app\controllers;

use app\helpers\app;
use app\models\parents;
use app\models\states;
use tb\start_cms\CMS;
use tb\start_cms\base\controller;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class parents_controller extends controller {
	/*
		This contoller contains actions to
			download parent signature and
			edit parent personal info.
	*/

	private static $runtime = [];

	public static function action_download_parent_signature() { // 2017-05-15
		$parent_id = @(int)$_GET['parent_id'];
		$p = parents::getParentById($parent_id);

		if (empty($p['id']) || empty($p['signature']) || !is_file(UPLOADS_DIR.parents::$signatures_dir.$p['signature'])) {
			return CMS::resolve('base/404');
		}

		$fname = UPLOADS_DIR.parents::$signatures_dir.$p['signature'];
		header('Content-Type: '.mime_content_type($fname));
		header('Content-Disposition: attachment; filename='.$p['signature']);
		header('Content-Length: '.filesize($fname));
		header('Content-Transfer-Encoding: binary');

		readfile($fname);

		return '';
	}

    public static function action_edit() { // 2017-07-25
		header('Content-type: application/json; charset=utf-8');

		$response = ['success' => false, 'message' => 'ajax_invalid_request'];

		if (utils::isAjax()) {
			$parent_id = @(string)$_GET['parent_id'];
			$allowed_fields = ['name', 'surname', 'state', 'address', 'gender', 'birth_date'];
			$upd = utils::array_filter_keys($_POST, $allowed_fields);
			$upd['gender'] = ((@$upd['gender']=='Female')? 'F': 'M');
			$upd['birth_date'] = utils::formatPlainDate('Y-m-d', @$upd['birth_date']);

			$state = states::getById($upd['state']);
			$upd['state'] = $state['name'];

			$saved = CMS::$db->mod('parents#'.$parent_id, [
				'encrypted_fields' => $allowed_fields,
				'data' => $upd
			]);

			$response['success'] = $saved;
			$response['message'] = CMS::t('update_'.($saved? 'suc': 'err'));

			if ($saved) {
				CMS::log([
					'subj_table' => 'parents',
					'subj_id' => $parent_id,
					'action' => 'edit',
					'descr' => 'Parent '.$upd['name'].' '.$upd['surname'].' was edited by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
				]);
			}
		}

        return json_encode($response);
    }
}

?>