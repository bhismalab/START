<?php

use tb\start_cms\db_adapters\mysql_pdo_encrypted;
use tb\start_cms\helpers\utils;


date_default_timezone_set('Asia/Calcutta');
define('_VALID_PHP', true);

include '../start_cms/app/config/db.php';
include '../start_cms/vendor/tb/start_cms/db_adapters/mysql_pdo.php';
include '../start_cms/vendor/tb/start_cms/db_adapters/mysql_pdo_encrypted.php';
include '../start_cms/vendor/tb/start_cms/helpers/utils.php';

$encryption_key = 't4-h8_hv5^f';
$aes_expression = 'AES_ENCRYPT(%s, UNHEX(SHA2(%s, 512)))';

$db = new mysql_pdo_encrypted([
	'host' => DB_HOST,
	'name' => DB_NAME,
	'user' => DB_USER,
	'password' => DB_PASSWORD,
	'charset' => DB_CHARSET,
	'encryption_key' => $encryption_key
]);

$encrypted_fields = ['name', 'surname', 'patronymic', 'state', 'address', 'gender', 'birth_date'];
$data = [
	[
		'name' => 'मकई',
		'surname' => 'खूबानी',
		'patronymic' => 'बेर',
		'state' => 'Manipur',
		'address' => 'Imphal, Haokip Veng, 13/24',
		'gender' => 'M',
		'birth_date' => '2009-07-13'
	],
	[
		'name' => 'გარგარი',
		'surname' => 'სიმინდის',
		'patronymic' => 'ქლიავი',
		'state' => 'Maharashtra',
		'address' => 'Pune, Hadapsar, 47/32',
		'gender' => 'F',
		'birth_date' => '2011-12-17'
	],
	[
		'name' => 'Əftandil',
		'surname' => 'Qaragözlu',
		'patronymic' => 'Aydın oğlu',
		'state' => 'Karnataka',
		'address' => 'Davangere, Nijalingappa layout, 22/18',
		'gender' => 'M',
		'birth_date' => '2007-03-28'
	],
	[
		'name' => 'Семён',
		'surname' => 'Горбуньков',
		'patronymic' => 'Семёнович',
		'state' => 'Andxra Pradesh',
		'address' => 'Khajipeta, Sri Naga Nadeswar',
		'gender' => 'F',
		'birth_date' => '2013-01-15'
	]
];

/*foreach ($data as $row) {
	$row['add_by'] = '3';
	$row['add_datetime'] = date('Y-m-d H:i:s');
	$db->add('children', [
		'encrypted_fields' => $encrypted_fields,
		'data' => $row
	]);
}*/

/*$data = $db->getAll("SELECT id, AES_DECRYPT(surname, UNHEX(SHA2(:crypt_key, 512))) AS plain_surname,
		AES_DECRYPT(name, UNHEX(SHA2(:crypt_key, 512))) AS plain_name, AES_DECRYPT(patronymic, UNHEX(SHA2(:crypt_key, 512))) AS plain_patronymic
	FROM crypt_children
	WHERE 1
	ORDER BY plain_surname, plain_name, plain_patronymic",
	[
		':crypt_key' => $encryption_key
	]
);*/
$data = $db->get([
	'fields' => ['id', 'surname', 'name', 'patronymic', 'add_datetime'],
	'encrypted_fields' => $encrypted_fields,
	'from' => 'children',
	'where' => "is_deleted='0'",
	'order_by' => "surname, name, patronymic",
	'fetch_style' => 'all'
]);

print "<pre>".var_export($data, 1)."\n\n".var_export($db->queries, 1)."\n\n".var_export($db->errors, 1)."</pre>";

?>