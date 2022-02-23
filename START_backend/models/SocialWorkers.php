<?php

namespace app\models;

use Yii;
use yii\helpers\Utils;
use yii\base\Model;

class SocialWorkers extends Model {

	public static function getSocialWorkerByEmail($email) { // 2017-04-22
		$sql = "SELECT * FROM social_workers WHERE email=:email AND is_deleted='0' LIMIT 1";
		$params = [':email' => $email];

		$social_worker = Yii::$app->db->createCommand($sql, $params)->queryOne();

		return $social_worker;
	}

	public static function getSocialWorkerById($social_worker_id) { // 2017-04-28
		$sql = "SELECT * FROM social_workers WHERE id=:swid AND is_deleted='0' LIMIT 1";
		$params = [':swid' => $social_worker_id];

		$social_worker = Yii::$app->db->createCommand($sql, $params)->queryOne();

		return $social_worker;
	}

	public static function mod($social_worker_id, $data) { // 2017-04-28
		return Yii::$app->db->createCommand()->update('social_workers', $data, 'id='.Yii::$app->db->quoteValue($social_worker_id))->execute();
	}
}