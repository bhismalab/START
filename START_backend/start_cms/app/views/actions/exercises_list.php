<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

// load resourses

// load datepicker
view::appendCss(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/css/bootstrap-datepicker3.css');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/js/bootstrap-datepicker.min.js');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/locales/bootstrap-datepicker.'.$_SESSION[CMS::$sess_hash]['ses_adm_lang'].'.min.js');

?>


<style>
td {
	vertical-align: middle !important;
}
thead th {
	text-transform: uppercase;
}

.cke_dialog_ui_vbox > table > tbody > tr:first-child > td {
	display:none;
}
/*.addon-datepicker-input {
	position: relative; z-index: 100000 !important;
}*/
</style>


<script type="text/javascript">
// <![CDATA[
$(document).ready(function() {
	$('#filter-button').on('click', function() {
		$.fancybox.open({
			href: '#popupFilter'
		});
	});

	$('#popupFilterClose').on('click', function() {
		$.fancybox.close();
		return false;
	});

	$('.aAjax').on('click', function() {
		var $el = $(this);
		var data = JSON.parse($(this).attr('data-ajax_post'));
		$.ajax({
			url: this.href,
			data: data,
			async: true,
			cache: false,
			type: 'post',
			dataType: 'json',
			success: function(response, status, xhr) {
				if (response.success) {
					if (response.data && response.data.action) {
						var new_status = response.data.action;
						var old_status = ((new_status=='on')? 'off': 'on');
						$('i', $el).removeClass('fa-toggle-'+old_status+' btn-toggle-'+old_status).addClass('fa-toggle-'+new_status+' btn-toggle-'+new_status);
						data.turn = old_status;
						$el.attr('data-ajax_post', JSON.stringify(data));
					}
				}
			},
			error: function(xhr, err, descr) {}
		});

		return false;
	});
});
// ]]>
</script>


	<!-- Deleting hidden form -->
	<form action="?controller=children&amp;action=delete" method="post" id="formDeleteItem">
		<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />
		<input type="hidden" name="delete" value="0" />
	</form>


	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>
			<?=CMS::t('menu_block_exercises');?>
		</h1>
	</section>

	<!-- Main content -->
	<section class="content">
		<!-- Info boxes -->

		<div class="box">
            <div class="box-body">
				<?php
					if (!empty($exercises) && is_array($exercises)) {
				?>
				<table class="table table-bordered table-striped">
					<thead>
						<tr>
							<th>TYPE</th>
							<th>LAST UPDATED</th>
							<th><?=CMS::t('controls');?></th>
						</tr>
					</thead>
					<tbody>
						<?php foreach ($exercises as $e) { ?>
						<?php if (!in_array($e['type'], ['parent_assestment', 'eye_tracking_slide'])) { ?>
						<tr>
							<td>
								<?=CMS::t('exercises_type_popup_'.$e['type']);?>
							</td>
							<td>
								<time datetime="<?=utils::formatMySQLDate('c', $e['mod_datetime']);?>" title="<?=$e['mod_datetime'];?>"><?=utils::formatMySQLDate('d.m.Y', $e['mod_datetime']);?></time>
							</td>
							<td>
								<?php if (CMS::hasAccessTo('exercises/edit')) { ?>
								<a href="?controller=exercises&amp;action=edit&amp;id=<?=$e['id'];?>&amp;type=<?=$e['type'];?>&amp;return=<?=$link_return;?>&amp;<?=time();?>" title="<?=CMS::t('edit');?>">
									<i class="fa fa-eye" aria-hidden="true"></i> Edit exercise
								</a>
								<?php } ?>
							</td>
						</tr>
						<?php } ?>
						<?php } ?>
                        <tr>
							<td><a href="?controller=parent_assestment&amp;action=list">Questionnaire</a></td>
						</tr>
						<tr>
							<td><a href="?controller=states&amp;action=list">States</a></td>
						</tr>
						<tr>
							<td><a href="?controller=languages&amp;action=list">Languages</a></td>
						</tr>
					</tbody>
				</table>
				<?php
					} else {
						print view::callout('', 'danger', 'no_data_found');
					}
				?>
			</div>
            <!-- /.box-body -->
		</div>
		<!-- /.box -->

		<div class="box">
    	<div class="box-body">
		<form action="?controller=consent_form&action=edit" method="post">
			<div class="form-group">
				<label for="consent_text">Edit consent form in English</label>
				<textarea class="form-control textarea" class="consent_textbox" name="consent_text_english" id="consent_text_english"><?= htmlspecialchars($consent_form_english)?></textarea>
			</div>
			<div class="form-group">
				<label for="consent_text">Edit consent form in Hindi</label>
				<textarea class="form-control textarea" class="consent_textbox" name="consent_text_hindi" id="consent_text_hindi"><?= htmlspecialchars($consent_form_hindi)?></textarea>
			</div>
			<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />
			<button type="submit" class="btn btn-primary">Submit</button>
		</form>
		</div>
    	<!-- /.box-body -->
	</div>
	<!-- /.box -->

	<!-- /.info boxes -->
	</section>
	<!-- /.content -->


<div id="popupFilter" style="display: none; width: 420px;">
	<h4 class="popupTitle"><?=CMS::t('filter_popup_title');?></h4>

	<div class="popupForm">
		<div class="popupFormFieldset">
			<div class="popupFormInputsBlock">
				<label for="selectGender" class="form-label"><?=CMS::t('children_gender');?></label>

				<select name="filter[gender]" id="selectGender" class="form-control" form="formSearchAndFilter">
					<option value=""><?=CMS::t('filter_no_matter');?></option>
					<option value="M"<?=((@$_GET['filter']['gender']=='M')? ' selected="selected"': '');?>><?=CMS::t('children_gender_M');?></option>
					<option value="F"<?=((@$_GET['filter']['gender']==='F')? ' selected="selected"': '');?>><?=CMS::t('children_gender_F');?></option>
				</select>
			</div>

			<div class="popupFormInputsBlock">
				<label for="selectState" class="form-label"><?=CMS::t('children_state');?></label>

				<select name="filter[state]" id="selectState" class="form-control" form="formSearchAndFilter">
					<option value=""><?=CMS::t('filter_no_matter');?></option>
					<?php
						if (!empty($states) && is_array($states)) foreach ($states as $s) {
					?><option value="<?=$s;?>"<?=((@$_GET['filter']['state']==$s)? ' selected="selected"': '');?>><?=$s;?></option><?php
						}
					?>
				</select>
			</div>

			<div class="popupFormInputsBlock">
				<label for="selectSocialWorkers" class="form-label"><?=CMS::t('children_social_worker');?></label>

				<select name="filter[social_worker]" id="selectSocialWorkers" class="form-control" form="formSearchAndFilter">
					<option value=""><?=CMS::t('filter_no_matter');?></option>
					<?php
						if (!empty($social_workers) && is_array($social_workers)) foreach ($social_workers as $sw) {
					?><option value="<?=$sw['id'];?>"<?=((@$_GET['filter']['social_worker']==$sw)? ' selected="selected"': '');?>><?=$sw['full_name'];?></option><?php
						}
					?>
				</select>
			</div>

			<div class="popupFormInputsBlock" style="height: 60px;">
				<label class="form-label"><?=CMS::t('site_user_reg_date');?></label>

				<div class="clear"></div>

				<div class="input-group" style="width: 192px; float: left;">
					<div class="input-group-addon"><i class="fa fa-calendar"></i></div>
					<input type="text" name="filter[reg_since]" value="<?=utils::safeEcho(@$_GET['filter']['reg_since'], 1);?>" placeholder="<?=CMS::t('site_user_reg_date_since');?>" class="form-control pull-right addon-datepicker-input" style="width: 152px;" id="inputRegSince" form="formSearchAndFilter" />
                </div>

				<img src="<?=IMAGE_DIR;?>interval_grey.png" alt="&mdash;" style="width: 31px; float: left; margin: 12px 3px 0 2px;" />

				<div class="input-group" style="width: 192px; float: left;">
					<div class="input-group-addon"><i class="fa fa-calendar"></i></div>
					<input type="text" name="filter[reg_till]" value="<?=utils::safeEcho(@$_GET['filter']['reg_till'], 1);?>" placeholder="<?=CMS::t('site_user_reg_date_till');?>" class="form-control pull-right addon-datepicker-input" style="width: 152px;" id="inputRegTill" form="formSearchAndFilter" />
                </div>

				<div class="clear"></div>

				<script type="text/javascript">
// <![CDATA[
$('.addon-datepicker-input').each(function() {
    $(this).datepicker({
		autoclose: true,
		format: 'dd.mm.yyyy',
		clearBtn: true
	});
});
// ]]>
				</script>
			</div>
		</div>

		<div class="popupControls">
			<button type="submit" name="go" value="1" form="formSearchAndFilter" class="btn btn-primary center-block"><i class="fa fa-filter" aria-hidden="true"></i> <?=CMS::t('filter');?></button>
		</div>
	</div>
</div>

<script src="https://cdn.ckeditor.com/4.7.3/standard/ckeditor.js"></script>
<script>
	CKEDITOR.plugins.addExternal( 'base64image', '<?= SITE . CMS_DIR . WEB_DIR  ?>js/base64image/', 'plugin.js' );
	
	var editorConfig = {
		extraPlugins: 'base64image',
		toolbar: [
			[ 'Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo' ],
			{ name: 'basicstyles', items: [ 'Bold', 'Italic', '-', 'base64image' ] }
		]
	};

    CKEDITOR.replace( 'consent_text_english', editorConfig);
	CKEDITOR.replace( 'consent_text_hindi', editorConfig);
</script>
