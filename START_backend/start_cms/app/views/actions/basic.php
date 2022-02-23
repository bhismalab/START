<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

?>

	<!-- Content Header (Page header) -->
	<section class="content-header">
		<h1>
			<?=CMS::t('menu_item_cms_users_list');?>
			<!-- <small>Subtitile</small> -->
		</h1>

		<!-- <ol class="breadcrumb">
			<li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
			<li class="active">Dashboard</li>
		</ol> -->
	</section>

	<!-- Main content -->
	<section class="content">
		<!-- Info boxes -->

		<pre><?php var_export($list); ?></pre>

		<!-- /.info boxes -->
	</section>
	<!-- /.content -->