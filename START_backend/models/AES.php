<?php

namespace app\models;

use Yii;
use yii\helpers\Utils;
use yii\base\Model;

class AES extends Model {

	public static $encryption_key = 't4-h8_hv5^f';
	protected static $aes_encrypt_expression = 'AES_ENCRYPT(%s, UNHEX(SHA2(%s, 512)))';
	protected static $aes_decrypt_expression = 'AES_DECRYPT(%s, UNHEX(SHA2(%s, 512)))';


	public static function escape($value) { // 2017-05-12
		$value = Yii::$app->db->quoteValue($value);
		return ($value? $value: "''");
	}

	public static function decrypt_field($f) { // 2017-04-28
		return sprintf(self::$aes_decrypt_expression, $f, Yii::$app->db->quoteValue(self::$encryption_key));
	}

	public static function f($f, $alias='') { // 2017-04-28
		return self::decrypt_field($f).(empty($alias)? (' AS '.$f): (' AS '.$alias));
	}

	public static function f_like($field, $w) { // 2017-05-11
		return "CONVERT(".self::decrypt_field($field)." USING utf8) LIKE ".Yii::$app->db->quoteValue($w);
	}

	public static function f_is($field, $w) { // 2017-05-11
		return "CONVERT(".self::decrypt_field($field)." USING utf8)=".Yii::$app->db->quoteValue($w);
	}

	public static function escape_encrypted($value) { // 2017-05-11
		return sprintf(self::$aes_encrypt_expression, Yii::$app->db->quoteValue($value), Yii::$app->db->quoteValue(self::$encryption_key));
	}

	public static function add($table, $data, $encrypted_fields) { // 2017-05-11
		$vals = [];
		foreach ($data as $key=>$val) {
			if (is_null($val)) {
				$vals[] = 'NULL';
			} else if (in_array($key, $encrypted_fields)) {
				$vals[] = self::escape_encrypted($val);
			} else {
				$vals[] = self::escape($val);
			}
		}

		$sql = "INSERT INTO `{$table}` (`".implode("`, `", array_keys($data))."`) VALUES (".implode(", ", $vals).")";

		$affected = Yii::$app->db->createCommand($sql)->execute();

		return ($affected? Yii::$app->db->getLastInsertID(): false);
	}

	public static function mod($table, $data, $encrypted_fields, $where, $params=[]) { // 2017-06-13
		$vals = [];
		foreach ($data as $key=>$val) {
			if (is_null($val)) {
				$v = 'NULL';
			} else if (in_array($key, $encrypted_fields)) {
				$v = self::escape_encrypted($val);
			} else {
				$v = self::escape($val);
			}
			$vals[] = "`{$key}`=".$v;
		}

		$sql = "UPDATE {$table} SET ".implode(', ', $vals).(empty($where)? '': " WHERE {$where}");

		$updated = Yii::$app->db->createCommand($sql, $params)->execute();

		return $updated;
	}
}