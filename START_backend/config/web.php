<?php

$params = require(__DIR__ . '/params.php');

$config = [
    'id' => 'basic',
    'basePath' => dirname(__DIR__),
	'timeZone' => 'Asia/Calcutta',
    'bootstrap' => ['log'],
    'components' => [
        'request' => [
            // !!! insert a secret key in the following (if it is empty) - this is required by cookie validation
            'cookieValidationKey' => '',
			'enableCsrfValidation' => false,
			/*'parsers' => [
				'application/json' => 'yii\web\JsonParser',
			]*/
        ],
        'cache' => [
            'class' => 'yii\caching\FileCache',
        ],
        'errorHandler' => [
            'errorAction' => 'api/error',
        ],
        'mailer' => [
            'class' => 'yii\swiftmailer\Mailer',
            // send all mails to a file by default. You have to set
            // 'useFileTransport' to false and configure a transport
            // for the mailer to send real emails.
            'useFileTransport' => true,
        ],
        'log' => [
            'traceLevel' => YII_DEBUG ? 3 : 0,
            'targets' => [
                [
                    'class' => 'yii\log\FileTarget',
                    'levels' => ['error', 'warning'],
                ],
            ],
        ],
        'db' => require(__DIR__ . '/db.php'),
        'urlManager' => [
            'enablePrettyUrl' => true,
            'showScriptName' => false,
            'rules' => [
				'<controller:[a-zA-Z\-\_\.]+>/<action:children>/<id:\d+>/<infotype:(surveys)>/' => '<controller>/children-<infotype>',
				'<controller:[a-zA-Z\-\_\.]+>/<action:[a-zA-Z\-\_\.]+>/<id:\d+>/<infotype:(attachments|results)>/<assestment_type:[a-z\_]+>' => '<controller>/survey-<infotype>',
				'<controller:[a-zA-Z\-\_\.]+>/<action:[a-zA-Z\-\_\.]+>/<id:\d+>/<infotype:(attachments|results)>/' => '<controller>/survey-<infotype>',
				'<controller:[a-zA-Z\-\_\.]+>/<action:[a-zA-Z\-\_\.]+>/<sef:[a-zA-Z\-\_\.]+>/' => '<controller>/<action>',
				'<controller:[a-zA-Z\-\_\.]+>/<action:[a-zA-Z\-\_\.]+>/<id:\d+>/' => '<controller>/<action>',
				'<controller:[a-zA-Z\-\_\.]+>/<action:[a-zA-Z\-\_\.]+>/' => '<controller>/<action>',
            ],
        ],
    ],
    'params' => $params,
];

if (YII_ENV_DEV) {
    // configuration adjustments for 'dev' environment
    $config['bootstrap'][] = 'debug';
    $config['modules']['debug'] = [
        'class' => 'yii\debug\Module',
        // uncomment the following to add your IP if you are not connecting from localhost.
        //'allowedIPs' => ['127.0.0.1', '::1'],
    ];

    $config['bootstrap'][] = 'gii';
    $config['modules']['gii'] = [
        'class' => 'yii\gii\Module',
        // uncomment the following to add your IP if you are not connecting from localhost.
        //'allowedIPs' => ['127.0.0.1', '::1'],
    ];
}

return $config;
