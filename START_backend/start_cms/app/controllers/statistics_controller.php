<?php

namespace app\controllers;

use app\helpers\app;
use app\models\children;
use app\models\cms_users;
use app\models\social_workers;
use app\models\surveys;
use tb\start_cms\CMS;
use tb\start_cms\base\controller;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class statistics_controller extends controller {
	/*
		This controller is responsible to collect and show statistics.
	*/

	private static $runtime = [];

	public static function action_dashboard() { // 2016-08-21
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_statistics_dashboard');

		$params = [];

		//$params['total_cms_users'] = cms_users::countUsers();
		$params['admins_num'] = cms_users::countUsers('admin');
		$params['clinicians_num'] = cms_users::countUsers('clinician');
		$params['researchers_num'] = cms_users::countUsers('researcher');
		$params['social_workers_count'] = social_workers::countWorkers();
		//$params['children_count'] = children::countChildren();
		// print "<pre>\n".var_export($params['latest_registered_users'], 1)."\n\n".var_export(CMS::$db->queries, 1)."\n\n".var_export(CMS::$db->errors, 1)."\n</pre>";

		return self::render('dashboard', $params);
	}

	public static function action_overview() { // 2017-07-12
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_statistics_overview');

		$params = [];

		$params['children_count'] = children::countChildren();

		$params['children_M'] = children::countChildrenByGender('M');
		$params['children_F'] = children::countChildrenByGender('F');

		$params['hand_dominance']['right'] = children::countChildrenByHand('right');
		$params['hand_dominance']['left'] = children::countChildrenByHand('left');
		$params['hand_dominance']['ambidexter'] = children::countChildrenByHand('ambidexter');
		$params['hand_dominance']['undefined'] = children::countChildrenByHand('undefined');

		$params['states'] = children::countChildrenByStates();

		$params['ages'] = children::countChildrenByAges();

		$params['surveys'] = surveys::countSurveys();
		$params['surveys_new'] = surveys::countSurveysByStatus('new');
		$params['surveys_active'] = surveys::countSurveysByStatus('active');
		$params['surveys_closed'] = surveys::countSurveysByStatus('closed');

		$params['langs'] = children::countChildrenByLangs();

		return self::render('statistics', $params);
	}
}

?>