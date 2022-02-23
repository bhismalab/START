<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

?><!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<!-- <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" /> -->
		<meta name="viewport" content="width=1280px" />
		<meta name="csrf-token" content="<?=utils::safeEcho($CSRF_token, 1);?>" />

		<link rel="icon" href="/favicon.ico" type="image/x-icon" />
		<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon" />

		<title><?=utils::safeEcho(CMS::$site_settings['cms_name'], 1);?> - <?=utils::safeEcho(self::$title, 1);?></title>

<?php

view::prependCss(SITE.CMS_DIR.CSS_DIR.'start-skin.css');
view::prependCss(SITE.CMS_DIR.CSS_DIR.'admin-lte-2.3.7/skins/skin-green.css');
view::prependCss(SITE.CMS_DIR.CSS_DIR.'admin-lte-2.3.7/AdminLTE.css');
view::prependCss(SITE.CMS_DIR.JS_DIR.'select2/css/select2.css');
view::prependCss(SITE.CMS_DIR.CSS_DIR.'font-awesome-4.7.0/font-awesome.css');
view::prependCss(SITE.CMS_DIR.CSS_DIR.'bootstrap-3.3.7/bootstrap.min.css');

view::appendCss(JS_DIR.'fancybox/jquery.fancybox.css');

print view::outputCssList();

?>

		<script type="text/javascript">
// <![CDATA[
var t = <?=json_encode(CMS::$lang);?>;
// ]]>
		</script>

<?php

view::prependJs(SITE.CMS_DIR.JS_DIR.'admin-lte-2.3.7/app.min.js');
view::prependJs(SITE.CMS_DIR.JS_DIR.'jquery.slimscroll.min.js');
view::prependJs(SITE.CMS_DIR.JS_DIR.'fastclick.min.js');
view::prependJs(SITE.CMS_DIR.JS_DIR.'bootstrap-3.3.7/bootstrap.min.js');
view::prependJs(SITE.CMS_DIR.JS_DIR.'jquery-2.2.3.min.js');

view::appendJs(SITE.CMS_DIR.JS_DIR.'bootbox.min.js');
view::appendJs(SITE.CMS_DIR.JS_DIR.'fancybox/jquery.fancybox.pack.js');
view::appendJs(SITE.CMS_DIR.JS_DIR.'utils.js');
view::appendJs(SITE.CMS_DIR.JS_DIR.'custom-skin.js');

print view::outputJsList();

?>
		<script type="text/javascript">
			bootbox.addLocale('<?=utils::safeJsEcho($_SESSION[CMS::$sess_hash]['ses_adm_lang'], 1);?>', {
				OK: '<?=utils::safeJsEcho(CMS::t('js_ok'), 1);?>',
				CANCEL: '<?=utils::safeJsEcho(CMS::t('js_cancel'), 1);?>',
				CONFIRM: '<?=utils::safeJsEcho(CMS::t('js_confirm'), 1);?>',
			});
			bootbox.setLocale('<?=utils::safeJsEcho($_SESSION[CMS::$sess_hash]['ses_adm_lang'], 1);?>');
			$(document).ready(function() {
				$('.fancybox').fancybox();
			});
		</script>

		<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
		<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
		<!--[if lt IE 9]>
		<script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
		<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
		<![endif]-->

	</head>
	<body class="hold-transition sidebar-mini skin-start sidebar-collapse">
		<div class="wrapper">
			<header class="main-header">

				<!-- Logo -->
				<a target="_blank" class="logo">
					<img src="<?=IMAGE_DIR;?>logo.png" alt="" />
				</a>

				<!-- Header Navbar: style can be found in header.less -->
				<nav class="navbar navbar-static-top">

					<div class="page-title"><?=utils::safeEcho(self::$title, 1);?></div>

					<!-- Navbar Right Menu -->
					<div class="navbar-custom-menu">
						<ul class="nav navbar-nav">

							<!-- User Account: style can be found in dropdown.less -->
							<li class="dropdown user user-menu">
								<a href="#" class="dropdown-toggle" data-toggle="dropdown">
									<span class="hidden-xs"><?=ADMIN_INFO;?></span>

									<i class="fa fa-caret-down" aria-hidden="true"></i>
								</a>

								<ul class="dropdown-menu">
									<!-- User image -->
									<li class="user-header">
										<p>
											<?=ADMIN_INFO;?> - <?=CMS::t('cms_users_role_'.$_SESSION[CMS::$sess_hash]['ses_adm_type']);?>
											<small><?=CMS::t('cms_user_reg_date');?> <?=utils::formatMySQLDate('d.m.Y', $_SESSION[CMS::$sess_hash]['ses_adm_reg_date']);?></small>
										</p>
									</li>

									<!-- Menu Footer-->
									<li class="user-footer">
										<?php if (CMS::hasAccessTo('cms_users/edit')) { ?>
										<div class="pull-left">
											<a href="?controller=cms_users&amp;action=edit&amp;id=<?=$_SESSION[CMS::$sess_hash]['ses_adm_id'];?>" class="btn btn-default btn-flat"><?=CMS::t('edit_cms_user_self');?></a>
										</div>
										<?php } else if (CMS::hasAccessTo('cms_users/change_my_password')) { ?>
										<div class="pull-left">
											<a href="?controller=cms_users&amp;action=change_my_password&amp;id=<?=$_SESSION[CMS::$sess_hash]['ses_adm_id'];?>" class="btn btn-default btn-flat">Change password</a>
										</div>
										<?php } ?>

										<div class="pull-right">
											<a href="?controller=base&amp;action=sign_out" class="btn btn-default btn-flat"><?=CMS::t('logout');?></a>
										</div>
									</li>
								</ul>
							</li>
						</ul>
					</div>
				</nav>
			</header>

			<!-- Left side column. contains the logo and sidebar -->
			<aside class="main-sidebar">
				<!-- sidebar: style can be found in sidebar.less -->
				<section class="sidebar">
					<ul class="sidebar-menu">
						<!-- <li class="header"><?=CMS::t('menu_title');?></li> -->

						<?=view::widget('menu');?>

						<!-- <li><a href="documentation/index.html"><i class="fa fa-book"></i> <span>Documentation</span></a></li> -->
					</ul>
				</section>
				<!-- /.sidebar -->
			</aside>


			<!-- Content Wrapper. Contains page content -->
			<div class="content-wrapper">
				<?=$content;?>
			</div>
			<!-- /.content-wrapper -->
		</div>
		<!-- ./wrapper -->
	</body>
</html>