<?php

namespace app\controllers;

use app\models\app_languages;
use tb\start_cms\CMS;
use tb\start_cms\base\controller;
use tb\start_cms\helpers\security;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class languages_controller extends controller {

	private static $runtime = [];

	public static function action_list() { // 2017-10-18
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_languages_list');

		$params = [];
		$params['languages'] = app_languages::getLanguagesList();
		$params['canWrite'] = CMS::hasAccessTo('languages/list', 'write');
		$params['link_return'] = urlencode(SITE.CMS_DIR.utils::trueLink(['controller', 'action']));

		return self::render('languages_list', $params);
	}

	public static function action_add() { // 2017-10-18
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_languages_add');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('languages/add', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=languages&action=add' : $_GET['return']);

		if (isset($_POST['add'])) {
			$params['op'] = app_languages::addLanguage();
			if ($params['op']['success']) {
				utils::delayedRedirect($params['link_back']);
			}
		}

		return self::render('languages_add', $params);
	}

	public static function action_edit() { // 2017-10-18
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_languages_edit');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('languages/edit', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=languages&action=list' : $_GET['return']);

		$id = @(int)$_GET['id'];
		$params['language'] = app_languages::getById($id);

		if (empty($params['language']['id'])) {
			return CMS::resolve('base/404');
		}

		if (isset($_POST['save']) || isset($_POST['apply'])) {
			$params['op'] = app_languages::updateLanguage($id);
			if ($params['op']['success']) {
				if ($params['op']['success'] && isset($_POST['save'])) {
					utils::delayedRedirect($params['link_back']);
				} else {
					return utils::redirect('?controller=languages&action=edit&id='.urlencode($id)); 
				}
			}
		}

		return self::render('languages_edit', $params);
	}

	public static function action_delete() { // 2016-10-18
		self::$layout = 'common_layout';
		view::$title = CMS::t('delete');
		$params = [];
		$params['canWrite'] = CMS::hasAccessTo('languages/delete', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=languages&action=list' : $_GET['return']);
		$deleted = false;
		if ($params['canWrite']) {
			$deleted = app_languages::deleteLanguage(@$_POST['delete']);
		}
		$params['op']['success'] = $deleted;
		$params['op']['message'] = 'del_'.($deleted? 'suc': 'err');
		return self::render('languages_delete', $params);
	}
}

?>