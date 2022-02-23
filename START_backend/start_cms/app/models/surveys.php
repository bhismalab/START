<?php

namespace app\models;

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class surveys {
	public static $tbl = 'surveys';

	public static function getLastSurveyByChildId($child_id) { // 2017-05-12
		return CMS::$db->getRow("SELECT *
			FROM `".self::$tbl."` s
			WHERE s.child_id=:child_id AND s.is_deleted='0'
			ORDER BY s.created_datetime DESC
			LIMIT 1",
			[':child_id' => $child_id]
		);
	}

	public static function getSurveyById($survey_id) { // 2017-07-06
		return CMS::$db->getRow("SELECT *
			FROM `".self::$tbl."` s
			WHERE s.id=:survey_id AND s.is_deleted='0'
			ORDER BY s.created_datetime DESC
			LIMIT 1",
			[':survey_id' => $survey_id]
		);
	}

	public static function getSurveyResults($survey_id, $assestment_type) { // 2017-07-03
		$sql = "SELECT *
			FROM survey_results
			WHERE survey_id=:survey_id AND assestment_type=:assestment_type
			ORDER BY attempt ASC";
		$params = [
			':survey_id' => $survey_id,
			':assestment_type' => $assestment_type,
		];
		$results = CMS::$db->getAll($sql, $params);
		if (!empty($results) && is_array($results)) {
			foreach ($results as $i=>$attempt) {
				$results[$i]['result_data'] = json_decode(file_get_contents(UPLOADS_DIR.'survey_results/'.$survey_id.'/'.$attempt['result_file']), 1);
			}
		}

		return $results;
	}

	public static function getSurveyAttachments($survey_id, $assestment_type, $http_link=true) { // 2017-07-04
		$sql = "SELECT *
			FROM survey_attachments
			WHERE survey_id=:survey_id AND assestment_type=:assestment_type";
		$params = [
			':survey_id' => $survey_id,
			':assestment_type' => $assestment_type,
		];
		$results = CMS::$db->getAll($sql, $params);

		if (!empty($results) && is_array($results)) {
			foreach ($results as $i=>$attempt) {
				$results[$i]['attachment_file'] = ($http_link? SITE.utils::dirCanonicalPath(CMS_DIR.UPLOADS_DIR): UPLOADS_DIR).'survey_attachments/'.$survey_id.'/'.$attempt['attachment_file'];
			}
		}

		return $results;
	}

	public static function exportAssestmentResults_using_PHPExcell($assestment_type, $data, $filename=0) { // 2017-07-07
		/*
			This function is out of duty.
			It was disabled because of PHPExcel memory consumption.
			Leaved here for reference only.
		*/

		set_time_limit(500);

		require_once VENDOR_DIR.'PHPExcel/PHPExcel/PHPExcel.php';
		require_once VENDOR_DIR.'PHPExcel/PHPExcel/PHPExcel/CachedObjectStorageFactory.php';
		require_once VENDOR_DIR.'PHPExcel/PHPExcel/PHPExcel/Settings.php';

		$cacheMethod = \PHPExcel_CachedObjectStorageFactory::cache_to_phpTemp;
		$cacheSettings = ['memoryCacheSize' => '999MB'];
		\PHPExcel_Settings::setCacheStorageMethod($cacheMethod, $cacheSettings);

		$xl = new \PHPExcel();
		$sheet = $xl->getActiveSheet();
		$sheet->setTitle('Attempt #1');
		$dumps = [];

		if (!empty($data) && is_array($data)) foreach ($data as $i=>$a) {
			$data[$i] = 0;

			$items = [];
			$subattempts = [];

			if ($i) {
				$sheet = $xl->createSheet($xl->getSheetCount());
				$sheet->setTitle('Attempt #'.($i+1));
			}

			$col = 'A';
			$row = '1';
			//$sheet->getColumnDimension($col)->setAutoSize(true);

			foreach ($a as $k=>$v) {
				if (is_array($v)) {
					foreach ($v as $k2=>$v2) {
						if (is_array($v2)) {
							if ($k2=='items') {
								$items = $v2;
							}
							if ($k2=='attempts') {
								$subattempts = $v2;
							}
							if ($k2=='questions') {
								$items['questions'] = $v2;
							}
							if ($k2=='videoQuestions') {
								$items['videoQuestions'] = $v2;
							}

							continue;
						}

						$sheet->setCellValue('A'.$row, $k2);
						if (in_array($k2, ['startTime', 'endTime'])) {
							$mts = $v2/1000;
							$ts = floor($mts);
							$ms = round(($mts-$ts)*1000);
							$sheet->setCellValue('B'.$row, date('d.m.Y H:i:s', $ts).' '.$ms);
						} else {
							$sheet->setCellValue('B'.$row, $v2);
						}

						$row++;
					}

					continue;
				}
				/*
                echo "<pre>", var_dump($v), "-2-", "<hr style='background-color: red; height: 2px;'>";*/
                //echo "<pre>", var_dump($k), "-1-", "<hr style='background-color: red; height: 2px;'>";
                if ($k=='id') {
                    $sql = "SELECT child_id
						FROM surveys
						WHERE id IN (SELECT survey_id FROM survey_results WHERE id = $v);";
                    $p = [];
                    $results = CMS::$db->getRow($sql, $p);
                    //var_dump($results);
                    if (count($results)==1) {
                        $v = $results['child_id'];
                    }
                }
				$sheet->setCellValue('A'.$row, $k);
				$sheet->setCellValue('B'.$row, $v);

				$row++;
			}

			if (!empty($items)) {
				if ($assestment_type=='eye_tracking_pairs') {
					foreach ($items as $test) {
						$row++;
						$col = 'A';
						$sheet->setCellValue($col.$row, $test['name']);

						$keys = @array_keys($test['items'][0]);
						if ($keys) {
							$row++;
							$col = 'A';
							foreach ($keys as $k5) {
								$sheet->setCellValue($col.$row, $k5);
								$col++;
							}

							$row++;
							$col = 'A';
							foreach ($test['items'] as $j=>$fields) {
								foreach ($fields as $f=>$v4) {
									if ($f=='time') {
										$sheet->setCellValue($col.$row, ($v4-$a['result_data']['startTime']));
									} else {
										$sheet->setCellValue($col.$row, $v4);
									}
									$col++;
								}
								$row++;
								$col = 'A';
							}
						}
					}
				} else if ($assestment_type=='bubbles_jubbing') {
                    if (isset($items[0])) {
                        $row++;
                        $col = 'A';

                        if (!isset($items[0]['bubble'])) {
                            // Если нету поля bubble мы его должны добавить в начало.
                            $items[0]['bubble'] = '';
                            end($items[0]);
                            $key=key($items[0]);
                            $items[0] = array($key => array_pop($items[0])) + $items[0];
                            foreach ($items[0] as $k3=>$v3) {
                                $sheet->setCellValue($col.$row, $k3);
                                $col++;
                            }
                        }
                        $get_keys_count = count($items[0]);
                        $row++;
                        $col = 'A';
                        foreach ($items as $j=>$fields) {
                            if ($get_keys_count > count($fields)) {
                                // Если у этой строки нету значения bubble записываем пустую строку, чтобы не было смещения.
                                $fields['bubble'] = '';
                                end($fields);
                                $key = key($fields);
                                $fields = array($key => array_pop($fields)) + $fields;
                            }
                            foreach ($fields as $f=>$v4) {
                                if ($f=='time') {
                                    $sheet->setCellValue($col.$row, ($v4-$a['result_data']['startTime']));
                                } else {
                                    $sheet->setCellValue($col.$row, $v4);
                                }
                                $col++;
                            }
                            $row++;
                            $col = 'A';
                        }
                    }
                } else if ($assestment_type=='parent_assestment') {
					foreach ($items as $questions_type=>$questions) {
						if (isset($questions[0])) {
							$row++;
							$col = 'A';
							foreach ($questions[0] as $k3=>$v3) {
								$sheet->setCellValue($col.$row, $k3);
								$col++;
							}

							$row++;
							$col = 'A';
							foreach ($questions as $j=>$fields) {
								foreach ($fields as $f=>$v4) {
									if ($f=='time') {
										$sheet->setCellValue($col.$row, ($v4-$a['result_data']['startTime']));
									} else {
										$sheet->setCellValue($col.$row, $v4);
									}
									$col++;
								}
								$row++;
								$col = 'A';
							}
						}
					}
				} else {
					if (isset($items[0])) {
						$row++;
						$col = 'A';
						foreach ($items[0] as $k3=>$v3) {
							$sheet->setCellValue($col.$row, $k3);
							$col++;
						}

						$row++;
						$col = 'A';
						foreach ($items as $j=>$fields) {
							foreach ($fields as $f=>$v4) {
								if ($f=='time') {
									$sheet->setCellValue($col.$row, ($v4-$a['result_data']['startTime']));
								} else {
									$sheet->setCellValue($col.$row, $v4);
								}
								$col++;
							}
							$row++;
							$col = 'A';
						}
					}
				}
			}
			$items = [];

			if (!empty($subattempts)) {
				foreach ($subattempts as $sub_a_key=>$sub_attempt) {
					$row++;
					$sheet->setCellValue('A'.$row, 'Sub-attempt #'.($sub_a_key+1));

					if (isset($sub_attempt['items'][0])) {
						$row++;
						$col = 'A';
						foreach ($sub_attempt['items'][0] as $k3=>$v3) {
							$sheet->setCellValue($col.$row, $k3);
							$col++;
						}

						$row++;
						$col = 'A';
						foreach ($sub_attempt['items'] as $j=>$fields) {
							foreach ($fields as $f=>$v4) {
								if ($f=='time') {
									$sheet->setCellValue($col.$row, ($v4-$a['result_data']['startTime']));
								} else {
									$sheet->setCellValue($col.$row, $v4);
								}
								$col++;
							}
							$row++;
							$col = 'A';
						}
					}

					if (isset($sub_attempt['image_dump'])) {
						$dumps[] = $sub_attempt['image_dump'];
					}
				}
				unset($subattempts, $sub_attempt);

				if ($dumps) {
					foreach ($dumps as $img_index=>$dump) {
						$dumps[$img_index] = 0;
						$dump = json_decode($dump, 1);
						//$dump = json_decode("{\"content\":null}", 1);

						$sheet = $xl->createSheet($xl->getSheetCount());
						$sheet->setTitle('Image dump #'.($img_index+1));

						$col = 'A';
						$row = '1';

						if (isset($dump[0])) {
							$col = 'A';
							foreach ($dump[0] as $k3=>$v3) {
								$sheet->setCellValue($col.$row, $k3);
								$col++;
							}

							$row++;
							$col = 'A';
							foreach ($dump as $j=>$fields) {
								foreach ($fields as $f=>$v4) {
									$sheet->setCellValue($col.$row, $v4);

									$col++;
								}
								$row++;
								$col = 'A';
							}
						}

						$dumps = [];
					}
					unset($dump);
				}

				$dumps = [];
			}
		}

		$xl_rw = \PHPExcel_IOFactory::createWriter($xl, 'Excel2007');

		$xl->disconnectWorksheets();
		unset($xl);

		if (empty($filename)) {
			header('Content-Type: application/octet-stream');
			header('Content-Disposition: attachment; filename='.$assestment_type.'_'.time().'.xlsx');
			header('Content-Transfer-Encoding: binary');
			$xl_rw->save('php://output');

			die();
		} else {
			//$fpath = 'uploads/files/'.$assestment_type.'_'.time().'.xlsx';
			$fpath = $_SERVER['DOCUMENT_ROOT'].'/uploads/files/'.$filename;
			//print "<p>".$fpath."</p>";
			$xl_rw->save($fpath);

			return $fpath;
		}
	}

    private static function getChild($child_id) { // 2017-05-02
		return CMS::$db->getRow("SELECT c.id, c.photo, ".CMS::$db->aes('c.surname', 'surname').", ".CMS::$db->aes('c.name', 'name').",
				".CMS::$db->aes('c.patronymic', 'patronymic').", ".CMS::$db->aes('c.birth_date', 'birth_date').", ".CMS::$db->aes('c.hand', 'hand').",
				".CMS::$db->aes('c.gender', 'gender').", ".CMS::$db->aes('c.state', 'state').", ".CMS::$db->aes('c.address', 'address').",
				".CMS::$db->aes('c.latitude', 'latitude').", ".CMS::$db->aes('c.longitude', 'longitude').",
				c.add_datetime, c.add_by, ".CMS::$db->aes('c.diagnosis', 'diagnosis').", ".CMS::$db->aes('c.diagnosis_clinic', 'diagnosis_clinic').", ".CMS::$db->aes('c.diagnosis_datetime', 'diagnosis_datetime')."
			FROM `children` c
			WHERE c.id=:id AND c.is_deleted='0'
			LIMIT 1",
			[':id' => $child_id]
		);
	}

	public static function getParentsByChildId($child_id) { // 2017-05-12
		return CMS::$db->getAll("SELECT p.id, p.child_relationship AS rel, ".CMS::$db->aes('p.surname', 'surname').", ".CMS::$db->aes('p.name', 'name').",
				".CMS::$db->aes('p.patronymic', 'patronymic').", ".CMS::$db->aes('p.birth_date', 'birth_date').", ".CMS::$db->aes('p.spoken_language', 'lang').",
				".CMS::$db->aes('p.gender', 'gender').", ".CMS::$db->aes('p.state', 'state').", ".CMS::$db->aes('p.address', 'address').",
				".CMS::$db->aes('p.phone', 'phone').", ".CMS::$db->aes('p.email', 'email').", ".CMS::$db->aes('p.preferable_contact', 'preferable_contact').",
				p.signature_scan AS signature
			FROM `parents` p
			WHERE p.child_id=:child_id AND p.is_deleted='0'
			ORDER BY p.id ASC",
			[':child_id' => $child_id]
		);
	}

	public static function exportAssestmentResults($assestment_type, $data, $filename=0) { // 2017-08-31
		/*
			This function generates Excell file with survey assessment results.
			Each assessment type have differencies in json structure. For example, coloring and touch tracking have sub-attempts.
		*/

		set_time_limit(500);

		if ($_SESSION[CMS::$sess_hash]['ses_adm_type']=='researcher') {
			$allow_child_fields = ['birth_date'];
		} else {
			$allow_child_fields = ['name', 'surname', 'birth_date', 'hand', 'gender', 'state' /*, 'address', 'latitude', 'longitude'*/];
		}

		require_once VENDOR_DIR.'php_xlsx_writer/xlsxwriter.class.php';
		$xl = new \XLSXWriter();

		$dumps = [];

		if (!empty($data) && is_array($data)) foreach ($data as $i=>$a) {

			$sql = "SELECT ".CMS::$db->aes('p.name', 'pname').", ".CMS::$db->aes('p.surname', 'psurname').", ".CMS::$db->aes('c.name', 'name').",
					".CMS::$db->aes('c.surname', 'surname').", ".CMS::$db->aes('c.address', 'address').", ".CMS::$db->aes('c.gender', 'gender').",
					".CMS::$db->aes('c.birth_date', 'birth_date').", ".CMS::$db->aes('c.state', 'state').", ".CMS::$db->aes('c.hand', 'hand')."
				FROM surveys s
					JOIN children c ON s.child_id = c.id
					JOIN parents p ON p.child_id = c.id
				WHERE s.id = " . (int) $a['survey_id'];
			$childrenData = CMS::$db->getRow($sql, []);

			$data[$i] = 0;

			$items = [];
			$subattempts = [];

			$sheet_title = 'Attempt #'.($i+1);

			$info = $_SESSION[CMS::$sess_hash]['ses_adm_type']=="researcher" ?
			[
				['Birth Date', $childrenData['birth_date']],
				['Age', utils::getAge($childrenData['birth_date'])],
			] :
			[
				['Child name', sprintf('%s %s', $childrenData['name'], $childrenData['surname'])],
				['Parent name', sprintf('%s %s', $childrenData['pname'], $childrenData['psurname'])],
				['Address', $childrenData['address']],
				['Gender', $childrenData['gender']],
				['Birth Date', $childrenData['birth_date']],
				['Age', utils::getAge($childrenData['birth_date'])],
				['State', $childrenData['state']],
				['Hand dominance ', $childrenData['hand']]
			];

			foreach($info as $infoRow) {
				$xl->writeSheetRow($sheet_title, [
					$infoRow[0], $infoRow[1]
				]);
			}
            $xl->writeSheetRow($sheet_title, ['']);
            
			$eyeTrackingMapping = [
				'eye_tracking_pairs' => 'testLooking',
				'eye_tracking_slide' => 'testAttention'
            ];

            if (in_array($assestment_type, array_keys($eyeTrackingMapping))) {
                $xl->writeSheetRow($sheet_title, ['Calibration Summary']);
                $xl->writeSheetRow($sheet_title, ["'Calibration Quality' represents the percentage of time the child gazes at the calibration stimulus."]);
                $xl->writeSheetRow($sheet_title, ['']);
            }

			foreach ($a as $k=>$v) {
				if (is_array($v)) {
					foreach ($v as $k2=>$v2) {
						if (is_array($v2)) {
							if ($k2=='items') {$items = $v2;}
							if ($k2=='attempts') {$subattempts = $v2;}
							if ($k2=='questions') {$items['questions'] = $v2;}
							if ($k2=='videoQuestions') {$items['videoQuestions'] = $v2;}
							continue;
						}

						$row = [$k2];
						if (in_array($k2, ['startTime', 'endTime'])) {
							$mts = $v2/1000;
							$ts = floor($mts);
							$ms = round(($mts-$ts)*1000);
							$row[] = date('d.m.Y H:i:s', $ts).' '.$ms;
						} else {
							$row[] = $v2;
						}
						$xl->writeSheetRow($sheet_title, $row);
					}

					continue;
				}
				
                /*if ($k=='id') {
                    $sql = "SELECT child_id
						FROM surveys
						WHERE id IN (SELECT survey_id FROM survey_results WHERE id = $v);";
                    $p = [];
                    $results = CMS::$db->getRow($sql, $p);
                    //var_dump($results);
                    if (count($results)==1) {
                        $v = $results['child_id'];
                    }
                }
				$xl->writeSheetRow($sheet_title, [$k, $v]);*/

				if ($k=='id') {
                    $sql = "SELECT id, child_id
						FROM surveys
						WHERE id IN (SELECT survey_id FROM survey_results WHERE id = $v);";
                    $p = [];
                    $results = CMS::$db->getRow($sql, $p);
                    //var_dump($results);
                    if (count($results)==1) {
                        //$v = $results['child_id'];
						$xl->writeSheetRow($sheet_title, ['child_id', $results['child_id']]);

						$child = self::getChild($results['child_id']);
						$parents = self::getParentsByChildId($results['child_id']);
						if (!empty($child['id'])) {
							$child = utils::array_filter_keys($child, $allow_child_fields);
							foreach ($child as $cf=>$cv) {
								$xl->writeSheetRow($sheet_title, ['child_'.$cf, $cv]);
							}
							if ($_SESSION[CMS::$sess_hash]['ses_adm_type']=='researcher') {
								foreach ($parents as $pk=>$pv) {
									$xl->writeSheetRow($sheet_title, ['parent_'.($pk+1), $pv['name'].' '.$pv['surname']]);
								}
							}
							$xl->writeSheetRow($sheet_title, ['']);
						}
						//$xl->writeSheetRow($sheet_title, ['survey_id', $results['id']]);
                    }
				}
			}

            $camelizeString = function($fieldName) {
                return ucwords(
                    preg_replace('/(?!^)[A-Z]{2,}(?=[A-Z][a-z])|[A-Z][a-z]/', ' $0', $fieldName)
                );
            };

            $array_swap_assoc = function ($key1, $key2, $array) {
                $newArray = array ();
                foreach ($array as $key => $value) {
                    if ($key == $key1) {
                        $newArray[$key2] = $array[$key2];
                    } elseif ($key == $key2) {
                        $newArray[$key1] = $array[$key1];
                    } else {
                        $newArray[$key] = $value;
                    }
                }
                return $newArray;
            };

            $addDpiValue = function($items, $xdpi, $ydpi, $nameField = 'touch') {
                $dpi = ($xdpi + $ydpi) / 2;

                $calculateDp = function($pixels) use ($dpi) {
                    return $pixels / ($dpi / 160);
                };

                $addElementAfter = function($items, $after, $newVal) {
                    $afterIndex = array_search($after, array_keys($items));
                    
                    return array_merge(
                        array_slice($items, 0, $afterIndex +1 ),
                        $newVal,
                        array_slice($items, $afterIndex + 1)
                    );
                };

                return array_map(function($e) use ($addElementAfter, $calculateDp, $nameField) {
                    foreach (['x', 'y'] as $axes) {
                        if (!isset($e[$nameField.'_'.$axes])) {
                            continue;   
                        }

                        $e = $addElementAfter(
                            $e, $nameField .'_'. $axes, [
                                $nameField .'_'.$axes.'_dp' => $calculateDp($e[$nameField .'_'.$axes])
                            ]
                        );
                    }
                    
                    return $e;
                }, $items);
            };

            $renameVar = 0; // tmp var
			if (in_array($assestment_type, array_keys($eyeTrackingMapping))) {
				$eyeTrackingData = $a['result_data'][
					$eyeTrackingMapping[$assestment_type]
				];

				// reorder some items
				foreach(array_reverse([
					'startTime', 'endTime', 'name', 'videoProcessed'
				]) as $key) {
					$temp = array($key => $eyeTrackingData[$key]);
					unset($eyeTrackingData[$key]);
					$eyeTrackingData = $temp + $eyeTrackingData;
                }
                
                if (isset($eyeTrackingData['calibrationQuality']) && $eyeTrackingData['calibrationQuality'] == -1) {
                    $eyeTrackingData['calibrationQuality'] = 'n/a';
                }

                $tablesDetails = [

                    'itemsEyeTrackingQuality' => ['Calibration Gaze Data', "'Gaze out’ represents gazing off the device. 0 means gazing was on the device and 1 means gazing was off the device.",
                    "‘Gaze X’; 0 represents gaze left, 1 represents gaze right and -1 represents gaze off the screen. Currently the screen is being split into x2 left and right."],

                    // itemsEyeTrackingQuality before itemsEyeTracking

                    'itemsEyeTracking' => [ 
                        $assestment_type === 'eye_tracking_pairs' ? 'Preferential Looking Gaze Data' : 'Award Attention Gaze Data',
                        "'Gaze out’ represents gazing off the device. 0 means gazing was on the device and 1 means gazing was off the device.",
                        "‘Gaze X’; 0 represents gaze left, 1 represents gaze right and -1 represents gaze off the screen. Currently the screen is being split into x2 left and right."
                    ],

                    'itemsMergedQality' => [
                        'Calibration Merged Gaze & Stimulus Data',
                        'This table is determined by the eye gaze of the participant'
                    ],

                   'itemsStimulus' => [
                        $assestment_type === 'eye_tracking_pairs' ? 'Preferential Looking Stimulus Onset' : 'Award Attention Stimulus Onset',
                        'This table shows the onset of the stimulus on the screen'
                    ],

                    'itemsMerged' => [ // itemsStimulus
                        $assestment_type === 'eye_tracking_pairs' ? 'Preferential Looking Merged Gaze & Stimulus Data' : 'Award Attention Merged Gaze & Stimulus Data',
                        'This table is determined by the eye gaze of the participant'
                    ],

                    'itemsStimulusQuality' => [
                        'Calibration Stimulus Onset Data',
                        'This table shows the onset of the stimulus on the screen'
                    ]
                ];

                $eyeTrackingData = $array_swap_assoc(
                    'itemsEyeTrackingQuality', 'itemsEyeTracking', $eyeTrackingData 
                );

                $eyeTrackingData = $array_swap_assoc(
                    'itemsMergedQality', 'itemsMerged', $eyeTrackingData 
                );

                $eyeTrackingData = $array_swap_assoc(
                    'itemsStimulusQuality', 'itemsStimulus', $eyeTrackingData 
                );

                $replacePreferentialTextToTrial = function(&$result) {
                    foreach ($result as &$val) {
                        if (is_string($val)) {
                            $val = str_replace('Preferential looking test ', 'Trial ', $val);
                        }
                    }
                };

                $blockItemStimulus = null;
				foreach ($eyeTrackingData as $name => $val) {
					if (is_array($val)) {
                        $xl->writeSheetRow($sheet_title, ['']);
                        
                        if (isset($tablesDetails[$name])) {
                            foreach ($tablesDetails[$name] as $documentation) {
                                $xl->writeSheetRow($sheet_title, [$documentation]);    
                            }

                            $xl->writeSheetRow($sheet_title, ['']);
                        }

						$isThisHeader = true;
						$currentKeys = [];
						foreach ($val as $testResult) {
							if ($isThisHeader) {
								$currentKeys = array_keys($testResult);
							}

							$result = [];
							foreach($currentKeys as $columnKey) {
                                // skip this columns
                                if ($columnKey === 'stimulusVideoName') {
                                    continue;
                                }
                                if ($name === 'itemsMerged' && $columnKey === 'stimulusSide') {
                                    continue;
                                }

                                if (is_array($testResult[$columnKey])) {
                                    foreach ($testResult[$columnKey] as $nextColumnKey) {

                                        $data = [];
                                        foreach ($nextColumnKey as $newKey => $newVal) {
                                            $data[$newKey] = $newVal;
                                        }

                                        $result[] = $data;
                                    }
                                } else {
                                    $result[$columnKey] = isset($testResult[$columnKey]) ? $testResult[$columnKey] : '';
                                }
                            }
                            
                            foreach ([
                                'stimulusName', 'name'
                            ] as $columnKeyToBreakLine) {
                                if (!empty($result[$columnKeyToBreakLine])) {
                                    if ($columnKeyToBreakLine === 'name' && $assestment_type === 'eye_tracking_pairs') {
                                        continue;
                                    }

                                    if ($blockItemStimulus !== $lastItem = filter_var($result[$columnKeyToBreakLine], FILTER_SANITIZE_NUMBER_INT)) {
                                        $xl->writeSheetRow($sheet_title, ['']);
                                        $blockItemStimulus = $lastItem;
                                    }
                                }
                            }

                            if ($isThisHeader) {
                                if (is_array(@$result[0])) {
                                    if ($assestment_type === 'eye_tracking_pairs') {
                                        $keys = array_map(function ($v) use ($renameVar) {
                                            return $v == 'stimulusAppear' && $renameVar < 4 ? 'videoName' : $v;
                                        }, array_keys($result[0]));
                                    } else {
                                        $keys = array_keys($result[0]);
                                    }

                                    $xl->writeSheetRow($sheet_title, array_map($camelizeString, $keys));
                                } else {
                                    if ($assestment_type === 'eye_tracking_pairs') {
                                        
                                        $keys = array_map(function ($v) use ($renameVar) {
                                            return $v == 'stimulusAppear' && $renameVar < 4 ? 'videoName' : $v;
                                        }, array_keys($result));
                                    } else {
                                        $keys = array_keys($result);
                                    }

                                    $xl->writeSheetRow($sheet_title, array_map($camelizeString, $keys));
                                }

                                $renameVar++;
                            }

                            $result = array_values($result);
                            if (is_array(@$result[0])) {
                                foreach((array)$result as $internalResults) {
                                    if (is_array($internalResults)) {
                                        $xl->writeSheetRow($sheet_title, $internalResults);
                                    }
                                }
                            } else {
                                $replacePreferentialTextToTrial($result);
                                $xl->writeSheetRow($sheet_title, $result);
                            }
							
							$isThisHeader = false;
						}
					} else {
						if (in_array($name, ['startTime', 'endTime'])) {
							$mts = $val/1000;
							$ts = floor($mts);
							$ms = round(($mts-$ts)*1000);
							$val = date('d.m.Y H:i:s', $ts).' '.$ms;
						} 
						$xl->writeSheetRow($sheet_title, [$camelizeString($name), $val]);
					}
                }
			} else if (!empty($items)) {
                if (in_array($assestment_type, [
                    'wheel',
                    'choose_touching',
                    'bubbles_jubbing',
                ])) {
                    $items = $addDpiValue($items, $a['result_data']['xdpi'], $a['result_data']['ydpi'], 'touch');
                }

                if ($assestment_type === 'bubbles_jubbing') {
                    for ($bubbleIndex = 1; $bubbleIndex <= 6; $bubbleIndex++) {
                        $items = $addDpiValue($items, $a['result_data']['xdpi'], $a['result_data']['ydpi'], 'bubble_'. $bubbleIndex);
                    }
                }

				if ($assestment_type=='bubbles_jubbing') {
                    if (isset($items[0])) {
						$xl->writeSheetRow($sheet_title, ['']);

						$row = array_keys($items[0]);
						if (!in_array('bubble', $row)) {
							array_unshift($row, 'bubble');
						}
						$xl->writeSheetRow($sheet_title, $row);

                        foreach ($items as $j=>$fields) {
                            if (!isset($fields['bubble'])) {
								$fields = array_merge(['bubble' => ''], $fields);
							}
							if (isset($fields['time'])) {
								$fields['time'] = ($fields['time']-$a['result_data']['startTime']);
							}
							$row = array_values($fields);
							$xl->writeSheetRow($sheet_title, $row);
                        }
                    }
                } else if ($assestment_type=='parent_assestment') {
					foreach ($items as $questions_type=>$questions) {
						if (isset($questions[0])) {
                            $xl->writeSheetRow($sheet_title, ['']);
                            
							$xl->writeSheetRow($sheet_title, array_keys($questions[0]));

							foreach ($questions as $j=>$fields) {
								$row = [];
								foreach ($fields as $f=>$v4) {
                                    if ($f == 'choices') {
                                        $v4 = implode(', ', $v4['english']);
                                    }

                                    if ($f == 'selected_choice') {
                                        $v4 = $fields['choices']['english'][$v4 - 1];
                                    }

									if ($f=='time') {
										$row[] = ($v4-$a['result_data']['startTime']);
									} else {
										$row[] = $v4;
									}
                                }
								$xl->writeSheetRow($sheet_title, $row);
							}
						}
                    }                    
				} else {
					if (isset($items[0])) {
						$xl->writeSheetRow($sheet_title, ['']);

						$xl->writeSheetRow($sheet_title, array_keys($items[0]));

						foreach ($items as $j=>$fields) {
							$row = [];
							foreach ($fields as $f=>$v4) {
								if ($f=='time') {
									$row[] = ($v4-$a['result_data']['startTime']);
								} else {
									$row[] = $v4;
								}
                            }
							$xl->writeSheetRow($sheet_title, $row);
                        }
                        
                        if (isset($v['stimulus_items'])) {
                            $xl->writeSheetRow($sheet_title, ['']);
                            $xl->writeSheetRow($sheet_title, ['stimulus action', 'time']);

                            foreach ($v['stimulus_items'] as $stimulus_items) {
                                if (isset($stimulus_items['time'])) {
                                    $stimulus_items['time'] = $stimulus_items['time'] - $a['result_data']['startTime'];
                                }
                                $xl->writeSheetRow($sheet_title, $stimulus_items);
                            }
                        }
					}
				}
			}
			$items = [];

			if (!empty($subattempts)) {
				foreach ($subattempts as $sub_a_key=>$sub_attempt) {
					$xl->writeSheetRow($sheet_title, ['']);
					$xl->writeSheetRow($sheet_title, ['Sub-attempt #'.($sub_a_key+1)]);

					if (isset($sub_attempt['items'][0])) {

                        if (in_array($assestment_type, [
                            'motoric_following',
                            'coloring'
                        ])) {
                            $sub_attempt['items'] = $addDpiValue($sub_attempt['items'], $a['result_data']['xdpi'], $a['result_data']['ydpi'], 'touch');
                        }

                        if ($assestment_type === 'motoric_following') {
                            $sub_attempt['items'] = $addDpiValue($sub_attempt['items'], $a['result_data']['xdpi'], $a['result_data']['ydpi'], 'bee');
                        }

						$xl->writeSheetRow($sheet_title, ['']);

						$xl->writeSheetRow($sheet_title, array_keys($sub_attempt['items'][0]));

						foreach ($sub_attempt['items'] as $j=>$fields) {
							$row = [];
							foreach ($fields as $f=>$v4) {
								if ($f=='time') {
									$row[] = ($v4-$a['result_data']['startTime']);
								} else {
									$row[] = $v4;
								}
							}
							$xl->writeSheetRow($sheet_title, $row);
						}
					}

					if (isset($sub_attempt['image_dump'])) {
						// instead of storing image dumps in memory, that can cause memory overflow, wright each dump to temporary file and store filename in array
						$tmp_file = tempnam(sys_get_temp_dir(), 'image_dump_');
						file_put_contents($tmp_file, $sub_attempt['image_dump']);
						$dumps[] = $tmp_file;
						$sub_attempt['image_dump'] = 0;
					}

					if (isset($sub_attempt['scaled_image_dump'])) {
						// instead of storing image dumps in memory, that can cause memory overflow, wright each dump to temporary file and store filename in array
						$tmp_file = tempnam(sys_get_temp_dir(), 'scaled_image_dump_');
						file_put_contents($tmp_file, $sub_attempt['scaled_image_dump']);
						$scaled_dumps[] = $tmp_file;
						$sub_attempt['scaled_image_dump'] = 0;
					}
				}
				unset($subattempts, $sub_attempt);

				if ($dumps) {
					foreach ($dumps as $img_index=>$dump) {
						$dump = json_decode(file_get_contents($dump), 1);
						$dumps[$img_index] = 0;

						$image_sheet_title = $sheet_title.' Image #'.($img_index+1);

						if (isset($dump[0])) {
							$keys = array_keys($dump[0]);
							$xl->writeSheetRow($image_sheet_title, $keys);

							foreach ($dump as $j=>$fields) {
								$row = array_values($fields);
								$xl->writeSheetRow($image_sheet_title, $row);
							}
						}

						if (isset($scaled_dumps[$img_index])) {
							$dump = json_decode(file_get_contents($scaled_dumps[$img_index]), 1);
							$image_sheet_title = $sheet_title.' Scaled image #'.($img_index+1);

							if (isset($dump[0])) {
								$keys = array_keys($dump[0]);
								$xl->writeSheetRow($image_sheet_title, $keys);

								foreach ($dump as $j=>$fields) {
									$row = array_values($fields);
									$xl->writeSheetRow($image_sheet_title, $row);
								}
							}

							$dump = 0;
						}
						$scaled_dumps[$img_index] = 0;
					}
					unset($dump);
				}

				$dumps = [];
				$scaled_dumps = [];
			}
        }

		if (empty($filename)) {
			header('Content-Type: application/octet-stream');
			header('Content-Disposition: attachment; filename='.$assestment_type.'_'.time().'.xlsx');
			header('Content-Transfer-Encoding: binary');
			$xl->writeToStdOut();

			die();
		} else {
			$fpath = sys_get_temp_dir() . '/' . $filename;
			$xl->writeToFile($fpath);

			return $fpath;
		}
	}

	public static function exportSurvey($survey_id, $as_file=0) { // 2017-07-25
		$survey = self::getSurveyById($survey_id);
		$assestment_types = ['bubbles_jubbing', 'choose_touching', 'wheel', 'coloring', 'eye_tracking_pairs', 'motoric_following', 'parent_assestment', 'parent_child_play'];
		$only_types = [];
		if (!empty($_POST['assestment_types']) && is_array($_POST['assestment_types'])) {
			$only_types = $_POST['assestment_types'];
		}
        $assestment_types = array_intersect($assestment_types, $only_types);
        
        if (in_array('eye_tracking_pairs', $assestment_types)) {
            $assestment_types[] = 'eye_tracking_slide';
        }

		//print "<pre>".var_export($all_types, 1)."\n\n".var_export($only_types, 1)."\n\n".var_export($assestment_types, 1)."</pre>"; die();

		if (empty($survey['id'])) {return '';}

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
                $fpath = surveys::exportAssestmentResults($type, $data, 'child_id'.'_survey_'.$survey_id.'_'.$fileName.'_results_'.time().'.xlsx');
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
		$zip_name = 'child_'.$survey['child_id'].'_survey_'.$survey_id.'_results_'.time().'.zip';
		$zip_path = sys_get_temp_dir() . '/' . $zip_name;

		if ($zip->open($zip_path, \ZipArchive::CREATE)===true) {
			foreach ($files as $f) {
				$zip->addFile($f, pathinfo($f, PATHINFO_BASENAME));
			}

			$zip->close();

			foreach ($temporary_files as $f) { // cleanup temporary files
				@unlink($f);
			}
		}

		if (empty($as_file)) {
			header('Content-Type: application/octet-stream');
			header('Content-Disposition: attachment; filename='.$zip_name);
			header('Content-Transfer-Encoding: binary');

			readfile($zip_path);
			unlink($zip_path);

			die();
		} else {
			return $zip_path;
		}
	}

	public static function countSurveys() { // 2017-07-13
		$where = [];
		$where[] = "c.is_deleted='0'";
		$where[] = "s.is_deleted='0'";
		$params = [];

		$where = ($where? ("WHERE ".implode(" AND ", $where)): '');

		$sql = "SELECT COUNT(s.id)
			FROM `children` c
				JOIN `".self::$tbl."` s ON s.child_id=c.id
			{$where}";

		return CMS::$db->get($sql, $params);
	}

	public static function countSurveysByStatus($status) { // 2017-07-13
		$where = [];
		$where[] = "c.is_deleted='0'";
		$where[] = "s.is_deleted='0'";
		$params = [];

		/*if ($status=='new') {
			$where[] = "s.is_inspected='0' AND s.is_closed='0'";
		} else if ($status=='active') {
			$where[] = "s.is_completed='0' AND s.is_closed='0'";
		} else if ($status=='closed') {
			$where[] = "s.is_closed='1'";
		}*/
        if ($status=='new') {
            $where[] = "s.is_inspected='0' AND s.is_closed='0'";
        } else if ($status=='active') {
            $where[] = "s.is_closed='0'";
        } else if ($status=='closed') {
            $where[] = "s.is_closed='1'";
        }
		$where = ($where? ("WHERE ".implode(" AND ", $where)): '');

		$sql = "SELECT COUNT(s.id)
			FROM `children` c
				JOIN `".self::$tbl."` s ON s.child_id=c.id
			{$where}";
		return CMS::$db->get($sql, $params);
	}

	public static function closeSurvey($survey_id) { // 2017-07-24
		$updated = CMS::$db->mod(self::$tbl.'#'.$survey_id, [
			'is_closed' => '1'
		]);

		return $updated;
	}

	public static function openSurvey($survey_id) { // 2017-07-25
		$updated = CMS::$db->mod(self::$tbl.'#'.$survey_id, [
			'is_closed' => '0'
		]);

		return $updated;
	}

    public static function deleteSurvey($survey_id) { // 2017-07-24
        $survey_id = (int)$survey_id;

        //$deleted = CMS::$db->exec('DELETE FROM `'.self::$tbl.'` WHERE `id`=:id', [':id' => $user_id]);

        $updated = CMS::$db->mod(self::$tbl.'#'.$survey_id, [
            'is_deleted' => '1'
        ]);

        return $updated;
    }

    public static function touchSurvey($survey_id) { // 2017-08-30
        $survey_id = (int)$survey_id;

        $touched = CMS::$db->mod(self::$tbl.'#'.$survey_id, [
            'is_inspected' => '1'
        ]);

        return $touched;
    }
}

?>