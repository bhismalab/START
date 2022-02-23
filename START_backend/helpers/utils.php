<?php
namespace yii\helpers;
use Yii;

class utils {

	/* validation */

	public static function checkPIN($pin) {
		return preg_match('/^[0-9A-Z]{7}$/i', $pin);
	}

	public static function checkLogin($login) {
		return preg_match('/^[0-9a-z\_\-\@\.]{3,32}$/i', $login);
	}

	public static function checkPass($pass) {
		return preg_match('|^[a-zA-Z0-9\.\_\-\+\!\@\#\$\%\^\&\*\(\)\=\`\~\{\[\]\}\;\:\>\<\?\'\"\/\\\]{6,64}$|', $pass);
	}

	public static function is_valid_email($email) {
		return filter_var((string)$email, FILTER_VALIDATE_EMAIL);
	}

	public static function is_valid_URI_element($s) {
		return preg_match('/^[0-9a-z\_\-]{1,255}$/i', $s);
	}

	public static function is_valid_human_name($name) {
		return preg_match('/^[a-zа-яА-ЯA-ZüöğıəçşёÜÖĞİƏÇŞЁ\-\`\\\'\s]{2,64}$/u', (string)$name);
	}

	public static function is_valid_human_nsp($nsp) {
		$nsp = (string)$nsp;
		$nsp = trim($nsp);
		return preg_match('/^[a-zа-яА-ЯA-ZüöğıəçşёÜÖĞİƏÇŞЁ]{2,}\s+[a-zа-яА-ЯA-ZüöğıəçşёÜÖĞİƏÇŞЁ\-\`\\\'\s]+$/u', $nsp);
	}

	public static function is_valid_phone($phone) {
		return preg_match('/^\+994\d{9}$/u', (string)$phone);
	}

	public static function valid_date($date) {
		return preg_match('/^\d{2}\.\d{2}\.\d{4}$/', $date);
	}

	public static function date2stamp($time) {
		$matches = array();
		if (preg_match('/^(\d{2})\.(\d{2})\.(\d{4})$/', $time, $matches)) {
			return mktime(0, 0, 0, $matches[2], $matches[1], $matches[3]);
		}
		return false;
	}

	public static function stamp2date($timestamp) {
		return date('d.m.Y', $timestamp);
	}

	public static function formatPlainDate($format, $date) {
		$date = self::date2stamp($date);
		if (empty($date)) {return '';}
		return @date($format, $date);
	}

	public static function parseMySQLDate($date) {
		@list($date, $time) = explode(' ', $date);
		if (empty($date)) {return 0;}
		$date_parts = [];
		if (preg_match('/^(\d{4})\-(\d{2})\-(\d{2})$/', $date, $date_parts)) {
			$time_parts = [];
			@preg_match('/^(\d{2})\:(\d{2})\:(\d{2})$/', $time, $time_parts);
			return mktime(@$time_parts[1], @$time_parts[2], @$time_parts[3], $date_parts[2], $date_parts[3], $date_parts[1]);
		}
		return 0;
	}

	public static function formatMySQLDate($format, $date) {
		$date = self::parseMySQLDate($date);
		if (empty($date)) {return '';}
		return @date($format, $date);
	}

	public static function parseXLSXDate($date) {
		$d = false;
		if (@preg_match('/^\d{2}\-\d{2}\-\d{2}$/', $date)) {
			$d = explode('-', $date);
			$d = (($d[2]>date('y'))? '19': '20').$d[2].'-'.$d[0].'-'.$d[1];
		} else if (@preg_match('/^\d{2}\.\d{2}\.\d{4}$/', $date)) {
			$d = explode('.', $date);
			$d = $d[2].'-'.$d[1].'-'.$d[0];
		} else if (@preg_match('/^\d{2}\,\d{2}\,\d{4}$/', $date)) {
			$d = explode(',', $date);
			$d = $d[2].'-'.$d[1].'-'.$d[0];
		}
		return self::parseMySQLDate($d);
	}

	public static function formatXLSXDate($format, $date) {
		$date = self::parseXLSXDate($date);
		if (empty($date)) {return '';}
		return @date($format, $date);
	}

	public static function changeDateFormat($fromFormat, $toFormat, $date) {
		if ($fromFormat=='c') {
			return gmdate($toFormat, strtotime($date));
		}

		$date_parsed = date_parse_from_format($fromFormat, $date);
		if ($date_parsed['error_count']) {return false;}
		$timestamp = mktime($date_parsed['hour'], $date_parsed['minute'], $date_parsed['second'], $date_parsed['month'], $date_parsed['day'], $date_parsed['year']);

		return date($toFormat, $timestamp);
	}

	public static function checkDate($format, $date) {
		$date_parsed = date_parse_from_format($format, $date);
		if ($date_parsed['error_count']) {return false;}
		return checkdate($date_parsed['month'], $date_parsed['day'], $date_parsed['year']);
	}

	public static function validateDatetime($date, $format="Y-m-d\TH:i:sP") {
		$d = \DateTime::createFromFormat($format, $date);
		return ($d && ($d->format($format)==$date));
	}

	public static function checkFieldName($name) {
		return @preg_match( '/^[0-9a-z\_]{2,32}$/i', $name);
	}

	public static function checkEmail($email) { // deprecated
		if (preg_match('/^[a-z0-9][a-z0-9\.\_\-]+@[0-9a-z][0-9a-z\.\-]+\.[0-9a-z]{2,7}$/', $email)) {
			if (strlen($email)<=64) {
				return true;
			}
			return false;
		}

		return false;
	}

	public static function checkEmailSoftly($email) { // deprecated
		return ((strlen($email)>2) && strpos($email, '@'));
	}

	public static function checkEmailStrict($email) { // deprecated
		global $php_version_numeric;

		if ($php_version_numeric>=5.2) {
			return filter_var($email, FILTER_VALIDATE_EMAIL);
		} else {
			return self::checkEmail($email);
		}
	}

	public static function validEmail($email) {
		return filter_var($email, FILTER_VALIDATE_EMAIL);
	}

	public static function validURL($url) {
		return filter_var($url, FILTER_VALIDATE_URL);
	}

	public static function checkIP($ip) {
		if (preg_match('/^[0-9]{1,3}\.[0-9]{0,3}\.[0-9]{1,3}\.[0-9]{1,3}$/', $ip)) {
			return true;
		}

		return false;
	}

	public static function safeEcho($str, $return=false) {
		$str = (is_string($str)? htmlspecialchars($str): '');
		$str = strtr($str, [
			'javascript:' => ''
		]);
		if (!$return) {print $str; return 1;}
		return $str;
	}

	public static function safeJSEcho($str, $return=false) {
		$str = (is_string($str)? addslashes($str): '');
		$str = strtr($str, [
			"\n" => "\\\n"
		]);
		if (!$return) {print $str; return 1;}
		return $str;
	}

	public static function safePostValue($key, $default='', $escape_default=false) {
		if (isset($_POST[$key])) {
			return self::safeEcho($_POST[$key], 1);
		}
		if ($escape_default) {$default = htmlspecialchars($default, ENT_COMPAT, 'UTF-8');}
		return $default;
	}

	public static function getuniqid() {
		return md5(uniqid(rand(), true).'i*M4+1$');
	}

	public static function unescapeTagInnerHTML($html, $tag='noscript') {
		$html = preg_replace("/(.*)(?<=\<{$tag}\>)(.*)(?=\<\/{$tag}\>)(.*)/Usue", "\"\$1\".htmlspecialchars_decode(htmlspecialchars_decode(\"\$2\")).\"\$3\"", $html);
		return $html;
	}

	public static function strClear($str, $spch=0, $itag=0, $trim=1, $snqq=0, $skp=0) {
		if (is_scalar($str)) {$str = strval($str);} else {return '';}
		if ($trim) {$str = str_replace(array("\r\n", "\r", "\n", "\t"), ' ', trim($str));}
		if (get_magic_quotes_gpc()) {$str = stripslashes($str);}
		if ($itag) {$str = self::unescapeTagInnerHTML($str);}
		if ($spch) {$str = htmlspecialchars($str, ($snqq? ENT_QUOTES: ENT_COMPAT), 'UTF-8');}
		if ($skp) {$str = str_replace(array("'"), array("\'"), $str);}
		return $str;
	}

	public static function prep_str($str) {
		return self::strClear($str, 0, 0, 1, 0, 1);
	}

	public static function sanitizeStringByWhitelist($string, $whitelist='a-z0-9\_', $escape=0) {
		if ($escape) {$whitelist = preg_quote($whitelist);}

		return preg_replace("/[^".$whitelist."]?(.*?)[^".$whitelist."]?/usD", '$1', $string);
	}

	public static function stripNonFormatTags($str, $inform=true, $extended=true) {
		if ($inform) {
			$str = strtr($str, array(
				'<iframe' => '%IFRAME%<iframe',
				'<object' => '%OBJECT%<object'
			));
		}
		$allowed = '<div><article><p><cite><a><span><strong><b><em><i><code><pre><ul><li><ol><br>';
		if ($extended) {
			$allowed.='<img><audio><video>';
		}
		$str = strip_tags($str, $allowed);
		return $str;
	}

	public static function del_bad_ascii($str) {
		$str = str_replace(array("\r\n", "\r", "\n", "\t"), ' ', $str);
		$str = trim($str);

		return $str;
	}

	public static function limitWords($str, $limit, $hellip=1) {
		$words = explode(' ', $str);
		return implode(' ', array_slice($words, 0, $limit)).(($hellip && (count($words)>$limit))? '&hellip;': '');
	}

	public static function limitStingLength($str, $limit, $hellip=1) {
		$res = mb_substr($str, 0, $limit, 'utf-8');
		if ($hellip) {
			if (mb_strlen($str)>$limit) {
				$res.='...';
			}
		}
		return $res;
	}

	public static function getRussianWordEndingByNumber($n, $s='', $d='а', $m='ов') {
		$last_digit = $n-(floor($n/10)*10);
		$dec_n = floor(($n-$last_digit)/10);
		$dec_digit = $dec_n-(floor($dec_n/10)*10);
		return ((($dec_digit==1) || ($last_digit>=5) || ($last_digit==0))? $m: (($last_digit==1)? $s: $d));
	}

	public static function getAzerbaijanianWordEndingByNumber($n, $n12578='i', $n34='ü', $n6='ı', $n9='u', $n0='i') {
		$last_digit = $n-(floor($n/10)*10);
		if (in_array($last_digit, array(1, 2, 5, 7, 8))) {
			return $n12578;
		} else if (in_array($last_digit, array(3, 4))) {
			return $n34;
		} else if ($last_digit==6) {
			return $n6;
		} else {
			return ($last_digit? $n9: $n0);
		}
	}

	/* url */

	public static function redirect($url) {
		if (headers_sent()) {
			print "<script type=\"text/javascript\">
				location.href = '".self::safeJSEcho($url, 1)."';
			</script>";
		} else {
			header('HTTP/1.1 301 Moved Permanently');
			header('Location: '.$url);
		}

		die();
	}

	public static function goLoc($getParams=array()) {
		if (empty($getParams)) {return;}
		if (is_string($getParams)) {$getParams = explode(',', $getParams);}
		self::goLocWith($getParams);
	}

	public static function goLocWith($getParams=array()) {
		self::redirect(self::trueLink($getParams));
	}

	public static function delayedRedirect($url, $delay=2000) {
		print "<script type=\"text/javascript\">
			setTimeout(function() {
				document.location.href = '".self::safeJSEcho($url, 1)."';
			}, {$delay});
		</script>";
	}

	public static function trueLink($arr) {
		foreach ($_GET as $key=>$val) if (in_array($key, $arr)) if (is_array($val)) {$link_arr[] = self::array2url($val, $key);} else {$link_arr[] = $key.'='.$val;}
		$link_arr[] = time();
		$link = implode('&', $link_arr);
		return '?'.$link;
	}

	public static function array2url($array, $variable='') {
		$url = '';
		if (count($array)) {
			$all = array();
			if (!empty($variable)) {
				foreach ($array as $key=>$value) {
					if (is_array($value)) {
						$all[] = self::array2url($value, $variable.'['.$key.']');
					}
					if (is_string($value) || is_numeric($value)) {
						$all[] = $variable.'['.$key.']='.urlencode($value);
					}
				}
			} else {
				foreach ($array as $parameter=>$value) {
					if (is_array($value)) {
						$all[] = self::array2url($value, $parameter);
					}
					if (is_string($value) || is_numeric($value)) {
						$all[] = $parameter.'='.urlencode($value);
					}
				}
			}
			$url = implode('&', $all);
		}
		return $url;
	}

	public static function array2form($arr, $inp_pattern="<input type=\"hidden\" name=\"{name}\" value=\"{value}\" />\n", $NOT_NULL=true) {
		$form = '';
		if (is_array($arr) && count($arr)) {
			if (function_exists('http_build_query')) {
				$str = strtr(http_build_query($arr), array(
					'%5B' => '[',
					'%5D' => ']'
				));
			} else {
				$link_arr = array();
				foreach ($arr as $key=>$value) {
					if (is_array($value)) {
						$link_arr[] = self::array2url($value, $key);
					} else {
						$link_arr[] = $key.'='.urlencode($value);
					}
				}
				$str = implode('&', $link_arr);
			}
			if (empty($inp_pattern)) {return $str;}
			$inputs = explode('&', $str);
			foreach ($inputs as $i=>$param) {
				$inp = explode('=', $param);
				//if ($NOT_NULL && ($inp[1]==='')) {continue;}
				if ($NOT_NULL && empty($inp[1])) {continue;}
				$form.=strtr($inp_pattern, array(
					'{name}' => $inp[0],
					'{value}' => urldecode($inp[1])
				));
			}
		}
		return $form;
	}

	public static function parseURL($url) {
		$request = parse_url($url);
		if (!empty($request['query'])) {
			$request['query'] = strtr($request['query'], [
				'%5B' => '[',
				'%5D' => ']',
			]);
			parse_str($request['query'], $request['query_params']);
		}
		return $request;
	}

	public static function makeSEF($s) {
		$s = (string)$s;
		$s = strtolower(trim($s));
		$s = preg_replace('/\s+/', '-', $s);
		$s = self::translit($s, 1, 1);
		$s = strtr($s, ['(' => '', ')' => '', '“' => '', '”' => '']);
		$s = (string)substr($s, 0, 80);
		return $s;
	}

	/* security */

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

	public static function genNewPass($length=14) { // deprecated
		return substr(self::getuniqid(), 0, $length);
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

	/* array */

	public static function variableHasValue($var) {
		return !(empty($var) && ($var!=='0') && ($var!==false));
	}

	public static function isEmptyArrayRecursive($arr, $zerosAreValue=1) {
		if (empty($arr) && (!$zerosAreValue || ($zerosAreValue && !self::variableHasValue($arr)))) {return true;}
		if (is_array($arr)) {
			foreach ($arr as $key=>$val) {
				if (!self::isEmptyArrayRecursive($val, $zerosAreValue)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public static function array_filter_keys($arr, $keys, $skip_empty_values=true) { // 2016-08-23
		$keys_num = count($keys);
		if (!$keys_num || !is_array($arr) || !is_array($keys)) {return array();}
		$res = [];
		foreach ($keys as $k) {
			if ($skip_empty_values && !isset($arr[$k])) {continue;}
			$res[$k] = (isset($arr[$k])? $arr[$k]: '');
		}

		return $res;
	}

	public static function array_exclude_keys($arr, $keys) {
		return self::array_filter_keys($arr, array_diff(array_keys($arr), $keys));
	}

	public static function array_divide($array, $parts=1) {
		$parts = intval($parts);
		if (($parts<=1)) {return $array;}
		$total = count($array);
		if ($total<=$parts) {
			$result = array();
			foreach ($array as $item) {$result[] = array($item);}
			array_pad($result, $parts, array());
			return $result;
		}
		$rows = $total/$parts;
		$size = ceil($rows);
		$nobr_rows = floor($rows);
		$solid = $nobr_rows*$parts;
		$remains = $total-$solid;
		if (($size==$solid)||($remains==($parts-1))) {
			return array_chunk($array, $size);
		} else {
			$result = array();
			$i=0;$offset=0;
			while ($i<$parts) {
				$length = (($remains>0)? ($nobr_rows+1): $nobr_rows);
				$result[$i] = array_slice($array, $offset, $length);
				$offset+=$length;
				$remains--;
				$i++;
			}
			return $result;
		}
	}

	public static function array_attributes_to_columns($attributes_list) {
		// Transforms numeric array with assotiative arrays in values
		// to associative array with numeric arrays as items
		if (!is_array($attributes_list)) {
			$columns_list = false;
		} else {
			$columns_list = array();
			foreach ($attributes_list as $attributes) {
				foreach ($attributes as $column=>$value) {
					$columns_list[$column][] = $value;
				}
			}
		}
		return $columns_list;
	}

	public static function array_columns_to_attributes($columns_list) {
		// Transforms associative array with numeric arrays in values
		// to numeric array with associative arrays as items
		if (!is_array($columns_list)) {
			$attributes_list = false;
		} else {
			$attributes_list = array();
			foreach ($attributes_list as $column=>$row) {
				foreach ($row as $i=>$value) {
					$attributes_list[$i][$column] = $value;
				}
			}
		}
		return $attributes_list;
	}

	public static function arraySortByCol($arr, $col) {
		$by = [];
		foreach ($arr as $key=>$value) {
			$by[$key] = $value[$col];
		}
		array_multisort($by, SORT_ASC, SORT_STRING, $arr);
		return $arr;
	}

	/* html */

	public static function styleChange($style1, $style2, $uvn='i') { // deprecated
		if (!isset($cells)) {static $cells = array();}
		$cells[$uvn] = (isset($cells[$uvn])? ($cells[$uvn]+1): 1);
		return (($cells[$uvn]%2)? $style1: $style2);
	}

	public static function makeSearchable($str) {
		$str = strip_tags($str);
		$str = mb_strtolower($str, 'UTF-8');

		$str = str_replace(array("\t", "\r\n", "  "), ' ', $str);
		$str = str_replace(array('"', '&nbsp;', '&amp;', '&quot;', "'", "<", ">", "  "), ' ', $str);
		$str = trim($str);

		//$str = self::preString($str);
		$str = self::strClear($str, 1);

		return $str;
	}

// E-mail functions

	public static function sendEMail($to, $subject, $message, $from='', $filename='', $smtp_status='DENY') {
		if (!$from) {
			$from = 'no-reply@'.str_replace('www.', '', $_SERVER['HTTP_HOST']).'';
		}
		$uniqid = self::getuniqid();
		$headers='Date: '.date('r')."\r\n";
		$headers.="From: {$from}\r\n";
		$headers.="Message-ID: <{$uniqid}@{$_SERVER['HTTP_HOST']}>\r\n";
		$headers.="MIME-Version: 1.0\r\n";
		$headers.="Content-Type: multipart/mixed;boundary=\"{$uniqid}\"\r\n\r\n";
		$headers.="--{$uniqid}\r\n";
		$headers.="Content-type: text/html; charset=UTF-8\r\n";
		$headers.="Content-transfer-encoding: base64\r\n\r\n";

		$subject = '=?utf-8?B?'.base64_encode($subject).'?=';

		$message = chunk_split(base64_encode($message));

		if (is_array($filename) && !empty($filename['file_name']) && !empty($filename['file_alias'])) {
			$file_alias = $filename['file_alias'];
			$filename = $filename['file_name'];
		} else {
			$file_alias = basename($filename);
		}
		if (is_file($filename)) {
			$file = fopen($filename, 'rb');
			$message.="\r\n".'--'.$uniqid."\r\n";
			$message.='Content-Type: application/octet-stream; name="'.$file_alias.'"'."\r\n";
			$message.='Content-Transfer-Encoding: base64'."\r\n";
			$message.='Content-Disposition: attachment; filename="'.$file_alias.'"'."\r\n\r\n";
			$message.=chunk_split(base64_encode(fread($file, filesize($filename))))."\r\n";
		}
		$message.="\r\n\r\n--".$uniqid."--";

		$sended = false;
		if ($smtp_status!='FORCE') {
			$sended = @mail($to, $subject, $message, $headers);
		}
		if (!$sended && ($smtp_status!='DENY')) { // $smtp_status value can be one of 'DENY', 'ALLOW', 'FORCE'
			require_once 'class/smtp.class.php';
			$smtp = new smtp();
			$sended = $smtp->send($to, $subject, $message, $headers);
		}

		return $sended;
	}

	private function sendMailProxyCURL($daemon_url, $post, $debug=0) {
		$q = curl_init($daemon_url);
		curl_setopt($q, CURLOPT_POST, 1);
		curl_setopt($q, CURLOPT_HEADER, 0);
		curl_setopt($q, CURLOPT_REFERER, 'http://'.$_SERVER['HTTP_HOST'].'/');
		curl_setopt($q, CURLOPT_USERAGENT, 'X-UA PHP CURL ('.$_SERVER['HTTP_HOST'].')');
		curl_setopt($q, CURLOPT_HTTPHEADER, array(
			'Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
			'Accept-Language: en-us,en;q=0.5',
			'Accept-Encoding: gzip,deflate',
			'Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7',
			//'Keep-Alive: 115',
			//'Connection: keep-alive'
			'Connection: close'
		));
		curl_setopt($q, CURLOPT_POSTFIELDS, $post);
		curl_setopt($q, CURLOPT_RETURNTRANSFER, 1);
		$sended = curl_exec($q);

		if (!empty($debug)) {
			print "<pre>\nsendMailProxyCURL()\n".curl_errno($q)." ".curl_error($q)."\n".var_export(curl_getinfo($q), true)."\n</pre>";
		}

		return $sended;
	}

	private function sendMailProxySocket($daemon_url, $post, $debug=0, $multipart=1) {
		$r = "\r\n";

		$path = explode('://', $daemon_url);
		$path = explode('/', @$path[1]);
		$host = array_shift($path);
		$path = implode('/', $path);

		$errno = 0;
		$errstr = '';
		$q = @fsockopen($host, 80, $errno, $errstr, 20);
		if ($q) {
			$request = 'POST /'.$path.' HTTP/1.1'.$r;
			$request.='Host: '.$host.$r;
			$request.='User-Agent: X-UA PHP CURL ('.$_SERVER['HTTP_HOST'].')'.$r;
			$request.='Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'.$r;
			$request.='Accept-Language: en-us,en;q=0.5'.$r;
			$request.='Accept-Encoding: gzip,deflate'.$r;
			$request.='Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7'.$r;
			//$request.='Keep-Alive: 115'.$r;
			//$request.='Connection: keep-alive'.$r;
			$request.='Connection: close'.$r;
			$request.='Referer: http://'.$_SERVER['HTTP_HOST'].'/'.$r;
			if ($multipart) {
				$uniqid = self::getuniqid();
				$request.='Content-Type: multipart/form-data; boundary='.$uniqid.$r;
				$urlencoded = '';
				foreach ($post as $key=>$val) {
					$urlencoded.='--'.$uniqid.$r.'Content-Disposition: form-data; name="'.$key.'"'.$r.$r.$val.$r;
				}
				$urlencoded.='--'.$uniqid.'--';
			} else {
				$request.='Content-Type: application/x-www-form-urlencoded'.$r;
				$urlencoded = array();
				foreach ($post as $key=>$val) {
					$urlencoded[] = $key.'='.urlencode($val);
				}
				$urlencoded = implode('&', $urlencoded);
			}
			$request.='Content-Length: '.strlen($urlencoded).$r.$r;
			$request.=$urlencoded;

			fputs($q, $request);

			$sended = '';
			while (!feof($q)) {
				$sended.=fgets($q);
			}
			fclose($q);

			//$full_response = $sended;
			$headers_end_pos = strpos($sended, $r.$r);
			if ($headers_end_pos===false) {
				$response = $sended;
				$sended = '';
			} else {
				$response = substr($sended, 0, $headers_end_pos);
				$sended = substr($sended, $headers_end_pos+4);
			}
			if (!empty($debug)) {print "<pre>\nsendMailProxySocket()\n\nREQUEST:\n{$request}\n\nRESPONSE:\n{$response}\n</pre>";}

			return $sended;
		}
		if (!empty($debug)) {print "<pre>\nsendMailProxySocket()\nERRNO: {$errno}; ERRSTR: {$errstr}\n</pre>";}

		return '0';
	}

	public static function dirWalk($dir, $callback) { // 2017-08-21
		if (empty($dir) || !is_dir($dir)) {return false;}
		if (substr($dir, -1)!='/') {$dir.='/';}
		$descriptor = opendir($dir);
		while ($item=readdir($descriptor)) {
			if (in_array($item, array('.', '..'))) {continue;}
			$callback($dir.$item);
		}
		return true;
	}

	public static function sendEmailViaProxy($to, $subject, $message, $from='', $filename='', $noCURL=0, $debug=0) {
		if (empty($from)) {$from = 'no-reply@'.str_replace('www.', '', $_SERVER['SERVER_NAME']);}
		$uniqid = self::getuniqid();
		$security_token = md5($to.'-'.$_SERVER['HTTP_HOST'].'-M#_0^.8*');
		$daemon_url = 'http://example.com/common_secure_mail_proxy.php';
		$rn = "\r\n";

		$headers = 'From: '.$from.$rn;
		$headers.='Message-ID: <'.$uniqid.'@'.$_SERVER['SERVER_NAME'].'>'.$rn;
		$headers.='MIME-Version: 1.0'.$rn;
		$headers.='Content-Type: multipart/mixed;boundary="'.$uniqid.'"'.$rn.$rn;
		$headers.='--'.$uniqid."\n";
		$headers.='Content-type: text/html; charset=UTF-8'.$rn;
		$headers.='Content-transfer-encoding: base64'.$rn.$rn;

		$subject = '=?utf-8?B?'.base64_encode($subject).'?=';

		$message = chunk_split(base64_encode($message));
		if (is_file($filename)) {$message.="\n\n--{$uniqid}
Content-Type: application/octet-stream;name=\"".basename($filename)."\"
Content-Transfer-Encoding: base64
Content-Disposition: attachment;filename=\"".basename($filename)."\"\n
".chunk_split(base64_encode(file_get_contents($filename)))."\n";}
		$message.="\n\n--".$uniqid."--";

		$post = array(
			'security_token' => $security_token,
			'to' => $to,
			'subject' => $subject,
			'message' => $message,
			'headers' => $headers
		);
		if (!empty($debug)) {$post['DEBUG'] = '1';}

		$sended = false;
		if (function_exists('curl_init') && empty($noCURL)) {
			$sended = self::sendMailProxyCURL($daemon_url, $post, $debug);
		} else {
			$sended = self::sendMailProxySocket($daemon_url, $post, $debug);
		}

		return $sended;
	}

	/* filesystem */

	public static function file2base64($fname) { // 2017-05-01
		if (!is_file($fname)) {return false;}

		$mime = mime_content_type($fname);
		$content = base64_encode(file_get_contents($fname));

		return "data:$mime;base64,$content";
	}

	public static function mime2ext($mime) { // 2017-05-03
		$rel = [
			'application/pdf'   => 'pdf',
			'application/zip'   => 'zip',

			'image/gif'         => 'gif',
			'image/jpeg'        => 'jpg',
			'image/png'         => 'png',

			'text/css'          => 'css',
			'text/html'         => 'html',
			'text/javascript'   => 'js',
			'application/json'  => 'json',
			'text/plain'        => 'txt',
			'text/xml'          => 'xml',

			'application/msword' => 'doc',
			'application/vnd.openxmlformats-officedocument.wordprocessingml.document' => 'docx',
			'application/vnd.openxmlformats-officedocument.wordprocessingml.template' => 'dotx',
			'application/vnd.ms-word.document.macroEnabled.12' => 'docm',
			'application/vnd.ms-word.template.macroEnabled.12' => 'dotm',
			'application/vnd.ms-excel' => 'xls',
			'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' => 'xlsx',
			'application/vnd.openxmlformats-officedocument.spreadsheetml.template' => 'xltx',
			'application/vnd.ms-excel.sheet.macroEnabled.12' => 'xlsm',
			'application/vnd.ms-excel.template.macroEnabled.12' => 'xltm',
			'application/vnd.ms-excel.addin.macroEnabled.12' => 'xlam',
			'application/vnd.ms-excel.sheet.binary.macroEnabled.12' => 'xlsb',
			'application/vnd.ms-powerpoint' => 'ppt',
			'application/vnd.openxmlformats-officedocument.presentationml.presentation' => 'pptx',
			'application/vnd.openxmlformats-officedocument.presentationml.template' => 'potx',
			'application/vnd.openxmlformats-officedocument.presentationml.slideshow' => 'ppsx',
			'application/vnd.ms-powerpoint.addin.macroEnabled.12' => 'ppam',
			'application/vnd.ms-powerpoint.presentation.macroEnabled.12' => 'pptm',
			'application/vnd.ms-powerpoint.template.macroEnabled.12' => 'potm',
			'application/vnd.ms-powerpoint.slideshow.macroEnabled.12' => 'ppsm',
			'application/vnd.ms-access' => 'mdb',

			'video/x-flv' => 'flv',
			'video/mp4' => 'mp4',
			'application/x-mpegURL' => 'm3u8',
			'video/MP2T' => 'ts',
			'video/3gpp' => '3gp',
			'video/quicktime' => 'mov',
			'video/x-msvideo' => 'avi',
			'video/x-ms-wmv' => 'wmv'
		];

		if (isset($rel[$mime])) {return $rel[$mime];}

		$pieces = explode('/', $mime);

		return array_pop($pieces);
	}

	public static function base64_to_file($str, $dir, $fname=0) { // 2017-05-03
		$photo_parts = explode(';base64,', $str);
		$mime = substr($photo_parts[0], 5);
		$photo = base64_decode($photo_parts[1]);

		$dir =  self::dirCanonicalPath($dir);
		if (!is_dir($dir)) {mkdir($dir, 0777, true);}
		if (empty($fname)) {$fname = self::getuniqid().'.'.self::mime2ext($mime);}
		$destination = $dir.$fname;

		$saved = file_put_contents($destination, $photo);

		if ($saved) {return $fname;}

		return false;
	}

	public static function getFileSize($file_size) {
		$kb = round(($file_size/1024), 0);
		return $kb;
	}

	public static function measureBites($size) {
		$size = (is_numeric($size)? floatval($size): 0);
		$measures = ['B', 'KB', 'MB', 'GB', 'TB'];
		for ($i=0; (($size>=1024) && isset($measures[$i])); $i++) {
			$size/=1024;
		}
		return [
			'value' => number_format($size, 2, '.', ' '),
			'measure' => $measures[$i]
		];
	}

	public static function getFileSizeFormatted($fname) {
		if (is_file($fname)) {
			$size = @filesize($fname);
			return self::measureBites($size);
		}
		return [
			'value' => '0',
			'measure' => 'B'
		];
	}

	public static function iniGetBytes($val) {
		$val = trim(ini_get($val));
		$last = '';
		if ($val!='') {
			$last = strtolower(
				$val{strlen($val) - 1}
			);
		}
		switch ($last) {
			// The 'G' modifier is available since PHP 5.1.0
			case 'g':
				$val*=1024;
			case 'm':
				$val*=1024;
			case 'k':
				$val*=1024;
		}

		return $val;
	}

	public static function isPostOverflow() {
		$maxPostSize = self::iniGetBytes('post_max_size');
		return ((@$_SERVER['CONTENT_LENGTH']>$maxPostSize) && ($_SERVER['REQUEST_METHOD']=='POST'));
	}

	public static function getFileExt($fname, &$fname_stripped=false) { // deprecated. use pathinfo() instead
		if (empty($fname)) {return '';}
		$fname_start_pos = strrpos($fname, '/');
		if ($fname_start_pos!==false) {
			$fname = substr($fname, ($fname_start_pos+1));
			if (empty($fname)) {return '';}
		}
		$fname_splitted = explode('.', $fname);
		$fname_parts_count = count($fname_splitted);
		if ($fname_parts_count<2) {return '';}
		$fname_stripped = implode('.', array_slice($fname_splitted, 0, ($fname_parts_count-1)));
		return end($fname_splitted);
	}

	public static function dirCount($dir='.', $restrictions='ONLY_FILES') {
		$count = 0;
		if (empty($dir) || !is_dir($dir)) {return $count;}
		if (substr($dir, -1)!='/') {$dir.='/';}
		$check_extensions = (is_array($restrictions) && count($restrictions));
		$only_files = ($check_extensions || in_array($restrictions, array('ONLY_FILES', 'ONLYFILE', 'FILE')));
		$only_dirs = in_array($restrictions, array('ONLY_DIRECTORIES', 'ONLY_DIRS', 'ONLYDIR', 'DIR'));
		$descriptor = opendir($dir);
		while ($item=readdir($descriptor)) {
			if (in_array($item, array('.', '..'))) {continue;}
			$is_dir = is_dir($dir.$item);
			if ($only_files && $is_dir) {continue;}
			if ($only_files && $check_extensions) {
				$fname_parts = explode('.', $item);
				$ext = end($fname_parts);
				if (!in_array($ext, $restrictions)) {continue;}
			}
			if ($only_dirs && !$is_dir) {continue;}
			$count++;
		}
		return $count;
	}

	public static function dirCanonicalPath($dir) {
		if (empty($dir)) {return '.';}
		$path_arr = explode('/', $dir);
		$res_arr = array();
		$allow_parentlink = true;
		foreach ($path_arr as $step) {
			if (empty($step)) {continue;}
			if ($step=='.') {continue;}
			if ($step=='..') {
				if ($allow_parentlink) {
					$res_arr[] = $step;
				} else {
					if (count($res_arr) && (end($res_arr)!='..')) {
						array_pop($res_arr);
					} else {
						$res_arr[] = $step;
						$allow_parentlink = true;
					}
				}
			} else {
				$allow_parentlink = false;
				$res_arr[] = $step;
			}
		}
		$canonical_path = implode('/', $res_arr);
		if (empty($canonical_path)) {return '.';}
		if (substr($canonical_path, 0, 3)!='../') {$canonical_path = './'.$canonical_path;}
		if (substr($canonical_path, -1)!='/') {$canonical_path.='/';}
		return $canonical_path;
	}

	public static function dirCreate($dir, $chmod=0777) {
		if (is_dir($dir)) {return true;}
		$dir = self::dirCanonicalPath($dir);
		$destination = dirname($dir);
		$new_folder = basename($dir);
		if (!is_dir($destination)) {
			$created = self::dirCreate($destination);
			if (!$created) {return false;}
		}
		return mkdir($dir, $chmod);
	}

	public static function rename($old_dir,$dir,$file) {
		$oldfile = $old_dir.$file;
		$newfile = $dir.$file;
		if (!@opendir($dir)) {mkdir($dir, 0777, true);}
		if (copy($oldfile, $newfile)) {
			unlink($oldfile);
			return true;
		}
		return false;
	}

	public static function renameFolder($in_source, $source_dir, $in_dest, $dest_dir, $chmod='0755') {
		if (is_dir($in_source.'/'.$source_dir) || is_dir($in_dest.'/'.$dest_dir)) {
			if (!file_exists($in_dest.$dest_dir)) {
				$mk = self::makeFolder($in_dest, $dest_dir, $chmod);
//				echo $dest_dir;
				self::copydir($in_source.$source_dir, $in_dest.$dest_dir);
				self::deleteDir($in_source.'/'.$source_dir);
				return $dest_dir;
			} else {
				$dest_dir = $dest_dir.'_';
				return self::renameFolder ($in_source, $source_dir, $in_dest, $dest_dir, $chmod='0755');
			}
		}
	}

	public static function copydir($source, $dest) {
		if (is_dir($source) || is_dir($dest)) {
			if ($dh = opendir($source)) {
				while (($file = readdir($dh)) !== false) {
					if ($file != '.' && $file != '..') {
						if (is_file($source.'/'.$file)) {
							copy($source.'/'.$file, $dest.'/'.$file);
						} else {
							$folder = self::makeFolder($dest.'/', $file);
//    						echo "$source/$file, $dest/$folder";
							self::copydir($source.'/'.$file, $dest.'/'.$folder);
						}
					}
				}
				closedir($dh);
			}
		}
	}

	public static function copyFile($source, $dest='') { // 2016-09-12
		if (empty($dest)) {
			$dest = $source;
		}

		$source_pp = pathinfo($source);
		if (!is_file($source)) {
			//print $source.' file not found';
			return false;
		}

		$dest_pp = pathinfo($dest);
		$dest_dir = self::dirCanonicalPath($dest_pp['dirname']);
		if (!is_dir($dest_dir)) {
			$created = mkdir($dest_dir, 0777, true);
			if (!$created) {
				//print $dest_dir.' dir cannot create';
				return false;
			}
		}

		$dest_file = $dest_pp['basename'];
		if (is_file($dest)) {
			$uniqid = self::getuniqid();
			$max_filename_length = 255-(strlen($uniqid)+1)-(strlen($dest_pp['extension'])+1);
			$filename = substr($dest_pp['filename'], 0, $max_filename_length).'_'.$uniqid;
			$dest = $dest_dir.$filename.'.'.$dest_pp['extension'];
			$dest_file = $filename.'.'.$dest_pp['extension'];
		}
		if (copy($source, $dest)) {
			return $dest_file;
		}

		//print $source.' cannot copy to '.$dest;
		return false;
	}

	public static function upload($file, $source, $to, $valid_extensions=[]) { // 2017-05-03
		$to = self::dirCanonicalPath($to);
		if (!is_dir($to)) {mkdir($to, 0777, true);}

		$filename = pathinfo($file, PATHINFO_FILENAME);
		$ext = strtolower(pathinfo($file, PATHINFO_EXTENSION));
		if ((!in_array($ext, $valid_extensions) && !empty($valid_extensions)) || empty($filename)) {return false;}

		$filename = self::translit($filename, true, true);

		if (file_exists($to.$filename.'.'.$ext)) {
			//$filename.='_'.self::getuniqid();
			$uniqid = self::getuniqid();
			$max_filename_length = 255-(strlen($uniqid)+1)-(strlen($ext)+1);
			$filename = substr($filename, 0, $max_filename_length).'_'.$uniqid;
		}
		$file = $filename.'.'.$ext;
		if (move_uploaded_file($source, $to.$file)) {
			return $file;
		}

		return false;
	}

	public function translitSMS($str) {
		$fr = array('ü', 'Ü', 'ö', 'Ö', 'ğ', 'Ğ', 'İ', 'ı', 'Ç', 'ç', 'Ş', 'ş', 'Ə', 'ə', '«', '»', '/', '\\', '|', ':', '*', '?', '"', '<', '>', '&', '%', '#', '№', '$');
		$to = array('u', 'U', 'o', 'O', 'g', 'G', 'I', 'i', 'C', 'c', 'S', 's', 'A', 'e', '',  '',  '',  '',   '',  '-', '',  '',  '',  '',  '',  '',  '',  'N', 'N', ' USD');
		$str = strtr($str, array_combine($fr, $to));

		return $str;
	}

	public static function translit($str, $full=false, $filename=false) {
		$fr = array('ü', 'Ü', 'ö', 'Ö', 'ğ', 'Ğ', 'İ', 'ı', 'Ç', 'ç', 'Ş', 'ş', 'Ə', 'ə');
		$to = array('u', 'U', 'o', 'O', 'g', 'G', 'I', 'i', 'Ch', 'ch', 'Sh', 'sh', 'A', 'e');
		if ($full) {
			$fr = array_merge($fr, array('ё', 'Ё', 'й', 'Й', 'ц', 'Ц', 'у', 'У', 'к', 'К', 'е', 'Е', 'н', 'Н', 'г', 'Г', 'ш', 'Ш', 'щ', 'Щ', 'з', 'З', 'х', 'Х', 'ъ', 'Ъ', 'ф', 'Ф', 'ы', 'Ы', 'в', 'В', 'а', 'А', 'п', 'П', 'р', 'Р', 'о', 'О', 'л', 'Л', 'д', 'Д', 'ж', 'Ж', 'э', 'Э', 'я', 'Я', 'ч', 'Ч', 'с', 'С', 'м', 'М', 'и', 'И', 'т', 'Т', 'ь', 'Ь', 'б', 'Б', 'ю', 'Ю', '№', '«', '»'));
			$to = array_merge($to, array('jo', 'Jo', 'j', 'J', 'ts', 'Ts', 'u', 'U', 'k', 'K', 'e', 'E', 'n', 'N', 'q', 'Q', 'sh', 'Sh', 'sch', 'Sch', 'z', 'Z', 'h', 'H', ($filename? '': '\''), ($filename? '': '\''), 'f', 'F', 'y', 'Y', 'v', 'V', 'a', 'A', 'p', 'P', 'r', 'R', 'o', 'O', 'l', 'L', 'd', 'D', 'g', 'G', 'e', 'E', 'ja', 'Ja', 'ch', 'Ch', 's', 'S', 'm', 'M', 'i', 'I', 't', 'T', ($filename? '': '`'), ($filename? '': '`'), 'b', 'B', 'ju', 'Ju', ($filename? 'n_': '#'), ($filename? '': '"'), ($filename? '': '"')));
		}
		if ($filename) {
			$fr = array_merge($fr, array(' ', '/', '\\', '|', ':', '*', '?', '"', '<', '>', '&', '%', '#', '№', '$'));
			$to = array_merge($to, array('_', '', '', '', '', '', '', '', '', '', '_and_', '_percents', 'n_', 'n_', '_usd'));
		}

		$str = strtr($str, array_combine($fr, $to));
		if ($filename) {$str = strtolower($str);}

		return $str;
	}

	public static function translit_kb_ru2az($str, $reverse=false) {
		$refs = array(
			'г' => 'q', 'Г' => 'Q',
			'ц' => 'ü', 'Ц' => 'Ü',
			'е' => 'e', 'Е' => 'E',
			'р' => 'r', 'Р' => 'R',
			'т' => 't', 'Т' => 'T',
			'й' => 'y', 'Й' => 'Y',
			'у' => 'u', 'У' => 'U',
			'и' => 'i', 'И' => 'İ',
			'о' => 'o', 'О' => 'O',
			'п' => 'p', 'П' => 'P',
			'ю' => 'ö', 'Ю' => 'Ö',
			'ь' => 'ğ', 'Ь' => 'Ğ',
			'а' => 'a', 'А' => 'A',
			'с' => 's', 'С' => 'S',
			'д' => 'd', 'Д' => 'D',
			'ф' => 'f', 'Ф' => 'F',
			'э' => 'g', 'Э' => 'G',
			'щ' => 'h', 'Щ' => 'H',
			'ъ' => 'j', 'Ъ' => 'J',
			'к' => 'k', 'К' => 'K',
			'л' => 'l', 'Л' => 'L',
			'ы' => 'ı', 'Ы' => 'I',
			'я' => 'ə', 'Я' => 'Ə',
			'з' => 'z', 'З' => 'Z',
			'х' => 'x', 'Х' => 'X',
			'ж' => 'c', 'Ж' => 'C',
			'в' => 'v', 'В' => 'V',
			'б' => 'b', 'Б' => 'B',
			'н' => 'n', 'Н' => 'N',
			'м' => 'm', 'М' => 'M',
			'ч' => 'ç', 'Ч' => 'Ç',
			'ш' => 'ş', 'Ш' => 'Ş'
		);
		if ($reverse) {
			$refs = array_flip($refs);
		}
		$str = strtr($str, $refs);

		return $str;
	}

	public static function makeTitleURI($title) {
		$uri = @mb_strtolower($title);
		$uri = strtr($uri, array(' ' => '-', '&' => 'and', '%' => 'percent', '$' => 'dollar', '#' => 'n', '^' => '', '(' => '', ')' => '', '[' => '', ']' => '', '{' => '', '}' => ''));
		return substr(self::translit($uri, 1, 1), 0, 127);
	}

	public static function deleteDir($directory) {
		if (!is_dir($directory)) {return false;}
		if (!is_writable($directory)) {return false;}

		$dir = @opendir($directory);
		while ($file=@readdir($dir)) {
			$fname = $directory.'/'.$file;
			if (is_file($fname)) {
				@unlink($fname);
			} else if (is_dir($fname) && ($file!='.') && ($file!='..')) {
				self::deleteDir($fname);
			}
		}
		@closedir($dir);
		@rmdir($directory);

		return true;
	}

	public static function sign($n) {
		return ($n > 0)? 1: (($n<0)? -1: 0);
	}

	public static function generateUrlHash($login, $salt, $pwd_hash) {
        return md5(md5($login.$salt).md5($pwd_hash));
	}

	public static function getYearsOld($birth_date) {
		$years_old = date('Y')-self::changeDateFormat('Y-m-d', 'Y', $birth_date);
		if (date('Y-m-d')<(date('Y').'-'.self::changeDateFormat('Y-m-d', 'm-d', $birth_date))) {
			$years_old = $years_old-1;
		}
		return $years_old;
	}

	public static function parseJsonRequest() { // 2017-06-30
		$request = file_get_contents('php://input');
		$json = json_decode($request, 1);

		return $json;
	}

	public static function bigint2string($bigint) { // 2017-07-06
		return sprintf('%.0f', $bigint);
	}
}