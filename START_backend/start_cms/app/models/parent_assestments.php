<?php

namespace app\models;

use abeautifulsite\simple_image\SimpleImage;
use tb\start_cms\CMS;
use tb\start_cms\helpers\security;
use tb\start_cms\helpers\utils;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class parent_assestments {
    public static $curr_pg = 1;
    public static $pp = 10;
    public static $pages_amount = 0;
    public static $items_amount = 0;

    public static $tbl = 'content_parent_assestment_tests';
    public static $avatar_size = ['width' => 256, 'height' => 256];
    public static $allowed_thumb_ext = ['jpg', 'jpeg', 'jpe', 'png', 'bmp', 'gif'];
    public static $allowed_video_ext = ['mp4'/*, 'flv', 'mov'*/];

    public static function getQueList() {
        $list = [];

        $where = [];

        if (!empty($_GET['q'])) {
            $where[] = "(
				title LIKE '%".utils::makeSearchable($_GET['q'])."%' OR
				question_text LIKE '%".utils::makeSearchable($_GET['q'])."%' OR
				question_text_hindi LIKE '%".utils::makeSearchable($_GET['q'])."%'
			)";
        }

        if (in_array(@$_GET['filter']['type'], ['common', 'video'])) {
            $where[] = "type=".CMS::$db->escape($_GET['filter']['type']);
        }

        $c = CMS::$db->select('COUNT(id)', self::$tbl, $where);
        self::$items_amount = $c;
        //print "<pre>RESULT:\n{$c}\n\nQUERIES:\n".var_export(CMS::$db->queries, 1)."\n\nERRORS:\n".var_export(CMS::$db->errors, 1)."\n</pre>";
        $pages_amount = ceil($c/self::$pp);

        if ($pages_amount>0) {
            self::$pages_amount = $pages_amount;
            self::$curr_pg = ((self::$curr_pg>self::$pages_amount)? self::$pages_amount: self::$curr_pg);
            $start_from = (self::$curr_pg-1)*self::$pp;

            $list = CMS::$db->selectAll('*', self::$tbl, $where, 'id', $start_from, self::$pp);
            //print "<pre>RESULT:\n".var_export($list, 1)."\n\nQUERIES:\n".var_export(CMS::$db->queries, 1)."\n\nERRORS:\n".var_export(CMS::$db->errors, 1)."\n</pre>";
        }

        return $list;
    }

    public static function getTest($id) {
		$sql = "SELECT * FROM `".self::$tbl."` WHERE id = :id LIMIT 1";

		return CMS::$db->getRow($sql, [
			':id' => $id
		]);
	}

    public static function addParentTest() { // 2016-09-13
        $response = ['success' => false, 'message' => 'insert_err', 'errors' => []];
        $allowed_types = array("common", "video");

        if (in_array($_POST['optradio'], $allowed_types)) {
            $test['type'] = $_POST['optradio'];
        } else {
            $response['errors'][] = 'parent_test_type_error';
        }

        $title = strip_tags(trim(@(string)$_POST['title']));
        if (empty($title)) {
            $response['errors'][] = 'Question title cannot be empty.';
        } else {
            $test['title'] = $title;
        }

        $question_text = strip_tags(trim(@(string)$_POST['question_text']));
        if (empty($question_text)) {
            $response['errors'][] = 'Question text cannot be empty.';
        } else {
            $test['question_text'] = $question_text;
        }

        $question_text_hindi = strip_tags(trim(@(string)$_POST['question_text_hindi']));
        if (empty($question_text_hindi)) {
            $response['errors'][] = 'Please, provide text on hindi too.';
        } else {
            $test['question_text_hindi'] = $question_text_hindi;
        }

        if ($_POST['optradio']=='video') {
            /* Checking video existence and uploading 'em  */

            if (!empty($_FILES['video_left']['name'])) {
                if (empty($_FILES['video_left']['error'])) {
                   // var_dump(UPLOADS_DIR.'tests_content/'); die();
                    $uploaded = utils::upload($_FILES['video_left']['name'], $_FILES['video_left']['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_video_ext);
                    if (empty($uploaded)) {
                        $response['errors'][] = 'upl_invalid_video_extension_err';
                    } else {
                        $test['video_left'] = $uploaded;

                    }
                } else {
                    $response['errors'][] = CMS::$upload_err[$_FILES['video_left']['error']];
                }
            } else {
                $response['errors'][] = "no file1";
            }

            if (!empty($_FILES['video_right']['name'])) {
                if (empty($_FILES['video_right']['error'])) {
                    $uploaded = utils::upload($_FILES['video_right']['name'], $_FILES['video_right']['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_video_ext);
                    if (empty($uploaded)) {
                        $response['errors'][] = 'upl_invalid_video_extension_err';
                    } else {
                        $test['video_right'] = $uploaded;

                    }
                } else {
                    $response['errors'][] = CMS::$upload_err[$_FILES['video_right']['error']];
                }
            } else {
                $response['errors'][] = "no file2";
            }
        }

        if ($_POST['optradio']!=='video') {
            if (isset($_POST['choices'])) {
                $test['choicesBlock'] = (int) $_POST['choices'];
            } else {
                $response['errors'][] = "No choices block selected";
            }
        } else {
            $test['choicesBlock'] = null;
        }

        if (empty($response['errors'])) {
            $test['add_by'] = $_SESSION[CMS::$sess_hash]['ses_adm_id'];
            $test['add_datetime'] = date('Y-m-d H:i:s');

            $test_id = CMS::$db->add(self::$tbl, $test);

            if ($test_id) {
                CMS::log([
                    'subj_table' => self::$tbl,
                    'subj_id' => $test_id,
                    'action' => 'add',
                    'descr' => 'Question for parent assessment "'.$test['title'].'" was added by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
                ]);

                $response['success'] = true;
                $response['message'] = 'insert_suc';
            }
        }

        return $response;
    }

    public static function deleteTest($id) {
		$test = CMS::$db->getRow("SELECT * FROM content_parent_assestment_tests WHERE `id`=:id", [':id' => $id]);

		if (count($test)) {
			if (!empty($test['video_left'])) {
				@unlink(UPLOADS_DIR.'tests_content/'.$test['video_left']);
			}
			if (!empty($test['video_right'])) {
				@unlink(UPLOADS_DIR.'tests_content/'.$test['video_right']);
			}
		}

        $deleted = CMS::$db->exec('DELETE FROM `content_parent_assestment_tests` WHERE `id`=:id', [':id' => $id]);

        if ($deleted) {
            CMS::log([
                'subj_table' => self::$tbl,
                'subj_id' => (int)$id,
                'action' => 'erase',
                'descr' => 'Question for parent assessment "'.$test['title'].'" was erased by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
            ]);
        }

        return $deleted;

    }

    public static function editParentTest($id, $data) {
        $response = ['success' => false, 'message' => 'insert_err', 'errors' => []];
        $allowed_types = array("common", "video");
        $updateFields = [];

        if (in_array($data['optradio'], $allowed_types, true)) {
            $updateFields['type'] = $data['optradio'];
        } else {
            $response['errors'][] = 'parent_test_type_error';
        }

        $title = strip_tags(trim(@(string)$data['title']));
        if (empty($title)) {
            $response['errors'][] = 'Question title cannot be empty.';
        } else {
            $updateFields['title'] = $title;
        }

        $question_text = strip_tags(trim(@(string)$data['question_text']));
        if (empty($question_text)) {
            $response['errors'][] = 'Question text cannot be empty.';
        } else {
            $updateFields['question_text'] = $question_text;
        }

        $question_text_hindi = strip_tags(trim(@(string)$data['question_text_hindi']));
        if (empty($question_text_hindi)) {
            $response['errors'][] = 'Please, provide text on hindi too.';
        } else {
            $updateFields['question_text_hindi'] = $question_text_hindi;
        }

        if ($data['optradio']=='video') {
            /* Checking video existence and uploading 'em  */

            if (!empty($_FILES['video_left']['name'])) {
                if (empty($_FILES['video_left']['error'])) {
                    //var_dump(UPLOADS_DIR.'tests_content/'); die();
                    $uploaded = utils::upload($_FILES['video_left']['name'], $_FILES['video_left']['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_video_ext);
                    if (empty($uploaded)) {
                        $response['errors'][] = 'upl_invalid_video_extension_err';
                    } else {
                        $updateFields['video_left'] = $uploaded;

                    }
                } else {
                    $response['errors'][] = CMS::$upload_err[$_FILES['video_left']['error']];
                }
            }

            if (!empty($_FILES['video_right']['name'])) {
                if (empty($_FILES['video_right']['error'])) {
                    $uploaded = utils::upload($_FILES['video_right']['name'], $_FILES['video_right']['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_video_ext);
                    if (empty($uploaded)) {
                        $response['errors'][] = 'upl_invalid_video_extension_err';
                    } else {
                        $updateFields['video_right'] = $uploaded;

                    }
                } else {
                    $response['errors'][] = CMS::$upload_err[$_FILES['video_right']['error']];
                }
            }
        } else {
            $updateFields['video_left'] = null;
            $updateFields['video_right'] = null;
        }

        if ($data['optradio']!=='video') {
            if (isset($data['choices'])) {
                $updateFields['choicesBlock'] = (int) $data['choices'];
            } else {
                $response['errors'][] = "No choices block selected";
            }
        } else {
            $updateFields['choicesBlock'] = null;
        }

        if (empty($response['errors'])) {
            $updated = CMS::$db->mod(self::$tbl.'#'.(int)$id, $updateFields);

            CMS::log([
				'subj_table' => self::$tbl,
				'subj_id' => $id,
				'action' => 'update',
				'descr' => 'Parent assestments was modified by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
            ]);
            
            $response['success'] = true;
			$response['message'] = 'update_suc';
        }
        
        return $response;
    }
}

?>