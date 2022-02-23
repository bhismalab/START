<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

?>


<style>
.image-preview {
	position: relative; margin-bottom: 2px;
}
.image-preview-img {
	border-radius: 3px;
}
.image-preview-overlay {
	display: none; position: absolute; left: 0; top: 0; z-index: 100; background: #000; opacity: 0.5;
}
.image-preview-info {
	display: none; position: absolute; left: 0; top: 0; z-index: 101; padding: 20px 30px;
}
.image-preview-info p {
	margin: 0; padding: 0; color: #fff; line-height: 20px;
}
.image-preview-info a {
	font-size: 16px; color: #fff; margin-right: 30px;
}
.image-preview-info a:hover {
	text-decoration: underline;
}
</style>


<?php

// load resourses

// load Bootstrap Datepicker
view::appendCss(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/css/bootstrap-datepicker3.css');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/js/bootstrap-datepicker.min.js');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/locales/bootstrap-datepicker.'.$_SESSION[CMS::$sess_hash]['ses_adm_lang'].'.min.js');

// load CK Editor
view::appendJs(SITE.CMS_DIR.JS_DIR.'ckeditor-4.6.1/ckeditor.js');
view::appendJs(SITE.CMS_DIR.JS_DIR.'ckfinder/ckfinder.js');

// load Select2 plugin
view::appendJs(SITE.CMS_DIR.JS_DIR.'select2/js/select2.min.js');
view::appendJs(SITE.CMS_DIR.JS_DIR.'select2/js/i18n/'.$_SESSION[CMS::$sess_hash]['ses_adm_lang'].'.js');

?>

<script type="text/javascript">
// <![CDATA[
//CKFinder.setupCKEditor(null);

$(document).ready(function() {
	$('.image-preview').append('<div class="image-preview-overlay"></div>');
	$('.image-preview').hover(function() {
		var $overlay = $('.image-preview-overlay', this);
		var $info = $('.image-preview-info', this);
		var $img = $('img', this);
		$overlay.show();
		$info.show();
		$overlay.height($img.outerHeight());
		$overlay.width($img.outerWidth());
	}, function() {
		var $overlay = $('.image-preview-overlay', this);
		var $info = $('.image-preview-info', this);
		$overlay.hide();
		$info.hide();
	});

	utils.setConfirmation('click', '#aDelImg', '<?=CMS::t('delete_confirmation');?>', function($el) {
		$.ajax({
			url: $el.attr('href'),
			async: true,
			cache: false,
			dataType: 'json',
			method: 'post',
			data: {
				article_id: '<?=$article['id'];?>',
				CSRF_token: '<?=$CSRF_token;?>'
			},
			success: function(response, status, xhr) {
				if (response.success) {
					utils.alert('<?=utils::safeJsEcho(CMS::t('del_suc'), 1);?>', function() {
						location.href = location.href;
					});
				} else {
					utils.alert('<?=utils::safeJsEcho(CMS::t('del_err'), 1);?>');
				}
			},
			error: function(xhr, err, descr) {
				utils.alert(err+' '+descr);
			}
		});

		return false;
	});
});
// ]]>
</script>
<?php
/** This names is for input type files */
/*$files = array("bubble_img", "video_1", "video_2", "img_1", "img_2", "img_3", "pair_1", "pair_2", "pair_3", "pair_4", "pair_5", "moving_fragment", "slide", "video_left", "video_right");
echo "<pre>"; var_dump($exercise); die();*/

?>

<!-- Content Header (Page header) -->
<section class="content-header">
	<?php /* <h1>
		<?=CMS::t('menu_item_articles_edit');?>
		<small><?=utils::safeEcho(utils::limitStringLength((empty($exercise['translates'][CMS::$default_site_lang]['title'])? $exercise['sef']: $exercise['translates'][CMS::$default_site_lang]['title']), 48), 1);?></small>
	</h1> */?>


</section>

<!-- Content Header (Page header) -->
<section class="contextual-navigation">
	<nav>
		<a href="<?=utils::safeEcho($link_back, 1);?>" class="btn btn-default"><i class="fa fa-arrow-left" aria-hidden="true"></i> <?=CMS::t('back');?></a>

		<?php  if (CMS::hasAccessTo('exercise/list')) { ?>
		<a href="?controller=exercise&amp;action=list" class="btn btn-default"><i class="fa fa-commenting" aria-hidden="true"></i> <?=CMS::t('menu_item_exercise_list');?> (<?=$comments_num;?>)</a>
		<?php } ?>
	</nav>
</section>


<!-- Main content -->
<section class="content">
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

	<div class="box" style="border-top: none;">
		<!-- <div class="box-header with-border">
			<h3 class="box-title"><?=CMS::t('menu_item_articles_edit');?></h3>
		</div> -->
		<!-- /.box-header -->

		<form action="" method="post" enctype="multipart/form-data" class="form-std" role="form">
			<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />

			<div class="box-body" style="padding: 0;">
				<div class="nav-tabs-custom">
					<ul class="nav nav-tabs">
						<li class="active"><a href="#common-tab" data-toggle="tab"><?=CMS::t('common_data');?></a></li>
						<?php
							if (!empty($langs) && is_array($langs)) foreach ($langs as $lng) {
						?><li><a href="#lang-<?=$lng['language_dir'];?>-tab" data-toggle="tab"><?=$lng['language_name'];?></a></li><?php
							}
						?>
					</ul>

					<div class="tab-content">
						<!-- Common Informational TAB -->
						<div class="tab-pane active" id="common-tab">
							<div class="row">
								<div class="col-md-6">

									<div class="form-group">
										<label><?=CMS::t('image');?></label>

										<?php if (!empty($article['img'])) {
												$uploadUrl = SITE.utils::dirCanonicalPath(CMS_DIR.UPLOADS_DIR);
												$previewUrl = $uploadUrl.'articles/originals/'.$article['img'];
												$preview_exists = is_file(UPLOADS_DIR.'articles/originals/'.$article['img']);
											?>
										<div class="image-preview">
											<img src="<?=($preview_exists? $previewUrl: IMAGE_DIR.'noimg.jpg');?>" alt="<?=$article['img'];?>" class="img-responsive image-preview-img" />
											<div class="image-preview-info">
												<p>
												<?php
													$orgSize = utils::getFileSizeFormatted(UPLOADS_DIR.'articles/originals/'.$article['img']);
													$tmbSize = utils::getFileSizeFormatted(UPLOADS_DIR.'articles/thumbs/'.$article['img']);
													$imgModTimestamp = @filemtime(UPLOADS_DIR.'articles/originals/'.$article['img']);
												?>
												<?=CMS::t('article_image_original_size');?>: <?=$orgSize['value'];?> <?=$orgSize['measure'];?><br />
												<?=CMS::t('article_image_thumbnail_size');?>: <?=$tmbSize['value'];?> <?=$tmbSize['measure'];?><br />
												<?=CMS::t('article_image_original_upload_datetime');?>: <?=($imgModTimestamp? date('d.m.Y H:i:s', $imgModTimestamp): '-');?><br />
												<br />
												<br />
												<?php if ($preview_exists) { ?>
												<a href="<?=$previewUrl;?>" target="_blank"><img src="<?=IMAGE_DIR;?>eye_white.png" alt="" style="height: 16px;" /> <?=CMS::t('article_image_original_view');?> <img src="<?=IMAGE_DIR;?>outer_link_white.png" alt="" /></a>
												<?php } ?>
												<?php if (CMS::hasAccessTo('articles/ajax_delete_image', 'write')) { ?>
												<a href="?controller=articles&amp;action=ajax_delete_image" id="aDelImg"><img src="<?=IMAGE_DIR;?>drop_white.png" alt="" style="height: 16px;" /> <?=CMS::t('delete');?></a>
												</p>
												<?php } ?>
											</div>
										</div>
										<?php } ?>

										<?=view::browse([
											'name' => 'img',
											'accept' => 'image/*'
										]);?>

										<p class="form-info-tip"><?=CMS::t('article_image_descr', [
											'{types}' => implode(', ', $allowed_thumb_ext)
										]);?></p>
									</div>

									<div class="form-group">
										<label><?=CMS::t('article_source');?></label>

										<input type="text" name="source_url" value="<?=utils::safePostValue('source_url', $article['source_url'], 1);?>" placeholder="<?=CMS::t('article_source_url');?>" class="form-control" style="margin-bottom: 5px;" />
										<input type="text" name="source_name" value="<?=utils::safePostValue('source_name', $article['source_name'], 1);?>" placeholder="<?=CMS::t('article_source_name');?>" class="form-control" />
									</div>

									<div class="form-group">
										<label><?=CMS::t('article_publish_datetime');?></label>

										<div class="row">
											<div class="col-xs-6">
												<div class="input-group">
													<div class="input-group-addon"><i class="fa fa-calendar"></i></div>
													<input type="text" name="publish_date" value="<?=utils::safePostValue('publish_date', utils::formatMySQLDate('d.m.Y', $article['publish_datetime']), 1);?>" placeholder="<?=CMS::t('article_publish_date_placeholder');?>" class="form-control datepicker" />
												</div>

												<script type="text/javascript">
// <![CDATA[
$(document).ready(function() {
	$('[name="publish_date"]').datepicker({
		format: 'dd.mm.yyyy',
		clearBtn: true,
		language: '<?=utils::safeJsEcho($_SESSION[CMS::$sess_hash]['ses_adm_lang'], 1);?>'
	});
});
// ]]>
												</script>
											</div>

											<div class="col-xs-6">
												<select name="publish_hour" class="form-control" style="display: inline-block; width: 45%;">
													<option value=""><?=CMS::t('article_publish_hour_placeholder');?></option>
													<?php
														$publish_hour_selected = (empty($_POST['publish_hour'])? utils::formatMySQLDate('G', $article['publish_datetime']): $_POST['publish_hour']);
														for ($i=0; $i<24; $i++) {
															?><option value="<?=$i;?>"<?=(($i==$publish_hour_selected)? ' selected="selected"': '');?>><?=str_pad($i, 2, '0', STR_PAD_LEFT);?></option><?php
														}
													?>
												</select>
												:
												<select name="publish_minutes" class="form-control" style="display: inline-block; width: 45%;">
													<option value=""><?=CMS::t('article_publish_minutes_placeholder');?></option>
													<?php
														$publish_minutes_selected = (empty($_POST['publish_minutes'])? intval(utils::formatMySQLDate('i', $article['publish_datetime'])): $_POST['publish_minutes']);
														if ($publish_minutes_selected>=60) {
															$publish_minutes_selected = 0;
														}
														for ($i=0; $i<60; $i=($i+5)) {
															?><option value="<?=$i;?>"<?=(($i==$publish_minutes_selected)? ' selected="selected"': '');?>><?=str_pad($i, 2, '0', STR_PAD_LEFT);?></option><?php
														}
													?>
												</select>
											</div>
										</div>
									</div>

									<div class="form-group">
										<?php
											$is_published = (isset($_POST['CSRF_token'])? @$_POST['is_published']: $article['is_published']);
										?>
										<input type="checkbox" name="is_published" value="1"<?=($is_published? ' checked="checked"': '');?> id="triggerArticleStatus" /><label for="triggerArticleStatus" style="display: inline; font-weight: normal;"> <?=CMS::t('publish');?></label>
									</div>
								</div>

								<div class="col-md-6">
									<?php if (!empty($cats) && count($cats)) { ?>
									<div class="form-group">
										<label><?=CMS::t('article_category');?></label>

										<div class="form-div-multicheckbox" style="width: 100%; height: auto; max-height: 214px;">
											<?php
												foreach ($cats as $c) {
													if (!empty($allowed_cats) && !in_array($c['id'], $allowed_cats)) {continue;}
													if (empty($_POST['CSRF_token'])) {
														$is_pos_selected = @in_array($c['id'], $art_cats);
													} else {
														$is_pos_selected = @in_array($c['id'], @$_POST['cats']);
													}
											?><input type="checkbox" name="cats[]" value="<?=$c['id'];?>"<?=($is_pos_selected? ' checked="checked"': '');?> id="multiCheckboxCat_<?=$c['id'];?>" /><label for="multiCheckboxCat_<?=$c['id'];?>"> <?=$c['name'];?><?php if (!empty($c['parent']['id'])) { ?> <span style="color: #aaa;">&crarr; <?=$c['parent']['name'];?></span><?php } ?></label><br /><?php
												}
											?>
										</div>
									</div>
									<?php } ?>

									<div class="form-group" style="margin-bottom: 5px;">
										<?php
											$show_on_main_page = (isset($_POST['CSRF_token'])? @$_POST['show_on_main_page']: $article['show_on_main_page']);
										?>
										<input type="checkbox" name="show_on_main_page" value="1"<?=($show_on_main_page? ' checked="checked"': '');?> id="triggerArticleShowOnMainPage" /><label for="triggerArticleShowOnMainPage" style="display: inline; font-weight: normal;"> <?=CMS::t('article_show_on_main_page');?></label>
									</div>

									<div class="form-group">
										<?php
											$is_highlighted = (isset($_POST['CSRF_token'])? @$_POST['is_highlighted']: $article['is_highlighted']);
										?>
										<input type="checkbox" name="is_highlighted" value="1"<?=($is_highlighted? ' checked="checked"': '');?> id="triggerArticleIsHighLighted" /><label for="triggerArticleIsHighLighted" style="display: inline; font-weight: normal;"> <?=CMS::t('article_is_highlighted');?></label>
									</div>

									<div class="form-group">
										<label><?=CMS::t('article_gallery');?></label>

										<select name="gallery_id" class="form-control select2" id="selectGalleryPicker">
											<?php if (!empty($gallery['id'])) { ?>
											<option value="<?=$gallery['id'];?>" selected="selected"><?=utils::safeEcho(@$gallery['translates'][CMS::$default_site_lang]['name'], 1);?></option>
											<?php } ?>
										</select>

										<script type="text/javascript">
// <![CDATA[
$('#selectGalleryPicker').select2({
	language: '<?=$_SESSION[CMS::$sess_hash]['ses_adm_lang'];?>',
	ajax: {
		url: '?controller=gallery&action=ajax_get_autocomplete',
		dataType: 'json',
		delay: 250,
		data: function(params) {
			return {
				q: params.term, // search term
				page: params.page
			};
		},
		processResults: function(response, params) {
			// parse the results into the format expected by Select2
			// since we are using custom formatting functions we do not need to
			// alter the remote JSON data, except to indicate that infinite
			// scrolling can be used
			params.page = params.page || 1;

			return {
				results: response.data.galleries,
				pagination: {
					more: (params.page * 30) < response.data.total_count
				}
			};
		},
		cache: true
	},
	escapeMarkup: function(markup) {
		return markup;
	}, // let our custom formatter work
	minimumInputLength: 1,
	templateResult: function(gallery) {
		if (gallery.loading) {return gallery.text;}

		var html = '<div class="select2-result-gallery clearfix">'+
			//'<div class="select2-result-gallery__avatar"><img src="'+gallery.preview_url+'" alt="" /></div>'+
			'<div class="select2-result-gallery__meta">'+
				'<div class="select2-result-gallery__title">'+gallery.label+'</div>'+
				//'<div class="select2-result-gallery__statistics">'+
					//'<div class="select2-result-gallery__forks"><i class="fa fa-photo"></i> '+gallery.photos_count+' photos</div>'+
				//'</div>'+
			'</div>'+
		'</div>';

		return html;
	},
	templateSelection: function(gallery) {
		return (gallery.label || gallery.text);
	}
});
// ]]>
										</script>
									</div>
								</div>
							</div>
						</div>


						<?php
							if (!empty($langs) && is_array($langs)) foreach ($langs as $lng) {
						?>
						<div id="lang-<?=$lng['language_dir'];?>-tab" class="tab-pane">
							<!-- <?=$lng['language_name'];?> tab -->

							<div class="row">
								<div class="col-md-12">
									<div class="row">
										<div class="col-md-6">
											<div class="form-group">
												<label><?=CMS::t('article_title');?></label>

												<input type="text" name="title[<?=$lng['language_dir'];?>]" value="<?=utils::safeEcho((isset($_POST['title'][$lng['language_dir']])? $_POST['title'][$lng['language_dir']]: @$article['translates'][$lng['language_dir']]['title']), 1);?>" class="form-control" />
											</div>

											<div class="form-group">
												<label><?=CMS::t('article_keywords');?></label>

												<textarea name="keywords[<?=$lng['language_dir'];?>]" rows="4" cols="32" class="form-control" style="height: 80px;"><?=utils::safeEcho((isset($_POST['keywords'][$lng['language_dir']])? $_POST['keywords'][$lng['language_dir']]: @$article['translates'][$lng['language_dir']]['keywords']), 1);?></textarea>
											</div>
										</div>

										<div class="col-md-6">
											<div class="form-group">
												<label><?=CMS::t('article_short');?></label>

												<textarea name="short[<?=$lng['language_dir'];?>]" rows="4" cols="32" class="form-control" style="height: 154px;"><?=utils::safeEcho((isset($_POST['short'][$lng['language_dir']])? $_POST['short'][$lng['language_dir']]: @$article['translates'][$lng['language_dir']]['short']), 1);?></textarea>
											</div>
										</div>
									</div>

									<div class="form-group">
										<label><?=CMS::t('article_full');?></label>

										<textarea name="full[<?=$lng['language_dir'];?>]" rows="4" cols="32" class="form-input-std" id="wysiwyg_full_<?=$lng['language_dir'];?>"><?=utils::safeEcho((isset($_POST['full'][$lng['language_dir']])? $_POST['full'][$lng['language_dir']]: @$article['translates'][$lng['language_dir']]['full']), 1);?></textarea>
										<script type="text/javascript">
// <![CDATA[
CKEDITOR.replace('wysiwyg_full_<?=$lng['language_dir'];?>', {
	language: '<?=$_SESSION[CMS::$sess_hash]['ses_adm_lang'];?>',
	baseHref: '<?=SITE;?>',
	contentsCss: '<?=SITE;?>web/assets/base/css/content.css',
	uploadUrl: '<?=UPLOADS_DIR;?>',
	contentsLanguage: '<?=$lng['language_dir'];?>'
});
// ]]>
										</script>
									</div>

									<div class="form-group">
										<input type="checkbox" name="is_published_lang[<?=$lng['language_dir'];?>]" value="1"<?=((isset($_POST['is_published_lang'][$lng['language_dir']])? $_POST['is_published_lang'][$lng['language_dir']]: @$article['translates'][$lng['language_dir']]['is_published_lang'])? ' checked="checked"': '');?> id="triggerLangStatus_<?=$lng['language_dir'];?>" /><label for="triggerLangStatus_<?=$lng['language_dir'];?>" style="display: inline; font-weight: normal;"> <?=CMS::t('publish_lang');?></label>
									</div>
								</div>
							</div>
						</div>
						<?php
							}
						?>
					</div>
				</div>
			</div>
			<!-- /.box-body -->

			<?php if ($canWrite) { ?>
			<div class="box-footer">
				<button type="submit" name="save" value="1" class="btn btn-primary"><i class="fa fa-save" aria-hidden="true"></i> <?=CMS::t('save');?></button>
				<button type="submit" name="apply" value="1" class="btn btn-success"><i class="fa fa-check" aria-hidden="true"></i> <?=CMS::t('apply');?></button>
				<button type="reset" name="reset" value="1" class="btn btn-default"><i class="fa fa-refresh" aria-hidden="true"></i> <?=CMS::t('reset');?></button>
			</div>
			<?php } ?>
		</form>
	</div>
	<!-- /.box -->

	<!-- /.info boxes -->
</section>
<!-- /.content -->