<?php

namespace app\models;

use Yii;
use yii\base\Model;

class States extends Model {
	public static function getStatesList() { // 2017-10-18
		$sql = "SELECT * FROM states";
		return Yii::$app->db->createCommand($sql)->queryAll();
	}

	public static function getById($id) { // 2017-10-25
		$sql = "SELECT * FROM states WHERE id=:id";

		return Yii::$app->db->createCommand($sql, [
			':id' => $id,
		])->queryOne();
    }
    
	public static function getByName($name) { // 2017-11-07
		$sql = "SELECT * FROM states WHERE name=:name";

		return Yii::$app->db->createCommand($sql, [
			':name' => $name,
		])->queryOne();
	}
}