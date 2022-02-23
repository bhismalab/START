<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined('_VALID_PHP')) {die('Direct access to this location is not allowed.');}

?>


<style>
.profile-child-img {
	border-radius: 5%; border: 2px solid #fff; box-shadow: 0 3px 5px rgba(0,0,0,0.3);
}
.parent-signature {
	max-height: 120px; border: 2px solid #8d6fde;
}

#divChildControls a {
	margin-right: 40px;
}
#divChildControls a:last-child {
	margin-right: 0px;
}
</style>


<!-- Content Header (Page header) -->
<section class="content-header">
	<h1>
		<?=CMS::t('menu_item_children_view_info');?>
		<small><?=utils::safeEcho($child['name'].' '.$child['surname'].' '.$child['patronymic'], 1);?></small>
	</h1>
</section>


<!-- Main content -->
<section class="content">
	<div class="row">
		<div class="col-md-12">
			<nav class="top-toolbar-stripe">
				<a href="<?=utils::safeEcho($link_back, 1);?>" title="<?=CMS::t('back');?>" class="toolbar-btn-primary"><i class="fa fa-arrow-circle-o-left" aria-hidden="true"></i> Back</a>

				<?=view::widget('breadcrumb', ['links' => [
					[
						'title' => 'Children',
						'href' => '?controller=children&action=list',
						'icon' => 'home'
					], [
						'title' => CMS::t('menu_item_children_view_info'),
						'icon' => 'child'
					]
				]]);?>
			</nav>
		</div>
	</div>

	<div class="row">
		<div class="col-md-3">

			<!-- Profile Image -->
			<div class="box box-primary">
				<div class="box-body box-profile">
					<?php if (!empty($child['photo'])) { ?>
					<img src="?controller=<?=$_GET['controller'];?>&amp;action=download_child_photo&amp;child_id=<?=$child['id'];?>" alt="" class="profile-child-img img-responsive" />
					<?php } ?>

					<?php if (ADMIN_TYPE!='researcher') { ?>
					<h3 class="profile-childname text-center"><?=utils::safeEcho($child['name'].' '.$child['surname'].' '.$child['patronymic'], 1);?></h3>
					<?php } else { ?>
					<h3 class="profile-childname text-center">------- --------</h3>
					<?php } ?>

					<!-- <p class="text-muted text-center">Software Engineer</p> -->

					<ul class="list-group list-group-unbordered">
						<?php if (ADMIN_TYPE!='researcher') { ?>
						<li class="list-group-item text-center">
							<?=utils::formatMySQLDate(CMS::$date_format, $child['birth_date']);?>
						</li>
						<?php } ?>
						<li class="list-group-item">
							<b><?=CMS::t('survey_status');?>:</b>
							<?php
							/*$survey_status = 'active';
							if ($survey['is_closed']) {
								$survey_status = 'closed';
							} else if ($survey['is_completed']) {
								$survey_status = 'finished';
							}*/

                            if ($survey['is_closed']=='0') {
                                $active = 'Active';
                            } else if ($survey['is_closed']=='1') {
                                $active = 'Closed';
                            }

							if ($survey['is_completed'] == '0') {
                                $survey_status = 'not_finished';
                            } else if ($survey['is_completed'] == '1') {
                                $survey_status = 'finished';
                            }
							?>
							<?=$active." (".CMS::t('survey_status_'.$survey_status).")";?>
							<?php
								if ($survey['is_closed']=='0') {
									?>
									<!-- <div>
										<a href="?controller=<?=$_GET['controller'];?>&amp;action=close&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>" class="btn btn-context" style="display: block; margin: 10px auto;"><i class="fa fa-lock" aria-hidden="true"></i> Close</a>
									</div> -->
									<?php
								}
							?>
						</li>

						<li class="list-group-item">
							<b><?=CMS::t('survey_date');?>:</b>
							<time datetime="<?=utils::formatMySQLDate('c', $survey['created_datetime']);?>" title="<?=$survey['created_datetime'];?>"><?=utils::formatMySQLDate('d.m.Y', $survey['created_datetime']);?></time>
						</li>
					</ul>

					<!-- <a href="#" class="btn btn-primary btn-block"><b>Follow</b></a> -->
				</div>
				<!-- /.box-body -->
			</div>
			<!-- /.box (Profile Image) -->

			<div class="box box-default">
				<div class="box-body text-center" id="divChildControls">
					<?php if (CMS::hasAccessTo('children/download_child_survey')) { ?>
					<a href="?controller=<?=$_GET['controller'];?>&amp;action=download_child_survey&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>"><img src="<?=IMAGE_DIR;?>purple-icon-download.png" alt="" /></a>
					<?php } ?>
					<?php if (CMS::hasAccessTo('children/close_survey') && ($survey['is_closed']=='0')) { ?>
					<a href="?controller=<?=$_GET['controller'];?>&amp;action=close_survey&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>&amp;return=<?=urlencode(utils::trueLink(['controller', 'action', 'child_id', 'survey_id']));?>"><img src="<?=IMAGE_DIR;?>purple-icon-open.png" alt="" /></a>
					<?php } ?>
					<?php if (CMS::hasAccessTo('children/open_survey') && ($survey['is_closed']=='1')) { ?>
					<a href="?controller=<?=$_GET['controller'];?>&amp;action=open_survey&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>&amp;return=<?=urlencode(utils::trueLink(['controller', 'action', 'child_id', 'survey_id']));?>"><img src="<?=IMAGE_DIR;?>purple-icon-close.png" alt="" /></a>
					<?php } ?>
					<?php if (CMS::hasAccessTo('children/view_info')) { ?>
					<a href="?controller=<?=$_GET['controller'];?>&amp;action=view_info&amp;id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>"><img src="<?=IMAGE_DIR;?>purple-icon-info.png" alt="" /></a>
					<?php } ?>
					<?php /* if (CMS::hasAccessTo('children/delete')) { ?>
					<a href="?controller=<?=$_GET['controller'];?>&amp;action=delete&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>"><img src="<?=IMAGE_DIR;?>purple-icon-trash.png" alt="" /></a>
					<?php } */ ?>
				</div>
				<!-- /.box-body -->
			</div>

		</div>
		<!-- /.col -->

		<div class="col-md-9">
			<div class="row">
				<div class="col-md-6">
					<div class="box box-solid">
						<div class="box-header with-border">
							<h3 class="box-title">1. Eye-tracking test</h3>

							<?php if (!empty($survey_results['eye_tracking_pairs'])) { ?>
							<div class="box-tools pull-right">
								<a href="?controller=<?=$_GET['controller'];?>&amp;action=download_child_survey_assestment&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>&amp;assestment_type=eye_tracking_pairs" class="btn btn-box-tool" title="Download assessment">
									<i class="fa fa-download"></i>
								</a>
							</div>
							<?php } ?>
						</div>

						<div class="box-body">
                            <?php
                                // have a data structure good for this...
                                $survey_results_combined = [];
                                foreach ($survey_results['eye_tracking_pairs'] as $pairs) {
                                    $survey_results_combined[$pairs['attempt']]['pairs'] = $pairs;
                                }

                                foreach ($survey_results['eye_tracking_slide'] as $slide) {
                                    $survey_results_combined[$slide['attempt']]['slide'] = $slide;
                                }
                            ?>
							<!-- pairs -->
							<?php if (!empty($survey_results['eye_tracking_pairs']) || !empty($survey_results['eye_tracking_slide'])) { ?>
							<?php foreach ($survey_results_combined as $attemp=>$a) { ?>
							<!-- <p><strong>Image pairs eye tracking assestment</strong></p> -->
							<?php if (count($survey_results_combined) > 1) { ?><?=(($attemp>1)? '<hr />': '');?><p><strong>Attempt: #<?=$attemp;?></strong></p><?php } ?>
							
                            <?php if (!empty($a['slide']['result_data']['testAttention']['calibrationQuality'])) { ?>
							<p>Calibration Quality Attention: <?=$a['slide']['result_data']['testAttention']['calibrationQuality'] == '-1' ? 'n/a' : $a['slide']['result_data']['testAttention']['calibrationQuality'];?></p>
							<?php } ?>
							
                            <?php if (!empty($a['pairs']['result_data']['testLooking']['calibrationQuality'])) { ?>
							<p>Calibration Quality Looking: <?=$a['pairs']['result_data']['testLooking']['calibrationQuality'] == '-1' ? 'n/a' : $a['pairs']['result_data']['testLooking']['calibrationQuality'];?></p>
							<?php } ?>

                            <?php if (!empty($a['slide']['result_data']['testAttention']['gazeDetection']) || !empty($a['pairs']['result_data']['testLooking']['gazeDetection'])) { ?>
							<p>Gaze Detection: <?php
								$data = [];
								
								if (!empty($a['slide']['result_data']['testAttention']['gazeDetection'])) {
									$data[] = $a['slide']['result_data']['testAttention']['gazeDetection'];
								}

								if (!empty($a['pairs']['result_data']['testLooking']['gazeDetection'])) {
									$data[] = $a['pairs']['result_data']['testLooking']['gazeDetection'];
								}

								echo array_sum($data) / count($data);
								?>%</p>
							<?php } ?>

                            <?php if (!empty($a['slide']['result_data']['testAttention']['gazeOnTheScreen']) || !empty($a['pairs']['result_data']['testLooking']['gazeOnTheScreen'])) { ?>
							<p>Gaze on the Screen: <?php
								$data = [];
								
								if (!empty($a['slide']['result_data']['testAttention']['gazeOnTheScreen'])) {
									$data[] = $a['slide']['result_data']['testAttention']['gazeOnTheScreen'];
								}

								if (!empty($a['pairs']['result_data']['testLooking']['gazeOnTheScreen'])) {
									$data[] = $a['pairs']['result_data']['testLooking']['gazeOnTheScreen'];
								}

								echo array_sum($data) / count($data);
								?>%</p>
							<?php } ?>

                            <p>Assessment status: <?=($a['pairs']['result_data']['testLooking']['interrupted'] || $a['slide']['result_data']['testAttention']['interrupted']? 'Interrupted': 'Finished');?></p>

                            <p>Duration: <?=(max($a['slide']['result_data']['testAttention']['endTime'], $a['pairs']['result_data']['testLooking']['endTime'])-min($a['slide']['result_data']['testAttention']['startTime'], $a['pairs']['result_data']['testLooking']['startTime']));?> ms</p>

                            <?php if (!empty($a['pairs']['result_data']['deviceId'])) { ?>
							<p>Device ID: <?=$a['pairs']['result_data']['deviceId'];?></p>
							<?php } ?>

                            <?php

                            if (!empty($attachments['eye_tracking_pairs'][$attemp - 1]['attachment_file'])) {
                                if (in_array(utils::getFileExt($attachments['eye_tracking_pairs'][$attemp - 1]['attachment_file']), ['jpg', 'bmp', 'png'])) { ?>
                                    <img src="<?=$attachments['eye_tracking_pairs'][$attemp - 1]['attachment_file'];?>" alt="" class="img-responsive" style="background: #eee;" />
                                <?php } else if (in_array(utils::getFileExt($attachments['eye_tracking_pairs'][$attemp - 1]['attachment_file']), ['mp4', 'mov'])) { ?>
                                    <p>Assessment video: <a href="<?=$attachments['eye_tracking_pairs'][$attemp - 1]['attachment_file'];?>" target="_blank"><?=pathinfo($attachments['eye_tracking_pairs'][$attemp - 1]['attachment_file'], PATHINFO_BASENAME);?></a></p>
                                <?php }
                            }

                            if (!empty($attachments['eye_tracking_slide'][$attemp - 1]['attachment_file'])) {
                                if (in_array(utils::getFileExt($attachments['eye_tracking_slide'][$attemp - 1]['attachment_file']), ['jpg', 'bmp', 'png'])) { ?>
                                    <img src="<?=$attachments['eye_tracking_slide'][$attemp - 1]['attachment_file'];?>" alt="" class="img-responsive" style="background: #eee;" />
                                <?php } else if (in_array(utils::getFileExt($attachments['eye_tracking_slide'][$attemp - 1]['attachment_file']), ['mp4', 'mov'])) { ?>
                                    <p>Assessment video: <a href="<?=$attachments['eye_tracking_slide'][$attemp - 1]['attachment_file'];?>" target="_blank"><?=pathinfo($attachments['eye_tracking_slide'][$attemp - 1]['attachment_file'], PATHINFO_BASENAME);?></a></p>
                                <?php }
                            }
                                ?>

							<div style="display: none;" id="divEyeTrackingPairsData_<?=$attemp;?>" data-filename="<?=$a['result_file'];?>" data-time="<?=time();?>">

								<table class="table table-bordered table-striped">
									<?php

									foreach([
										'Attention test' => $a['slide']['result_data']['testAttention'],
										'Looking test' => $a['pairs']['result_data']['testLooking']
									] as $testName => $test) {
										?>

										<tr><th colspan="9"><?= $testName; ?>: Eye tracking</th></tr>

										<tr>
										<?php
										foreach ($labels = [
											'time', 'gazeOut', 'gazeDetect', 'gazeX', 'gazeY', '', '', '', ''
										] as $label) { ?>
											<td><?=$label;?></td>
										<?php } ?>
										</tr>

										<tr>
										<?php
										foreach ($test['itemsEyeTracking'] as $dataEye) {
											?>
											<tr>
											<?php foreach ($labels as $label) { ?>
											<td><?= isset($dataEye[$label]) ? htmlspecialchars($dataEye[$label]) : ''; ?></td>
											<?php
											}
											?>
											</tr>
											<?php
										}
										?>
										</tr>

										<tr><th colspan="9"><?= $testName; ?>: Stimulus</th></tr>

										<tr>
										<?php
										foreach ($labels = [
											'time', 'name', 'videoName', 'stimulusAppear', 'stimulusSide', '', '', '', ''
										] as $label) { ?>
											<td><?=$label;?></td>
										<?php } ?>
										</tr>

										<?php
										foreach ($test['itemsStimulus'] as $itemsStimulus) {
											foreach ($itemsStimulus['itemsStimulus'] as $stimulis) {
											?>
											<tr>
											<td><?= htmlspecialchars($stimulis['time']); ?></td>
											<td><?= htmlspecialchars($itemsStimulus['name']); ?></td>
											<td><?= htmlspecialchars($itemsStimulus['videoName']); ?></td>
											<td><?= htmlspecialchars($stimulis['stimulusAppear']); ?></td>
											<td><?= htmlspecialchars($stimulis['stimulusSide']); ?></td>
											<td></td>
											<td></td>
											<td></td>
											<td></td>
											</tr>
											<?php
											}
										}
										?>
										</tr>

										<tr><th colspan="9"><?= $testName; ?>: Merged</th></tr>

										<tr>
										<?php
										foreach ($labels = [
											'time', 'gazeOut', 'gazeDetect', 'gazeX', 'gazeY', 'stimulusAppear', 'stimulusName', 'stimulusSide', 'stimulusVideoName'
										] as $label) { ?>
											<td><?=$label;?></td>
										<?php } ?>
										</tr>

										<?php
										foreach ($test['itemsMerged'] as $itemsMerged) {
											?>
											<tr>
											<td><?= htmlspecialchars($itemsMerged['time']); ?></td>
											<td><?= htmlspecialchars($itemsMerged['gazeOut']); ?></td>
											<td><?= htmlspecialchars($itemsMerged['gazeDetect']); ?></td>
											<td><?= htmlspecialchars($itemsMerged['gazeX']); ?></td>
											<td><?= htmlspecialchars($itemsMerged['gazeY']); ?></td>
											<td><?= htmlspecialchars($itemsMerged['stimulusAppear']); ?></td>
											<td><?= htmlspecialchars($itemsMerged['stimulusName']); ?></td>
											<td><?= htmlspecialchars($itemsMerged['stimulusSide']); ?></td>
											<td><?= htmlspecialchars($itemsMerged['stimulusVideoName']); ?></td>
											</tr>
											<?php
										}
										?>
									<?php
									}
									?>
								</table>
							</div>
							<p><a href="#divEyeTrackingPairsData_<?=$attemp;?>" class="fancybox">View results</a></p>
							<!-- <p>Assessment video: View / Download</p> -->

							<?php } ?>
							<?php } ?>


							<?php if (0) { /* Switch off slides output */ ?>
							<hr />

							<!-- slide -->
							<?php if (!empty($survey_results['eye_tracking_slide'])) { ?>
							<?php foreach ($survey_results['eye_tracking_slide'] as $i=>$a) { ?>
							<p><strong>Slide eye tracking assestment</strong></p>
							<?php if (count($survey_results['eye_tracking_slide'])>1) { ?><p>Attempt: #<?=$a['attempt'];?></p><?php } ?>
							<p>Data Quality: <?=$a['result_data']['dataQuality'];?>%</p>
							<p>Assessment status: <?=($a['result_data']['interrupted']? 'Interrupted': 'Finished');?></p>
							<p>Duration: <?=($a['result_data']['endTime']-$a['result_data']['startTime']);?> ms  <?php //date('d.m.Y H:i:s', round($a['result_data']['startTime']/1000));?>  <?php //date('d.m.Y H:i:s', round($a['result_data']['endTime']/1000));?> </p>
							<div style="display: none;" id="divEyeTrackingSlideData_<?=$a['id'];?>" data-filename="<?=$a['result_file'];?>" data-time="<?=time();?>">
								<table class="table table-bordered table-striped">
									<?php foreach ($a['result_data']['items'] as $test) { ?>
									<tr><th colspan="5"><?=$test['name'];?></td></tr>
									<?php
									$keys = @array_keys($test['items'][0]);
									if ($keys) {
										?><tr>
										<?php foreach ($keys as $k) { ?>
											<td><?=$k;?></td>
										<?php } ?>
										</tr><?php

										foreach ($test['items'] as $row) {
											?><tr>
											<td><?=$row['stimulusAppear'];?></td>
											<td><?=($row['time']-$a['result_data']['startTime']);?></td>
											<td><?=($row['valid']? 'Yes': 'No');?></td>
											<td><?=$row['gazeX'];?></td>
											<td><?=$row['gazeY'];?></td>
											</tr><?php
										}
									}
									?>
									<?php } ?>
								</table>
							</div>
							<p>Assessment results: <a href="#divEyeTrackingSlideData_<?=$a['id'];?>" class="fancybox">View</a> / Download</p>
							<!-- <p>Assessment video: View / Download</p> -->
							<?php } ?>
							<?php } ?>
							<?php } /* Switch off slides output END */ ?>
						</div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="box box-solid">
						<div class="box-header with-border">
							<h3 class="box-title">2. Wheel test</h3>

							<?php if (!empty($survey_results['wheel'])) { ?>
							<div class="box-tools pull-right">
								<a href="?controller=<?=$_GET['controller'];?>&amp;action=download_child_survey_assestment&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>&amp;assestment_type=wheel" class="btn btn-box-tool" title="Download assessment">
									<i class="fa fa-download"></i>
								</a>
							</div>
							<?php } ?>
						</div>

						<div class="box-body">
							<?php if (!empty($survey_results['wheel'])) { ?>
							<?php foreach ($survey_results['wheel'] as $i=>$a) { ?>
							<?php if (count($survey_results['wheel'])>1) { ?><p>Attempt: #<?=$a['attempt'];?></p><?php } ?>
							<p>Assessment status: <?=($a['result_data']['interrupted']? 'Interrupted': 'Finished');?></p>
							<p>Duration: <?=($a['result_data']['endTime']-$a['result_data']['startTime']);?> ms</p>
							<?php if (!empty($a['result_data']['deviceId'])) { ?>
							<p>Device ID: <?=$a['result_data']['deviceId'];?></p>
							<?php } ?>

							<div style="display: none;" id="divWheelData_<?=$a['id'];?>" data-filename="<?=$a['result_file'];?>" data-time="<?=time();?>">
								<table class="table table-bordered table-striped">
									<?php if (isset($a['result_data']['items'][0])) { ?>
									<thead>
										<tr>
											<?php
											foreach ($a['result_data']['items'][0] as $k=>$v) {
												?><td><?=$k;?></td><?php
											}
											?>
										</tr>
									</thead>
									<tbody>
										<?php foreach ($a['result_data']['items'] as $item) { ?>
										<tr>
                                            <?php
											if (count($item)) {
												foreach ($item as $t=>$i) {
													if ($t == 'time') {
														echo "<td>", ($i - $a['result_data']['startTime']), "</td>";
													} else if ($t == "touch") {
														echo "<td>", ($item['touch']? 'Yes': 'No'), "</td>";
													} else{
														echo "<td>", $i, "</td>";
													}
												}
											}
											?>
										</tr>
										<?php } ?>
									</tbody>
									<?php } ?>
								</table>
							</div>
							<p><a href="#divWheelData_<?=$a['id'];?>" class="fancybox">View results</a></p>
							<?php } ?>
							<?php } else { ?>
							<p>No results</p>
							<?php } ?>

							<?php if (!empty($attachments['wheel'])) { ?>
								<?php foreach ($attachments['wheel'] as $a) { ?>
									<?php if (in_array(utils::getFileExt($a['attachment_file']), ['jpg', 'bmp', 'png'])) { ?>
										<img src="<?=$a['attachment_file'];?>" alt="" class="img-responsive" style="background: #eee;" />
									<?php } else if (in_array(utils::getFileExt($a['attachment_file']), ['mp4', 'mov'])) { ?>
										<p>Assessment video: <a href="<?=$a['attachment_file'];?>" target="_blank"><?=pathinfo($a['attachment_file'], PATHINFO_BASENAME);?></a></p>
									<?php } ?>
								<?php } ?>
							<?php } ?>
						</div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="box box-solid">
						<div class="box-header with-border">
							<h3 class="box-title">3. Touch test</h3>

							<?php if (!empty($survey_results['choose_touching'])) { ?>
							<div class="box-tools pull-right">
								<a href="?controller=<?=$_GET['controller'];?>&amp;action=download_child_survey_assestment&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>&amp;assestment_type=choose_touching" class="btn btn-box-tool" title="Download assessment">
									<i class="fa fa-download"></i>
								</a>
							</div>
							<?php } ?>
						</div>

						<div class="box-body">
							<?php if (!empty($survey_results['choose_touching'])) { ?>
							<?php foreach ($survey_results['choose_touching'] as $i=>$a) { ?>
							<?php if (count($survey_results['choose_touching'])>1) { ?><p>Attempt: #<?=$a['attempt'];?></p><?php } ?>
							<p>Assessment status: <?=($a['result_data']['interrupted']? 'Interrupted': 'Finished');?></p>
							<p>Duration: <?=($a['result_data']['endTime']-$a['result_data']['startTime']);?> ms  <?php //date('d.m.Y H:i:s', round($a['result_data']['startTime']/1000));?>  <?php //date('d.m.Y H:i:s', round($a['result_data']['endTime']/1000));?> </p>
							<p>Green button: <?=$a['result_data']['greenClickCount'];?></p>
							<p>Red button: <?=$a['result_data']['redClickCount'];?></p>
							<?php if (!empty($a['result_data']['deviceId'])) { ?>
							<p>Device ID: <?=$a['result_data']['deviceId'];?></p>
							<?php } ?>

							<div style="display: none;" id="divChooseTouchingData_<?=$a['id'];?>" data-filename="<?=$a['result_file'];?>" data-time="<?=time();?>">
								<table class="table table-bordered table-striped">
									<?php if (isset($a['result_data']['items'][0])) { ?>
									<thead>
										<tr>
											<?php
											foreach ($a['result_data']['items'][0] as $k=>$v) {
												?><td><?=$k;?></td><?php
											}
											?>
										</tr>
									</thead>
									<tbody>
										<?php foreach ($a['result_data']['items'] as $item) { ?>
										<tr>
                                            <?php
                                        if (count($item)) {
											
											foreach ($item as $t=>$i) {
												if ($t == 'time') {
													echo "<td>", ($i - $a['result_data']['startTime']), "</td>";
												} else if ($t == "touch") {
													echo "<td>", ($item['touch']? 'Yes': 'No'), "</td>";
												} else {
													echo "<td>", $i, "</td>";
												}
											}
											
                                            
                                        }?>
											<!--<td><?php /*if(isset($item['button'])) echo $item['button'];*/?></td>
											<td><?php /*if(isset($item['device_x'])) echo $item['device_x'];*/?></td>
											<td><?php /*$item['device_y'];*/?></td>
											<td><?php /*$item['device_z'];*/?></td>
											<td><?php /*($item['time']-$a['result_data']['startTime']);*/?></td>
											<td><?php /*($item['touch']? 'Yes': 'No');*/?></td>
											<td><?php /*$item['x'];*/?></td>
											<td><?php /*$item['y'];*/?></td>
											<td><?php /*$item['z'];*/?></td>-->
										</tr>
										<?php } ?>
									</tbody>
									<?php } ?>
								</table>
							</div>
							<p><a href="#divChooseTouchingData_<?=$a['id'];?>" class="fancybox">View results</a></p>
							<!-- <p>Assessment video: View / Download</p> -->
							<?php } ?>
							<?php } else { ?>
							<p>No results</p>
							<?php } ?>
						</div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="box box-solid">
						<div class="box-header with-border">
							<h3 class="box-title">4. Motor Following test</h3>

							<?php if (!empty($survey_results['motoric_following'])) { ?>
							<div class="box-tools pull-right">
								<a href="?controller=<?=$_GET['controller'];?>&amp;action=download_child_survey_assestment&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>&amp;assestment_type=motoric_following" class="btn btn-box-tool" title="Download assessment">
									<i class="fa fa-download"></i>
								</a>
							</div>
							<?php } ?>
						</div>

						<div class="box-body">
							<?php if (!empty($survey_results['motoric_following'])) { ?>
							<?php foreach ($survey_results['motoric_following'] as $i=>$a) { ?>
							<?php if (count($survey_results['motoric_following'])>1) { ?><p><strong>Attempt: #<?=$a['attempt'];?></strong></p><?php } ?>
							<p>Assessment status: <?=($a['result_data']['interrupted']? 'Interrupted': 'Finished');?></p>
							<p>Duration: <?=($a['result_data']['endTime']-$a['result_data']['startTime']);?> ms  <?php //date('d.m.Y H:i:s', round($a['result_data']['startTime']/1000));?>  <?php //date('d.m.Y H:i:s', round($a['result_data']['endTime']/1000));?> </p>
							<?php if (!empty($a['result_data']['deviceId'])) { ?>
							<p>Device ID: <?=$a['result_data']['deviceId'];?></p>
							<?php } ?>

							<div style="display: none;" id="divMotoricFollowingData_<?=$a['id'];?>" data-filename="<?=$a['result_file'];?>" data-time="<?=time();?>">
								<?php
								if (!empty($a['result_data']['attempts'])) {
									foreach ($a['result_data']['attempts'] as $a_key=>$attempt) {
								?>
								<p>Sub-attempt #<?=($a_key+1);?></p>
								<table class="table table-bordered table-striped">
									<?php if (isset($attempt['items'][0])) { ?>
									<thead>
										<tr>
											<?php
											foreach ($attempt['items'][0] as $k=>$v) {
												?><td><?=$k;?></td><?php
											}
											?>
										</tr>
									</thead>
									<tbody>
										<?php foreach ($attempt['items'] as $item) { ?>
										<tr>
											<?php foreach ($item as $fk=>$f) { ?>
											<td><?=utils::safeEcho((($fk=='time')? ($item[$fk]-$a['result_data']['startTime']): $item[$fk]), 1);?></td>
											<?php } ?>
										</tr>
										<?php } ?>
									</tbody>
									<?php } ?>
								</table>
								<?php
									}
								}
								?>
							</div>
							<p><a href="#divMotoricFollowingData_<?=$a['id'];?>" class="fancybox">View results</a></p>
							<!-- <p>Assessment video: View / Download</p> -->

							<?php /*if (!empty($attachments['motoric_following'][$i]['attachment_file'])) { ?>
								<?php if (in_array(utils::getFileExt($attachments['motoric_following'][$i]['attachment_file']), ['jpg', 'bmp', 'png'])) { ?>
									<img src="<?=$attachments['motoric_following'][$i]['attachment_file'];?>" alt="" class="img-responsive" style="background: #eee;" />
								<?php } ?>
							<?php }*/ ?>

							<?php
							if (!empty($attachments['motoric_following'])) {
								$per_portion = 4;
								$width = '23%';
								if ($survey['created_datetime']>'2017-08-11') {
									$per_portion = 4;
									$width = '24%';
								} else {
									$per_portion = 3;
									$width = '32.5%';
								}
								$bunch = array_slice($attachments['motoric_following'], ($i*$per_portion), $per_portion);
								foreach ($bunch as $attempt) {
									if (in_array(utils::getFileExt($attempt['attachment_file']), ['jpg', 'bmp', 'png'])) {
										?>
										<a href="<?=$attempt['attachment_file'];?>" class="fancybox">
											<img src="<?=$attempt['attachment_file'];?>" alt="" class="img-responsive" style="width: <?=$width;?>; display: inline-block; border: 1px solid #ccc;" />
										</a> 
										<?php
									}
								}
							}
							?>

							<?php } ?>

							<?php
							if (!empty($attachments['motoric_following'])) {
								foreach ($attachments['motoric_following'] as $attempt) {
									if (in_array(utils::getFileExt($attempt['attachment_file']), ['mp4', 'mov'])) {
										?>
										<p>
											Assessment video: <a href="<?=$attempt['attachment_file'];?>" target="_blank"><?=pathinfo($attempt['attachment_file'], PATHINFO_BASENAME);?></a>
										</p>
										<?php
									}
								}
							}
							?>

							<?php } else { ?>
							<p>No results</p>
							<?php } ?>
						</div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="box box-solid">
						<div class="box-header with-border">
							<h3 class="box-title">5. Pop bubbles test</h3>

							<?php if (!empty($survey_results['bubbles_jubbing'])) { ?>
							<div class="box-tools pull-right">
								<a href="?controller=<?=$_GET['controller'];?>&amp;action=download_child_survey_assestment&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>&amp;assestment_type=bubbles_jubbing" class="btn btn-box-tool" title="Download assessment">
									<i class="fa fa-download"></i>
								</a>
							</div>
							<?php } ?>
						</div>

						<div class="box-body">
							<?php if (!empty($survey_results['bubbles_jubbing'])) { ?>
							<?php foreach ($survey_results['bubbles_jubbing'] as $i=>$a) { ?>
							<?php if (count($survey_results['bubbles_jubbing'])>1) { ?><p><strong>Attempt: #<?=$a['attempt'];?></strong></p><?php } ?>
							<p>Assessment status: <?=($a['result_data']['interrupted']? 'Interrupted': 'Finished');?></p>
							<p>Duration: <?=($a['result_data']['endTime']-$a['result_data']['startTime']);?> ms  <?php //date('d.m.Y H:i:s', round($a['result_data']['startTime']/1000));?>  <?php //date('d.m.Y H:i:s', round($a['result_data']['endTime']/1000));?> </p>
							<p>Bubbles popped: <?=$a['result_data']['bubblesPopped'];?></p>
							<p>Bubbles total: <?=$a['result_data']['bubblesTotal'];?></p>
							<?php if (!empty($a['result_data']['deviceId'])) { ?>
							<p>Device ID: <?=$a['result_data']['deviceId'];?></p>
							<?php } ?>

							<div style="display: none;" id="divBubblesJubbingData_<?=$a['id'];?>" data-filename="<?=$a['result_file'];?>" data-time="<?=time();?>">
								<table class="table table-bordered table-striped">
									<?php if (isset($a['result_data']['items'][0])) { ?>
									<thead>
										<tr>
											<td>Bubble</td>
											<?php
											foreach ($a['result_data']['items'][0] as $k=>$v) {
												?><td><?=$k;?></td><?php
											}
											?>
										</tr>
									</thead>
									<tbody>
										<?php foreach ($a['result_data']['items'] as $item) { ?>
										<tr>
                                            <?php
                                            if (count($item)) {
                                                if (array_keys($item)[0] == 'bubble') {
													echo "<td>", $item["bubble"], "</td>";
													unset($item["bubble"]);
												} else {
													echo "<td></td>";
												}

												foreach ($item as $z=>$i) {
													if ($z == 'time') {
														echo "<td>", ($i - $a['result_data']['startTime']), "</td>";
													} else {
														echo "<td>", $i, "</td>";
													}
												}
                                            }
                                            ?>
											<!--<td><?/*=$item['bubble'];*/?></td>
											<td><?/*=$item['bubble_1_x'];*/?></td>
											<td><?/*=$item['bubble_1_y'];*/?></td>
											<td><?/*=$item['bubble_2_x'];*/?></td>
											<td><?/*=$item['bubble_2_y'];*/?></td>
											<td><?/*=$item['bubble_3_x'];*/?></td>
											<td><?/*=$item['bubble_3_y'];*/?></td>
											<td><?/*=$item['bubble_4_x'];*/?></td>
											<td><?/*=$item['bubble_4_y'];*/?></td>
											<td><?/*=$item['bubble_5_x'];*/?></td>
											<td><?/*=$item['bubble_5_y'];*/?></td>
											<td><?/*=$item['bubble_6_x'];*/?></td>
											<td><?/*=$item['bubble_6_y'];*/?></td>
											<td><?/*=$item['device_x'];*/?></td>
											<td><?/*=$item['device_y'];*/?></td>
											<td><?/*=$item['device_z'];*/?></td>
											<td><?/*=($item['time']-$a['result_data']['startTime']);*/?></td>
											<td><?/*=$item['x'];*/?></td>
											<td><?/*=$item['y'];*/?></td>
											<td><?/*=$item['z'];*/?></td>-->
										</tr>
										<?php } ?>
									</tbody>
									<?php } ?>
								</table>
							</div>
							<p><a href="#divBubblesJubbingData_<?=$a['id'];?>" class="fancybox">View results</a></p>
							<!-- <p>Assessment video: View / Download</p> -->
							<?php } ?>
							<?php } else { ?>
							<p>No results</p>
							<?php } ?>
						</div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="box box-solid">
						<div class="box-header with-border">
							<h3 class="box-title">6. Colouring test</h3>

							<?php if (!empty($survey_results['coloring'])) { ?>
							<div class="box-tools pull-right">
								<a href="?controller=<?=$_GET['controller'];?>&amp;action=download_child_survey_assestment&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>&amp;assestment_type=coloring" class="btn btn-box-tool" title="Download assessment">
									<i class="fa fa-download"></i>
								</a>
							</div>
							<?php } ?>
						</div>

						<div class="box-body">
							<?php if (!empty($survey_results['coloring'])) { ?>
							<?php foreach ($survey_results['coloring'] as $i=>$a) { ?>
							<?php if (count($survey_results['coloring'])>1) { ?><p><strong>Attempt: #<?=$a['attempt'];?></strong></p><?php } ?>
							<p>Assessment status: <?=($a['result_data']['interrupted']? 'Interrupted': 'Finished');?></p>
							<p>Duration: <?=($a['result_data']['endTime']-$a['result_data']['startTime']);?> ms  <?php //date('d.m.Y H:i:s', round($a['result_data']['startTime']/1000));?>  <?php //date('d.m.Y H:i:s', round($a['result_data']['endTime']/1000));?> </p>
							<?php if (!empty($a['result_data']['deviceId'])) { ?>
							<p>Device ID: <?=$a['result_data']['deviceId'];?></p>
							<?php } ?>

							<div style="display: none;" id="divColoringData_<?=$a['id'];?>" data-filename="<?=$a['result_file'];?>" data-time="<?=time();?>">
								<?php
								if (!empty($a['result_data']['attempts'])) {
									foreach ($a['result_data']['attempts'] as $a_key=>$attempt) {
								?>
								<p>Sub-attempt #<?=($a_key+1);?></p>
								<table class="table table-bordered table-striped">
									<?php if (isset($attempt['items'][0])) { ?>
									<thead>
										<tr>
											<?php

                                            if (isset($a['result_data']['attempts'][0]['items'][0]) && is_array($a['result_data']['attempts'][0]['items'][0])) {
                                                foreach ($a['result_data']['attempts'][0]['items'][0] as $k=>$v) {
                                                    ?><td><?=$k;?></td><?php
                                                }
                                            }

											?>
										</tr>
									</thead>
									<tbody>
										<?php foreach ($attempt['items'] as $item) { ?>
										<tr>
											<?php foreach ($item as $fk=>$f) { ?>
											<td><?=utils::safeEcho((($fk=='time')? ($item[$fk]-$a['result_data']['startTime']): $item[$fk]), 1);?></td>
											<?php } ?>
										</tr>
										<?php } ?>
									</tbody>
									<?php } ?>
								</table>
								<?php
									}
								}
								?>
							</div>
							<p><a href="#divColoringData_<?=$a['id'];?>" class="fancybox">View results</a></p>
                            <?php
                            if (!empty($attachments['coloring'])) {
                                $bunch = array_slice($attachments['coloring'], ($i*2), 2);
                                foreach ($bunch as $attempt) {
                                    if (in_array(utils::getFileExt($attempt['attachment_file']), ['jpg', 'bmp', 'png'])) {
                                        ?>
                                        <a href="<?=$attempt['attachment_file'];?>" class="fancybox">
                                            <img src="<?=$attempt['attachment_file'];?>" alt="" class="img-responsive" style="width: 32.5%; display: inline-block; border: 1px solid #ccc;" />
                                        </a>
                                        <?php
                                    }
                                }
                            }
                            ?>
							<?php } ?>
							<?php } else { ?>
							<p>No results</p>
							<?php } ?>
						</div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="box box-solid">
						<div class="box-header with-border">
							<h3 class="box-title">7. Questionnaire</h3>

							<?php if (!empty($survey_results['parent_assestment'])) { ?>
							<div class="box-tools pull-right">
								<a href="?controller=<?=$_GET['controller'];?>&amp;action=download_child_survey_assestment&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>&amp;assestment_type=parent_assestment" class="btn btn-box-tool" title="Download assessment">
									<i class="fa fa-download"></i>
								</a>
							</div>
							<?php } ?>
						</div>

						<div class="box-body">
							<?php if (!empty($survey_results['parent_assestment'])) { ?>
							<?php foreach ($survey_results['parent_assestment'] as $i=>$a) { ?>
							<?php if (count($survey_results['parent_assestment'])>1) { ?><p><strong>Attempt: #<?=$a['attempt'];?></strong></p><?php } ?>
							<p>Status: <?=($a['result_data']['interrupted']? 'Interrupted': 'Finished');?></p>
							<p>Parent: <?=utils::safeEcho($a['result_data']['parent'], 1);?></p>
							<!--<p>Duration: <?/*=($a['result_data']['endTime']-$a['result_data']['startTime']);*/?> ms  <?php /*//date('d.m.Y H:i:s', round($a['result_data']['startTime']/1000));*/?>  <?php /*//date('d.m.Y H:i:s', round($a['result_data']['endTime']/1000));*/?> </p>-->
							<?php if (!empty($a['result_data']['deviceId'])) { ?>
							<p>Device ID: <?=$a['result_data']['deviceId'];?></p>
							<?php } ?>

							<div style="display: none;" id="divParentAssestmentData_<?=$a['id'];?>" data-filename="<?=$a['result_file'];?>" data-time="<?=time();?>">
								<table class="table table-bordered table-striped">
									<?php if (isset($a['result_data']['questions'][0])) { ?>
									<thead>
										<tr>
											<?php
											foreach ($a['result_data']['questions'][0] as $k=>$v) {
												?><td><?=$k;?></td><?php
											}
											?>
										</tr>
									</thead>
									<tbody>
										<?php foreach ($a['result_data']['questions'] as $q) { ?>
										<tr>
											<?php foreach ($q as $key=>$value) { ?>
											<td><?=$value;?></td>
											<?php } ?>
										</tr>
										<?php } ?>
									</tbody>
									<?php } ?>
								</table>

								<table class="table table-bordered table-striped">
									<?php if (isset($a['result_data']['videoQuestions'][0])) { ?>
									<thead>
										<tr>
											<?php
											foreach ($a['result_data']['videoQuestions'][0] as $k=>$v) {
												?><td><?=$k;?></td><?php
											}
											?>
										</tr>
									</thead>
									<tbody>
										<?php foreach ($a['result_data']['videoQuestions'] as $q) { ?>
										<tr>
											<?php foreach ($q as $key=>$value) { ?>
											<td><?=$value;?></td>
											<?php } ?>
										</tr>
										<?php } ?>
									</tbody>
									<?php } ?>
								</table>
							</div>
							<p><a href="#divParentAssestmentData_<?=$a['id'];?>" class="fancybox">View results</a></p>

							<!-- <?php if (!empty($attachments['parent_assestment'][$i]['attachment_file'])) { ?>
								<?php if (in_array(utils::getFileExt($attachments['parent_assestment'][$i]['attachment_file']), ['jpg', 'bmp', 'png'])) { ?>
									<img src="<?=$attachments['parent_assestment'][$i]['attachment_file'];?>" alt="" class="img-responsive" style="background: #eee;" />
								<?php } else if (in_array(utils::getFileExt($attachments['parent_assestment'][$i]['attachment_file']), ['mp4', 'mov'])) { ?>
									<p>Assessment video &quot;<?=$attachments['parent_assestment'][$i]['attachment_file'];?>&quot;: <a href="<?=$attachments['parent_assestment'][$i]['attachment_file'];?>" target="_blank">View</a> / Download</p>
								<?php } ?>
							<?php } ?> -->
							<?php } ?>

							<?php } else { ?>
							<p>No assesstment results</p>
							<?php } ?>

							<?php if (!empty($attachments['parent_assestment'])) { ?>
								<?php foreach ($attachments['parent_assestment'] as $a) { ?>
									<?php if (in_array(utils::getFileExt($a['attachment_file']), ['jpg', 'bmp', 'png'])) { ?>
										<img src="<?=$a['attachment_file'];?>" alt="" class="img-responsive" style="background: #eee;" />
									<?php } else if (in_array(utils::getFileExt($a['attachment_file']), ['mp4', 'mov'])) { ?>
										<p>Assessment video: <a href="<?=$a['attachment_file'];?>" target="_blank"><?=pathinfo($a['attachment_file'], PATHINFO_BASENAME);?></a></p>
									<?php } ?>
								<?php } ?>
							<?php } ?>
						</div>
					</div>
				</div>

				<div class="col-md-6">
					<div class="box box-solid">
						<div class="box-header with-border">
							<h3 class="box-title">8. Parent-child play</h3>
						</div>

						<div class="box-body">
							<?php if (!empty($survey_results['parent_child_play'])) { ?>
							<?php foreach ($survey_results['parent_child_play'] as $i=>$a) { ?>
							<?php if (count($survey_results['parent_child_play'])>1) { ?><p><strong>Attempt: #<?=$a['attempt'];?></strong></p><?php } ?>
							<p>Assessment status: <?=($a['result_data']['interrupted']? 'Interrupted': 'Finished');?></p>
							<p>Duration: <?=($a['result_data']['endTime']-$a['result_data']['startTime']);?> ms  <?php //echo date('d.m.Y H:i:s', round($a['result_data']['startTime']/1000));?>  <?php //echo date('d.m.Y H:i:s', round($a['result_data']['endTime']/1000));?> </p>
							<?php if (!empty($a['result_data']['deviceId'])) { ?>
							<p>Device ID: <?=$a['result_data']['deviceId'];?></p>
							<?php } ?>

							<div style="display: none;" id="divParentChildPlayData_<?=$a['id'];?>" data-filename="<?=$a['result_file'];?>" data-time="<?=time();?>">
								<table class="table table-bordered table-striped">
									<?php if (isset($a['result_data']['items'][0])) { ?>
									<thead>
										<tr>
											<?php
											foreach ($a['result_data']['items'][0] as $k=>$v) {
												?><td><?=$k;?></td><?php
											}
											?>
										</tr>
									</thead>
									<tbody>
										<?php foreach ($a['result_data']['items'] as $item) { ?>
										<tr>
											<?php foreach ($q as $key=>$value) { ?>
											<td><?=$value;?></td>
											<?php } ?>
										</tr>
										<?php } ?>
									</tbody>
									<?php } ?>
								</table>
							</div>

							<!-- <p>Assessment results: <a href="#divParentChildPlayData_<?=$a['id'];?>" class="fancybox">View</a> / <a href="?controller=<?=$_GET['controller'];?>&amp;action=download_child_survey_assestment&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$survey['id'];?>&amp;assestment_type=parent_child_play">Download</a></p> -->

							<?php } ?>

							<?php } else { ?>
							<p>No results</p>
							<?php } ?>

							<?php if (!empty($attachments['parent_child_play'])) { ?>
								<?php foreach ($attachments['parent_child_play'] as $a) { ?>
									<?php if (in_array(utils::getFileExt($a['attachment_file']), ['jpg', 'bmp', 'png'])) { ?>
										<img src="<?=$a['attachment_file'];?>" alt="" class="img-responsive" style="background: #eee;" />
									<?php } else if (in_array(utils::getFileExt($a['attachment_file']), ['mp4', 'mov', 'mpg'])) { ?>
										<p>Assessment video: <a href="<?=$a['attachment_file'];?>" target="_blank"><?=pathinfo($a['attachment_file'], PATHINFO_BASENAME);?></a></p>
									<?php } ?>
								<?php } ?>
							<?php } ?>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- /.row -->

</section>



<!-- <script type="text/javascript">
    $('#aDeleteItem_<?=$child['id'];?>_<?=$child['survey_id']?>').on('click', function(e) {
        bootbox.confirm({
            message: '<?=CMS::t('child_delete_confirmation');?>',
            callback: function(ok) {
                if (ok) {
                    var $form = $('#formDeleteItem');
                    $('[name="delete"]', $form).val('<?=$child['id'];?>');
                    $form.submit();
                }
            }
        });
        return false;
    });
</script> -->
<!-- /.content -->

<!-- <pre>
<?php var_export($attachments); ?>
</pre> -->