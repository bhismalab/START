<?php

namespace tb\start_cms\helpers;

if (!defined('_VALID_PHP')) die('Direct access to this location is not allowed.');

class utils {
	/*
		This class contains only independent functions (can use only self:: methods).
		If you need to store method that uses any non-standard library, better
		take a look to app\helpers\app class.

		Validation functions:
			(bool) checkPIN($pin)
			(bool) checkLogin($login)
			(bool) checkPass($pass)
			(bool) is_valid_email($email)
			(bool) validEmail($email)
			(bool) is_valid_URI_element($s)
			(bool) validURL($url)
			(bool) is_valid_human_name($name)
			(bool) is_valid_human_nsp($nsp)
			(bool) is_valid_phone($phone)
			(bool) valid_date($date)
			(bool) checkDate($format, $date)
			(bool) checkFieldName($name)
			(bool) checkIP($ip)
			(bool) variableHasValue($var)
		Converting:
			(string) bigint2string($bigint)
			(int) date2stamp($time)
			(string) stamp2date($timestamp)
			(string) formatPlainDate($format, $date)
			(int) parseMySQLDate($date)
			(int) parseXLSXDate($date)
			(string) formatXLSXDate($format, $date)
			(string) changeDateFormat($fromFormat, $toFormat, $date)
			(int) getYearsOld($birth_date)
			(array/string) getAge($birth_date, $stringify=true)
		HTML:
			(string) safeEcho($str, $return=false)
			(string) safeJsEcho($str, $return=false)
			(string) safePostValue($key, $default='', $escape_default=false)
			(string) safeCleanMarkupEcho($str, $return=false, $limit=1000)
			(string) html2text($str)
			(string) unescapeTagInnerHTML($html, $tag='noscript')
			(string) strClear($str, $spch=0, $itag=0, $trim=1, $snqq=0, $skp=0)
			(string) prep_str($str)
			(string) stripNonFormatTags($str, $inform=true, $extended=true)
			(string) array2form($arr, $inp_pattern="<input type=\"hidden\" name=\"{name}\" value=\"{value}\" />\n", $NOT_NULL=true)
		Texts & Strings:
			(string) strCollapseSpaces($str)
			(string) limitWords($str, $limit, $hellip=1)
			(string) limitStringLength($str, $limit, $hellip=1)
			(string) getRussianWordEndingByNumber($n, $s='', $d='а', $m='ов')
			(string) getAzerbaijanianWordEndingByNumber($n, $n12578='i', $n34='ü', $n6='ı', $n9='u', $n0='i')
			(string) getOnlyWords($str)
			(string) sanitizeStringByWhitelist($string, $whitelist, $escape=0)
			(string) makeSearchable($str)
			(string) makeSEF($s)
			(string) translitSMS($str)
			(string) translit($str, $full=false, $filename=false)
			(string) translit_kb_ru2az($str, $reverse=false)
			(string) az_upper($str)
			(string) makeTitleURI($title)
		URL & HTTP:
			(bool) isAjax()
			redirect($url)
			delayedRedirect($url, $delay=2000)
			goLoc($getParams=array())
			goLocWith($getParams=array())
			(string) trueLink($allowed_keys)
			(string) array2url($array, $variable='')
			(array) parseURL($url)
		Arrays:
			(bool) isEmptyArrayRecursive($arr, $zerosAreValue=1)
			(array) array_filter_keys($arr, $keys)
			(array) array_exclude_keys($arr, $keys)
			(array) array_divide($array, $parts=1)
			(array) array_attributes_to_columns($attributes_list)
			(array) array_columns_to_attributes($columns_list)
			(array) arraySortByCol($arr, $col)
		Files:
			(string) file2base64($fname)
			(float) getFileSize($file_size)
			(array) measureBites($size)
			(string) getFileSizeFormatted($fname)
			(int) iniGetBytes($val)
			(bool) isPostOverflow()
			(string) getFileExt($fname, &$fname_stripped=false)
			(int) dirCount($dir='.', $restrictions='ONLY_FILES')
			(string) dirCanonicalPath($dir)
			dirCreate($dir, $chmod=0777)
			deleteDir($directory)
			rename($old_dir, $dir, $file)
			renameFolder($in_source, $source_dir, $in_dest, $dest_dir, $chmod='0755')
			copyDir($source, $dest)
			(string) copyFile($source, $dest='')
			(string) upload($file, $source, $to, $valid_extensions)
		misc:
			(string) getuniqid()
	*/

	/* validation */

	public static function checkPIN($pin) {
		return preg_match('/^[0-9A-Z]{7}$/i', $pin);
	}

	public static function checkLogin($login) {
		return preg_match('/^[0-9a-z\@\.\_\-]{3,32}$/i', $login);
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

	public static function bigint2string($bigint) { // 2017-07-06
		return sprintf('%.0f', $bigint);
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

	public static function getYearsOld($birth_date) {
		$years_old = date('Y')-self::changeDateFormat('Y-m-d', 'Y', $birth_date);
		if (date('Y-m-d')<(date('Y').'-'.self::changeDateFormat('Y-m-d', 'm-d', $birth_date))) {
			$years_old = $years_old-1;
		}

		return $years_old;
	}

	public static function getAge($birth_date, $stringify=true) { // 2017-05-01
		$y = self::getYearsOld($birth_date);

		$bDay = new \DateTime($birth_date);
		$now = new \DateTime();
		$interval = $bDay->diff($now, true);
		$m = $interval->format('%m');

		if ($stringify) {
			return "{$y}y".(($m>0)? " {$m}m": '');
		}

		return ['y' => $y, 'm' => $m];
	}

	public static function checkFieldName($name) {
		return @preg_match( '/^[0-9a-z\_]{2,32}$/i', $name);
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

	public static function safeEcho($str, $return=false) { // 2016-07-28
		$str = (is_scalar($str)? htmlspecialchars($str): '');
		$str = strtr($str, [
			'javascript:' => '',
			'data:' => ''
		]);
		if (!$return) {print $str; return 1;}
		return $str;
	}

	public static function safeJsEcho($str, $return=false) { // 2016-07-28
		$str = (is_scalar($str)? addslashes($str): '');
		$str = strtr($str, [
			"\n" => "\\\n",
			'</script>' => "</scr'+'ipt>"
		]);

		if (!$return) {
			print $str;
			return 1;
		}

		return $str;
	}

	public static function safePostValue($key, $default='', $escape_default=false) {
		if (isset($_POST[$key])) {
			return self::safeEcho($_POST[$key], 1);
		}
		if ($escape_default) {$default = htmlspecialchars($default, ENT_COMPAT, 'UTF-8');}
		return $default;
	}

	public static function safeCleanMarkupEcho($str, $return=false, $limit=1000) { // 2016-01-02
		// clean HTML markup, but preserve line break tags
		$str = trim(@(string)$str);
		$str = str_replace(["\r\n", "\n", "\r"], '', $str);
		$str = strtr($str, [
			'</p>' => "</p>\n",
			'<br>' => "<br>\n",
			'<br/>' => "<br/>\n",
			'<br />' => "<br />\n",
		]);
		$str = nl2br(utils::safeEcho(utils::limitStringLength(html_entity_decode(strip_tags($str)), $limit), 1));
		if (!$return) {print $str; return 1;}

		return $str;
	}

	public static function html2text($str) { // 2017-03-04
		// clean HTML markup, but preserve newlines
		$str = trim(@(string)$str);
		$str = str_replace(["\r\n", "\n", "\r"], '', $str);
		$str = strtr($str, [
			'</p>' => "</p>\n",
			'<br>' => "<br>\n",
			'<br/>' => "<br/>\n",
			'<br />' => "<br />\n",
		]);
		$str = html_entity_decode(strip_tags($str));

		return $str;
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

	public static function strCollapseSpaces($str) { // 2016-10-11
		return trim(preg_replace('/[  \t\n\r]+/u', ' ', $str));
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

	public static function limitWords($str, $limit, $hellip=1) {
		$words = explode(' ', $str);

		return implode(' ', array_slice($words, 0, $limit)).(($hellip && (count($words)>$limit))? '&hellip;': '');
	}

	public static function limitStringLength($str, $limit, $hellip=1) {
		$res = mb_substr($str, 0, $limit, 'UTF-8');
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

	public static function getOnlyWords($str) { // 2017-04-06
		preg_match_all('/([a-zA-Z]|\xC3[\x80-\x96\x98-\xB6\xB8-\xBF]|\xC5[\x92\x93\xA0\xA1\xB8\xBD\xBE]){4,}/', $str, $match_arr);

		return implode(' ', $match_arr[0]);
	}

	public static function sanitizeStringByWhitelist($string, $whitelist, $escape=0) {
		if ($escape) {$whitelist = preg_quote($whitelist);}

		return preg_replace("/[^".$whitelist."]?(.*?)[^".$whitelist."]?/usD", '$1', $string);
	}

	public static function makeSearchable($str) { // 2016-07-29
		$str = strip_tags(@(string)$str);
		$str = mb_strtolower($str, 'UTF-8');
		$str = str_replace(array("\t", "\r\n", "  "), ' ', $str);
		$str = str_replace(array('"', '&nbsp;', '&amp;', '&quot;', "'", "<", ">", "  "), ' ', $str);
		$str = trim($str);
		$str = self::strClear($str, 1);

		return $str;
	}

	public static function makeSEF($s) {
		$s = (string)$s;
		$s = mb_strtolower(trim($s));
		$s = preg_replace('/\s+/u', '-', $s);
		$s = self::translit($s, 1, 1);
		//$s = strtr($s, ['(' => '', ')' => '', '“' => '', '”' => '', '—' => '', ',' => '']);
		$s = self::sanitizeStringByWhitelist($s, '0-9a-z\-');
		$s = preg_replace('/\-+/u', '-', $s);
		$s = (string)substr($s, 0, 80);
		$s = trim($s, '-');

		return $s;
	}

	/* url */

	public static function isAjax() { // 2017-04-06
		return (!empty($_SERVER['HTTP_X_REQUESTED_WITH']) && (strtolower($_SERVER['HTTP_X_REQUESTED_WITH'])=='xmlhttprequest'));
	}

	public static function redirect($url) {
		if (headers_sent()) {
			print "<script type=\"text/javascript\">
				location.href = '".self::safeJSEcho($url, 1)."';
			</script>";
		} else {
			header('HTTP/1.1 301 Moved Permanently');
			header('Location: '.trim($url));
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

	public static function trueLink($allowed_keys) { // 2016-11-24
		foreach ($_GET as $key=>$val) {
			if (!in_array($key, $allowed_keys)) {continue;}

			if (is_array($val)) {
				$link_arr[] = self::array2url($val, $key);
			} else {
				$link_arr[] = $key.'='.urlencode($val);
			}
		}

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

	public static function array_filter_keys($arr, $keys) { // 2016-08-23
		$keys_num = count($keys);
		if (!$keys_num || !is_array($arr) || !is_array($keys)) {return array();}
		$res = [];
		foreach ($keys as $k) {
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

	/* filesystem */

	public static function file2base64($fname) { // 2017-05-01
		if (!is_file($fname)) {return false;}

		$mime = mime_content_type($fname);
		$content = base64_encode(file_get_contents($fname));

		return "data:$mime;base64,$content";
	}

	public static function getFileSize($file_size) {
		$kb = round(($file_size/1024),0);
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
		//if (substr($canonical_path, 0, 3)!='../') {$canonical_path = './'.$canonical_path;}
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
				self::copyDir($in_source.$source_dir, $in_dest.$dest_dir);
				self::deleteDir($in_source.'/'.$source_dir);
				return $dest_dir;
			} else {
				$dest_dir = $dest_dir.'_';
				return self::renameFolder ($in_source, $source_dir, $in_dest, $dest_dir, $chmod='0755');
			}
		}
	}

	public static function copyDir($source, $dest) {
		if (is_dir($source) || is_dir($dest)) {
			if ($dh = opendir($source)) {
    			while (($file = readdir($dh)) !== false) {
    				if ($file != '.' && $file != '..') {
    					if (is_file($source.'/'.$file)) {
        					copy($source.'/'.$file, $dest.'/'.$file);
    					} else {
    						$folder = self::makeFolder($dest.'/', $file);
//    						echo "$source/$file, $dest/$folder";
    						self::copyDir($source.'/'.$file, $dest.'/'.$folder);
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

	public static function upload($file, $source, $to, $valid_extensions) {
		$to = self::dirCanonicalPath($to);
		if (!is_dir($to)) {mkdir($to, 0777, true);}

		$filename = pathinfo($file, PATHINFO_FILENAME);
		$ext = strtolower(pathinfo($file, PATHINFO_EXTENSION));
		if (!in_array($ext, $valid_extensions) || empty($filename)) {return false;}

		$filename = self::translit($filename, true, true);

		while (file_exists($to.$filename.'.'.$ext)) {
			$uniqid = substr(self::getuniqid(), -3);
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

	public static function translit_kb_ru2az($str, $reverse=false) { // 2017-01-25
		$refs = [
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
			'ъ' => 'c', 'Ъ' => 'C',
			'к' => 'k', 'К' => 'K',
			'л' => 'l', 'Л' => 'L',
			'ы' => 'ı', 'Ы' => 'I',
			'я' => 'ə', 'Я' => 'Ə',
			'з' => 'z', 'З' => 'Z',
			'х' => 'x', 'Х' => 'X',
			'ж' => 'j', 'Ж' => 'J',
			'в' => 'v', 'В' => 'V',
			'б' => 'b', 'Б' => 'B',
			'н' => 'n', 'Н' => 'N',
			'м' => 'm', 'М' => 'M',
			'ч' => 'ç', 'Ч' => 'Ç',
			'ш' => 'ş', 'Ш' => 'Ş'
		];
		if ($reverse) {
			$refs = array_flip($refs);
		}
		$str = strtr($str, $refs);

		return $str;
	}

	public static function az_upper($str) { // 2017-01-24
		$str = strtr($str, [
			'i' => 'İ',
			'ı' => 'I'
		]);
		$str = mb_convert_case($str, MB_CASE_UPPER, "UTF-8");

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
}

?>