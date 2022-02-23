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


<!-- Content Header (Page header) -->
<section class="content-header">
	<h1>
		<?=CMS::t(($_SESSION[CMS::$sess_hash]['ses_adm_id']==$user['id'])? 'edit_cms_user_self': 'menu_item_cms_users_edit');?>
		<small><?=utils::safeEcho(@$user['name'], 1);?></small>
	</h1>

	<div class="clearfix"></div>
</section>


<!-- Main content -->
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

			<div class="box-body">
				<?php
					$pair = 1;
					$pairs_num = 8;
					while ($pair<=$pairs_num) {
						if ($pair%2) {
							?><div class="row"><?php
						}
						$img = 1;
						?>
						<div class="col-md-6">
							<div class="form-group">
								<label>Pair #<?=$pair;?>:</label>

								<?php
								$f = $exercise['pair_'.$pair.'_img_'.$img];

								if (!empty($f)) {
									$previewUrl = $uploadUrl.'tests_content/'.$f;
									$preview_exists = is_file(UPLOADS_DIR.'tests_content/'.$f);
									if (in_array(utils::getFileExt($f), $allowed_thumb_ext)) {
										?>
										<div class="image-preview">
											<img src="<?=($preview_exists? $previewUrl: IMAGE_DIR.'noimg.jpg');?>" class="img-responsive image-preview-img" alt="<?=$f;?>" style="height: 300px;" />
										</div>
										<?php
									} else {
										?>
										<div class="video-preview">
											<video class="media-player" autoplay="autoplay" loop="loop" src="<?=$previewUrl;?>" volume="0.0" controls="true" style="max-height: 300px;"></video>
										</div>
										<?php
									}
									?><p>
										<strong>Filename</strong>: <?=utils::safeEcho($f, 1);?><br />
									</p><?php
								}
								?>

								<?=view::browse([
									'name' => 'pair_'.$pair.'_img_'.$img
								]);?>

								<p class="form-info-tip"><?=CMS::t('article_image_descr', [
									'{types}' => implode(', ', array_merge($allowed_thumb_ext, $allowed_video_ext))
								]);?></p>
							</div>
						</div>
					<?php
						$pair++;

						if ($pair%2) {
							?></div><hr /><?php
						}
					}
				?>
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