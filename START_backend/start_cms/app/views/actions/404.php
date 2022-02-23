<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

?>

	<body class="hold-transition 404-page">
		<section class="content">
			<div class="error-page">
				<h2 class="headline text-yellow"><?=CMS::t('404_title');?></h2>

				<div class="error-content">
					<h3><i class="fa fa-warning text-yellow"></i> <?=CMS::t('404_headline');?></h3>

					<p>
						<?=CMS::t('404_descr');?>
					</p>

					<!-- <form class="search-form">
						<div class="input-group">
							<input type="text" name="search" class="form-control" placeholder="Search" />

							<div class="input-group-btn">
								<button type="submit" name="submit" class="btn btn-warning btn-flat"><i class="fa fa-search"></i></button>
							</div>
						</div>
					</form> -->
				</div>
			</div>
		</section>
	</body>
