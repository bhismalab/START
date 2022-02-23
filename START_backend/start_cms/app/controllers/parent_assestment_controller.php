<?php
namespace app\controllers;

use app\helpers\app;
use app\models\parent_assestments;
use app\models\question_choices;
use tb\start_cms\CMS;
use tb\start_cms\base\controller;
use tb\start_cms\helpers\security;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;


if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class parent_assestment_controller extends controller {
	/* This contoller contains actions to manage Quesstionnaire */

    public static function action_list() { // 2017-05-19
        self::$layout = 'common_layout';
        $params = [];

        $page = intval(@$_GET['page']);
        parent_assestments::$curr_pg = (empty($page)? 1: $page);

        $params['ques'] = parent_assestments::getQueList();
        $params['count'] = parent_assestments::$items_amount;
        $params['total'] = parent_assestments::$pages_amount;
        $params['current'] = parent_assestments::$curr_pg;
        //echo "<pre>", print_r($params['ques']); die();
        $params['link_sc'] = utils::trueLink(['controller', 'action', 'q']);
        $params['link_return'] = urlencode(SITE.CMS_DIR.utils::trueLink(['controller', 'action', 'q', 'page']));

        $params['canWrite'] = CMS::hasAccessTo('parent_assestment/list', 'write');

        $params['uploadUrl'] = SITE.utils::dirCanonicalPath(CMS_DIR.UPLOADS_DIR);

        return self::render('parent_assestment_list', $params);
    }

    public static function action_add() { // 2017-05-19
        self::$layout = 'common_layout';
        view::$title = 'Add question';

        $params = [];

        $params['canWrite'] = CMS::hasAccessTo('parent_assestment/add', 'write');
        $params['link_back'] = (empty($_GET['return'])? '?controller=parent_assestment&action=list': $_GET['return']);
		$params['choicesBlocks'] = question_choices::getChoicesList();

        if (isset($_POST['add'])) {
            $params['op'] = parent_assestments::addParentTest();

            if ($params['op']['success']) {
                utils::delayedRedirect($params['link_back']);
            }
        }

        $params['allowed_video_ext'] = parent_assestments::$allowed_video_ext;

        return self::render('parent_assestment_add', $params);
    }


    public static function action_delete() { // 2016-10-18
        self::$layout = 'common_layout';
        view::$title = CMS::t('delete');

        $params = [];

        $params['canWrite'] = CMS::hasAccessTo('parent_assestment/delete', 'write');
        $params['link_back'] = (empty($_GET['return'])? '?controller=parent_assestment&action=list': $_GET['return']);

        $deleted = false;
        if ($params['canWrite']) {
            $deleted = parent_assestments::deleteTest(@$_POST['delete']);
        }
        $params['op']['success'] = $deleted;
        $params['op']['message'] = 'del_'.($deleted? 'suc': 'err');

        return self::render('parents_delete', $params);
	}
	

	public static function action_edit() { // 2017-10-17
		self::$layout = 'common_layout';
		view::$title = 'Edit question';

		$id = @(int)$_GET['id'];

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('parent_assestment/delete', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=parent_assestment&action=list' : $_GET['return']);
		$params['allowed_video_ext'] = parent_assestments::$allowed_video_ext;
		$params['uploadUrl'] = SITE.utils::dirCanonicalPath(CMS_DIR.UPLOADS_DIR);
		$params['choicesBlocks'] = question_choices::getChoicesList();

		$params['test'] = parent_assestments::getTest($id);
		if (empty($params['test']['id'])) {
			return CMS::resolve('base/404');
		}

		if ($_SERVER['REQUEST_METHOD'] == 'POST') {
			if (!$params['canWrite']) {
				return CMS::logout();
			}

			$params['op'] = parent_assestments::editParentTest($params['test']['id'], $_POST);
			
			if ($params['op']['success']) {
				isset($_POST['apply']) ?
					utils::redirect('?controller=parent_assestment&action=edit&id='.urlencode($id)) :
					utils::delayedRedirect($params['link_back']);			}
		}

		return self::render('parent_assestment_edit', $params);
	}
}

?>