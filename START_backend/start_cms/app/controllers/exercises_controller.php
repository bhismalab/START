<?php

namespace app\controllers;

use app\helpers\app;
use app\models\cms_users;
use app\models\exercises;
use tb\start_cms\CMS;
use tb\start_cms\base\controller;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class exercises_controller extends controller {
	/*
		This contoller contains actions to list and manage assessments content except Quesstionnaire.
	*/

    public static $tables = [
        //'bubbles_jubbing' => 'content_bubbles_jubbing_tests',
        'choose_touching' => 'content_choose_touching_tests',
        'wheel' => 'content_wheel_tests',
        'coloring' => 'content_coloring_tests',
        'eye_tracking_pairs' => 'content_eye_tracking_pairs_tests',
        'eye_tracking_slide' => 'content_eye_tracking_slide_tests',
        //'motoric_following' => 'content_motoric_following_tests',
        'parent_assestment' => 'content_parent_assestment_tests',
        'parent_child' => 'content_parent_child_play_tests',
    ];
	private static $runtime = [];

	public static function action_list() { // 2017-05-19
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_block_exercises');

		$params = [];

		$params['exercises'] = exercises::getDataTable();

		$params['canWrite'] = CMS::hasAccessTo('exercises/list', 'write');
		$params['link_sc'] = utils::trueLink(['controller', 'action', 'q', 'filter']);
		$params['link_return'] = urlencode(SITE.CMS_DIR.utils::trueLink(['controller', 'action', 'q', 'filter', 'page']));

		$params['uploadUrl'] = SITE.utils::dirCanonicalPath(CMS_DIR.UPLOADS_DIR);
		$params['avatarDirUrl'] = $params['uploadUrl'].cms_users::AVATAR_UPL_DIR;

		$params['consent_form_english'] = file_get_contents(CONSENT_FORM_FILE_ENGLISH);
		$params['consent_form_hindi'] = file_get_contents(CONSENT_FORM_FILE_HINDI);

		return self::render('exercises_list', $params);
	}

	public static function action_add() { // 2017-05-19
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_exercises_add');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('exercises/add', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=exercises&action=list': $_GET['return']);

		if (isset($_POST['add'])) {
			$params['op'] = exercises::addEx();
			if ($params['op']['success']) {
				utils::delayedRedirect($params['link_back']);
			}
		}

		$params['langs'] = CMS::$site_langs;
		$params['allowed_thumb_ext'] = exercises::$allowed_thumb_ext;

		return self::render('exercises_add', $params);
	}

	public static function action_edit() { // 2017-05-19
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_exercises_edit');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('exercises/edit', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=exercises&action=list': $_GET['return']);

		$id = @(int)$_GET['id'];
		$type = @(string)$_GET['type'];
		$params['exercise'] = exercises::getEx($id, $type);
		if (empty($params['exercise']['id'])) {
			return CMS::resolve('base/404');
		}

		$params['langs'] = CMS::$site_langs;
		$params['allowed_thumb_ext'] = exercises::$allowed_thumb_ext;
		$params['allowed_video_ext'] = exercises::$allowed_video_ext;

		if (isset($_POST['save']) || isset($_POST['apply'])) {
			if (!$params['canWrite']) {CMS::logout();}
			$params['op'] = exercises::editEx($id, $type);
			//print "<pre>\n".var_export($params['op'], 1)."\n\nQUERIES:\n".var_export(CMS::$db->queries, 1)."\n\nERRORS:\n".var_export(CMS::$db->errors, 1)."\n</pre>";
			if ($params['op']['success'] && isset($_POST['save'])) {
				utils::delayedRedirect($params['link_back']);
			}
			$params['exercise'] = exercises::getEx($id, $type);
		}

		//echo "<pre>"; var_dump(self::$tables[$type]); die();
		if (array_key_exists($type, self::$tables)) {
			return self::render(self::$tables[$type], $params);
		} else {
			die("Oops! Something's wrong :(");
		}
	}

	public static function action_delete() { // 2017-05-19
		self::$layout = 'common_layout';
		view::$title = CMS::t('delete');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('exercises/delete', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=exercises&action=list': $_GET['return']);

		$deleted = false;
		if ($params['canWrite']) {
			$deleted = exercises::deleteEx(@$_POST['delete']);
		}
		$params['op']['success'] = $deleted;
		$params['op']['message'] = 'del_'.($deleted? 'suc': 'err');

		return self::render('cms_user_delete', $params);
	}

	public static function action_ajax_set_status() { // 2017-05-19
		header('Content-type: application/json; charset=utf-8');

		$response = ['success' => false, 'message' => 'ajax_invalid_request'];

		if (!CMS::hasAccessTo('exercises/ajax_set_status', 'write')) {
			$response['code'] = '403';
			$response['message'] = 'ajax_request_not_allowed_to_write';
		} else if (!empty($_SERVER['HTTP_X_REQUESTED_WITH']) && strtolower($_SERVER['HTTP_X_REQUESTED_WITH'])=='xmlhttprequest') {
			$id = @(int)$_POST['id'];
			$status = @(string)$_POST['turn'];
			$updated = exercises::setExStatus($id, $status);
			if ($updated) {
				$response['success'] = true;
				$response['message'] = 'update_suc';
				$response['data']['action'] = $status;
			}
		}

		return json_encode($response);
	}

	public static function action_ajax_delete_image() { // 2017-05-19
		header('Content-type: application/json; charset=utf-8');

		$response = ['success' => false, 'message' => 'ajax_invalid_request'];

		if (!CMS::hasAccessTo('exercises/ajax_delete_image', 'write')) {
			$response['code'] = '403';
			$response['message'] = 'ajax_request_not_allowed_to_write';
		} else if (!empty($_SERVER['HTTP_X_REQUESTED_WITH']) && strtolower($_SERVER['HTTP_X_REQUESTED_WITH'])=='xmlhttprequest') {
			if (!empty($_POST['article_id'])) {
				$deleted = exercises::deleteExImages($_POST['article_id']);

				if ($deleted) {
					$response['success'] = true;
					$response['message'] = 'Performed successfully';
				}
			}
		}

		return json_encode($response);
	}
}

?>