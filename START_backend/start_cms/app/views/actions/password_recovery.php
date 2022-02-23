<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

?>


<?php

// load resourses

// load Bootstrap Datepicker
view::appendCss(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/css/bootstrap-datepicker3.css');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/js/bootstrap-datepicker.min.js');

?>

<body class="hold-transition login-page">
	<div class="login-box" style="margin: 30px auto;">

		<?php if (isset($_POST['get_security_hash']) && !empty($response['errors'])) foreach ($response['errors'] as $e) { ?>
		<div class="callout callout-danger">
			<!-- <h4></h4> -->
			<p><?=CMS::t($e);?></p>
		</div>
		<?php } ?>

		<?php if (isset($_POST['get_security_hash']) && $response['success']) { ?>
		<div class="callout callout-success">
			<!-- <h4></h4> -->
			<p><?=CMS::t('password_recovery_suc_email_sended', [
				'{email}' => utils::safeEcho($_POST['email'], 1)
			]);?></p>
		</div>
		<?php } ?>

		<div class="box box-primary login-box-body" >
            <div class="logo-container" style="margin-bottom: 40px;">
                <img src="<?=IMAGE_DIR;?>/l2.png" alt="">
            </div>
            <div class="password_recovery-title text-center" style="color: #999; font-size: 18px; font-weight: bold; margin-bottom: 15px;"><?=CMS::t('password_recovery_title');?></div>

			<form action="" method="post">
				<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />

				<div class="form-group has-feedback">
					<input type="text" name="email" value="<?=utils::safeEcho(@$_POST['email'], 1);?>" class="form-control text-field" placeholder="Username" autofocus="autofocus" />
					<span class="fa fa-user form-control-feedback fa-lg"></span>
				</div>

				<div class="form-group has-feedback">
					<input type="text" name="birth_date" value="<?=utils::safeEcho(@$_POST['birth_date'], 1);?>" class="form-control datepicker text-field" placeholder="Birth date" />
					<span class="fa fa-calendar-o form-control-feedback fa-lg"></span>
				</div>

				<script type="text/javascript">
// <![CDATA[

$(document).ready(function() {
	$('[name="birth_date"]').datepicker({
		autoclose: true,
		format: 'dd.mm.yyyy',
		clearBtn: true,
		language: 'en',
		defaultViewDate: {year: '<?=(date('Y')-25);?>', month: <?=(date('n')-1);?>, day: <?=date('j');?>}
	});
});

// ]]>
				</script>

				<div class="row button-bl">
					<button type="submit" name="get_security_hash" value="1" class="btn btn-primary btn-block">Reset password</button>
				</div>

				<div class="text-center" style="margin-top: 12px; font-size: 16px; font-weight: bold;">
					<a href="<?=SITE.CMS_DIR;?>">
						<?=CMS::t('password_recovery_login');?>
					</a>
				</div>
			</form>
		</div>
	</div>

	<!--<div class="login-logotypes-stripe">
		<table><tr><td>
		<span class="logostripe-logo"><img src="<?/*=IMAGE_DIR;*/?>partners/university-of-reading.png" alt="University of Reading" /></span>
		</td><td>
		<span class="logostripe-logo"><img src="<?/*=IMAGE_DIR;*/?>partners/medi.png" alt="" /></span>
		</td><td>
		<span class="logostripe-logo"><img src="<?/*=IMAGE_DIR;*/?>partners/institute-of-technology.png" alt="" /></span>
		</td><td>
		<span class="logostripe-logo"><img src="<?/*=IMAGE_DIR;*/?>partners/phfi.png" alt="Public Health Foundation of India" /></span>
		</td><td>
		<span class="logostripe-logo"><img src="<?/*=IMAGE_DIR;*/?>partners/birkbeck.png" alt="Birkbeck University of London" /></span>
		</td><td>
		<span class="logostripe-logo"><img src="<?/*=IMAGE_DIR;*/?>partners/sangath.png" alt="Sangath" /></span>
		</td><td>
		<span class="logostripe-logo"><img src="<?/*=IMAGE_DIR;*/?>partners/tbox.png" alt="TherapyBox" /></span>
		</td></tr></table>
	</div>-->
    <script>

        $(".text-field").on("focus", function () {
            console.log("in");
            $(this).addClass("active-input");
        })
        $(".text-field").on("blur", function () {
            console.log("in");
            $(this).removeClass("active-input");
        })
    </script>
</body>
