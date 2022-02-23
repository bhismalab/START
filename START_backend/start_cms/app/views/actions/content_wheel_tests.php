<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined('_VALID_PHP')) {die('Direct access to this location is not allowed.');}

$uploadUrl = SITE.utils::dirCanonicalPath(CMS_DIR.UPLOADS_DIR);

// load resourses

// load Bootstrap Datepicker
view::appendCss(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/css/bootstrap-datepicker3.css');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/js/bootstrap-datepicker.min.js');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/locales/bootstrap-datepicker.'.$_SESSION[CMS::$sess_hash]['ses_adm_lang'].'.min.js');

?>


<section class="content">
	<div class="row">
		<div class="col-md-12">
			<nav class="top-toolbar-stripe">
				<a href="<?=utils::safeEcho($link_back, 1);?>" title="<?=CMS::t('back');?>" class="toolbar-btn-primary"><i class="fa fa-arrow-circle-o-left" aria-hidden="true"></i> Back</a>

                <?=view::widget('breadcrumb', ['links' => [
                    /*[
                        'title' => 'Users dashboard',
                        'href' => '?controller=statistics&action=dashboard',
                        'icon' => 'users'
                    ],*/ [
                        'title' => 'Exercises',
                        'href' => $link_back,
                        'icon' => 'picture-o'
                    ], [
                        'title' => 'Edit Exercise',
                        'icon' => 'edit'
                    ]
                ]]);?>
			</nav>
		</div>
	</div>

	<?php
		if (!empty($op)) {
			if ($op['success']) {
				print view::notice($op['message'], 'success');
			} else {
				print view::notice(empty($op['errors'])? $op['message']: $op['errors']);
			}
		}
	?>

	<!-- Info boxes -->

	<div class="box">
		<form action="" method="post" enctype="multipart/form-data" class="form-std" autocomplete="off" role="form" id="formUserInfo">
			<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />
			<input type="hidden" name="MAX_FILE_SIZE" value="20971520" />

			<div class="box-body">
				<div class="row">
					<div class="col-md-12">
						<!-- <h3 class="box-title" style="margin-top: 0;">Video</h3> -->

						<div class="row">
							<div class="col-md-6">
								<div class="form-group">
									<label>Video</label>

									<?php
									if (!empty($exercise['video'])) {
										$previewUrl = $uploadUrl.'tests_content/'.$exercise['video'];
										$preview_exists = is_file(UPLOADS_DIR.'tests_content/'.$exercise['video']);
									?>
									<div class="image-preview">
										<?php if ($preview_exists) { ?>
										<video src="<?=$previewUrl;?>" style="width: 100%;" controls></video>
										<?php } ?>

										<div class="image-preview-info">
											<p>
												<?php
												$imgModTimestamp = @filemtime(UPLOADS_DIR.'tests_content/'.$exercise['video']);
												?>

												<strong>Filename</strong>: <?=utils::safeEcho($exercise['video'], 1);?><br />
												<strong>Uploaded</strong>: <?=($imgModTimestamp? date('d.m.Y', $imgModTimestamp): '-');?><br />
												<strong>Filesize</strong>: <?=(file_exists($filepath=$_SERVER['DOCUMENT_ROOT'] . "/start2/Start/uploads/tests_content/" . $exercise['video'])?filesize($filepath)/1000000 . " MB": 'File must not exceed 20MB');?>
											</p>
										</div>
									</div>
									<?php } ?>

									<?=view::browse([
										'name' => 'video',
										'accept' => 'video/*'
									]);?>

									<p class="form-info-tip"><?=CMS::t('article_image_descr', [
										'{types}' => implode(', ', $allowed_video_ext)
									]);?></p>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- /.box-body -->

			<div class="box-footer">
				<button type="submit" name="save" value="1" class="btn btn-primary"><i class="fa fa-save" aria-hidden="true"></i> <?=CMS::t('save');?></button>
				<button type="submit" name="apply" value="1" class="btn btn-primary"><i class="fa fa-check" aria-hidden="true"></i> <?=CMS::t('apply');?></button>
				<button type="reset" name="reset" value="1" class="btn btn-default"><i class="fa fa-refresh" aria-hidden="true"></i> <?=CMS::t('reset');?></button>
			</div>
		</form>
	</div>
	<!-- /.box -->

	<!-- /.info boxes -->
</section>
<!-- /.content -->