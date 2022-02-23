<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

view::$title = 'Questionnaire';

?>


<style>
td {
	vertical-align: middle !important;
}
thead th {
	text-transform: uppercase;
}
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
	<form action="?controller=parent_assestment&amp;action=delete" method="post" id="formDeleteItem">
		<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />
		<input type="hidden" name="delete" value="0" />
	</form>

<?php
$uploadUrl = SITE.utils::dirCanonicalPath(CMS_DIR.UPLOADS_DIR);
?>

	<!-- Main content -->
	<section class="content">
		<!-- <div class="row">
			<div class="col-md-12">
				<nav class="top-toolbar-stripe">
					<a href="?controller=statistics&amp;action=dashboard" title="<?=CMS::t('back');?>" class="toolbar-btn-primary"><i class="fa fa-arrow-circle-o-left" aria-hidden="true"></i> Back</a>

					<?=view::widget('breadcrumb', ['links' => [
						/*[
							'title' => 'Users dashboard',
							'href' => '?controller=statistics&action=dashboard',
							'icon' => 'users'
						],*/ [
							'title' => 'CMS users',
							'icon' => 'user'
						]
					]]);?>
				</nav>
			</div>
		</div> -->

		<!-- Info boxes -->

		<!-- <pre><?php /*var_export($users);*/ ?></pre> -->

		<div class="box">
			<div class="box-header with-border">
				<h3 class="box-title"><?=$count;?> questions found</h3>

				<?php if (CMS::hasAccessTo('parent_assestment/add', 'write')) { ?>
				<a href="?controller=parent_assestment&amp;action=add&amp;return=<?=$link_return;?>&amp;<?=time();?>" class="btn btn-context"><i class="fa fa-plus-circle" aria-hidden="true"></i> Create new question</a>
				<?php } ?>



				<div class="box-tools pull-right col-sm-5 col-lg-6">
					<form action="" method="get" id="formSearchAndFilter">
						<input type="hidden" name="controller" value="<?=utils::safeEcho(@$_GET['controller'], 1);?>" />
						<input type="hidden" name="action" value="<?=utils::safeEcho(@$_GET['action'], 1);?>" />
						<input type="hidden" name="<?=time();?>" value="" />

						<div class="input-group has-feedback" style="top: 5px;">
							<div class="input-group-btn">
								<button type="button" class="btn btn-<?=(utils::isEmptyArrayRecursive(@$_GET['filter'])? 'success': 'warning');?>" id="filter-button"><i class="fa fa-filter" aria-hidden="true"></i> <?=CMS::t('filter');?></button>
							</div>
							<input type="text" name="q" value="<?=utils::safeEcho(@$_GET['q'], 1);?>" placeholder="<?=CMS::t('cms_users_search');?>" class="form-control input-md" onfocus="this.value='';" />
							<span class="input-group-btn">
								<button type="submit" name="go" value="1" class="btn btn-primary"><i class="fa fa-search" aria-hidden="true"></i> <?=CMS::t('search');?></button>
							</span>
						</div>
					</form>
				</div>
			</div>
			<!-- /.box-header -->

            <div class="box-body">
				<?php
					if (!empty($ques) && is_array($ques)) {
				?>
				<table class="table table-bordered table-striped">
					<thead>
						<tr>
							<th><?=CMS::t('id');?></th>
							<th><?=CMS::t('title');?></th>
							<th><?=CMS::t('type');?></th>
							<th><?=CMS::t('question_text');?></th>
							<th><?=CMS::t('video_left');?></th>
							<th><?=CMS::t('video_right');?></th>
							<th><?=CMS::t('mod_datetime');?></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
                    <?php //echo "<pre>", print_r($ques); die(); ?>
						<?php
							foreach ($ques as $que) {
						?>
							<tr>
                                <td><?=$que['id'];?></td>
								<td>
									<strong><?php utils::safeEcho($que['title']); ?></strong>
								</td>
								<td>
									<?=utils::safeEcho($que['type'], 1);?>
								</td>
                                <td>
									<p><?=utils::safeEcho($que['question_text'], 1);?></p>
									<hr />
									<p><?=utils::safeEcho($que['question_text_hindi'], 1);?></p>
								</td>
                                <td>
									<?php
									if (!is_null($que['video_left'])) {
                                        $previewUrl = $uploadUrl.'tests_content/'.utils::safeEcho($que['video_left'], 1);?>
                                        <video controls style="width: 200px;" src="<?=$previewUrl;?>"></video>
                                    <?php } ?>

								</td>
                                <td>

                                    <?php

                                    if (!is_null($que['video_right'])) {
                                        $previewUrl = $uploadUrl.'tests_content/'.utils::safeEcho($que['video_right'], 1);?>
                                        <video controls style="width: 200px;" src="<?=$previewUrl;?>"></video>
                                    <?php } ?>
                                </td>

								<td>
									<?=utils::formatMySQLDate('d.m.Y H:i:s', $que['add_datetime']);?>
								</td>

								<td style="white-space: nowrap;">
									<?php if (CMS::hasAccessTo('parent_assestment/delete', 'write')) { ?>
									<a href="#" title="<?=CMS::t('delete');?>" class="text-red" id="aDeleteItem_<?=$que['id'];?>" data-item-id="<?=$que['id'];?>">
										<i class="fa fa-trash" aria-hidden="true"></i> <?=CMS::t('delete');?>
									</a>
									<script type="text/javascript">
										$('#aDeleteItem_<?=$que['id'];?>').on('click', function() {
											bootbox.confirm({
												message: 'Are you sure you want to delete this question?',
												callback: function(ok) {
													if (ok) {
														var $form = $('#formDeleteItem');
														$('[name="delete"]', $form).val('<?=$que['id'];?>');
														$form.submit();
													}
												}
											});
											return false;
										});
									</script>
									<?php } ?>
									
									<?php if (CMS::hasAccessTo('parent_assestment/edit', 'write')) { ?>
									<a href="?controller=parent_assestment&action=edit&id=<?=urlencode($que['id']);?>" title="<?=CMS::t('edit');?>" style="margin-left: 15px;">
										<i class="fa fa-pencil-square-o" aria-hidden="true"></i> <?=CMS::t('edit');?>
									</a>
									<?php } ?>
								</td>
							</tr>
		<?php
				}
		?>
					</tbody>
				</table>

				<div class="pagination"><?=view::pg([
					'total' => $total,
					'current' => $current,
					'page_url' => $link_sc.'&amp;page=%d'
				]);?></div>
				<?php
					} else {
						print view::callout('', 'danger', 'no_data_found');
					}
				?>
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
				<label for="selectType" class="form-label">Type</label>
				<select name="filter[type]" id="selectType" class="form-control" form="formSearchAndFilter">
					<option value=""><?=CMS::t('filter_no_matter');?></option>
					<option value="common"<?=((@$_GET['filter']['type']=='common')? ' selected="selected"': '');?>>Common</option>
					<option value="video"<?=((@$_GET['filter']['type']=='video')? ' selected="selected"': '');?>>Video</option>
				</select>
			</div>
		</div>

		<div class="popupControls">
			<button type="submit" name="go" value="1" form="formSearchAndFilter" class="btn btn-primary center-block"><i class="fa fa-filter" aria-hidden="true"></i> <?=CMS::t('filter');?></button>
		</div>
	</div>
</div>