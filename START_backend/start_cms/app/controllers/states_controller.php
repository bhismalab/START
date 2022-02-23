<?php

namespace app\controllers;

use app\models\states;
use tb\start_cms\CMS;
use tb\start_cms\base\controller;
use tb\start_cms\helpers\security;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class states_controller extends controller {

	private static $runtime = [];

	public static function action_list() { // 2017-10-18
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_states_list');

		$params = [];
		$params['states'] = states::getStatesList();
		$params['canWrite'] = CMS::hasAccessTo('states/list', 'write');
		$params['link_return'] = urlencode(SITE.CMS_DIR.utils::trueLink(['controller', 'action']));

		return self::render('states_list', $params);
	}

	public static function action_add() { // 2017-10-18
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_states_add');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('states/add', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=states&action=add' : $_GET['return']);

		if (isset($_POST['add'])) {
			$params['op'] = states::addState();
			if ($params['op']['success']) {
				utils::delayedRedirect($params['link_back']);
			}
		}

		return self::render('states_add', $params);
	}

	public static function action_edit() { // 2017-10-18
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_states_edit');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('states/edit', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=states&action=list' : $_GET['return']);

		$id = @(int)$_GET['id'];
		$params['state'] = states::getById($id);

		if (empty($params['state']['id'])) {
			return CMS::resolve('base/404');
		}

		if (isset($_POST['save']) || isset($_POST['apply'])) {
			$params['op'] = states::updateState($id);
			if ($params['op']['success']) {
				if ($params['op']['success'] && isset($_POST['save'])) {
					utils::delayedRedirect($params['link_back']);
				} else {
					return utils::redirect('?controller=states&action=edit&id='.urlencode($id)); 
				}
			}
		}

		return self::render('states_edit', $params);
	}

	public static function action_delete() { // 2016-10-18
		self::$layout = 'common_layout';
		view::$title = CMS::t('delete');
		$params = [];
		$params['canWrite'] = CMS::hasAccessTo('states/delete', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=states&action=list' : $_GET['return']);
		$deleted = false;
		if ($params['canWrite']) {
			$deleted = states::deleteState(@$_POST['delete']);
		}
		$params['op']['success'] = $deleted;
		$params['op']['message'] = 'del_'.($deleted? 'suc': 'err');
		return self::render('states_delete', $params);
	}
}

?>