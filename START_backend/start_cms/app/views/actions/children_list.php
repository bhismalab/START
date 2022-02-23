<?php

use tb\start_cms\CMS;
use tb\start_cms\helpers\security;
use tb\start_cms\helpers\utils;
use tb\start_cms\helpers\view;

if (!defined("_VALID_PHP")) {die('Direct access to this location is not allowed.');}

// load resourses

// load datepicker
view::appendCss(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/css/bootstrap-datepicker3.css');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/js/bootstrap-datepicker.min.js');
view::appendJs(SITE.CMS_DIR.JS_DIR.'bootstrap-datepicker/locales/bootstrap-datepicker.'.$_SESSION[CMS::$sess_hash]['ses_adm_lang'].'.min.js');

?>


<style>
td {
	vertical-align: middle !important;
}
thead th {
	text-transform: uppercase;
}
/*.addon-datepicker-input {
	position: relative; z-index: 100000 !important;
}*/

section.content tr.highlighted, #main-content tr.highlighted td {
	background: #dcf; /* #b199f1; */
}

.btn-context[disabled="disabled"] {
	color: #888; border-color: #888;
}
</style>


<script type="text/javascript">
// <![CDATA[
$(document).ready(function() {
    function parse_query_string(query) {
        var vars = query.split("&");
        var query_string = {};
        for (var i = 0; i < vars.length; i++) {
            var pair = vars[i].split("=");
            // If first entry with this name
            if (typeof query_string[pair[0]] === "undefined") {
                query_string[pair[0]] = decodeURIComponent(pair[1]);
                // If second entry with this name
            } else if (typeof query_string[pair[0]] === "string") {
                var arr = [query_string[pair[0]], decodeURIComponent(pair[1])];
                query_string[pair[0]] = arr;
                // If third or later entry with this name
            } else {
                query_string[pair[0]].push(decodeURIComponent(pair[1]));
            }
        }
        return query_string;
    }

    function insertParam(newText, key, value) {
        key = encodeURIComponent(key); value = encodeURIComponent(value);

        var s = newText;
        var kvp = key+"="+value;

        var r = new RegExp("(&|\\?)"+key+"=[^\&]*");

        s = s.replace(r,"$1"+kvp);

        if(!RegExp.$1) {s += (s.length>0 ? '&' : '?') + kvp;};
        return s;
        //again, do what you will here
        //document.location.search = s;
    }


    function UpdateQueryString(key, value, url) {
        if (!url) url = window.location.href;
        var re = new RegExp("([?&])" + key + "=.*?(&|#|$)(.*)", "gi"),
            hash;

        if (re.test(url)) {
            if (typeof value !== 'undefined' && value !== null)
                return url.replace(re, '$1' + key + "=" + value + '$2$3');
            else {
                hash = url.split('#');
                url = hash[0].replace(re, '$1$3').replace(/(&|\?)$/, '');
                if (typeof hash[1] !== 'undefined' && hash[1] !== null)
                    url += '#' + hash[1];
                return url;
            }
        } else {
            if (typeof value !== 'undefined' && value !== null) {
                var separator = url.indexOf('?') !== -1 ? '&' : '?';
                hash = url.split('#');
                url = hash[0] + separator + key + '=' + value;
                if (typeof hash[1] !== 'undefined' && hash[1] !== null)
                    url += '#' + hash[1];
                return url;
            }

			return url;
        }
    }

    $('.order').on('click', function() {
        //console.log($(this).data("order"));
        var orderData = $(this).data("sort-by");
        var url = window.location.href;
        var sortRule;
        if ($(this).hasClass("up")) {
            sortRule = "asc";
        } else if($(this).hasClass("down")) {
            sortRule = "desc";
        }
        $.urlParam = function(name){
            var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
            if (results==null) {
                return null;
            } else {
                return decodeURI(results[1]) || 0;
            }
        }
        var orderInUrl = $.urlParam("order");
        var ruleInUrl = $.urlParam("sort_rule");

        if (orderInUrl == null) {
            url+="&order="+orderData;
        } else {
            url = UpdateQueryString("order", orderData, url);
        }
        if (ruleInUrl == null) {
            url += "&sort_rule="+sortRule;
        } else {
            url = UpdateQueryString("sort_rule", sortRule, url);
        }
        window.location = url;
    });

	$('#filter-button').on('click', function() {
		$.fancybox.open({
			href: '#popupFilter'
		});
	});

	$('#popupFilterClose').on('click', function() {
		$.fancybox.close();
		return false;
	});

	$('.aAjax').on('click', function() {
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
	});

	$('[name="surveys_selected[]"]').on('change', function() {
		if (this.checked) {
			$(this).parents('tr').addClass('highlighted');
		} else {
			$(this).parents('tr').removeClass('highlighted');
		}

		if ($('[name="surveys_selected[]"]:checked').length) {
			$('.btnDownloadSurveys').removeAttr('disabled');
		} else {
			$('.btnDownloadSurveys').attr('disabled', 'disabled');
		}
	});

	$('[name="check_all"]').on('change', function() {
		$('[name="surveys_selected[]"]').prop('checked', this.checked);
		$('[name="surveys_selected[]"]').each(function() {
			if (this.checked) {
				$(this).parents('tr').addClass('highlighted');
			} else {
				$(this).parents('tr').removeClass('highlighted');
			}
		});

		if ($('[name="surveys_selected[]"]:checked').length) {
			$('.btnDownloadSurveys').removeAttr('disabled');
		} else {
			$('.btnDownloadSurveys').attr('disabled', 'disabled');
		}
	});

	$('.btnDownloadSurveys').click(function() {
		if ($(this).is('[disabled="disabled"]')) {
			return false;
		}

		$('#formExportSurveys input[name="surveys_selected[]"]').remove();

		$('[name="surveys_selected[]"]').each(function() {
			if (this.checked) {
				$('#formExportSurveys').append('<input type="hidden" name="surveys_selected[]" value="'+this.value+'" />');
			}
		});

		$.fancybox.open({
			'href': '#popupDownloadSurveysChooseAssesstments'
		});

		//$('#formExportSurveys').submit();

		return false;
	});
});
// ]]>
</script>

<?php
//var_dump(ADMIN_TYPE);
    function highlight_sort($order, $sort_rule){
        if ($_GET['order'] == $order && $_GET['sort_rule'] == $sort_rule) {
            echo "active-sort";
        }
    }
?>

<!-- Deleting hidden form -->
<form action="?controller=children&amp;action=delete_survey" method="post" id="formDeleteItem">
	<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />
	<input type="hidden" name="delete" value="0" />
</form>

<!-- Exporting hidden form -->
<form action="?controller=children&amp;action=download_surveys" method="post" id="formExportSurveys">
	<input type="hidden" name="CSRF_token" value="<?=$CSRF_token;?>" />
</form>


<!-- Content Header (Page header) -->
<section class="content-header">
	<h1>
		<?=CMS::t('menu_block_children');?>
	</h1>
</section>

<!-- Main content -->
<section class="content">

	<!-- Info boxes -->

	<div class="box">
		<div class="box-header with-border">
			<h3 class="box-title"><?=$count;?> surveys found</h3>

			<!-- <?php if (CMS::hasAccessTo('children/download_surveys')) { ?>
			<a href="?controller=<?=$_GET['controller'];?>&amp;action=download_surveys" class="btn btn-context btnDownloadSurveys" disabled="disabled"><i class="fa fa-download" aria-hidden="true"></i> Download selected</a>
			<?php } ?> -->

			<div class="box-tools pull-right col-sm-5 col-lg-6">
				<form action="" method="get" id="formSearchAndFilter">
					<input type="hidden" name="controller" value="<?=utils::safeEcho(@$_GET['controller'], 1);?>" />
					<input type="hidden" name="action" value="<?=utils::safeEcho(@$_GET['action'], 1);?>" />
					<input type="hidden" name="<?=time();?>" value="" />

					<div class="input-group has-feedback">
						<div class="input-group-btn">
							<button type="button" <?php if (ADMIN_TYPE=='researcher') echo "style='float: right;'"; ?> class="btn btn-<?=(utils::isEmptyArrayRecursive(@$_GET['filter'])? 'success': 'warning');?>" id="filter-button"><i class="fa fa-filter" aria-hidden="true"></i> <?=CMS::t('filter');?></button>
						</div>
                        <?php
                        /** Don't show search button to researcher */
                        if ($_SESSION[CMS::$sess_hash]['ses_adm_type']!="researcher") {
                            ?>
						<input type="text" name="q" value="<?=utils::safeEcho(@$_GET['q'], 1);?>" placeholder="<?=CMS::t('search');?>" class="form-control input-md" onfocus="this.value='';" />
						<span class="input-group-btn">
							<button type="submit" name="go" value="1" class="btn btn-primary"><i class="fa fa-search" aria-hidden="true"></i> <?=CMS::t('search');?></button>
						</span>
                        <?php } ?>
					</div>
				</form>
			</div>
		</div>
		<!-- /.box-header -->

        <div class="box-body">
			<?php
				if (!empty($children) && is_array($children)) {
			?>
			<table class="table table-bordered table-striped">
				<thead>
					<tr>
						<td>
							<input type="checkbox" name="check_all" value="1" />
						</td>
						<th>ID</th>
						<th style="position: relative">
							<div><?=CMS::t('children_name');?></div>

							<div class="sort-directions flex column" >
								<div class="up order <?php highlight_sort("name", "asc"); ?>" data-sort-by="name">
									<i class="fa fa-arrow-up" aria-hidden="true"></i>
								</div>
								<div class="down order <?php highlight_sort("name", "desc"); ?>" data-sort-by="name">
									<i class="fa fa-arrow-down" aria-hidden="true"></i>
								</div>
							</div>
                        </th>
                        <th>
                            <div><?=CMS::t('children_gender');?></div>
                        </th>
						<?php if (ADMIN_TYPE!='researcher') { ?>
						<th style="position: relative">
                            <div><?=CMS::t('children_age');?></div>
                            <div class="sort-directions flex column">
                                <div class="up order <?php highlight_sort("age", "asc"); ?>" data-sort-by="age">
                                    <i class="fa fa-arrow-up" aria-hidden="true"></i>
                                </div>
                                <div class="down order <?php highlight_sort("age", "desc"); ?>" data-sort-by="age">
                                    <i class="fa fa-arrow-down" aria-hidden="true"></i>
                                </div>
                            </div>
                        </th>

						<th style="position: relative">
                            <div><?=CMS::t('children_state');?></div>
                            <div class="sort-directions flex column">
                                <div class="up order <?php highlight_sort("state", "asc"); ?>" data-sort-by="state">
                                    <i class="fa fa-arrow-up" aria-hidden="true"></i>
                                </div>
                                <div class="down order <?php highlight_sort("state", "desc"); ?>" data-sort-by="state">
                                    <i class="fa fa-arrow-down" aria-hidden="true"></i>
                                </div>
                            </div>
                        </th>
						<?php } ?>
						<th style="position: relative" >
                            <div><?=CMS::t('children_survey_status');?></div>
                            <div class="sort-directions flex column">
                                <div class="up order <?php highlight_sort("status", "asc"); ?>" data-sort-by="status">
                                    <i class="fa fa-arrow-up" aria-hidden="true"></i>
                                </div>
                                <div class="down order <?php highlight_sort("status", "desc"); ?>" data-sort-by="status">
                                    <i class="fa fa-arrow-down" aria-hidden="true"></i>
                                </div>
                            </div>
                        </th>
						<th style="position: relative" >
                            <div><?=CMS::t('children_survey_date');?></div>
                            <div class="sort-directions flex column">
                                <div class="up order <?php highlight_sort("date", "asc"); ?>" data-sort-by="date">
                                    <i class="fa fa-arrow-up" aria-hidden="true"></i>
                                </div>
                                <div class="down order <?php highlight_sort("date", "desc"); ?>" data-sort-by="date">
                                    <i class="fa fa-arrow-down" aria-hidden="true"></i>
                                </div>
                            </div>
                        </th>
						<!-- <th>Social worker</th> -->
						<th>Registered</th>
						<th><?=CMS::t('controls');?></th>
                        <?php if (ADMIN_TYPE=='admin') { ?>
						<th>Delete</th>
                        <?php } ?>
					</tr>
				</thead>
				<tbody>
					<?php
                    //echo "<pre>"; var_dump($children);
							foreach ($children as $child) {
					?>
						<tr <?php if ($child['is_inspected']=='0') {echo "class=\"new-survey\"";}?>>
							<td>
								<input type="checkbox" name="surveys_selected[]" value="<?=$child['survey_id'];?>" />
							</td>
							<td>
								<?=$child['id'];?>
							</td>
							<td>
								<?php if (!empty($child['photo']) && ADMIN_TYPE!='researcher') { ?>
								<img src="?controller=<?=$_GET['controller'];?>&amp;action=download_child_photo&amp;child_id=<?=$child['id'];?>" alt="" class="userpic img-circle img-bordered-sm" />
								<?php } ?>
								<?php if (ADMIN_TYPE!='researcher') { ?><?=utils::safeEcho($child['name'].' '.$child['surname'].' '.$child['patronymic'], 1);?><?php } else { ?>------ ------<?php } ?>
							</td>
                            <td>
                                <?=CMS::t('children_gender_'.$child['gender']);?>
                            </td>
							<?php if (ADMIN_TYPE!='researcher') { ?>
							<td>
								<span title="<?=$child['birth_date'];?>"><?=utils::getAge($child['birth_date']);?></span>
							</td>

							<td>
								<?=utils::safeEcho($child['state'], 1);?>
							</td>
							<?php } ?>
							<td>
								<?php
								/*$survey_status = 'active';
								if ($child['is_closed']) {
									$survey_status = 'closed';
								} else if ($child['is_completed']) {
									$survey_status = 'finished';
								}*/
                                if ($child['is_closed'] == '1') {
                                    $survey_status = 'closed';
                                } else if ($child['is_closed'] == '0') {
                                    $survey_status = 'active';
                                }
								print CMS::t('survey_status_'.$survey_status);
								?>
							</td>
							<td>
								<?php
									if (!empty($child['survey_date'])) {
								?><time datetime="<?=utils::formatMySQLDate('c', $child['survey_date']);?>" title="<?=$child['survey_date'];?>"><?=utils::formatMySQLDate(CMS::$date_format, $child['survey_date']);?></time><?php
									}
								?>
							</td>
							<!-- <td>
								<?php if (CMS::hasAccessTo('social_workers/edit')) { ?>
								<a href="?controller=social_workers&amp;action=edit&amp;id=<?=$child['add_by'];?>&amp;return=<?=$link_return;?>" />
								<?php } ?>
								<?=utils::safeEcho($child['social_worker_name'], 1);?>
								<?php if (CMS::hasAccessTo('social_workers/edit')) { ?>
								</a>
								<?php } ?>
							</td> -->
							<td>
								<time datetime="<?=utils::formatMySQLDate('c', $child['add_datetime']);?>" title="<?=$child['add_datetime'];?>"><?=utils::formatMySQLDate(CMS::$date_format, $child['add_datetime']);?></time>
							</td>
							<td>
								<?php if (CMS::hasAccessTo('children/survey')) { ?>
								<a href="?controller=children&amp;action=survey&amp;child_id=<?=$child['id'];?>&amp;survey_id=<?=$child['survey_id'];?>&amp;return=<?=$link_return;?>&amp;<?=time();?>" title="<?=CMS::t('view');?>">
									<!-- <i class="fa fa-eye" aria-hidden="true"></i> --> <?=CMS::t('children_view_survey');?>
								</a>
								<?php } ?>
							</td>
                            <?php if (CMS::hasAccessTo('children/delete_survey', 'write') && (ADMIN_TYPE=='admin')) { ?>
                            <td>
                                <a href="#" title="<?=CMS::t('delete');?>" class="text-red" style="margin-left: 15px;" id="aDeleteItem_<?=$child['id'];?>_<?=$child['survey_id'];?>" data-item-id="<?=$child['id'];?>">
                                    <i class="fa fa-trash" aria-hidden="true"></i> <?=CMS::t('delete');?>
                                </a>
                                <script type="text/javascript">
                                    $('#aDeleteItem_<?=$child['id'];?>_<?=$child['survey_id'];?>').on('click', function() {
                                        bootbox.confirm({
                                            message: 'Are you sure you want to delete this survey?',
                                            callback: function(ok) {
                                                if (ok) {
                                                    var $form = $('#formDeleteItem');
                                                    $('[name="delete"]', $form).val('<?=$child['survey_id'];?>');
                                                    $form.submit();
                                                }
                                            }
                                        });

                                        return false;
                                    });
                                </script>
                            </td>
                            <?php } ?>
						</tr>
	<?php
			}
	?>
				</tbody>
			</table>


			<div>
				<?php if (CMS::hasAccessTo('children/download_surveys')) { ?>
				<a href="?controller=<?=$_GET['controller'];?>&amp;action=download_surveys" class="btn btn-context btnDownloadSurveys" disabled="disabled" style="margin: 13px 8px 0 0; display: inline-block; vertical-align: top;"><i class="fa fa-download" aria-hidden="true"></i> Download selected</a>
				<?php } ?>


				<?php
					if (isset($_GET['order']) && (trim(strip_tags((string)$_GET['order']))!="") && isset($_GET['sort_rule']) && (trim(strip_tags((string)$_GET['sort_rule']))!="")) {
						$order = "&amp;order=".utils::safeEcho((string)$_GET['order'], 1)."&amp;sort_rule=".utils::safeEcho((string)$_GET['sort_rule'], 1);
					} else {
						$order = "";
					}
				?>
				<div class="pagination" style="display: inline-block; vertical-align: top; float: right;"><?=view::pg([
					'total' => $total,
					'current' => $current,
					'page_url' => $link_sc.'&amp;page=%d'.$order
				]);?></div>
				<?php
					} else {
						print view::callout('', 'danger', 'no_data_found');
					}
				?>
			</div>
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
            <?php if (ADMIN_TYPE!='researcher') { ?>
			<div class="popupFormInputsBlock">
				<label for="selectGender" class="form-label"><?=CMS::t('children_gender');?></label>

				<select name="filter[gender]" id="selectGender" class="form-control" form="formSearchAndFilter">
					<option value=""><?=CMS::t('filter_no_matter');?></option>
					<option value="M"<?=((@$_GET['filter']['gender']=='M')? ' selected="selected"': '');?>><?=CMS::t('children_gender_M');?></option>
					<option value="F"<?=((@$_GET['filter']['gender']=='F')? ' selected="selected"': '');?>><?=CMS::t('children_gender_F');?></option>
				</select>
			</div>

			<div class="popupFormInputsBlock">
				<label for="selectGender" class="form-label">Age</label>

				<select name="filter[age]" id="selectGender" class="form-control" form="formSearchAndFilter">
					<option value=""><?=CMS::t('filter_no_matter');?></option>
					<option value="lt3"<?=((@$_GET['filter']['age']=='lt3')? ' selected="selected"': '');?>>&lt;3 year</option>
					<option value="3to4"<?=((@$_GET['filter']['age']=='3to4')? ' selected="selected"': '');?>>3-4 year</option>
					<option value="gt4"<?=((@$_GET['filter']['age']=='gt4')? ' selected="selected"': '');?>>&gt;4 year</option>
				</select>
			</div>

			<div class="popupFormInputsBlock">
				<label for="selectState" class="form-label"><?=CMS::t('children_state');?></label>

				<select name="filter[state]" id="selectState" class="form-control" form="formSearchAndFilter">
					<option value=""><?=CMS::t('filter_no_matter');?></option>
					<?php
						if (!empty($states) && is_array($states)) foreach ($states as $s) {
					?><option value="<?=$s;?>"<?=((@$_GET['filter']['state']==$s)? ' selected="selected"': '');?>><?=$s;?></option><?php
						}
					?>
				</select>
			</div>

			<div class="popupFormInputsBlock">
				<label for="selectDiagnosisAvailability" class="form-label">Diagnosis info</label>

				<select name="filter[diagnosis]" id="selectDiagnosisAvailability" class="form-control" form="formSearchAndFilter">
					<option value=""><?=CMS::t('filter_no_matter');?></option>
					<option value="available"<?=((@$_GET['filter']['diagnosis']=='available')? ' selected="selected"': '');?>>Available</option>
					<option value="not_available"<?=((@$_GET['filter']['diagnosis']=='not_available')? ' selected="selected"': '');?>>Not available</option>
				</select>
			</div>
            <?php } ?>
			<div class="popupFormInputsBlock">
				<label for="selectSocialWorkers" class="form-label"><?=CMS::t('children_social_worker');?></label>

				<select name="filter[social_worker]" id="selectSocialWorkers" class="form-control" form="formSearchAndFilter">
					<option value=""><?=CMS::t('filter_no_matter');?></option>
					<?php
						if (!empty($social_workers) && is_array($social_workers)) foreach ($social_workers as $sw) {
					?><option value="<?=$sw['id'];?>"<?=((@$_GET['filter']['social_worker']==$sw)? ' selected="selected"': '');?>><?=$sw['full_name'];?></option><?php
						}
					?>
				</select>
			</div>

            <div class="popupFormInputsBlock">
                <label for="selectStatus" class="form-label"><?=CMS::t('children_status');?></label>

                <select name="filter[status]" id="selectStatus" class="form-control" form="formSearchAndFilter">
                    <option value=""><?=CMS::t('filter_no_matter');?></option>
                    <?php
                    if (!empty($statuses) && is_array($statuses)) foreach ($statuses as $status) {
                        ?><option value="<?=$status;?>"<?=((@$_GET['filter']['status']==$status) ? ' selected="selected"': '');?>><?=$status;?></option><?php
                    }
                    ?>
                </select>
            </div>

			<div class="popupFormInputsBlock" style="height: 60px;">
				<label class="form-label"><?=CMS::t('site_user_reg_date');?></label>

				<div class="clear"></div>

				<div class="input-group" style="width: 192px; float: left;">
					<div class="input-group-addon"><i class="fa fa-calendar"></i></div>
					<input type="text" name="filter[reg_since]" value="<?=utils::safeEcho(@$_GET['filter']['reg_since'], 1);?>" placeholder="<?=CMS::t('site_user_reg_date_since');?>" class="form-control pull-right addon-datepicker-input" style="width: 152px;" id="inputRegSince" form="formSearchAndFilter" />
                </div>

				<img src="<?=IMAGE_DIR;?>interval_grey.png" alt="&mdash;" style="width: 31px; float: left; margin: 12px 3px 0 2px;" />

				<div class="input-group" style="width: 192px; float: left;">
					<div class="input-group-addon"><i class="fa fa-calendar"></i></div>
					<input type="text" name="filter[reg_till]" value="<?=utils::safeEcho(@$_GET['filter']['reg_till'], 1);?>" placeholder="<?=CMS::t('site_user_reg_date_till');?>" class="form-control pull-right addon-datepicker-input" style="width: 152px;" id="inputRegTill" form="formSearchAndFilter" />
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


<div id="popupDownloadSurveysChooseAssesstments" style="display: none; width: 520px;">
	<h4 class="popupTitle"><?=CMS::t('popupDownloadSurveysChooseAssesstments_title');?></h4>

	<div class="popupForm">
		<div class="popupFormFieldset" style="display: inline-block; width: 255px; vertical-align: top;">
			<?php
			//$assestment_types = ['bubbles_jubbing', 'choose_touching', 'coloring', 'eye_tracking_pairs', 'motoric_following', 'parent_assestment', 'parent_child_play'];
			$assestment_types = ['eye_tracking_pairs', 'choose_touching', 'bubbles_jubbing', 'parent_assestment'];
			foreach ($assestment_types as $a) {
				?>
				<div>
					<input type="checkbox" name="assestment_types[]" value="<?=$a;?>" checked="checked" form="formExportSurveys" /> <label for="selectGender" style="font-weight: normal;"><?=CMS::t('exercises_type_popup_'.$a);?></label>
				</div>
				<?php
			}
			?>
		</div>

		<div class="popupFormFieldset" style="display: inline-block; width: 255px; vertical-align: top;">
			<?php
			//$assestment_types = ['bubbles_jubbing', 'choose_touching', 'coloring', 'eye_tracking_pairs', 'motoric_following', 'parent_assestment', 'parent_child_play'];
			$assestment_types = ['wheel', 'motoric_following', 'coloring', 'parent_child_play'];
			foreach ($assestment_types as $a) {
				?>
				<div>
					<input type="checkbox" name="assestment_types[]" value="<?=$a;?>" checked="checked" form="formExportSurveys" /> <label for="selectGender" style="font-weight: normal;"><?=CMS::t('exercises_type_popup_'.$a);?></label>
				</div>
				<?php
			}
			?>
		</div>

		<div class="popupControls">
			<button type="submit" name="go" value="1" form="formExportSurveys" class="btn btn-primary center-block"><i class="fa fa-check" aria-hidden="true"></i> Ok</button>
		</div>
	</div>
</div>