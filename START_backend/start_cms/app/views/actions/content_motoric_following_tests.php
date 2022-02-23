<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined('_VALID_PHP')) {die('Direct access to this location is not allowed.');}

?>


<?php

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
				<div class="row">
					<div class="col-md-6 col-md-offset-3">
                        <div class="form-group">
                            <label><?=CMS::t('image');?></label>

                            <?php if (!empty($exercise['moving_fragment'])) {
                                $uploadUrl = SITE.utils::dirCanonicalPath(CMS_DIR.UPLOADS_DIR);
                                $previewUrl = $uploadUrl.'tests_content/'.$exercise['moving_fragment'];
                                $preview_exists = is_file(UPLOADS_DIR.'tests_content/'.$exercise['moving_fragment']);
                                //var_dump($preview_exists); die("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAa");
                                ?>
                                <div class="image-preview">
                                    <img src="<?=($preview_exists? $previewUrl: IMAGE_DIR.'noimg.jpg');?>" alt="<?=$exercise['moving_fragment'];?>" class="img-responsive image-preview-img" />
                                    <div class="image-preview-info">
                                        <p>
                                            <?php
                                            $orgSize = utils::getFileSizeFormatted(UPLOADS_DIR.'tests_content/'.$exercise['moving_fragment']);

                                            $tmbSize = utils::getFileSizeFormatted(UPLOADS_DIR.'tests_content/'.$exercise['moving_fragment']);
                                            $imgModTimestamp = @filemtime(UPLOADS_DIR.'tests_content/'.$exercise['moving_fragment']);
                                            ?>
                                            <?=CMS::t('article_image_original_size');?>: <?=$orgSize['value'];?> <?=$orgSize['measure'];?><br />
                                            <?=CMS::t('article_image_thumbnail_size');?>: <?=$tmbSize['value'];?> <?=$tmbSize['measure'];?><br />
                                            <?=CMS::t('article_image_original_upload_datetime');?>: <?=($imgModTimestamp? date('d.m.Y H:i:s', $imgModTimestamp): '-');?><br />
                                            <br />
                                            <br />


                                    </div>
                                </div>
                            <?php } ?>

                            <?=view::browse([
                                'name' => 'moving_fragment',
                                'accept' => 'image/*'
                            ]);?>
                            <p class="form-info-tip"><?=CMS::t('article_image_descr', [
                                    '{types}' => implode(', ', $allowed_thumb_ext)
                                ]);?></p>
                        </div>

                        <div class="form-group">
                            <label><?=CMS::t('name');?></label>

                            <input type="text" name="name" value="<?=utils::safePostValue('name', $exercise['name'], 1);?>" placeholder="<?=CMS::t('article_source_url');?>" class="form-control" style="margin-bottom: 5px;" />

                        </div>





						<input type="hidden" name="lang" value="<?=$user['lang'];?>" />
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