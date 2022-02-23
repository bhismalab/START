<?php

namespace tb\start_cms;

//use tb\start_cms\db_adapters\mysql_pdo;
use tb\start_cms\db_adapters\mysql_pdo_encrypted;
use tb\start_cms\helpers\security;
use tb\start_cms\helpers\utils;

if (!defined('_VALID_PHP')) die('Direct access to this location is not allowed.');

class CMS {
	public static $version = '6.2.0';
	public static $db;
	public static $lang;
	public static $site_langs;
	public static $default_site_lang;
	//public static $date_format = 'd/m/Y';
	public static $date_format = 'd.m.Y';
	public static $sess_hash = '';
	public static $salt = 'o8f4s2@-h8s';	// must be project-unique
	public static $roles = [
		'admin' => [],
		'clinician' => [],
		'researcher' => []
	];
	public static $site_settings;
	public static $upload_err = [
		1 => 'upl_ini_size_err',			// The uploaded file exceeds the upload_max_filesize directive in php.ini
		2 => 'upl_form_size_err',			// The uploaded file exceeds the MAX_FILE_SIZE directive that was specified in the HTML form
		3 => 'upl_partial_err',				// The uploaded file was only partially uploaded
		4 => 'upl_no_file_err',				// No file was uploaded
		// 5 => 'upl_empty_file_err',		// Empty file was uploaded
		6 => 'upl_no_tmp_dir_err',			// Missing a temporary folder
		7 => 'upl_can_write_err',			// Failed to write file to disk
		8 => 'upl_extension_err'			// A PHP extension stopped the file upload
	];

	public static function autoload($class) {
		$is_app = preg_match('/^app\\\/', $class);
		$path = ($is_app? '': VENDOR_DIR).str_replace('\\', '/', $class).'.php';

		if (is_file($path)) {
			include $path;
			return true;
		}

		return false;
	}

	public static function init() {
		/*
			CMS environtment initialization.
			Enables CSRF protection for POST forms globally.
			Generated session hash to isolate CMS session data from the rest of domain session data.
			Connects to database.
			Loads site settings.
			Checks logged user in DB (still exists, not blocked).
			Loads CMS language file specified by settings or user.
		*/

		security::$CSRF_token = security::getCSRF_token();
		if ($_SERVER['REQUEST_METHOD']=='POST') {
			security::checkCSRF_token(@$_POST['CSRF_token']);
		}

		self::generateSessionHash();

		self::$db = new mysql_pdo_encrypted([
			'host' => DB_HOST,
			'name' => DB_NAME,
			'user' => DB_USER,
			'password' => DB_PASSWORD,
			'charset' => DB_CHARSET,
			'encryption_key' => DB_AES_KEY
		]);

		self::loadSiteSettings();

		self::checkAdminUserSession();

		self::$lang = include LANG_DIR.self::$site_settings['cms_default_lang'].'.php';
		if (!empty($_SESSION[self::$sess_hash]['ses_adm_id'])) {
			$user_lang_file = LANG_DIR.$_SESSION[self::$sess_hash]['ses_adm_lang'].'.php';
			if (is_file($user_lang_file)) {
				self::$lang = include $user_lang_file;
			}
		}

		self::$site_langs = self::getLangsRegistered();
		self::$default_site_lang = self::getDefaultSiteLang();
	}

	public static function generateSessionHash() {
		self::$sess_hash = md5(SITE.self::$salt.session_id());
	}

	public static function generateAccountHash($user, $action) {
		$hash = md5(md5($user['login'].$action).md5($user['password']));
		return $hash;
	}

	public static function checkAccountHash($hash, $user, $action) {
		$etalon = self::generateAccountHash($user, $action);
		return ($hash==$etalon);
	}

	public static function t($key, $params=[]) {
		if (isset(self::$lang[$key])) {
			if (!empty($params)) {
				return strtr(self::$lang[$key], $params);
			}
			return self::$lang[$key];
		}
		return $key;
	}

	public static function resolve($controller='', $action='') { // 2016-09-05
		if (empty($controller)) {$controller = @(string)@$_GET['controller'];}
		$controller = utils::sanitizeStringByWhitelist($controller, 'a-z\_');
		if (empty($controller)) {$controller = 'base';}
		if (empty($action)) {$action = @(string)@$_GET['action'];}
		$action = utils::sanitizeStringByWhitelist($action, 'a-zA-Z0-9\_');
		if (empty($action)) {$action = '404';}

		if (empty($_SESSION[CMS::$sess_hash]['ses_adm_id'])) {
			$p_all = self::getPrivilegiesByRole('all');
			$has_general_access = isset($p_all[$controller.'/'.$action]);
			if (!$has_general_access) {
				$controller = 'base';
				$action = 'sign_in';
			}
		}

		$method = 'app\\controllers\\'.$controller.'_controller::action_'.$action;
		if (is_file(CONTROLLER_DIR.$controller.'_controller.php') && is_callable($method)) {
			if (!empty($_SESSION[CMS::$sess_hash]['ses_adm_id']) && !self::hasAccessTo($controller.'/'.$action)) {
				$method = 'app\\controllers\\base_controller::action_403';
			}
			$tpl = call_user_func($method);
		} else {
			$tpl = call_user_func('app\\controllers\\base_controller::action_404');
		}

		return $tpl;
	}

	public static function getAdminUser($login) { // 2016-08-14
		$sql = "SELECT * FROM cms_users WHERE login=".self::$db->escape($login)." AND role IN ('".implode("', '", array_keys(self::$roles))."') AND is_blocked='0' LIMIT 1";
		$user = self::$db->getRow($sql);
		return $user;
	}

	public static function login($user) { // 2016-08-14
		if (!empty($user['id'])) {
			$_SESSION[self::$sess_hash]['ses_adm_id'] = $user['id'];
			$_SESSION[self::$sess_hash]['ses_adm_login'] = $user['login'];
			$_SESSION[self::$sess_hash]['ses_adm_name'] = $user['name'];
			$_SESSION[self::$sess_hash]['ses_adm_lang'] = $user['lang'];
			$_SESSION[self::$sess_hash]['ses_adm_type'] = $user['role'];
			$_SESSION[self::$sess_hash]['ses_adm_reg_date'] = $user['reg_date'];
			$_SESSION[self::$sess_hash]['ses_adm_last_login_date'] = $user['last_login_date'];
			$_SESSION[self::$sess_hash]['ses_adm_is_blocked'] = $user['is_blocked'];
			$_SESSION[self::$sess_hash]['avatar'] = (empty($user['avatar'])? '': (UPLOADS_DIR.'/avatars/cms_users/'.$user['avatar']));
			$_SESSION[self::$sess_hash]['ses_adm_is_menu_collapsed'] = $user['is_menu_collapsed'];
			$_SESSION['EditorAuthorized'] = true;
			$_SESSION['EditorBaseUrl'] = SITE;
			$_SESSION['EditorUploadsUrl'] = SITE.utils::dirCanonicalPath(CMS_DIR.UPLOADS_DIR);
			$_SESSION[self::$sess_hash]['LAST_REQUEST_TIME'] = time();
			$_SESSION[self::$sess_hash]['ses_adm_privilegies'] = self::getPrivilegies($user['id']);

			define('ADMIN_ID', $_SESSION[self::$sess_hash]['ses_adm_id']);
			define('ADMIN_TYPE', $_SESSION[self::$sess_hash]['ses_adm_type']);
			define('ADMIN_INFO', $_SESSION[self::$sess_hash]['ses_adm_name']);

			self::$db->mod('cms_users#'.$user['id'], [
				'last_login_date' => date('Y-m-d H:i:s')
			]);

			return true;
		}

		return false;
	}

	public static function logout() { // 2016-08-14
		unset($_SESSION[self::$sess_hash]);
		// session_destroy();
		utils::redirect(SITE.CMS_DIR);
	}

	public static function getPrivilegiesByRole($role) { // 2016-09-05
		return CMS::$db->getPairs("SELECT CONCAT_WS('/', controller, action), is_readonly FROM `cms_users_roles_actions` WHERE role=:role", [':role' => $role]);
	}

	public static function getPrivilegiesByUser($admin_id) { // 2016-09-05
		return CMS::$db->getPairs("SELECT CONCAT_WS('/', controller, action), is_readonly FROM `cms_users_actions` WHERE cms_user_id=:admin_id", [':admin_id' => $admin_id]);
	}

	public static function getPrivilegies($admin_id=0) { // 2016-09-05
		if (empty($admin_id)) {$admin_id = $_SESSION[self::$sess_hash]['ses_adm_id'];}
		$all_p = self::getPrivilegiesByRole('all');
		$user = CMS::$db->getRow("SELECT * FROM `cms_users` WHERE id=:admin_id AND is_blocked='0' LIMIT 1", [':admin_id' => $admin_id]);
		if (empty($user['id'])) {return $all_p;}
		$role_p = self::getPrivilegiesByRole($user['role']);
		$p = array_merge($all_p, $role_p);
		$user_p = self::getPrivilegiesByUser($admin_id);
		if (!empty($user_p)) {
			$p = array_merge($all_p, $user_p);
		}
		return $p;
	}

	public static function hasAccessTo($page, $mode='read', $admin_id=0) { // 2016-09-05
		$hasAccess = false;

		if (empty($admin_id)) {$admin_id = $_SESSION[self::$sess_hash]['ses_adm_id'];}
		$privilegies = self::getPrivilegies($admin_id);

		$hasAccess = isset($privilegies[$page]);

		if ($hasAccess && $mode=='write') {
			$hasAccess = !$privilegies[$page];
		}

		return $hasAccess;
	}

	public static function getLandingPage() { // 2017-05-17
		$landing_page = @self::$site_settings['cms_default_landing_page'];
		$user_role = self::$db->getRow("SELECT * FROM `cms_users_roles` WHERE role=:role LIMIT 1", [':role' => $_SESSION[self::$sess_hash]['ses_adm_type']]);
		if (!empty($user_role['id'])) {
			$landing_page = $user_role['landing_page'];
		}
		if (empty($landing_page)) {die('Unable to get landing page.');}
		return $landing_page;
	}

	public static function loadSiteSettings() {
		$site_settings = self::$db->getPairs("SELECT `option`, `value` FROM `site_settings`");
		if (empty($site_settings)) {die('Unable to load site settings.');}
		define('DEFAULT_LANG_DIR', $site_settings['site_default_lang_dir']);
		self::$site_settings = $site_settings;
	}

	public static function checkAdminUserSession() {
		if (!empty($_SESSION[self::$sess_hash]['ses_adm_id'])) {
			$uid = self::$db->get("SELECT id FROM cms_users WHERE id='".intval($_SESSION[self::$sess_hash]['ses_adm_id'])."' AND role IN ('".implode("', '", array_keys(self::$roles))."') AND is_blocked='0' LIMIT 1");
			if (!empty($uid)) {
				$time = time();
				if (!empty($_SESSION[self::$sess_hash]['LAST_REQUEST_TIME'])) if (($time-$_SESSION[self::$sess_hash]['LAST_REQUEST_TIME']) < 2*3600) {
					define('ADMIN_ID', $_SESSION[self::$sess_hash]['ses_adm_id']);
					define('ADMIN_TYPE', $_SESSION[self::$sess_hash]['ses_adm_type']);
					define('ADMIN_INFO', $_SESSION[self::$sess_hash]['ses_adm_name']);
					$_SESSION[self::$sess_hash]['LAST_REQUEST_TIME'] = $time;

					return true;
				}
			}
		}
		unset($_SESSION[self::$sess_hash]['ses_adm_id']);
		//session_destroy();
		return false;
	}

	public static function getCMSLangsRegistered() {
		return self::$db->getAll("SELECT * FROM `cms_languages` ORDER BY `id` ASC");
	}

	public static function getLangsRegistered() {
		return self::$db->getAll("SELECT * FROM `site_languages` ORDER BY `id` ASC");
	}

	public static function getLangsRegisteredList() {
		return CMS::$db->getList("SELECT `language_dir` FROM `site_languages` ORDER BY `language_dir` ASC");
	}

	public static function getDefaultSiteLang() {
		return self::$db->get("SELECT language_dir FROM `site_languages` WHERE is_default='1' LIMIT 1");
	}

	public static function log($data) {
		return self::$db->add('cms_log', [
			'cms_user_id' => $_SESSION[self::$sess_hash]['ses_adm_id'],
			'subj_table' => $data['subj_table'],
			'subj_id' => $data['subj_id'],
			'action' => $data['action'],
			'descr' => $data['descr'],
			'reg_date' => date('Y-m-d H:i:s')
		]);
	}
}

?>