<?php

namespace app\helpers;

use tb\start_cms\CMS;
use tb\start_cms\helpers\smtp;
use tb\start_cms\helpers\utils;

if (!defined('_VALID_PHP')) die('Direct access to this location is not allowed.');

class app {
	/*
		This class is supposed to provide access to non-system pan-application functions.
		Any procedures that have dependencies that prevents from putting function into
		`tb\start_cms\helpers\utils` but need to be available globally in application can
		be placed here.
	*/

	public static $config = [
		'smtp' => [
			'protocol' => 'ssl://',
			'host' => 'smtp.gmail.com',
			'port' => '465',
			'user' => 'user@gmail.com',
			'password' => '************'
		]
	];

	// E-mail functions

	public static function sendEmail($to, $msg) {
		if (is_array($to)) {
			if (!isset($to['email'])) {return false;}
			$username = @$to['username'];
			$to = $to['email'];
		}

		$headers = "From: no-reply@start.az\r\n";
		$headers.="Bcc: start@gmail.com\r\n";
		$headers.="Content-type: text/html; charset=UTF-8\r\n";
		$headers.="Content-transfer-encoding: base64\r\n\r\n";
		if (!empty($username)) {
			$to = '=?utf-8?B?'.base64_encode($username).'?= <'.$to.'>';
		}
		$subj = '=?utf-8?B?'.base64_encode($msg['subject']).'?=';
		$text = chunk_split(base64_encode($msg['message']));

		return mail($to, $subj, $text, $headers, '-fno-reply@start.com');
		//$smtp = new smtp(self::$config['smtp']['host'], self::$config['smtp']['port'], self::$config['smtp']['user'], self::$config['smtp']['password'], self::$config['smtp']['protocol']);
		//$sended = $smtp->send($to, $subj, $text, $headers);
		//print "<pre>".var_export($smtp->log, 1)."</pre>";

		//return $sended;
	}


	private static function sendMailBasic($to, $subject, $message, $from='', $filename='', $smtp_status='DENY') {
		if (!$from) {
			$from = 'no-reply@'.str_replace('www.', '', $_SERVER['HTTP_HOST']).'';
		}
		$uniqid = utils::getuniqid();
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

	private static function sendEmailViaProxy($to, $subject, $message, $from='', $filename='', $noCURL=0, $debug=0) {
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
}

?>