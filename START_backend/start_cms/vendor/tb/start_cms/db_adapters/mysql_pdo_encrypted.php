<?php

namespace tb\start_cms\db_adapters;

use tb\start_cms\db_adapters\mysql_pdo;

if (!defined('_VALID_PHP')) die('Direct access to this location is not allowed.');

class mysql_pdo_encrypted extends mysql_pdo {
	/*
		This class provides methods to work with encrypted fields and construct such queries.
		It extends tb\start_cms\db_adapters\mysql_pdo class.
		There are
			decrypt_field($f) // generates AES_DECRYPT statement with fieldname for search `... OR CONVERT(".CMS::$db->decrypt_field('c.name')." USING utf8) LIKE {$w}`
				and where conditions `... AND ".CMS::$db->decrypt_field('gender')."=".CMS::$db->escape($gender)`
				and selects `SELECT ".CMS::$db->decrypt_field('state')." AS state`
				and cases `WHEN (".CMS::$db->decrypt_field('birth_date').">'{$y3}')`

			aes($f, $alias='') // shorthand for decrypt_field($f) to use in selects `SELECT c.id, c.photo, ".CMS::$db->aes('c.surname', 'surname').",
				".CMS::$db->aes('c.name', 'name').",`

			escape_encrypted($value) // generates AES_ENCRYPT with given value statement for use in UPDATEs

			escape_decrypted($value) // generates AES_DECRYPT statement

			encrypt_field($f) // generates AES_ENCRYPT statement with fieldname

			add($target, $data, $return_id=true) // supports 'encrypted_fields' list, that means this values in 'data' must be escape_encrypted()

			mod($target, $data, $where='', $params=[]) // supports 'encrypted_fields' list, that means this values in 'data' must be escape_encrypted()
				$saved = CMS::$db->mod('children#'.$child_id, [
					'encrypted_fields' => $allowed_fields,
					'data' => $upd
				]);
		methods available.
	*/

	protected $encryption_key = '';
	protected $aes_encrypt_expression = 'AES_ENCRYPT(%s, UNHEX(SHA2(%s, 512)))';
	protected $aes_decrypt_expression = 'AES_DECRYPT(%s, UNHEX(SHA2(%s, 512)))';

	public function __construct($settings, $select_db=true) {
		$this->encryption_key = $settings['encryption_key'];
		return parent::__construct($settings, $select_db);
	}

	public function escape_encrypted($value) { // 2017-04-20
		return sprintf($this->aes_encrypt_expression, $this->escape($value), $this->escape($this->encryption_key));
	}

	public function escape_decrypted($value) { // 2017-04-20
		return sprintf($this->aes_decrypt_expression, $this->escape($value), $this->escape($this->encryption_key));
	}

	public function encrypt_field($f) { // 2017-04-26
		return sprintf($this->aes_encrypt_expression, $f, $this->escape($this->encryption_key));
	}

	public function decrypt_field($f) { // 2017-04-26
		return sprintf($this->aes_decrypt_expression, $f, $this->escape($this->encryption_key));
	}

	public function aes($f, $alias='') { // 2017-04-27
		return $this->decrypt_field($f).(empty($alias)? (' AS '.$f): (' AS '.$alias));
	}

	protected function interpolateQuery($sql, $params) { // 2017-04-20
		$keys = [];

		$encrypted_fields = [];
		$decrypted_fields = [];
		if (!empty($params['encrypted_fields']) || !empty($params['decrypted_fields'])) {
			extract($params);
		}

		foreach ($params as $key=>$value) {
			if (is_null($value)) {
				$value = 'NULL';
			} else if (in_array($key, $encrypted_fields)) {
				$value = $this->escape_encrypted($value);
			} else if (in_array($key, $decrypted_fields)) {
				$value = $this->escape_decrypted($value);
			} else {
				$value = $this->escape($value);
			}

			if (is_string($key)) {
				$sql = str_replace($key, $value, $sql);
			} else {
				$sql = preg_replace('/[?]/', $value, $sql, 1);
			}
		}
		return $sql;
	}

	public function run($sql, $params=[]) { // 2017-04-20
		if (empty($sql)) {return false;}
		$begin = microtime(true);
		$sql = $this->interpolateQuery($sql, $params);
		$st = $this->pdo->query($sql);
		$this->queries_count++;
		$end = microtime(true);
		$this->queries[] = [
			'query_number' => $this->queries_count,
			'begin' => $begin,
			'end' => $end,
			'duration' => round(($end-$begin), 4).'',
			'query' => $sql
		];

		if (!$st || !empty($err)) {
			if (empty($err)) {
				$err = $this->pdo->errorInfo();
			}
			if (!empty($err[1])) {
				$this->errors[] = [
					'query_number' => $this->queries_count,
					'code' => $err[1],
					'SQLSTATE' => $err[0],
					'time' => $end,
					'message' => $err[2]
				];
			}
		}

		return $st;
	}

	public function add($target, $data, $return_id=true) { // 2017-04-20
		if (empty($target) || !is_array($data) || !count($data)) {return false;}

		$path = $this->preparePath($target);
		if (empty($path)) {return false;}

		$encrypted_fields = [];
		$decrypted_fields = [];
		if (!empty($data['encrypted_fields']) || !empty($data['decrypted_fields'])) {
			extract($data);
		}

		$vals = [];
		foreach ($data as $key=>$val) {
			if (is_null($val)) {
				$vals[] = 'NULL';
			} else if (in_array($key, $encrypted_fields)) {
				$vals[] = $this->escape_encrypted($val);
			} else if (in_array($key, $decrypted_fields)) {
				$vals[] = $this->escape_decrypted($val);
			} else {
				$vals[] = $this->escape($val);
			}
		}

		$sql = "INSERT INTO {$path} (`".implode("`, `", array_keys($data))."`) VALUES (".implode(", ", $vals).")";

		$affected = $this->exec($sql);
		if ($return_id && $affected) {
			$inserted = $this->pdo->lastInsertId();
			return $inserted;
		}

		return $affected;
	}

	public function mod($target, $data, $where='', $params=[]) { // 2017-05-02
		if (empty($target) || !is_array($data) || !count($data)) {return false;}

		if (is_array($where) && count($where)) {
			$where = implode(' AND ', $where);
		}

		$target_parts = explode('#', $target);
		$path = array_shift($target_parts);
		if (empty($where) && count($target_parts)) {
			$where = "id='".intval(array_shift($target_parts))."'";
		}

		if (empty($where)) {
			$this->errors[] = [
				'code' => 5003,
				'time' => microtime(true),
				'message' => 'Mysql_PDO class: The empty WHERE parameter is disallowed due to data security reasons'
			];
			return false;
		}

		$path = $this->preparePath($path);
		if (empty($path)) {return false;}


		$encrypted_fields = [];
		$decrypted_fields = [];
		if (!empty($data['encrypted_fields']) || !empty($data['decrypted_fields'])) {
			extract($data);
		}

		$vals = [];
		foreach ($data as $fname=>$val) {
			if (is_null($val)) {
				$v = 'NULL';
			} else if (in_array($fname, $encrypted_fields)) {
				$v = $this->escape_encrypted($val);
			} else if (in_array($fname, $decrypted_fields)) {
				$v = $this->escape_decrypted($val);
			} else {
				$v = $this->escape($val);
			}
			$vals[] = "`{$fname}`=".$v;
		}

		$sql = "UPDATE {$path} SET ".implode(', ', $vals).(empty($where)? '': " WHERE {$where}");

		$affected = $this->exec($sql, $params);

		return $affected;
	}

	protected function genSelectQuery($col, $tbl='', $flt='', $ord='', $fr='', $num='') { // 2017-04-26
		if (!empty($col) && is_array($col)) {
			$col = implode(', ', $col);
		}

		if (!empty($flt) && is_array($flt)) {
			$flt = implode(' AND ', $flt);
		}

		return "SELECT {$col}".(empty($tbl)? '': " FROM {$tbl}").(empty($flt)? '': " WHERE {$flt}").(empty($ord)? '': " ORDER BY {$ord}").(empty($num)? '': (" LIMIT ".(($fr>0)? "{$fr}, ": '')."{$num}"));
	}

	public function get($opts, $params=[]) { // 2017-04-26
		if (is_string($opts)) {return parent::get($opts, $params);}

		if (!empty($opts['params']) && is_array($opts['params'])) {
			$params = array_merge($params, $opts['params']);
			unset($opts['params']);
		}

		if (empty($opts['fields']) || empty($opts['from'])) {
			return false;
		}

		$encrypted_fields = [];
		extract($opts);
		if (empty($fetch_style) || !in_array($fetch_style, ['scalar', 'row', 'pairs', 'list', 'all'])) {
			$fetch_style = 'scalar';
			$fetch_func = 'get';
		}
		if (empty($fetch_func)) {
			$fetch_func = 'get'.ucfirst($fetch_style);
		}

		if (is_array($fields)) {
			if (!empty($encrypted_fields)) {
				foreach ($fields as $i=>$f) {
					if (in_array($f, $encrypted_fields)) {
						$fields[$i] = $this->decrypt_field($f);
					}
				}
			}
		}

		$sql = $this->genSelectQuery($fields, $from, @$where, @$order_by, @$starting_from, @$limit);

		return $this->$fetch_func($sql, $params);
	}
}

?>