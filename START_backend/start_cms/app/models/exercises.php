<?php

namespace app\models;

use abeautifulsite\simple_image\SimpleImage;
use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class exercises {
	private static $runtime = [];
	public static $tables = [
		//'bubbles_jubbing' => 'content_bubbles_jubbing_tests',
		'choose_touching' => 'content_choose_touching_tests',
		'wheel' => 'content_wheel_tests',
		'coloring' => 'content_coloring_tests',
		'eye_tracking_pairs' => 'content_eye_tracking_pairs_tests',
		'eye_tracking_slide' => 'content_eye_tracking_slide_tests',
		//'motoric_following' => 'content_motoric_following_tests',
		'parent_assestment' => 'content_parent_assestment_tests',
        'parent_child' => 'content_parent_child_play_tests',
	];
	public static $allowed_thumb_ext = ['jpg', 'jpeg', 'jpe', 'png'];
	public static $allowed_video_ext = ['mp4', 'flv', 'mov'];
	public static $dimensions = [
		'thumbs' => [
			'width' => 262,
			'height' => 169
		],
		'square' => [
			'width' => 60,
			'height' => 60
		],
		'larges' => [
			'width' => 800,
			'height' => 600
		],
		'block' => [
			'width' => 360,
			'height' => 190
		]
	];

	private static function union_exercises_tables($intersect=[]) { // 2017-05-19
		$tables = self::$tables;
		if (!empty($intersect)) {
			$tables = utils::array_filter_keys($tables, $intersect);
		}
		if (count($tables)) {
			$union = [];
			foreach ($tables as $type=>$table) {
			    //echo $table, "<br>";
			    if ($table == 'content_parent_assestment_tests') {
                    $union[] = "(SELECT id, ".CMS::$db->escape($type)." AS type, question_text, add_datetime, add_by, mod_datetime, mod_by, is_deleted FROM `".$table."`)";
                } else {
			        /** `content_parent_assestment_tests` table doesn't have field `name` */
                    $union[] = "(SELECT id, ".CMS::$db->escape($type)." AS type, name, add_datetime, add_by, mod_datetime, mod_by, is_deleted FROM `".$table."`)";
                }

			}
			$union = "(
				".implode("\nUNION\n", $union)."
			)";

			return $union;
		}

		return false;
	}

	public static function getDataTable() { // 2017-05-19
		$list = [];

		$joins = [];
		$joins['cu'] = "LEFT JOIN cms_users u ON u.id=exercises.add_by";
		$filter = [];
		$filter[] = "exercises.is_deleted='0'";
		if (!empty($_GET['q'])) {
			$filter[] = "tr.text LIKE '%".utils::makeSearchable($_GET['q'])."%'";
		}
		if (in_array(@$_GET['filter']['status'], ['0', '1'])) {
			$filter[] = "exercises.is_published=".CMS::$db->escape($_GET['filter']['status']);
		}
		if (!empty($_GET['filter']['author'])) {
			$filter[] = "exercises.add_by='".(int)$_GET['filter']['author']."'";
		}
		$where = (empty($filter)? '': ('WHERE '.implode(" AND ", $filter)));

		$list = CMS::$db->getAll("SELECT exercises.id, exercises.type, exercises.name, exercises.add_datetime, exercises.add_by, exercises.mod_datetime,
				exercises.mod_by, exercises.is_deleted,
				u.id AS author_id, u.name AS author_name, u.avatar AS author_avatar, u.login AS author_email
			FROM ".self::union_exercises_tables(['choose_touching', 'wheel', 'coloring', 'eye_tracking_pairs', 'parent_child'])." exercises
				".implode("\n", $joins)."
			{$where}
			ORDER BY exercises.name ASC");
		// print "<pre>RESULT:\n".var_export($list, 1)."\n\nQUERIES:\n".var_export(CMS::$db->queries, 1)."\n\nERRORS:\n".var_export(CMS::$db->errors, 1)."\n</pre>";

		return $list;
	}

	public static function editEx($id, $type) { // 2017-05-19
		$response = ['success' => false, 'message' => 'update_err'];
        $exercise = self::getEx($id, $type);
		if (empty($exercise['id'])) {
			$response['message'] = 'exercise_edit_err_not_found';
			return $response;
		}

		$upd = [];
		$translates = [];

		//$title = trim(@$_POST['title'][CMS::$default_site_lang]);


		/*self::checkSource($response, $upd, $exercise['id']);

		self::checkGallery($response, $upd, $exercise['id']);*/
        if ($type == "bubbles_jubbing") {
            /** TABLE:  content_bubbles_jubbing_tests */
            if (!empty($_FILES['bubble_img']['name'])) {
                if (empty($_FILES['bubble_img']['error'])) {
                    $uploaded = utils::upload($_FILES['bubble_img']['name'], $_FILES['bubble_img']['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_thumb_ext);
                    if (empty($uploaded)) {
                        $response['errors'][] = 'upl_invalid_image_extension_err';
                    } else {
                        $upd['bubble_img'] = $uploaded;
                        @unlink(UPLOADS_DIR.'tests_content/'.$exercise['bubble_img']);

                        /*foreach (self::$dimensions as $dir=>$size) {
                            @unlink(UPLOADS_DIR.'articles/'.$dir.'/'.$article['img']);
                            $img = new SimpleImage(UPLOADS_DIR.'articles/originals/'.$uploaded);
                            $img->thumbnail($size['width'], $size['height'])->save(UPLOADS_DIR.'articles/'.$dir.'/'.$uploaded);
                        }*/
                    }
                } else {
                    $response['errors'][] = CMS::$upload_err[$_FILES['bubble_img']['error']];
                }
            }
           /* $name = trim(@(string)$_POST['name']);
            if (empty($name)) {
                $response['errors'][] = 'exercise_name_err';
            } else {
                $upd['name'] = $name;
            }*/
        } else if (in_array($type, ['choose_touching', 'wheel'])) {
            /** TABLE:  content_choose_touching_tests */
            foreach ($_FILES as $key=>$file) {
                if ($file['name']!='') {
                    if (empty($file['error'])) {
                        $uploaded = utils::upload($file['name'], $file['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_video_ext);
                        if (empty($uploaded)) {
                            $response['errors'][] = 'upl_invalid_video_extension_err';
                        } else {
                            $upd[$key] = $uploaded;
                            @unlink(UPLOADS_DIR.'tests_content/'.$exercise[$key]);
                        }
                    } else {
                        $response['errors'][] = CMS::$upload_err[$file['error']];
                    }
                }
            }
        } else if ($type == "coloring") {
            /** TABLE:  content_coloring_tests */
            if (!empty($_FILES['img_1']['name'])) {
                if (empty($_FILES['img_1']['error'])) {
                    $uploaded = utils::upload($_FILES['img_1']['name'], $_FILES['img_1']['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_thumb_ext);
                    if (empty($uploaded)) {
                        $response['errors'][] = 'upl_invalid_image_extension_err';
                    } else {
                        $upd['img_1'] = $uploaded;
                        @unlink(UPLOADS_DIR.'tests_content/'.$exercise['img_1']);


                    }
                } else {
                    $response['errors'][] = CMS::$upload_err[$_FILES['img_1']['error']];
                }
            }
            if (!empty($_FILES['img_2']['name'])) {
                if (empty($_FILES['img_2']['error'])) {
                    $uploaded = utils::upload($_FILES['img_2']['name'], $_FILES['img_2']['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_thumb_ext);
                    if (empty($uploaded)) {
                        $response['errors'][] = 'upl_invalid_image_extension_err';
                    } else {
                        $upd['img_2'] = $uploaded;
                        @unlink(UPLOADS_DIR.'tests_content/'.$exercise['img_2']);

                    }
                } else {
                    $response['errors'][] = CMS::$upload_err[$_FILES['img_2']['error']];
                }
            }
            if (!empty($_FILES['img_3']['name'])) {
                if (empty($_FILES['img_3']['error'])) {
                    $uploaded = utils::upload($_FILES['img_3']['name'], $_FILES['img_3']['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_thumb_ext);
                    if (empty($uploaded)) {
                        $response['errors'][] = 'upl_invalid_image_extension_err';
                    } else {
                        $upd['img_3'] = $uploaded;
                        @unlink(UPLOADS_DIR.'tests_content/'.$exercise['img_3']);


                    }
                } else {
                    $response['errors'][] = CMS::$upload_err[$_FILES['img_3']['error']];
                }
            }
            if (!empty($_FILES['img_4']['name'])) {
                if (empty($_FILES['img_4']['error'])) {
                    $uploaded = utils::upload($_FILES['img_4']['name'], $_FILES['img_4']['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_thumb_ext);
                    if (empty($uploaded)) {
                        $response['errors'][] = 'upl_invalid_image_extension_err';
                    } else {
                        $upd['img_4'] = $uploaded;
                        @unlink(UPLOADS_DIR.'tests_content/'.$exercise['img_4']);


                    }
                } else {
                    $response['errors'][] = CMS::$upload_err[$_FILES['img_4']['error']];
                }
            }
            if (!empty($_FILES['img_5']['name'])) {
                if (empty($_FILES['img_5']['error'])) {
                    $uploaded = utils::upload($_FILES['img_5']['name'], $_FILES['img_5']['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_thumb_ext);
                    if (empty($uploaded)) {
                        $response['errors'][] = 'upl_invalid_image_extension_err';
                    } else {
                        $upd['img_5'] = $uploaded;
                        @unlink(UPLOADS_DIR.'tests_content/'.$exercise['img_5']);


                    }
                } else {
                    $response['errors'][] = CMS::$upload_err[$_FILES['img_5']['error']];
                }
            }
            if (!empty($_FILES['img_6']['name'])) {
                if (empty($_FILES['img_6']['error'])) {
                    $uploaded = utils::upload($_FILES['img_6']['name'], $_FILES['img_6']['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_thumb_ext);
                    if (empty($uploaded)) {
                        $response['errors'][] = 'upl_invalid_image_extension_err';
                    } else {
                        $upd['img_6'] = $uploaded;
                        @unlink(UPLOADS_DIR.'tests_content/'.$exercise['img_6']);


                    }
                } else {
                    $response['errors'][] = CMS::$upload_err[$_FILES['img_6']['error']];
                }
            }
            /*$name = trim(@(string)$_POST['name']);
            if (empty($name)) {
                $response['errors'][] = 'exercise_name_err';
            } else {
                $upd['name'] = $name;
            }*/
        } else if ($type == "eye_tracking_pairs") {
			$pair = 1;
			$pairs_num = 8;
			while ($pair<=$pairs_num) {
				$img = 1;
				$imgs_num = 2;
				while ($img<=$imgs_num) {
					if (!empty($_FILES['pair_'.$pair.'_img_'.$img]['name'])) {
						if (empty($_FILES['pair_'.$pair.'_img_'.$img]['error'])) {
							$uploaded = utils::upload($_FILES['pair_'.$pair.'_img_'.$img]['name'], $_FILES['pair_'.$pair.'_img_'.$img]['tmp_name'], UPLOADS_DIR.'tests_content/', array_merge(self::$allowed_thumb_ext, self::$allowed_video_ext));
							if (empty($uploaded)) {
								$response['errors'][] = 'upl_invalid_image_extension_err';
							} else {
								$upd['pair_'.$pair.'_img_'.$img] = $uploaded;
								@unlink(UPLOADS_DIR.'tests_content/'.$exercise['pair_'.$pair.'_img_'.$img]);
							}
						} else {
							$response['errors'][] = CMS::$upload_err[$_FILES['pair_'.$pair.'_img_'.$img]['error']];
						}
					}

					$img++;
				}

				$pair++;
			}
        } else if ($type == "eye_tracking_slide") {
            /** TABLE:  content_eye_tracking_slide_tests */
            if (!empty($_FILES['moving_fragment']['name'])) {
                if (empty($_FILES['moving_fragment']['error'])) {
                    $uploaded = utils::upload($_FILES['moving_fragment']['name'], $_FILES['moving_fragment']['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_thumb_ext);
                    if (empty($uploaded)) {
                        $response['errors'][] = 'upl_invalid_image_extension_err';
                    } else {
                        $upd['moving_fragment'] = $uploaded;
                        @unlink(UPLOADS_DIR.'tests_content/'.$exercise['moving_fragment']);


                    }
                } else {
                    $response['errors'][] = CMS::$upload_err[$_FILES['moving_fragment']['error']];
                }
            }
            if (!empty($_FILES['slide']['name'])) {
                if (empty($_FILES['slide']['error'])) {
                    $uploaded = utils::upload($_FILES['slide']['name'], $_FILES['slide']['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_thumb_ext);
                    if (empty($uploaded)) {
                        $response['errors'][] = 'upl_invalid_image_extension_err';
                    } else {
                        $upd['slide'] = $uploaded;
                        @unlink(UPLOADS_DIR.'tests_content/'.$exercise['slide']);

                    }
                } else {
                    $response['errors'][] = CMS::$upload_err[$_FILES['slide']['error']];
                }
            }

           /* $name = trim(@(string)$_POST['name']);
            if (empty($name)) {
                $response['errors'][] = 'exercise_name_err';
            } else {
                $upd['name'] = $name;
            }*/
        } else if ($type == "motoric_following") {
            /** TABLE:  content_motoric_following_tests */
            if (!empty($_FILES['moving_fragment']['name'])) {
                if (empty($_FILES['moving_fragment']['error'])) {
                    $uploaded = utils::upload($_FILES['moving_fragment']['name'], $_FILES['moving_fragment']['tmp_name'], UPLOADS_DIR.'tests_content/', self::$allowed_thumb_ext);
                    if (empty($uploaded)) {
                        $response['errors'][] = 'upl_invalid_image_extension_err';
                    } else {
                        $upd['moving_fragment'] = $uploaded;
                        @unlink(UPLOADS_DIR.'tests_content/'.$exercise['moving_fragment']);
                    }
                } else {
                    $response['errors'][] = CMS::$upload_err[$_FILES['moving_fragment']['error']];
                }
            }


            /*$name = trim(@(string)$_POST['name']);
            if (empty($name)) {
                $response['errors'][] = 'exercise_name_err';
            } else {
                $upd['name'] = $name;
            }*/
        } else if ($type=="parent_child") {
            $instruction = trim(@(string)$_POST['instruction']);
            if (empty($instruction)) {
                $response['errors'][] = 'exercise_instruction_err';
            } else {
                $upd['instruction'] = $instruction;
            }

            $instruction_hindi = trim(@(string)$_POST['instruction_hindi']);
            if (empty($instruction_hindi)) {
                $response['errors'][] = 'exercise_instruction_err';
            } else {
                $upd['instruction_hindi'] = $instruction_hindi;
            }
        } else {
            /** Unknown table... Somebody tried to modify  `type` manually ! */
            die("Oops !");
        }

		if (empty($response['errors'])) {
			$upd['mod_by'] = $_SESSION[CMS::$sess_hash]['ses_adm_id'];
			$upd['mod_datetime'] = date('Y-m-d H:i:s');

			$updated = CMS::$db->mod(self::$tables[$type].'#'.(int)$id, $upd);

			// log event
			CMS::log([
				'subj_table' => self::$tables[$type],
				'subj_id' => $id,
				'action' => 'edit',
				'descr' => 'Assessment type "'.$type.'" was modified by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
			]);

			$response['success'] = true;
			$response['message'] = 'update_suc';
		}

		return $response;
	}

	public static function setExStatus($id, $status) { // 2017-05-19
		$updated = CMS::$db->mod(self::$tbl.'#'.(int)$id, [
			'is_published' => (($status=='on')? '1': '0')
		]);

		if ($updated) {
			CMS::log([
				'subj_table' => self::$tbl,
				'subj_id' => $id,
				'action' => 'edit',
				'descr' => 'Assessment was '.(($status=='on')? '': 'un').'published by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
			]);
		}

		return $updated;
	}

	public static function deleteEx($id) { // 2016-12-04
		$deleted = CMS::$db->mod(self::$tbl.'#'.(int)$id, [
			'is_deleted' => '1',
		]);

		if ($deleted) {
			CMS::log([
				'subj_table' => self::$tbl,
				'subj_id' => $id,
				'action' => 'delete',
				'descr' => 'Assessment was deleted by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
			]);
		}

		return $deleted;
	}

	public static function deleteExImages($id) { // 2017-05-19
		$article = self::getArticle($id);
		if (empty($article['id']) || empty($article['img'])) {return false;}

		$updated = CMS::$db->mod(self::$tbl.'#'.(int)$id, [
			'img' => null
		]);

		if ($updated) {
			CMS::log([
				'subj_table' => self::$tbl,
				'subj_id' => $id,
				'action' => 'edit',
				'descr' => 'Article image removed by '.$_SESSION[CMS::$sess_hash]['ses_adm_type'].' '.ADMIN_INFO,
			]);
		}

		@unlink(UPLOADS_DIR.'articles/originals/'.$article['img']);
		foreach (self::$dimensions as $dir=>$size) {
			@unlink(UPLOADS_DIR.'articles/'.$dir.'/'.$article['img']);
		}

		return true;
	}

	public static function getEx($id, $type) { // 2017-05-19
        if (!array_key_exists($type, self::$tables)) {
            /** wrong table in params */
            die("Wrong table!");
        }

		$sql = "SELECT * FROM `".self::$tables[$type]."` WHERE id=:id AND is_deleted='0' LIMIT 1";

		$assessment = CMS::$db->getRow($sql, [
			':id' => $id
		]);

        //echo "<pre>"; var_dump($assessment);  die();
		//if (!empty($assessment['id'])) {$assessment['translates'] = tr::get(self::$tables, $id);}
		return $assessment;
	}

	public static function countExs() { // 2017-05-19
		$sql = "SELECT COUNT(a.id)
			FROM `".self::$tbl."` a
			WHERE a.is_deleted='0'";

		return CMS::$db->get($sql);
	}
}

?>