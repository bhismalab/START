<!DOCTYPE html>
<html lang="en">
	<head>
		<meta charset="utf-8" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge" />
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
		<meta name="csrf-token" content="4dd89f6875e4c85ab96038addc42666d" />

		<link rel="icon" href="/favicon.ico" type="image/x-icon" />
		<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon" />

		<title>START CMS - 500 memory overflow error</title>

		<link rel="stylesheet" href="//35.195.78.2/start_cms/web/css/bootstrap-3.3.7/bootstrap.min.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="//35.195.78.2/start_cms/web/css/font-awesome-4.7.0/font-awesome.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="//35.195.78.2/start_cms/web/css/admin-lte-2.3.7/AdminLTE.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="//35.195.78.2/start_cms/web/css/admin-lte-2.3.7/skins/skin-green.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="//35.195.78.2/start_cms/web/css/start-skin.css" type="text/css" media="screen" />

		<script type="text/javascript" src="//35.195.78.2/start_cms/web/js/jquery-2.2.3.min.js"></script>
		<script type="text/javascript" src="//35.195.78.2/start_cms/web/js/bootstrap-3.3.7/bootstrap.min.js"></script>
		<script type="text/javascript" src="//35.195.78.2/start_cms/web/js/fastclick.min.js"></script>
		<script type="text/javascript" src="//35.195.78.2/start_cms/web/js/jquery.slimscroll.min.js"></script>
		<script type="text/javascript" src="//35.195.78.2/start_cms/web/js/admin-lte-2.3.7/app.min.js"></script>
		<script type="text/javascript" src="//35.195.78.2/start_cms/web/js/utils.js"></script>
		<script type="text/javascript" src="//35.195.78.2/start_cms/web/js/custom-skin.js"></script>

		<!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
		<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
		<!--[if lt IE 9]>
		<script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
		<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
		<![endif]-->

	</head>

	
	<body class="hold-transition 500-page">
		<section class="content">
			<div class="error-page">
				<h2 class="headline text-red">500</h2>

				<div class="error-content">
					<h3><i class="fa fa-warning text-red"></i> Memory overflow.</h3>

					<p>
						Too many surveys selected. Please try again with less. <!-- <?= $errstr; ?> -->
					</p>
				</div>
			</div>
		</section>
	</body>
</html>