<?php

namespace app\models;

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class log {
	public static $tbl = 'cms_log';

	public static function getLog($opts=[]) { // 2017-08-30
	    $where = [];

        if (!empty($_GET['filter']['user'])) {
            $where[] = "cms_user_id='".(int)$_GET['filter']['user']."'";
        }

        if (isset($_GET['q']) && !empty($_GET['q'])) {
            $where[] = "descr LIKE '%".utils::makeSearchable($_GET['q'])."%'";
        }

		if (utils::valid_date(@(string)@$_GET['filter']['since'])) {
			$where[] = "reg_date>=".CMS::$db->escape(utils::changeDateFormat('d.m.Y', 'Y-m-d', $_GET['filter']['since']));
		}

		if (utils::valid_date(@(string)@$_GET['filter']['till'])) {
			$where[] = "DATE(reg_date)<=".CMS::$db->escape(utils::changeDateFormat('d.m.Y', 'Y-m-d', $_GET['filter']['till']));
		}

		$where = (empty($where)? '': ('WHERE '.implode(' AND ', $where)));

		$list = CMS::$db->getAll("SELECT *
			FROM ".self::$tbl."
			{$where}
			ORDER BY reg_date DESC
			".((@$opts['limit']=='no')? '': ("LIMIT ".(empty($opts['limit'])? '200': (int)$opts['limit']))));

		// print "<pre>\n".var_export(CMS::$db->queries, 1)."\n\n".var_export(CMS::$db->errors, 1)."\n</pre>";

		return $list;
	}

    public static function getUsersAreInLogs() { // 2017-08-30
        $list = CMS::$db->getAll("SELECT DISTINCT l.cms_user_id, u.login, u.name
			FROM `".self::$tbl."` l
				JOIN cms_users u ON u.id=l.cms_user_id
			ORDER BY u.name ASC");

        return $list;
    }

	public static function exportLog($data) { // 2017-09-04
		set_time_limit(500);

		require_once VENDOR_DIR.'php_xlsx_writer/xlsxwriter.class.php';
		$xl = new \XLSXWriter();
		$sheet_title = 'Log';

		if (is_array($data) && count($data)) {
			//$xl->writeSheetRow($sheet_title, array_keys($data[0]));
			$xl->writeSheetRow($sheet_title, ['ID', 'CMS user ID', 'Subject table', 'Subject table row ID', 'Action', 'Description', 'Registered at']);

			foreach ($data as $row) {
				$xl->writeSheetRow($sheet_title, $row);
			}
		}

		header('Content-Type: application/octet-stream');
		header('Content-Disposition: attachment; filename=log_'.time().'.xlsx');
		header('Content-Transfer-Encoding: binary');
		$xl->writeToStdOut();

		die();
	}
}

?>