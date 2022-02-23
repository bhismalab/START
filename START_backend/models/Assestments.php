<?php

namespace app\models;

use Yii;
use yii\helpers\Utils;
use yii\base\Model;

class Assestments extends Model {

	public static $content_dir = 'uploads/tests_content/';

	public static function get($type, $since=0) { // 2017-06-14
		$type = utils::sanitizeStringByWhitelist($type);

		$params = [];
		$where = [];
		$where[] = "is_deleted='0'";

		if (!empty($since) && utils::validateDatetime($since)) {
			$where[] = "IFNULL(mod_datetime, add_datetime)>=:since_datetime";
			$params[':since_datetime'] = utils::changeDateFormat("c", "Y-m-d H:i:s", $since);
		}

		$where = (empty($where)? '': ('WHERE '.implode(' AND ', $where)));

		//print Yii::$app->db->createCommand("SELECT * FROM `content_{$type}_tests` {$where} LIMIT 1", $params)->getRawSql(); die();
		return Yii::$app->db->createCommand("SELECT * FROM `content_{$type}_tests` {$where} LIMIT 1", $params)->queryOne();
	}

    public static function getParentQuestions($since=0) { // 2017-06-22
        $params = [];
        $where = '';
		if (!empty($since) && utils::validateDatetime($since)) {
			$where = "IFNULL(mod_datetime, add_datetime)>=:since_datetime AND ";
			$params[':since_datetime'] = utils::changeDateFormat("c", "Y-m-d H:i:s", $since);
        }
        
		return Yii::$app->db->createCommand("SELECT id, type, title, question_text, question_text_hindi, video_left, video_right, choicesBlock FROM `content_parent_assestment_tests` WHERE {$where} is_deleted='0' ORDER BY title ASC", $params)->queryAll();
	}

	/*public static function add($social_worker_id, $survey) { // 2017-06-12
		$survey['created_by_social_worker'] = $social_worker_id;

		$inserted = Yii::$app->db->createCommand()->insert('surveys', $survey)->execute();

		return ($inserted? Yii::$app->db->getLastInsertID(): false);
	}*/

	/*public static function update($social_worker_id, $survey_id, $survey) { // 2017-06-12
		$survey = utils::array_filter_keys($survey, ['completed_datetime']);
		if (!empty($survey['completed_datetime'])) {
			$survey['completed_datetime'] = utils::changeDateFormat('c', 'Y-m-d H:i:s', $survey['completed_datetime']);
			$survey['is_completed'] = '1';
		} else {
			return false;
		}

		$updated = Yii::$app->db->createCommand()->update('surveys', $survey, "id=:survey_id AND created_by_social_worker=:social_worker_id", [
			':survey_id' => $survey_id,
			':social_worker_id' => $social_worker_id
		])->execute();

		return $updated;
	}*/
}