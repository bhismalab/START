<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

?>


<?php

// load resourses

// load Bootstrap Datepicker
view::appendCss(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/css/bootstrap-datepicker3.css');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/js/bootstrap-datepicker.min.js');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/locales/bootstrap-datepicker.'.$_SESSION[CMS::$sess_hash]['ses_adm_lang'].'.min.js');

// load Bootstrap Colorpicker
view::appendCss(SITE.CMS_DIR.JS_DIR.'colorpicker/css/bootstrap-colorpicker.css');
view::appendCss(SITE.CMS_DIR.CSS_DIR.'custom-colorpicker.css');
view::appendJs(SITE.CMS_DIR.JS_DIR.'colorpicker/js/bootstrap-colorpicker.min.js');

?>


<!-- Content Header (Page header) -->
<section class="content-header">
	<h1>
		<?=CMS::t('menu_item_social_workers_add');?>
		<!-- <small>Subtitile</small> -->
	</h1>
</section>


<!-- Main content -->
<section class="content">
	<div class="row">
		<div class="col-md-12">
			<nav class="top-toolbar-stripe">
				<a href="<?=utils::safeEcho($link_back, 1);?>" title="<?=CMS::t('back');?>" class="toolbar-btn-primary"><i class="fa fa-arrow-circle-o-left" aria-hidden="true"></i> Back</a>

				<?=view::widget('breadcrumb', ['links' => [
					/*[
						'title' => 'Users dashboard',
						'href' => '?controller=statistics&action=dashboard',
						'icon' => 'users'
					],*/ [
						'title' => 'Social workers',
						'href' => $link_back,
						'icon' => 'user-circle'
					], [
						'title' => 'Create new social worker',
						'icon' => 'plus-circle'
					]
				]]);?>
			</nav>
		</div>
	</div>

	<?php
		if (!empty($op)) {
			if ($op['success']) {
				print view::notice($op['message'], 'success');
			} else {
				print view::notice(empty($op['errors'])? $op['message']: $op['errors']);
			}
		}
	?>

	<!-- Info boxes -->

	<div class="box">
		<form action="" method="post" enctype="multipart/form-data" class="form-std" role="form" id="formUserInfo">
			<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />

			<div class="box-body">
				<div class="row">
					<div class="col-md-6">
						<div class="form-group">
							<label>Username</label>

							<input type="text" name="email" value="<?=utils::safeEcho(@$_POST['email'], 1);?>" class="form-control" autocomplete="off" />
						</div>
                        <div class="form-group">
                            <label><?=CMS::t('social_workers_name');?></label>

                            <input type="text" name="full_name" value="<?=utils::safeEcho(@$_POST['full_name'], 1);?>" class="form-control" autocomplete="off" />
                        </div>


						<!--<div class="form-group">
							<label><?/*=CMS::t('social_workers_gender');*/?></label>

							<select name="gender" class="form-control">
								<option value=""><?/*=CMS::t('social_workers_gender_placeholder');*/?></option>
								<option value="M"<?/*=((@$_POST['gender']=='M')? ' selected="selected"': '');*/?>><?/*=CMS::t('social_workers_gender_male');*/?></option>
								<option value="F"<?/*=((@$_POST['gender']=='F')? ' selected="selected"': '');*/?>><?/*=CMS::t('social_workers_gender_female');*/?></option>
							</select>
						</div>-->
					</div>

					<div class="col-md-6">
                        <div class="form-group">
                            <label><?=CMS::t('social_workers_pwd');?></label>

                            <div class="input-group">
                                <div class="input-group-btn">
                                    <a class="btn btn-danger" id="btnShowPassword"><i class="fa fa-eye" aria-hidden="true"></i></a>
                                </div>

                                <input type="password" name="pwd" class="form-control" autocomplete="off" id="inpSetPassword" />

                                <div class="input-group-btn">
                                    <a class="btn btn-success" id="btnGeneratePassword"><i class="fa fa-asterisk" aria-hidden="true"></i> <?=CMS::t('social_workers_pwd_generate');?></a>
                                </div>
                            </div>

                            <script type="text/javascript">
                                // <![CDATA[

                                $('#btnGeneratePassword').on('click', function() {
                                    var pwd = utils.generatePassword();
                                    utils.alert(pwd, function() {
                                        $('#inpSetPassword').val(pwd);
                                    });
                                });

                                $('#btnShowPassword').on('click', function() {
                                    var el = $('#inpSetPassword').get(0);
                                    if (el.type=='password') {
                                        el.type = 'text';
                                        $('i', this).removeClass('fa-eye');
                                        $('i', this).addClass('fa-eye-slash');
                                        $(this).removeClass('btn-danger');
                                        $(this).addClass('btn-success');
                                    } else {
                                        el.type = 'password';
                                        $('i', this).removeClass('fa-eye-slash');
                                        $('i', this).addClass('fa-eye');
                                        $(this).removeClass('btn-success');
                                        $(this).addClass('btn-danger');
                                    }
                                });

                                // ]]>
                            </script>
                        </div>


						<!-- <div class="form-group">
							<label><?=CMS::t('social_workers_color');?></label>

							<div class="input-group colorpicker-component" id="componentPickColor">
								<span class="input-group-addon"><i></i></span>
								<input type="text" name="color" value="<?=utils::safeEcho(@$_POST['color'], 1);?>" class="form-control" id="inpPickColor" />
							</div>

							<script type="text/javascript">
// <![CDATA[

var array_filter_keys = function(arr, keys) {
	var res = {};

	$.each(arr, function(k, v) {
		if ($.inArray(k, keys)!=-1) {
			res[k] = v;
		}
	});

	return res;
};

var color_aliases = { // 140 predefined colors from the HTML Colors spec
    "aliceblue": "#f0f8ff",
    "antiquewhite": "#faebd7",
    "aqua": "#00ffff",
    "aquamarine": "#7fffd4",
    "azure": "#f0ffff",
    "beige": "#f5f5dc",
    "bisque": "#ffe4c4",
    "black": "#000000",
    "blanchedalmond": "#ffebcd",
    "blue": "#0000ff",
    "blueviolet": "#8a2be2",
    "brown": "#a52a2a",
    "burlywood": "#deb887",
    "cadetblue": "#5f9ea0",
    "chartreuse": "#7fff00",
    "chocolate": "#d2691e",
    "coral": "#ff7f50",
    "cornflowerblue": "#6495ed",
    "cornsilk": "#fff8dc",
    "crimson": "#dc143c",
    "cyan": "#00ffff",
    "darkblue": "#00008b",
    "darkcyan": "#008b8b",
    "darkgoldenrod": "#b8860b",
    "darkgray": "#a9a9a9",
    "darkgreen": "#006400",
    "darkkhaki": "#bdb76b",
    "darkmagenta": "#8b008b",
    "darkolivegreen": "#556b2f",
    "darkorange": "#ff8c00",
    "darkorchid": "#9932cc",
    "darkred": "#8b0000",
    "darksalmon": "#e9967a",
    "darkseagreen": "#8fbc8f",
    "darkslateblue": "#483d8b",
    "darkslategray": "#2f4f4f",
    "darkturquoise": "#00ced1",
    "darkviolet": "#9400d3",
    "deeppink": "#ff1493",
    "deepskyblue": "#00bfff",
    "dimgray": "#696969",
    "dodgerblue": "#1e90ff",
    "firebrick": "#b22222",
    "floralwhite": "#fffaf0",
    "forestgreen": "#228b22",
    "fuchsia": "#ff00ff",
    "gainsboro": "#dcdcdc",
    "ghostwhite": "#f8f8ff",
    "gold": "#ffd700",
    "goldenrod": "#daa520",
    "gray": "#808080",
    "green": "#008000",
    "greenyellow": "#adff2f",
    "honeydew": "#f0fff0",
    "hotpink": "#ff69b4",
    "indianred": "#cd5c5c",
    "indigo": "#4b0082",
    "ivory": "#fffff0",
    "khaki": "#f0e68c",
    "lavender": "#e6e6fa",
    "lavenderblush": "#fff0f5",
    "lawngreen": "#7cfc00",
    "lemonchiffon": "#fffacd",
    "lightblue": "#add8e6",
    "lightcoral": "#f08080",
    "lightcyan": "#e0ffff",
    "lightgoldenrodyellow": "#fafad2",
    "lightgrey": "#d3d3d3",
    "lightgreen": "#90ee90",
    "lightpink": "#ffb6c1",
    "lightsalmon": "#ffa07a",
    "lightseagreen": "#20b2aa",
    "lightskyblue": "#87cefa",
    "lightslategray": "#778899",
    "lightsteelblue": "#b0c4de",
    "lightyellow": "#ffffe0",
    "lime": "#00ff00",
    "limegreen": "#32cd32",
    "linen": "#faf0e6",
    "magenta": "#ff00ff",
    "maroon": "#800000",
    "mediumaquamarine": "#66cdaa",
    "mediumblue": "#0000cd",
    "mediumorchid": "#ba55d3",
    "mediumpurple": "#9370d8",
    "mediumseagreen": "#3cb371",
    "mediumslateblue": "#7b68ee",
    "mediumspringgreen": "#00fa9a",
    "mediumturquoise": "#48d1cc",
    "mediumvioletred": "#c71585",
    "midnightblue": "#191970",
    "mintcream": "#f5fffa",
    "mistyrose": "#ffe4e1",
    "moccasin": "#ffe4b5",
    "navajowhite": "#ffdead",
    "navy": "#000080",
    "oldlace": "#fdf5e6",
    "olive": "#808000",
    "olivedrab": "#6b8e23",
    "orange": "#ffa500",
    "orangered": "#ff4500",
    "orchid": "#da70d6",
    "palegoldenrod": "#eee8aa",
    "palegreen": "#98fb98",
    "paleturquoise": "#afeeee",
    "palevioletred": "#d87093",
    "papayawhip": "#ffefd5",
    "peachpuff": "#ffdab9",
    "peru": "#cd853f",
    "pink": "#ffc0cb",
    "plum": "#dda0dd",
    "powderblue": "#b0e0e6",
    "purple": "#800080",
    "red": "#ff0000",
    "rosybrown": "#bc8f8f",
    "royalblue": "#4169e1",
    "saddlebrown": "#8b4513",
    "salmon": "#fa8072",
    "sandybrown": "#f4a460",
    "seagreen": "#2e8b57",
    "seashell": "#fff5ee",
    "sienna": "#a0522d",
    "silver": "#c0c0c0",
    "skyblue": "#87ceeb",
    "slateblue": "#6a5acd",
    "slategray": "#708090",
    "snow": "#fffafa",
    "springgreen": "#00ff7f",
    "steelblue": "#4682b4",
    "tan": "#d2b48c",
    "teal": "#008080",
    "thistle": "#d8bfd8",
    "tomato": "#ff6347",
    "turquoise": "#40e0d0",
    "violet": "#ee82ee",
    "wheat": "#f5deb3",
    "white": "#ffffff",
    "whitesmoke": "#f5f5f5",
    "yellow": "#ffff00",
    "yellowgreen": "#9acd32",
    "transparent": "#transparent"
};
var colors = <?=json_encode($colors);?>;
var color_set = array_filter_keys(color_aliases, colors);

$('#componentPickColor').colorpicker({
	component: '.input-group-addon',
	format: 'alias',
	colorSelectors: color_set,
	template: '<div class="colorpicker dropdown-menu">'+
		'<div class="colorpicker-selectors"></div>'+
	'</div>',
	align: 'left'
});

$('#inpPickColor').on('click', function() {
	$('.input-group-addon', $(this).parent()).trigger('click');
});

// ]]>
							</script>
						</div> -->

						<div class="form-group">
							<label><?=CMS::t('social_workers_birth_date');?></label>

							<div class="input-group">
								<div class="input-group-addon"><i class="fa fa-calendar"></i></div>
								<input type="text" name="birth_date" value="<?=utils::safePostValue('birth_date', '', 1);?>" placeholder="" class="form-control datepicker" />
							</div>

							<script type="text/javascript">
// <![CDATA[

$(document).ready(function() {
	$('[name="birth_date"]').datepicker({
		autoclose: true,
		format: 'dd.mm.yyyy',
		clearBtn: true,
		language: '<?=utils::safeJsEcho($_SESSION[CMS::$sess_hash]['ses_adm_lang'], 1);?>',
		defaultViewDate: {year: '<?=(date('Y')-25);?>', month: <?=(date('n')-1);?>, day: <?=date('j');?>},
        orientation: 'bottom'
	});
});

// ]]>
							</script>
						</div>
					</div>
				</div>
			</div>
			<!-- /.box-body -->

			<div class="box-footer">
				<button type="submit" name="add" value="1" class="btn btn-primary"><i class="fa fa-plus-circle" aria-hidden="true"></i> <?=CMS::t('add');?></button>
				<button type="reset" name="reset" value="1" class="btn btn-default"><i class="fa fa-refresh" aria-hidden="true"></i> <?=CMS::t('reset');?></button>
			</div>
		</form>
	</div>
	<!-- /.box -->

	<!-- /.info boxes -->
</section>
<!-- /.content -->