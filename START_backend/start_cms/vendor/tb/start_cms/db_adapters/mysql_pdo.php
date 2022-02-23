<?php

namespace tb\start_cms\db_adapters;

if (!defined('_VALID_PHP')) die('Direct access to this location is not allowed.');

class mysql_pdo {
	/*
		This class contains methods for connection and basic interaction with DB.
		There are
			run($sql, $params=[]) // returns query result
			exec($sql, $params=[], $return_affected_rows=true) // executes not-select queries
			add($target, $data, $return_id=true) // inserts data
			mod($target, $data, $where='', $params=[]) // updates data
			getAll($sql, $params=[]) // returns [['id' => '1', 'name' => 'Record 1'], ['id' => '2', 'name' => 'Record 2']]
			getList($sql, $params=[]) // returns ['1', '2']
			getPairs($sql, $params=[]) // returns [['1' => 'Record 1'], ['2' => 'Record 2']]
			getRow($sql, $params=[]) // returns ['id' => '1', 'name' => 'Record 1']
			get($sql, $params=[]) // returns '1'
		methods available.
	*/

	// properties
	public $errors = [];
	public $queries = [];
	public $settings = [];
	protected $pdo;
	protected $queries_count = 0;
	protected $prepared_query = 0;

	public function __construct($settings, $select_db=true) {
		$this->settings = $settings;
		$this->connect($select_db);
	}


	// core functions

	public function getPDO() {
		return $this->pdo;
	}

	protected function interpolateQuery($sql, $params) {
		$keys = [];
		foreach ($params as $key=>$value) {
			$value = (is_null($value)? 'NULL': $this->escape($value));
			if (is_string($key)) {
				$sql = str_replace($key, $value, $sql);
			} else {
				$sql = preg_replace('/[?]/', $value, $sql, 1);
			}
		}
		return $sql;
	}

	public function run($sql, $params=[]) {
		if (empty($sql)) {return false;}
		$begin = microtime(true);
		if (is_array($params) && count($params)) {
			$st = $this->pdo->prepare($sql);
			$st->execute($params);
			$sql = $this->interpolateQuery($sql, $params);
			$err = $st->errorInfo();
		} else {
			$st = $this->pdo->query($sql);
		}
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

	public function exec($sql, $params=[], $return_affected_rows=true) {
		$st = $this->run($sql, $params);

		if ($st) {
			$st_inf = $st->fetchAll(\PDO::FETCH_ASSOC);
			if ($return_affected_rows) {
				$affected = $st->rowCount();
				$st->closeCursor();
				return $affected;
			}
			$st->closeCursor();
			return true;
		}

		return false;
	}

	public function add($target, $data, $return_id=true) {
		if (empty($target) || !is_array($data) || !count($data)) {return false;}

		$path = $this->preparePath($target);
		if (empty($path)) {return false;}

		$vals = [];
		foreach ($data as $val) {
			$vals[] = (is_null($val)? 'NULL': $this->pdo->quote($val));
		}

		$sql = "INSERT INTO {$path} (`".implode("`, `", array_keys($data))."`) VALUES (".implode(", ", $vals).")";

		$affected = $this->exec($sql);
		if ($return_id && $affected) {
			$inserted = $this->pdo->lastInsertId();
			return $inserted;
		}

		return $affected;
	}

	public function mod($target, $data, $where='', $params=[]) {
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

		$vals = [];
		foreach ($data as $fname=>$val) {
			$vals[] = "`{$fname}`=".(is_null($val)? 'NULL': $this->pdo->quote($val));
		}

		$sql = "UPDATE {$path} SET ".implode(', ', $vals).(empty($where)? '': " WHERE {$where}");

		$affected = $this->exec($sql, $params);

		return $affected;
	}

	public function query($sql, $params=[]) {
		return $this->run($sql, $params);
	}

	public function quote($val) {
		return $this->escape($val);
	}


	// protected system functions

	protected function connect($select_db=true) {
		$dsn = 'mysql:host='.$this->settings['host'];
		if (!empty($this->settings['name']) && ($select_db)) {
			$dsn.=';dbname='.$this->settings['name'];
		}

		try {
			$this->pdo = new \PDO($dsn, $this->settings['user'], $this->settings['password']);
		} catch (PDOException $e) {
			$this->errors[] = [
				'time' => microtime(true),
				'message' => $e->getMessage()
			];
			return false;
        }

		if (empty($this->settings['charset'])) {
			//$this->settings['charset'] = 'utf8';
			$this->settings['charset'] = 'utf8mb4';
		}
		$this->exec("SET NAMES :charset", [
			':charset' => $this->settings['charset']
		]);

		return true;
	}

	protected function genSelectQuery($col, $tbl='', $flt='', $ord='', $fr='', $num='') {
		if (!empty($flt) && is_array($flt)) {
			$flt = implode(' AND ', $flt);
		}
		return "SELECT {$col}".(empty($tbl)? '': " FROM {$tbl}").(empty($flt)? '': " WHERE {$flt}").(empty($ord)? '': " ORDER BY {$ord}").(empty($num)? '': (" LIMIT ".(($fr>0)? "{$fr}, ": '')."{$num}"));
	}

	protected function preparePath($target) {
		if (empty($target)) {return false;}

		$path = explode('.', $target);
		$tbl = @array_pop($path);
		if (count($path)) {
			$db = @array_pop($path);
		}
		if (empty($tbl)) {return false;}

		$path = (empty($db)? '': "`{$db}`")."`{$tbl}`";

		return $path;
	}


	// helpers

	public function getAll($sql, $params=[]) {
		$st = $this->run($sql, $params);

		if ($st) {
			$data = $st->fetchAll(\PDO::FETCH_ASSOC);
			$st->closeCursor();
			return $data;
		}

		return false;
	}

	public function getList($sql, $params=[]) {
		$st = $this->run($sql, $params);

		if ($st) {
			$data = $st->fetchAll(\PDO::FETCH_COLUMN);
			$st->closeCursor();
			return $data;
		}

		return false;
	}

	public function getPairs($sql, $params=[]) {
		$st = $this->run($sql, $params);

		if ($st) {
			$data = $st->fetchAll(\PDO::FETCH_KEY_PAIR);
			$st->closeCursor();
			return $data;
		}

		return false;
	}

	public function getRow($sql, $params=[]) {
		$data = $this->getAll($sql, $params);

		if ($data && is_array($data) && isset($data[0])) {
			return $data[0];
		}

		return false;
	}

	public function get($sql, $params=[]) {
		$st = $this->run($sql, $params);

		if ($st) {
			$data = $st->fetchAll(\PDO::FETCH_NUM);
			$st->closeCursor();
			return @$data[0][0];
		}

		return false;
	}

	public function selectAll($col, $tbl='', $flt='', $ord='', $fr='', $num='') {
		$sql = $this->genSelectQuery($col, $tbl, $flt, $ord, $fr, $num);

		return $this->getAll($sql);
	}

	public function selectList($col, $tbl='', $flt='', $ord='', $fr='', $num='') {
		$sql = $this->genSelectQuery($col, $tbl, $flt, $ord, $fr, $num);

		return $this->getList($sql);
	}

	public function selectRow($col, $tbl='', $flt='', $ord='', $fr='', $num='') {
		$sql = $this->genSelectQuery($col, $tbl, $flt, $ord, $fr, $num);

		return $this->getRow($sql);
	}

	public function select($col, $tbl='', $flt='', $ord='', $fr='', $num='') {
		$sql = $this->genSelectQuery($col, $tbl, $flt, $ord, $fr, $num);

		return $this->get($sql);
	}

	public function escape($val) {
		return $this->pdo->quote(@(string)$val);
	}
}

?>