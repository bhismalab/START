<?php

namespace tb\start_cms\helpers;

use tb\start_cms\helpers\utils;

if (!defined('_VALID_PHP')) die('Direct access to this location is not allowed.');

class security {
	public static $CSRF_token;

	public static function getCSRF_token() {
		return md5(session_id().'-'.$_SERVER['REMOTE_ADDR'].'-'.$_SERVER['HTTP_USER_AGENT']);
	}
	
	public static function checkCSRF_token($input_token) {
		$ref = @(string)$_SERVER['HTTP_REFERER'];
		$scheme = parse_url($ref, PHP_URL_SCHEME);
		$site = SITE;
		if (substr($site, 0, 2)=='//') {
			$site = $scheme.':'.$site;
		}

		if ((self::$CSRF_token!=$input_token) || (strpos($ref, $site)!==0)) {
			header('HTTP/1.0 400 Bad Request');
			die('400 Bad Request');
		}
	}

	public static function getRandStr($length=8) {
		$set = 'abcdefghijkmnpqrstuvwxyz23456789';
		$set_chars_num = strlen($set);
		$str = '';
		for ($i=0; $i<$length; $i++) {
			$ch = mt_rand(0, $set_chars_num-1);
			$str.=substr($set, $ch, 1);
		}
		$str = str_shuffle($str);
		return $str;
	}

	public static function generatePassword($params=array()) {
		$min_allowed_length = 8;
		$max_allowed_length = 32;
		$length = $min_allowed_length;
		if (isset($params['length'])) {
			$custom_length = intval($params['length']);
			if (($custom_length>=$min_allowed_length) && ($custom_length<=$max_allowed_length)) {
				$length = $custom_length;
			}
		} else if (isset($params['min_length']) || isset($params['max_length'])) {
			$min_length = intval(@$params['min_length']);
			if (($min_length>=$min_allowed_length) && ($min_length<=$max_allowed_length)) {
				$max_length = intval(@$params['max_length']);
				if (($max_length>=$min_allowed_length) && ($max_length<=$max_allowed_length) && ($min_length<$max_length)) {
					$length = mt_rand($min_length, $max_length);
				} else {
					$length = $min_length;
				}
			}
		}
		$sets = [
			'standard' => 'abcdefghijkmnpqrstuvwxyz23456789',
			'extended' => 'abcdefghijkmnpqrstuvwxyz23456789!@#%^&*(){}[]+=_-,.?',
			'extended_cs' => 'abcdefghijkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789!@#%^&*(){}[]+=_-,.?',
			'alphabetical' => 'abcdefghijklmnopqrstuvwxyz',
			'numeric' => '0123456789',
		];
		$set = $sets['standard'];
		if (isset($params['set']) && isset($sets[$params['set']])) {
			$set = $sets[$params['set']];
		}
		$set_chars_num = strlen($set);

		$pwd = '';
		for ($i=0; $i<$length; $i++) {
			$ch = mt_rand(0, $set_chars_num-1);
			$pwd.=substr($set, $ch, 1);
		}

		$pwd = str_shuffle($pwd);

		return $pwd;
	}

	public static function generatePasswordHash($pwd, $salt='') {
		$random_salt = md5(self::generatePassword([
			'set' => 'extended_cs',
			'min_length' => 12,
			'max_length' => 16
		]));
		$hash = hash_hmac('sha256', md5($pwd), $random_salt.$salt);
		return $hash.$random_salt;
	}

	public static function validatePassword($hash, $pwd, $salt='') {
		$random_salt = substr($hash, 64, 32);
		if (!$random_salt || (strlen($hash)<96)) {return false;}
		$input_hash = hash_hmac('sha256', md5($pwd), $random_salt.$salt);
		//die($input_hash.$random_salt);
		return ($input_hash==substr($hash, 0, 64));
	}
}

?>