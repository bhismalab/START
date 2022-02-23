<?php

namespace app\models;

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class languages {
	public static $site_langs_dir = '../../messages/';

	public static function getLangsList() { // 2016-04-25
		$sql = "SELECT * FROM site_languages ORDER BY is_default DESC, language_dir ASC";
		return CMS::$db->getAll($sql);
	}

	public static function addLang($code, $name) { // 2016-04-26
		$response = [
			'success' => false,
			'message' => CMS::t('insert_err')
		];
		$name = trim($name);
		if (!preg_match('/^[a-z]{2}$/', $code)) {
			$response['errors']['language_dir'] = CMS::t('site_lang_add_err_invalid_code');
		} else if (self::isLangExists($code)) {
			$response['errors']['language_dir'] = CMS::t('site_lang_add_err_duplicate_code');
		}
		if (empty($name)) {
			$response['errors']['language_name'] = CMS::t('site_lang_add_err_invalid_name');
		}
		if (empty($response['errors'])) {
			$added = CMS::$db->add('site_languages', [
				'language_dir' => $code,
				'language_name' => $name
			]);

			if ($added) {
				CMS::log([
					'subj_table' => 'site_languages',
					'subj_id' => $added,
					'action' => 'add',
					'descr' => 'New site language '.$code.' added by admin '.ADMIN_INFO,
				]);

				self::refreshLangSwitchEntries();

				$response['success'] = true;
				$response['message'] = CMS::t('insert_suc');
			}
		}

		return $response;
	}

	public static function isLangExists($code) { // 2016-04-26
		return CMS::$db->get("SELECT id FROM site_languages WHERE language_dir=:code LIMIT 1", [':code' => $code]);
	}

	public static function setLangStatus($code, $status) { // 2016-04-26
		if (!preg_match('/^[a-z]{2}$/', $code)) {return false;}
		if (!in_array($status, ['allow', 'deny'])) {return false;}
		$lang = self::getLang($code);
		if (empty($lang['id'])) {return false;}

		$updated = CMS::$db->mod('site_languages#'.$lang['id'], [
			'is_published' => (($status=='allow')? '1': '0')
		]);

		if ($updated) {
			CMS::log([
				'subj_table' => 'site_languages',
				'subj_id' => $lang['id'],
				'action' => 'edit',
				'descr' => 'Site language '.$code.' '.(($status=='allow')? '': 'un').'published by admin '.ADMIN_INFO,
			]);
		}

		return $updated;
	}

	public static function getLang($code) { // 2016-04-27
		if (!preg_match('/^[a-z]{2}$/', $code)) {return false;}
		return CMS::$db->getRow("SELECT * FROM site_languages WHERE language_dir=:code LIMIT 1", [':code' => $code]);
	}

	public static function getDefaultLangCode() { // 2016-04-27
		return CMS::$db->get("SELECT language_dir FROM site_languages WHERE is_default='1' LIMIT 1");
	}

	public static function getLangFile($code) { // 2016-04-27
		$fname = self::$site_langs_dir.$code.'/app.php';
		if (!is_file($fname)) {return false;}
		$lang_file = include($fname);
		ksort($lang_file);
		return $lang_file;
	}

	public static function saveLangFile($code, $entries) { // 2016-04-27
		if (empty($entries) || !is_array($entries)) {return false;}
		$dir = self::$site_langs_dir.$code.'/';
		if (!is_dir($dir)) {
			mkdir($dir, '0777', true);
		}
		$fname = $dir.'app.php';
		$content = '<?php
	return '.var_export($entries, 1).';
?>';

		CMS::log([
			'subj_table' => 'site_languages',
			'subj_id' => @self::getLang($code)['id'],
			'action' => 'edit',
			'descr' => 'Site language '.$code.' translations file saved by admin '.ADMIN_INFO,
		]);

		return file_put_contents($fname, $content);
	}

	public static function deleteLang($code) { // 2016-04-28
		$lang = self::getLang($code);
		if (empty($lang['id'])) {return false;}

		$deleted = CMS::$db->run("DELETE FROM site_languages WHERE language_dir=:code", [':code' => $code]);

		if ($deleted) {
			CMS::log([
				'subj_table' => 'site_languages',
				'subj_id' => $lang['id'],
				'action' => 'erase',
				'descr' => 'Site language '.$code.' and translations file erased by admin '.ADMIN_INFO,
			]);

			CMS::$db->run("OPTIMIZE TABLE site_languages");
			$dir = self::$site_langs_dir.$code.'/';
			utils::deleteDir($dir);
		}

		return $deleted;
	}

	public static function refreshLangSwitchEntries() { // 2016-05-13
		$default_code = self::getDefaultLangCode();
		$langs = self::getLangsList();
		$langfile = self::getLangFile($default_code);
		foreach ($langs as $l) {
			if (!isset($langfile['_auto_lang_switch_entry_'.$l['language_dir']])) {
				$langfile['_auto_lang_switch_entry_'.$l['language_dir']] = $l['language_dir'];
			}
		}
		self::saveLangFile($default_code, $langfile);
	}
}

?>