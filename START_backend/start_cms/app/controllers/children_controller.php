<?php

namespace app\controllers;

use app\helpers\app;
use app\models\children;
use app\models\parents;
use app\models\social_workers;
use app\models\surveys;
use app\models\states;
use tb\start_cms\CMS;
use tb\start_cms\base\controller;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;
//use PHPExcel\PHPExcel\PHPExcel;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class children_controller extends controller {
	/*
		This contoller contains actions to
			get surveys data table,
			preview child and survey info,
			export surveys data,
			edit child personal info.
	*/

	private static $runtime = [];

	public static function action_list() { // 2017-04-27
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_block_children');

		$params = [];

		$page = intval(@$_GET['page']);
		children::$curr_pg = (empty($page)? 1: $page);

		$params['children'] = children::getChildrenDataTable();
		/*if (!empty($params['children']) && is_array($params['children'])) {
			foreach ($params['children'] as $i=>$c) {
				$params['children'][$i]['last_survey'] = surveys::getLastSurveyByChildId($c['id']);
			}
		}*/

		$params['count'] = children::$items_amount;
		$params['total'] = children::$pages_amount;
		$params['current'] = children::$curr_pg;

		$params['canWrite'] = CMS::hasAccessTo('children/list', 'write');
		$params['link_sc'] = utils::trueLink(['controller', 'action', 'q', 'filter']);
		$params['link_return'] = urlencode(SITE.CMS_DIR.utils::trueLink(['controller', 'action', 'q', 'page']));

		$params['states'] = CMS::$db->getList("SELECT name FROM states ORDER BY name");
		$params['statuses'] = children::$allowed_statuses;
		$params['social_workers'] = social_workers::getList();

		// print "<pre>\n".var_export(CMS::$db->queries, 1)."\n\n".var_export(CMS::$db->errors, 1)."\n</pre>";

		return self::render('children_list', $params);
	}

	public static function action_delete() { // 2017-04-27
		self::$layout = 'common_layout';
		view::$title = CMS::t('delete');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('children/delete', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=children&action=list': $_GET['return']);

		$deleted = false;
		if ($params['canWrite']) {
			$child_id = @(string)$_POST['delete'];
			$child = children::getChild($child_id);
			$deleted = children::deleteChild($child_id);

			if ($deleted) {
				CMS::log([
					'subj_table' => 'children',
					'subj_id' => $child_id,
					'action' => 'delete',
					'descr' => 'Child #'.$child_id.' '.$child['name'].' '.$child['surname'].' deleted by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
				]);
			}
		}
		$params['op']['success'] = $deleted;
		$params['op']['message'] = 'del_'.($deleted? 'suc': 'err');

		return self::render('children_delete', $params);
	}

	public static function action_view_info() { // 2017-04-27
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_children_view_info');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('children/view_info', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=children&action=list': $_GET['return']);

		$id = @(int)$_GET['id'];
		$params['child'] = children::getChild($id);

		if (empty($params['child']['id'])) {
			return CMS::resolve('base/404');
		}

		$params['parents'] = parents::getParentsByChildId($id);
		//$params['survey'] = surveys::getLastSurveyByChildId($id);
		$params['survey'] = surveys::getSurveyById(@(int)$_GET['survey_id']);
		$params['states'] = states::getStatesList();

		return self::render('child_info', $params);
	}

	public static function action_survey() { // 2017-07-02
		self::$layout = 'common_layout';
		view::$title = CMS::t('menu_item_children_survey');

		$params = [];

		$params['canWrite'] = CMS::hasAccessTo('children/survey', 'write');
		$params['link_back'] = (empty($_GET['return'])? '?controller=children&action=list': $_GET['return']);

		$id = @(int)$_GET['child_id'];
		//var_dump($id); die("aaa");
		$params['child'] = children::getChild($id);

		if (empty($params['child']['id'])) {
			return CMS::resolve('base/404');
		}

		$params['survey'] = surveys::getSurveyById(@(int)$_GET['survey_id']);

		if (empty($params['survey']['id'])) {
			return CMS::resolve('base/404');
		}

		//CMS::$db->mod('surveys#'.$params['survey']['id'], ['is_inspected' => '1']);
		$touched = surveys::touchSurvey($params['survey']['id']);
		if ($touched) {
			CMS::log([
				'subj_table' => 'surveys',
				'subj_id' => $params['survey']['id'],
				'action' => 'close',
				'descr' => 'Survey #'.$params['survey']['id'].' of child '.$params['child']['name'].' '.$params['child']['surname'].' was viewed by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
			]);
		}

		$params['survey_results'] = [];
		$params['survey_results']['bubbles_jubbing'] = surveys::getSurveyResults($params['survey']['id'], 'bubbles_jubbing');
		$params['survey_results']['choose_touching'] = surveys::getSurveyResults($params['survey']['id'], 'choose_touching');
		$params['survey_results']['wheel'] = surveys::getSurveyResults($params['survey']['id'], 'wheel');
		$params['survey_results']['coloring'] = surveys::getSurveyResults($params['survey']['id'], 'coloring');
		$params['survey_results']['eye_tracking_pairs'] = surveys::getSurveyResults($params['survey']['id'], 'eye_tracking_pairs');
		$params['survey_results']['eye_tracking_slide'] = surveys::getSurveyResults($params['survey']['id'], 'eye_tracking_slide');
		$params['survey_results']['motoric_following'] = surveys::getSurveyResults($params['survey']['id'], 'motoric_following');
		$params['survey_results']['parent_assestment'] = surveys::getSurveyResults($params['survey']['id'], 'parent_assestment');
		$params['survey_results']['parent_child_play'] = surveys::getSurveyResults($params['survey']['id'], 'parent_child_play');

		$params['attachments'] = [];
		$params['attachments']['eye_tracking_pairs'] = surveys::getSurveyAttachments($params['survey']['id'], 'eye_tracking_pairs');
		$params['attachments']['eye_tracking_slide'] = surveys::getSurveyAttachments($params['survey']['id'], 'eye_tracking_slide');
		$params['attachments']['wheel'] = surveys::getSurveyAttachments($params['survey']['id'], 'wheel');
		$params['attachments']['motoric_following'] = surveys::getSurveyAttachments($params['survey']['id'], 'motoric_following');
		$params['attachments']['coloring'] = surveys::getSurveyAttachments($params['survey']['id'], 'coloring');
		$params['attachments']['parent_assestment'] = surveys::getSurveyAttachments($params['survey']['id'], 'parent_assestment');
		$params['attachments']['parent_child_play'] = surveys::getSurveyAttachments($params['survey']['id'], 'parent_child_play');

		// print "<pre>\n".var_export(CMS::$db->queries, 1)."\n\n".var_export(CMS::$db->errors, 1)."\n</pre>";
		// print "<pre>\n".var_export($params, 1)."\n</pre>";

		return self::render('child_survey', $params);
	}

    public static function action_close_survey() { // 2017-07-24
        $params = [];

        $params['canWrite'] = CMS::hasAccessTo('children/close_survey', 'write');
        $params['link_back'] = (empty($_GET['return'])? '?controller=children&action=list': $_GET['return']);

        $closed = false;
        if ($params['canWrite']) {
			$survey_id = @(string)$_GET['survey_id'];
            $closed = surveys::closeSurvey($survey_id);

			if ($closed) {
				$survey = surveys::getSurveyById($survey_id);
				$child = children::getChild($survey['child_id']);
				CMS::log([
					'subj_table' => 'surveys',
					'subj_id' => $survey_id,
					'action' => 'close',
					'descr' => 'Survey #'.$survey_id.' of child '.$child['name'].' '.$child['surname'].' was closed by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
				]);
			}
        }

		utils::redirect($params['link_back']);
        return '';
    }

    public static function action_open_survey() { // 2017-07-25
        $params = [];

        $params['canWrite'] = CMS::hasAccessTo('children/open_survey', 'write');
        $params['link_back'] = (empty($_GET['return'])? '?controller=children&action=list': $_GET['return']);

        $opened = false;
        if ($params['canWrite']) {
			$survey_id = @(string)$_GET['survey_id'];
            $opened = surveys::openSurvey($survey_id);

			if ($opened) {
				$survey = surveys::getSurveyById($survey_id);
				$child = children::getChild($survey['child_id']);
				CMS::log([
					'subj_table' => 'surveys',
					'subj_id' => $survey_id,
					'action' => 'open',
					'descr' => 'Survey #'.$survey_id.' of child '.$child['name'].' '.$child['surname'].' was opened by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
				]);
			}
        }

		utils::redirect($params['link_back']);
        return '';
    }

    public static function action_delete_survey() { // 2017-07-02
        self::$layout = 'common_layout';
        view::$title = CMS::t('delete');

        $params = [];

        $params['canWrite'] = CMS::hasAccessTo('children/delete_survey', 'write');
        $params['link_back'] = (empty($_GET['return'])? '?controller=children&action=list': $_GET['return']);

        $deleted = false;
        if ($params['canWrite']) {
			$survey_id = @(string)$_POST['delete'];
			$survey = surveys::getSurveyById($survey_id);
            $deleted = surveys::deleteSurvey(@$_POST['delete']);

			if ($deleted) {
				$child = children::getChild($survey['child_id']);
				CMS::log([
					'subj_table' => 'surveys',
					'subj_id' => $survey_id,
					'action' => 'delete',
					'descr' => 'Survey #'.$survey_id.' of child '.$child['name'].' '.$child['surname'].' was deleted by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
				]);
			}
        }

        $params['op']['success'] = $deleted;
        $params['op']['message'] = 'del_'.($deleted? 'suc': 'err');

        return self::render('children_delete', $params);
    }

	public static function action_download_child_survey_assestment() { // 2017-07-05
		/*
			Action do download single assessment results and attachments.
			Invokes when CMS user click on download button in assessment block at survey page.
			If there is no attachments it exports results as XLSX file, otherwise it sends ZIP archive.
		*/

		$child_id = @(int)$_GET['child_id'];
		$survey_id = @(int)$_GET['survey_id'];
		$assestment_type = @(string)$_GET['assestment_type'];

		if (empty($child_id) || empty($survey_id) || empty($assestment_type)) {
			return CMS::resolve('base/404');
		}

		$child = children::getChild($child_id);
		$parents = parents::getParentsByChildId($child_id);
		$survey = surveys::getSurveyById($survey_id);

		if (empty($child['id']) || empty($survey['id'])) {
			return CMS::resolve('base/404');
		}

		$assestment_type.=(($assestment_type=='eye_tracking')? '_pairs': '');
		$data = surveys::getSurveyResults($survey_id, $assestment_type);
		$attachments = surveys::getSurveyAttachments($survey_id, $assestment_type, 0);

		if (empty($attachments)) {
			surveys::exportAssestmentResults($assestment_type, $data);
		} else {
			$files = [];
			$temporary_files = [];

			if ($assestment_type=='eye_tracking_pairs') {
				$more_attachments = surveys::getSurveyAttachments($survey_id, 'eye_tracking_slide', 0);
				if (!empty($more_attachments)) {
					$attachments = array_merge($attachments, $more_attachments);
				}
			}
			if (!empty($data)) {
				$requestAssestmentTypes = $assestment_type !== 'eye_tracking_pairs' ? [$assestment_type] : [
					'eye_tracking_pairs', 'eye_tracking_slide'
				];

				foreach($requestAssestmentTypes as $requestAssestmentType) {
                    $fileName = $requestAssestmentType;

                    // hack
                    if ($requestAssestmentType === 'eye_tracking_pairs') {
                        $fileName = 'eye_tracking_preferential_looking_test';
                    }
                    if ($requestAssestmentType === 'eye_tracking_slide') {
                        $data = surveys::getSurveyResults($survey_id, $requestAssestmentType);
                        $fileName = 'eye_tracking_attention_disengagement_test';
                    }

					$fpath = surveys::exportAssestmentResults($requestAssestmentType, $data, 'child_'.$child_id.'_survey_'.$survey_id.'_'.$fileName.'_results_'.time().'.xlsx');
					$files[] = $fpath;
					$temporary_files[] = $fpath;
				}
			}
			if (!empty($attachments)) {
				foreach ($attachments as $i=>$a) {
					$files[] = $a['attachment_file'];
				}
			}

			$zip = new \ZipArchive();
			$zip_name = 'child_'.$child_id.'_survey_'.$survey_id.'_results_'.time().'.zip';
			$zip_path = sys_get_temp_dir() . '/' . $zip_name;
			
			if ($zip->open($zip_path, \ZipArchive::CREATE)===true) {
				foreach ($files as $f) {
					$zip->addFile($f, pathinfo($f, PATHINFO_BASENAME));
				}

				self::add_word_file_to_zip($zip, $parents);

				$zip->close();

				foreach ($temporary_files as $f) { // cleanup temporary files
					unlink($f);
				}
            }

			header('Content-Type: application/octet-stream');
			header('Content-Disposition: attachment; filename='.$zip_name);
			header('Content-Transfer-Encoding: binary');

			readfile($zip_path);
			unlink($zip_path);

			die();
		}

		return '';
	}

	public static function action_download_child_survey() { // 2017-07-05
		/*
			Action do download whole survey (all assessments) results and attachments.
			Invokes when CMS user click on download button under child photo at survey page.
			Always sends ZIP archive.
		*/

		$child_id = @(int)$_GET['child_id'];
		$survey_id = @(int)$_GET['survey_id'];

		if (empty($child_id) || empty($survey_id)) {
			return CMS::resolve('base/404');
		}

		$child = children::getChild($child_id);
		$parents = parents::getParentsByChildId($child_id);
		$survey = surveys::getSurveyById($survey_id);

		if (empty($child['id']) || empty($survey['id'])) {
			return CMS::resolve('base/404');
		}

		CMS::log([
			'subj_table' => 'surveys',
			'subj_id' => $survey_id,
			'action' => 'download',
			'descr' => 'Survey #'.$survey_id.' of child '.$child['name'].' '.$child['surname'].' was downloaded by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
		]);

		$assestment_types = ['bubbles_jubbing', 'choose_touching', 'wheel', 'coloring', 'eye_tracking_pairs', 'eye_tracking_slide', 'motoric_following', 'parent_assestment', 'parent_child_play'];

		$files = [];
		$temporary_files = [];
		foreach ($assestment_types as $type) {
			$data = surveys::getSurveyResults($survey_id, $type);
			$attachments = surveys::getSurveyAttachments($survey_id, $type, 0);
			if (empty($attachments)) {
				$attachments = [];
			}
			if ($type=='eye_tracking_pairs') {
				$more_attachments = surveys::getSurveyAttachments($survey_id, 'eye_tracking_slide', 0);
				if (!empty($more_attachments)) {
					$attachments = array_merge($attachments, $more_attachments);
				}
            }
            
            $fileName = $type;
            // hack
            if ($type === 'eye_tracking_pairs') {
                $fileName = 'eye_tracking_preferential_looking_test';
            }
            if ($type === 'eye_tracking_slide') {
                $fileName = 'eye_tracking_attention_disengagement_test';
            }

			if (!empty($data)) {
                $fpath = surveys::exportAssestmentResults($type, $data, 'child_'.$child_id.'_survey_'.$survey_id.'_'.$fileName.'_results_'.time().'.xlsx');
                $files[] = $fpath;
                $temporary_files[] = $fpath;
			}
			if (!empty($attachments)) {
				foreach ($attachments as $i=>$a) {
					$files[] = $a['attachment_file'];
				}
			}
		}

		$zip = new \ZipArchive();
		$zip_name = 'child_'.$child_id.'_survey_'.$survey_id.'_results_'.time().'.zip';
		$zip_path = sys_get_temp_dir() . '/' . $zip_name;

		if ($zip->open($zip_path, \ZipArchive::CREATE)===true) {
			foreach ($files as $f) {
				$zip->addFile($f, pathinfo($f, PATHINFO_BASENAME));
			}

			self::add_word_file_to_zip($zip, $parents);

			$zip->close();

			foreach ($temporary_files as $f) { // cleanup temporary files
				unlink($f);
			}
		}

		header('Content-Type: application/octet-stream');
		header('Content-Disposition: attachment; filename='.$zip_name);
		header('Content-Transfer-Encoding: binary');

		readfile($zip_path);
		unlink($zip_path);

		die();

		return '';
	}

	private static function add_word_file_to_zip($zip, $parents) {
		$forms = [
			'english' => [
				'text' => @file_get_contents(__DIR__ . '/../../../consent_form_english.txt'),
				'disclamer' => 'The procedures have been explained clearly to us, and we have had opportunities to ask questions. Consequently, we are happy to provide consent for our child to take part in this study.'
			],
			/*'hindi' => [
				'text' => @file_get_contents(__DIR__ . '/../../../consent_form_hindi.txt'),
				'disclamer' => 'इस प्रोजेक्ट के बारे में हमें स्पष्ट रूप से समझा दिया गया है और हमें प्रश्न पूछने के अवसर मिल चुके हैं।  हम इस प्रोजेक्ट में भाग लेने के लिए सहमत हैं'
			]*/
		];
		
		foreach($forms as $lang => $form) {
			$phpWord = new \PhpOffice\PhpWord\PhpWord();
			$objWriter = \PhpOffice\PhpWord\IOFactory::createWriter($phpWord, 'Word2007');
				
			$section = $phpWord->addSection();
			
			\PhpOffice\PhpWord\Shared\Html::addHtml($section, $form['text']);
			
			$section->addText("\n\n");

			$section->addText($form['disclamer']);

			if ($_SESSION[CMS::$sess_hash]['ses_adm_type'] !=='researcher') {
				foreach ($parents as $parent) {
					if (!empty($parent['signature']) && is_file($signature = UPLOADS_DIR . parents::$signatures_dir . $parent['signature'])) {
						$section->addImage($signature, [
							'width' => 100,
							'height' => 100,
						]);
					}
				}
			}
			
			$tempFile = tempnam(sys_get_temp_dir(), 'start' . $lang);
			$objWriter->save($tempFile);

			$zip->addFile($tempFile, sprintf('signature_parent_%s.docx', $lang));
		}
	}

	public static function action_download_surveys() { // 2017-07-25
		/*
			Action do mass-download selected surveys.
			Invokes when at surveys listing page CMS user checks surveys and assessment types and pushes Download button.
			Always sends ZIP archive of ZIP archives.
		*/

		$surveys = @$_POST['surveys_selected'];

		if (empty($surveys) || !is_array($surveys)) {
			return CMS::resolve('base/404');
		}

		CMS::log([
			'subj_table' => 'surveys',
			'subj_id' => $surveys[0],
			'action' => 'mass_download',
			'descr' => 'Surveys #'.implode(', #', $surveys).' are mass downloaded by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
		]);

		$zip = new \ZipArchive();
		$zip_name = 'multiple_surveys_'.utils::getuniqid().'.zip';
		$zip_path = sys_get_temp_dir() . '/' . $zip_name;

		$files = [];

		if ($zip->open($zip_path, \ZipArchive::CREATE)===true) {
			foreach ($surveys as $s) {
				$f = surveys::exportSurvey($s, 1);
				$files[] = $f;
				$zip->addFile($f, pathinfo($f, PATHINFO_BASENAME));
			}

			$zip->close();

			foreach ($files as $f) { // cleanup temporary files
				unlink($f);
			}
		}

		header('Content-Type: application/octet-stream');
		header('Content-Disposition: attachment; filename='.$zip_name);
		header('Content-Transfer-Encoding: binary');

		readfile($zip_path);
		unlink($zip_path);

		die();

		return '';
	}

	public static function action_download_child_photo() { // 2017-04-27
		$child_id = @(int)$_GET['child_id'];
		$size = @(string)$_GET['size'];
		if (!in_array($size, ['thumb', 'preview', 'original'])) {
			$size = 'thumb';
		}
		$child = children::getChild($child_id);

		if (empty($child['id']) || empty($child['photo']) || !is_file(UPLOADS_DIR.children::$thumbs_dir.$child['photo'])) {
			return CMS::resolve('base/404');
		}

		$fname = UPLOADS_DIR.children::$thumbs_dir.$child['photo'];
		header('Content-Type: '.mime_content_type($fname));
		header('Content-Disposition: attachment; filename='.$child['photo']);
		header('Content-Length: '.filesize($fname));
		header('Content-Transfer-Encoding: binary');

		readfile($fname);

		return '';
	}

    public static function action_edit() { // 2017-07-25
		header('Content-type: application/json; charset=utf-8');

		$response = ['success' => false, 'message' => 'ajax_invalid_request'];

		if (utils::isAjax()) {
			$child_id = @(string)$_GET['child_id'];
			$allowed_fields = ['name', 'surname', 'state', 'address', 'gender', 'birth_date', 'hand'];
			$upd = utils::array_filter_keys($_POST, $allowed_fields);
			$upd['gender'] = ((@$upd['gender']=='Female')? 'F': 'M');
			$upd['birth_date'] = utils::formatPlainDate('Y-m-d', @$upd['birth_date']);

			$state = states::getById($upd['state']);
			$upd['state'] = $state['name'];

			$saved = CMS::$db->mod('children#'.$child_id, [
				'encrypted_fields' => $allowed_fields,
				'data' => $upd
			]);

			if ($saved) {
				CMS::log([
					'subj_table' => 'children',
					'subj_id' => $child_id,
					'action' => 'edit',
					'descr' => 'Child '.$upd['name'].' '.$upd['surname'].' was edited by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
				]);

				$response['success'] = $saved;
				$response['message'] = CMS::t('update_suc');
			} else {
				$response['success'] = false;
				$response['message'] = 'No changes.';
			}
		}

        return json_encode($response);
    }
}

?>