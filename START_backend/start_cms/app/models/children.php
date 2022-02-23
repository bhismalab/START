<?php

namespace app\models;

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class children {
	public static $curr_pg = 1;
	public static $pp = 20;
	public static $pages_amount = 0;
	public static $items_amount = 0;

	public static $tbl = 'children';

	public static $photos_dir = 'children_photos/';
	public static $thumbs_dir = 'children_photos/thumbs/';
	public static $previews_dir = 'children_photos/previews/';
	public static $originals_dir = 'children_photos/originals/';
    public static $allowed_statuses = ["Active", "Closed", "New"];
    public static $allowed_orders = ["name", "age", "state", "status", "date"];


	public static function getChildrenDataTable() { // 2017-04-27
		$list = [];

		$y3 = date('Y-m-d', mktime(0, 0, 0, date('n'), date('j'), date('Y')-3));
		$y4 = date('Y-m-d', mktime(0, 0, 0, date('n'), date('j')+1, date('Y')-4));

		$joins = [];
		$joins['s'] = "JOIN surveys s ON s.child_id=c.id";
		$joins['sw'] = "JOIN social_workers sw ON sw.id=c.add_by";
		$where = [];
		$where[] = "c.is_deleted='0'";
		$where[] = "s.is_deleted='0'";

		if (!empty($_GET['q'])) {
            if ($_SESSION[CMS::$sess_hash]['ses_adm_type']=="researcher") {
                /** if user try to search using CURL or Sockets */
                die("U can't use search!");
            }
			//$where[] = "first_name LIKE '%".utils::makeSearchable($_GET['q'])."%' OR last_name LIKE '%".utils::makeSearchable($_GET['q'])."%'";
			$q = utils::makeSearchable((string)$_GET['q']);
			$qXploded = explode(' ', $q);
			$byName = '';
			if (is_array($qXploded) && count($qXploded)) foreach ($qXploded as $w) {
				$w = CMS::$db->quote('%'.$w.'%');
				$byName.=(empty($byName)? '': ' AND ')."(
					CONVERT(".CMS::$db->decrypt_field('c.name')." USING utf8) LIKE {$w} OR
					CONVERT(".CMS::$db->decrypt_field('c.surname')." USING utf8) COLLATE utf8_general_ci LIKE {$w} OR
					CONVERT(".CMS::$db->decrypt_field('c.patronymic')." USING utf8) COLLATE utf8_general_ci LIKE {$w} OR
					CONVERT(".CMS::$db->decrypt_field('c.state')." USING utf8) COLLATE utf8_general_ci LIKE {$w}
				)";
			}
			$where[] = $byName;
		}

		if (in_array(@$_GET['filter']['gender'], ['M', 'F'])) {
		    if (ADMIN_TYPE != 'researcher') {
                $where[] = CMS::$db->decrypt_field('c.gender')."=".CMS::$db->escape($_GET['filter']['gender']);
            }
		}

		if (in_array(@$_GET['filter']['age'], ['lt3', '3to4', 'gt4'])) {
		    if (ADMIN_TYPE!='researcher') {
                //$having[] = "age='{$_GET['filter']['age']}'";
                //$where[] = "age='{$_GET['filter']['age']}'";
				if ($_GET['filter']['age']=='lt3') {
					$where[] = CMS::$db->decrypt_field('c.birth_date').">'{$y3}'";
				} else if ($_GET['filter']['age']=='3to4') {
					$where[] = "(".CMS::$db->decrypt_field('c.birth_date')."<='{$y3}' AND ".CMS::$db->decrypt_field('c.birth_date').">='{$y4}')";
				} else if ($_GET['filter']['age']=='gt4') {
					$where[] = CMS::$db->decrypt_field('c.birth_date')."<'{$y4}'";
				}
            }
		}

		if (in_array(@$_GET['filter']['diagnosis'], ['available', 'not_available']) && (ADMIN_TYPE!='researcher')) {
			if ($_GET['filter']['diagnosis']=='available') {
                $where[] = CMS::$db->decrypt_field('c.diagnosis')."!=''";
			} else {
				$where[] = CMS::$db->decrypt_field('c.diagnosis')."=''";
			}
		}

		if (!empty($_GET['filter']['state'])) {
            if (ADMIN_TYPE!='researcher') {
                $where[] = CMS::$db->decrypt_field('c.state')."=".CMS::$db->escape($_GET['filter']['state']);
            }
		}

		if (!empty($_GET['filter']['social_worker'])) {
			$where[] = "c.add_by=".CMS::$db->escape($_GET['filter']['social_worker']);
		}

		if (isset($_GET['order']) && !empty($_GET['order']) && in_array($_GET['order'], self::$allowed_orders)) {
		    if (isset($_GET['sort_rule']) && !empty($_GET['sort_rule']) && in_array($_GET['sort_rule'], ['asc', 'desc'])) {
                $sort_rule = $_GET['sort_rule'];
            } else {
		        $sort_rule = "asc";
            }

            /** Sorting  */
            $order = $_GET['order'];
            switch ($order){
                case "name":
                    $order = CMS::$db->decrypt_field('c.name')." ".$sort_rule;
				break;
                case "age":
                    $order = CMS::$db->decrypt_field("c.birth_date" )." ".$sort_rule;
				break;
                case "state":
                    $order = CMS::$db->decrypt_field("c.state" )." ".$sort_rule;
				break;
                case "date":
                    //$order = "s.created_datetime ".$sort_rule; break;
                    $order = "s.created_datetime"." ".$sort_rule;
				break;
                case "status":
                    $order = "s.is_closed ".$sort_rule;
				break;
            }
        } else {
		    $order = "s.id DESC";
        }

		if (!empty($_GET['filter']['status']) && in_array($_GET['filter']['status'], self::$allowed_statuses)) {
		    /** Filter by Survey status*/
            $status = $_GET['filter']['status'];
            switch ($status){
                case "Active":
                    $where[] = "s.is_closed='0'";
				break;
                case "Closed":
                    $where[] = "s.is_closed='1'";
				break;
                case "New":
                    $where[] = "s.is_inspected='0'";
				break;
            }
		}
		/*if (in_array(@$_GET['filter']['is_blocked'], ['0', '1'])) {
			$where[] = "is_blocked=".CMS::$db->escape($_GET['filter']['is_blocked']);
		}*/
		if (utils::valid_date(@(string)@$_GET['filter']['reg_since'])) {
			$where[] = "add_datetime>=".CMS::$db->escape(utils::changeDateFormat('d.m.Y', 'Y-m-d', $_GET['filter']['reg_since']));
		}
		if (utils::valid_date(@(string)@$_GET['filter']['reg_till'])) {
			$where[] = "DATE(add_datetime)<=".CMS::$db->escape(utils::changeDateFormat('d.m.Y', 'Y-m-d', $_GET['filter']['reg_till']));
		}

		$where = (empty($where)? '': ("WHERE ".implode(" AND ", $where)));
		$having = (empty($having)? '': ("HAVING ".implode(" AND ", $having)));

		$c = CMS::$db->get("SELECT COUNT(c.id)
			FROM ".self::$tbl." c
				".implode("\n", $joins)."
			$where");
		self::$items_amount = $c;
		// print "<pre>RESULT:\n{$c}\n\nQUERIES:\n".var_export(CMS::$db->queries, 1)."\n\nERRORS:\n".var_export(CMS::$db->errors, 1)."\n</pre>";
		$pages_amount = ceil($c/self::$pp);

		if ($pages_amount>0) {
			self::$pages_amount = $pages_amount;
			self::$curr_pg = ((self::$curr_pg>self::$pages_amount)? self::$pages_amount: self::$curr_pg);
			$start_from = (self::$curr_pg-1)*self::$pp;


			$list = CMS::$db->getAll("SELECT c.id, c.photo, ".CMS::$db->aes('c.surname', 'surname').", ".CMS::$db->aes('c.name', 'name').",
					".CMS::$db->aes('c.patronymic', 'patronymic').", ".CMS::$db->aes('c.birth_date', 'birth_date').",
					".CMS::$db->aes('c.gender', 'gender').", ".CMS::$db->aes('c.state', 'state').", c.add_datetime, c.add_by,
					sw.full_name AS social_worker_name, sw.email AS social_worker_email, sw.color AS social_worker_color,
					s.id AS survey_id, s.is_inspected, s.created_datetime AS survey_date, s.is_closed, s.is_completed
				FROM ".self::$tbl." c
					".implode("\n", $joins)."
				$where
				ORDER BY ".$order."
				LIMIT ".(($start_from>0)? ($start_from.', '): '').self::$pp);
			// print "<pre>RESULT:\n".var_export($list, 1)."\n\nQUERIES:\n".var_export(CMS::$db->queries, 1)."\n\nERRORS:\n".var_export(CMS::$db->errors, 1)."\n</pre>";
		}

		return $list;
	}

	public static function getLastRegistered() { // 2017-04-27
		$list = [];

		$sql = "SELECT * FROM `".self::$tbl."` WHERE is_deleted='0' ORDER BY id DESC LIMIT 8";
		$list = CMS::$db->getAll($sql);

		return $list;
	}

	public static function countChildren() { // 2017-04-27
		return CMS::$db->get("SELECT COUNT(id) FROM `".self::$tbl."` WHERE is_deleted='0'");
	}

	public static function countChildrenByGender($gender='M') { // 2017-07-12
		return CMS::$db->get("SELECT COUNT(id) FROM `".self::$tbl."` WHERE is_deleted='0' AND ".CMS::$db->decrypt_field('gender')."=".CMS::$db->escape($gender));
	}

	public static function countChildrenByHand($hand_dominance='undefined') { // 2017-09-05
		$where = [];

		$where[] = "is_deleted='0'";
		if ($hand_dominance=='undefined') {
			$where[] = "(".CMS::$db->decrypt_field('hand')." IS NULL OR ".CMS::$db->decrypt_field('hand')." NOT IN ('right', 'left', 'ambidexter'))";
		} else {
			$where[] = CMS::$db->decrypt_field('hand')."=".CMS::$db->escape($hand_dominance);
		}

		$where = (empty($where)? '': ('WHERE '.implode(' AND ', $where)));

		return CMS::$db->get("SELECT COUNT(id)
			FROM `".self::$tbl."`
			{$where}");
	}

	public static function countChildrenByStates() { // 2017-07-12
		return CMS::$db->getAll("SELECT ".CMS::$db->decrypt_field('state')." AS state, COUNT(id) AS children_count
			FROM `".self::$tbl."`
			WHERE is_deleted='0'
			GROUP BY state
			ORDER BY state");
	}

	public static function countChildrenByLangs() { // 2017-07-14
		return CMS::$db->getAll("SELECT ".CMS::$db->decrypt_field('p.spoken_language')." AS lang, COUNT(p.id) AS p_count
			FROM `".self::$tbl."` c
				JOIN `parents` p ON p.child_id=c.id AND p.is_deleted='0'
			WHERE c.is_deleted='0'
			GROUP BY lang
			ORDER BY lang");
	}

	public static function countChildrenByAges() { // 2017-07-12
		$y3 = date('Y-m-d', mktime(0, 0, 0, date('n'), date('j'), date('Y')-3));
		$y4 = date('Y-m-d', mktime(0, 0, 0, date('n'), date('j')+1, date('Y')-4));
		return CMS::$db->getPairs("SELECT (
				CASE
					WHEN (".CMS::$db->decrypt_field('birth_date').">'{$y3}')
					THEN '<3'

					WHEN (".CMS::$db->decrypt_field('birth_date')."<'{$y4}')
					THEN '>4'

					ELSE '3-4'
				END
			) AS age, COUNT(id) AS children_count
			FROM `".self::$tbl."`
			WHERE is_deleted='0'
			GROUP BY age
			ORDER BY age");
	}

	public static function getCountNewComers() { // 2017-04-27
		return CMS::$db->get("SELECT COUNT(id) FROM `".self::$tbl."` WHERE registration_datetime>='".date('Y-m-d', mktime(0, 0, 0, date('n')-1))."' AND is_deleted='0'");
	}

	/*public static function setChildStatus($id, $status) { // 2017-04-27
		$updated = CMS::$db->mod(self::$tbl.'#'.(int)$id, [
			'is_blocked' => (($status=='on')? '0': '1')
		]);

		if ($updated) {
			CMS::log([
				'subj_table' => self::$tbl,
				'subj_id' => (int)$id,
				'action' => 'edit',
				'descr' => 'Child was '.(($status=='on')? 'un': '').'blocked by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
			]);
		}

		return $updated;
	}*/

	public static function deleteChild($child_id) { // 2017-04-27
		$child_id = (int)$child_id;

		$updated = CMS::$db->mod(self::$tbl.'#'.$child_id, [
			'is_deleted' => '1',
            'mod_datetime' => date('Y-m-d H:i:s')
		]);

		return $updated;
	}

    public static function getChild($child_id) { // 2017-05-02
		return CMS::$db->getRow("SELECT c.id, c.photo, ".CMS::$db->aes('c.surname', 'surname').", ".CMS::$db->aes('c.name', 'name').",
				".CMS::$db->aes('c.patronymic', 'patronymic').", ".CMS::$db->aes('c.birth_date', 'birth_date').", ".CMS::$db->aes('c.hand', 'hand').",
				".CMS::$db->aes('c.gender', 'gender').", ".CMS::$db->aes('c.state', 'state').", ".CMS::$db->aes('c.address', 'address').",
				".CMS::$db->aes('c.latitude', 'latitude').", ".CMS::$db->aes('c.longitude', 'longitude').",
				c.add_datetime, c.add_by, ".CMS::$db->aes('c.diagnosis', 'diagnosis').", ".CMS::$db->aes('c.diagnosis_clinic', 'diagnosis_clinic').", ".CMS::$db->aes('c.diagnosis_datetime', 'diagnosis_datetime')."
			FROM `".self::$tbl."` c
			WHERE c.id=:id AND c.is_deleted='0'
			LIMIT 1",
			[':id' => $child_id]
		);
	}
}

?>