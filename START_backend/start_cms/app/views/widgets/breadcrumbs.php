<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

?>

<ol class="breadcrumb">
	<?php
		if (!empty($links) && is_array($links)) {
			foreach ($links as $link) {
	?>
	<li<?=(empty($link['href'])? ' class="active"': '');?>>
		<?=(empty($link['href'])? '': ('<a href="'.utils::safeEcho($link['href'], 1).'">'));?>
			<?=(empty($link['icon'])? '': ('<i class="fa fa-'.$link['icon'].'"></i>'));?> 
			<?=utils::safeEcho($link['title'], 1);?>
		<?=(empty($link['href'])? '': '</a>');?>
	</li>
	<?php
			}
		}
	?>
</ol>