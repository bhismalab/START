<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

?>


	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>
			<?=CMS::t('delete');?>
			<!-- <small>Subtitile</small> -->
		</h1>

		<!-- <ol class="breadcrumb">
			<li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
			<li class="active">Dashboard</li>
		</ol> -->
	</section>

	<!-- Content Header (Page header) -->
	<section class="contextual-navigation">
		<nav>
			<a href="<?=utils::safeEcho($link_back, 1);?>" class="btn btn-default"><i class="fa fa-arrow-left" aria-hidden="true"></i> <?=CMS::t('back');?></a>
		</nav>
	</section>


	<!-- Main content -->
	<section class="content">
		<?php
			if (!empty($op)) {
				if ($op['success']) {
					print view::notice($op['message'], 'success');
					utils::delayedRedirect($link_back);
				} else {
					print view::notice(empty($op['errors'])? $op['message']: $op['errors']);
				}
			}
		?>
	</section>
	<!-- /.content -->