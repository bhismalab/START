<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

?>

<style>
td {
	vertical-align: middle !important;
}
thead th {
	text-transform: uppercase;
}
</style>

	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>
			<?=CMS::t('menu_item_languages_list');?>
		</h1>
	</section>
	<section class="content">
		<!-- Deleting hidden form -->
		<form action="?controller=languages&amp;action=delete" method="post" id="formDeleteItem">
			<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />
			<input type="hidden" name="delete" value="0" />
		</form>

		<div class="box">
			<div class="box-header with-border">
				<h3 class="box-title">Languages</h3>

				<?php if ($canWrite) { ?>
				<a href="?controller=languages&amp;action=add&amp;return=<?=$link_return;?>&amp;<?=time();?>" class="btn btn-context"><i class="fa fa-plus-circle" aria-hidden="true"></i> Create new language</a>
				<?php } ?>
			</div>
			<!-- /.box-header -->

            <div class="box-body">
				<?php
					if (!empty($languages) && is_array($languages)) {
				?>
				<table class="table table-bordered table-striped">
					<thead>
						<tr>
							<th>Language (english)</th>
							<th>Language (hindi)</th>
							<th>Actions</th>
						</tr>
					</thead>
					<tbody>
						<?php
								foreach ($languages as $language) {
						?>
							<tr>
								<td>
									<?= htmlspecialchars($language['name']); ?>
								</td>
								<td>
									<?= htmlspecialchars($language['name_hindi']); ?>
								</td>
								<td>
								<a href="?controller=languages&amp;action=edit&amp;id=<?= (int) $language['id'] ?>" title="Edit"><i class="fa fa-pencil-square-o" aria-hidden="true"></i> Edit</a>
								<a href="#" title="Delete" class="text-red" style="margin-left: 15px;" id="aDeleteItem_<?=$language['id'];?>" data-item-id="<?=$language['id'];?>"><i class="fa fa-trash" aria-hidden="true"></i> Delete</a>
								<script type="text/javascript">
									$('#aDeleteItem_<?=$language['id'];?>').on('click', function() {
										bootbox.confirm({
											message: '<?=CMS::t('cms_languages_delete_confirmation');?>',
											callback: function(ok) {
												if (ok) {
													var $form = $('#formDeleteItem');
													$('[name="delete"]', $form).val('<?=$language['id'];?>');
													$form.submit();
												}
											}
										});
										return false;
									});
								</script>
								</td>
							</tr>
						<?php
								}
						?>
					</tbody>
				</table>
				<?php
					}
				?>
			</div>
            <!-- /.box-body -->
		</div>
		<!-- /.box -->

		<!-- /.info boxes -->
	</section>
	<!-- /.content -->
