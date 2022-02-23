<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

// load datepicker
view::appendCss(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/css/bootstrap-datepicker3.css');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/js/bootstrap-datepicker.min.js');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/locales/bootstrap-datepicker.'.$_SESSION[CMS::$sess_hash]['ses_adm_lang'].'.min.js');

?>


<script type="text/javascript">
    // <![CDATA[
    $(document).ready(function() {
        $('#filter-button').on('click', function() {
            $.fancybox.open({
                href: '#popupFilter'
            });
        });

        $('#popupFilterClose').on('click', function() {
            $.fancybox.close();
            return false;
        });

        /*$('.aAjax').on('click', function() {
            var $el = $(this);
            var data = JSON.parse($(this).attr('data-ajax_post'));
            $.ajax({
                url: this.href,
                data: data,
                async: true,
                cache: false,
                type: 'post',
                dataType: 'json',
                success: function(response, status, xhr) {
                    if (response.success) {
                        if (response.data && response.data.action) {
                            var new_status = response.data.action;
                            var old_status = ((new_status=='on')? 'off': 'on');
                            $('i', $el).removeClass('fa-toggle-'+old_status+' btn-toggle-'+old_status).addClass('fa-toggle-'+new_status+' btn-toggle-'+new_status);
                            data.turn = old_status;
                            $el.attr('data-ajax_post', JSON.stringify(data));
                        }
                    }
                },
                error: function(xhr, err, descr) {}
            });

            return false;
        });*/
    });
    // ]]>
</script>


<style>
td {
	vertical-align: middle !important;
}
thead th {
	text-transform: uppercase;
}
</style>


<!-- Content Header (Page header) -->
<section class="content-header">
	<h1>
		<?=CMS::t('menu_item_log_list');?>
	</h1>

</section>

<!-- Main content -->
<section class="content">
	<div class="box">
        <div class="box-header with-border">
			<h3 class="box-title"><!-- Events log -->&nbsp;</h3>

			<?php if (CMS::hasAccessTo('log/download_log')) { 
				parse_str($_SERVER['QUERY_STRING'], $queryData);
				$downloadQueryString = array_merge($queryData, [
					'action' => 'download_log'
				]);
			?>
			<a href="?<?php echo http_build_query($downloadQueryString); ?>" class="btn btn-context btnDownloadLog" style="margin-left: -9px;"><i class="fa fa-download" aria-hidden="true"></i> Export to Excel</a>
			<?php } ?>

			<div class="box-tools pull-right col-sm-5 col-lg-6">
				<form action="" method="get" id="formSearchAndFilter" style="margin-top: 7px;">
					<input type="hidden" name="controller" value="<?=utils::safeEcho(@$_GET['controller'], 1);?>" />
					<input type="hidden" name="action" value="<?=utils::safeEcho(@$_GET['action'], 1);?>" />
					<input type="hidden" name="<?=time();?>" value="" />

					<div class="input-group has-feedback">
						<div class="input-group-btn">
							<button type="button" class="btn btn-<?=(utils::isEmptyArrayRecursive(@$_GET['filter'])? 'success': 'warning');?>" id="filter-button"><i class="fa fa-filter" aria-hidden="true"></i> <?=CMS::t('filter');?></button>
						</div>
						<input type="text" name="q" value="<?=utils::safeEcho(@$_GET['q'], 1);?>" placeholder="<?=CMS::t('cms_users_search');?>" class="form-control input-md" onfocus="this.value='';" />
						<span class="input-group-btn">
							<button type="submit" name="go" value="1" class="btn btn-primary"><i class="fa fa-search" aria-hidden="true"></i> <?=CMS::t('search');?></button>
						</span>
					</div>
				</form>
			</div>
        </div>

        <div class="box-body">
			<?php
				if (!empty($log) && is_array($log)) {
			?>
			<table class="table table-bordered table-striped">
				<thead>
					<tr>
						<th>NN</th>
						<th>ID</th>
						<th>Action datetime</th>
						<th>Description</th>
					</tr>
				</thead>
				<tbody>
					<?php
							foreach ($log as $i=>$r) {
					?>
						<tr>
							<td>
								<?=($i+1);?>
							</td>
							<td>
								<?=$r['id'];?>
							</td>
							<td>
								<time datetime="<?=utils::formatMySQLDate('c', $r['reg_date']);?>" title="<?=$r['reg_date'];?>">
									<?=utils::formatMySQLDate(CMS::$date_format.' H:i:s', $r['reg_date']);?>
								</time>
							</td>
							<td>
								<?=utils::safeEcho($r['descr'], 1);?>
							</td>
						</tr>
	<?php
			}
	?>
				</tbody>
			</table>
	<?php
		}
	?>
		</div>
        <!-- /.box-body -->
	</div>
	<!-- /.box -->

	<!-- /.info boxes -->
</section>
<!-- /.content -->

<div id="popupFilter" style="display: none; width: 420px;">
    <h4 class="popupTitle"><?=CMS::t('filter_popup_title');?></h4>

    <div class="popupForm">
        <div class="popupFormFieldset">
            <div class="popupFormInputsBlock">
                <label for="selectRole" class="form-label">Users</label>
                <select name="filter[user]" id="selectRole" class="form-control" form="formSearchAndFilter">
                    <option value=""><?=CMS::t('filter_no_matter');?></option>
                    <?php
                    foreach ($users as $user) {
                        ?><option value="<?=$user['cms_user_id'];?>"<?=(($user['cms_user_id']==@$_GET['filter']['user'])? ' selected="selected"': '');?>><?=utils::safeEcho($user['name'], 1);?></option><?php
                    }
                    ?>
                </select>
            </div>

			<div class="popupFormInputsBlock" style="height: 60px;">
				<label class="form-label">Period</label>

				<div class="clear"></div>

				<div class="input-group" style="width: 192px; float: left;">
					<div class="input-group-addon"><i class="fa fa-calendar"></i></div><input type="text" name="filter[since]" value="<?=utils::safeEcho(@$_GET['filter']['since'], 1);?>" placeholder="<?=CMS::t('site_user_reg_date_since');?>" class="form-control pull-right addon-datepicker-input" style="width: 152px;" id="inputRegSince" form="formSearchAndFilter" />
                </div>

				<img src="<?=IMAGE_DIR;?>interval_grey.png" alt="&mdash;" style="width: 31px; float: left; margin: 12px 3px 0 2px;" />

				<div class="input-group" style="width: 192px; float: left;">
					<div class="input-group-addon"><i class="fa fa-calendar"></i></div><input type="text" name="filter[till]" value="<?=utils::safeEcho(@$_GET['filter']['till'], 1);?>" placeholder="<?=CMS::t('site_user_reg_date_till');?>" class="form-control pull-right addon-datepicker-input" style="width: 152px;" id="inputRegTill" form="formSearchAndFilter" />
                </div>

				<div class="clear"></div>

				<script type="text/javascript">
// <![CDATA[
$('.addon-datepicker-input').each(function() {
    $(this).datepicker({
		autoclose: true,
		format: 'dd.mm.yyyy',
		clearBtn: true
	});
});
// ]]>
				</script>
			</div>
        </div>

        <div class="popupControls">
            <button type="submit" name="go" value="1" form="formSearchAndFilter" class="btn btn-primary center-block"><i class="fa fa-filter" aria-hidden="true"></i> <?=CMS::t('filter');?></button>
        </div>
    </div>
</div>