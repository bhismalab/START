<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

?>

	<body class="hold-transition lockscreen">
		<div class="lockscreen-wrapper">
			<div class="lockscreen-logo">
				<?=CMS::$site_settings['cms_name_formatted'];?>
			</div>

			<?php if (isset($_POST['change']) && !empty($response['errors'])) foreach ($response['errors'] as $e) { ?>
			<div class="callout callout-danger">
				<!-- <h4></h4> -->
				<p><?=CMS::t($e);?></p>
			</div>
			<?php } ?>

			<?php if (isset($_POST['change']) && $response['success']) { ?>
			<div class="callout callout-success">
				<!-- <h4></h4> -->
				<p><?=CMS::t($response['message']);?></p>
			</div>
			<?php utils::delayedRedirect(SITE.CMS_DIR); ?>
			<?php } ?>

			<div class="lockscreen-name"><?=ADMIN_INFO;?></div>

			<div class="lockscreen-item">
				<div class="lockscreen-image">
					<?=view::gravatar($_SESSION[CMS::$sess_hash]['ses_adm_login'], 128, [
						'class' => 'img-circle',
						'alt' => ADMIN_INFO
					]);?>
				</div>

				<form method="post" action="" class="lockscreen-credentials">
					<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />
					<input type="hidden" name="approvement_key" value="<?=$approvement_key;?>" />

					<div class="input-group">
						<input type="password" name="password" value="" placeholder="<?=CMS::t('password_approve_placeholder');?>" class="form-control" />

						<div class="input-group-btn">
							<button type="submit" name="approve" value="1" class="btn"><i class="fa fa-arrow-right text-muted"></i></button>
						</div>
					</div>
				</form>
			</div>

			<div class="help-block text-center"><?=CMS::t('password_approve_descr');?></div>

			<div class="text-center"><a href="?controller=base&amp;action=sign_out"><?=CMS::t('password_recovery_login');?></a></div>
		</div>
	</body>
