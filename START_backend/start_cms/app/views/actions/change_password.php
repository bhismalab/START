<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

?>

<body class="hold-transition login-page">
	<div class="login-box" style="margin: 30px auto;">

		<?php if (isset($_POST['change_password']) && !empty($response['errors'])) foreach ($response['errors'] as $e) { ?>
		<div class="callout callout-danger">
			<!-- <h4></h4> -->
			<p><?=CMS::t($e);?></p>
		</div>
		<?php } ?>

		<?php if (isset($_POST['change_password']) && $response['success']) { ?>
		<div class="callout callout-success">
			<!-- <h4></h4> -->
			<p><?=CMS::t($response['message']);?></p>
		</div>
		<?php utils::delayedRedirect(SITE.CMS_DIR); ?>
		<?php } ?>

		<div class="box box-primary login-box-body" style="padding-top: 180px;">
			<div class="password_recovery-title text-center" style="color: #999; font-size: 18px; font-weight: bold; margin-bottom: 15px;"><?=CMS::t('password_change_title');?></div>

			<form action="" method="post">
				<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />
				<input type="hidden" name="username" value="<?=$username;?>" />
				<input type="hidden" name="security_hash" value="<?=$security_hash;?>" />

				<div class="form-group has-feedback">
					<input type="password" name="password" value="" class="form-control" placeholder="Your new password" autofocus="autofocus" />
					<span class="fa fa-user form-control-feedback"></span>
				</div>

				<div class="row">
					<button type="submit" name="change_password" value="1" class="btn btn-primary btn-block">Change password</button>
				</div>

				<div class="text-center" style="margin-top: 12px; font-size: 16px; font-weight: bold;">
					<a href="<?=SITE.CMS_DIR;?>">
						<?=CMS::t('password_recovery_login');?>
					</a>
				</div>
			</form>
		</div>
	</div>

	<div class="login-logotypes-stripe">
		<table><tr><td>
		<span class="logostripe-logo"><img src="<?=IMAGE_DIR;?>partners/university-of-reading.png" alt="University of Reading" /></span>
		</td><td>
		<span class="logostripe-logo"><img src="<?=IMAGE_DIR;?>partners/medi.png" alt="" /></span>
		</td><td>
		<span class="logostripe-logo"><img src="<?=IMAGE_DIR;?>partners/institute-of-technology.png" alt="" /></span>
		</td><td>
		<span class="logostripe-logo"><img src="<?=IMAGE_DIR;?>partners/phfi.png" alt="Public Health Foundation of India" /></span>
		</td><td>
		<span class="logostripe-logo"><img src="<?=IMAGE_DIR;?>partners/birkbeck.png" alt="Birkbeck University of London" /></span>
		</td><td>
		<span class="logostripe-logo"><img src="<?=IMAGE_DIR;?>partners/sangath.png" alt="Sangath" /></span>
		</td><td>
		<span class="logostripe-logo"><img src="<?=IMAGE_DIR;?>partners/tbox.png" alt="TherapyBox" /></span>
		</td></tr></table>
	</div>
</body>
