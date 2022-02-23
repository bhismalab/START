<?php

namespace app\controllers;

use Yii;
use yii\helpers\Url;
use app\models\SocialWorkers;
use yii\web\Controller;
use yii\web\UrlManager;

use yii\helpers\Utils;
require_once Yii::getAlias('@app').'/helpers/utils.php';

class TestApiController extends Controller {

	private $log = [];
	private $auth_token;

	// methods

	public function actionRun() { // 2017-04-23 / Start functional tests
		//print "<p>".utils::changeDateFormat('c', 'Y-m-d H:i:s', '2017-08-22T11:00:00+04:00')."</p>";
		//print "<p>".utils::changeDateFormat('c', 'Y-m-d H:i:s', '2017-08-22T12:30:00+05:30')."</p>";

		$this->Test_Login_Available();
		$this->Test_Login_Successful();
		//$this->Test_Login_Blocked();

		//$this->Test_CheckAuthToken_Valid();
		//$this->Test_CheckAuthToken_Invalid();

		//$this->Test_Children_GET_List();
		//$this->Test_Children_GET_ListSince();
		//$this->Test_Children_GET_Unauthorized();

		//$this->Test_Children_POST_Addnew();
		//$this->Test_Children_PUT_ChangeState();

		//$this->Test_PasswordRecovery_Available();
		//$this->Test_PasswordRecovery_Successful();
		//$this->Test_SetPassword_InvalidHash();
		//print "-".md5(md5('3'.'irrevion@gmail').md5('123456789'))."-"; // ba2c35604f44433b5092c9d80019cdd6
		//$this->Test_SetPassword_Successful();
		//$this->Test_SetPassword_SuccessfulRevert();

		//$this->Test_Survey_POST_AddNewFail();
		//$this->Test_Survey_POST_AddNewSuccess();

		//$this->Test_Surveys_PUT_SetComplete();

		//$this->Test_Assestments_GET_BubbleJubbing();
		//$this->Test_Assestments_GET_BubbleJubbingSince();
		//$this->Test_Assestments_GET_ChooseTouching();
		//$this->Test_Assestments_GET_Wheel();
		//$this->Test_Assestments_GET_Coloring();
		//$this->Test_Assestments_GET_EyeTrackingPairs();
		//$this->Test_Assestments_GET_EyeTrackingSlide();
		//$this->Test_Assestments_GET_MotoricFollowing();
		//$this->Test_Assestments_GET_ParentQuestions();
		$this->Test_Assestments_GET_ParentChildPlay();

		//$this->Test_SurveyAttachments_POST_AddNewSuccess();
		//$this->Test_SurveyResults_POST_AddNewSuccess();

		//$this->Test_Surveys_GET_Attachment();
		//$this->Test_Surveys_GET_Result();

		///$this->Test_ChildrenSurveys_GET_List();

		return "<pre>".var_export($this->log, 1)."</pre>";
	}

	private function Test_Login_Available() { // 2017-04-23 / Test if login action defined
		$url = Url::to(['api/login'], 1);
		$params = [
			'email' => 'no email',
			'password' => 'no password',
		];

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_POST, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'Content-type: application/json'
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = (array)json_decode($response_json);
		if (($request_info['http_code']=='404') && ($response_data['message']=='Invalid username or password of social worker.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Login_Available '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Login_Available';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Login_Successful() { // 2017-04-23 / Test if user with correct password gets JWT token
		$url = Url::to(['api/login'], 1);
		$params = [
			'email' => 'social1',
			'password' => '123456',
			//'email' => 'amrit.naik@gmail.com',
			//'password' => '11111111',
		];

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_POST, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'Content-type: application/json'
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Successfully logged in.') && !empty($response_data['data']['jwt_token'])) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Login_Successful '.$request_info['http_code'].' '.$response_json;
			$this->auth_token = $response_data['data']['jwt_token'];
		} else {
			$this->log[] = '[FAILURE] Test_Login_Successful';
			$this->log[] = 'Sended request '.json_encode($params);
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Login_Blocked() { // 2017-04-23 / Test if user with correct password cannot authenticate and receives proper error message
		$url = Url::to(['api/login'], 1);
		$params = [
			'email' => 'qurban.qurbanov93@gmail.com',
			'password' => 'wt46e9dv',
		];

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_POST, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'Content-type: application/json'
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = (array)json_decode($response_json);
		if (($request_info['http_code']=='403') && ($response_data['message']=='This account is blocked.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Login_Blocked '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Login_Blocked';
			$this->log[] = 'Sended request '.json_encode($params);
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_CheckAuthToken_Valid() { // 2017-04-25 / Test if correct X-Auth-Token detects properly
		$url = Url::to(['api/check-auth-token'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Auth token is valid.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_CheckAuthToken_Valid '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_CheckAuthToken_Valid';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_CheckAuthToken_Invalid() { // 2017-04-25 / Test if incorrect (fake) X-Auth-Token detects properly
		$url = Url::to(['api/check-auth-token'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: fake_token'
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='403') && ($response_data['message']=='Token is invalid.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_CheckAuthToken_Invalid '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_CheckAuthToken_Invalid';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Children_GET_List() { // 2017-04-28 / Try to get list of children
		$url = Url::to(['api/children'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		//echo "<pre>", var_dump($response_data);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Chilren list successfully extracted.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Children_GET_List '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Children_GET_List';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Children_GET_ListSince() { // 2017-05-02 / Try to get list of children since defined time
		$url = Url::to(['api/children', 'since_datetime' => '2017-08-22T11:28:00+04:00'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Chilren list successfully extracted.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Children_GET_ListSince ('.$url.') '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Children_GET_ListSince';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Children_GET_Unauthorized() { // 2017-04-28 / Try to get list of children with unauthorized user
		$url = Url::to(['api/children'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: fake_token'
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='403') && ($response_data['message']=='Token is invalid.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Children_GET_Unauthorized '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Children_GET_Unauthorized';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	/*private function Test_Children_PUT_InvalidMethod() { // 2017-04-28 / Try to get list of children with unauthorized user
		$url = Url::to(['api/children'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_PUT, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='405') && ($response_data['message']=='Invalid method used in request.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Children_PUT_InvalidMethod '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Children_PUT_InvalidMethod';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}*/

	private function Test_Children_PUT_ChangeState() { // 2017-06-12 / Change country state of a child
		$url = Url::to(['api/children/212'], 1);

		$params = [
			/*'surname' => 'खूबानी',
			'name' => 'मकई',
			'patronymic' => 'बेर',
			'gender' => 'M',
			'birth_date' => '2009-07-13',
			'state' => 'Manipur',
			'address' => 'Imphal, Haokip Veng, 13/24',
			'state' => 'Manipur',*/
			'diagnosis' => 'ASD',
			'diagnosis_clinic' => 'dr.Beytiyev',
			'diagnosis_datetime' => '1999-09-09T09:09:09+05:30',
			'hand' => 'ambidexter',
			'parents' => [
				[
					'child_relationship' => 'guardian',
					'name' => 'सफरचंद',
					'surname' => 'नारिंगी',
					'state' => 'महाराष्ट्र',
					'address' => '1, सोलापूर - औरंगाबाद Hwy सहारा कॉलनी, स्वराज नगर, बीड, महाराष्ट्र 431122',
					'gender' => 'M',
					'birth_date' => '1951-11-04',
					'preferable_contact' => 'Post',
					'spoken_language' => 'मराठी',
					'phone' => '+123 (53) 574 82 13',
					'email' => 'eskimos@gmail.com',
					'signature_scan' => utils::file2base64('uploads/samples/signature_scan.gif'),
				],
				/*[
					'child_relationship' => 'parent',
					'name' => 'माकड',
					'surname' => 'सिंह',
					'state' => 'राजपुत्राने',
					'address' => 'павыиавмфвыфмсвымавс',
					'gender' => 'M',
					'birth_date' => '1954-07-21',
					'spoken_language' => 'मराठी',
					'phone' => '+123 (53) 574 82 13',
					'email' => 'impala@gmail.com',
					//'signature_scan' => utils::file2base64('uploads/samples/signature_scan.gif'),
				]*/
			]
		];

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_CUSTOMREQUEST, 'PUT');
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token,
			'Content-type: application/json'
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Child info successfully updated.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Children_PUT_ChangeState '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Children_PUT_ChangeState';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Children_POST_Addnew() { // 2017-05-02 / Try to add new child
		$url = Url::to(['api/children'], 1);

		/*$mother = [
			'child_relationship' => 'parent',
			'name' => 'Rama',
			'surname' => 'Soni',
			'state' => 'Telangana',
			'address' => '480, 3-2-836/2, Kachiguda Station Road Mahalaxmi Nilayam, Kachiguda, Mahalaxmi Nilayam, Kachiguda, Hyderabad, Telangana 500027',
			'gender' => 'F',
			'birth_date' => '1983-09-03',
			'spoken_language' => 'Meiteilon',
			'phone' => '+123 (44) 567 88 99',
			'email' => 'sima.doshi83@gmail.com',
			'signature_scan' => utils::file2base64('uploads/samples/sign-scan.jpg'),
		];
		$father = [
			'child_relationship' => 'guardian',
			'name' => 'Gaurav',
			'surname' => 'Soni',
			'state' => 'Maharashtra',
			'address' => '1, Solapur - Aurangabad Hwy, Sahara Colony, Swaraj Nagar, Beed, Maharashtra 431122',
			'gender' => 'M',
			'birth_date' => '1971-11-24',
			'spoken_language' => 'Marathi',
			'phone' => '+123 (53) 574 82 13',
			'email' => 'gaurav.soni71@gmail.com',
			'signature_scan' => utils::file2base64('uploads/samples/signature_scan.gif'),
		];
		$child = [
			'photo' => utils::file2base64('uploads/samples/dims.jpg'),
			'name' => 'Sima',
			'surname' => 'Soni',
			'state' => 'Tamil Nadu',
			'address' => 'Andanallur, Tiruchirappalli, Tamil Nadu 639101',
			'gender' => 'F',
			'birth_date' => '2010-01-17',
			'latitude' => '10.8746456',
			'longitude' => '78.6020387',
			'diagnosis' => 'Я абсолютно здоров и счастлив! Душа моя переполняется радостью как эти чудесные утра...',
			'diagnosis_clinic' => 'Мемориальный онкологический центр им. Слоуна-Кеттеринга',
			'diagnosis_datetime' => '2017-08-04 17:37:12',
			'parents' => [$mother, $father]
		];*/

		$mother = [
			'child_relationship' => 'parent',
			'name' => 'Amonet',
			'surname' => 'Osiris',
			'state' => 'Telangana',
			'address' => '480, 3-2-836/2, Kachiguda Station Road Mahalaxmi Nilayam, Kachiguda, Mahalaxmi Nilayam, Kachiguda, Hyderabad, Telangana 500027',
			'gender' => 'F',
			'birth_date' => '1987-03-13',
			'spoken_language' => 'Meiteilon',
			'phone' => '+123 (44) 567 88 99',
			'email' => 'amanet.osiris@gmail.com'
		];
		$father = [
			'child_relationship' => 'guardian',
			'name' => 'Gaurav',
			'surname' => 'Soni',
			'state' => 'Maharashtra',
			'address' => '1, Solapur - Aurangabad Hwy, Sahara Colony, Swaraj Nagar, Beed, Maharashtra 431122',
			'gender' => 'M',
			'birth_date' => '1971-11-24',
			'spoken_language' => 'Marathi',
			'phone' => '+123 (53) 574 82 13',
			'email' => 'gaurav.soni71@gmail.com',
			'signature_scan' => utils::file2base64('uploads/samples/signature_scan.gif'),
		];
		$child = [
			'photo' => utils::file2base64('uploads/samples/pakistani.jpg'),
			'name' => 'Anaksunamun',
			'surname' => 'Osiris',
			'state' => 'Egipto',
			'address' => 'Giza valley, Piramide #7',
			'gender' => 'F',
			'birth_date' => '2010-04-07',
			'latitude' => '29.388528',
			'longitude' => '31.157172',
			'diagnosis' => 'Я абсолютно здоров и счастлив! Душа моя переполняется радостью как эти чудесные утра...',
			'diagnosis_clinic' => 'Мемориальный онкологический центр им. Слоуна-Кеттеринга',
			'diagnosis_datetime' => '2017-08-04 17:37:12',
			'parents' => [$mother, $father]
		];

		$params = $child;

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_POST, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token,
			'Content-type: application/json'
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Child successfully created.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Children_POST_Addnew '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Children_POST_Addnew';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_PasswordRecovery_Available() { // 2017-05-24
		$url = Url::to(['api/password-recovery'], 1);
		$params = [
			'email' => 'no email',
			'password' => 'no password',
		];

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_POST, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'Content-type: application/json'
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = (array)json_decode($response_json);
		if (($request_info['http_code']=='404') && ($response_data['message']=='Social worker is not registered or is deleted.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_PasswordRecovery_Available '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_PasswordRecovery_Available';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_PasswordRecovery_Successful() { // 2017-05-24
		$url = Url::to(['api/password-recovery'], 1);
		$params = [
			'email' => 'irrevion@gmail.com',
			'birth_date' => '1986-03-28',
		];

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_POST, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'Content-type: application/json'
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Password recovery hash successfully generated.') && !empty($response_data['data']['security_hash'])) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_PasswordRecovery_Successful '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_PasswordRecovery_Successful';
			$this->log[] = 'Sended request '.json_encode($params);
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_SetPassword_InvalidHash() { // 2017-05-25
		$url = Url::to(['api/social-workers/3'], 1);
		$params = [
			'password' => 'test',
			'security_hash' => 'invalid', //'1a0050e2ca8568e26e459b32c09410c3',
		];

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_CUSTOMREQUEST, 'PUT');
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'Content-type: application/json'
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='403') && ($response_data['message']=='Security hash is invalid or obsolete.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_SetPassword_InvalidHash '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_SetPassword_InvalidHash';
			$this->log[] = 'Sended request '.json_encode($params);
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_SetPassword_Successful() { // 2017-05-25
		$url = Url::to(['api/social-workers/3'], 1);
		$sw = SocialWorkers::getSocialWorkerById(3);
		$params = [
			'password' => '123456789',
			'security_hash' => md5(md5('3'.'irrevion@gmail.com').md5($sw['password'])),
		];

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_CUSTOMREQUEST, 'PUT');
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'Content-type: application/json'
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Password successfully updated.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_SetPassword_Successful '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_SetPassword_Successful';
			$this->log[] = 'Sended request '.json_encode($params);
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_SetPassword_SuccessfulRevert() { // 2017-05-25
		$url = Url::to(['api/social-workers/3'], 1);
		$sw = SocialWorkers::getSocialWorkerById(3);
		$params = [
			'password' => 'x37_0zF',
			'security_hash' => md5(md5('3'.'irrevion@gmail.com').md5($sw['password']))
		];

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_CUSTOMREQUEST, 'PUT');
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'Content-type: application/json'
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Password successfully updated.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_SetPassword_SuccessfulRevert '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_SetPassword_SuccessfulRevert';
			$this->log[] = 'Sended request '.json_encode($params);
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Survey_POST_Addnew() { // 2017-06-12 / Try to init survey for child
		$url = Url::to(['api/surveys'], 1);

		$survey = [
			'child_id' => '5',
			'created_datetime' => date('c')
		];
		$params = $survey;

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_POST, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token,
			'Content-type: application/json'
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Survey successfully initialized.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Survey_POST_Addnew '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Survey_POST_Addnew';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Survey_POST_AddNewFail() { // 2017-06-12 / Try to init survey for child of another social worker
		$url = Url::to(['api/surveys'], 1);

		$survey = [
			'child_id' => '5',
			'created_datetime' => date('c')
		];
		$params = $survey;

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_POST, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token,
			'Content-type: application/json'
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='404') && ($response_data['message']=='Child not found.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Survey_POST_AddNewFail '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Survey_POST_AddNewFail';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Survey_POST_AddNewSuccess() { // 2017-06-12 / Try to init survey for child
		$url = Url::to(['api/surveys'], 1);

		$survey = [
			'child_id' => '1',
			'created_datetime' => date('c')
		];
		$params = $survey;

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_POST, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token,
			'Content-type: application/json'
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Survey successfully initialized.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Survey_POST_AddNewSuccess '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Survey_POST_AddNewSuccess';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Surveys_PUT_SetComplete() { // 2017-06-12 / Complete survey for child
		$url = Url::to(['api/surveys/5'], 1);

		$params = [
			'completed_datetime' => date('c')
		];

		$c = curl_init($url);
		//curl_setopt($c, CURLOPT_PUT, true);
		curl_setopt($c, CURLOPT_CUSTOMREQUEST, 'PUT');
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token,
			'Content-type: application/json'
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Survey successfully updated.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Surveys_PUT_SetComplete '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Surveys_PUT_SetComplete';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Assestments_GET_BubbleJubbing() { // 2017-06-14 / Try to get content of bubble jubbing assestment
		$url = Url::to(['api/assestments/bubbles_jubbing'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Assestment data successfully extracted.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Assestments_GET_BubbleJubbing '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Assestments_GET_BubbleJubbing';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Assestments_GET_BubbleJubbingSince() { // 2017-06-14 / Try to get content of bubble jubbing assestment
		//$url = Url::to(['api/assestments/bubbles_jubbing', 'since_datetime' => '2017-04-27T12:51:19+05:30'], 1);
		$url = Url::to(['api/assestments/bubbles_jubbing', 'since_datetime' => '2017-05-29T19:38:36+05:30'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='404') && ($response_data['message']=='Assestment content is not found.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Assestments_GET_BubbleJubbingSince '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Assestments_GET_BubbleJubbingSince';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Assestments_GET_ChooseTouching() { // 2017-06-14 / Try to get content of choose touching assestment
		$url = Url::to(['api/assestments/choose_touching'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Assestment data successfully extracted.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Assestments_GET_ChooseTouching '.$request_info['http_code'].' '.$response_json;
			//$inf = json_decode($response_json, 1);
			//utils::base64_to_file($inf['data']['assestment']['video_1_src'], 'uploads/');
		} else {
			$this->log[] = '[FAILURE] Test_Assestments_GET_ChooseTouching';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Assestments_GET_Wheel() { // 2017-09-07 / Try to get content of wheel assestment
		$url = Url::to(['api/assestments/wheel'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Assestment data successfully extracted.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Assestments_GET_ChooseTouching '.$request_info['http_code'].' '.$response_json;
			//$inf = json_decode($response_json, 1);
			//utils::base64_to_file($inf['data']['assestment']['video_1_src'], 'uploads/');
		} else {
			$this->log[] = '[FAILURE] Test_Assestments_GET_ChooseTouching';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Assestments_GET_Coloring() { // 2017-06-15 / Try to get content of coloring assestment
		$url = Url::to(['api/assestments/coloring'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Assestment data successfully extracted.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Assestments_GET_Coloring '.$request_info['http_code'].' '.$response_json;
			//$inf = json_decode($response_json, 1);
			//utils::base64_to_file($inf['data']['assestment']['img_1_src'], 'uploads/');
		} else {
			$this->log[] = '[FAILURE] Test_Assestments_GET_Coloring';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Assestments_GET_EyeTrackingPairs() { // 2017-06-15 / Try to get content of eye tracking assestment with 5 pairs of images
		$url = Url::to(['api/assestments/eye_tracking_pairs'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Assestment data successfully extracted.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Assestments_GET_EyeTrackingPairs '.$request_info['http_code'].' '.$response_json;
			//$inf = json_decode($response_json, 1);
			//utils::base64_to_file($inf['data']['assestment']['pair_5_src'], 'uploads/');
		} else {
			$this->log[] = '[FAILURE] Test_Assestments_GET_EyeTrackingPairs';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Assestments_GET_EyeTrackingSlide() { // 2017-06-15 / Try to get content of eye tracking assestment with moving slide
		$url = Url::to(['api/assestments/eye_tracking_slide'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Assestment data successfully extracted.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Assestments_GET_EyeTrackingSlide '.$request_info['http_code'].' '.$response_json;
			//$inf = json_decode($response_json, 1);
			//utils::base64_to_file($inf['data']['assestment']['moving_fragment_src'], 'uploads/');
		} else {
			$this->log[] = '[FAILURE] Test_Assestments_GET_EyeTrackingSlide';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Assestments_GET_MotoricFollowing() { // 2017-06-15 / Try to get content of eye tracking assestment with moving slide
		$url = Url::to(['api/assestments/motoric_following'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Assestment data successfully extracted.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Assestments_GET_MotoricFollowing '.$request_info['http_code'].' '.$response_json;
			//$inf = json_decode($response_json, 1);
			//utils::base64_to_file($inf['data']['assestment']['moving_fragment_src'], 'uploads/');
		} else {
			$this->log[] = '[FAILURE] Test_Assestments_GET_MotoricFollowing';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Assestments_GET_ParentChildPlay() { // 2017-07-12 / Try to get instructions for parent-child play
		$url = Url::to(['api/assestments/parent_child_play'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Assestment data successfully extracted.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Assestments_GET_ParentChildPlay '.$request_info['http_code'].' '.$response_json;
			//$inf = json_decode($response_json, 1);
			//utils::base64_to_file($inf['data']['assestment']['moving_fragment_src'], 'uploads/');
		} else {
			$this->log[] = '[FAILURE] Test_Assestments_GET_ParentChildPlay';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Assestments_GET_ParentQuestions() { // 2017-06-22 / Try to get parent questions
		$url = Url::to(['api/assestments/parent'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Assestment data successfully extracted.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Assestments_GET_ParentQuestions '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Assestments_GET_ParentQuestions';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_SurveyAttachments_POST_AddNewSuccess() { // 2017-06-16 / Try to save survey attachment
		$url = Url::to(['api/surveys/339/attachments'], 1);

		$params = [
			'assestment_type' => 'wheel',
			'survey_id' => '265',
			//'file_name' => 'sisters-running.mp4',
			//'file_content' => utils::file2base64('uploads/samples/sisters-running.mp4')
		];

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_POST, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_POSTFIELDS, [
			'data' => json_encode($params),
			'file' => new \CurlFile(realpath('uploads/samples/sisters-running.mp4'))
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);

		if (($request_info['http_code']=='200') && ($response_data['message']=='Attachment successfully saved.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_SurveyAttachments_POST_AddNewSuccess '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_SurveyAttachments_POST_AddNewSuccess';
			$this->log[] = 'X-Auth-Token: '.$this->auth_token;
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_SurveyResults_POST_AddNewSuccess() { // 2017-09-07 / Try to save survey result
		$url = Url::to(['api/surveys/339/results'], 1);

		$params = [
			'assestment_type' => 'wheel',
			'survey_id' => '265',
			'file_name' => 'wheel2.json',
			'file_content' => utils::file2base64('uploads/samples/wheel.json')
		];

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_POST, true);
		//curl_setopt($c, CURLOPT_SAFE_UPLOAD, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token,
			'Content-type: application/json'
		]);
		/*curl_setopt($c, CURLOPT_POSTFIELDS, [
			'data' => json_encode($params),
			'file' => new \CurlFile(realpath('uploads/samples/wheel.json'))
		]);*/
		//print file_get_contents('uploads/samples/wheel.json');
		curl_setopt($c, CURLOPT_POSTFIELDS, json_encode($params));
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);

		if (($request_info['http_code']=='200') && ($response_data['message']=='Results file successfully saved.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_SurveyResults_POST_AddNewSuccess '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_SurveyResults_POST_AddNewSuccess';
			$this->log[] = 'X-Auth-Token: '.$this->auth_token;
			$this->log[] = 'CURL error: '.curl_error($c);
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Surveys_GET_Attachment() { // 2017-06-22 / get survey's attachment
		//$url = Url::to(['api/surveys/257/attachments/eye_tracking_pairs'], 1);
		//$url = Url::to(['api/surveys/255/attachments/motoric_following'], 1);
		//$url = Url::to(['api/surveys/252/attachments/coloring'], 1);
		//$url = Url::to(['api/surveys/271/attachments/coloring'], 1);
		//$url = Url::to(['api/surveys/277/attachments/parent_child_play'], 1);
		$url = Url::to(['api/surveys/339/attachments/wheel'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Survey`s attachment successfully retrieved.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Surveys_GET_Attachment '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Surveys_GET_Attachment';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_Surveys_GET_Result() { // 2017-06-22 / get survey's results
		//$url = Url::to(['api/surveys/323/results/choose_touching'], 1);
		//$url = Url::to(['api/surveys/271/results/coloring'], 1);
		$url = Url::to(['api/surveys/339/results/wheel'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Survey`s results successfully retrieved.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_Surveys_GET_Result '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_Surveys_GET_Result';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}

	private function Test_ChildrenSurveys_GET_List() { // 2017-05-02 / Try to get list of children's surveys
		$url = Url::to(['api/children/11/surveys'], 1);

		$c = curl_init($url);
		curl_setopt($c, CURLOPT_HTTPGET, true);
		curl_setopt($c, CURLOPT_HTTPHEADER, [
			'X-Auth-Token: '.$this->auth_token
		]);
		curl_setopt($c, CURLOPT_RETURNTRANSFER, true);
		$response_json = curl_exec($c);
		$request_info = curl_getinfo($c);

		$response_data = json_decode($response_json, 1);
		if (($request_info['http_code']=='200') && ($response_data['message']=='Surveys listing successfully produced.')) {
			$this->log[] = '[SUCCESSFULLY PASSED] Test_ChildrenSurveys_GET_List '.$request_info['http_code'].' '.$response_json;
		} else {
			$this->log[] = '[FAILURE] Test_ChildrenSurveys_GET_List';
			$this->log[] = 'Received response '.$response_json;
			$this->log[] = 'Request info:';
			$this->log[] = $request_info;
			$this->log[] = '- - - - - - -';
		}
	}
}