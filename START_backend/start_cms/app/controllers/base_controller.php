<?php

namespace app\controllers;

use app\helpers\app;
use app\models\cms_users;
use tb\start_cms\CMS;
use tb\start_cms\base\controller;
use tb\start_cms\helpers\security;
use tb\start_cms\helpers\tr;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class base_controller extends controller {
	/*
		This contoller contains actions handling pages that are part of CMS interface, not business logic.
		This actions are primarily public accessible (do not requires authorization).
		They renders error pages, login page, change password page and performs logout.
	*/

	private static $runtime = [];

	public static function action_404() { // 2016-08-18
		header('HTTP/1.1 404 Not Found');

		self::$layout = 'simple_layout';
		view::$title = CMS::t('404_title');

		return self::render('404');
	}

	public static function action_403() { // 2016-09-05
		header('HTTP/1.1 403 Forbidden');

		self::$layout = 'simple_layout';
		view::$title = CMS::t('403_title');

		return self::render('403');
	}

	public static function action_sign_in() { // 2016-08-14
		/*
			This action shows login page and processes user credentials when submitted.
			When authorization attempt occurs it
				seeks for such user in database,
				checks his password,
				counts login attemts and blocks user if its over limit,
				checks if user is blocked and
				redirects to landing page if user is successfully logged in.
		*/

		//self::$layout = 'simple_layout';
		self::$layout = 'login_layout';
		view::$title = CMS::t('login_title');

		$response = [
			'success' => false,
			'message' => 'undefined'
		];
		if (isset($_POST['ad_send'])) {
			$user = cms_users::getUserByLogin(@$_POST['ad_login']);
			if (empty($user['id'])) {
				$response['errors'][] = 'login_err';
			} else if (!security::validatePassword($user['password'], @$_POST['ad_password'], CMS::$salt)) {
                $response['errors'][] = 'login_err';
			    /** Not admin users login attempts - 5 times */
			    if ($user['role']!='admin') {
                    CMS::$db->mod("cms_users".'#'.$user['id'], [
                        'login_attempts' => $user['login_attempts']+1
                    ]);

                    if (($user['login_attempts']+1)>=5) {
                        CMS::$db->mod("cms_users".'#'.$user['id'], [
                            'is_blocked' => '1'
                        ]);
                        $response['errors'] = array();
                        $response['errors'][] = 'too_many_login_attempts';
                    }
                }
			} else if ($user['is_blocked']) {
			    if($user['role'] != 'admin' && $user['login_attempts'] >= 5){
                    $response['errors'][] = 'too_many_login_attempts';
                }else{
                    $response['errors'][] = 'login_err_blocked';
                }
			} else {
                if ($user['role']!='admin') {
                    CMS::$db->mod("cms_users".'#'.$user['id'], [
                        'login_attempts' => 0
                    ]);
                }
				CMS::login($user);
				$response = [
					'success' => true,
					'message' => 'login_suc'
				];
				utils::redirect(SITE.CMS_DIR.(empty($_SERVER['QUERY_STRING'])? CMS::getLandingPage(): ('?'.$_SERVER['QUERY_STRING'])));
			}
		}
		return self::render('sign_in', [
			'response' => $response
		]);
	}

	public static function action_sign_out() { // 2016-08-18
		CMS::logout();
		return '';
	}

	public static function action_password_recovery() { // 2017-06-10
		self::$layout = 'simple_layout';
		view::$title = CMS::t('password_recovery_title');

		$response = [
			'success' => false,
			'message' => 'undefined'
		];
		if (!empty($_POST['get_security_hash'])) {
            $_SESSION["fog_pw"] = "";

			if (!utils::checkLogin($_POST['email'])) {
				$response['errors'][] = 'password_recovery_err_email_invalid';
			} else {
				$user = CMS::getAdminUser($_POST['email']);
				if (empty($user['id'])) {
					$response['errors'][] = 'password_recovery_err_user_not_found';
				} else if (empty($user['birth_date']) || (utils::formatMySQLDate('d.m.Y', $user['birth_date'])!=@$_POST['birth_date'])) {
					$response['errors'][] = 'password_recovery_err_birth_date_invalid';
				} else {
				    /*if($user['login'] !== null){
                        $_SESSION["fog_pw"] = $user['login'];
                    }*/
					return self::render('change_password', [
						'username' => $user['login'],
						'security_hash' => CMS::generateAccountHash($user, 'password_recovery')
					]);
				}
			}
		}
		if (!empty($_POST['change_password'])) {

			if (!utils::checkPass(@$_POST['password'])) {
				$response['errors'][] = 'cms_user_pwd_err';
                $user = CMS::getAdminUser($_POST['username']);

                CMS::$db->mod('cms_users#'.$user['id'], [
                    'security_hash' => CMS::generateAccountHash($user, 'password_recovery')
                ]);
			} else {

                $user = CMS::getAdminUser($_POST['username']);
				if (empty($user['id'])) {
					$response['errors'][] = 'password_recovery_err_user_not_found';
				} else if (!CMS::checkAccountHash(@$_POST['security_hash'], $user, 'password_recovery')) {
					$response['errors'][] = 'password_change_err_account_hash_expired';
				} else {
					$new_hash = security::generatePasswordHash($_POST['password'], CMS::$salt);
					CMS::$db->mod('cms_users#'.$user['id'], [
						'password' => $new_hash
					]);
					$response = [
						'success' => true,
						'message' => 'password_change_suc'
					];
				}
			}

            //var_dump($response); die("aaa");

			return self::render('change_password', [
				'response' => $response,
                'username' => $_POST['username'],
                'security_hash' => CMS::generateAccountHash($user, 'password_recovery')
			]);
		}

		return self::render('password_recovery', [
			'response' => $response
		]);
	}

	public static function action_save_menubar_status() { // 2017-04-15
		header('Content-type: application/json; charset=utf-8');

		$response = [
			'success' => false,
			'message' => CMS::t('ajax_invalid_request')
		];

		if (utils::isAjax()) {
			$collapse = (empty($_POST['is_menu_collapsed'])? '0': '1');

			$updated = CMS::$db->mod('cms_users#'.ADMIN_ID, [
				'is_menu_collapsed' => $collapse
			]);

			//'message' => CMS::t('base_save_menubar_status_unknown_err')
			$response = [
				'success' => true,
				'message' => ($updated? CMS::t('update_suc'): CMS::t('update_no_data_affected'))
			];

			if ($updated) {
				$_SESSION[CMS::$sess_hash]['ses_adm_is_menu_collapsed'] = $collapse;
			}
		}

		return json_encode($response);
	}
}

?>