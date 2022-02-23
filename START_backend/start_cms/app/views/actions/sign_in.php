<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

?>

<body class="hold-transition login-page">
	<div class="login-box">

		<?php if (!empty($response['errors'])) foreach ($response['errors'] as $e) { ?>
		<div class="callout callout-danger">
			<p><?=CMS::t($e);?></p>
		</div>
		<?php } ?>

		<div class="box box-primary login-box-body">
			<form action="" method="post">
                
				<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />
                <div class="logo-container">
                    <img src="<?=IMAGE_DIR;?>/l2.png" alt="">
                </div>
				<div class="form-group has-feedback">
					<input type="text" name="ad_login" value="<?=utils::safeEcho(@$_POST['ad_login'], 1);?>" class="form-control text-field" placeholder="Username" autofocus="autofocus" />
					<span class="fa fa-user form-control-feedback fa-lg"></span>
				</div>

				<div class="form-group has-feedback">
					<input type="password" name="ad_password" placeholder="<?=CMS::t('login_password_placeholder');?>" class="form-control text-field" />
					<span class="fa fa-lock form-control-feedback fa-lg"></span>
				</div>

				<div class="row button-bl">
					<button type="submit" name="ad_send" value="1" class="btn btn-primary btn-block"><?=CMS::t('login_sign_in');?></button>
				</div>

				<div class="row text-center loginbox-password-recovery-link">
					<a href="?controller=base&amp;action=password_recovery"><?=CMS::t('login_password_recovery');?></a>
				</div>
			</form>
		</div>
	</div>

	<!--div class="login-logotypes-stripe">
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
	</div-->

    <script>
$(".text-field").on("focus", function() {
	$(this).addClass("active-input");
})
$(".text-field").on("blur", function() {
	$(this).removeClass("active-input");
})
    </script>
</body>
