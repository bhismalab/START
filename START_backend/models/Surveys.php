<?php

namespace app\models;

use Yii;
use yii\helpers\BaseUrl;
use yii\helpers\Utils;
use yii\base\Model;
//use app\models\AES;

class Surveys extends Model {

	public static function add($social_worker_id, $survey) { // 2017-06-12
		$survey['created_by_social_worker'] = $social_worker_id;

		$inserted = Yii::$app->db->createCommand()->insert('surveys', $survey)->execute();

		return ($inserted? Yii::$app->db->getLastInsertID(): false);
	}

	public static function update($social_worker_id, $survey_id, $survey) { // 2017-06-12
		$survey = utils::array_filter_keys($survey, ['completed_datetime']);
		if (!empty($survey['completed_datetime'])) {
			$survey['completed_datetime'] = utils::changeDateFormat('c', 'Y-m-d H:i:s', $survey['completed_datetime']);
			$survey['is_completed'] = '1';
			$survey['is_inspected'] = '0';
		} else {
			return false;
		}

		$updated = Yii::$app->db->createCommand()->update('surveys', $survey, "id=:survey_id AND created_by_social_worker=:social_worker_id", [
			':survey_id' => $survey_id,
			':social_worker_id' => $social_worker_id
		])->execute();

		return $updated;
	}

	public static function makeNew($social_worker_id, $survey_id) { // 2017-09-06
		$updated = Yii::$app->db->createCommand()->update('surveys', [
			'is_inspected' => '0'
		], "id=:survey_id AND created_by_social_worker=:social_worker_id", [
			':survey_id' => $survey_id,
			':social_worker_id' => $social_worker_id
		])->execute();

		return $updated;
	}

	public static function getSurveyById($social_worker_id, $survey_id) { // 2017-06-16
		$sql = "SELECT s.*
			FROM surveys s
				JOIN children c ON c.id=s.child_id AND c.is_deleted='0'
			WHERE s.id=:survey_id AND s.is_deleted='0' AND c.add_by=:social_worker_id
			LIMIT 1";

		return Yii::$app->db->createCommand($sql, [
			':survey_id' => $survey_id,
			':social_worker_id' => $social_worker_id
		])->queryOne();
	}

	public static function isAttachmentExists($survey_id, $file_name) { // 2017-06-20
		return Yii::$app->db->createCommand("SELECT id FROM survey_attachments WHERE survey_id=:survey_id AND attachment_file=:attachment_file LIMIT 1", [
			':survey_id' => $survey_id,
			':attachment_file' => $file_name,
		])->queryScalar();
	}

	public static function addAttachment($survey_id, $assestment_type, $file_name) { // 2017-06-16
		$max = Yii::$app->db->createCommand("SELECT MAX(attempt) FROM survey_attachments WHERE survey_id=:survey_id AND assestment_type=:assestment_type", [
			':survey_id' => $survey_id,
			':assestment_type' => $assestment_type,
		])->queryScalar();
		$attempt = ($max? ($max+1): 1);

		$inserted = Yii::$app->db->createCommand()->insert('survey_attachments', [
			'survey_id' => $survey_id,
			'assestment_type' => $assestment_type,
			'attempt' => $attempt,
			'attachment_file' => $file_name,
		])->execute();

		return ($inserted? Yii::$app->db->getLastInsertID(): false);
	}

	public static function getAttachment($survey_id, $assestment_type) { // 2017-06-22
		$attempts_count = Yii::$app->db->createCommand("SELECT MAX(attempt)
			FROM survey_results
			WHERE survey_id=:survey_id AND assestment_type=:assestment_type
			LIMIT 1",
			[
				'survey_id' => $survey_id,
				'assestment_type' => $assestment_type,
			]
		)->queryScalar();

		if (in_array($assestment_type, ['eye_tracking_pairs'])) {
			$attempts = Yii::$app->db->createCommand("SELECT survey_id, assestment_type, attempt, attachment_file
				FROM survey_attachments
				WHERE survey_id=:survey_id AND assestment_type IN ('eye_tracking_pairs', 'eye_tracking_slide')
				ORDER BY attempt",
				[
					'survey_id' => $survey_id,
				]
			)->queryAll();
		} else {
			$attempts = Yii::$app->db->createCommand("SELECT survey_id, assestment_type, attempt, attachment_file
				FROM survey_attachments
				WHERE survey_id=:survey_id AND assestment_type=:assestment_type
				ORDER BY attempt",
				[
					'survey_id' => $survey_id,
					'assestment_type' => $assestment_type,
				]
			)->queryAll();
		}

		$result = [];

		if (in_array($assestment_type, ['eye_tracking_pairs', 'choose_touching', 'wheel', 'bubbles_jubbing', 'parent_assestment', 'parent_child_play'])) {
			if ($attempts) foreach ($attempts as $f) {
				//$f['attachment_file'] = BaseUrl::base(true).'/uploads/survey_attachments/'.$survey_id.'/'.$f['attachment_file'];
				$i = $f['attempt']-1;
				if (empty($result[$i])) {
					$result[$i] = [
						'survey_id' => $f['survey_id'],
						'assestment_type' => $f['assestment_type'],
						'attempt' => $f['attempt'],
						'files' => []
					];
				}
				$result[$i]['files'][] = BaseUrl::base(true).'/uploads/survey_attachments/'.$survey_id.'/'.$f['attachment_file'];
			}
		} else if (in_array($assestment_type, ['motoric_following', 'coloring'])) {
			$i = 0;
			while ($i<$attempts_count) {
				$per_portion = (($assestment_type=='motoric_following')? 4: 2);
				$bunch = array_slice($attempts, ($i*$per_portion), $per_portion);

				foreach ($bunch as $f) {
					if (empty($result[$i])) {
						$result[$i] = [
							'survey_id' => $f['survey_id'],
							'assestment_type' => $f['assestment_type'],
							'attempt' => ($i+1),
							'files' => []
						];
					}
					if (in_array(utils::getFileExt($f['attachment_file']), ['jpg', 'bmp', 'png'])) {
						$result[$i]['files'][] = BaseUrl::base(true).'/uploads/survey_attachments/'.$survey_id.'/'.$f['attachment_file'];
					}
				}

				$i++;
			}
		}

		return $result;
	}

	public static function isResultExists($survey_id, $file_name) { // 2017-06-20
		return Yii::$app->db->createCommand("SELECT id FROM survey_results WHERE survey_id=:survey_id AND result_file=:result_file LIMIT 1", [
			':survey_id' => $survey_id,
			':result_file' => $file_name,
		])->queryScalar();
	}

	public static function addResult($survey_id, $assestment_type, $file_name) { // 2017-06-16
		$max = Yii::$app->db->createCommand("SELECT MAX(attempt) FROM survey_results WHERE survey_id=:survey_id AND assestment_type=:assestment_type", [
			':survey_id' => $survey_id,
			':assestment_type' => $assestment_type,
		])->queryScalar();
		$attempt = ($max? ($max+1): 1);

		$inserted = Yii::$app->db->createCommand()->insert('survey_results', [
			'survey_id' => $survey_id,
			'assestment_type' => $assestment_type,
			'attempt' => $attempt,
			'result_file' => $file_name,
		])->execute();

		return ($inserted? Yii::$app->db->getLastInsertID(): false);
	}

	public static function getResult($survey_id, $assestment_type) { // 2017-08-28
		$attempts = Yii::$app->db->createCommand("SELECT survey_id, assestment_type, attempt, result_file
			FROM survey_results
			WHERE survey_id=:survey_id AND assestment_type=:assestment_type
			ORDER BY attempt",
			[
				'survey_id' => $survey_id,
				'assestment_type' => $assestment_type,
			]
		)->queryAll();

		if (!empty($attempts) && is_array($attempts)) {
			foreach ($attempts as $k=>$f) {
				if (utils::getFileExt($f['result_file'])=='json') {
					$attempts[$k]['survey_result'] = json_decode(file_get_contents('uploads/survey_results/'.$survey_id.'/'.$f['result_file']), 1);
				}
				$attempts[$k]['result_file'] = BaseUrl::base(true).'/uploads/survey_results/'.$survey_id.'/'.$f['result_file'];
			}
		}

		return $attempts;
	}

	/*
	public static function getResult($survey_id, $assestment_type) { // 2017-06-22
		$f = Yii::$app->db->createCommand("SELECT survey_id, assestment_type, attempt, result_file FROM survey_results WHERE survey_id=:survey_id AND assestment_type=:assestment_type LIMIT 1", [
			'survey_id' => $survey_id,
			'assestment_type' => $assestment_type,
		])->queryOne();

		if ($f) {
			if (utils::getFileExt($f['result_file'])=='json') {
				$f['survey_result'] = json_decode(file_get_contents('uploads/survey_results/'.$survey_id.'/'.$f['result_file']), 1);
			}
			$f['result_file'] = BaseUrl::base(true).'/uploads/survey_results/'.$survey_id.'/'.$f['result_file'];

			return $f;
		}

		return false;
	}
	*/

	public static function getSurveysList($social_worker_id, $child_id) { // 2017-06-21
		return Yii::$app->db->createCommand("SELECT s.*
			FROM surveys s
				JOIN children c
			WHERE s.created_by_social_worker=:social_worker_id AND s.child_id=:child_id AND c.is_deleted='0'
			ORDER BY s.id DESC",
		[
			'social_worker_id' => $social_worker_id,
			'child_id' => $child_id,
		])->queryAll();
	}
}