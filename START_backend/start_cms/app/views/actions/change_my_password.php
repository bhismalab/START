<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined('_VALID_PHP')) {die('Direct access to this location is not allowed.');}

view::$title = 'Change password';

?>


<?php

// load resourses

// load Bootstrap Datepicker
view::appendCss(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/css/bootstrap-datepicker3.css');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/js/bootstrap-datepicker.min.js');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/locales/bootstrap-datepicker.'.$_SESSION[CMS::$sess_hash]['ses_adm_lang'].'.min.js');

?>


<!-- Content Header (Page header) -->
<section class="content-header">
	<h1>
		Change my password
		<small><?=utils::safeEcho(@$user['name'], 1);?></small>
	</h1>

	<div class="clearfix"></div>
</section>


<!-- Main content -->
<section class="content">
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
		<form action="" method="post" enctype="multipart/form-data" class="form-std" autocomplete="off" role="form" id="formUserInfo">
			<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />

			<div class="box-body">
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label>Username</label>

							<input type="text" name="login" value="<?=utils::safePostValue('login', $user['login'], 1);?>" class="form-control" autocomplete="off" disabled="disabled" />
						</div>

						<div class="form-group">
							<label>Password</label>

							<div class="input-group">
								<div class="input-group-btn">
									<a class="btn btn-danger" id="btnShowPassword"><i class="fa fa-eye" aria-hidden="true"></i></a>
								</div>

								<input type="password" name="pwd" class="form-control" autocomplete="off" id="inpSetPassword" />

								<div class="input-group-btn">
									<a class="btn btn-success" id="btnGeneratePassword"><i class="fa fa-asterisk" aria-hidden="true"></i> <?=CMS::t('cms_user_pwd_generate');?></a>
								</div>
							</div>

							<script type="text/javascript">
// <![CDATA[
$('#btnGeneratePassword').on('click', function() {
	var pwd = utils.generatePassword();
	bootbox.dialog({
		title: 'Password Generator',
		message: '<p class="text-center" id="pPwdGenVal" style="font-size: 28px;">'+pwd+'</p><p><label style="font-weight: normal;"><input type="checkbox" name="pwd_gen_approve" value="1" /> I have copied this password in a safe place.</label></p>',
		backdrop: true,
		buttons: {
			cancel: {
				label: '<i class="fa fa-times"></i> Cancel'
			},
			regenerate: {
				label: '<i class="fa fa-refresh"></i> Generate again',
				callback: function () {
					pwd = utils.generatePassword();
					$('#pPwdGenVal').text(pwd);
					return false;
				}
			},
			confirm: {
				className: 'btn-disabled',
				label: '<i class="fa fa-check"></i> Use password',
				callback: function () {
					if ($('[data-bb-handler="confirm"]').hasClass('btn-disabled')) {
						return false;
					} else {
						$('#inpSetPassword').val($('#pPwdGenVal').text());
					}
				}
			}
		},
	});
});

$(document).on('click', '[name="pwd_gen_approve"]', function() {
	if (this.checked) {
		$('[data-bb-handler="confirm"]').attr('class', 'btn btn-primary');
	} else {
		$('[data-bb-handler="confirm"]').attr('class', 'btn btn-disabled');
	}
});

$('#btnShowPassword').on('click', function() {
	var el = $('#inpSetPassword').get(0);
	if (el.type=='password') {
		el.type = 'text';
		$('i', this).removeClass('fa-eye');
		$('i', this).addClass('fa-eye-slash');
		$(this).removeClass('btn-danger');
		$(this).addClass('btn-success');
	} else {
		el.type = 'password';
		$('i', this).removeClass('fa-eye-slash');
		$('i', this).addClass('fa-eye');
		$(this).removeClass('btn-success');
		$(this).addClass('btn-danger');
	}
});
// ]]>
							</script>
						</div>
					</div>

					<div class="col-md-6">
					</div>
				</div>
			</div>
			<!-- /.box-body -->

			<div class="box-footer">
				<button type="submit" name="save" value="1" class="btn btn-primary"><i class="fa fa-save" aria-hidden="true"></i> <?=CMS::t('save');?></button>
				<!-- <button type="submit" name="apply" value="1" class="btn btn-primary"><i class="fa fa-check" aria-hidden="true"></i> <?=CMS::t('apply');?></button> -->
				<button type="reset" name="reset" value="1" class="btn btn-default"><i class="fa fa-refresh" aria-hidden="true"></i> <?=CMS::t('reset');?></button>
			</div>
		</form>
	</div>
	<!-- /.box -->

	<!-- /.info boxes -->
</section>
<!-- /.content -->