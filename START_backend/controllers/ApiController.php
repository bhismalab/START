<?php

namespace app\controllers;


use Yii;
use yii\helpers\BaseUrl;
use yii\helpers\Url;
use app\models\Assestments;
use app\models\Children;
use app\models\SocialWorkers;
use app\models\Surveys;
use app\models\States;
use app\models\Languages;

//use yii\rest\ActiveController
use yii\web\Controller;
//use yii\web\HttpException;

use yii\helpers\Utils;
require_once Yii::getAlias('@app').'/helpers/utils.php';

use \Firebase\JWT\JWT;
require_once Yii::getAlias('@vendor').'/firebase/jwt/JWT.php';

// move this out of start_cms?
use app\models\question_choices;
require_once __DIR__.'/../start_cms/app/models/question_choices.php';

class ApiController extends Controller {


	// properties

	private $jwt_key = 'b#Ih54-j*s';
	private $password_salt = 'o8f4s2@-h8s';
	private $max_password_recovery_attempts = 5;
	private $max_login_attempts = 5;


	// methods

    public function actionLogin() { // 2017-04-21
		Yii::$app->response->format = 'json';
		$response = [
			'success' => false,
			'message' => 'Unknown error.'
		];

		if (!Yii::$app->request->isPost) {
			Yii::$app->response->statusCode = 405;
			Yii::$app->response->headers->set('Allow', 'POST');
			$response['message'] = 'Invalid method used in request.';
			return $response;
		}

		$json_data = Utils::parseJsonRequest();
		$email = @(string)$json_data['email'];
		$password = @(string)$json_data['password'];

		$social_worker = SocialWorkers::getSocialWorkerByEmail($email);

		if (empty($social_worker['id'])) {
			Yii::$app->response->statusCode = 404;
			$response['message'] = 'Invalid username or password of social worker.';
			return $response;
		} else if ($social_worker['is_blocked']) {
			Yii::$app->response->statusCode = 403;
			$response['message'] = 'This account is blocked.';
			return $response;
		}

		$is_valid_password = Utils::validatePassword($social_worker['password'], $password, $this->password_salt);

		if (!$is_valid_password) {
			Yii::$app->response->statusCode = 404;
			$response['message'] = 'Invalid username or password of social worker.';

			SocialWorkers::mod($social_worker['id'], [
				'login_attempts' => $social_worker['login_attempts']+1
			]);
			if (($social_worker['login_attempts']+1)>=$this->max_login_attempts) {
				SocialWorkers::mod($social_worker['id'], [
					'is_blocked' => '1'
				]);
			}

			return $response;
		}

		SocialWorkers::mod($social_worker['id'], [
			'login_attempts' => '0'
		]);

		$token_payload = [
			'iss' => 'START app',
			'aud' => 'http://start.tboxapps.co.uk',
			'swid' => $social_worker['id'],
			'iat' => time()
		];

		$jwt_token = JWT::encode($token_payload, $this->jwt_key);

		$response = [
			'success' => true,
			'message' => 'Successfully logged in.',
			'data' => [
				'social_worker' => $social_worker,
				'jwt_token' => $jwt_token
			]
		];

		return $response;
	}

	public function actionError() { // 2017-04-23
		Yii::$app->response->format = 'json';
		Yii::$app->response->statusCode = 404;
		$response = [
			'success' => false,
			'message' => 'Requested action doesn`t exists. Please, check API documentation.'
		];

		return $response;
	}

	public function actionCheckAuthToken() { // 2017-04-23
		Yii::$app->response->format = 'json';
		$response = [
			'success' => false,
			'message' => 'Unknown error.'
		];

		if (!Yii::$app->request->isGet) {
			Yii::$app->response->statusCode = 405;
			Yii::$app->response->headers->set('Allow', 'GET');
			$response['message'] = 'Invalid method used in request.';
			return $response;
		}

		$request_headers = apache_request_headers();
		$auth_token = @(string)$request_headers['X-Auth-Token'];
		if (!empty($auth_token)) {
			try {
				$data = JWT::decode($auth_token, $this->jwt_key, ['HS256']);

				Yii::$app->response->statusCode = 200;
				$response['success'] = true;
				$response['message'] = 'Auth token is valid.';
				$response['data']['payload'] = $data;
				return $response;
			} catch (\Exception $e) {
				Yii::$app->response->statusCode = 403;
				$response['message'] = 'Token is invalid.';
				$response['data']['exception']['message'] = $e->getMessage();
				return $response;
			}
		}

		Yii::$app->response->statusCode = 403;
		$response['message'] = 'Token is invalid.';

		return $response;
	}

	private function check_auth_header() { // 2017-04-28
		$response = [
			'success' => false,
			'code' => 403,
			'message' => 'Token is invalid.',
		];

		$request_headers = apache_request_headers();
		$auth_token = @(string)$request_headers['X-Auth-Token'];

		if (!empty($auth_token)) {
			try {
				$data = (array)JWT::decode($auth_token, $this->jwt_key, ['HS256']);

				if (empty($data['swid'])) {
					$response['message'] = 'Social worker ID is invalid.';
				} else {
					$social_worker = SocialWorkers::getSocialWorkerById($data['swid']);

					if (empty($social_worker['id'])) {
						$response['message'] = 'Social worker not found.';
					} else if ($social_worker['is_blocked']) {
						$response['message'] = 'This account is blocked.';
					} else {
						$response['success'] = true;
						$response['code'] = 200;
						$response['message'] = 'Auth token is valid.';
						$response['data']['payload'] = $data;
						$response['data']['social_worker'] = $social_worker;
					}
				}
			} catch (\Exception $e) {
				$response['data']['exception']['message'] = $e->getMessage();
				return $response;
			}
		}

		return $response;
	}

    public function actionChildren() { // 2017-04-28
		Yii::$app->response->format = 'json';
		$response = [
			'success' => false,
			'message' => 'Unknown error.'
		];

		$auth_data = $this->check_auth_header();
		if (!$auth_data['success']) {
			Yii::$app->response->statusCode = $auth_data['code'];
			$response = $auth_data;
            unset($response['code']);
			return $response;
		}
        $social_worker = $auth_data['data']['social_worker'];

		if (!in_array(Yii::$app->request->method, ['GET', 'POST', 'PUT'])) {
			Yii::$app->response->statusCode = 405;
			Yii::$app->response->headers->set('Allow', 'GET,POST,PUT');
            $response['message'] = 'Invalid method used in request.';
			return $response;
        }

		if (Yii::$app->request->isGet) { // Get children list
			$since = Yii::$app->request->get('since_datetime');
			if (!empty($since) && !utils::validateDatetime($since)) {
				Yii::$app->response->statusCode = 400;
                $response['message'] = 'since_datetime GET parameter is invalid.';
				return $response;
			}

			$children = Children::getChildrenList($social_worker['id'], Yii::$app->request->get(), @$debug);
			if (is_array($children) && count($children)) {
				foreach ($children as $i=>$child) {
					$children[$i]['add_datetime'] = date("c", strtotime($child['add_datetime']));
					if (!empty($child['mod_datetime'])) {
						$children[$i]['mod_datetime'] = date("c", strtotime($child['mod_datetime']));
					}
					if (!empty($child['diagnosis_datetime'])) {
						$children[$i]['diagnosis_datetime'] = date("c", strtotime($child['diagnosis_datetime']));
					}
					if (!empty($child['photo'])) {
						$children[$i]['photo'] = utils::file2base64(Children::$thumbs_dir.$child['photo']);
					}
					$parents = Children::getParentsByChild($social_worker['id'], $child['id']);
					if (!empty($parents)) {
						$children[$i]['parents'] = $parents;
						foreach ($parents as $j=>$p) {
							$children[$i]['parents'][$j]['signature_scan'] = utils::file2base64(Children::$signatures_dir.$p['signature_scan']);
						}
					}
				}
			}

			Yii::$app->response->statusCode = 200;
			$response['success'] = true;
			$response['message'] = 'Chilren list successfully extracted.';
			$response['data']['children'] = $children;
		} else if (Yii::$app->request->isPost) { // Add new child
			//Yii::$app->response->statusCode = 423;
			//$response['message'] = 'Resourse is locked. Implementation is in progress.';
			//$response['data']['input'] = file_get_contents('php://input');

			$c = Utils::parseJsonRequest();
			$child_validation_status = Children::validateNewChildData($c);
			if (!$child_validation_status['success']) {
				Yii::$app->response->statusCode = 404;
                $response = $child_validation_status;
				return $response;
			}

			$child_id = Children::add($social_worker['id'], $child_validation_status['data']['validated']);

			if ($child_id) {
				Yii::$app->response->headers->add('Location', Url::to(['api/child', 'id' => $child_id], 1));
				$response['data']['child_id'] = $child_id;

				if (!empty($c['parents']) && is_array($c['parents'])) {
					foreach ($c['parents'] as $p) {
						Children::addParent($social_worker['id'], $child_id, $p);
					}
				}
            }
            
			Yii::$app->response->statusCode = 201;
			$response['success'] = true;
			$response['message'] = 'Child successfully created.';
		} else if (Yii::$app->request->isPut) { // Update child/parents info
			$child_id = @(string)Yii::$app->request->get('id');
			$child_data = Utils::parseJsonRequest();

			$updated = Children::update($social_worker['id'], $child_id, $child_data);

			if (!empty($child_data['parents'])) {
				Children::deleteParents($social_worker['id'], $child_id);
				foreach ($child_data['parents'] as $p) {
					Children::addParent($social_worker['id'], $child_id, $p);
				}
			}

			if ($updated) {
				$response = [
					'success' => true,
					'message' => 'Child info successfully updated.'
				];
			}
        }

		return $response;
	}

    public function actionChildrenSurveys() { // 2017-06-21
		Yii::$app->response->format = 'json';
		$response = [
			'success' => false,
			'message' => 'Unknown error.'
		];

		$auth_data = $this->check_auth_header();
		if (!$auth_data['success']) {
			Yii::$app->response->statusCode = $auth_data['code'];
			$response = $auth_data;
			unset($response['code']);
			return $response;
		}
		$social_worker = $auth_data['data']['social_worker'];

		if (!in_array(Yii::$app->request->method, ['GET'])) {
			Yii::$app->response->statusCode = 405;
			Yii::$app->response->headers->set('Allow', 'GET');
			$response['message'] = 'Invalid method used in request.';
			return $response;
		}

		$child_id = @(string)Yii::$app->request->get('id');
		$child = Children::getChildById($social_worker['id'], $child_id);

		if (empty($child_id) || empty($child['id'])) {
			Yii::$app->response->statusCode = 404;
			$response['message'] = 'Child not found.';
			return $response;
		}

		$surveys = Surveys::getSurveysList($social_worker['id'], $child['id']);

		if (empty($surveys)) {
			Yii::$app->response->statusCode = 404;
			$response['message'] = 'Surveys not found.';
			return $response;
		}

		$response['success'] = true;
		$response['message'] = 'Surveys listing successfully produced.';
		$response['data'] = [
			'child_info' => $child,
			'items_count' => count($surveys),
			'surveys' => $surveys,
		];

		return $response;
	}

	public function actionPasswordRecovery() { // 2017-05-24
		Yii::$app->response->format = 'json';
		$response = [
			'success' => false,
			'message' => 'Unknown error.'
		];

		if (!Yii::$app->request->isPost) {
			Yii::$app->response->statusCode = 405;
			Yii::$app->response->headers->set('Allow', 'POST');
			$response['message'] = 'Invalid method used in request.';
			return $response;
		}

		$json_data = Utils::parseJsonRequest();
		$email = @(string)$json_data['email'];
		$birth_date = @(string)$json_data['birth_date'];

		$social_worker = SocialWorkers::getSocialWorkerByEmail($email);

		if (empty($social_worker['id'])) {
			Yii::$app->response->statusCode = 404;
			$response['message'] = 'Social worker is not registered or is deleted.';
			return $response;
		} else if ($social_worker['is_blocked']) {
			Yii::$app->response->statusCode = 403;
			$response['message'] = 'This account is blocked.';
			return $response;
		} else if ($social_worker['birth_date']!=$birth_date) {
			SocialWorkers::mod($social_worker['id'], [
				'password_recovery_attempts' => $social_worker['password_recovery_attempts']+1
			]);
			if (($social_worker['password_recovery_attempts']+1)>=$this->max_password_recovery_attempts) {
				SocialWorkers::mod($social_worker['id'], [
					'is_blocked' => '1'
				]);
			}
			Yii::$app->response->statusCode = 404;
			$response['message'] = 'Wrong birth date.';
			return $response;
		}

		SocialWorkers::mod($social_worker['id'], [
			'password_recovery_attempts' => '0'
		]);

		$security_hash = md5(md5($social_worker['id'].$social_worker['email']).md5($social_worker['password']));

		$response = [
			'success' => true,
			'message' => 'Password recovery hash successfully generated.',
			'data' => [
				'social_worker_id' => $social_worker['id'],
				'security_hash' => $security_hash
			]
        ];

		return $response;
	}

    public function actionSocialWorkers() { // 2017-05-25
		Yii::$app->response->format = 'json';
		$response = [
			'success' => false,
			'message' => 'Unknown error.'
		];

		if (empty($this->id)) {
			Yii::$app->response->statusCode = 403;
			$response['message'] = 'You are not permitted to list social workers.';
			return $response;
		}

		if (!Yii::$app->request->isPut) {
			Yii::$app->response->statusCode = 405;
			Yii::$app->response->headers->set('Allow', 'PUT');
			$response['message'] = 'Invalid method used in request.';
			return $response;
		}

		$json_data = Utils::parseJsonRequest();
		$new_password = @(string)$json_data['password'];
		$security_hash = @(string)$json_data['security_hash'];
		$social_worker_id = @(string)Yii::$app->request->get('id');
		$social_worker = SocialWorkers::getSocialWorkerById($social_worker_id);

		if (empty($social_worker['id'])) {
			Yii::$app->response->statusCode = 404;
			$response['message'] = 'Social worker is not registered or is deleted.';
			return $response;
		} else if ($security_hash!=md5(md5($social_worker['id'].$social_worker['email']).md5($social_worker['password']))) {
			Yii::$app->response->statusCode = 403;
			$response['message'] = 'Security hash is invalid or obsolete.';
			return $response;
		} else if ($social_worker['is_blocked']) {
			Yii::$app->response->statusCode = 403;
			$response['message'] = 'This account is blocked.';
			return $response;
		} else if (empty($new_password) || !utils::checkPass($new_password)) {
			Yii::$app->response->statusCode = 403;
			$response['message'] = 'Password must be 6-64 characters in length.';
			return $response;
		}

		SocialWorkers::mod($social_worker['id'], [
			'password' => utils::generatePasswordHash($new_password, $this->password_salt)
		]);

		$response = [
			'success' => true,
			'message' => 'Password successfully updated.'
		];

		return $response;
	}

    public function actionSurveys() { // 2017-06-12
		Yii::$app->response->format = 'json';
		$response = [
			'success' => false,
			'message' => 'Unknown error.'
		];

		$auth_data = $this->check_auth_header();
		if (!$auth_data['success']) {
			Yii::$app->response->statusCode = $auth_data['code'];
			$response = $auth_data;
			unset($response['code']);
			return $response;
		}
		$social_worker = $auth_data['data']['social_worker'];

		if (!in_array(Yii::$app->request->method, [/*'GET',*/ 'POST', 'PUT'])) {
			Yii::$app->response->statusCode = 405;
			Yii::$app->response->headers->set('Allow', 'POST,PUT');
			$response['message'] = 'Invalid method used in request.';
			return $response;
		}

		if (Yii::$app->request->isPost) { // initialize new survey
			$json_data = Utils::parseJsonRequest();
			$child_id = @(int)$json_data['child_id'];
			$child = Children::getChildById($social_worker['id'], $child_id);
			if (empty($child['id'])) {
				Yii::$app->response->statusCode = 404;
				$response['message'] = 'Child not found.';
				return $response;
			}

			$survey = [];
			$survey['child_id'] = $child['id'];
			$survey['created_datetime'] = $json_data['created_datetime'];
			$survey['created_datetime'] = utils::changeDateFormat('Y-m-d\TH:i:sP', 'Y-m-d H:i:s', $survey['created_datetime']);

			$survey_id = Surveys::add($social_worker['id'], $survey);
			if ($survey_id) {
				$response = [
					'success' => true,
					'message' => 'Survey successfully initialized.',
					'data' => [
						'survey_id' => $survey_id
					]
				];
			}
		} else if (Yii::$app->request->isPut) { // update survey status
			$survey_id = @(string)Yii::$app->request->get('id');
			$survey_data = Utils::parseJsonRequest();

			$updated = Surveys::update($social_worker['id'], $survey_id, $survey_data);

			if ($updated) {
				$response = [
					'success' => true,
					'message' => 'Survey successfully updated.'
				];
			}
        }

		return $response;
	}

    public function actionSurveyAttachments() { // 2017-06-16
		Yii::$app->response->format = 'json';
		$response = [
			'success' => false,
			'message' => 'Unknown error.'
		];

		$auth_data = $this->check_auth_header();
		if (!$auth_data['success']) {
			Yii::$app->response->statusCode = $auth_data['code'];
			$response = $auth_data;
			unset($response['code']);
			return $response;
		}
		$social_worker = $auth_data['data']['social_worker'];

		if (!in_array(Yii::$app->request->method, ['GET', 'POST'])) {
			Yii::$app->response->statusCode = 405;
			Yii::$app->response->headers->set('Allow', 'GET,POST');
			$response['message'] = 'Invalid method used in request.';
			return $response;
		}

		$survey_id = @(string)Yii::$app->request->get('id');

		if (Yii::$app->request->isGet) {
			$assestment_type = @(string)Yii::$app->request->get('assestment_type');
			if (empty($assestment_type)) {
				Yii::$app->response->statusCode = 404;
				$response['message'] = 'Assestment type is not specified.';
				return $response;
			}

			$data = Surveys::getAttachment($survey_id, $assestment_type);

			if ($data) {
				$response['success'] = true;
				$response['message'] = 'Survey`s attachment successfully retrieved.';
				$response['data'] = $data;
			} else {
				$response['message'] = 'Data not found.';
			}
		} else if (Yii::$app->request->isPost) {
			$data = json_decode(Yii::$app->request->post('data'), 1);
			$assestment_type = @(string)$data['assestment_type'];

			if (empty($survey_id) || empty($assestment_type)) {
				Yii::$app->response->statusCode = 404;
				$response['message'] = 'Assestment type is not specified.';
				return $response;
			}

			$survey = Surveys::getSurveyById($social_worker['id'], $survey_id);

			if (empty($survey['id'])) {
				Yii::$app->response->statusCode = 404;
				$response['message'] = 'Survey not found.';
				return $response;
			}

			$file_name = @$_FILES['file']['name'];

			if (empty($file_name)) {
				Yii::$app->response->statusCode = 404;
				$response['message'] = 'Survey attachment cannot be uploaded.';
				return $response;
			}

			if (Surveys::isAttachmentExists($survey_id, $file_name)) {
				Yii::$app->response->statusCode = 404;
				$response['message'] = 'Survey attachment already exists.';
				return $response;
			}

			//$saved = utils::base64_to_file($file_content, 'uploads/survey_attachments/'.$survey_id.'/', $file_name);
			$saved = utils::upload($file_name, $_FILES['file']['tmp_name'], 'uploads/survey_attachments/'.$survey_id.'/');

			if ($saved) {
				$attachment_id = Surveys::addAttachment($survey_id, $assestment_type, $saved);
				if (!empty($attachment_id)) {
					Surveys::makeNew($social_worker['id'], $survey_id);

					$response = [
						'success' => true,
						'message' => 'Attachment successfully saved.',
						'data' => [
							'attachment_id' => $attachment_id
						]
					];
				}
			} else {
				Yii::$app->response->statusCode = 500;
				$response['message'] = 'Cannot save attachment to disc.';
				return $response;
			}
		}

		return $response;
	}

    public function actionSurveyResults() { // 2017-06-16
		Yii::$app->response->format = 'json';
		$response = [
			'success' => false,
			'message' => 'Unknown error.'
		];

		$auth_data = $this->check_auth_header();
		if (!$auth_data['success']) {
			Yii::$app->response->statusCode = $auth_data['code'];
			$response = $auth_data;
			unset($response['code']);
			return $response;
		}
        $social_worker = $auth_data['data']['social_worker'];       

		if (!in_array(Yii::$app->request->method, ['GET', 'POST'])) {
			Yii::$app->response->statusCode = 405;
			Yii::$app->response->headers->set('Allow', 'GET,POST');
			$response['message'] = 'Invalid method used in request.';
			return $response;
		}

		$survey_id = @(string)Yii::$app->request->get('id');

		if (Yii::$app->request->isGet) {
			$assestment_type = @(string)Yii::$app->request->get('assestment_type');
			if (empty($assestment_type)) {
				Yii::$app->response->statusCode = 404;
				$response['message'] = 'Assessment type is not specified.';
				return $response;
			}

			$data = Surveys::getResult($survey_id, $assestment_type);

			if ($data) {
				$response['success'] = true;
				$response['message'] = 'Survey`s results successfully retrieved.';
				$response['data'] = $data;
			} else {
				$response['message'] = 'Data not found.';
			}
		} else if (Yii::$app->request->isPost) {
			$data = Utils::parseJsonRequest();
			$assestment_type = @(string)$data['assestment_type'];

			if (empty($survey_id) || empty($assestment_type)) {
				Yii::$app->response->statusCode = 404;
				$response['message'] = 'Assessment type is not specified.';
				return $response;
			}

			$survey = Surveys::getSurveyById($social_worker['id'], $survey_id);

			if (empty($survey['id'])) {
				Yii::$app->response->statusCode = 404;
				$response['message'] = 'Survey not found.';
				return $response;
			}

			$file_name = @(string)$data['file_name'];
			$file_content = @(string)$data['file_content'];

			if ($alreadyExistsResultId = Surveys::isResultExists($survey_id, $file_name)) {
				/*Yii::$app->response->statusCode = 404;
				$response['message'] = 'Survey result already exists.';
                return $response;*/
                
                return [
                    'success' => true,
                    'message' => 'Results file successfully saved.',
                    'data' => [
                        'result_id' => $alreadyExistsResultId
                    ]
                ];
			}

			$saved = utils::base64_to_file($file_content, 'uploads/survey_results/'.$survey_id.'/', $file_name);

			if ($saved) {
				$result_id = Surveys::addResult($survey_id, $assestment_type, $saved);
				if (!empty($result_id)) {
					Surveys::makeNew($social_worker['id'], $survey_id);

					$response = [
						'success' => true,
						'message' => 'Results file successfully saved.',
						'data' => [
							'result_id' => $result_id
						]
					];
				}
			} else {
				Yii::$app->response->statusCode = 500;
				$response['message'] = 'Cannot save results file to disc.';
				return $response;
			}
        }
        
		return $response;
	}

    public function actionAssestments() { // 2017-06-14
		Yii::$app->response->format = 'json';
		$response = [
			'success' => false,
			'message' => 'Unknown error.'
		];

		$auth_data = $this->check_auth_header();
		if (!$auth_data['success']) {
			Yii::$app->response->statusCode = $auth_data['code'];
			$response = $auth_data;
			unset($response['code']);
			return $response;
		}
		$social_worker = $auth_data['data']['social_worker'];

		if (!in_array(Yii::$app->request->method, ['GET'])) {
			Yii::$app->response->statusCode = 405;
			Yii::$app->response->headers->set('Allow', 'GET');
			$response['message'] = 'Invalid method used in request.';
			return $response;
		}

		$type = @(string)Yii::$app->request->get('sef');
		if (empty($type)) {
			Yii::$app->response->statusCode = 404;
			$response['message'] = 'Assestment type is not specified.';
			return $response;
		}

		$choicesList = question_choices::getChoicesList();

        $since = @(string)Yii::$app->request->get('since_datetime');

		if ($type=='parent') {
			$assestments = Assestments::getParentQuestions($since);
			if (!empty($assestments) && is_array($assestments)) {
				foreach ($assestments as $i=>$assestment) {
					$choicesBlock = $assestment['choicesBlock'];
					unset($assestment['choicesBlock']);

					if ($assestment['type']=='common') {
						unset($assestment['video_left'], $assestment['video_right']);
                        $assestments[$i] = $assestment;
                        $assestments[$i]['choices'] = $choicesList[(int) $choicesBlock];
					} else {
                        unset($assestments[$i]['choicesBlock']);
						$assestments[$i]['video_left'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['video_left'];
                        $assestments[$i]['video_right'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['video_right'];
                        $assestments[$i]['choices'] = [
							'english' => ['Video 1', 'Video 2'],
							'hindi' => ['वीडियो 1', 'वीडियो 2']
                        ];
					}
				}
			
                $response = [
                    'success' => true,
                    'message' => 'Assestment data successfully extracted.',
                    'data' => [
                        'questions' => $assestments
                    ]
                ];
                return $response;
            } else {
                Yii::$app->response->statusCode = 404;
                $response['message'] = 'Assestment content is not found.';
                return $response;
            }
		}

		$assestment = Assestments::get($type, $since);
		if (empty($assestment['id'])) {
			Yii::$app->response->statusCode = 404;
			$response['message'] = 'Assestment content is not found.';
			return $response;
		} else {
			if ($type=='bubbles_jubbing') {
				$assestment['bubble_img'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['bubble_img'];
			} else if ($type=='choose_touching') {
				$assestment['video_1_social'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['video_1_social'];
				$assestment['video_1_nonsocial'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['video_1_nonsocial'];
				$assestment['video_2_social'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['video_2_social'];
				$assestment['video_2_nonsocial'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['video_2_nonsocial'];
				$assestment['video_3_social'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['video_3_social'];
				$assestment['video_3_nonsocial'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['video_3_nonsocial'];
				$assestment['video_4_social'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['video_4_social'];
				$assestment['video_4_nonsocial'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['video_4_nonsocial'];
				$assestment['demo_social'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['demo_social'];
				$assestment['demo_nonsocial'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['demo_nonsocial'];
			} else if ($type=='wheel') {
				$assestment['video'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['video'];
			} else if ($type=='coloring') {
				$assestment['img_1'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['img_1'];
				$assestment['img_2'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['img_2'];
				$assestment['img_3'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['img_3'];
				$assestment['img_4'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['img_4'];
				$assestment['img_5'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['img_5'];
				$assestment['img_6'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['img_6'];
			} else if ($type=='eye_tracking_pairs') {
				$assestment['pair_1_img_1'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_1_img_1'];
				$assestment['pair_1_img_2'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_1_img_2'];
				$assestment['pair_2_img_1'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_2_img_1'];
				$assestment['pair_2_img_2'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_2_img_2'];
				$assestment['pair_3_img_1'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_3_img_1'];
				$assestment['pair_3_img_2'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_3_img_2'];
				$assestment['pair_4_img_1'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_4_img_1'];
				$assestment['pair_4_img_2'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_4_img_2'];
				$assestment['pair_5_img_1'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_5_img_1'];
				$assestment['pair_5_img_2'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_5_img_2'];
				$assestment['pair_6_img_1'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_6_img_1'];
				$assestment['pair_6_img_2'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_6_img_2'];
                $assestment['pair_7_img_1'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_7_img_1'];
                $assestment['pair_7_img_2'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_7_img_2'];
                $assestment['pair_8_img_1'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_8_img_1'];
                $assestment['pair_8_img_2'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['pair_8_img_2'];
			} else if ($type=='eye_tracking_slide') {
				$assestment['moving_fragment'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['moving_fragment'];
				$assestment['slide'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['slide'];
			} else if ($type=='motoric_following') {
				$assestment['moving_fragment'] = BaseUrl::base(true).'/'.Assestments::$content_dir.$assestment['moving_fragment'];
			}
			$response = [
				'success' => true,
				'message' => 'Assestment data successfully extracted.',
				'data' => [
					'assestment' => $assestment
				]
			];
		}

		return $response;
	}


	public function actionConsent() { // 2017-10-14
		Yii::$app->response->format = 'json';

		if (!in_array(Yii::$app->request->method, ['GET'])) {
			Yii::$app->response->statusCode = 405;
			Yii::$app->response->headers->set('Allow', 'GET');
			$response['message'] = 'Invalid method used in request.';
			return $response;
		}

		$response = [
			'success' => true,
			'message' => 'Consent form successfully retrieved.',
			'data' => [
				'consent_form' => [
					'english' => file_get_contents(__DIR__ . '/../consent_form_english.txt'),
					'hindi' => file_get_contents(__DIR__ . '/../consent_form_hindi.txt'),
				],
				'isHtml' => true
			]
		];

		return $response;
	}

	public function actionStates() {
		Yii::$app->response->format = 'json';
		/*$response = [
			'success' => false,
			'message' => 'Unknown error.'
		];
	
		$auth_data = $this->check_auth_header();
		if (!$auth_data['success']) {
			Yii::$app->response->statusCode = $auth_data['code'];
			$response = $auth_data;
			unset($response['code']);
			return $response;
		}*/
	
		if (!in_array(Yii::$app->request->method, ['GET'])) {
			Yii::$app->response->statusCode = 405;
			Yii::$app->response->headers->set('Allow', 'GET');
			$response['message'] = 'Invalid method used in request.';
			return $response;
		}

		$states = States::getStatesList();

		$response['success'] = true;
		$response['message'] = 'States successfully retrieved.';
		$response['data'] = [
			'states' => $states,
		];
	
		return $response;
	}

	public function actionLanguages() {
		Yii::$app->response->format = 'json';
		/*$response = [
			'success' => false,
			'message' => 'Unknown error.'
		];
	
		$auth_data = $this->check_auth_header();
		if (!$auth_data['success']) {
			Yii::$app->response->statusCode = $auth_data['code'];
			$response = $auth_data;
			unset($response['code']);
			return $response;
		}*/
	
		if (!in_array(Yii::$app->request->method, ['GET'])) {
			Yii::$app->response->statusCode = 405;
			Yii::$app->response->headers->set('Allow', 'GET');
			$response['message'] = 'Invalid method used in request.';
			return $response;
		}

		$languages = Languages::getLanguagesList();

		$response['success'] = true;
		$response['message'] = 'Languages successfully retrieved.';
		$response['data'] = [
			'languages' => $languages,
		];
	
		return $response;
	}	
}