<?php

namespace tb\start_cms\helpers;

if (!defined('_VALID_PHP')) die('Direct access to this location is not allowed.');

class smtp {
	public $log;

	private $protocol;
	private $host;
	private $port;
	private $user;
	private $password;
	private $connection;
	private $stack=array();

	function __construct($host='127.0.0.2', $port='25', $user='', $password='', $protocol='') {
		$this->protocol = $protocol;
		$this->host = $host;
		$this->port = $port;
		$this->user = $user;
		$this->password = $password;
		$this->log[] = "SMTP - initialized for ".($user? $user: 'anonimous')." at {$host}:{$port} [".$this->getmicrotime_formatted()."]";
	}

	private function getmicrotime() {
		return ceil(microtime(true)*1000);
	}

	private function getmicrotime_formatted() {
		list($msec, $sec) = explode(' ', microtime());
		return date('Y.d.m H:i:s:', $sec).str_pad(ceil($msec*1000), 3, '0', STR_PAD_LEFT);
	}

	private function connect() {
		$errno = 0;
		$errstr = '';
		$this->connection = @fsockopen($this->protocol.$this->host, $this->port, $errno, $errstr, '12');
		if (is_resource($this->connection)) {
			$this->log[] = "SMTP::connect() - Successfully connected to {$this->host}:{$this->port} [".$this->getmicrotime_formatted()."]";
			return true;
		} else {
			$this->log[] = "SMTP::connect() - Cannot connect to {$this->host}:{$this->port} - {$errno} {$errstr}";
			return false;
		}
	}

	private function disconnect() {
		fclose($this->connection);
		if (is_resource($this->connection)) {
			$this->log[] = 'SMTP::disconnect() - Cannot disconnect: no responce from server';
			unset($this->connection);
		} else {
			$this->log[] = 'SMTP::disconnect() - Disconnected ['.$this->getmicrotime_formatted().']';
		}
		return true;
	}

	private function run($command=false, $arg=false) {
		$msg = $command? ($command.(empty($arg)? '': (' '.$arg))."\r\n"): $arg;
		$this->stack = array();
		if (is_resource($this->connection)) {
			$writed = fwrite($this->connection, $msg);
			$this->log[] = 'SMTP::run() - '.($writed? ('Writed "'.$msg.'" ('.$writed.' bytes) ['.$this->getmicrotime_formatted().']'): ('Cannot send "'.$msg.'" string'));
			if ($command=='QUIT') {
				$this->stack[] = "221 {$this->host} Service closing transmission channel";
				return '221'; // Optimize performance, DO NOT wait server's answer
			}
			$code = '';
			while (!feof($this->connection)) {
				$answer = fgets($this->connection);
				if (empty($answer)) {break;}
				$this->log[] = 'SMTP::run() - Readed "'.$answer.'" ['.$this->getmicrotime_formatted().']';
				$code = substr($answer, 0, 3);
				$this->stack[] = $answer;
				//if (in_array($code, array('250', '354', '221'))) {break;}
				//if (in_array($code, array('354', '221'))) {break;}
				//if ($code[0]=='5') {break;}
				if (($answer[3]==' ') && ($code!='220')) {break;}
			}
			return $code;
		} else {
			$this->log[] = 'SMTP::run() - Command failure. Connection lost.';
			return false;
		}
	}

	private function extractParameterFromHeaders($param, $text) {
		$text = explode("\r\n\r\n", $text);
		$text = reset($text);
		$regexp = '/'.$param.'\:(.*)\r\n/iDu';
		$maches = array();
		$found = @preg_match_all($regexp, $text, $matches);
		$entries = array();
		if ($found && !empty($matches[1])) {
			foreach ($matches[1] as $values) {
				$values = explode(',', $values);
				foreach ($values as $value) {
					$value = trim($value);
					$value = explode(' ', $value);
					$value = end($value);
					if ($value) {
						if (($value[0]=='<') && (substr($value, -1)=='>')) {
							$value = substr($value, 1, -1);
							if (empty($value)) {continue;}
						}
						if ((strpos($value, '<')===false) && (strpos($value, '>')===false) && (strpos($value, '@')!==false)) {
							$entries[] = $value;
						}
					}
				}
			}
			$entries = array_unique($entries);
		}
		return $entries;
	}

	private function transfer($email) {
		$sended = false;
		if ($this->user && $this->password) {
			$code = $this->run('EHLO', $this->host);
			if ($code[0]=='2') {
				$handshake = 1;
				foreach ($this->stack as $line) {
					if (strpos($line, 'AUTH')) {
						if (strpos($line, 'LOGIN')) {
							$code = $this->run('AUTH LOGIN');
							if ($code=='334') {$code = $this->run(base64_encode($this->user));}
							if ($code=='334') {$code = $this->run(base64_encode($this->password));}
						} else if (strpos($line, 'PLAIN')) {
							$code = $this->run('AUTH PLAIN', base64_encode("\000{$this->user}\000{$this->password}"));
						}
						//break;
					}
				}
			}
		}
		if (empty($handshake)) {$code = $this->run('HELO', $this->host);}
		if (@$code[0]=='2') {
			$sender = $this->extractParameterFromHeaders('From', $email);
			if (empty($sender)) {
				$sender = array('no-reply@'.((substr($_SERVER['HTTP_HOST'], 0, 4)=='www.')? substr($_SERVER['HTTP_HOST'], 4): $_SERVER['HTTP_HOST']));
			}
			$recipient = $this->extractParameterFromHeaders('To', $email);
			if (empty($recipient)) {
				$this->log[] = 'SMTP::transfer() - No recipients found';
			} else {
				$sender = reset($sender);
				$code = $this->run('MAIL', 'FROM:<'.$sender.'>');
				if ($code=='250') {
					$code = $this->run('RCPT', 'TO:<'.array_shift($recipient).'>');
					if (!empty($recipient)) foreach ($recipient as $to) {
						$this->run('RCPT', 'TO:<'.$to.'>');
					}
					if ($code=='250') {
						$code = $this->run('DATA', false);
						if ($code=='354') {
							$email = str_replace("\r\n.", "\r\n..", $email);
							//$email = chunk_split($email);
							$code = $this->run(false, $email."\r\n.\r\n");
							if ($code=='250') {
								//$sended = substr($this->stack[0], (strrpos($this->stack[0], ' ')+1));
								$sended = true;
								$this->log[] = "SMTP::transfer() - Message was succesfully sent as {$sended} [".$this->getmicrotime_formatted().']';
							} else {
								$this->log[] = 'SMTP::transfer() - An error has occured while sending the message';
							}
						}
					}
				}
			}
		}
		$code = $this->run('QUIT');
		return $sended;
	}

	public function send($to, $subject='', $message='', $headers='') {
		@set_time_limit(120);
		$email = "To: {$to}\r\nSubject: {$subject}\r\n{$headers}{$message}";
		$this->log[] = "SMTP::send() - E-mail combined:\n{$email}";
		$connected = $this->connect();
		if (!$connected) {return false;}
		$sended = $this->transfer($email);
		$this->disconnect();
		return true;
	}
}
// UTF-8 forced [uёξ]

// 2014.08.22 v1.0.1 - AUTH LOGIN fixed, server response ending recognition fixed

// 2013.04.16 v1.0.0
?>