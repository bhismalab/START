<?php

namespace app\models;

use Yii;
use yii\base\Model;

class Languages extends Model {
	public static function getLanguagesList() { // 2017-10-19
		$sql = "SELECT * FROM languages";
		return Yii::$app->db->createCommand($sql)->queryAll();
	}

	public static function getById($id) { // 2017-10-25
		$sql = "SELECT * FROM languages WHERE id=:id";

		return Yii::$app->db->createCommand($sql, [
			':id' => $id,
		])->queryOne();
    }
    
	public static function getByName($name) { // 2017-11-07
		$sql = "SELECT * FROM languages WHERE name=:name";

		return Yii::$app->db->createCommand($sql, [
			':name' => $name,
		])->queryOne();
	}
}