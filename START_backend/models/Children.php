<?php

namespace app\models;

use Yii;
use yii\helpers\Utils;
use yii\base\Model;
use app\models\AES;

class Children extends Model {

	public static $photos_dir = 'uploads/children_photos/';
	public static $thumbs_dir = 'uploads/children_photos/thumbs/';
	public static $previews_dir = 'uploads/children_photos/previews/';
	public static $originals_dir = 'uploads/children_photos/originals/';
	public static $signatures_dir = 'uploads/parent_signatures/';

	public static function getChildrenList($social_worker, $opts=[]) { // 2017-04-28
		$where = [];
		$params = [];

		//$where[] = "is_deleted='0'";

		$where[] = "add_by=:swid";
		$params[':swid'] = $social_worker;

		if (!empty($opts['since_datetime']) && utils::validateDatetime($opts['since_datetime'])) {
			$where[] = "(add_datetime>=:since OR mod_datetime>=:since)";
			//$params[':since'] = date('Y-m-d H:i:s', strtotime($opts['since_datetime']));
			$params[':since'] = utils::changeDateFormat('c', 'Y-m-d H:i:s', $opts['since_datetime']);
		}

		$sql = "SELECT id, photo, ".AES::f('surname').", ".AES::f('name').", ".AES::f('patronymic').", ".AES::f('gender').", ".AES::f('birth_date').", ".AES::f('state').", ".AES::f('address').", ".AES::f('latitude').", ".AES::f('longitude').", ".AES::f('diagnosis').", ".AES::f('diagnosis_clinic').", ".AES::f('diagnosis_datetime').", ".AES::f('hand').", add_datetime, add_by, mod_datetime, mod_by, is_deleted
			FROM children
			".(empty($where)? '': ("WHERE ".implode(" AND ", $where)));

        $children = Yii::$app->db->createCommand($sql, $params)->queryAll();
        $children = array_map(function($c) {
            return self::mapChildToStateId($c);
        }, $children);

		return $children;
	}

	public static function validateNewChildData($child) { // 2017-05-03
		$response = [
			'success' => false,
			'message' => 'Errors occured during validation.'
		];

		if (!empty($child['photo'])) {
			$saved = utils::base64_to_file($child['photo'], self::$originals_dir);
			if ($saved) {
				$response['data']['validated']['photo'] = $saved;

				require_once 'helpers/SimpleImage.php';

				$img = new yii\helpers\SimpleImage(self::$originals_dir.$saved);
				$img->thumbnail(600, 400)->save(self::$previews_dir.$saved);

				$img = new yii\helpers\SimpleImage(self::$originals_dir.$saved);
				$img->thumbnail(300, 300)->save(self::$thumbs_dir.$saved);
			}
		}

		$name = @(string)$child['name'];
		$surname = @(string)$child['surname'];
		$patronymic = @(string)$child['patronymic'];
		if (empty($name) || empty($surname)) {
			//$response['errors'][] = 'Child name/surname is empty.';
        } /*else if (self::isExists($child)) {
			$response['errors'][] = 'Child already exists.';
		}*/ else {
			$response['data']['validated']['name'] = $name;
			$response['data']['validated']['surname'] = $surname;
			$response['data']['validated']['patronymic'] = $patronymic;
		}

		$bday = @(string)$child['birth_date'];
		if (Utils::validateDatetime($bday, "Y-m-d")) {
			$response['data']['validated']['birth_date'] = $bday;
		} else {
			$response['errors'][] = 'Child birth date is invalid.';
		}

		$gender = @(string)$child['gender'];
		if (in_array($gender, ['M', 'F'])) {
			$response['data']['validated']['gender'] = $gender;
		} else {
			$response['errors'][] = 'Child gender is invalid.';
		}

        $hand = @(string)$child['hand'];
        if (in_array($hand, ['right', 'left', 'ambidextrous'])) {
            $response['data']['validated']['hand'] = $hand;
        } else {
            $response['errors'][] = 'Child hand value is invalid.';
        }

		$state = @(string)$child['state'];
		$state = States::getById($state);
		if (empty($state)) {
			$response['errors'][] = 'Child state is empty.';
		} else {
			$response['data']['validated']['state'] = $state['name'];
		}

		$address = @(string)$child['address'];
		if (empty($address)) {
			$response['errors'][] = 'Child address is empty.';
		} else {
			$response['data']['validated']['address'] = $address;
		}

		$latitude = @(string)$child['latitude'];
		if (empty($latitude)) {
			$response['errors'][] = 'Child GPS latitude is empty.';
		} else {
			$response['data']['validated']['latitude'] = $latitude;
		}

		$longitude = @(string)$child['longitude'];
		if (empty($longitude)) {
			$response['errors'][] = 'Child GPS longitude is empty.';
		} else {
			$response['data']['validated']['longitude'] = $longitude;
		}

		$response['data']['validated']['diagnosis'] = @(string)$child['diagnosis'];
		$response['data']['validated']['diagnosis_clinic'] = @(string)$child['diagnosis_clinic'];
		$response['data']['validated']['diagnosis_datetime'] = date("Y-m-d H:i:s", strtotime(@(string)$child['diagnosis_datetime']));

		if (empty($response['errors'])) {
			$response['success'] = true;
			$response['message'] = 'Validation passed.';
		}

		return $response;
	}

	public static function isExists($child) { // 2017-05-11
		$sql = "SELECT c.id
			FROM children c
			WHERE ".AES::f_is('c.name', $child['name'])." AND ".AES::f_is('c.surname', $child['surname'])." AND ".AES::f_is('c.birth_date', $child['birth_date'])." AND ".AES::f_is('c.gender', $child['gender'])."
			LIMIT 1";

		return Yii::$app->db->createCommand($sql)->queryScalar();
	}

	public static function add($social_worker_id, $child) { // 2017-05-11
		$child['add_datetime'] = date('Y-m-d H:i:s');
		$child['add_by'] = $social_worker_id;
		return AES::add('children', $child, ['name', 'surname', 'state', 'address', 'gender', 'birth_date', 'latitude', 'longitude', 'diagnosis', 'diagnosis_clinic', 'diagnosis_datetime', 'hand']);
	}

	public static function addParent($social_worker_id, $child_id, $parent_data) { // 2017-05-11
		if (!empty($parent_data['signature_scan'])) {
			$parent_data['signature_scan'] = utils::base64_to_file($parent_data['signature_scan'], self::$signatures_dir);
		}

		$parent_data['child_id'] = $child_id;
		$parent_data['add_datetime'] = date('Y-m-d H:i:s');
		$parent_data['add_by'] = $social_worker_id;
		$state = States::getById($parent_data['state']);
		$parent_data['state'] = !isset($state['name']) ? '0' : $state['name'];
		$lang = Languages::getById($parent_data['spoken_language']);
		$parent_data['spoken_language'] = $lang['name'];

		return AES::add('parents', $parent_data, ['name', 'surname', 'state', 'address', 'gender', 'birth_date', 'spoken_language', 'phone', 'email', 'preferable_contact']);
	}

	public static function getParentsByChild($social_worker_id, $child_id) { // 2017-06-13
		$where = [];
		$params = [];

		$where[] = "is_deleted='0'";

		$where[] = "add_by=:swid";
		$params[':swid'] = $social_worker_id;

		$where[] = "child_id=:child_id";
		$params[':child_id'] = $child_id;

		$sql = "SELECT id, child_relationship, ".AES::f('surname').", ".AES::f('name').", ".AES::f('patronymic').", ".AES::f('state').", ".AES::f('address').", ".AES::f('gender').", ".AES::f('birth_date').", ".AES::f('spoken_language').", ".AES::f('phone').", ".AES::f('email').", ".AES::f('preferable_contact').", signature_scan, add_datetime, add_by, mod_datetime, mod_by
			FROM parents
			".(empty($where)? '': ("WHERE ".implode(" AND ", $where)));

		$parents = Yii::$app->db->createCommand($sql, $params)->queryAll();

        $parents = array_map(function($p) {
            if (isset($p['state'])) {
                $p['state'] = self::mapStateNameToStateId($p['state']);
            }

            if (isset($p['spoken_language'])) {
                $p['spoken_language'] = self::mapLanguageNameToLanguageId($p['spoken_language']);
            }

            return $p;
        }, $parents);

		//return Yii::$app->db->createCommand($sql, $params)->getRawSql();
		return $parents;
	}

	public static function deleteParents($social_worker_id, $child_id) { // 2017-06-20
		$sql = "DELETE FROM parents WHERE child_id=:child_id AND add_by=:social_worker_id";
		$params = [
			':social_worker_id' => $social_worker_id,
			':child_id' => $child_id,
		];

		return Yii::$app->db->createCommand($sql, $params)->execute();
	}

	public static function update($social_worker_id, $child_id, $child_data) { // 2017-06-13
		$child_data = utils::array_filter_keys($child_data, ['name', 'surname', 'patronymic', 'photo', 'state', 'address', 'gender', 'birth_date', 'latitude', 'longitude', 'diagnosis', 'diagnosis_clinic', 'diagnosis_datetime', 'hand']);
		if (empty($child_data)) {return false;}

		if (isset($child_data['diagnosis_datetime'])) {
            $child_data['diagnosis_datetime'] = date("Y-m-d H:i:s", strtotime($child_data['diagnosis_datetime']));
        }

        if (isset($child_data['state'])) {
            if ($state = States::getById($child_data['state'])) {
                $child_data['state'] = $state['name'];
            }
        }

        if (isset($child_data['photo']) && !empty($child_data['photo'])) {
		    $sql = "SELECT photo from children where id = :child_id";
            $previous_photo = Yii::$app->db->createCommand($sql, [':child_id' => $child_id])->queryOne();
            if (!empty($previous_photo['photo'])) {
                $previous_photo = $previous_photo['photo'];
                $bool1 = @unlink(realpath(self::$originals_dir.$previous_photo));
                $bool2 = @unlink(realpath(self::$thumbs_dir.$previous_photo));
                $bool3 = @unlink(realpath(self::$previews_dir.$previous_photo));
            }
           /* var_dump($bool1);
            var_dump($bool2);
            var_dump($bool3);*/
            //echo "<pre>", var_dump($previous_photo); die();
            $saved = utils::base64_to_file($child_data['photo'], self::$originals_dir);
            if ($saved) {
                $child_data['photo'] = $saved;
                require_once 'helpers/SimpleImage.php';
                $img = new yii\helpers\SimpleImage(self::$originals_dir.$saved);
                $img->thumbnail(600, 400)->save(self::$previews_dir.$saved);

                $img = new yii\helpers\SimpleImage(self::$originals_dir.$saved);
                $img->thumbnail(300, 300)->save(self::$thumbs_dir.$saved);
            }
        }

		$child_data['mod_datetime'] = date('Y-m-d H:i:s');
        $child_data['mod_by'] = $social_worker_id;

		/*$updated = Yii::$app->db->createCommand()->update('children', $child_data, "id=:child_id AND add_by=:social_worker_id", [
			':child_id' => $child_id,
			':social_worker_id' => $social_worker_id
		])->execute();*/

		$updated = AES::mod(
			'children',
			$child_data,
			['name', 'surname', 'patronymic', 'state', 'address', 'gender', 'birth_date', 'latitude', 'longitude', 'diagnosis', 'diagnosis_clinic', 'diagnosis_datetime', 'hand'],
			"id=:child_id AND add_by=:social_worker_id",
			[
				':child_id' => $child_id,
				':social_worker_id' => $social_worker_id
			]
		);

		return $updated;
	}

	public static function getChildById($social_worker_id, $child_id) { // 2017-06-12
		$sql = "SELECT c.id, c.photo, ".AES::f('c.surname', 'surname').", ".AES::f('c.name', 'name').", ".AES::f('c.patronymic', 'patronymic').", ".AES::f('c.gender', 'gender').", ".AES::f('c.birth_date', 'birth_date').", c.add_datetime, c.add_by
			FROM children c
			WHERE c.id=:child_id AND c.add_by=:social_worker_id AND c.is_deleted='0'
			LIMIT 1";

		$child = Yii::$app->db->createCommand($sql, [
			':child_id' => $child_id,
			':social_worker_id' => $social_worker_id
        ])->queryOne();
        
        if ($child) {
            $child = self::mapChildToStateId($child);
        }

        return $child;
    }
    
    private static function mapChildToStateId($childData) {
        if (isset($childData['state'])) {
            $childData['state'] = self::mapStateNameToStateId($childData['state']);
        }

        return $childData;
    }

    private static function mapStateNameToStateId($name) {
        if (!$state = States::getByName($name)) {
            return '0';
        }

        return $state['id'];
    }

    private static function mapLanguageNameToLanguageId($name) {
        if (!$lang = Languages::getByName($name)) {
            return '0';
        }

        return $lang['id'];
    }
}