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
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
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

view::appendJs(SITE.CMS_DIR.JS_DIR.'fancybox/jquery.fancybox.pack.js');
view::appendJs(SITE.CMS_DIR.JS_DIR.'utils.js');
view::appendJs(SITE.CMS_DIR.JS_DIR.'custom-skin.js');

print view::outputJsList();

?>
		<script type="text/javascript">
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

	<?=$content;?>
</html>