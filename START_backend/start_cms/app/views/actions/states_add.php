<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

?>

<!-- Content Header (Page header) -->
<section class="content-header">
	<h1>
		<?=CMS::t('menu_item_states_add');?>
	</h1>
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
						'title' => 'States',
						'href' => $link_back,
						'icon' => 'flag'
					], [
						'title' => 'Add state',
						'icon' => 'plus'
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
		<form action="" method="post" enctype="multipart/form-data" class="form-std" role="form" id="formUserInfo">
			<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />

			<div class="box-body">
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label>State (english)</label>

							<input type="text" name="state" value="<?=utils::safeEcho(@$_POST['state'], 1);?>" class="form-control" autocomplete="off" />
						</div>

						<div class="form-group">
							<label>State (hindi)</label>

							<input type="text" name="state_hindi" value="<?=utils::safeEcho(@$_POST['state_hindi'], 1);?>" class="form-control" autocomplete="off" />
						</div>
					</div>
				</div>
			</div>
			<!-- /.box-body -->

			<div class="box-footer">
				<button type="submit" name="add" value="1" class="btn btn-primary"><i class="fa fa-plus-circle" aria-hidden="true"></i> <?=CMS::t('add');?></button>
				<button type="reset" name="reset" value="1" class="btn btn-default"><i class="fa fa-refresh" aria-hidden="true"></i> <?=CMS::t('reset');?></button>
			</div>
		</form>
	</div>
	<!-- /.box -->

	<!-- /.info boxes -->
</section>
<!-- /.content -->