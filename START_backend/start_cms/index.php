<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;

define('_VALID_PHP', true);
if (isset($_GET['PHPSESSID'])) {
	header('HTTP/1.1 404 Not Found');
	die();
}


define('CONFIG_DIR', 'app/config/');
require_once CONFIG_DIR.'app.php';


/* Register shutdown function to catch fatal errors */

register_shutdown_function(function() {
	$root = @ROOT;
	if (empty($root)) {
		$root = $_SERVER['DOCUMENT_ROOT'].'/start_cms/';
	}

	$errfile = "unknown file";
	$errstr = "shutdown";
	$errno = E_CORE_ERROR;
	$errline = 0;

	$error = error_get_last();

	if ($error!==NULL) {
		$errno = $error["type"];
		$errfile = $error["file"];
		$errline = $error["line"];
		$errstr = $error["message"];

		//file_put_contents($_SERVER['DOCUMENT_ROOT'].'/Start/start_cms/debug/fatal_caught.txt', 'Error #'.$errno.' occured in '.$errfile.' at '.$errline.' with message: '.$errstr);
		//file_put_contents($_SERVER['DOCUMENT_ROOT'].'/start_cms/debug/fatal_caught.txt', 'Error #'.$errno.' occured in '.$errfile.' at '.$errline.' with message: '.$errstr);
		if (substr($errstr, 0, 19)=='Allowed memory size') {
			include($root.VIEW_DIR.'500_memory_limit.php');
		} else {
			@file_put_contents($root.'debug/last_caught_error.txt', 'Error #'.$errno.' occured in '.$errfile.' at '.$errline.' with message: '.$errstr);
		}
    }

	die();
});

@session_start();

/*
	Entry point.
	Loads configuration files, invokes CMS wich perfoms all neccessary startup operations
	in CMS::init(), resolves page.
*/

require_once CORE_DIR.'CMS.php';
require(__DIR__.'/deps/vendor/autoload.php');
spl_autoload_register(['tb\start_cms\CMS', 'autoload'], true);
CMS::init();


header('Content-type: text/html; charset=utf-8');
header('X-Frame-Options: DENY');

/*
	Page resolving.
	If no session started and no page specified show login page.
	If no page specified and user is authorized, redirect him to landing page.
	Otherwise let CMS to resolve this controller/action.
	It will check user privilegies and show appropriate page.
	CMS::resolve() going to be called if unauthorized user trying to access password
	recovery page or any other public page except login, or if user is authorized and
	tries to access any controller/action.
*/

if (empty($_SESSION[CMS::$sess_hash]['ses_adm_id']) && empty($_GET['controller'])) {
	print CMS::resolve('base', 'sign_in');
	die();
} else if (!empty($_SESSION[CMS::$sess_hash]['ses_adm_id']) && empty($_GET['controller'])) {
	utils::redirect(SITE.CMS_DIR.CMS::getLandingPage());
	die();
}

print CMS::resolve();

session_write_close();

?>