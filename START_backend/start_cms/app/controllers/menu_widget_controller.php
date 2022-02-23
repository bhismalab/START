<?php

namespace app\controllers;

use app\helpers\app;
use tb\start_cms\base\widget;
use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

class menu_widget_controller extends widget {
	/* See tb\start_cms\helpers\view class for function widget($widget_name, $options=[]) information */

	private static $menu = [
		[
			'name' => 'menu_block_children',
			'icon' => 'home',
			'selected' => ['children/list', 'children/view_info', 'children/survey'],
			'url' => '?controller=children&action=list'
		],
		[
			'name' => 'menu_block_cms_users',
			'icon' => 'users',
			'selected' => ['cms_users/list', 'cms_users/add', 'cms_users/delete', 'cms_users/edit', 'social_workers/list', 'social_workers/add', 'social_workers/edit'],
			'url' => '?controller=cms_users&action=list',
		],
		[
			'name' => 'menu_block_exercises',
			'icon' => 'exercises',
			'selected' => ['exercises/list', 'exercises/add', 'exercises/edit', 'exercises/delete'],
			'url' => '?controller=exercises&action=list',
		],
		[
			'name' => 'menu_block_statistics',
			'icon' => 'stats',
			'selected' => ['statistics/overview'],
			'url' => '?controller=statistics&action=overview',
		],
		[
			'name' => 'menu_block_log',
			'icon' => 'log',
			'selected' => ['log/list'],
			'url' => '?controller=log&action=list',
		],
		[
			'name' => 'menu_block_logout',
			'icon' => 'logout',
			'selected' => ['base/sign_out'],
			'url' => '?controller=base&action=sign_out'
		],
	];

	private static function menu() { // 2017-04-06
		$privilegies = CMS::getPrivilegies();

		$html = '';
		$cur_page = @$_GET['controller'].'/'.@$_GET['action'];

		foreach (self::$menu as $i=>$block) {
			if (!empty($privilegies)) { // check privilegies
				$pages = $block['selected'];
				$allowed_pages = array_intersect($pages, array_keys($privilegies));
				if (empty($allowed_pages)) {continue;}
			}

			if (empty($block['selected'])) {$block['selected'] = array_keys(@$block['subs']);}
			if (empty($block['icon'])) {$block['icon'] = 'circle-o';}
			$html.='<li class="treeview'.(in_array($cur_page, $block['selected'])? ' active': '').'">
				<a href="'.(empty($block['url'])? '#': $block['url']).'" title="'.CMS::t($block['name']).'">
					<i class="custom-icon icon-'.$block['icon'].'"></i>
				</a>
			</li>';
		}

		return $html;
	}

	public static function run() {
		return self::menu();
	}
}

?>