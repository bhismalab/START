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
						'title' => 'Questionnaire',
						'href' => $link_back,
						'icon' => 'user'
					], [
						'title' => 'Add question',
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
                            <div class="radio">
                                <label><input type="radio" class="common switch-types" name="optradio" checked value="common">Common</label>
                            </div>
                            <div class="radio">
                                <label><input type="radio" class="video switch-types" name="optradio" value="video">Video</label>
                            </div>
                        </div>
                        <div class="video-block hide">
                            <div class="form-group">
                                <label>Left video</label>

                                <?=view::browse([
                                    'name' => 'video_left',
                                    'accept' => 'video/*'
                                ]);?>
                                <p class="form-info-tip"><?=CMS::t('article_image_descr', [
                                    '{types}' => implode(', ', $allowed_video_ext)
                                ]);?></p>
                            </div>

                            <div class="form-group">
                                <label>Right video</label>

                                <?=view::browse([
                                    'name' => 'video_right',
                                    'accept' => 'video/*'
                                ]);?>
                                <p class="form-info-tip"><?=CMS::t('article_image_descr', [
                                    '{types}' => implode(', ', $allowed_video_ext)
                                ]);?></p>
                            </div>
                        </div>

                        <div class="form-group">
                            <label><?=CMS::t('question_title');?></label>

                            <input type="text" name="title" value="<?=utils::safeEcho(@$_POST['title'], 1);?>"  class="form-control" />
                        </div>

                        <div class="form-group">
                            <label><?=CMS::t('question_text');?></label>

                            <textarea rows="5" placeholder="<?=CMS::t('question_text');?>" name="question_text" id="" style="width: 100%;"><?=utils::safeEcho(@$_POST['question_text'], 1);?></textarea>
                        </div>

                        <div class="form-group">
                            <label><?=CMS::t('question_text');?> (Hindi)</label>

                            <textarea rows="5" placeholder="<?=CMS::t('question_text');?> (Hindi)" name="question_text_hindi" id="" style="width: 100%;"><?=utils::safeEcho(@$_POST['question_text_hindi'], 1);?></textarea>
                        </div>

                        <div class="form-group choices-block">
                            <label>Choices</label>

                            <div class="row">
								<?php
								$i = 0;
		                        foreach ($choicesBlocks as $id => $choices) {
								?>
								<div class="col-md-1">
									<input type="radio" name="choices" value="<?php echo htmlspecialchars($id); ?>">
	                            </div>
								<div class="col-md-5">
								<ul>
								<?php
									foreach ($choices['english'] as $choice) {
								?>
										<li><?php echo htmlspecialchars($choice); ?></li>
								<?php
									}

									echo '</ul></div>';

									if (++$i % 4 == 0) {
										echo '</div><div class="row">';
									}
								}
								?>
							</div>
					    </div>
                    </div>
				</div>
			</div>
			<!-- /.box-body -->

			<div class="box-footer">
				<button type="submit" name="add" value="1" class="btn btn-primary"><i class="fa fa-plus" aria-hidden="true"></i> <?=CMS::t('add');?></button>
				<button type="reset" name="reset" value="1" class="btn btn-default"><i class="fa fa-refresh" aria-hidden="true"></i> <?=CMS::t('reset');?></button>
			</div>
		</form>
	</div>
	<!-- /.box -->

	<!-- /.info boxes -->
</section>
<!-- /.content -->
<style>
    .hide{
        display: none;
    }
</style>
<script>
    $(".switch-types").on("click", function () {
        if($(this).hasClass("common")){
            $(".video-block").addClass("hide");
            $(".choices-block").removeClass("hide");
        }
        if($(this).hasClass("video")){
            $(".video-block").removeClass("hide");
            $(".choices-block").addClass("hide");
        }
    })
</script>